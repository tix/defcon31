package com.starp.zoo.vo;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;

import java.util.Date;

/**
 * @author magic
 * @date 2021/9/24
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
@Data
public class HtmlInfoVO {

    private Date createTime;

    private String appId;

    private String appName;

    private String offerId;

    private String offerName;

    private String originUrl;

    private String userId;

    /**
     * s3 存储地址
     */
    private String saveUrl;

    private String msisdn;
}
