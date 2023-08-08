package com.starp.zoo.controller;

import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.service.IAndroidMncService;
import com.starp.zoo.service.IApplicationService;
import com.starp.zoo.service.IEpmService;
import com.starp.zoo.service.IMsisdnRecordService;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.service.IOfferStepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.text.ParseException;


/**
 * 同步接口.
 *
 * @author david
 */
@RestController
@RequestMapping
public class SyncRedisController {

    @Autowired
    private IMsisdnRecordService msisdnRecordService;

    @Autowired
    private IOfferService offerService;

    @Autowired
    private IAndroidMncService mncService;

    @Autowired
    private IApplicationService appInfoService;

    @Autowired
    private IEpmService epmService;

    @Autowired
    private IOfferStepService offerStepService;

    /**
     * 同步所有关联关系.
     * @return
     */
    @GetMapping(value = "/sync/all/redis")
    public ResponseInfo syncAllRedis(){
        appInfoService.importRedis();
        offerService.initOfferRedis();
        offerService.initFilterRedis();
        offerStepService.initRedis();
        msisdnRecordService.syncMsisdn();
        msisdnRecordService.initCurrentIsPull();
        mncService.syncMysql();
        mncService.syncRedis();
        return ResponseInfoUtil.success();
    }


    /**
     * 同步app redis
     *
     * @return
     */
    @RequestMapping(value = "/sync/app/redis", method = {RequestMethod.GET})
    public ResponseInfo importAppRedis() {
        appInfoService.importRedis();
        return ResponseInfoUtil.success();
    }

    /**
     * 同步offer redis
     *
     * @return
     */
    @RequestMapping(value = "/sync/offer/redis", method = {RequestMethod.GET})
    public ResponseInfo initOfferRedis() {
        offerService.initOfferRedis();
        return ResponseInfoUtil.success();
    }

    /**
     * 同步offer filter 关联关系redis
     *
     * @return
     */
    @GetMapping("/sync/offer/filter/redis")
    public ResponseInfo initOfferFilterController() {
        offerService.initFilterRedis();
        return ResponseInfoUtil.success();
    }

    /**
     * 破解步骤同步接口.
     *
     * @return
     */
    @GetMapping("/sync/step/init/redis")
    public ResponseInfo initStepRedis() {
        offerStepService.initRedis();
        return ResponseInfoUtil.success();
    }

    /**
     * 同步msisdn redis.
     *
     * @return
     */
    @GetMapping(value = "/sync/msisdn/record")
    public ResponseInfo syncMsisdnRecord() {
        msisdnRecordService.syncMsisdn();
        return ResponseInfoUtil.success();
    }

    @GetMapping("/sync/msisdn")
    public ResponseInfo initCurrentIsPull() {
        msisdnRecordService.initCurrentIsPull();
        return ResponseInfoUtil.success();
    }


    /**
     * 同步mnc code.
     *
     * @return
     */
    @GetMapping(value = "/sync/mnc/redis")
    public ResponseInfo syncMncCode() {
        mncService.syncMysql();
        return ResponseInfoUtil.success();
    }

    /**
     * 同步mnc permission.
     *
     * @return
     */
    @GetMapping(value = "/sync/mnc/permission/redis")
    public ResponseInfo syncRedis() {
        mncService.syncRedis();
        return ResponseInfoUtil.success();
    }

    /**
     * 同步epm redis
     *
     * @return
     */
    @GetMapping("/sync/epm/redis")
    public ResponseInfo initAllOfferAndAppController() {
        epmService.syncEpmRedis();
        return ResponseInfoUtil.success();
    }

    /**
     * 同步今天EPM mo 转化
     *
     * @return
     */
    @GetMapping(value = "sync/day/epm/motrans")
    public ResponseInfo syncEpmMoTrans() {
        epmService.incrPayMoTrans();
        return ResponseInfoUtil.success();
    }

    /**
     * 同步当前小时 epm redis key
     *
     * @return
     */
    @GetMapping(value = "/sync/current/hour/epm")
    public ResponseInfo initEpmCurrentRedis() {
        offerService.initEpmCurrentHourRedis();
        return ResponseInfoUtil.success();
    }

    /**
     * 将不可用的offer同步到数据库
     *
     * @return
     */
    @GetMapping(value = "/sync/unused/epm")
    public ResponseInfo importUnusedEpm() throws ParseException {
        epmService.importUnused();
        return ResponseInfoUtil.success();
    }
}
