package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.VideoInfoModel;
import com.starp.zoo.vo.PageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author curry by 2023/7/4
 */
public interface VideoInfoService {
	/**
	 * saveVideoType
	 * @param name
	 * @return void
	 * @author Curry
	 * @date 2023/7/4
	 */
	void saveVideoType(String name);

	/**
	 * getVideoTypeList
	 * @param page
	 * @param limit
	 * @return com.starp.zoo.vo.PageVO
	 * @author Curry
	 * @date 2023/7/4
	 */
	PageVO getVideoTypeList(Integer page, Integer limit);

	/**
	 * deleteVideoType
	 * @param type
	 * @return void
	 * @author Curry
	 * @date 2023/7/4
	 */
	void deleteVideoType(String type);

	/**
	 * uploadVideoInfo
	 * @param videoFile
	 * @param imageFile
	 * @param dataJson
	 * @return boolean
	 * @author Curry
	 * @date 2023/7/4
	 */
	boolean uploadVideoInfo(MultipartFile videoFile, MultipartFile imageFile, String dataJson);

	/**
	 * getVideoInfoList
	 * @param type
	 * @param page
	 * @param limit
	 * @return com.starp.zoo.vo.PageVO
	 * @author Curry
	 * @date 2023/7/5
	 */
	PageVO getVideoInfoList(String type, Integer page, Integer limit);

	/**
	 * multiDelete
	 * @param ids
	 * @return void
	 * @author Curry
	 * @date 2023/7/5
	 */
	void multiDelete(List<String> ids);

	/**
	 * save
	 * @param model
	 * @return void
	 * @author Curry
	 * @date 2023/7/5
	 */
	void saveVideoInfo(VideoInfoModel model);

	/**
	 * getVideoInfo
	 * @param id
	 * @return com.starp.zoo.entity.zoo.VideoInfoModel
	 * @author Curry
	 * @date 2023/7/5
	 */
	VideoInfoModel getVideoInfo(String id);

	/**
	 * randomGetVideoInfoList
	 * @param type
	 * @return java.util.List<com.starp.zoo.entity.zoo.VideoInfoModel>
	 * @author Curry
	 * @date 2023/7/5
	 */
	List<VideoInfoModel> randomGetVideoInfoList(String type);
}
