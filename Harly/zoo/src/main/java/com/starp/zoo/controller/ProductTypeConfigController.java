package com.starp.zoo.controller;

import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.ProductTypeModel;
import com.starp.zoo.service.IProductTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Charles
 * @date 2019/3/6
 * @description :
 */
@RestController
@RequestMapping("/config/product")
public class ProductTypeConfigController {

    @Autowired
    private IProductTypeService productTypeService;

    @PostMapping("/save")
    public ResponseInfo save(@RequestBody ProductTypeModel productTypeModel){
        productTypeService.saveModel(productTypeModel);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/list")
    public ResponseInfo list(@RequestParam int page, @RequestParam int limit,
                             @RequestParam(required = false) String name){
        return ResponseInfoUtil.success(productTypeService.getList(page, limit, name));
    }

    @GetMapping("/delete/{id}")
    public ResponseInfo delete(@PathVariable String id){
        productTypeService.deleteById(id);
        return ResponseInfoUtil.success();
    }

    @PostMapping(value = "/multi/delete")
    public ResponseInfo multiDelete(@RequestBody List<String> ids){
        productTypeService.multiDelete(ids);
        return ResponseInfoUtil.success();
    }

    @GetMapping(value = "/names")
    public ResponseInfo names(){
        return ResponseInfoUtil.success(productTypeService.getAllNames());
    }
}
