package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.ScriptModel;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.service.IScriptService;
import com.starp.zoo.vo.OptionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Charles
 * @date 2019/3/4
 * @description :
 */
@RestController
@RequestMapping("/config/script")
public class ScriptConfigController {

    @Autowired
    private IScriptService scriptService;

    @GetMapping(value = "/options")
    public ResponseInfo getScriptOptions(@RequestParam(required = false) String country){
        return ResponseInfoUtil.success(scriptService.getOptions(country));
    }

    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public ResponseInfo getScriptList(@RequestBody JSONObject jsonObject){
        int page = jsonObject.getInteger("page");
        int limit = jsonObject.getInteger("limit");
        String type = jsonObject.getString("type");
        String name = jsonObject.getString("name");
        String country = jsonObject.getString("country");
        String eventType = jsonObject.getString("eventType");
        JSONArray jsonArray = jsonObject.getJSONArray("script");
        List<String> scripts = jsonArray == null ? null : Arrays.asList(jsonArray.toArray(new String[jsonArray.size()]));
        return ResponseInfoUtil.success(scriptService.getScriptList(page,limit,type,name,country,eventType,scripts));
    }

    @GetMapping(value="/online/list")
    public ResponseInfo getOnlineScriptList(@RequestParam String type){
        return ResponseInfoUtil.success(scriptService.getOnlineJsList(type));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseInfo getScript(@PathVariable String id){
        return ResponseInfoUtil.success(scriptService.getScript(id));
    }

    @PostMapping(value = "/save")
    public ResponseInfo save(@RequestBody JSONObject jsonObject){
        ScriptModel scriptModel = JSONObject.parseObject(jsonObject.toJSONString(), ScriptModel.class);
        JSONArray jsonArray = jsonObject.getJSONArray("scriptOfferList");
        List<String> offerIds = null;
        if(jsonArray != null) {
            offerIds = JSONObject.parseArray(jsonArray.toJSONString(), String.class);

        }
        return ResponseInfoUtil.success(scriptService.saveScript(scriptModel, offerIds));
    }

    @GetMapping(value = "/delete/{id}")
    public ResponseInfo deleteTag(@PathVariable String id){
        scriptService.delete(id);
        return ResponseInfoUtil.success();
    }

    @PostMapping(value = "/multi/delete")
    public ResponseInfo multiDelete(@RequestBody List<String> ids){
        scriptService.multiDelete(ids);
        return ResponseInfoUtil.success();
    }

    @GetMapping(value = "/get/name")
    public ResponseInfo changeOffer(@RequestParam String query){
        return ResponseInfoUtil.success(scriptService.findScriptName(query));
    }

    @GetMapping(value ="/offer-script/{id}")
    public ResponseInfo getOfferScript(@PathVariable String id){
        return ResponseInfoUtil.success(scriptService.getOfferScript(id));
    }

    @GetMapping(value ="/fetchScriptOption")
    public ResponseInfo fetchScriptOption(){
        return ResponseInfoUtil.success(scriptService.fetchScriptOption());
    }

    @PostMapping(value = "/checkRegular")
    @ResponseBody
    public ResponseInfo checkRegular(@RequestBody JSONObject jsonObject){
        String regular = jsonObject.getString("query");
        String country = jsonObject.getString("country");
        String type = jsonObject.getString("type");
        String eventType = jsonObject.getString("eventType");
        String identification = jsonObject.getString("identification");
        return ResponseInfoUtil.success(scriptService.checkExistRegular(regular,country,identification,type,eventType));
    }

    @GetMapping(value = "/findAllNames")
    public ResponseInfo findAllNames(HttpServletRequest request){
        List<OptionVO> list=scriptService.findAllNames();
        return ResponseInfoUtil.success(list);
    }

    @GetMapping(value = "/findJsByName")
    public ResponseInfo findJsByName(HttpServletRequest request){
        String name = request.getParameter("name");
        ScriptModel scriptModel=scriptService.findJsByName(name);
        return ResponseInfoUtil.success(scriptModel);
    }

    @PostMapping(value = "/fetchResult")
    public ResponseInfo fetchResult(@RequestBody List<JSONObject> list){
        List<String> offerNames = new ArrayList<>();
        for (JSONObject jsonObject : list) {
            String offerName = jsonObject.getString("offerName");
            offerNames.add(offerName);
        }
        return ResponseInfoUtil.success(scriptService.fetchResult(offerNames));
    }



    @PostMapping(value = "/fetchScriptsDetail")
    public ResponseInfo fetchScriptsDetail(@RequestBody JSONObject jsonObject){
        String country = jsonObject.getString("country");
        String name = jsonObject.getString("name");
        String eventType = jsonObject.getString("eventType");
        String type = jsonObject.getString("type");
        JSONArray jsonArray = jsonObject.getJSONArray("script");
        List<String> scripts = jsonArray == null ? null : Arrays.asList(jsonArray.toArray(new String[jsonArray.size()]));
        List<OptionVO> result = scriptService.fetchScriptsDetail(country,name,type,eventType,scripts);
        return ResponseInfoUtil.success(result);
    }

}
