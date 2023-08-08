package com.starp.zoo.service.impl;

import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.MsisdnRecordModel;
import com.starp.zoo.repo.zoo.MsisdnRecordRepo;
import com.starp.zoo.service.IMsisdnRecordService;
import com.starp.zoo.util.DateUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * MsisdnRecordServiceImpl.
 *
 * @author magic
 * @date 2021/10/29
 */
@Service
@Slf4j
public class MsisdnRecordServiceImpl implements IMsisdnRecordService {

    @Autowired
    private MsisdnRecordRepo msisdnRecordRepo;

    @Autowired
    private StringRedisTemplate masterRedisTemplate;

    @Resource(name = "cluster3RedisTemplate")
    private StringRedisTemplate cluster3RedisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(10, 30,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(), nameThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

    private static ThreadFactory nameThreadFactory() {
        AtomicInteger tag = new AtomicInteger(1);
        ThreadFactory factory = (Runnable r) -> {
            Thread thread = new Thread(r);
            thread.setName("线程-adMsisdn-" + tag.getAndIncrement());
            return thread;
        };
        return factory;
    }

    @Override
    @Async
    public void saveRecord(String msisdn, String result, String r1, String r2, String r3, String r4, String r5, String r6, String r7) {
        MsisdnRecordModel model = msisdnRecordRepo.findByMsisdn(msisdn);
        if (model != null) {
            model.setCurrrentDayIsPull("1");
            if (!StringUtils.isEmpty(result)) {
                model.setResult(result);
            }
            if (!StringUtils.isEmpty(r1)) {
                model.setR1(r1);
            }
            if (!StringUtils.isEmpty(r2)) {
                model.setR2(r2);
            }
            if (!StringUtils.isEmpty(r3)) {
                model.setR3(r3);
            }
            if (!StringUtils.isEmpty(r4)) {
                model.setR4(r4);
            }
            if (!StringUtils.isEmpty(r5)) {
                model.setR5(r5);
            }
            if (!StringUtils.isEmpty(r6)) {
                model.setR6(r6);
            }
            if (!StringUtils.isEmpty(r7)) {
                model.setR7(r7);
            }
            msisdnRecordRepo.save(model);
        }
    }

    @Override
    public String getOne(String param) {
        if (param.equals(ZooConstant.RESULT)) {
            param = ZooConstant.R0;
        }
        String lockName = ZooConstant.MSISDN_RECORD_LOCK + ZooConstant.UNDER_LINE + param;
        RLock lock = redissonClient.getLock(lockName);
        String redisKey = ZooConstant.MSISDN_RECORD_DAY + ZooConstant.UNDER_LINE  + param;
        String msisdn = "";
        try {
            if (lock.tryLock(NumberEnum.ONE.getNum(), NumberEnum.THREE.getNum(), TimeUnit.SECONDS)) {
                List<String> msisdnList = cluster3RedisTemplate.opsForList().range(redisKey, 0, -1);
                if (msisdnList != null && msisdnList.size() > 0) {
                    msisdn = masterRedisTemplate.opsForList().leftPop(redisKey);
                } else {
                    //重新获取数组
                    handleResetMsisdnList(param);
                    msisdn = masterRedisTemplate.opsForList().leftPop(redisKey);
                }
            }
        } catch (Exception e) {
            log.error("GET MSISDN ERROR:{}", e.getMessage());
        } finally {
            lock.unlock();
        }
        return msisdn;
    }

    /**
     * 重新生成数组也需要锁
     *
     * @param param
     */
    @SuppressFBWarnings({"BX_UNBOXING_IMMEDIATELY_REBOXED","DM_BOXED_PRIMITIVE_FOR_PARSING"})
    private void handleResetMsisdnList(String param) {
        RLock indexLock = redissonClient.getLock(ZooConstant.LOCK_MSISDN_LOCAL_INDEX);
        try {
            if (indexLock.tryLock(NumberEnum.ONE.getNum(), NumberEnum.THREE.getNum(), TimeUnit.SECONDS)) {
                Object index = cluster3RedisTemplate.opsForValue().get(ZooConstant.LOG_MSISDN_LOCAL_INDEX);
                if (index != null) {
                    List<MsisdnRecordModel> msisdnRecordModelList;
                    List<String> msisdnList;
                    int localIndex = Integer.valueOf(index.toString());
                    Integer nextIndex = 1;
                    if(localIndex >= NumberEnum.ONE_THOUSAND_AND_ONE.getNum()){
                        nextIndex  = Integer.parseInt(index.toString()) - 1000;
                    }else if(Integer.parseInt(index.toString()) == 1){
                        String time =DateUtil.today() + " 00:00:00";
                        localIndex = msisdnRecordRepo.findTodayId(time);
                        nextIndex = localIndex - 1000;
                    }
                    msisdnRecordModelList = msisdnRecordRepo.findResetList(nextIndex,localIndex);
                    msisdnList = msisdnRecordModelList.stream().filter(a -> StringUtils.isEmpty(a.getCurrrentDayIsPull()) &&
                            checkResultIsNull(a, Integer.valueOf(param.substring(1)))).map(MsisdnRecordModel::getMsisdn).collect(Collectors.toList());
                    masterRedisTemplate.delete(ZooConstant.LOG_MSISDN_LOCAL_INDEX);
                    masterRedisTemplate.opsForValue().set(ZooConstant.LOG_MSISDN_LOCAL_INDEX, nextIndex.toString());
                    String redisKey = ZooConstant.MSISDN_RECORD_DAY + ZooConstant.UNDER_LINE + param;
                    masterRedisTemplate.opsForList().rightPushAll(redisKey, msisdnList);
                }
            }
        } catch (Exception e) {
            log.error("RESET MSISDN LIST ERROR, PARAM:{},ERROR:{}", param, e.getMessage());
        } finally {
            indexLock.unlock();
        }
    }

    @Override
    @Async
    public void initCurrentIsPull() {
        List<MsisdnRecordModel> list = msisdnRecordRepo.findAll();
        for (MsisdnRecordModel model : list) {
            EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    model.setCurrrentDayIsPull(null);
                    msisdnRecordRepo.save(model);
                }
            });
        }
    }

    @Override
    public void addMsisdn(List<MsisdnRecordModel> list) {
        for (MsisdnRecordModel model : list) {
            EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        msisdnRecordRepo.save(model);
                    } catch (Exception e) {
                        log.error("SAVE MSISDN SAME MSISDN:{}",model.getMsisdn());
                    }
                }
            });
        }
    }

    @Override
    public void saveBlack(String[] msisdnArr, String[] field) {
        List<String> fieldList = Arrays.asList(field);
        for (String msisdn : msisdnArr) {
            EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        MsisdnRecordModel model = msisdnRecordRepo.findByMsisdn(msisdn);
                        if (model == null) {
                            MsisdnRecordModel newModel = new MsisdnRecordModel();
                            newModel.setMsisdn(msisdn);
                            setCommonPorp(newModel, fieldList);
                            msisdnRecordRepo.save(newModel);
                        } else {
                            setCommonPorp(model, fieldList);
                            msisdnRecordRepo.save(model);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    @Async
    public void syncMsisdn() {
        masterRedisTemplate.delete(ZooConstant.LOG_MSISDN_LOCAL_INDEX);
        List<Integer> randomR = generateRandomR();
        List<MsisdnRecordModel> msisdnRecordModelList = msisdnRecordRepo.findMsisdnDay();
        for (int i = 0; i < NumberEnum.EIGHT.getNum(); i++) {
            handleMsisdnRecordRedis(msisdnRecordModelList, i, randomR.get(i));
        }
    }

    /**
     * 生成redis数组
     *
     * @param msisdnRecordModelList
     * @param i
     * @param r
     */
    private void handleMsisdnRecordRedis(List<MsisdnRecordModel> msisdnRecordModelList, int i, Integer r) {
        List<MsisdnRecordModel> msisdnRecordModels;
        List<MsisdnRecordModel> filterMsisdnRecords;
        if (i == 0) {
            msisdnRecordModels = msisdnRecordModelList.stream().limit(1000).collect(Collectors.toList());
        } else {
            msisdnRecordModels = msisdnRecordModelList.stream().skip(i * 1000).limit(1000).collect(Collectors.toList());
        }
        filterMsisdnRecords = msisdnRecordModels.stream().filter(a -> StringUtils.isEmpty(a.getCurrrentDayIsPull()) &&
                checkResultIsNull(a, r)).collect(Collectors.toList());
        String redisKey = ZooConstant.MSISDN_RECORD_DAY + ZooConstant.UNDER_LINE + ZooConstant.R + r;
        masterRedisTemplate.delete(redisKey);
        masterRedisTemplate.opsForList().rightPushAll(redisKey, filterMsisdnRecords.stream().map(msisdnRecordModel -> msisdnRecordModel.getMsisdn()).collect(Collectors.toList()));
        if (i == NumberEnum.SEVEN.getNum()) {
            //初始化是记录当前最后一个msisdn的下标
            Integer localIndex = filterMsisdnRecords.get(filterMsisdnRecords.size() - 1).getIdentification();
            masterRedisTemplate.opsForValue().set(ZooConstant.LOG_MSISDN_LOCAL_INDEX, String.valueOf(localIndex));
            log.info("ZOO LOG MSISDN LOCAL INDEX:{}", localIndex);
        }
    }

    private List<Integer> generateRandomR() {
        List<Integer> rList = new ArrayList<>();
        for (int i = 0; i < NumberEnum.EIGHT.getNum(); i++) {
            rList.add(i);
        }
        Collections.shuffle(rList);
        return rList;
    }

    private boolean checkResultIsNull(MsisdnRecordModel msisdnRecordModel, int i) {
        boolean result = false;
        switch (i) {
            case 0:
                result = StringUtils.isEmpty(msisdnRecordModel.getResult());
                break;
            case 1:
                result = StringUtils.isEmpty(msisdnRecordModel.getR1());
                break;
            case 2:
                result = StringUtils.isEmpty(msisdnRecordModel.getR2());
                break;
            case 3:
                result = StringUtils.isEmpty(msisdnRecordModel.getR3());
                break;
            case 4:
                result = StringUtils.isEmpty(msisdnRecordModel.getR4());
                break;
            case 5:
                result = StringUtils.isEmpty(msisdnRecordModel.getR5());
                break;
            case 6:
                result = StringUtils.isEmpty(msisdnRecordModel.getR6());
                break;
            case 7:
                result = StringUtils.isEmpty(msisdnRecordModel.getR7());
                break;
            default:
        }
        return result;
    }

    /**
     * 设置公共属性.
     *
     * @param model
     * @param fieldList
     */
    private void setCommonPorp(MsisdnRecordModel model, List<String> fieldList) {
        if (fieldList.contains(ZooConstant.RESULT)) {
            model.setResult(ZooConstant.B);
        }
        if (fieldList.contains(ZooConstant.R1)) {
            model.setR1(ZooConstant.B);
        }
        if (fieldList.contains(ZooConstant.R2)) {
            model.setR2(ZooConstant.B);
        }
        if (fieldList.contains(ZooConstant.R3)) {
            model.setR3(ZooConstant.B);
        }
        if (fieldList.contains(ZooConstant.R4)) {
            model.setR4(ZooConstant.B);
        }
        if (fieldList.contains(ZooConstant.R5)) {
            model.setR5(ZooConstant.B);
        }
    }
}
