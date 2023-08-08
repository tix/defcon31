package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author charles
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@Entity
@Table(name = "t_offer_step_assign")
public class StepAssignModel extends EntityBase {

    @Column(name = "step_id")
    private String stepId;

    @Column(name = "offer_id")
    private String offerId;
}
