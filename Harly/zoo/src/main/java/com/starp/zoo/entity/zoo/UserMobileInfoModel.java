package com.starp.zoo.entity.zoo;


import com.starp.zoo.entity.common.EntityBase;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Charles
 * @date 2019/4/17
 * @description :
 */
@SuppressFBWarnings("EI_EXPOSE_REP2")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "t_user_mobile")
public class UserMobileInfoModel extends EntityBase{
    

    @Column(name = "device_id", columnDefinition = "varchar(50) default null")
    private String deviceId;

    @Column(name = "user_id", columnDefinition = "varchar(50) default null")
    private String userId;


    @Column
    private String mobile;

    @Column
    private String mnc;
}
