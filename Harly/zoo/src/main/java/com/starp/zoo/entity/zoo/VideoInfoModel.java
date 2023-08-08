package com.starp.zoo.entity.zoo;

import com.starp.zoo.entity.common.EntityBase;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 苹果所需视频信息类
 * @author curry by 2023/7/4
 */
@Data
@Entity
@Table(name = "t_video_info")
public class VideoInfoModel extends EntityBase {

	@Column(name = "title", columnDefinition = "varchar(100)")
	private String title;

	/**
	 * 'describe' 是一个 MySQL 保留关键字，用于描述表结构，因此在使用 'describe' 作为字段名时可能会导致语法错误
	 */
	@Column(name = "description", columnDefinition = "varchar(255)")
	private String description;

	/**
	 * 视频类别
	 */
	@Column(name = "type", columnDefinition = "varchar(30)")
	private String type;

	@Column(name = "video_url", columnDefinition = "varchar(255)")
	private String videoUrl;

	@Column(name = "image_url", columnDefinition = "varchar(255)")
	private String imageUrl;

	public VideoInfoModel() {
	}

	public VideoInfoModel(String title, String description, String type, String videoUrl, String imageUrl) {
		this.title = title;
		this.description = description;
		this.type = type;
		this.videoUrl = videoUrl;
		this.imageUrl = imageUrl;
	}
}
