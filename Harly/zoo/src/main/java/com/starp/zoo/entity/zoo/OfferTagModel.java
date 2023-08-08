package com.starp.zoo.entity.zoo;

import com.alibaba.fastjson.annotation.JSONField;
import com.starp.zoo.entity.common.EntityBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

/***
 *
 * @Author David
 * @Date 10:46 2019/3/1
 * @param
 * @return
 **/

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name="t_offer_tag", uniqueConstraints = {
        @UniqueConstraint(name = "unique_offerId_tagId", columnNames = {"offer_id", "tag_id"})
})
public class OfferTagModel extends EntityBase{

    public OfferTagModel() {
    }

    public OfferTagModel(String offerId, String tagId) {
        this.offerId = offerId;
        this.tagId = tagId;
    }

    @ManyToOne (targetEntity = TagModel.class, cascade = { CascadeType.DETACH })
    @JoinColumn(name = "tag_id", referencedColumnName = "identification", nullable = true, insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private TagModel tagModel;


    @Column(name = "offer_id")
    private String offerId;



    @Column(name="tag_id")
    private String tagId;

}
