package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Controller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/***
 *
 * @Author David
 * @Date 14:48 2019/2/25
 * @param
 * @return
 **/

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_gp_report")
public class GpReportModel extends EntityBase{

    @Column
    private String name;

    @Column
    private String link;

    /**
     * 0.未上线1.在线 2.下线
     */
    @Column
    private String online;

    @Column
    private String email;

    @Column
    private String ccEmail;

    /**
     * 1. 开启， 2.暂停
     */
    @Column
    private Integer status =1;

    @Column
    private String customer;

    @Column
    private String updateDate;

    @Column
    private String offlineDate;


}
