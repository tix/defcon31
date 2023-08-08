package com.starp.zoo.config.aws;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/13.
 */
@Component
public class AmazonUploadConfig {
    
    /** 存储段(桶名) */
    @Value("${s3.bucketName}")
    private String bucketName;
    
    @Value("${s3.amazon_domain}")
    /** Amazon域 */
    private String amazonDomain;
    
    public String getBucketName() {
        return bucketName;
    }
    
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
    
    public String getAmazonDomain() {
        return amazonDomain;
    }
    
    public void setAmazonDomain(String amazonDomain) {
        this.amazonDomain = amazonDomain;
    }
}
