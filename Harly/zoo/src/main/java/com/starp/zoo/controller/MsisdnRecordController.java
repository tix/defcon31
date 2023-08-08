package com.starp.zoo.controller;

import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.MsisdnRecordModel;
import com.starp.zoo.service.IMsisdnRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * MsisdnRecordController.
 *
 * @author magic
 * @date 2021/10/29
 */
@RestController
public class MsisdnRecordController {

    @Autowired
    private IMsisdnRecordService msisdnRecordService;

    @GetMapping("/save/msisdn")
    public ResponseInfo saveMsisdn(@RequestParam String msisdn, @RequestParam(required = false) String result,
                                   @RequestParam(required = false) String r1, @RequestParam(required = false) String r2,
                                   @RequestParam(required = false) String r3, @RequestParam(required = false) String r4,
                                   @RequestParam(required = false) String r5, @RequestParam(required = false) String r6,
                                   @RequestParam(required = false) String r7) {
        if (StringUtils.isEmpty(msisdn)) {
            return ResponseInfoUtil.error("msisdn is null");
        }
        msisdnRecordService.saveRecord(msisdn, result, r1, r2, r3, r4, r5, r6, r7);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/get/msisdn/{param}")
    public ResponseInfo getMsisdn(@PathVariable("param") String param) {
        if (StringUtils.isEmpty(param)) {
            return ResponseInfoUtil.success();
        }
        String msisdn = msisdnRecordService.getOne(param);
        return ResponseInfoUtil.success(msisdn);
    }



    @RequestMapping(value = "/add/msisdn", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseInfo addMsisdn(@RequestBody String msisdn) {
        if (!StringUtils.isEmpty(msisdn)) {
            String[] split = msisdn.split(",");
            List<MsisdnRecordModel> list = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                MsisdnRecordModel model = new MsisdnRecordModel();
                model.setMsisdn(split[i].trim());
                list.add(model);
            }
            msisdnRecordService.addMsisdn(list);
        }
        return ResponseInfoUtil.success();
    }

    @RequestMapping(value = "/save/black/{param}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseInfo saveBlack(@PathVariable("param") String param, @RequestBody String msisdn) {
        if (!StringUtils.isEmpty(msisdn)) {
            String str = msisdn.replaceAll("\r\n", "");
            String[] msisdnArr = str.split(",");
            if (StringUtils.isEmpty(param)) {
                return ResponseInfoUtil.success();
            } else {
                String[] field = param.split(",");
                msisdnRecordService.saveBlack(msisdnArr, field);
            }
        }
        return ResponseInfoUtil.success();
    }

}
