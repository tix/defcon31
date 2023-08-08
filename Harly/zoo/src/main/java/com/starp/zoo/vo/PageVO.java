package com.starp.zoo.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Charles
 * @date 2019/2/21
 * @description :
 */
@Data
public class PageVO<T> {

    List<T> list;

    Long total;

    Integer limit;

    Integer page;
}
