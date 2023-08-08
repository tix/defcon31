package com.starp.zoo.entity.zoo;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.starp.zoo.entity.common.EntityBase;

import javax.persistence.*;
import java.util.Date;

/**
 * @author david
 */
@SuppressFBWarnings("EI_EXPOSE_REP2")
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "t_app_install")
public class AppInstallModel extends EntityBase {


    /**
     * appid
     */
    @Column
    private String appName;

    /**
     * 备注
     */
    @Column
    private String description;

    /**
     * 国家
     */
    @Column
    private String country;

    /**
     * 安装数
     */
    @Column
    private int installation;

    /**
     * 时间
     */
    @SuppressFBWarnings("EI_EXPOSE_REP")
    @Column
    @Temporal(TemporalType.DATE)
    private Date dayTime = new Date();

}
