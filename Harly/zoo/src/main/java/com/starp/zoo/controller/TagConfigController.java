package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.BadRequestException;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.TagModel;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.service.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/***
 * 
 * @Author David
 * @Date 10:57 2019/3/6
 * @param  
 * @return 
 **/
@RestController
@RequestMapping("/config/tag")
public class TagConfigController {

    @Autowired
    private ITagService tagService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public ResponseInfo getTagList(@RequestParam int page, @RequestParam int limit,
                                   @RequestParam(required = false) String type,@RequestParam(required = false) String name){
        return ResponseInfoUtil.success(tagService.getTagList(page,limit,type,name));
    }


    @RequestMapping(value = "/get/{id}",method = RequestMethod.GET)
    public ResponseInfo getTagById(@PathVariable String id){
        return ResponseInfoUtil.success(tagService.findTagById(id));
    }

    @PostMapping(value = "/save")
    public ResponseInfo saveTag(@RequestBody JSONObject jsonObject) throws BadRequestException {
        TagModel model = JSONObject.parseObject(jsonObject.toJSONString(), TagModel.class);
        JSONArray jsonArray = jsonObject.getJSONArray("tagOfferList");
        List<String> offerIds = null;
        if(jsonArray != null) {
            offerIds = JSONObject.parseArray(jsonArray.toJSONString(), String.class);
        }
        return ResponseInfoUtil.success(tagService.saveTag(model, offerIds));
    }

    @GetMapping(value = "/delete/{id}")
    public ResponseInfo deleteTag(@PathVariable String id){
        tagService.delete(id);
        return ResponseInfoUtil.success();
    }

    @PostMapping(value = "/multi/delete")
    public ResponseInfo multiDelete(@RequestBody List<String> ids){
        tagService.multiDelete(ids);
        return ResponseInfoUtil.success();
    }

    @GetMapping(value = "/get/name/{name}")
    public ResponseInfo getTagName(@PathVariable String name){
        return ResponseInfoUtil.success(tagService.findTagName(name));
    }

    @GetMapping(value = "/options")
    public ResponseInfo options(){
        return ResponseInfoUtil.success(tagService.getAllOptions());
    }

    @GetMapping(value ="/offer-tag/{id}")
    public ResponseInfo getOfferScript(@PathVariable String id){
        return ResponseInfoUtil.success(tagService.getOfferTag(id));
    }

}
