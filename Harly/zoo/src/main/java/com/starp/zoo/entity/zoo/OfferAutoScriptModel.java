package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/9.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_offer_auto_script")
public class OfferAutoScriptModel extends EntityBase implements Cloneable {

    public OfferAutoScriptModel() {}

    public OfferAutoScriptModel(String offerId, String autoScriptId, int sortIndex) {
        this.offerId = offerId;
        this.autoScriptId = autoScriptId;
        this.sortIndex = sortIndex;
    }

    @Column(name = "offer_id", columnDefinition = "varchar(36)")
    private String offerId;
    
    @Column(name = "auto_script_id", columnDefinition = "varchar(36)")
    private String autoScriptId;

    @Column(name = "sort_index", columnDefinition = "int(11) default 0")
    private int sortIndex;
    
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
