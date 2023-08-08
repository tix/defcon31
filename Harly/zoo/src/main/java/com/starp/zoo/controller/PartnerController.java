package com.starp.zoo.controller;


import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.PartnerModel;
import com.starp.zoo.service.IPartnerService;
import com.starp.zoo.vo.OptionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author covey
 */
@RestController
@RequestMapping("/partner")
public class PartnerController {

    @Autowired
    private IPartnerService partnerService;

    /**
     * 查询所有广告主
     *
     * @return
     */
    @GetMapping("/list")
    public ResponseInfo getList(@RequestParam(required = false) Integer page,
                                @RequestParam(required = false) Integer limit,
                                @RequestParam(required = false) String partnerName,
                                @RequestParam(required = false) String partnerId) {
        if (StringUtils.isEmpty(partnerName) && !StringUtils.isEmpty(partnerId)) {
            PartnerModel partnerModel = partnerService.findByPartnerId(partnerId);
            if (partnerModel != null) {
                partnerName = partnerModel.getPartnerName();
            }
        }
        if(page == null){
            page = 1;
        }
        if(limit == null){
            limit = 10;
        }
        return ResponseInfoUtil.success(partnerService.getList(page, limit, partnerName));
    }

    /**
     * 新增和修改广告主
     *
     * @param partnerModel
     * @return
     */
    @PostMapping("/save")
    public ResponseInfo save(@RequestBody PartnerModel partnerModel) {
        if (StringUtils.isEmpty(partnerModel.getPartnerId())) {
            PartnerModel last = partnerService.findLast();
            if (last == null) {
                partnerModel.setPartnerId("A1");
            } else {
                String lastId = partnerService.findLast().getPartnerId();
                String substring = lastId.substring(1);
                String partnerId = String.valueOf(Integer.parseInt(substring) + 1);
                partnerId = "A" + partnerId;
                partnerModel.setPartnerId(partnerId);
            }
        }
        partnerService.save(partnerModel);

        return ResponseInfoUtil.success();
    }

    /**
     * 删除广告主
     *
     * @param partnerModel
     * @return
     */
    @PostMapping("/delete")
    public ResponseInfo delete(@RequestBody PartnerModel partnerModel) {
        partnerService.delete(partnerModel);
        return ResponseInfoUtil.success();
    }

    /**
     * 查询广告主名称
     *
     * @return
     */
    @RequestMapping("/partnerNames")
    public ResponseInfo partnerNames() {
        List<OptionVO> list = partnerService.findPartnerNames();
        return ResponseInfoUtil.success(list);
    }

    /**
     * 查询广告主ID
     *
     * @return
     */
    @RequestMapping("/partnerIds")
    public ResponseInfo fetchPartnerId() {
        List<OptionVO> list = partnerService.findPartnerIds();
        return ResponseInfoUtil.success(list);
    }

    @GetMapping(value = "/check/exist/testOffer")
    public ResponseInfo checkExistTestOffer(@RequestParam(required = false) String id) {
        return ResponseInfoUtil.success(partnerService.existTestOffer(id));
    }

}
