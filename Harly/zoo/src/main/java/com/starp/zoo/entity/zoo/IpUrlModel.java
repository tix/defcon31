package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 保存ip和url
 * @author curry by 2023/7/24
 */
@Entity
@Data
@Table(name = "t_ip_url")
public class IpUrlModel extends EntityBase {

	@Column(name = "ip")
	private String ip;

	@Column(name = "url")
	private String url;

	public IpUrlModel() {
	}

	public IpUrlModel(String ip, String url) {
		this.ip = ip;
		this.url = url;
	}
}
