package com.starp.zoo.util;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author Charles
 * @date 2019/8/5
 * @description :
 */
public class ObjectUtil {

    public static String serializeToStr(Object object) {
        // 缓冲区会随着数据的不断写入而自动增长。可使用 toByteArray() 和 toString() 获取数据。
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 专用于java对象序列化，将对象进行序列化
        ObjectOutputStream objectOutputStream = null;
        String serStr = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            serStr = byteArrayOutputStream.toString("UTF-8");
            serStr = URLEncoder.encode(serStr, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return serStr;
    }

    public static Object deserializeFromStr(String serStr) {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            String desetStr = URLDecoder.decode(serStr, "UTF-8");
            byteArrayInputStream = new ByteArrayInputStream(desetStr.getBytes("UTF-8"));
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return  objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
