package com.starp.zoo.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Charles
 * @date 2019/3/5
 * @description :
 */
@Data
public class TagsOptionVO {

    private List<OptionVO> stack;

    private List<OptionVO> group;

    private List<OptionVO> others;
}
