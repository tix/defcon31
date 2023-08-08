package com.starp.zoo.entity.statistics;

import com.starp.zoo.entity.common.EntityBase;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/8.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_sta_mymap")
public class MyMapModel extends EntityBase{
    /**
     * key值
     */
    private String itemKey;
    
    /**
     * value值
     */
    private String itemValue;
    
    /**
     * 关键字名称 map的指针
     */
    private String keyName;
    
    /**
     * 关键字名称 对应的值
     */
    private String keyValue;
    
    /**
     * 是否只是key-value格式的数据
     */
    private int iskey;
    
    /**
     * 备注
     */
    private String remark;
}
