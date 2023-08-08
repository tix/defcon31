package com.starp.zoo.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.ProductTypeModel;
import com.starp.zoo.service.IProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author Charles
 * Created by eric.luo on 2017/11/8.
 */
@Controller
public class ProductTypeController {

    @Autowired
    IProductTypeService productTypeServiceImpl;

    @RequestMapping(value = "/product/type/add", method = { RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo saveModel(ProductTypeModel productTypeModel){
        productTypeServiceImpl.saveModel(productTypeModel);
        return ResponseInfoUtil.success();
    }

    @RequestMapping(value = "/product/type/delete", method = { RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo deleteSingleTask(@RequestParam String id) throws Exception{
        productTypeServiceImpl.deleteProductModel(id);
        return ResponseInfoUtil.success();
    }

    @RequestMapping(value = "/product/type/get", method = { RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONPObject getSingleTask(@RequestParam String id, @RequestParam String callback) throws Exception{
        return new JSONPObject(callback, productTypeServiceImpl.getProductTypeModel(id));
    }

    @RequestMapping(value = "/product/type/list", method = RequestMethod.GET)
    @ResponseBody
    public JSONPObject listAppInfo(@RequestParam String callback) throws Exception{
        return new JSONPObject(callback, ResponseInfoUtil.success(productTypeServiceImpl.getProductTypeList()));
    }
}
