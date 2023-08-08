package com.starp.zoo.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author Bowen
 */
public final class StringEncodeUtil2 {

    private static final String SERVER_KEY = "07bfeb1a-47d6";
    private static final String LOCATION_KEY = "67890567890-r87e8wr7we8";

    public static String encodeServer(String value){
        try {
            return base64Encode(encrypt(value, SERVER_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String encodeLocation(String value){
        try {
            return base64Encode(encrypt(value, LOCATION_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decode(String value) {
        try {
            return decrypt(base64Decode(value), LOCATION_KEY);
        } catch (Exception e) {
            return "";
        }
    }

    public static String decodeServer(String value){
        try {
            return decrypt(base64Decode(value), SERVER_KEY);
        } catch (Exception e) {
            return "";
        }
    }
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    private static byte[] encrypt(String content, String strKey ) throws Exception {
        SecretKeySpec skeySpec = getKey(strKey);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(content.getBytes());
        return  encrypted;
    }

    @SuppressFBWarnings({"DM_DEFAULT_ENCODING", "DM_DEFAULT_ENCODING"})
    private static String decrypt(byte[] content, String key ) throws Exception {
        SecretKeySpec skeySpec = getKey(key);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] original = cipher.doFinal(content);
        String originalString = new String(original);
        return originalString;
    }

    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    private static SecretKeySpec getKey(String strKey) throws Exception {
        byte[] arrBTmp = strKey.getBytes();
        byte[] arrB = new byte[16];
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        SecretKeySpec skeySpec = new SecretKeySpec(arrB, "AES");
        return skeySpec;
    }

    private static String base64Encode(byte[] bytes){
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static byte[] base64Decode(String base64Code) throws Exception {
        return base64Code.isEmpty() ? null : Base64.getDecoder().decode(base64Code);
    }

}
