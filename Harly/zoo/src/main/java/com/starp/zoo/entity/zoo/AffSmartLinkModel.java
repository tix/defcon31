package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_aff_smart_link", uniqueConstraints = {
        @UniqueConstraint(name = "unique_id", columnNames = {"id"})
})
public class AffSmartLinkModel extends EntityBase {

    @Column(name = "name", columnDefinition = "varchar(100) default null")
    private String name;

    @Column(name = "id", columnDefinition = "varchar(11) NOT NULL")
    private String id;

    @Column(name = "tracking_url", columnDefinition = "varchar(500) default null")
    private String trackingUrl;

    @Column(name = "aff_id", columnDefinition = "varchar(36) default null")
    private String affId;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Transient
    private List<DeductConfigModel> deducts;

    @Transient
    private List<String> tags;
}
