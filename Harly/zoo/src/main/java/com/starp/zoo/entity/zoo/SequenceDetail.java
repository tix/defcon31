package com.starp.zoo.entity.zoo;

import lombok.Data;

/**
 * @author magic
 * @date 2020/12/7
 */
@Data
public class SequenceDetail {
    /**
     * 次序
     */
    private String seq;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 成功或者失败的最新url
     */
    private String successOrUrl;
    /**
     * offerName
     */
    private String offerName;
}
