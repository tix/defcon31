package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.GpReportModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.service.IGpReportService;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/***
 *
 * @Author David
 * @Date 14:49 2019/2/25
 * @param
 * @return
 **/

@Controller
@Slf4j
public class GpReportController {

    @Autowired
    private IGpReportService gpReportService;



    @RequestMapping(value="/GpReport/config/save",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo saveConfig(@RequestBody JSONObject jsonObject){
        try {
            GpReportModel gpReportModel = JSONObject.parseObject(jsonObject.toJSONString(), GpReportModel.class);
            String mail = getEmailByJSON(jsonObject);
            gpReportModel.setEmail(mail);
            gpReportService.save(gpReportModel);
            return ResponseInfoUtil.success("success");
        }catch (Exception e){
            return ResponseInfoUtil.error();
        }
    }

    private String getEmailByJSON(JSONObject jsonObject) {
        List<String> emails = JSONObject.parseArray(jsonObject.getJSONArray("emails").toJSONString(), String.class);
        StringBuffer mails = new StringBuffer();
        for (String email : emails) {
            mails.append(email).append(",");
        }
        String mail =new String(mails);
        mail = mail.substring(0,mail.length() - 1);
        return mail;
    }

    /**
     * 更新email
     * @param param
     * @return
     */
    @PostMapping(value = "/GpReport/config/update/email")
    @ResponseBody
    public ResponseInfo updateEmail(@RequestBody JSONObject param){
        String id = param.getString("id");
        String mail = getEmailByJSON(param);
        gpReportService.updateEmail(id,mail);
        return ResponseInfoUtil.success();
    }

    /**
     * 批量修改状态
     * @param request
     * @param ids
     * @return
     */
    @PostMapping(value = "/GpReport/config/multi/changeStatus")
    @ResponseBody
    public ResponseInfo multiUpdateStatus(HttpServletRequest request,@RequestBody List<String> ids){
        String status = request.getParameter("status");
        gpReportService.multiUpdateStatus(status,ids);
        return ResponseInfoUtil.success();
    }

    @PostMapping(value = "/GpReport/config/multiDelete")
    @ResponseBody
    public ResponseInfo multiDelete(@RequestBody List<String> ids){
        gpReportService.multiDelete(ids);
        return ResponseInfoUtil.success();
    }


    /**
     * 删除
     * @param request
     * @return
     */
    @RequestMapping(value="/GpReport/config/delete",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo deleteConfig(HttpServletRequest request){
        try {
            String id = request.getParameter("id");
            gpReportService.delete(id);
            return ResponseInfoUtil.success("delete success");
        }catch (Exception e){
            return ResponseInfoUtil.error();
        }
    }


    @RequestMapping(value="/GpReport/config/getNames",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo getNames(){
       return ResponseInfoUtil.success(gpReportService.getNames());
    }

    @PostMapping(value="/GpReport/config/list")
    @ResponseBody
    public ResponseInfo getConfig(@RequestBody Map<String,String> params){
        String name = params.get("name");
        int page = Integer.parseInt(params.get("page"));
        int limit = Integer.parseInt(params.get("limit"));
        String online = params.get("online");
        PageVO list= gpReportService.getConfigList(name,page,limit,online);
        return ResponseInfoUtil.success(list);
    }


    @RequestMapping(value = "/GpReport/config/show",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo findById(HttpServletRequest request){
        String id = request.getParameter("id");
        return ResponseInfoUtil.success(gpReportService.findById(id));
    }


    @RequestMapping(value = "/GpReport/config/changeStatus" ,method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo changeStatus(HttpServletRequest request){
        try {
            String id = request.getParameter("id");
            int type = Integer.parseInt(request.getParameter("type"));
            gpReportService.changeStatus(id,type);
            return ResponseInfoUtil.success("success");
        }catch (Exception e){
            return ResponseInfoUtil.error();
        }
    }


    @RequestMapping(value = "/GpReport/config/check",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo reportGpConfig(){
        List<GpReportModel> reportModelList = gpReportService.getStartConfig();
        for(GpReportModel model:reportModelList){
            if(!String.valueOf(NumberEnum.TWO.getNum()).equalsIgnoreCase(model.getOnline())){
                gpReportService.checkConfig(model);
            }
        }
        return ResponseInfoUtil.success();
    }

}
