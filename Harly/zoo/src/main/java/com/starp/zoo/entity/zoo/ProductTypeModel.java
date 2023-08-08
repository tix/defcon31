package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/7.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_product_type")
public class ProductTypeModel extends EntityBase {
    
    @Column(name = "product_key")
    private String productKey;
    
    @Column(name = "product_value")
    private String productValue;
}
