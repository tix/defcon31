package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.GpReportModel;
import com.starp.zoo.entity.zoo.ResultGpReportModel;
import com.starp.zoo.repo.zoo.GpReportRepo;
import com.starp.zoo.service.IGpReportService;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.util.EmailUtil;
import com.starp.zoo.util.HttpUtil;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/***
 *
 * @Author David
 * @Date 14:53 2019/2/25
 * @param
 * @return
 **/

@SuppressWarnings("AliDeprecation")
@Slf4j
@Service
public class GpRepoertServiceImpl implements IGpReportService {

    @Autowired
    private GpReportRepo gpReportRepo;

    @Autowired
    private EmailUtil emailUtil;


    @Override
    public void save(GpReportModel gpReportModel) {
        if (StringUtils.isEmpty(gpReportModel.getIdentification())) {
            gpReportModel.setOnline("0");
        } else {
            GpReportModel config = gpReportRepo.findConfig(gpReportModel.getIdentification());
            gpReportModel.setOnline(config.getOnline());
            gpReportModel.setStatus(config.getStatus());
        }
        gpReportRepo.save(gpReportModel);
    }

    @Override
    public void delete(String id) {
        gpReportRepo.deleteById(id);
    }

    /**
     * 获取配置列表
     *
     * @param name
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PageVO getConfigList(String name, int page, int limit, String online) {
        Specification specification = new Specification<GpReportModel>() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(name)) {
                    predicates.add(criteriaBuilder.equal(root.get("name"), name));
                }
                if (!StringUtils.isEmpty(online)) {
                    predicates.add(criteriaBuilder.equal(root.get("online"), online));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime")));
                return criteriaQuery.getRestriction();

            }
        };
        PageVO<GpReportModel> pageVO = new PageVO();
        Long total = gpReportRepo.count(specification);
        pageVO.setTotal(total);
        page = page >= 1 ? page - 1 : 0;
        List<GpReportModel> list = gpReportRepo.findAll(specification, PageRequest.of(page, limit)).getContent();
        pageVO.setList(list);
        pageVO.setLimit(limit);
        pageVO.setPage(page);
        return pageVO;
    }

    /**
     * 通过ID查找对象
     *
     * @param id
     * @return
     */
    @Override
    public GpReportModel findById(String id) {
        GpReportModel model = gpReportRepo.findConfig(id);
        return model;
    }

    /**
     * 改变model状态
     *
     * @param id
     * @param type
     */
    @Override
    public void changeStatus(String id, int type) {
        gpReportRepo.updateStatus(id, type);
    }

    /**
     * 获取开启列表
     *
     * @return
     */
    @Override
    public List<GpReportModel> getStartConfig() {
        List<GpReportModel> list = gpReportRepo.findAllByStatus(1);
        return list;
    }

    /**
     * 定时检查包是否在线，并更新在线状态
     *
     * @param model
     */
    @Override
    public void checkConfig(GpReportModel model) {
        try {
            String link = model.getLink();
            JSONObject result = HttpUtil.doGet(link.trim());
            log.info("GP model is:{},result:{}", JSON.toJSONString(model), result.toJSONString());
            String isOnline = "";
            if (ResponseInfoEnum.SUCCESS.getCode().toString().equalsIgnoreCase(result.getString(HttpUtil.CODE))) {
                isOnline = ZooConstant.GP_REPORT_ONLINE;
                // 若上次检查是未上线，当前检查是上线，则发送邮件通知
                if (ZooConstant.GP_REPORT_NOT_ONLINE.equalsIgnoreCase(model.getOnline())) {
                    sendMailReport(model, isOnline);
                }
                if (StringUtils.isEmpty(model.getUpdateDate())) {
                    String[] msgs = result.getString("msg").split("<div class=\"xg1aie\">")[1].split("</div></div></div>")[0].split(" ");
                    String updateDate = getUpdateDate(msgs);
                    model.setUpdateDate(updateDate);
                    gpReportRepo.save(model);
                }
                log.info("mode:{} is online ", JSON.toJSONString(model));
            } else if (ResponseInfoEnum.NOT_FOUND.getCode().toString().equalsIgnoreCase(result.getString(HttpUtil.CODE))) {
                isOnline = ZooConstant.GP_REPORT_OFFLINE;
                // 若上次检查是上线，当前检查是下线，则发送邮件通知
                if (ZooConstant.GP_REPORT_ONLINE.equalsIgnoreCase(model.getOnline())) {
                    sendMailReport(model, isOnline);
                } else if (ZooConstant.GP_REPORT_NOT_ONLINE.equalsIgnoreCase(model.getOnline())) {
                    isOnline = ZooConstant.GP_REPORT_NOT_ONLINE;
                }
                if (StringUtils.isEmpty(model.getOfflineDate())) {
                    model.setOfflineDate(DateUtil.formatDay(new Date()));
                    gpReportRepo.save(model);
                }
                log.info("mode:{} is offline ", JSON.toJSONString(model));
            }
            gpReportRepo.updateIsOnline(model.getIdentification(), isOnline);
        } catch (Exception e) {
            log.info("GP MODEL CHECK CONFIG ERROR : {}, MODEL : {} ", JSON.toJSONString(e), JSON.toJSONString(model));
        }
    }

