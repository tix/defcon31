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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


/***
 *
 * @Author David
 * @Date 10:38 2019/3/1
 * @param
 * @return
 **/

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_tag")
public class TagModel extends EntityBase{

    public TagModel() {
    }

    public TagModel(String tagName, Integer tagType) {
        this.tagName = tagName;
        this.tagType = tagType;
    }

    @Column(name = "tag_name", columnDefinition = "varchar(255)")
    private String tagName;


    /**
     * 1: stack 2: group 3: others
     */
    @Column(name = "tag_type", columnDefinition = "int(1)")
    private Integer tagType;

}
