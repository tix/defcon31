package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author  Charles, DATE: 2018/9/29.
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_aff_click_info",indexes={
        @Index(name="click_id_index",columnList="click_id"),
        @Index(name="offer_id_index",columnList="offer_id"),
        @Index(name="aff_name_index",columnList="aff_name"),
        @Index(name="callback_type_index",columnList="callback_type")
})
public class AffClickInfoModel extends EntityBase {

    @Column
    private String ip;

    @Column(name = "click_id")
    private String clickId;

    @Column(name = "resource_type", columnDefinition = "int(1)")
    private int resourceType;

    @Column(name = "resource_id")
    private String resourceId;

    @Column(name = "resource_name")
    private String resourceName;

    @Column(name = "offer_id")
    private String offerId;

    @Column(name = "offer_name")
    private String offerName;

    @Column(name = "aff_name")
    private String affName;

    @Column(name = "user_agent")
    private String userAgent;

    /**
     * 回调类型
     *  1 支持透传
     *  2 不支持透传但有clickId
     *  3 不支持透传且无法区分（例如 IE）
     */
    @Column(name = "callback_type", columnDefinition = "int(1) default 1")
    private Integer callbackType;

    @Column(name = "package_name")
    private String packageName;

    @Column
    private String gaid;
}
