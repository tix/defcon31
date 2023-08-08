package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONObject;
import com.amazonaws.util.StringUtils;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.constant.ZooConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author david
 */
@Controller
public class MachineController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "cluster1RedisTemplate")
    private StringRedisTemplate cluster1RedisTemplate;

    @PostMapping(value = "/machine/blackName/save")
    @ResponseBody
    public ResponseInfo saveMachineBlackName(@RequestBody JSONObject jsonObject) {
        String oldName = jsonObject.getString(ZooConstant.OLD_NAME);
        String name = jsonObject.getString(ZooConstant.NAME);
        Boolean existBlackModel = cluster1RedisTemplate.opsForHash().hasKey(ZooConstant.BLACK_MACHINE, name);
        if (existBlackModel != null && existBlackModel) {
            return ResponseInfoUtil.error(400, "exist black machine name");
        }
        if (StringUtils.isNullOrEmpty(oldName)) {
            stringRedisTemplate.opsForHash().put(ZooConstant.BLACK_MACHINE, name, name);
        } else {
            stringRedisTemplate.opsForHash().delete(ZooConstant.BLACK_MACHINE, oldName);
            stringRedisTemplate.opsForHash().put(ZooConstant.BLACK_MACHINE, name, name);
        }
        return ResponseInfoUtil.success();
    }

    @GetMapping(value = "/machine/blackName/delete")
    @ResponseBody
    public ResponseInfo deleteBlackMachine(@RequestParam String name) {
        Boolean existMachine = cluster1RedisTemplate.opsForHash().hasKey(ZooConstant.BLACK_MACHINE, name);
        if (existMachine != null && existMachine) {
            stringRedisTemplate.opsForHash().delete(ZooConstant.BLACK_MACHINE, name);
        }
        return ResponseInfoUtil.success();
    }


    @GetMapping(value = "/machine/blackName/list")
    @ResponseBody
    public ResponseInfo getBlackMachineList() {
        Boolean existMachine = cluster1RedisTemplate.hasKey(ZooConstant.BLACK_MACHINE);
        System.out.println(existMachine);
        Set<Object> nameSet = cluster1RedisTemplate.opsForHash().keys(ZooConstant.BLACK_MACHINE);
        List<String> nameList = new ArrayList<>();
        if (nameSet != null && nameSet.size() > 0) {
            nameList = new ArrayList(nameSet);
        }
        return ResponseInfoUtil.success(nameList);
    }
}
