package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.starp.zoo.config.websocket.UserOfferServer;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.SequenceDetail;
import com.starp.zoo.entity.zoo.UserOfferDetailModel;
import com.starp.zoo.entity.zoo.UserOfferExcelModel;
import com.starp.zoo.entity.zoo.UserOfferOriginModel;
import com.starp.zoo.service.IUserOfferDetailService;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.util.PoiUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author magic
 * @date 2020/12/4
 */
@Service
@Slf4j
public class UserOfferDetailServiceImpl implements IUserOfferDetailService {

    @PersistenceContext(unitName = "appEventEntityManger")
    EntityManager appEventEntityManager;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "cluster3RedisTemplate")
    private StringRedisTemplate cluster3RedisTemplate;

    @Autowired
    private AmazonS3 s3Client;

    @Value("${useroffer.localPath}")
    private String localPath;

    @Value("${useroffer.s3.bucket}")
    private String bucketName;

    @Value("${useroffer.s3.path}")
    private String s3Path;

    @Override
    public JSONObject getDetail(String tableName, String app, String operator, String userCount, int page, String createDate) {
        List<UserOfferDetailModel> result = new ArrayList<>();
        int curNumber = 0;
        List<String> userIdList = getUserList(app, operator, userCount, tableName, createDate);
        curNumber = userIdList.size() > (page - 1) * 10 ? (page - 1) * 10 : 0;
        log.info("当前开始下标:" + curNumber + "======当前page:" + page + "======当前userIdList大小:" + userIdList.size());
        for (int i = 0; i < NumberEnum.TEN.getNum(); i++) {
            if (curNumber == userIdList.size() || curNumber == page * 10) {
                break;
            }
            List<UserOfferOriginModel> userOriginList = getUserOriginDetail(app, operator, userIdList.get(curNumber), tableName);
            String lastSeq = "";
            String lastOffer = "";
            boolean otherEntity = false;
            boolean addList = false;
            //当前offer是否成功,同一个offerName在userOriginList有多条数据
            boolean offerSuccess = false;
            //当前正在遍历的j下标的offer是否成功
            boolean curOfferSuccess = false;
            //上一个offer已经成功,并且加入到当前行的list中,判断新的offer不用立马加(因为offerName换了)
            boolean lastOfferSuccessed = false;
            //是否跳过
            boolean isContinue = false;
            List<SequenceDetail> sequenceDetailList = new ArrayList<>();
            for (int j = 0; j < userOriginList.size(); j++) {
                if (!lastOffer.equals(userOriginList.get(j).getOfferName())) {
                    isContinue = false;
                }
                if (isContinue) {
                    continue;
                }
                if (j != 0 && !lastSeq.equals(userOriginList.get(j).getSeq())) {
                    if (!lastOfferSuccessed) {
                        addList = true;
                    }
                    if (String.valueOf(NumberEnum.ONE.getNum()).equals(userOriginList.get(j).getSeq())) {
                        otherEntity = true;
                    }
                } else if (j != 0 && lastSeq.equals(userOriginList.get(j).getSeq()) && !lastOfferSuccessed) {
                    if (!lastOffer.equals(userOriginList.get(j).getOfferName())) {
                        otherEntity = true;
                        addList = true;
                    }
                }
                if (lastOfferSuccessed) {
                    lastOfferSuccessed = false;
                }
                if (String.valueOf(NumberEnum.EIGHT_TEEN.getNum()).equals(userOriginList.get(j).getEventCode())) {
                    offerSuccess = true;
                    curOfferSuccess = true;
                    addList = true;
                    isContinue = true;
                }
                if (addList) {
                    lastOfferSuccessed = addList(userOriginList, sequenceDetailList, offerSuccess, curOfferSuccess, j, false);
                    offerSuccess = false;
                    curOfferSuccess = false;
                    addList = false;
                }
                if (otherEntity) {
                    addResult(result, userOriginList, sequenceDetailList, j);
                    sequenceDetailList = new ArrayList<>();
                    otherEntity = false;
                }
                lastSeq = userOriginList.get(j).getSeq();
                lastOffer = userOriginList.get(j).getOfferName();
                if (j == userOriginList.size() - 1) {
                    addList(userOriginList, sequenceDetailList, offerSuccess, curOfferSuccess, j, true);
                    addResult(result, userOriginList, sequenceDetailList, j);
                }
            }
            curNumber++;
        }
        JSONObject jsonObject = backData(page, result);
        return jsonObject;
    }

    public JSONObject backData(int page, List<UserOfferDetailModel> result) {
        JSONObject json = new JSONObject();
        json.put("list", result);
        int length = 6;
        if (result.size() > 0) {
            length = result.stream().mapToInt(item -> item.getListSize()).max().getAsInt();
        }
        json.put("chartsTabData", length);
        UserOfferServer.sendInfo(JSON.toJSONString(json));
        return json;
    }

    /**
     * 加入offer次序详细集合
     *
     * @param userOriginList
     * @param sequenceDetailList
     * @param offerSuccess
     * @param curOfferSuccess
     * @param j
     * @param isLast
     * @return
     */
    public boolean addList(List<UserOfferOriginModel> userOriginList, List<SequenceDetail> sequenceDetailList, boolean offerSuccess, boolean curOfferSuccess, int j, boolean isLast) {
        SequenceDetail sequenceDetailModel = new SequenceDetail();
        int index = 0;
        if (isLast) {
            index = j;
        } else {
            if (curOfferSuccess) {
                index = j;
            } else {
                index = j - 1;
            }
        }
        if (offerSuccess) {
            sequenceDetailModel.setSuccessOrUrl("success");
        } else {
            sequenceDetailModel.setSuccessOrUrl(userOriginList.get(index).getUrl());
        }
        sequenceDetailModel.setOfferName(userOriginList.get(index).getOfferName());
        sequenceDetailModel.setCreateTime(userOriginList.get(index).getCreateTime());
        sequenceDetailModel.setSeq(userOriginList.get(index).getSeq());
        sequenceDetailList.add(sequenceDetailModel);
        if (offerSuccess) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 加入最终结果集
     *
     * @param result
     * @param userOriginList
     * @param sequenceDetailList
     * @param j
     */
    public void addResult(List<UserOfferDetailModel> result, List<UserOfferOriginModel> userOriginList, List<SequenceDetail> sequenceDetailList, int j) {
        UserOfferDetailModel model = new UserOfferDetailModel();
        model.setUserId(userOriginList.get(j).getUserId());
        model.setDetailList(sequenceDetailList);
        model.setListSize(sequenceDetailList.size());
        result.add(model);
    }

    /**
     * 查用户和offer的原始信息
     *
     * @param app
     * @param operator
     * @param userId
     * @param tableName
     * @return
     */
    public List<UserOfferOriginModel> getUserOriginDetail(String app, String operator, String userId, String tableName) {
        List<UserOfferOriginModel> userOfferOriginModelList = new ArrayList<>();
        StringBuilder outSideSql = new StringBuilder(" select * from  ( ");
        StringBuilder sql = new StringBuilder(" SELECT user_id, offer_name,createtime,event_code,param1 AS url,param4 AS seq FROM " + tableName + " WHERE user_id = '" + userId + "' ");
        StringBuilder whereSql = null;
        List<String> eventCodes = new ArrayList<>();
        eventCodes.add("11");
        eventCodes.add("18");
        eventCodes.add("104");
        for (int i = 0; i < eventCodes.size(); i++) {
            whereSql = new StringBuilder();
            whereSql.append(" AND event_code = '" + eventCodes.get(i) + "' ");
            splicingSql(whereSql, app, operator);
            if (i != eventCodes.size() - 1) {
                outSideSql.append(sql).append(whereSql).append(" UNION ALL ");
            } else {
                outSideSql.append(sql).append(whereSql).append(" ) result order by result.createtime ");
            }
        }
        Query query = appEventEntityManager.createNativeQuery(outSideSql.toString());
        setSqlParams(query, app, operator);
        List resultList = query.getResultList();
        for (int j = 0; j < resultList.size(); j++) {
            Object[] obj = (Object[]) resultList.get(j);
            if (obj[1] == null) {
                continue;
            }
            UserOfferOriginModel model = new UserOfferOriginModel();
            model.setUserId(obj[0].toString());
            model.setOfferName(obj[1].toString());
            model.setCreateTime(obj[2].toString());
            model.setEventCode(obj[3].toString());
            model.setUrl(obj[4].toString());
            model.setSeq(obj[5].toString());
            userOfferOriginModelList.add(model);
        }
        return userOfferOriginModelList;
    }

    /**
     * 查用户
     *
     * @param app
     * @param operator
     * @param userCount
     * @param tableName
     * @return
     */
    public List<String> getUserList(String app, String operator, String userCount, String tableName, String dateTime) {
        List<String> userList = new ArrayList<>();
        StringBuilder sql = new StringBuilder(" SELECT DISTINCT user_id  FROM " + tableName + " where createtime < STR_TO_DATE('" + dateTime + "','%Y-%m-%d %H:%i:%s') ");
        StringBuilder whereSql = new StringBuilder();
        StringBuilder orderBySql = new StringBuilder(" ORDER BY createtime DESC LIMIT " + Integer.parseInt(userCount) + " ");
        splicingSql(whereSql, app, operator);
        Query query = appEventEntityManager.createNativeQuery(sql.append(whereSql).append(orderBySql).toString());
        setSqlParams(query, app, operator);
        List resultList = query.getResultList();
        for (int j = 0; j < resultList.size(); j++) {
            userList.add(resultList.get(j).toString());
        }
        return userList;
    }

    public void splicingSql(StringBuilder whereSql, String app, String operator) {
        if (!StringUtils.isEmpty(app)) {
            whereSql.append(" and app_id = :app ");
        }
        if (!StringUtils.isEmpty(operator)) {
            whereSql.append(" and operator = :operator ");
        }
    }

    public void setSqlParams(Query query, String app, String operator) {
        if (!StringUtils.isEmpty(app)) {
            query.setParameter("app", app);
        }
        if (!StringUtils.isEmpty(operator)) {
            query.setParameter("operator", operator);
        }
    }

    @Override
    public void exportData(String tableName, String app, String operator, String userCount, int page, String createDate) {
        List<UserOfferDetailModel> result = new ArrayList<>();
        List<String> userLists = getUserList(app, operator, userCount, tableName, createDate);
        for (int i = 0; i < userLists.size(); i++) {
            List<UserOfferOriginModel> userOriginList = getUserOriginDetail(app, operator, userLists.get(i), tableName);
            String lastSeq = "";
            String lastOffer = "";
            boolean otherEntity = false;
            boolean addList = false;
            //当前offer是否成功,同一个offerName在userOriginList有多条数据
            boolean offerSuccess = false;
            //当前正在遍历的j下标的offer是否成功
            boolean curOfferSuccess = false;
            //上一个offer已经成功,并且加入到当前行的list中,判断新的offer不用立马加(因为offerName换了)
            boolean lastOfferSuccessed = false;
            //是否跳过
            boolean isContinue = false;
            List<SequenceDetail> sequenceDetailList = new ArrayList<>();
            for (int j = 0; j < userOriginList.size(); j++) {
                if (!lastOffer.equals(userOriginList.get(j).getOfferName())) {
                    isContinue = false;
                }
                if (isContinue) {
                    continue;
                }
                if (j != 0 && !lastSeq.equals(userOriginList.get(j).getSeq())) {
                    if (!lastOfferSuccessed) {
                        addList = true;
                    }
                    if (String.valueOf(NumberEnum.ONE.getNum()).equals(userOriginList.get(j).getSeq())) {
                        otherEntity = true;
                    }
                } else if (j != 0 && lastSeq.equals(userOriginList.get(j).getSeq()) && !lastOfferSuccessed) {
                    if (!lastOffer.equals(userOriginList.get(j).getOfferName())) {
                        otherEntity = true;
                        addList = true;
                    }
                }
                if (lastOfferSuccessed) {
                    lastOfferSuccessed = false;
                }
                if (String.valueOf(NumberEnum.EIGHT_TEEN.getNum()).equals(userOriginList.get(j).getEventCode())) {
                    offerSuccess = true;
                    curOfferSuccess = true;
                    addList = true;
                    isContinue = true;
                }
                if (addList) {
                    lastOfferSuccessed = addList(userOriginList, sequenceDetailList, offerSuccess, curOfferSuccess, j, false);
                    offerSuccess = false;
                    curOfferSuccess = false;
                    addList = false;
                }
                if (otherEntity) {
                    addResult(result, userOriginList, sequenceDetailList, j);
                    sequenceDetailList = new ArrayList<>();
                    otherEntity = false;
                }
                lastSeq = userOriginList.get(j).getSeq();
                lastOffer = userOriginList.get(j).getOfferName();
                if (j == userOriginList.size() - 1) {
                    addList(userOriginList, sequenceDetailList, offerSuccess, curOfferSuccess, j, true);
                    addResult(result, userOriginList, sequenceDetailList, j);
                }
            }
        }
        exportExcel(result);
    }

    public void exportExcel(List<UserOfferDetailModel> result) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFCellStyle cellStyle = PoiUtil.cellStyle(workbook);
        HSSFRow headRow = sheet.createRow(0);
        int length = result.stream().mapToInt(item -> item.getListSize()).max().getAsInt();
        HSSFCell headRowCell0 = headRow.createCell(0);
        headRowCell0.setCellValue("userId");
        headRowCell0.setCellStyle(cellStyle);
        int number = 1;
        number = createExcelHead(result, headRow, cellStyle, number, "offerName");
        number = createExcelHead(result, headRow, cellStyle, number, "createTime");
        createExcelHead(result, headRow, cellStyle, number, "URL");
        exportDataPart(result, sheet, cellStyle, length);
        PoiUtil.setCellComumnWidth(sheet, length * 3 + 1);
        String excelName = DateUtil.newFormatyyyyMMddHHmmss(new Date());
        String excelFullName = excelName + ".csv";
        String path = localPath + excelFullName;
        writeLocal(workbook, path);
        uploadS3AndSaveRedis(path, excelName);
    }

    /**
     * 写到将表格数据写到本地
     *
     * @param workbook
     */
    public void writeLocal(HSSFWorkbook workbook, String path) {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(new File(path));
            workbook.write(stream);
        } catch (Exception e) {
            log.error("[userOffer] [FileOutputStream] [writeLocal] [message]:" + e.getMessage());
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                log.error("[userOffer] [FileOutputStream] [close] [message]:" + e.getMessage());
            }
        }
    }

    /**
     * 上传s3,并且将下载链接保存到redis中
     *
     * @param localLogPath
     */
    @SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE"})
    public void uploadS3AndSaveRedis(String localLogPath, String excelName) {
        try {
            File file = new File(localLogPath);
            String[] split = localLogPath.split("/");
            String fileName = split[split.length - 1];
            //创建当日的目录
            String name = DateUtil.formatDay(new Date());
            s3Client.putObject(new PutObjectRequest(bucketName + s3Path + name, fileName, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName + s3Path + name, fileName);
            URL url = s3Client.generatePresignedUrl(urlRequest);
            String[] split1 = url.getPath().split("/");
            String exportUrl = url.getProtocol() + "://" + bucketName + "." + url.getHost() + s3Path + name + "/" + split1[split1.length - 1];
            log.info("exportUrl:" + exportUrl);
            stringRedisTemplate.opsForHash().put(ZooConstant.USER_OFFER_DETAIL, excelName, exportUrl);
            if (cluster3RedisTemplate.opsForHash().keys(ZooConstant.USER_OFFER_DETAIL).size() == 0) {
                stringRedisTemplate.expire(ZooConstant.USER_OFFER_DETAIL, 1, TimeUnit.DAYS);
            }
            if (!file.exists()) {
                return;
            }
            if (file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            log.error("[userOffer] [uploadS3AndSaveRedis] :" + e.getMessage());
        }
    }

    /**
     * 创建表头
     *
     * @param result
     * @param headRow
     * @param cellStyle
     * @param number
     * @param prop
     * @return
     */
    public int createExcelHead(List<UserOfferDetailModel> result, HSSFRow headRow, HSSFCellStyle cellStyle, int number, String prop) {
        int length = result.stream().mapToInt(item -> item.getListSize()).max().getAsInt();
        HSSFCell contentCell = null;
        for (int i = 0; i < length; i++) {
            int index = i + 1;
            contentCell = headRow.createCell(number);
            contentCell.setCellStyle(cellStyle);
            contentCell.setCellValue(prop + index);
            number++;
        }
        return number;
    }

    /**
     * 导出数据部分
     *
     * @param result
     * @param sheet
     * @param cellStyle
     * @param length
     */
    public void exportDataPart(List<UserOfferDetailModel> result, HSSFSheet sheet, HSSFCellStyle cellStyle, int length) {
        HSSFCell cell = null;
        for (int i = 0; i < result.size(); i++) {
            HSSFRow row = sheet.createRow(i + 1);
            cell = row.createCell(0);
            cell.setCellValue(result.get(i).getUserId());
            cell.setCellStyle(cellStyle);
            List<SequenceDetail> detailList = result.get(i).getDetailList();
            if (detailList != null && detailList.size() > 0) {
                int index = 1;
                for (int j = 0; j < detailList.size(); j++) {
                    cell = row.createCell(index);
                    cell.setCellValue(detailList.get(j).getOfferName());
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(length + index);
                    cell.setCellValue(detailList.get(j).getCreateTime());
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(2 * length + index);
                    cell.setCellValue(detailList.get(j).getSuccessOrUrl());
                    cell.setCellStyle(cellStyle);
                    index++;
                }
            }
        }
    }

    @Override
    public List<UserOfferExcelModel> getExcelList() {
        List<UserOfferExcelModel> result = new ArrayList<>();
        String key = ZooConstant.USER_OFFER_DETAIL;
        Set<Object> hashKeys = cluster3RedisTemplate.opsForHash().keys(key);
        for (Object hashKey : hashKeys) {
            UserOfferExcelModel model = new UserOfferExcelModel();
            model.setExcelName(hashKey.toString());
            Object o = cluster3RedisTemplate.opsForHash().get(key, hashKey);
            model.setExcelDownloadUrl(o != null ? o.toString() : null);
            result.add(model);
        }
        result = result.stream().sorted(Comparator.comparing(UserOfferExcelModel::getExcelName).reversed()).collect(Collectors.toList());
        return result;
    }
}
