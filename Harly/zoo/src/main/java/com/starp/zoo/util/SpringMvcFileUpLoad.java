package com.starp.zoo.util;

import com.starp.zoo.config.aws.AmazonUploadConfig;
import com.starp.zoo.constant.ZooConstant;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * spring mvc MultipartFile 工具类 进行文件上传   留着
 * 
 * @author Joe
 * 
 */
@Component
@Slf4j
public class SpringMvcFileUpLoad {
	
	@Autowired(required = true)
	private static AmazonUploadConfig amazonUploadConfig;
	
	@Autowired
	private S3Util s3Util;
	
	/** 取S3服务器的地址 // 10M 10485760; 20M
						// 20971520; 50M
						// 52428800; 100M
						// 104857600
	 */
	private final static long MAX_UPLOAD_SIZE = 104857600;
	
	public final static String CODE_TEXT = "code";
	public final static String MESSAGE_TEXT = "message";
	public final static String CODE_BAD_REQUEST = "400";

	/**
	 * 主要是上传图片，音频文件，视频文件，上传后名字修改为一个uuid.后缀名
	 * 
	 * @param file  需要上传的文件对象
	 * @param filePath 文件临时在服务器上的目录
	 * @param fileType 文件类型（图片，音频，视频）
	 * @param otherPath 需要一些额外的目录
	 * @return
	 */
	public String fileUpload(MultipartFile file, String filePath,
                                    String fileType, String otherPath) {
		String fileUUIDName = FileUtil.getUuidFileName(file
				.getOriginalFilename());
		if ("".equals(fileUUIDName)) {
			return "The file name error!";
		}
		// 检测文件：非空，小于100M，是对应的类型
		String result = validateFile(file, fileType);
		if (!ZooConstant.SUCCESS.equalsIgnoreCase(result)) {
			return result;
		}
		// 先把文件从客户端传到服务器
		File tempFile = upLoad(filePath, file);

		// 再次调用S3上传的方法，把服务器上的文件传至S3服务器上
		Map<String, File> map = new HashMap<String, File>(1);
		// fileName 需要考虑，全局唯一，否则后期的会覆盖前面的
		map.put(fileUUIDName, tempFile);
		// http://justdownit.s3.amazonaws.com/images/example.jpg
		// images/example.jpg = filetype + key
		s3Util.uploadToS3(otherPath, map);
		// 上传S3完成，删除本地服务器的临时文件
		SpringMvcFileUpLoad.deleteFile(filePath + "\\"
				+ file.getOriginalFilename());
		

		return getS3UrlHead().trim() + "/" + otherPath + fileUUIDName;

	}

	public static List<String> fileUpload(MultipartFile file, String filePath) {

		List<String> result = null;
		// 检测文件：非空，小于100M，是对应的类型
		if (file.getSize() > MAX_UPLOAD_SIZE) {
			return result;
		}
		// 先把文件从客户端传到服务器
		upLoad(filePath, file);
		filePath = filePath + "/" + file.getOriginalFilename();
		result = FileUtil.readToList(filePath);
		deleteFile(filePath);
		return result;
	}

	/**
	 * 主要是上传图片，音频文件，视频文件，上传后名字修改为一个uuid.后缀名
	 * 
	 * @param file 需要上传的文件对象
	 * @param filePath 文件临时在服务器上的目录
	 * @param fileType 文件类型（图片，音频，视频）
	 * @param otherPath  需要一些额外的目录
	 * @return
	 */
	public String fileUpload(MultipartFile file, String filePath,
                                    String fileName, String fileType, String otherPath) {
		// 检测文件：非空，小于100M，是对应的类型
		String result = validateFile(file, fileType);
		if (!ZooConstant.SUCCESS.equalsIgnoreCase(result)) {
			return result;
		}
		// 先把文件从客户端传到服务器
		File tempFile = upLoad(filePath, file);

		// 再次调用S3上传的方法，把服务器上的文件传至S3服务器上
		Map<String, File> map = new HashMap<String, File>(1);
		// fileName 需要考虑，全局唯一，否则后期的会覆盖前面的
		map.put(fileName, tempFile);
		// http://justdownit.s3.amazonaws.com/images/example.jpg
		// images/example.jpg = filetype + key
		s3Util.uploadToS3("ContentManage/" + otherPath, map);
		// 上传S3完成，删除本地服务器的临时文件
		SpringMvcFileUpLoad.deleteFile(filePath + "\\"
				+ file.getOriginalFilename());

		return getS3UrlHead().trim() + "/" + otherPath + fileName;

	}

	/**
	 * 文件上传至服务器上
	 * 
	 * @param filePath 传入服务器的目录
	 * @param file spring的文件上传工具类
	 * @return
	 */
	public static File upLoad(String filePath, MultipartFile file) {
		// 取文件名
		String fileName = file.getOriginalFilename();
		File targetFile = null;
		if(null != fileName){
			targetFile = new File(filePath, fileName);
			if (!targetFile.exists()) {
				Boolean mkdirs = targetFile.mkdirs();
			}
			// 上传文件
			try {
				file.transferTo(targetFile);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}

		return targetFile;
	}

	/**
	 * 文件上传控制
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String validateFile(MultipartFile file, String fileType) {
		if (file == null) {
			return "上传文件是空！";
		}
		String fileName = file.getOriginalFilename();
		String type = file.getContentType();
		long size = file.getSize();
		if (size > MAX_UPLOAD_SIZE) {
			return fileName + "文件大于100M请选择其他工具！";
		}
		if (!type.startsWith(fileType)) {
			return fileName + "请检查上传文件的格式！";
		}

		return "SUCCESS";
	}

	/**
	 * 删除filePath对应的文件
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static boolean deleteFile(String filePath) {
		boolean success = Boolean.FALSE;
		File f = new File(filePath);
		if (f.exists()) {
		boolean delete =f.delete();
		if(delete){
		log.info("delete file success");
		}

			success = Boolean.TRUE;
		}
		return success;
	}
	
	public static Map<String, String> getBadRequestMap(String errorMessage) {
		Map<String, String> map = new HashMap<String, String>(2);
		map.put(CODE_TEXT, CODE_BAD_REQUEST);
		map.put(MESSAGE_TEXT, errorMessage);
		return map;
	}

	private String getS3UrlHead(){
		return "http://" + amazonUploadConfig.getBucketName() + "." + amazonUploadConfig.getAmazonDomain();
	}
}
