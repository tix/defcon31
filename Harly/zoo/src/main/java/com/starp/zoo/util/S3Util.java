package com.starp.zoo.util;

import com.alibaba.fastjson.JSON;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.starp.zoo.config.aws.AmazonUploadConfig;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.sun.org.apache.bcel.internal.generic.NEW;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Amazon S3云存储工具类   留着
 *
 * @author John 2014-04-21
 *
 */
@Component
@Slf4j
public class S3Util {

	/**
	 * 上传的目录
	 */
	private static final String UPLOAD_PATH = "ContentManage";

	@Autowired(required = true)
	private AmazonS3 s3Client;

	@Autowired(required = true)
	private AmazonUploadConfig amazonUploadConfig;

    /** 存储段(桶名) */
    @Value("${s3.bucketName}")
    private String bucket;

	/**
	 * 视频内容上传桶名
	 */
	private static final String VIDEO_BUCKET_NAME = "maincontent";

	private static final String SUFFIX_BUCKET_NAME = "maincontent.s3.ap-southeast-1.amazonaws.com";


	/**
	 * 上传到S3服务器
	 *
	 * @param otherPath
	 * 				参考FileType枚举，上传的内容属于什么类型，key的组成部分
	 * @param files
	 * 				上传的文件集合<br>
	 *
	 * s3 key:与map的key意义相同，唯一对应一个对象。组成：fileType + map.key<br>
	 * 例如：key的名字为images/example.jpg,则访问该图片地址为http://justdownit.s3.amazonaws.com/images/example.jpg<br>
	 *       key的名字为doc/example,访问该对象地址为http://justdownit.s3.amazonaws.com/doc/example
	 */
	public void uploadToS3(String otherPath, Map<String, File> files) {
		if (files != null && files.size() > 0) {
			File file = null;
			Iterator<Map.Entry<String,File>> entryIterator = files.entrySet().iterator();
			while(entryIterator.hasNext()){
				Map.Entry<String,File> entry = entryIterator.next();
				file = entry.getValue();
				String key = entry.getKey();
				AmazonS3 aa = s3Client;
				// 上传到S3服务器，同时赋予该Object一个public read权限，以便通过URL直接访问
				s3Client.putObject(new PutObjectRequest(amazonUploadConfig.getBucketName(), otherPath + key, file).withCannedAcl(CannedAccessControlList.PublicRead));
			}
//			for (String key : files.keySet()) {
//				file = files.get(key);
//				AmazonS3 aa = s3Client;
//				// 上传到S3服务器，同时赋予该Object一个public read权限，以便通过URL直接访问
//				s3Client.putObject(new PutObjectRequest(amazonUploadConfig.getBucketName(), otherPath + key, file).withCannedAcl(CannedAccessControlList.PublicRead));
//			}
		}
	}

	@Timed
	public String uploadToS3(String filePath, String filename, String content){
		String savedUrl = "";
		try (InputStream inputStream = IOUtils.toInputStream(content, StandardCharsets.UTF_8.name())){
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(inputStream.available());
			String bucketName = amazonUploadConfig.getBucketName();
			String key = filePath + "/" + filename;
			s3Client.putObject(new PutObjectRequest(bucketName, key, inputStream, metadata).withCannedAcl(CannedAccessControlList.PublicRead));
			savedUrl = "http://www.flygemobi.com/" + key;
		}catch (IOException e) {
			log.error("uploadToS3 error", e);
		}
		return savedUrl;
	}

	/**
	 *
	 * @Title: uploadToS3
	 * @Description: 上传文件到S3
	 * @param  key 文件路径
	 * @return String
	 * @throws
	 */
	public String uploadToS3(String key, InputStream stream) {
		// 上传到S3服务器，同时赋予该Object一个public read权限，以便通过URL直接访问
		PutObjectResult result = s3Client.putObject(new PutObjectRequest(amazonUploadConfig.getBucketName(), key, stream, new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead));
		log.info("upload to s3 result:{}", JSON.toJSONString(result));
		String downloadPath = "http://justdownit.s3.amazonaws.com"+"/"+key;
		return downloadPath;
	}

	/**
	 * 根据key获取对象
	 *
	 * @param key
	 * @return
	 */
	public S3Object getS3ObjectByKey(String key) {
		return s3Client.getObject(new GetObjectRequest(amazonUploadConfig.getBucketName(), key));
	}

	public void deleteS3ObjectByKey(String key){
		s3Client.deleteObject(new DeleteObjectRequest(amazonUploadConfig.getBucketName(), key));
	}

	/**
	 * 获取存储段(桶)下指定key前缀的所有object
	 *
	 * @param prefix
	 * @return
	 */
	public Map<String, S3Object> getObjectByPrefix(String prefix) {
		if (prefix == null || "".equals(prefix)) {
			return null;
		}

		Map<String, S3Object> s3Objects = new HashMap<>(1);

		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(amazonUploadConfig.getBucketName()).withPrefix(prefix);
		ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
		for (S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries()) {
			s3Objects.put(s3ObjectSummary.getKey(), s3Client.getObject(new GetObjectRequest(amazonUploadConfig.getBucketName(), s3ObjectSummary.getKey())));
		}
		return s3Objects;
	}

