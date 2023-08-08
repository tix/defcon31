package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 
 * @Author David
 * @Date 18:09 2018/12/18
 * @param  
 * @return 
 **/
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name="t_app_offer")
public class OfferTaskModel extends EntityBase implements Cloneable{


    @Column(columnDefinition = "varchar(10)")
    private String country;

    @Column(columnDefinition = "varchar(20)")
    private String operator;

    @Column(name = "app_id", columnDefinition = "text")
    private String appId;

    @Column(name = "offer_id", columnDefinition = "text")
    private String offerId;

    @Column(name="offer_level",columnDefinition = "int(3) default 1")
    private int level;

    @Column(name = "app_name", columnDefinition = "varchar(50)")
    private String appName;


    /**
     * 默认 1 为开启
     */
    @Column(columnDefinition = "int(1) default 1")
    private int status;
    
    /**
     * 时间间隔 以秒为单位
     */
    @Column(name="offer_time",columnDefinition = "int(3)")
    private int offerTime;


    @Override
    public Object clone() {
        Object obj = null;
        try {
            obj =  super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
