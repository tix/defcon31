package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.config.AndroidGroupGetDesConfig;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.util.DesUtil;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Charles
 * @date 2019/8/9
 * @description : 安卓组 临时信息保存获取及管理
 */
@RestController
public class AndroidGroupTempInfoController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "cluster1RedisTemplate")
    private StringRedisTemplate cluster1RedisTemplate;

    @PostMapping(value = {"/kv/m7Klx"})
    public ResponseInfo sendInfoToRedis(@PathVariable(required = false) String app, @RequestBody JSONObject jsonObject, HttpServletRequest request) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        String encodeStr = jsonObject.getString(ZooConstant.VALUE);
        JSONObject params = JSONObject.parseObject(desUtil.decode(encodeStr));
        if (params != null) {
            String hKey = params.getString("token");
            stringRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_APP_TEMP_INFO, hKey, params.toJSONString());
        }
        return ResponseInfoUtil.success();
    }

    /**
     * send app temp info
     * @param value
     * @param request
     * @return
     * @throws Exception
     */
    @Timed
    @PostMapping(value = {"uic/d9xhw","/ciw/xnci2","/bjs/io8ah","/zxb/4njz9","/xsb/sdau2","/ydb/cj78i","/ube/xbi8h","/ube/hob2z","/sks/aRrks", "/ds2/Hjh2j", "/yMk/H2JKc", "/gbn/Gj2kt", "/xjf/ts8nP", "/fvp/3nhfG", "/miz/Nu5kS", "/wgh/vnf3k", "/axs/nbKot", "/jts/vnkRt", "/ksh/ftjk7", "/bnB/bns3k", "/bns/mhkot", "/oj2/d65nl"})
    public String sendInfoToRedis( @RequestBody String value, HttpServletRequest request) throws Exception { JSONObject params =null;
        try{
            params = JSONObject.parseObject(value);
        }catch (Exception e){
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(),ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return ResponseInfoUtil.errorMsgWithoutDesc(errorMessage);
        }
        String hKey = params.getString("token");
        if(!StringUtils.isEmpty(hKey)){
            stringRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_APP_TEMP_INFO, hKey, params.toJSONString());
            JSONObject str = new JSONObject();
            str.put("string","success");
            return ResponseInfoUtil.successWithoutDesc(JSON.toJSONString(str));
        }else {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.TOKEN_NULL.getCode(),ResponseInfoEnum.TOKEN_NULL.getMsg());
            return ResponseInfoUtil.errorMsgWithoutDesc(errorMessage);
        }
    }

    @PostMapping(value = {"/kv/lmx9V"})
    public ResponseInfo getInfoFromRedis(@PathVariable(required = false) String app, @RequestBody JSONObject jsonObject, HttpServletRequest request) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        String encodeStr = jsonObject.getString(ZooConstant.VALUE);
        JSONObject params = JSONObject.parseObject(desUtil.decode(encodeStr));
        String token = params.getString("token");
        String info = (String) cluster1RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_APP_TEMP_INFO, token);
        String desString = info != null ? desUtil.encode(info) : null;
        return ResponseInfoUtil.success(desString);
    }

    /**
     * GET_APP_TEMP_INFO
     * @param value
     * @param request
     * @return
     * @throws Exception
     */
    @Timed
    @PostMapping(value = {"/cua/dsn6y","/acb/zni2n", "/c8j/xnkas","/yxn/ion9y","/max/sda7h","/xyu/cj52j","/yzi/oju6m","/bGn/fpKs2", "/mk8/JKnj2","/iBN/U82jb", "/vjo/3ksJs", "/xbn/tkJs3", "/cnv/k5htR", "/P2M/bJK3D", "/vkg/aksT2" ,"/sjt/vns4T", "/fnc/tk9bf", "/bnt/rvjgf", "/cmx/abjTg", "/bit/kxgFe", "/mld/bjd2a"})
    public String getInfoFromRedis( @RequestBody String value, HttpServletRequest request) throws Exception {
        JSONObject params =null;
        try{
            params = JSONObject.parseObject(value);
        }catch (Exception e){
            String errorCode = "500";
            return errorCode;
        }
        String token = params.getString("token");
        if(!StringUtils.isEmpty(token)){
            String info = (String) cluster1RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_APP_TEMP_INFO, token);
            JSONObject str = JSON.parseObject(info);
            String target = str.getString("target");
            return target;
        }else {
            String errorCode = "500";
            return errorCode;
        }
    }

    @GetMapping(value = {"/android/app/temp/info/list"})
    public ResponseInfo getInfoListFromRedis() {
        List<JSONObject> list = new ArrayList<>();
        Map<Object, Object> map = cluster1RedisTemplate.opsForHash().entries(CacheNameSpace.ZOO_APP_TEMP_INFO);
        if (map != null && map.size() > 0) {
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                JSONObject json = new JSONObject();
                json.put("token", entry.getKey());
                json.put("json", entry.getValue());
                list.add(json);
            }
        }
        return ResponseInfoUtil.success(list);
    }

    @GetMapping(value = {"/android/app/temp/info/delete/{token}"})
    public ResponseInfo deleteInfoListFromRedis(@PathVariable String token) {
        return ResponseInfoUtil.success(stringRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_APP_TEMP_INFO, token));
    }

    /**
     * format错误信息
     * @param code
     * @param msg
     * @return
     */
    private String formatErrorMessage(Integer code, String msg) {
        Map<String,Object> errorMessage = new HashMap<>(1);
        errorMessage.put("code",code);
        errorMessage.put("msg",msg);
        return JSON.toJSONString(errorMessage);
    }
}
