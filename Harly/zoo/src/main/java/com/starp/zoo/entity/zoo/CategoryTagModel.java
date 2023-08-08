package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;



/**
 * @author Charles
 * @date 2019/3/6
 * @description :
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_category_tag",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_categoryId_tagId",columnNames = {"category_id", "tag_id", "type"})
        })
public class CategoryTagModel extends EntityBase {

    /**
     *  1: 为app, 2: 为线下
     */
    @Column(columnDefinition = "int(1) NOT NULL")
    private int type;

    @Column(name = "category_id", columnDefinition = "varchar(36) NOT NULL")
    private String categoryId;

    @Column(name = "tag_id", columnDefinition = "varchar(36) NOT NULL")
    private String tagId;
}
