package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Vic on 2020/5/22
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name = "t_referer_regx")
public class RefererRegxModel extends EntityBase {

    @Column(name = "url_regx")
    private String urlRegx;

    @Column(name = "referer_url_regx")
    private String refererUrlRegx;
}
