package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.UserOfferExcelModel;
import com.starp.zoo.service.IUserOfferDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author magic
 * @date 2020/12/7
 */
@RestController
public class UserOfferDetailController {

    @Autowired
    private IUserOfferDetailService userOfferDetailService;

    /**
     * 获取列表
     *
     * @param jsonObject
     * @return
     */
    @PostMapping("/userOffer/getDetail")
    public ResponseInfo getUserOfferDetail(@RequestBody JSONObject jsonObject) {
        String pageStr = jsonObject.getString("page");
        int page = Integer.parseInt(pageStr);
        String app = jsonObject.getString(ZooConstant.APP);
        String operator = jsonObject.getString(ZooConstant.OPERATOR);
        String userCount = jsonObject.getString(ZooConstant.USER_COUNT);
        String datetime = jsonObject.getString(ZooConstant.DATE_TIME);
        String tableName = "t_app_user_event";
        String toDay = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String strSelectDate = datetime.split(" ")[0].replace("-", "");
        if (!toDay.equals(strSelectDate)) {
            tableName = "t_app_user_event" + strSelectDate;
        }
        JSONObject json = userOfferDetailService.getDetail(tableName, app, operator, userCount, page, datetime);
        return ResponseInfoUtil.success(json);
    }

    @PostMapping("/userOffer/export")
    public ResponseInfo export(@RequestBody JSONObject jsonObject) {
        String pageStr = jsonObject.getString("page");
        int page = Integer.parseInt(pageStr);
        String app = jsonObject.getString(ZooConstant.APP);
        String operator = jsonObject.getString(ZooConstant.OPERATOR);
        String userCount = jsonObject.getString(ZooConstant.USER_COUNT);
        String datetime = jsonObject.getString(ZooConstant.DATE_TIME);
        String tableName = "t_app_user_event";
        String toDay = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String strSelectDate = datetime.split(" ")[0].replace("-", "");
        if (!toDay.equals(strSelectDate)) {
            tableName = "t_app_user_event" + strSelectDate;
        }
        userOfferDetailService.exportData(tableName, app, operator, userCount, page, datetime);
        return ResponseInfoUtil.success();
    }

    @PostMapping("/userOffer/getExcelList")
    public ResponseInfo getExcelList() {
        List<UserOfferExcelModel> excelList = userOfferDetailService.getExcelList();
        return ResponseInfoUtil.success(excelList);
    }
}