	/**
	 * 获取存储段(桶)下指定key前缀的图片集合URL地址信息
	 *
	 * @param prefix
	 * @return 访问地址
	 */
	public List<String> getImagesByPrefix(String prefix) {
		if (prefix == null || "".equals(prefix)) {
			return null;
		}
		List<String> imagesUrls = new ArrayList<>();
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(amazonUploadConfig.getBucketName()).withPrefix(prefix);
		ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
		for (S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries()) {
			String imageUrl = "http://" + amazonUploadConfig.getBucketName() + "." + amazonUploadConfig.getAmazonDomain() + "/" + s3ObjectSummary.getKey();
			imagesUrls.add(imageUrl);
		}
		return imagesUrls;
	}


	/**
	 * 下载文件
	 * @param bucketName
	 * @param key
	 * @throws Exception
	 */
	@SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "DM_DEFAULT_ENCODING", "SBSC_USE_STRINGBUFFER_CONCATENATION", "OS_OPEN_STREAM", "OS_OPEN_STREAM", "RE_POSSIBLE_UNINTENDED_PATTERN"})
	@Cacheable(value = "sdk", key = "#path")
	public byte[] download(String bucketName, String key, String path) throws Exception {
		if (key == null || "".equals(key)) {
			return null;
		}
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, key));
		InputStream input = null;
        byte[] data = null;
		try {
			//获取文件流
			input = s3Object.getObjectContent();
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while( (len=input.read(buffer)) != -1 ){
				outStream.write(buffer, 0, len);
			}
			data = outStream.toByteArray();
			input.close();
			outStream.close();
			return data;
		} catch (IOException e) {
			log.error(" download error file:{} ERROR:{}", s3Object.getKey(), e.getMessage(), e);
			throw e;
		}finally {
			try {
				if(input != null){
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	@CacheEvict(value = "sdk", key = "#path")
	public void deleteEache(String path) {
		log.info("ZOO PULL S3 SDK :{}",path);
	}

	/**
	 * 上传文件到S3
	 * @param bucketName
	 * @param key
	 * @param fileBytes
	 * @return void
	 * @author Curry
	 * @date 2023/4/23
	 */
	public void uploadFileToS3(String bucketName, String key, InputStream fileBytes) {
		ObjectMetadata metadata = new ObjectMetadata();
		try {
			metadata.setContentLength(fileBytes.available());
			s3Client.putObject(new PutObjectRequest(bucketName, key, fileBytes, metadata).withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (Exception e) {
			log.error("uploadFileToS3_error" ,e.getMessage());
		}
	}

	/**
	 * 上传视频、图片内容到S3
	 * @param uploadFile
	 * @param fileName
	 * @param path
	 * @return java.lang.String
	 * @author Curry
	 * @date 2023/7/4
	 */
	public String uploadVideoFileToS3(File uploadFile, String fileName, String path) {
		try {
			String bucketPath = VIDEO_BUCKET_NAME + path;
			s3Client.putObject(new PutObjectRequest(bucketPath, fileName, uploadFile)
					.withCannedAcl(CannedAccessControlList.PublicRead));
			GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(VIDEO_BUCKET_NAME, fileName);
			URL url = s3Client.generatePresignedUrl(urlRequest);
			return url.toString();
		} catch (Exception e) {
			log.error("uploadVideoFileToS3 error:{}", e.getMessage());
		}
		return null;
	}

	/**
	 * 上传视频、图片内容到S3
	 * @param file
	 * @param key
	 * @return java.lang.String
	 * @author Curry
	 * @date 2023/7/4
	 */
	public String uploadFileToS3(MultipartFile file, String key) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		try {
			// 创建上传请求
			ObjectMetadata metadata = new ObjectMetadata();
			// metadata.setContentType(file.getContentType()) 这里设置了 ContentType 访问AWS URL 就会显示图片,而不是直接下载。
			// 因为他不是application/octet-stream流的形式，不设置就会流式下载
			metadata.setContentLength(file.getSize());

			// 将文件上传到 S3 (并赋予访问权限
			PutObjectRequest request = new PutObjectRequest(VIDEO_BUCKET_NAME, key, file.getInputStream(), metadata).withCannedAcl(CannedAccessControlList.PublicRead);
			s3Client.putObject(request);

			// 生成文件的访问 URL
			return ZooConstant.HTTP + SUFFIX_BUCKET_NAME + ZooConstant.SLASH + key;
		} catch (Exception e) {
			log.error("uploadFileToS3 error:{}", e.getMessage());
		}
		return null;
	}

	/**
	 * 从S3下载文件
	 * @param bucketName
	 * @param key
	 * @return byte[]
	 * @author Curry
	 * @date 2023/4/23
	 */
	public byte[] downloadFileToS3(String bucketName, String key) {
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, key));
			InputStream inputStream = s3Object.getObjectContent();
			byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] buff = new byte[NumberEnum.ONE_HUNDRED.getNum()];
			int rc = 0;
			while ((rc = inputStream.read(buff, 0, NumberEnum.ONE_HUNDRED.getNum())) > 0) {
				byteArrayOutputStream.write(buff, 0, rc);
			}
		} catch (IOException e) {
			log.error("downloadFileToS3_error:{}", e.getMessage());
		}
		return byteArrayOutputStream.toByteArray();
	}
}
