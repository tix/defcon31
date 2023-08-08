package com.starp.zoo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.constant.Constant;
import com.starp.zoo.constant.ZooConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author david
 */
public class DecodeDescUtil {


    public static void main(String[] args) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ap","87b9d0c0-97d6-407b-8fbb-252b24a73d56");
        jsonObject.put("of","e2f007c8-066b-4989-812e-cfeff40d3b7f");
        jsonObject.put("status",1);
        jsonObject.put("debug","Y");
        jsonObject.put("op","46003");
        jsonObject.put("upc","8888");
        jsonObject.put("path","http://msisdn.mife-aoc.com/api/celcom/aoc/ask/test");
        jsonObject.put("body","Content-Type: text/plain;charset=utf-8\n" +
                "Content-Length: 673\n" +
                "Host: 54.179.143.95\n" +
                "Connection: Keep-Alive\n" +
                "Accept-Encoding: gzip\n" +
                "User-Agent: okhttp/4.2.2\n" +
                "\\/nokhttp3.RequestBody$Companion$toRequestBody$2@7b82551");
        jsonObject.put("pid","0a98d0c8-d534-40fe-a525-d870f294a87a");
        jsonObject.put("userid","81ebf343-30cb-45f6-b67b-ae0713adcdd9");
        jsonObject.put("sio","52000");
//        String decodeStr = decodeDesc(str);
        String offer = "{\n" +
                "    \"appId\":\"afe7689b-0086-4371-b936-d94680a72cfd\",\n" +
                "    \"deviceId\":\"15ab45b4-b69f-4a17-a4c4-78511a7ac633\",\n" +
                "    \"packageName\":\"TEST-GG1\",\n" +
                "    \"status\":1,\n" +
                "    \"offerId\":\"14dcff32-d47d-4bef-bde6-7abf4bf21184\",\n" +
                "    \"userId\":\"1234567\"\n" +
                "}";

