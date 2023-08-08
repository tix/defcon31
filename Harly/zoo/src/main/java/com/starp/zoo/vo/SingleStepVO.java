package com.starp.zoo.vo;

import lombok.Data;

/**
 * offer破解日志,详情页面实体.
 *
 * @author magic
 * @date 2021/7/27
 */
@Data
public class SingleStepVO {
    private Integer stepNumber;

    private String body;

    private String encodeBody;

    private String encodeResponse;
}
