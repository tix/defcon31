package com.starp.zoo.entity.zoo;

import lombok.Data;

/**
 * @author magic
 * @date 2020/12/25
 */
@Data
public class UserOfferExcelModel {
    /**
     * excel名
     */
    private String excelName;

    /**
     * excel下载链接
     */
    private String excelDownloadUrl;
}
