package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.config.AndroidGroupGetDesConfig;
import com.starp.zoo.entity.zoo.HttpLoggingModel;
import com.starp.zoo.entity.zoo.HttpLoggingRecordModel;
import com.starp.zoo.service.IHttpLoggingRecordService;
import com.starp.zoo.service.IHttpLoggingService;
import com.starp.zoo.util.AesUtil;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.util.DesUtil;
import com.starp.zoo.vo.HttpLoggingVO;
import com.starp.zoo.vo.SingleStepVO;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * httpLogingServiceImpl.
 *
 * @author magic
 * @date 2021/7/26
 */
@Service
@Slf4j
public class HttpLoggingServiceImpl implements IHttpLoggingService {

    @Value("${httplogging.s3.bucket}")
    private String bucket;

    @Value("${httplogging.s3.path}")
    private String path;

    @Value("${httplogging.localPath}")
    private String localPath;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    IHttpLoggingRecordService httpLoggingRecordService;

    @SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "DM_DEFAULT_ENCODING"})
    @Override
    @Async
    public void saveLoggingFileToS3(HttpLoggingModel httpLoggingModel, DesUtil desUtil, String servletPath, String response) {
        String dir = DateUtil.formatDay(new Date()) + ZooConstant.SLASH + httpLoggingModel.getCarrier() + ZooConstant.SLASH + httpLoggingModel.getAppId() + ZooConstant.SLASH + httpLoggingModel.getOfferId() + ZooConstant.SLASH + httpLoggingModel.getStepName() + ZooConstant.SLASH + httpLoggingModel.getUserId() + "_" + httpLoggingModel.getPid();
        String localP = localPath + dir;
        File file = new File(localP);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(new File(localP + ZooConstant.SLASH + httpLoggingModel.getStepId() + ZooConstant.LOG));
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"servletPath\":" + "\"" + servletPath + "\",");
            sb.append("\"carrier\":" + "\"" + httpLoggingModel.getCarrier() + "\",");
            sb.append("\"url\":" + "\"" + httpLoggingModel.getUrl() + "\",");
            sb.append("\"realUrl\":" + "\"" + httpLoggingModel.getRealUrl() + "\",");
            sb.append("\"method\":" + "\"" + httpLoggingModel.getMethod() + "\",");
            if (!StringUtils.isEmpty(httpLoggingModel.getHeaders()) && httpLoggingModel.getHeaders().contains(ZooConstant.LEFT_BRACKETS)) {
                sb.append("\"headers\":" + httpLoggingModel.getHeaders() + ",");
            } else {
                sb.append("\"headers\":" + "\"" + httpLoggingModel.getHeaders() + "\",");
            }
            if (!StringUtils.isEmpty(httpLoggingModel.getFormData()) && httpLoggingModel.getFormData().contains(ZooConstant.LEFT_BRACKETS)) {
                sb.append("\"formData\":" + httpLoggingModel.getFormData() + ",");
            } else {
                sb.append("\"formData\":" + "\"" + httpLoggingModel.getFormData() + "\",");
            }
            sb.append("\"body\":" + desUtil.decode(httpLoggingModel.getBody()) + ",");
            log.info("response:" + response);
            if (!StringUtils.isEmpty(response) && response.contains(ZooConstant.LEFT_BRACKETS)) {
                sb.append("\"response\":" + response);
            } else {
                sb.append("\"response\":" + "\"\"");
            }
            sb.append("\r}");
            JSONObject object = JSONObject.parseObject(sb.toString(), Feature.OrderedField);
            String pretty = JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat);
            stream.write(pretty.getBytes(StandardCharsets.UTF_8));
            file = new File(localP + ZooConstant.SLASH + httpLoggingModel.getStepId() + ZooConstant.LOG);
            s3Client.putObject(new PutObjectRequest(bucket + path + dir, httpLoggingModel.getStepId() + ZooConstant.LOG, file).withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            log.error("logging upload S3 exception:{}", e.getStackTrace());
            return;
        } finally {
            deleteDir(localPath + DateUtil.formatDay(new Date()));
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * key 为:zoo/appEvent/Axe/2021-09-27/ZA_MTN/26bc16b4-2a7d-487d-8062-507f98bada1c/12a5ca7a-775a-48dc-a2e9-f6e8a9d75cfa/步骤name/49470f02-7f08-4cd1-ae3d-93f4c0305060_2cb7d55d-fa4c-4597-83a5-049b00071c3b/0.log
     *
     * @param operator
     * @param appName
     * @param offerId
     * @param userId
     * @param pid
     * @param stepNumber
     * @param begin
     * @param end
     * @param stepName
     * @param page
     * @param limit
     * @return
     */
    @Override
    public List<HttpLoggingVO> findFormS3(String operator, String appName, String offerId, String userId, String pid, String stepNumber, Long begin, Long end, String stepName, Integer page, Integer limit) {
        List<HttpLoggingRecordModel> recordList = httpLoggingRecordService.findList(operator, appName, offerId, userId, pid, stepNumber, begin, end, stepName, page, limit);
        List<HttpLoggingVO> result = new ArrayList<>();
        String dirPath = path.substring(1, path.length());
        ArrayList<HttpLoggingRecordModel> distinctOffer = recordList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getOfferId()))), ArrayList::new));
        for (HttpLoggingRecordModel modelOffer : distinctOffer) {
            String tempOfferId = modelOffer.getOfferId();
            //相同的offerId集合
            List<HttpLoggingRecordModel> sameOfferList = recordList.stream().filter(o -> tempOfferId.equals(o.getOfferId())).collect(Collectors.toList());
            //不同的stepName集合
            ArrayList<HttpLoggingRecordModel> distinctStepNameList = sameOfferList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getStepName()))), ArrayList::new));
            for (HttpLoggingRecordModel modelStepName : distinctStepNameList) {
                String tempStepName = modelStepName.getStepName();
                //相同的StepName集合
                List<HttpLoggingRecordModel> sameStepName = sameOfferList.stream().filter(o -> tempStepName.equals(o.getStepName())).collect(Collectors.toList());
                //不同的userId集合
                ArrayList<HttpLoggingRecordModel> distinctUser = sameStepName.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getUserId()))), ArrayList::new));
                for (HttpLoggingRecordModel modelUser : distinctUser) {
                    String tempUserId = modelUser.getUserId();
                    //相同的userId集合
                    List<HttpLoggingRecordModel> sameUserList = sameStepName.stream().filter(o -> tempUserId.equals(o.getUserId())).collect(Collectors.toList());
                    //不同的pid集合
                    ArrayList<HttpLoggingRecordModel> distinctPid = sameUserList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getPid()))), ArrayList::new));
                    for (HttpLoggingRecordModel modelPid : distinctPid) {
                        String tempPid = modelPid.getPid();
                        HttpLoggingVO httpLogging = new HttpLoggingVO();
                        httpLogging.setCreateTime(DateUtil.formatyyyyMMddHHmmss(modelPid.getCreateTime()));
                        httpLogging.setCarrier(modelPid.getCarrier());
                        httpLogging.setAppId(modelPid.getAppId());
                        httpLogging.setOfferId(modelPid.getOfferId());
                        httpLogging.setUserId(modelPid.getUserId());
                        httpLogging.setRecordDate(modelPid.getRecordDate());
                        httpLogging.setPid(tempPid);
                        httpLogging.setStepName(modelPid.getStepName());
                        String key = dirPath + modelPid.getRecordDate() + ZooConstant.SLASH + operator + ZooConstant.SLASH + appName + ZooConstant.SLASH + modelPid.getOfferId() + ZooConstant.SLASH + modelPid.getStepName() + ZooConstant.SLASH + modelPid.getUserId() + "_" + modelPid.getPid();
                        httpLogging.setKey(key);
                        List<SingleStepVO> singleStepList = new ArrayList<>();
                        //相同的pid集合
                        List<HttpLoggingRecordModel> samePid = sameUserList.stream().filter(o -> tempPid.equals(o.getPid())).collect(Collectors.toList()).stream().sorted(
                                Comparator.comparing(HttpLoggingRecordModel::getStepNumber)).collect(Collectors.toList());
                        for (HttpLoggingRecordModel samePidModel : samePid) {
                            SingleStepVO singleStepVO = new SingleStepVO();
                            singleStepVO.setStepNumber(Integer.parseInt(samePidModel.getStepNumber()));
                            singleStepList.add(singleStepVO);
                        }
                        httpLogging.setSingleStepList(singleStepList);
                        result.add(httpLogging);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<SingleStepVO> findDetail(String operator, String appName, String offerId, String userId, String pid, String recordDate, String stepNumber, String stepName) {
        String dirPath = path.substring(1, path.length()) + recordDate + ZooConstant.SLASH + operator + ZooConstant.SLASH + appName + ZooConstant.SLASH + offerId + ZooConstant.SLASH + stepName + ZooConstant.SLASH + userId + "_" + pid;
        if (!StringUtils.isEmpty(stepNumber)) {
            dirPath += ZooConstant.SLASH + stepNumber + ZooConstant.LOG;
        }
        ListObjectsV2Result objectsV2 = s3Client.listObjectsV2(bucket, dirPath);
        int size = objectsV2.getObjectSummaries().size();
        List<SingleStepVO> singleStepList = new ArrayList<>();
        if (size > 0) {
            for (int i = 0; i < objectsV2.getObjectSummaries().size(); i++) {
                SingleStepVO singleStepVO = new SingleStepVO();
                String key = objectsV2.getObjectSummaries().get(i).getKey();
                String[] split = key.split("/");
                if (split.length == NumberEnum.TEN.getNum()) {
                    singleStepVO.setStepNumber(Integer.parseInt(split[9].split("\\.")[0]));
                    readDataFromS3(key, singleStepVO);
                    singleStepList.add(singleStepVO);
                } else {
                    continue;
                }
            }
        }
        return singleStepList;
    }


    /**
     * 从s3上读文件数据.
     *
     * @param key
     * @param singleStepVO
     * @return
     */
    @SuppressFBWarnings({"RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE", "DM_DEFAULT_ENCODING"})
    public void readDataFromS3(String key, SingleStepVO singleStepVO) {
        StringBuilder data = new StringBuilder();
        S3Object object = s3Client.getObject(new GetObjectRequest(bucket, key));
        InputStream inputStream = object.getObjectContent();
        BufferedReader br = null;
        br = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = "";
            while ((line = br.readLine()) != null) {
                data.append(line).append("\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JSONObject jsonObject = JSONObject.parseObject(data.toString(), Feature.OrderedField);
        String body = JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        singleStepVO.setBody(body);
        String decodeBody = null;
        try {
            decodeBody = JSON.toJSONString(JSONObject.parseObject(jsonObject.getString("body"), Feature.OrderedField), SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat);
        } catch (Exception e) {
            decodeBody = JSON.toJSONString(jsonObject.getJSONArray("body").toJSONString(), SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        }
        String decodeResponse = null;
        try {
            decodeResponse = JSON.toJSONString(JSONObject.parseObject(jsonObject.getString("response"), Feature.OrderedField), SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat);
        } catch (Exception e) {
            decodeResponse = JSON.toJSONString(jsonObject.getJSONArray("response").toJSONString(), SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        }
        String encodeKey = AndroidGroupGetDesConfig.URI_MAP.get(jsonObject.getString("servletPath"));
        if (!StringUtils.isEmpty(encodeKey)) {
            DesUtil desUtil = AndroidGroupGetDesConfig.getDesUtil(encodeKey);
            if (desUtil != null) {
                try {
                    String encodeBody = desUtil.encode(decodeBody);
                    String encodeResponse = desUtil.encode(decodeResponse);
                    singleStepVO.setEncodeBody(encodeBody);
                    singleStepVO.setEncodeResponse(encodeResponse);
                } catch (Exception e) {
                    log.error("encode error,{}", e.getStackTrace());
                }
            }
        }
    }

    /**
     * delete directory and file
     *
     * @param dirPath
     */
    @SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"})
    public void deleteDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteDir(files[i].getAbsolutePath());
            }
            file.delete();
        }
    }

    @Override
    public void deleteLoggingFile() {
        try {
            long time = System.currentTimeMillis() + 8 * NumberEnum.ONE_HOUR_MILLISECONDS.getNum();
            String day = DateUtil.incrDay(DateUtil.formatDay(new Date(time)), -7);
            String prefix = path.substring(1, path.length()) + day;
            int preLen = prefix.length();
            ObjectListing objectListing = s3Client.listObjects(bucket, prefix);
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                String key = objectSummary.getKey();
                int len = key.length();
                if (len < preLen) {
                    continue;
                }
                int i;
                for (i = 0; i < preLen; i++) {
                    if (key.charAt(i) != prefix.charAt(i)) {
                        break;
                    }
                }
                if (i < preLen) {
                    continue;
                }
                s3Client.deleteObject(bucket, key);
            }
        } catch (SdkClientException e) {
            log.error("delete logging error:{}", e.getStackTrace());
        }
    }

    @SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "DM_DEFAULT_ENCODING"})
    @Override
    @Async
    public void saveLoggingFileToS3New(HttpLoggingModel httpLoggingModel, AesUtil desUtil, String servletPath, String response, int encodeType) {
        String dir = DateUtil.formatDay(new Date()) + ZooConstant.SLASH + httpLoggingModel.getCarrier() + ZooConstant.SLASH + httpLoggingModel.getAppId() + ZooConstant.SLASH + httpLoggingModel.getOfferId() + ZooConstant.SLASH + httpLoggingModel.getStepName() + ZooConstant.SLASH + httpLoggingModel.getUserId() + "_" + httpLoggingModel.getPid();
        String localP = localPath + dir;
        File file = new File(localP);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(new File(localP + ZooConstant.SLASH + httpLoggingModel.getStepId() + ZooConstant.LOG));
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"servletPath\":" + "\"" + servletPath + "\",");
            sb.append("\"carrier\":" + "\"" + httpLoggingModel.getCarrier() + "\",");
            sb.append("\"url\":" + "\"" + httpLoggingModel.getUrl() + "\",");
            sb.append("\"realUrl\":" + "\"" + httpLoggingModel.getRealUrl() + "\",");
            sb.append("\"method\":" + "\"" + httpLoggingModel.getMethod() + "\",");
            if (!StringUtils.isEmpty(httpLoggingModel.getHeaders()) && httpLoggingModel.getHeaders().contains(ZooConstant.LEFT_BRACKETS)) {
                sb.append("\"headers\":" + httpLoggingModel.getHeaders() + ",");
            } else {
                sb.append("\"headers\":" + "\"" + httpLoggingModel.getHeaders() + "\",");
            }
            if (!StringUtils.isEmpty(httpLoggingModel.getFormData()) && httpLoggingModel.getFormData().contains(ZooConstant.LEFT_BRACKETS)) {
                sb.append("\"formData\":" + httpLoggingModel.getFormData() + ",");
            } else {
                sb.append("\"formData\":" + "\"" + httpLoggingModel.getFormData() + "\",");
            }
            sb.append("\"body\":" + new String(desUtil.decode(httpLoggingModel.getBody().getBytes(StandardCharsets.UTF_8), encodeType)) + ",");
            log.info("response:" + response);
            if (!StringUtils.isEmpty(response) && response.contains(ZooConstant.LEFT_BRACKETS)) {
                sb.append("\"response\":" + response);
            } else {
                sb.append("\"response\":" + "\"\"");
            }
            sb.append("\r}");
            JSONObject object = JSONObject.parseObject(sb.toString(), Feature.OrderedField);
            String pretty = JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat);
            stream.write(pretty.getBytes(StandardCharsets.UTF_8));
            file = new File(localP + ZooConstant.SLASH + httpLoggingModel.getStepId() + ZooConstant.LOG);
            s3Client.putObject(new PutObjectRequest(bucket + path + dir, httpLoggingModel.getStepId() + ZooConstant.LOG, file).withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            log.error("logging upload S3 exception:{}", e.getStackTrace());
            return;
        } finally {
            deleteDir(localPath + DateUtil.formatDay(new Date()));
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