    public String getUpdateDate(String[] msgs) {
        String result = "";
        String month = "01";
        switch (msgs[0]) {
            case ZooConstant.UK_JANUARY:
                month = "01";
                break;
            case ZooConstant.UK_FEBRUARY:
                month = "02";
                break;
            case ZooConstant.UK_MARCH:
                month = "03";
                break;
            case ZooConstant.UK_APRIL:
                month = "04";
                break;
            case ZooConstant.UK_MAY:
                month = "05";
                break;
            case ZooConstant.UK_JUNE:
                month = "06";
                break;
            case ZooConstant.UK_JULY:
                month = "07";
                break;
            case ZooConstant.UK_AUGUST:
                month = "08";
                break;
            case ZooConstant.UK_SEPTEMBER:
                month = "09";
                break;
            case ZooConstant.UK_OCTOBER:
                month = "10";
                break;
            case ZooConstant.UK_NOVEMBER:
                month = "11";
                break;
            case ZooConstant.UK_DECEMBER:
                month = "12";
                break;
            default:
                break;
        }
        String day = msgs[1].substring(0, msgs[1].length() - 1);
        if (String.valueOf(NumberEnum.ONE.getNum()).equals(day)) {
            day = "01";
        } else if (String.valueOf(NumberEnum.TWO.getNum()).equals(day)) {
            day = "02";
        } else if (String.valueOf(NumberEnum.THREE.getNum()).equals(day)) {
            day = "03";
        } else if (String.valueOf(NumberEnum.FOUR.getNum()).equals(day)) {
            day = "04";
        } else if (String.valueOf(NumberEnum.FIVE.getNum()).equals(day)) {
            day = "05";
        } else if (String.valueOf(NumberEnum.SIX.getNum()).equals(day)) {
            day = "06";
        } else if (String.valueOf(NumberEnum.SEVEN.getNum()).equals(day)) {
            day = "07";
        } else if (String.valueOf(NumberEnum.EIGHT.getNum()).equals(day)) {
            day = "08";
        } else if (String.valueOf(NumberEnum.NIEN.getNum()).equals(day)) {
            day = "09";
        }
        result = msgs[2] + "-" + month + "-" + day;
        return result;
    }

    /**
     * 查找model名称列表
     *
     * @return
     */
    @Override
    public List<OptionVO> getNames() {
        List<OptionVO> optionVOS = new ArrayList<>();
        List<ResultGpReportModel> reportModels = gpReportRepo.findAllResultGpReportModel();
        for (ResultGpReportModel reportModel : reportModels) {
            OptionVO optionVO = new OptionVO();
            optionVO.setIdentification(reportModel.getIdentification());
            optionVO.setLabel(reportModel.getName());
            optionVO.setValue(reportModel.getName());
            optionVOS.add(optionVO);
        }
        return optionVOS;
    }

    /**
     * 批量更改状态
     *
     * @param status
     * @param ids
     */
    @Override
    public void multiUpdateStatus(String status, List<String> ids) {
        gpReportRepo.multilUpdateStatus(status, ids);
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    public void multiDelete(List<String> ids) {
        for (String id : ids) {
            gpReportRepo.deleteById(id);
        }
    }

    @Override
    public void updateEmail(String id, String mail) {
        gpReportRepo.updateEmail(id, mail);
    }

    /**
     * 发送邮件
     *
     * @param model
     */
    private void sendMailReport(GpReportModel model, String isOnline) {
        try {
            Map<String, Object> content = new HashMap<>(8);
            String subject = "";
            if (ZooConstant.GP_REPORT_ONLINE.equalsIgnoreCase(isOnline)) {
                subject = ZooConstant.GPREPORT_MAIL_ONLINE_SUBJECT;
            } else {
                subject = ZooConstant.GPREPORT_MAIL_OFFLINE_SUBJECT;
            }
            content.put(ZooConstant.TITLE, subject);
            content.put(ZooConstant.EMAIL_DATE, DateUtil.formatyyyyMMddHHmmss(new Date()));
            content.put(ZooConstant.GP_REPORT_NAME, model.getName());
            content.put(ZooConstant.GP_REPORT_LINK, model.getLink());
            content.put(ZooConstant.GP_REPORT_CUSTOMER, model.getCustomer());
            String defaultEmail = model.getEmail();
            if (!StringUtils.isEmpty(defaultEmail)) {
                String[] mails = defaultEmail.split(",");
                for (String mail : mails) {
                    emailUtil.sendMimeMessageMail(ZooConstant.GPREPORT_MAIL_TEMPLATE, mail, subject, content);
                }
            }
        } catch (Exception e) {
            log.info(JSON.toJSONString(e));
        }
    }
}
