package com.starp.zoo.vo;

import lombok.Data;

/**
 * @author Charles
 * @date 2019/2/21
 * @description :
 */
@Data
public class OptionVO {
    public OptionVO() {
    }

    public OptionVO(String identification, String label, String value) {
        this.identification = identification;
        this.label = label;
        this.value = value;
    }

    String identification;

    String label;

    String value;
}
