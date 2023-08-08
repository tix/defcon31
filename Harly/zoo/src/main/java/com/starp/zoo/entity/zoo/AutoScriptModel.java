package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @Author David
 * @Date 18:10 2018/12/18
 * @param
 * @return
 **/
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name="t_auto_script")
public class AutoScriptModel extends EntityBase{

    @Column(columnDefinition = "varchar(50)")
    private String name;

    @Column(columnDefinition = "text")
    private String regular;

    @Column
    private String script;

    @Column(name = "event_type", columnDefinition = "int(1)")
    private Integer eventType;


    /**
     * 类型1 : GP-手动分配 类型2： 线下合作商-手动分配
     */
    @Column(name = "type", columnDefinition = "int(1)")
    private Integer type;
}
