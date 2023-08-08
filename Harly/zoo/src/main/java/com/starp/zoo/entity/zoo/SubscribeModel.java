package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.UUID;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/9.
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_subscribe",
        uniqueConstraints = {@UniqueConstraint(name = "unique_transaction_id", columnNames = "transaction_id")})
public class SubscribeModel extends EntityBase {
    
    @Column(name = "offer_id", columnDefinition = "varchar(36)")
    private String offerId;
    
    @Column(columnDefinition = "varchar(100)")
    private String ip;
    
    @Column(name = "user_id", columnDefinition = "varchar(36)")
    private String userId;
    
    @Column(columnDefinition = "varchar(10)")
    private String status;
    
    @Column(columnDefinition = "varchar(10)")
    private String type;
    
    @Column(name = "user_agent", columnDefinition = "varchar(255)")
    private String userAgent;
    
    @Column(name = "package_name", columnDefinition = "varchar(100)")
    private String packageName;
    
    @Column(name = "transaction_id", columnDefinition = "varchar(255)")
    private String transactionId = UUID.randomUUID().toString();

    @Column(name = "click_id")
    private String clickId;

    @Column
    private String msisdn;
}
