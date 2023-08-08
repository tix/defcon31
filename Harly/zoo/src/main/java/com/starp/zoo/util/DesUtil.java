package com.starp.zoo.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

/**
 * @author Charles
 * @date 2019/8/12
 * @description :
 */
@Slf4j
public class DesUtil {

    /**
     *  设置密钥，略去  至少8位
     */
    private static final String DES_KEY = "abcdefgh";
    /**
     * 设置向量，略去  至少8位
     */
    private static final String DES_IV = "12345678";


    /**
     * 加密算法的参数接口，IvParameterSpec是它的一个实现
     */
    private AlgorithmParameterSpec iv = null;
    private SecretKey key = null;

    public DesUtil() throws Exception {
        byte[] desKey = DES_KEY.getBytes("UTF-8");
        byte[] desIV =  DES_IV.getBytes("UTF-8");
        // 设置密钥参数
        DESKeySpec keySpec = new DESKeySpec(desKey);
        // 设置向量
        iv = new IvParameterSpec(desIV);
        // 获得密钥工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 得到密钥对象
        key = keyFactory.generateSecret(keySpec);
    }

    public DesUtil(String desKey, String desIv)  {
        try {
            byte[] desKeyBt = StringUtils.isEmpty(desKey) ? DES_KEY.getBytes("UTF-8") : desKey.getBytes("UTF-8");
            byte[] desIvBt =  StringUtils.isEmpty(desIv) ? DES_IV.getBytes("UTF-8") : desIv.getBytes("UTF-8");
            // 设置密钥参数
            DESKeySpec keySpec = new DESKeySpec(desKeyBt);
            // 设置向量
            iv = new IvParameterSpec(desIvBt);
            // 获得密钥工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // 得到密钥对象
            key = keyFactory.generateSecret(keySpec);
        }catch (Exception e){
            log.info("DES KEY ERROR:{}",e.getMessage());
        }

    }

    /**
     * 加密
     * @param data 待加密的数据
     * @return 加密后的数据
     * @throws Exception
     */
    public String encode(String data) throws Exception {
        // 得到加密对象Cipher
        Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        // 设置工作模式为加密模式，给出密钥和向量
        enCipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));
        return Base64.getEncoder().encodeToString(pasByte);
    }

    /**
     * 解密
     * @param data  解密前的数据
     * @return 解密后的数据
     * @throws Exception
     */
    public String decode(String data) throws Exception {
        Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        deCipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] strBytes = Base64.getDecoder().decode(data.getBytes("UTF-8"));
        byte[] pasByte = deCipher.doFinal(strBytes);
        return new String(pasByte, "UTF-8");
    }

    public static void main(String[] args) throws Exception {
        DesUtil tools = new DesUtil("20190826", "20190826");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", "123456");
        jsonObject.put("value", "value");
        System.out.println("加密:" + tools.encode(jsonObject.toJSONString()));
        String data1 = tools.encode(jsonObject.toJSONString());
        System.out.println("解密:" + tools.decode(data1));
    }


}
