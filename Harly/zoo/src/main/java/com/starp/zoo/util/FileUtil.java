package com.starp.zoo.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 测试向文件中写文件
 *
 * @author rey
 *
 */
@Slf4j
public class FileUtil {

    private static final Logger notifiLogger = LoggerFactory.getLogger("notifi");

    public static void main(String[] args) throws IOException {
//        // 生成号码文件名
//        String fileName = "fileName.txt";
//        // 创建文件对象
//        String path = "F:\\test\\sinoadmin\\Desktop\\TestPath\\TestPath2\\";
////        String content = "just some content";
////
////        createFile(path, fileName, content);
//
//        deleteFile(path, fileName);
    }

    public static void createFile(String path, String fileName, String content) throws IOException {
        File file = new File(path, fileName);

        if (!file.getParentFile().exists()) {
           Boolean getParentFile = file.getParentFile().mkdirs();
           if(getParentFile){
               log.info("getParent File success");
           }
        }
        if (file.exists()) {
            Boolean delete=  file.delete();
        }
        // 创建文件
        Boolean create = file.createNewFile();

        if(create){
            log.info("create File success");
        }

        notifiLogger.info("create file:{}", file.getAbsolutePath());

        // 将内容写入文件
       // FileWriter writer = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (file,true),"UTF-8"));
        writer.write(content);
        writer.flush();
        writer.close();

        Runtime.getRuntime().exec("sudo chmod 777 " + file.getAbsolutePath());
    }

    public static void createHtmlFile(String path, String fileName, String content) throws IOException {
        File file = new File(path, fileName);

        if (!file.getParentFile().exists()) {
            Boolean mkdirs  =  file.getParentFile().mkdirs();
            if(mkdirs){
                log.info("mkdirs File success");
            }
        }
        if (file.exists()) {
            Boolean delete =  file.delete();
        }
        // 创建文件
        Boolean create =  file.createNewFile();

        if(create){
            log.info("create File success");
        }

        notifiLogger.info("create file:{}", file.getAbsolutePath());

        // 将内容写入文件
        OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
        BufferedWriter writer = new BufferedWriter(write);
        writer.write(URLDecoder.decode(content,"UTF-8"));
        writer.close();

        Runtime.getRuntime().exec("sudo chmod 777 " + file.getAbsolutePath());
    }

    public static void deleteFile(String path, String fileName){
        File file = new File(path, fileName);
        notifiLogger.info("delete file:{}, exist result;{}", file.getAbsolutePath(), file.exists());
        if (file.exists()) {
            Boolean delete =  file.delete();
        }
    }
    
    public static String getUuidFileName(String fileName) {
        if (fileName == null || "".equals(fileName)) {
            return "";
        }
        int pointIndex = fileName.lastIndexOf(".");
        if (pointIndex <= 0) {
            return "";
        }
        String filetype = fileName.substring(pointIndex, fileName.length());
        String fileNewName = UUID.randomUUID().toString() + filetype;
        return fileNewName;
    }
    
    public static List<String> readToList(String path) {
        List<String> list = null;
        BufferedReader br = null;
        if (StringUtils.isEmpty(path)) {
            return list;
        }
        
        try {
            File file = new File(path);
            // 判断文件是否存在
            if (file.exists()) {
            } else {
                System.out.println(path + "文件不存在！");
                return list;
            }
            // 中文的时候要注意文件的编码
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(file), "utf-8");
            br = new BufferedReader(reader, 1024 * 1024);
            String line = "";
            list = new ArrayList<String>();
            while ((line = br.readLine()) != null) {
                if (StringUtils.isEmpty(line.trim())) {
                    continue;
                }
                list.add(line.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}