        String html = "<div class=\"col-md-6 col-md-offset-3 col-xs-8 col-xs-offset-2\" id=\"msisdn-4g\">\n" +
                "                    <input type=\"text\" class=\"form-control\" id=\"msisdn-4g-box\" value=\"66822230098\" maxlength=\"11\" placeholder=\"กรุณากรอกหมายเลขโทรศัพท์เคลื่อนที่\" disabled=\"\">\n" +
                "                </div>\n" +
                "                <div class=\"col-md-6 col-md-offset-3 col-xs-12 col-xs-offset-0 hidden\" id=\"msisdn-wifi\">\n" +
                "                    <div class=\"alertReqOtpError hidden\">\n" +
                "                        <strong>หมายเลขโทรศัพท์ไม่ถูกต้อง</strong> กรุณากรอกใหม่อีกครั้ง\n" +
                "                    </div>\n" +
                "                    <p class=\"clearfix\"></p>\n" +
                "                    <input type=\"text\" class=\"form-control \" id=\"msisdn-wifi-box\" maxlength=\"11\" placeholder=\"กรุณากรอกหมายเลขโทรศัพท์เคลื่อนที่\">\n" +
                "                </div>";
        JSONObject htmlJson = new JSONObject();
        htmlJson.put("appid","69930407-13a8-46be-9deb-e9c3afe05281");
        htmlJson.put("offerid","9fca890e-1c9b-4d26-9dad-2a9f3a3f52d5");
        htmlJson.put("originUrl","http://ss1.mobilelife.co.th/wis/wap?ch=WAP&cmd=s_exp&SN=4653030&spName=653&token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJTTiI6IjQ2NTMwMzAiLCJzcHNJRCI6IjE2MDI1MDAwNTA2MDM5MyIsImNVUkwiOiJhMytwVjJSTEk1dXdEZ2ZMUmhUeHAydCsybmpnU052UUsrXC90eUNoMmRucUgyNXlFT1hqbVV3TmltZzFRdzZsUkgzNkozSGwyZXIxN3p2eDFkQWNYY2tRRitwRDhLTEg0elIzMzB2a3V3WnRCTEkwRll4NzNTaWYwZ2V0emtLMHRQdkFlK2MyTlwvY09ISnlTQlwvQTBtRXR6Qm5sRzZkQ3BacURVMk9TRmd2bzZFbXpyZ2FSYjVNeEpnYTZiUnFOKzhGb0lGSUY0aHh5NmZtZDg3cXVtMk5Qckd0MnJZTHRwVzF1XC96NDR2K255T3NVdkR4SlM3SzduK3ZaYkZqZFF4NFBQNldnUm9HbVBiVndaQnRtdEIrY3ozY0RobFZzY21XTUx0TXRwV3N0TURpRlR4N0ZEMWk5R2k0MFZockNqbFFFbm4xZ1RcL3VRakp6Wk5IWXNCUzllQT09IiwidGltZXN0YW1wIjoxNjAyNTAwMDUwNjAzfQ.XRH9NBCqjMPcckub2O6nKC0JjKjbUXsi5M5uijlm348");
        htmlJson.put("userid","123456789");
        htmlJson.put("source",html);
        String decode = "LjXqG/VNj2iUCeSSn8nfqXYFxMatb0WMEXrf37JDbyCQ77oCRphSQ1uV/8cszZUNHVgoMIf+B598h5QUubZg1HWMBXMG2K7qPRv20nLnzHJO/23qvmeQ4oWJ2gzPga+lS/b8Ct8seB97S/wU36YoBa8Fi5dMHovpX8j24BNiaC09pOG4sSRD/8Lwc5qk2muIsqvITqBEi2AJPFeLBRqr37dJSdQcDzgjfMA9rVzhI3Y9nZWC/qmVjNrchWzz9F9t++s5+d8XmYZZbM62/qW0eS5rp2/EdYuRCr4r3jpHJTJkTPCge279rH/S/TSt1GmfDaruyMrYr1RT0K7mFT4DfzMN0hfTsnegk/Trm9qZw4VEznfATOX7ism7asNLRopTntycIcsjkSU=";
        String strs = "Atwu05Zf4eOkcSeaxDYP8AUrFahkxSlfXpZ//fL+mObJM0OAJQKG1RS5cT9fk1enPurmrTwJzHPY47pDFQcba3RhJatTN0Pi3Xs7TAtlujRNl6/ZchjPCsz2+lIHgda4pnTHtjM7vIX5CapI4OnNuMUCFGQEr82DO7iH7tfgvwj6zQbfcZRSsiYMUAYItCQxkPDyQJ1Blx0A42yZtm4ZA3B4pzFiA51JJlYpUlx0n/o149p83TuuV0WizNMfqIvQSSv8JaYqzxACf50AiYVYtaU4NmyGDKABypPLMHEVA+EXHB6uH0M7rjID3kpVbAyB7ZxOUqGhg3BVHYpSnA3uxBHHaTqDhngtQa8TdJoPmLZkMGviFymw+YmsrgdH5QytTDyAt9S0og8rjmTkicAalFOnpKD4M7KFDKfIweSQFENcrYVpOtt44VQDx5AcJ53l9FE4VNRJxGitayl89oHawYRkr8xVKWwyXI1uZ6pxI1ozmtXlzrYncNY+1dYaIRFqRN4O4aK1LuVpGY5MPlKx3imcDn4K2BrTFXKmDPzIko+YPskX7gRcmfdV9ckQ8FfIbSihCzkKDE4DGSUc0kFV3XDDrrlKLjTrmIb30JF78bazBWTzj9gEGyT/rYNskQImGH1jZYIMy0c2/+kJtx35KwL3K2a3Uvjx9VNZ+bALe5JLSe+1cv+9VxF7CDtN1VMqcRU5KvhmO3WLn0EW4h3meA==";
        String jsParam = "B+spSZW8y8Xp9xonf3CSvFgE4KpEhcOrbYgbCKg67pHfKw6cN3Leayh+Xnf2QcRP9OY3v6BRqUC9s6wqadeeSbZ9ijtLKuUu4T8cnImf74Y/rJpuFV/4nlDvjxKQWXxNpgWql9FuU1xx92pJ3NQhKEhKwkp2cohRNnCV0KfE4irio4pQ00MzWmA/fgn7o4ewO5SyBjMCMmadIJtDURB/PgPTbIRH5RyUfXXQ5CEH9VNwBeMMFtSmFfTitH2eoLc8MfjtMdDSp06dg06YIT6hbbS5cECI03JZmhthT2O0BSKmbfyCZBYCGUP6bFcJgHV5mb49ymJGLY4uUdCyQCFlZv6Ira0GKb1AX0Cdn3qzwqSk9qaYub0ctNvW7wZ2MMcXzrmzYZGxPAc21XfnYfQMfthl/GP3KMHpbKjYpCx/RE1mXVRyxiaZAxCw/gir3YZhARCSxNU/Zm0OtEPQFeI3Hb43uck5vyZIfCM6vR9E0KgAZLCmWPtjPzfKaJ5vYWDVdDkfbR3xxNquQoEKa/PKQG4SSVNpgvgK6KvuYjAJYr/SEQ8dUH2fVwTLJZkzAtyHEoL2PfOEh01vMHoA/6c0BKpuLL+ombGdX3RJAx9E8EVLDNoR/XOvoDH26x5iqndLYofB63EYcjT03PbQTCMyqeXy2aMdMVKKrPippK7H8mxHYmo/sPxCSfmGdnazzW3GlEj8yxBGrfc23T44oc+xZKtjZgLOeuHfx/IISIwo+Bo2bCWGnpqnbg==";
    }
    private static final String KEY_14 = "20200420";

    private static final String KEY_16 = "20200416";

    private static final String KEY_18 = "20200418";

    public static String encodeDesc(String data) throws Exception {
        DesUtil desUtil =  new DesUtil(KEY_14, KEY_14);
        String encodeStr = desUtil.encode(data);
        return encodeStr;
    }

    public static String decodeDesc(String data) throws Exception {
        DesUtil desUtil =  new DesUtil(KEY_14, KEY_14);
        String encodeStr = desUtil.decode(data);
        return encodeStr;
    }



}
