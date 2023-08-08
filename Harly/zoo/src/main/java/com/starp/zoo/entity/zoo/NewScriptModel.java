package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

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
@Table(name="t_new_auto_script")
public class NewScriptModel extends EntityBase{

    @Column(columnDefinition = "varchar(50)")
    private String name;

    @Column
    private String country;

    @Column
    private String regular;

    @Column
    private String script;

    @Column(name = "event_type", columnDefinition = "int(1)")
    private Integer eventType;

    @Column(name = "pin_front_code", columnDefinition = "varchar(10)")
    private String pinFrontCode;

    @Column(name = "pin_byte_count", columnDefinition = "int(11)")
    private int pinByteCount;

    @Column(name = "pin_input_location", columnDefinition = "varchar(255)")
    private String pinInputLocation;

    @Column(name = "pin_confirm_location", columnDefinition = "varchar(255)")
    private String pinConfirmLocation;

    @Column(name = "pin_btn_location", columnDefinition = "varchar(255)")
    private String pinBtnLocation;

    @Column
    private String shortCode;

    @Column
    private String keyword;


    @Transient
    private List<OfferModel> scriptOfferList;
}
