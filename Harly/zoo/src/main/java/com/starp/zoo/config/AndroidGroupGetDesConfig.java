package com.starp.zoo.config;

import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.util.DesUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Charles
 * @date 2019/8/12
 * @description :
 */
public class AndroidGroupGetDesConfig {

    /**
     * new
     */

    private static final String SDK14 = "sdk14";

    private static final String KEY_14 = "20200414";

    private static final String SDK15 = "sdk15";

    private static final String KEY_15 = "20200415";

    private static final String SDK16 = "sdk16";

    private static final String KEY_16 = "20200416";

    private static final String SDK17 = "sdk17";

    private static final String KEY_17 = "20200417";

    private static final String SDK18 = "sdk18";

    private static final String KEY_18 = "20210108";

    private static final String SDK19 = "sdk19";

    private static final String KEY_19 = "20200419";

    private static final String SDK20 = "sdk20";

    private static final String KEY_20 = "20200420";

    private static final String SDK21 = "sdk21";

    private static final String KEY_21 = "20210629";

    private static final String SDK22 = "sdk22";

    private static final String KEY_22 = "20210628";

    private static final String SDK23 = "sdk23";

    private static final String KEY_23 = "20210627";


    private static final String SDK24 = "sdk24";

    private static final String KEY_24 = "20211026";

    private static final String SDK25 = "sdk25";

    private static final String KEY_25 = "20211027";

    private static final String SDK26 = "sdk26";

    private static final String KEY_26 = "20211028";

    private static final String SDK27 = "sdk27";

    private static final String KEY_27 = "20220310";

    private static final String SDK28 = "sdk28";

    private static final String KEY_28 = "20220311";

    private static final String SDK29 = "sdk29";

    private static final String KEY_29 = "20220312";

    private static final String SDK30 = "sdk30";

    private static final String KEY_30 = "20220606";

    private static final String SDK31 = "sdk31";

    private static final String KEY_31 = "20220607";

    private static final String SDK32 = "sdk32";

    private static final String KEY_32 = "20220830";

    private static final String SDK33 = "sdk33";

    private static final String KEY_33 = "20220831";

    private static final String SDK34 = "sdk34";

    private static final String KEY_34 = "20220919";

    private static final String SDK35 = "sdk35";

    private static final String KEY_35 = "20220920";

    private static final String SDK44 = "sdk44";

    private static final String KEY_44 = "20230214";

    private static final String SDK45 = "sdk45";

    private static final String KEY_45 = "20230323";

    private static final String SDK46 = "sdk46";

    private static final String KEY_46 = "20230504";

    private static final String SDK47 = "sdk47";

    private static final String KEY_47 = "20230601";

    private static final String SDK48 = "sdk48";

    private static final String KEY_48 = "20230703";

    private static final String SDK49 = "sdk49";

    private static final String KEY_49 = "20230802";


    @SuppressFBWarnings("MS_SHOULD_BE_REFACTORED_TO_BE_FINAL")
    public static Map<String, String> URI_MAP = new HashMap<>(1);

    static {
        Map<String, String> tempMap = new HashMap<>(1);


        /**
         * 测试加密类  专属alie 勿删   每次改偏移量记着做调整  并且告诉alie要用谁的接口测试
         */
        tempMap.put("/get/encode", SDK14);


        /**
         * Chet 2020-07-20新接口
         */
        /**
         * Chet新接口
         */
        // GET APP STATUS
        tempMap.put("/vks/csK2t", SDK18);
        // SEND_APP_TEMP_INFO
        tempMap.put("/xnm/vhJ3s", SDK18);
        // GET_APP_TEMP_INFO
        tempMap.put("/vcx/pso7J", SDK18);
        // GOT_OFFER
        tempMap.put("/rxv/fvcKs", SDK18);
        // API_PULL_CONFIG
        tempMap.put("/lst/xjRk2", SDK18);
        // EVENT_URL
        tempMap.put("/cka/uck4A", SDK18);
        // API_WRITE_SUBSCRIBE
        tempMap.put("/vkg/rjS2m", SDK18);
        // API_UPLOAD_SOURCE
        tempMap.put("/a7y/vSn3b", SDK18);
        // HTTP ALEX PULL CONFIG
        tempMap.put("/lsk/v1SdJ", SDK18);
        // HTTP ALEX SAVE LOG
        tempMap.put("/vks/bsn3T", SDK18);
        // HTTP ALEX TRANSFER
        tempMap.put("/kop/xnB2t", SDK18);
        // HTTP OFFLINE PULL OFFER
        tempMap.put("/syH/ash1T", SDK18);
        // HTTP CHECK MNC
        tempMap.put("/xJr/sxjH2", SDK18);
        //SUBMIT DEVICEID
        tempMap.put("/kst/n7sTr", SDK18);
        //GET DIVICEID
        tempMap.put("/cnh/cha2s", SDK18);
        //CHECK MSISDN PAGE
        tempMap.put("/cvs/arn2t", SDK18);
        //CHECK PERMISSION
        tempMap.put("/bsk/v3jmS", SDK18);

        /**
         * Kevin 2020-07-20新接口
         */
        /**
         * Kevin新接口
         */
        // GET APP STATUS
        tempMap.put("/vkf/fkS5j", SDK17);
        // SEND_APP_TEMP_INFO
        tempMap.put("/sks/aRrks", SDK17);
        // GET_APP_TEMP_INFO
        tempMap.put("/vjo/3ksJs", SDK17);
        // GOT_OFFER
        tempMap.put("/klt/xv3jR", SDK17);
        // API_PULL_CONFIG
        tempMap.put("/mns/artx2", SDK17);
        // EVENT_URL
        tempMap.put("/ghs/xjv2N", SDK17);
        // API_WRITE_SUBSCRIBE
        tempMap.put("/vbc/kNs2t", SDK17);
        // API_UPLOAD_SOURCE
        tempMap.put("/cxu/nuT5t", SDK17);
        // HTTP ALEX PULL CONFIG
        tempMap.put("/kvh/cgk3S", SDK17);
        // HTTP ALEX SAVE LOG
        tempMap.put("/vbn/rjvSk", SDK17);
        // HTTP ALEX TRANSFER
        tempMap.put("/bjv/sv3Jg", SDK17);
        // HTTP OFFLINE PULL OFFER
        tempMap.put("/sxb/sjv3B", SDK17);
        // HTTP CHECK MNC
        tempMap.put("/mzc/vnb2K", SDK17);
        //SUBMIT DEVICEID
        tempMap.put("/bnf/fKs3n", SDK17);
        //GET DIVICEID
        tempMap.put("/xjb/vjg2T", SDK17);
        //CHECK MSISDN PAGE
        tempMap.put("/bkt/xjvF8", SDK17);
        //CHECK PERMISSION
        tempMap.put("/qwz/waQzt", SDK17);


        /**
         * brave 2020-09-07 新增接口
         */
        // GET APP STATUS
        tempMap.put("/gjs/tjJ2g", SDK17);
        // SEND_APP_TEMP_INFO
        tempMap.put("/rkx/fosT7", SDK17);
        // GET_APP_TEMP_INFO
        tempMap.put("/cml/xkJt4", SDK17);
        // GOT_OFFER
        tempMap.put("/vbs/tvsF3", SDK17);
        // 利刃_PULL_CONFIG
        tempMap.put("/ltj/kgb6J", SDK17);
        // SEND_APP_EVENT
        tempMap.put("/gyp/bvUh9", SDK17);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/ksc/aBsc4", SDK17);
        // 利刃_SEND_HTML
        tempMap.put("/usp/Asw3e", SDK17);
        // HTTP ALEX PULL CONFIG
        tempMap.put("/g2j/xklTR", SDK17);
        // HTTP_SEND_LOG
        tempMap.put("/jkb/Cjh3g", SDK17);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/ust/cs3tG", SDK17);
        // HTTP OFFLINE PULL OFFER
        tempMap.put("/pos/zaJe4", SDK17);
        // CHECK_FB
        tempMap.put("/hxm/mvbTa", SDK17);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/vkg/ghRt9", SDK17);
        //GET_MSISDN
        tempMap.put("/bty/xgrt3", SDK17);
        //CHECK_MSISDN
        tempMap.put("/csh/psrTx", SDK17);
        //CHECK PERMISSION
        tempMap.put("/vsf/gjheT", SDK17);


        /**
         * Chet 2020-09-07 新增接口
         */
        /**
         * Chet新接口
         */
        // GET APP STATUS
        tempMap.put("/sig/v8gsT", SDK18);
        // SEND_APP_TEMP_INFO
        tempMap.put("/wgh/vnf3k", SDK18);
        // GET_APP_TEMP_INFO
        tempMap.put("/vkg/aksT2", SDK18);
        // GOT_OFFER
        tempMap.put("/ger/msTgf", SDK18);
        // 利刃_PULL_CONFIG
        tempMap.put("/xng/dkr2T", SDK18);
        // SEND_APP_EVENT
        tempMap.put("/wbs/bGktn", SDK18);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/vjs/lkhTj", SDK18);
        // 利刃_SEND_HTML
        tempMap.put("/ckt/xmg5T", SDK18);
        // HTTP ALEX PULL CONFIG
        tempMap.put("/kqw/wtkNb", SDK18);
        // HTTP_SEND_LOG
        tempMap.put("/psh/bntD5", SDK18);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/dfg/vkjT4", SDK18);
        // HTTP OFFLINE PULL OFFER
        tempMap.put("/wre/lkaJt", SDK18);
        // CHECK_FB
        tempMap.put("/vgT/jgvFr", SDK18);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/msg/ktjJm", SDK18);
        //GET_MSISDN
        tempMap.put("/qtn/gjTkn", SDK18);
        //CHECK_MSISDN
        tempMap.put("/eqr/fkjYs", SDK18);
        //CHECK PERMISSION
        tempMap.put("/bvn/tiOpn", SDK18);


        /**
         * vic 2020-09-09新接口
         * 飞哥接口
         */
        // GET APP STATUS
        tempMap.put("/vjs/Bgk5t", SDK19);
        // SEND_APP_TEMP_INFO
        tempMap.put("/jts/vnkRt", SDK19);
        // GET_APP_TEMP_INFO
        tempMap.put("/fnc/tk9bf", SDK19);
        // GOT_OFFER
        tempMap.put("/cks/pkvT3", SDK19);
        // 利刃_PULL_CONFIG
        tempMap.put("/vjs/gkteb", SDK19);
        // SEND_APP_EVENT
        tempMap.put("/gkj/vnsk9", SDK19);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/bmv/tjhgn", SDK19);
        // 利刃_SEND_HTML
        tempMap.put("/nba/bvktc", SDK19);
        // HTTP ALEX PULL CONFIG
        tempMap.put("/vng/lqhgn", SDK19);
        // HTTP_SEND_LOG
        tempMap.put("/gbj/btjh1", SDK19);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/hgk/dMn3g", SDK19);
        // HTTP OFFLINE PULL OFFER
        tempMap.put("/vjg/ptjBn", SDK19);
        // CHECK_FB
        tempMap.put("/kfs/bkgoT", SDK19);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/csk/lgjk2", SDK19);
        //GET_MSISDN
        tempMap.put("/lxs/bngth", SDK19);
        //CHECK_MSISDN
        tempMap.put("/dwg/vjfdk", SDK19);
        //CHECK PERMISSION
        tempMap.put("/skt/Pgjk2", SDK19);


        /**
         * 2020-10-12
         */

        /**
         * brave 2020-10-12 新增接口
         */
        // GET APP STATUS
        tempMap.put("/rns/xkf1g", SDK17);
        // SEND_APP_TEMP_INFO
        tempMap.put("/ksh/ftjk7", SDK17);
        // GET_APP_TEMP_INFO
        tempMap.put("/bnt/rvjgf", SDK17);
        // GOT_OFFER
        tempMap.put("/vks/blgb2", SDK17);
        // 利刃_PULL_CONFIG
        tempMap.put("/skf/oyg3j", SDK17);
        // SEND_APP_EVENT
        tempMap.put("/jgn/kgj2s", SDK17);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/vgb/aJbgs", SDK17);
        // 利刃_SEND_HTML
        tempMap.put("/jbp/Ashge", SDK17);
        // HTTP ALEX PULL CONFIG
        tempMap.put("/bgs/uyhTn", SDK17);
        // HTTP_SEND_LOG
        tempMap.put("/b2t/vjgTs", SDK17);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/qmt/bgj2G", SDK17);
        // HTTP OFFLINE PULL OFFER
        tempMap.put("/uyt/ap3sX", SDK17);
        // CHECK_FB
        tempMap.put("/skg/g2kwi", SDK17);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/fxy/rchle", SDK17);
        //GET_MSISDN
        tempMap.put("/bvk/b2ks3", SDK17);
        //CHECK_MSISDN
        tempMap.put("/bgo/xmjTs", SDK17);
        //CHECK PERMISSION
        tempMap.put("/lhj/bjg2v", SDK17);
        // CHECK APP LOG STATUS
        tempMap.put("/hvc/vjs2T", SDK17);


        /**
         * Chet 2020-10-12 新增接口
         */
        /**
         * Chet新接口
         */
        // GET APP STATUS
        tempMap.put("/hgv/jkbTs", SDK18);
        // SEND_APP_TEMP_INFO
        tempMap.put("/bnB/bns3k", SDK18);
        // GET_APP_TEMP_INFO
        tempMap.put("/cmx/abjTg", SDK18);
        // GOT_OFFER
        tempMap.put("/xcj/klbYj", SDK18);
        // 利刃_PULL_CONFIG
        tempMap.put("/vns/kz2jT", SDK18);
        // SEND_APP_EVENT
        tempMap.put("/tes/jgwtn", SDK18);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/asn/bvnGs", SDK18);
        // 利刃_SEND_HTML
        tempMap.put("/fgn/lsTes", SDK18);
        // HTTP ALEX PULL CONFIG
        tempMap.put("/jdg/wk5sn", SDK18);
        // HTTP_SEND_LOG
        tempMap.put("/drx/dxeft", SDK18);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/dwg/vlsdx", SDK18);
        // HTTP OFFLINE PULL OFFER
        tempMap.put("/gpf/lgkbt", SDK18);
        // CHECK_FB
        tempMap.put("/hbT/jHskT", SDK18);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/mcs/gksgb", SDK18);
        //GET_MSISDN
        tempMap.put("/giu/vjfbn", SDK18);
        //CHECK_MSISDN
        tempMap.put("/xns/3kfYs", SDK18);
        //CHECK PERMISSION
        tempMap.put("/sfg/gjtPn", SDK18);
        // CHECK APP LOG STATUS
        tempMap.put("/cjt/jvgvt", SDK18);


        /**
         * mt 2020-10-12 新增接口
         */
        /**
         * Martin新接口
         */
        // GET APP STATUS
        tempMap.put("/ksg/bgkgt", SDK14);
        // SEND_APP_TEMP_INFO
        tempMap.put("/bns/mhkot", SDK14);
        // GET_APP_TEMP_INFO
        tempMap.put("/bit/kxgFe", SDK14);
        // GOT_OFFER
        tempMap.put("/yus/jbngT", SDK14);
        // 利刃_PULL_CONFIG
        tempMap.put("/sio/tbs3w", SDK14);
        // SEND_APP_EVENT
        tempMap.put("/rks/eytns", SDK14);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/pkv/klbmr", SDK14);
        // 利刃_SEND_HTML
        tempMap.put("/wjf/stjsf", SDK14);
        // HTTP ALEX PULL CONFIG
        tempMap.put("/jvt/bvkgk", SDK14);
        // HTTP_SEND_LOG
        tempMap.put("/lkt/nvbTm", SDK14);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/dfr/lksjt", SDK14);
        // HTTP OFFLINE PULL OFFER
        tempMap.put("/vjs/dkTmr", SDK14);
        // CHECK_FB
        tempMap.put("/cgk/jskgT", SDK14);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/wjt/gmxcn", SDK14);
        //GET_MSISDN
        tempMap.put("/gks/g3ksRt", SDK14);
        //CHECK_MSISDN
        tempMap.put("/pfh/ugsTj", SDK14);
        //CHECK PERMISSION
        tempMap.put("/xbs/kbvTk", SDK14);
        // CHECK APP LOG STATUS
        tempMap.put("/gks/tkwoN", SDK14);


        /**
         * gray 2020-11-11 新增接口
         *
         */
        // GET APP STATUS
        tempMap.put("/N2K/HJj5k", SDK20);
        // SEND_APP_TEMP_INFO
        tempMap.put("/oj2/d65nl", SDK20);
        // GET_APP_TEMP_INFO
        tempMap.put("/mld/bjd2a", SDK20);
        //CHECK PERMISSION
        tempMap.put("/cke/k32NK", SDK20);
        //GET_MSISDN
        tempMap.put("/h8L/BJK8c", SDK20);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/onl/bj3Js", SDK20);
        // GET_OFFER（线上）
        tempMap.put("/wui/hu2on", SDK20);
        // GET_OFFER（线下）
        tempMap.put("/ope/Hkl2j", SDK20);
        // 利刃_PULL_CONFIG
        tempMap.put("/pnb/bh2km", SDK20);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/cvm/hjk8b", SDK20);
        // HTTP PULL CONFIG
        tempMap.put("/une/nj3nK", SDK20);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/i3k/Njk3k", SDK20);
        // SEND_APP_EVENT
        tempMap.put("/omd/yu28B", SDK20);
        // 利刃_SEND_HTML
        tempMap.put("/J3d/B2kmx", SDK20);
        // HTTP_SEND_LOG
        tempMap.put("/x4m/H2klj", SDK20);
        // CHECK_FB
        tempMap.put("/pwm/nlKn2", SDK20);
        // CHECK_MSISDN
        tempMap.put("/L6k/fdJ2S", SDK20);
        // CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/I2k/Nkdfs", SDK20);

        /**
         * Martin 2020-11-11新接口
         *
         */
        // GET APP STATUS
        tempMap.put("/qwe/HJkhk", SDK14);
        // SEND_APP_TEMP_INFO
        tempMap.put("/ds2/Hjh2j", SDK14);
        // GET_APP_TEMP_INFO
        tempMap.put("/mk8/JKnj2", SDK14);
        //CHECK PERMISSION
        tempMap.put("/jk2/HIn2i", SDK14);
        //GET_MSISDN
        tempMap.put("/y2j/kHJj2", SDK14);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/bhj/BGT2j", SDK14);
        // GET_OFFER（线上）
        tempMap.put("/oe3/7HJKs", SDK14);
        // GET_OFFER（线下）
        tempMap.put("/yyn/7hjGj", SDK14);
        // 利刃_PULL_CONFIG
        tempMap.put("/iNn/IGyv9", SDK14);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/udb/B2BJ0", SDK14);
        // HTTP_PULL_CONFIG
        tempMap.put("/xer/v5fdv", SDK14);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/byk/h3J2B", SDK14);
        // SEND_APP_EVENT
        tempMap.put("/d4s/Mjhjd", SDK14);
        // 利刃_SEND_HTML
        tempMap.put("/io3/Hk8BN", SDK14);
        // HTTP_SEND_LOG
        tempMap.put("/H9n/BJM7n", SDK14);
        // CHECK_FB
        tempMap.put("/J2K/Hjk2n", SDK14);
        //CHECK_MSISDN
        tempMap.put("/op2/f2JNK", SDK14);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/ion/h3k2M", SDK14);

        /**
         * brave 2020-11-11 新增接口
         *
         */
        // GET APP STATUS
        tempMap.put("/dbj/Bjk2k", SDK17);
        // SEND_APP_TEMP_INFO
        tempMap.put("/miz/Nu5kS", SDK17);
        // GET_APP_TEMP_INFO
        tempMap.put("/P2M/bJK3D", SDK17);
        //CHECK PERMISSION
        tempMap.put("/BHJ/nj32l", SDK17);
        //GET_MSISDN
        tempMap.put("/NI2/fdk2b", SDK17);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/mid/Hjk2V", SDK17);
        // GET_OFFER（线上）
        tempMap.put("/Bh6/zbkkp", SDK17);
        // GET_OFFER（线下）
        tempMap.put("/bnj/BU8jk", SDK17);
        // 利刃_PULL_CONFIG
        tempMap.put("/mxj/Nji3J", SDK17);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/REb/vbhH2", SDK17);
        // HTTP_PULL_CONFIG
        tempMap.put("/ejk/b3k4k", SDK17);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/dbv/bjkB2", SDK17);
        // SEND_APP_EVENT
        tempMap.put("/Yxj/Jbj2l", SDK17);
        // 利刃_SEND_HTML
        tempMap.put("/N2n/dfs9B", SDK17);
        // HTTP_SEND_LOG
        tempMap.put("/ejg/UIonw", SDK17);
        // CHECK_FB
        tempMap.put("/xcu/bvu5j", SDK17);
        //CHECK_MSISDN
        tempMap.put("/yrn/jk23g", SDK17);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/vco/io45b", SDK17);


        /**
         * chet 2020-11-11 新增接口
         *
         */
        // GET APP STATUS
        tempMap.put("/h2l/jB24k", SDK18);
        // SEND_APP_TEMP_INFO
        tempMap.put("/yMk/H2JKc", SDK18);
        // GET_APP_TEMP_INFO
        tempMap.put("/iBN/U82jb", SDK18);
        //CHECK PERMISSION
        tempMap.put("/cyb/df23j", SDK18);
        //GET_MSISDN
        tempMap.put("/ebj/bj4hx", SDK18);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/sdi/fd3nj", SDK18);
        // GET_OFFER（线上）
        tempMap.put("/xcv/Nlm3s", SDK18);
        // GET_OFFER（线下）
        tempMap.put("/dfJ/Vm2Ms", SDK18);
        // 利刃_PULL_CONFIG
        tempMap.put("/udb/3h4ks", SDK18);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/fdn/xbb2j", SDK18);
        // HTTP PULL CONFIG
        tempMap.put("/sda/2SD8m", SDK18);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/dsk/zf78h", SDK18);
        // SEND_APP_EVENT
        tempMap.put("/szd/wek23", SDK18);
        // 利刃_SEND_HTML
        tempMap.put("/dsz/we3fe", SDK18);
        // HTTP_SEND_LOG
        tempMap.put("/dso/das2I", SDK18);
        // CHECK_FB
        tempMap.put("/X2K/SDh2l", SDK18);
        //CHECK_MSISDN
        tempMap.put("/wl2/qwo3j", SDK18);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/ON1/Dd3jk", SDK18);


        /**
         * chet 2020-12-03 新增接口
         *
         */
        // GET APP STATUS
        tempMap.put("/hb2/bj32k", SDK18);
        // SEND_APP_TEMP_INFO
        tempMap.put("/xbo/fb21i", SDK18);
        // GET_APP_TEMP_INFO
        tempMap.put("/ojn/jm2n0", SDK18);
        //CHECK PERMISSION
        tempMap.put("/nzu/oxn2p", SDK18);
        //GET_MSISDN
        tempMap.put("/nqs/dnk1z", SDK18);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/nmj/nj92a", SDK18);
        // GET_OFFER（线上）
        tempMap.put("/xcn/zo3nk", SDK18);
        // GET_OFFER（线下）
        tempMap.put("/cnu/ds2bn", SDK18);
        // 利刃_PULL_CONFIG
        tempMap.put("/los/zbu2k", SDK18);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/cuw/2zioh", SDK18);
        // HTTP PULL CONFIG
        tempMap.put("/fbj/xoebw", SDK18);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/cbj/zjkf7", SDK18);
        // SEND_APP_EVENT
        tempMap.put("/xow/2bj5k", SDK18);
        // 利刃_SEND_HTML
        tempMap.put("/bzi/fbj0a", SDK18);
        // HTTP_SEND_LOG
        tempMap.put("/a2d/bjvin", SDK18);
        // CHECK_FB
        tempMap.put("/nki/nkx5h", SDK18);
        //CHECK_MSISDN
        tempMap.put("/bqb/jxi7a", SDK18);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/bci/xzowj", SDK18);
        //PULL SDK
        tempMap.put("/mue/d48yg", SDK18);

        /**
         * brave 2020-12-03 新增接口
         *
         */
        // GET APP STATUS
        tempMap.put("/cad/2bnj3", SDK17);
        // SEND_APP_TEMP_INFO
        tempMap.put("/zxb/4njz9", SDK17);
        // GET_APP_TEMP_INFO
        tempMap.put("/c8j/xnkas", SDK17);
        //CHECK PERMISSION
        tempMap.put("/zio/mk2la", SDK17);
        //GET_MSISDN
        tempMap.put("/xnj/njks7", SDK17);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/bfs/jkas1", SDK17);
        // GET_OFFER（线上）
        tempMap.put("/bnv/dsfk9", SDK17);
        // GET_OFFER（线下）
        tempMap.put("/akn/dfnj2", SDK17);
        // 利刃_PULL_CONFIG
        tempMap.put("/vjk/bh72a", SDK17);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/qne/njksd", SDK17);
        // HTTP_PULL_CONFIG
        tempMap.put("/nlp/2i86b", SDK17);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/bjk/n8sdb", SDK17);
        // SEND_APP_EVENT
        tempMap.put("/cbj/zby62", SDK17);
        // 利刃_SEND_HTML
        tempMap.put("/njz/nds8q", SDK17);
        // HTTP_SEND_LOG
        tempMap.put("/xji/3njsf", SDK17);
        // CHECK_FB
        tempMap.put("/sbu/fbu7x", SDK17);
        //CHECK_MSISDN
        tempMap.put("/bnj/9hjew", SDK17);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/zbu/hij2q", SDK17);
        //PULL SDK
        tempMap.put("/nmu/bu2ia", SDK17);


        /**
         * gray 2020-12-03 新增接口
         *
         */
        // GET APP STATUS
        tempMap.put("/zxp/dnk2z", SDK20);
        // SEND_APP_TEMP_INFO
        tempMap.put("/bjs/io8ah", SDK20);
        // GET_APP_TEMP_INFO
        tempMap.put("/acb/zni2n", SDK20);
        //CHECK PERMISSION
        tempMap.put("/bql/wbj82", SDK20);
        //GET_MSISDN
        tempMap.put("/cuo/3si89", SDK20);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/gsi/bzjk9", SDK20);
        // GET_OFFER（线上）
        tempMap.put("/niw/nzj2j", SDK20);
        // GET_OFFER（线下）
        tempMap.put("/wko/bj71z", SDK20);
        // 利刃_PULL_CONFIG
        tempMap.put("/zbq/huf74", SDK20);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/l2h/ju2nk", SDK20);
        // HTTP PULL CONFIG
        tempMap.put("/znj/duf18", SDK20);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/kaz/hue9k", SDK20);
        // SEND_APP_EVENT
        tempMap.put("/uis/cu4me", SDK20);
        // 利刃_SEND_HTML
        tempMap.put("/buz/fhu1i", SDK20);
        // HTTP_SEND_LOG
        tempMap.put("/o2b/zi3ob", SDK20);
        // CHECK_FB
        tempMap.put("/s3d/ndswm", SDK20);
        // CHECK_MSISDN
        tempMap.put("/x5s/dbski", SDK20);
        // CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/bus/civbe", SDK20);
        //PULL SDK
        tempMap.put("/hus/cdh9m", SDK20);


        /**
         * Martin 2020-12-03新接口
         *
         */
        // GET APP STATUS
        tempMap.put("/ds8/njapq", SDK14);
        // SEND_APP_TEMP_INFO
        tempMap.put("/ciw/xnci2", SDK14);
        // GET_APP_TEMP_INFO
        tempMap.put("/cua/dsn6y", SDK14);
        //CHECK PERMISSION
        tempMap.put("/ab3/ig2mu", SDK14);
        //GET_MSISDN
        tempMap.put("/und/jo2fs", SDK14);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/svd/p4sub", SDK14);
        // GET_OFFER（线上）
        tempMap.put("/bui/cb5bz", SDK14);
        // GET_OFFER（线下）
        tempMap.put("/du7/isnyb", SDK14);
        // 利刃_PULL_CONFIG
        tempMap.put("/mua/i89sd", SDK14);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/sud/k2bum", SDK14);
        // HTTP_PULL_CONFIG
        tempMap.put("/sdh/zni2s", SDK14);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/sug/bz6ym", SDK14);
        // SEND_APP_EVENT
        tempMap.put("/gbd/bcu2j", SDK14);
        // 利刃_SEND_HTML
        tempMap.put("/bum/u2bkq", SDK14);
        // HTTP_SEND_LOG
        tempMap.put("/bsd/h76bq", SDK14);
        // CHECK_FB
        tempMap.put("/guc/7ksdp", SDK14);
        //CHECK_MSISDN
        tempMap.put("/bus/n5uks", SDK14);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/gyu/bnu3g", SDK14);
        //PULL SDK
        tempMap.put("/niq/db76h", SDK14);


        /**
         * chet 2021-01-08 新增接口
         *
         */
        // GET APP STATUS
        tempMap.put("/hjd/yuiw2", SDK18);
        // SEND_APP_TEMP_INFO
        tempMap.put("/ube/hob2z", SDK18);
        // GET_APP_TEMP_INFO
        tempMap.put("/yzi/oju6m", SDK18);
        //CHECK PERMISSION
        tempMap.put("/smo/7b92h", SDK18);
        //GET_MSISDN
        tempMap.put("/uib/bv97a", SDK18);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/ioq/xu3l7", SDK18);
        // GET_OFFER（线上）
        tempMap.put("/ya2/dbh98", SDK18);
        // GET_OFFER（线下）
        tempMap.put("/ix1/73bjd", SDK18);
        // 利刃_PULL_CONFIG
        tempMap.put("/uez/3kh0h", SDK18);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/oue/73qho", SDK18);
        // HTTP PULL CONFIG
        tempMap.put("/snj/zio4a", SDK18);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/oqj/cu2jk", SDK18);
        // SEND_APP_EVENT
        tempMap.put("/nuw/cdi54", SDK18);
        // 利刃_SEND_HTML
        tempMap.put("/dfj/znue8", SDK18);
        // HTTP_SEND_LOG
        tempMap.put("/2hu/shome", SDK18);
        // CHECK_FB
        tempMap.put("/wui/fn3jl", SDK18);
        //CHECK_MSISDN
        tempMap.put("/vui/zioea", SDK18);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/cyw/ni27b", SDK18);
        //PULL SDK
        tempMap.put("/opq/62hju", SDK18);


        /**
         * chet 2021-01-25 新增接口
         *
         */
        // GET APP STATUS
        tempMap.put("/nkk/hui29", SDK18);
        // SEND_APP_TEMP_INFO
        tempMap.put("/ube/xbi8h", SDK18);
        // GET_APP_TEMP_INFO
        tempMap.put("/yxn/ion9y", SDK18);
        //CHECK PERMISSION
        tempMap.put("/bus/cdu56", SDK18);
        //GET_MSISDN
        tempMap.put("/zug/aho2n", SDK18);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/pqn/bgy37", SDK18);
        // GET_OFFER（线上）
        tempMap.put("/yui/cduia", SDK18);
        // GET_OFFER（线下）
        tempMap.put("/eui/5q3ud", SDK18);
        // 利刃_PULL_CONFIG
        tempMap.put("/yue/kuais", SDK18);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/uye/cjudk", SDK18);
        // HTTP PULL CONFIG
        tempMap.put("/bhc/zbc23", SDK18);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/sod/sa4id", SDK18);
        // SEND_APP_EVENT
        tempMap.put("/cug/njs8y", SDK18);
        // 利刃_SEND_HTML
        tempMap.put("/ubs/6nj2b", SDK18);
        // HTTP_SEND_LOG
        tempMap.put("/ybd/dj3la", SDK18);
        // CHECK_FB
        tempMap.put("/cbu/djh72", SDK18);
        //CHECK_MSISDN
        tempMap.put("/ush/37j8n", SDK18);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/u7a/cbhyd", SDK18);
        //PULL SDK
        tempMap.put("/ucb/xbsk9", SDK18);


        /**
         * brave 2021-01-25 新增接口
         *
         */
        // GET APP STATUS
        tempMap.put("/ysm/xbh75", SDK17);
        // SEND_APP_TEMP_INFO
        tempMap.put("/ydb/cj78i", SDK17);
        // GET_APP_TEMP_INFO
        tempMap.put("/xyu/cj52j", SDK17);
        //CHECK PERMISSION
        tempMap.put("/yus/cbh23", SDK17);
        //GET_MSISDN
        tempMap.put("/cdu/52jdj", SDK17);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/sdu/gbd68", SDK17);
        // GET_OFFER（线上）
        tempMap.put("/svz/ma34s", SDK17);
        // GET_OFFER（线下）
        tempMap.put("/dsh/c7d2h", SDK17);
        // 利刃_PULL_CONFIG
        tempMap.put("/fhk/xy3hd", SDK17);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/cds/sdhyu", SDK17);
        // HTTP_PULL_CONFIG
        tempMap.put("/hsl/gd6b1", SDK17);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/udb/sbd5j", SDK17);
        // SEND_APP_EVENT
        tempMap.put("/suc/sdk2g", SDK17);
        // 利刃_SEND_HTML
        tempMap.put("/cdb/sdui2", SDK17);
        // HTTP_SEND_LOG
        tempMap.put("/cys/hs83i", SDK17);
        // CHECK_FB
        tempMap.put("/cyq/cdu62", SDK17);
        //CHECK_MSISDN
        tempMap.put("/yxa/sdj21", SDK17);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/xcu/dak23", SDK17);
        //PULL SDK
        tempMap.put("/dsz/sd23w", SDK17);


        /**
         * gray 2021-01-25 新增接口
         *
         */
        // GET APP STATUS
        tempMap.put("/cus/aoz2j", SDK20);
        // SEND_APP_TEMP_INFO
        tempMap.put("/xsb/sdau2", SDK20);
        // GET_APP_TEMP_INFO
        tempMap.put("/max/sda7h", SDK20);
        //CHECK PERMISSION
        tempMap.put("/cdu/si2hd", SDK20);
        //GET_MSISDN
        tempMap.put("/dsz/hjgf4", SDK20);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/cxz/cdg3z", SDK20);
        // GET_OFFER（线上）
        tempMap.put("/ysh/xhj78", SDK20);
        // GET_OFFER（线下）
        tempMap.put("/cyn/isn5a", SDK20);
        // 利刃_PULL_CONFIG
        tempMap.put("/dfi/cdb34", SDK20);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/udh/dfs23", SDK20);
        // HTTP PULL CONFIG
        tempMap.put("/sdz/qloa2", SDK20);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/udy/msu34", SDK20);
        // SEND_APP_EVENT
        tempMap.put("/twj/wgq23", SDK20);
        // 利刃_SEND_HTML
        tempMap.put("/dsi/ewh21", SDK20);
        // HTTP_SEND_LOG
        tempMap.put("/tnm/sd25z", SDK20);
        // CHECK_FB
        tempMap.put("/dfu/ds65s", SDK20);
        // CHECK_MSISDN
        tempMap.put("/ddw/dshuj", SDK20);
        // CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/yui/cdi78", SDK20);
        //PULL SDK
        tempMap.put("/haq/nmq31", SDK20);


        /**
         * chet 2021-03-25 新增接口
         *
         */
        // GET APP STATUS
        tempMap.put("/din/c86h2", SDK18);
        //CHECK PERMISSION
        tempMap.put("/poi/dnxja", SDK18);
        //GET_MSISDN
        tempMap.put("/usd/fhuaz", SDK18);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/sdh/78njz", SDK18);
        // GET_OFFER（线上）
        tempMap.put("/d9k/cfn8z", SDK18);
        // 利刃_PULL_CONFIG
        tempMap.put("/dfd/89hsj", SDK18);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/mxi/sji2o", SDK18);
        // HTTP PULL CONFIG
        tempMap.put("/us1/8hdnz", SDK18);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/di2/ish8g", SDK18);
        // SEND_APP_EVENT
        tempMap.put("/pnj/8whx1", SDK18);
        // 利刃_SEND_HTML
        tempMap.put("/yxn/8shbx", SDK18);
        // HTTP_SEND_LOG
        tempMap.put("/cua/0shb3", SDK18);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/dum/ue73g", SDK18);
        //NEW HTTP PULL CONFIG
        tempMap.put("/sza/u28nc", SDK18);

        /**
         * brave 2021-03-25 新增接口
         *
         */
        // GET APP STATUS
        tempMap.put("/yxb/wuh2q", SDK18);
        //CHECK PERMISSION
        tempMap.put("/jsd/bcy8s", SDK18);
        //GET_MSISDN
        tempMap.put("/dhf/uu3fd", SDK18);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/dcu/su8d4", SDK18);
        // GET_OFFER（线上）
        tempMap.put("/ydn/du5nj", SDK18);
        // 利刃_PULL_CONFIG
        tempMap.put("/dcw/fhu6i", SDK18);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/gyd/dfhu2", SDK18);
        // HTTP PULL CONFIG
        tempMap.put("/ynd/dfjl3", SDK18);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/cxd/sdhul", SDK18);
        // SEND_APP_EVENT
        tempMap.put("/fdu/dnifs", SDK18);
        // 利刃_SEND_HTML
        tempMap.put("/tdn/cbdus", SDK18);
        // HTTP_SEND_LOG
        tempMap.put("/ueg/bvd2p", SDK18);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/dyu/uebm7", SDK18);
        //NEW HTTP PULL CONFIG
        tempMap.put("/bnx/gk2cb", SDK18);

        /**
         * gray 2021-03-25 新增接口
         *
         */
        // GET APP STATUS
        tempMap.put("/tej/niodj", SDK18);
        //CHECK PERMISSION
        tempMap.put("/diu/buc7e", SDK18);
        //GET_MSISDN
        tempMap.put("/ydn/dfubx", SDK18);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/ysw/cdbyj", SDK18);
        // GET_OFFER（线上）
        tempMap.put("/njp/duaqk", SDK18);
        // 利刃_PULL_CONFIG
        tempMap.put("/cym/ueh9j", SDK18);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/urn/fidn3", SDK18);
        // HTTP PULL CONFIG
        tempMap.put("/dyh/ycxme", SDK18);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/tml/dhfap", SDK18);
        // SEND_APP_EVENT
        tempMap.put("/dfm/xiwlo", SDK18);
        // 利刃_SEND_HTML
        tempMap.put("/ydn/qmwoc", SDK18);
        // HTTP_SEND_LOG
        tempMap.put("/dui/powqm", SDK18);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/ern/icpsb", SDK18);
        //NEW HTTP PULL CONFIG
        tempMap.put("/xvf/whx2r", SDK18);

        /**
         * chet 2021-06-29 新增接口
         * SDK21,KEY21
         */
        // GET APP STATUS
        tempMap.put("/sdh/cuu2s", SDK21);
        //CHECK PERMISSION
        tempMap.put("/cu9/sh6dj", SDK21);
        //GET_MSISDN
        tempMap.put("/tdj/so9zy", SDK21);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/ysn/snziu", SDK21);
        // GET_OFFER（线上）
        tempMap.put("/tsh/xu1sj", SDK21);
        // 利刃_PULL_CONFIG
        tempMap.put("/dyb/opany", SDK21);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/nxh/sduhi", SDK21);
        // HTTP PULL CONFIG
        tempMap.put("/szm/sdyup", SDK21);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/ydn/8hj2p", SDK21);
        // SEND_APP_EVENT
        tempMap.put("/njs/sd3si", SDK21);
        // 利刃_SEND_HTML
        tempMap.put("/bhd/dwm2u", SDK21);
        // HTTP_SEND_LOG
        tempMap.put("/tsh/xnuso", SDK21);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/tak/bcyui", SDK21);
        //NEW HTTP PULL CONFIG
        tempMap.put("/nsh/zu4da", SDK21);

        /**
         * brave 2021-06-29 新增接口
         * SDK22,KEY22
         */
        // GET APP STATUS
        tempMap.put("/ydg/sdhui", SDK22);
        //CHECK PERMISSION
        tempMap.put("/xua/sd7ty", SDK22);
        //GET_MSISDN
        tempMap.put("/sah/xnsy4", SDK22);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/mus/sh76g", SDK22);
        // GET_OFFER（线上）
        tempMap.put("/yts/su92d", SDK22);
        // 利刃_PULL_CONFIG
        tempMap.put("/shu/cyw1k", SDK22);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/tys/xud8h", SDK22);
        // HTTP PULL CONFIG
        tempMap.put("/xys/sd54h", SDK22);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/wtm/sdu9j", SDK22);
        // SEND_APP_EVENT
        tempMap.put("/dhu/cdy4z", SDK22);
        // 利刃_SEND_HTML
        tempMap.put("/xty/sdu3z", SDK22);
        // HTTP_SEND_LOG
        tempMap.put("/tdj/dnu6x", SDK22);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/tss/su4yw", SDK22);
        //NEW HTTP PULL CONFIG
        tempMap.put("/dal/tsj8h", SDK22);

        /**
         * gray 2021-06-29 新增接口
         * SDK23,KEY23
         */
        // GET APP STATUS
        tempMap.put("/jdl/dfh6j", SDK23);
        //CHECK PERMISSION
        tempMap.put("/jid/du6yo", SDK23);
        //GET_MSISDN
        tempMap.put("/duz/cby3a", SDK23);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/ndh/su87d", SDK23);
        // GET_OFFER（线上）
        tempMap.put("/nhz/sduiq", SDK23);
        // 利刃_PULL_CONFIG
        tempMap.put("/co2/sdhuz", SDK23);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/diq/ahduz", SDK23);
        // HTTP PULL CONFIG
        tempMap.put("/usj/zmisw", SDK23);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/hgs/cud8l", SDK23);
        // SEND_APP_EVENT
        tempMap.put("/sdy/yenlo", SDK23);
        // 利刃_SEND_HTML
        tempMap.put("/soi/sdhum", SDK23);
        // HTTP_SEND_LOG
        tempMap.put("/duq/dj5io", SDK23);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/sd8/dhu2i", SDK23);
        //NEW HTTP PULL CONFIG
        tempMap.put("/ydw/duu4f", SDK23);


        /**
         * chet 2021-09-01 新增接口
         * SDK21,KEY21
         */
        // GET APP STATUS
        tempMap.put("/ydb/udhsa", SDK21);
        //CHECK PERMISSION
        tempMap.put("/ywh/u6bdm", SDK21);
        //GET_MSISDN
        tempMap.put("/su9/qu4mj", SDK21);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/twg/dyu9l", SDK21);
        // GET_OFFER（线上）
        tempMap.put("/msw/duz4d", SDK21);
        // 利刃_PULL_CONFIG
        tempMap.put("/njx/ehw1k", SDK21);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/wij/shd3j", SDK21);
        // HTTP PULL CONFIG
        tempMap.put("/nze/df3uw", SDK21);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/udj/cudjf", SDK21);
        // SEND_APP_EVENT
        tempMap.put("/ysb/dusiw", SDK21);
        // 利刃_SEND_HTML
        tempMap.put("/sjd/dji6h", SDK21);
        // HTTP_SEND_LOG
        tempMap.put("/teh/jdif3", SDK21);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/ysh/sdhub", SDK21);
        //NEW HTTP PULL CONFIG
        tempMap.put("/ops/dujis", SDK21);

        /**
         * brave 2021-09-01 新增接口
         * SDK22,KEY22
         */
        // GET APP STATUS
        tempMap.put("/amd/hj3id", SDK22);
        //CHECK PERMISSION
        tempMap.put("/whj/uisdn", SDK22);
        //GET_MSISDN
        tempMap.put("/anx/suh3u", SDK22);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/nxj/weu24", SDK22);
        // GET_OFFER（线上）
        tempMap.put("/pue/sdj5i", SDK22);
        // 利刃_PULL_CONFIG
        tempMap.put("/xhs/fe53k", SDK22);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/yud/cdi8s", SDK22);
        // HTTP PULL CONFIG
        tempMap.put("/zhd/sd4jk", SDK22);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/sgd/cud6h", SDK22);
        // SEND_APP_EVENT
        tempMap.put("/dhu/cxuis", SDK22);
        // 利刃_SEND_HTML
        tempMap.put("/xyu/ui4n2", SDK22);
        // HTTP_SEND_LOG
        tempMap.put("/ejf/djk4s", SDK22);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/psh/und4d", SDK22);
        //NEW HTTP PULL CONFIG
        tempMap.put("/ydm/din2u", SDK22);

        /**
         * gray 2021-09-01 新增接口
         * SDK23,KEY23
         */
        // GET APP STATUS
        tempMap.put("/ydm/xcid2", SDK23);
        //CHECK PERMISSION
        tempMap.put("/dyd/dui3c", SDK23);
        //GET_MSISDN
        tempMap.put("/udn/dicn3", SDK23);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/xow/cdj4l", SDK23);
        // GET_OFFER（线上）
        tempMap.put("/uud/dfnkc", SDK23);
        // 利刃_PULL_CONFIG
        tempMap.put("/cdl/djkn3", SDK23);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/cyd/dfj5k", SDK23);
        // HTTP PULL CONFIG
        tempMap.put("/dus/dui4n", SDK23);
        // HTTP_WRITE_SUBSCRIBE
        tempMap.put("/yfb/vun4j", SDK23);
        // SEND_APP_EVENT
        tempMap.put("/dbj/ci5hd", SDK23);
        // 利刃_SEND_HTML
        tempMap.put("/cyf/dfk9j", SDK23);
        // HTTP_SEND_LOG
        tempMap.put("/ynf/vi4f1", SDK23);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/wkv/fjg7r", SDK23);
        //NEW HTTP PULL CONFIG
        tempMap.put("/duf/risn3", SDK23);

        /**
         * chet 2021-10-26 新增接口
         * SDK24,KEY24
         */
        // GET APP STATUS
        tempMap.put("/dfd/cuid3", SDK24);
        //GET_MSISDN
        tempMap.put("/yun/cbd4h", SDK24);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/sg2/d6jdn", SDK24);
        // GET_OFFER（线上）
        tempMap.put("/ehn/ci4h5", SDK24);
        // 利刃_PULL_CONFIG
        tempMap.put("/ydh/iio7m", SDK24);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/op6/djk3s", SDK24);
        // SEND_APP_EVENT
        tempMap.put("/usn/zij6d", SDK24);
        // 利刃_SEND_HTML
        tempMap.put("/chj/uis3h", SDK24);
        // HTTP_SEND_LOG
        tempMap.put("/ysh/cj5id", SDK24);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/uxn/sdij4", SDK24);
        //NEW HTTP PULL CONFIG
        tempMap.put("/cuk/ui4nf", SDK24);

        /**
         * brave 2021-10-26 新增接口
         * SDK25,KEY25
         */
        // GET APP STATUS
        tempMap.put("/ydc/df46j", SDK25);
        //GET_MSISDN
        tempMap.put("/ydn/fdui5", SDK25);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/cgd/oj55f", SDK25);
        // GET_OFFER（线上）
        tempMap.put("/idn/fuc8n", SDK25);
        // 利刃_PULL_CONFIG
        tempMap.put("/yuv/fui7n", SDK25);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/yfk/fuiod", SDK25);
        // SEND_APP_EVENT
        tempMap.put("/dud/o6inf", SDK25);
        // 利刃_SEND_HTML
        tempMap.put("/vif/yu5kh", SDK25);
        // HTTP_SEND_LOG
        tempMap.put("/udn/io8fn", SDK25);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/dun/if4dn", SDK25);
        //NEW HTTP PULL CONFIG
        tempMap.put("/udm/df48j", SDK25);

        /**
         * gray 2021-10-26 新增接口
         * SDK26,KEY26
         */
        // GET APP STATUS
        tempMap.put("/yjl/duion", SDK26);
        //GET_MSISDN
        tempMap.put("/yfn/oid5k", SDK26);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/umv/iof7l", SDK26);
        // GET_OFFER（线上）
        tempMap.put("/dic/pod5d", SDK26);
        // 利刃_PULL_CONFIG
        tempMap.put("/hdj/inf9d", SDK26);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/kdj/op5fi", SDK26);
        // SEND_APP_EVENT
        tempMap.put("/u4d/ih5uk", SDK26);
        // 利刃_SEND_HTML
        tempMap.put("/udn/cjufe", SDK26);
        // HTTP_SEND_LOG
        tempMap.put("/ydj/ci6od", SDK26);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/dty/ci9fj", SDK26);
        //NEW HTTP PULL CONFIG
        tempMap.put("/ygf/iodfj", SDK26);

        /**
         * chet 2022-02-09 新增接口
         * SDK24,KEY24
         */
        // GET APP STATUS
        tempMap.put("/gdh/dfui3", SDK24);
        //GET_MSISDN
        tempMap.put("/ydm/uxn4e", SDK24);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/ks2/fdi5h", SDK24);
        // GET_OFFER（线上）
        tempMap.put("/yen/dui4a", SDK24);
        // 利刃_PULL_CONFIG
        tempMap.put("/gdj/cue4s", SDK24);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/win/cbd6h", SDK24);
        // SEND_APP_EVENT
        tempMap.put("/njx/dubfx", SDK24);
        // 利刃_SEND_HTML
        tempMap.put("/hdz/cudb5", SDK24);
        // HTTP_SEND_LOG
        tempMap.put("/euh/cbh4z", SDK24);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/cyd/dfuix", SDK24);
        //NEW HTTP PULL CONFIG
        tempMap.put("/ifb/ci52g", SDK24);

        /**
         * brave 2022-02-09 新增接口
         * SDK25,KEY25
         */
        // GET APP STATUS
        tempMap.put("/sxs/fdh7h", SDK25);
        //GET_MSISDN
        tempMap.put("/xsu/7ycdh", SDK25);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/swj/xcdid", SDK25);
        // GET_OFFER（线上）
        tempMap.put("/uxb/sdnbf", SDK25);
        // 利刃_PULL_CONFIG
        tempMap.put("/erb/cdinf", SDK25);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/dbf/xcnbd", SDK25);
        // SEND_APP_EVENT
        tempMap.put("/vfn/cid3o", SDK25);
        // 利刃_SEND_HTML
        tempMap.put("/cdu/xsuh3", SDK25);
        // HTTP_SEND_LOG
        tempMap.put("/cdz/du3bf", SDK25);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/ndc/df6ji", SDK25);
        //NEW HTTP PULL CONFIG
        tempMap.put("/ydb/cd8bf", SDK25);

        /**
         * gray 2022-02-09 新增接口
         * SDK26,KEY26
         */
        // GET APP STATUS
        tempMap.put("/zax/sdhsa", SDK26);
        //GET_MSISDN
        tempMap.put("/scn/xsn2j", SDK26);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/dza/cdn3g", SDK26);
        // GET_OFFER（线上）
        tempMap.put("/cdb/xbhsz", SDK26);
        // 利刃_PULL_CONFIG
        tempMap.put("/pos/xcdbj", SDK26);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/xsn/dfuna", SDK26);
        // SEND_APP_EVENT
        tempMap.put("/sic/cdj4u", SDK26);
        // 利刃_SEND_HTML
        tempMap.put("/zyd/c4bja", SDK26);
        // HTTP_SEND_LOG
        tempMap.put("/udb/fus3z", SDK26);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/cdb/d52hv", SDK26);
        //NEW HTTP PULL CONFIG
        tempMap.put("/tgs/cz7bj", SDK26);

        // 0310新增接口Start
        /**
         * chet 2022-03-10 新增接口
         * SDK27,KEY27
         */
        // GET APP STATUS
        tempMap.put("/gds/dsa56", SDK27);
        //GET_MSISDN
        tempMap.put("/s5z/aw45s", SDK27);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/jh8/v854x", SDK27);
        // GET_OFFER（线上）
        tempMap.put("/jk5/j6687", SDK27);
        // 利刃_PULL_CONFIG
        tempMap.put("/uty/uty25", SDK27);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/fsd/gf575", SDK27);
        // SEND_APP_EVENT
        tempMap.put("/b22/l55kj", SDK27);
        // 利刃_SEND_HTML
        tempMap.put("/i45/jk487", SDK27);
        // HTTP_SEND_LOG
        tempMap.put("/nbv/df48s", SDK27);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/k7j/gh971", SDK27);
        //NEW HTTP PULL CONFIG
        tempMap.put("/d27/q455w", SDK27);
        //address msisdn
        tempMap.put("/h4n/fdihn", SDK27);

        /**
         * brave 2022-03-10 新增接口
         * SDK28,KEY28
         */
        // GET APP STATUS
        tempMap.put("/u46/45cva", SDK28);
        //GET_MSISDN
        tempMap.put("/456/48fdq", SDK28);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/s89/1qwrz", SDK28);
        // GET_OFFER（线上）
        tempMap.put("/f12/12erz", SDK28);
        // 利刃_PULL_CONFIG
        tempMap.put("/j41/687qw", SDK28);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/y84/f64rq", SDK28);
        // SEND_APP_EVENT
        tempMap.put("/hyo/13woh", SDK28);
        // 利刃_SEND_HTML
        tempMap.put("/nvb/27fkw", SDK28);
        // HTTP_SEND_LOG
        tempMap.put("/f57/uky97", SDK28);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/gt6/4567f", SDK28);
        //NEW HTTP PULL CONFIG
        tempMap.put("/fds/4567w", SDK28);
        //address msisdn
        tempMap.put("/xjk/hjx5j", SDK28);

        /**
         * gray 2022-03-10 新增接口
         * SDK29,KEY29
         */
        // GET APP STATUS
        tempMap.put("/1ds/54few", SDK29);
        //GET_MSISDN
        tempMap.put("/fds/157ew", SDK29);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/15o/4567e", SDK29);
        // GET_OFFER（线上）
        tempMap.put("/47q/487lk", SDK29);
        // 利刃_PULL_CONFIG
        tempMap.put("/fd8/1357w", SDK29);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/78w/18gfd", SDK29);
        // SEND_APP_EVENT
        tempMap.put("/8dw/1oilj", SDK29);
        // 利刃_SEND_HTML
        tempMap.put("/78d/15fer", SDK29);
        // HTTP_SEND_LOG
        tempMap.put("/8de/d4812", SDK29);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/e89/564fe", SDK29);
        //NEW HTTP PULL CONFIG
        tempMap.put("/sd8/6578p", SDK29);
        //address msisdn
        tempMap.put("/ydn/8zkdf", SDK29);
        // 0310新增接口End

        /**
         * chet 2022-06-06 新增接口
         * SDK30,KEY30
         */
        // GET APP STATUS
        tempMap.put("/fp5/ds2qe", SDK30);
        //GET_MSISDN
        tempMap.put("/1jk/dt574", SDK30);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/87g/jy354", SDK30);
        // GET_OFFER（线上）
        tempMap.put("/hgk/hfgh5", SDK30);
        // 利刃_PULL_CONFIG
        tempMap.put("/456/fdgrh", SDK30);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/424/fsdgs", SDK30);
        // SEND_APP_EVENT
        tempMap.put("/fd5/222sd", SDK30);
        // 利刃_SEND_HTML
        tempMap.put("/pps/plfd5", SDK30);
        // HTTP_SEND_LOG
        tempMap.put("/ss4/vxcvc", SDK30);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/pla/7945a", SDK30);
        //NEW HTTP PULL CONFIG
        tempMap.put("/sa4/das66", SDK30);
        //address msisdn
        tempMap.put("/fs5/7892a", SDK30);

        /**
         * gray 2022-06-07 新增接口
         * SDK31,KEY31
         */
        // GET APP STATUS
        tempMap.put("/785/5467l", SDK31);
        //GET_MSISDN
        tempMap.put("/dsf/ujh77", SDK31);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/pko/pifgh", SDK31);
        // GET_OFFER（线上）
        tempMap.put("/ugj/fh544", SDK31);
        // 利刃_PULL_CONFIG
        tempMap.put("/uha/okpss", SDK31);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/ooo/sss88", SDK31);
        // SEND_APP_EVENT
        tempMap.put("/saf/441ss", SDK31);
        // 利刃_SEND_HTML
        tempMap.put("/mbf/cjh55", SDK31);
        // HTTP_SEND_LOG
        tempMap.put("/okk/ssd22", SDK31);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/cvn/mnm38", SDK31);
        //NEW HTTP PULL CONFIG
        tempMap.put("/ylm/wznm6", SDK31);
        //address msisdn
        tempMap.put("/okk/8pobn", SDK31);

        /**
         * chet 2022-08-30 新增接口
         * SDK32,KEY32
         */
        // GET APP STATUS
        tempMap.put("/f3f/4gs1h", SDK32);
        //GET_MSISDN
        tempMap.put("/8gg/4hsd6", SDK32);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/0gf/pkhr1", SDK32);
        // GET_OFFER（线上）
        tempMap.put("/9gd/j4561", SDK32);
        // 利刃_PULL_CONFIG
        tempMap.put("/hf1/gfd13", SDK32);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/ng2/mcd35", SDK32);
        // SEND_APP_EVENT
        tempMap.put("/5hj/nvbzq", SDK32);
        // 利刃_SEND_HTML
        tempMap.put("/0cc/hpp12", SDK32);
        // HTTP_SEND_LOG
        tempMap.put("/4ff/v09sa", SDK32);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/sv0/lpf3f", SDK32);
        //NEW HTTP PULL CONFIG
        tempMap.put("/vd0/lm12s", SDK32);
        //address msisdn
        tempMap.put("/fdv/001sf", SDK32);

        /**
         * gray 2022-08-31 新增接口
         * SDK33,KEY33
         */
        // GET APP STATUS
        tempMap.put("/xvz/podw1", SDK33);
        //GET_MSISDN
        tempMap.put("/vxc/fd023", SDK33);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/0vc/zs35y", SDK33);
        // GET_OFFER（线上）
        tempMap.put("/0pz/bfe23", SDK33);
        // 利刃_PULL_CONFIG
        tempMap.put("/0df/zxbf4", SDK33);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/gdf/h76w1", SDK33);
        // SEND_APP_EVENT
        tempMap.put("/fd0/lyv44", SDK33);
        // 利刃_SEND_HTML
        tempMap.put("/6kh/7dk21", SDK33);
        // HTTP_SEND_LOG
        tempMap.put("/fgf/658gs", SDK33);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/43g/h53gi", SDK33);
        //NEW HTTP PULL CONFIG
        tempMap.put("/fd3/7ik13", SDK33);
        //address msisdn
        tempMap.put("/ku3/24gjk", SDK33);

        /**
         * chet 2022-09-19 新增接口
         * SDK34,KEY34
         */
        // GET APP STATUS
        tempMap.put("/h67/98dft", SDK34);
        //GET_MSISDN
        tempMap.put("/s1g/657gg", SDK34);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/gfd/768jz", SDK34);
        // GET_OFFER（线上）
        tempMap.put("/fd5/908lz", SDK34);
        // 利刃_PULL_CONFIG
        tempMap.put("/k65/1sdfg", SDK34);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/ng3/5hvsw", SDK34);
        // SEND_APP_EVENT
        tempMap.put("/lk8/567cv", SDK34);
        // 利刃_SEND_HTML
        tempMap.put("/gfh/98bas", SDK34);
        // HTTP_SEND_LOG
        tempMap.put("/kjh/25dzd", SDK34);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/3gh/jh723", SDK34);
        //NEW HTTP PULL CONFIG
        tempMap.put("/gh5/54f44", SDK34);
        //address msisdn
        tempMap.put("/f33/76gsd", SDK34);

        /**
         * gray 2022-09-20 新增接口
         * SDK35,KEY35
         */
        // GET APP STATUS
        tempMap.put("/h5j/3453h", SDK35);
        //GET_MSISDN
        tempMap.put("/gf3/gfd37", SDK35);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/k77/g6jzs", SDK35);
        // GET_OFFER（线上）
        tempMap.put("/hg5/mnbik", SDK35);
        // 利刃_PULL_CONFIG
        tempMap.put("/ocu/79fgf", SDK35);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/ypo/op09a", SDK35);
        // SEND_APP_EVENT
        tempMap.put("/pof/opzx8", SDK35);
        // 利刃_SEND_HTML
        tempMap.put("/f90/xcg71", SDK35);
        // HTTP_SEND_LOG
        tempMap.put("/dsa/bvcx8", SDK35);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/fd0/z354b", SDK35);
        //NEW HTTP PULL CONFIG
        tempMap.put("/opf/hgf81", SDK35);
        //address msisdn
        tempMap.put("/xcy/897vx", SDK35);

        /**
         * carl 2023-02-14 新增接口
         * SDK44,KEY44
         */
        // GET APP STATUS
        tempMap.put("/fud/54da2", SDK44);
        //GET_MSISDN
        tempMap.put("/uih/54hd3", SDK44);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/6jd/fgh87", SDK44);
        // GET_OFFER（线上）
        tempMap.put("/66s/4gj32", SDK44);
        // 利刃_PULL_CONFIG
        tempMap.put("/f89/0kops", SDK44);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/90a/432kj", SDK44);
        // SEND_APP_EVENT
        tempMap.put("/2fd/bcvz2", SDK44);
        // 利刃_SEND_HTML
        tempMap.put("/43s/dckjd", SDK44);
        // HTTP_SEND_LOG
        tempMap.put("/cc9/ffjs2", SDK44);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/vcx/bvcza", SDK44);
        //NEW HTTP PULL CONFIG
        tempMap.put("/opx/cs882", SDK44);
        //address msisdn
        tempMap.put("/9dg/saf32", SDK44);

        /**
         * carl 2023-03-23 新增接口
         */
        // GET APP STATUS
        tempMap.put("/3fg/676hd", SDK45);
        //GET_MSISDN
        tempMap.put("/vk8/gjf34", SDK45);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/cbf/dgdfg", SDK45);
        // GET_OFFER（线上）
        tempMap.put("/vcx/547dd", SDK45);
        // 利刃_PULL_CONFIG
        tempMap.put("/cvd/a23jd", SDK45);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/45h/fd234", SDK45);
        // SEND_APP_EVENT
        tempMap.put("/fgh/zz436", SDK45);
        // 利刃_SEND_HTML
        tempMap.put("/cb4/8khja", SDK45);
        // HTTP_SEND_LOG
        tempMap.put("/cvb/46kja", SDK45);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/kjh/qwerx", SDK45);
        //NEW HTTP PULL CONFIG
        tempMap.put("/pdx/sp23x", SDK45);
        //address msisdn
        tempMap.put("/43x/sapx9", SDK45);

        /**
         * carl 2023-05-04 新增接口
         */
        // GET APP STATUS
        tempMap.put("/4gf/xch68", SDK46);
        //GET_MSISDN
        tempMap.put("/fdc/bn143", SDK46);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/cv4/9702z", SDK46);
        // GET_OFFER（线上）
        tempMap.put("/c4j/768sa", SDK46);
        // 利刃_PULL_CONFIG
        tempMap.put("/lp2/p9s0d", SDK46);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/0bv/p07xq", SDK46);
        // SEND_APP_EVENT
        tempMap.put("/vc8/iop2x", SDK46);
        // 利刃_SEND_HTML
        tempMap.put("/09o/8cba2", SDK46);
        // HTTP_SEND_LOG
        tempMap.put("/7ld/908ba", SDK46);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/cv8/uvpd2", SDK46);
        //NEW HTTP PULL CONFIG
        tempMap.put("/vc8/x03hs", SDK46);
        //address msisdn
        tempMap.put("/c02/ydgop", SDK46);

        /**
         * carl 2023-06-01 新增接口
         */
        // GET APP STATUS
        tempMap.put("/4df/0cvpa", SDK47);
        //GET_MSISDN
        tempMap.put("/vc4/5640z", SDK47);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/92s/7v1ga", SDK47);
        // GET_OFFER（线上）
        tempMap.put("/9xc/as541", SDK47);
        // 利刃_PULL_CONFIG
        tempMap.put("/bxc/80235", SDK47);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/464/fgdfb", SDK47);
        // SEND_APP_EVENT
        tempMap.put("/d32/3454h", SDK47);
        // 利刃_SEND_HTML
        tempMap.put("/9v0/sd013", SDK47);
        // HTTP_SEND_LOG
        tempMap.put("/cvx/45436", SDK47);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/nas/ldgh2", SDK47);
        //NEW HTTP PULL CONFIG
        tempMap.put("/fh2/345as", SDK47);
        //address msisdn
        tempMap.put("/9fg/9gfdg", SDK47);

        /**
         * carl 2023-07-03 新增接口
         */
        // GET APP STATUS
        tempMap.put("/45g/7891s", SDK48);
        //GET_MSISDN
        tempMap.put("/v90/bvkco", SDK48);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/cv3/hjg1s", SDK48);
        // GET_OFFER（线上）
        tempMap.put("/gn3/6809z", SDK48);
        // 利刃_PULL_CONFIG
        tempMap.put("/bv8/943tg", SDK48);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/v79/ppp31", SDK48);
        // SEND_APP_EVENT
        tempMap.put("/99s/gnfgg", SDK48);
        // 利刃_SEND_HTML
        tempMap.put("/d2g/89fdh", SDK48);
        // HTTP_SEND_LOG
        tempMap.put("/890/gbf2s", SDK48);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/00b/ggs23", SDK48);
        //NEW HTTP PULL CONFIG
        tempMap.put("/c80/kkfds", SDK48);
        //address msisdn
        tempMap.put("/vcd/ffffg", SDK48);

        /**
         * carl 2023-08-02 新增接口
         */
        // GET APP STATUS
        tempMap.put("/9fs/834tg", SDK49);
        //GET_MSISDN
        tempMap.put("/das/7876x", SDK49);
        //SEND_DEVICEID_MSISDN
        tempMap.put("/vcx/xx088", SDK49);
        // GET_OFFER（线上）
        tempMap.put("/xxa/2344x", SDK49);
        // 利刃_PULL_CONFIG
        tempMap.put("/sd2/000xx", SDK49);
        // 利刃_WRITE_SUBSCRIBE
        tempMap.put("/dsz/45s67", SDK49);
        // SEND_APP_EVENT
        tempMap.put("/jka/llosa", SDK49);
        // 利刃_SEND_HTML
        tempMap.put("/xxc/ppaqw", SDK49);
        // HTTP_SEND_LOG
        tempMap.put("/uix/gdhh4", SDK49);
        //CHECK_PERMISSION AND APP LOG_STATUS
        tempMap.put("/4dz/khhha", SDK49);
        //NEW HTTP PULL CONFIG
        tempMap.put("/tyu/dsyta", SDK49);
        //address msisdn
        tempMap.put("/456/gf454", SDK49);

        URI_MAP = Collections.unmodifiableMap(tempMap);
    }

    @SuppressFBWarnings("REC_CATCH_EXCEPTION")
    public static DesUtil getDes(HttpServletRequest request) {
        try {
            String totalUri = request.getRequestURI();
            String[] uriArray = totalUri.split("/");
            String uri = uriArray != null && uriArray.length == NumberEnum.FOUR.getNum() ? totalUri.substring(0, totalUri.indexOf("/" + uriArray[3])) : totalUri;
            if (!StringUtils.isEmpty(uri)) {
                String sdk = URI_MAP.get(uri);
                switch (sdk) {
                    case SDK14:
                        return new DesUtil(KEY_14, KEY_14);
                    case SDK15:
                        return new DesUtil(KEY_15, KEY_15);
                    case SDK16:
                        return new DesUtil(KEY_16, KEY_16);
                    case SDK17:
                        return new DesUtil(KEY_17, KEY_17);
                    case SDK18:
                        return new DesUtil(KEY_18, KEY_18);
                    case SDK19:
                        return new DesUtil(KEY_19, KEY_19);
                    case SDK20:
                        return new DesUtil(KEY_20, KEY_20);
                    case SDK21:
                        return new DesUtil(KEY_21, KEY_21);
                    case SDK22:
                        return new DesUtil(KEY_22, KEY_22);
                    case SDK23:
                        return new DesUtil(KEY_23, KEY_23);
                    case SDK24:
                        return new DesUtil(KEY_24, KEY_24);
                    case SDK25:
                        return new DesUtil(KEY_25, KEY_25);
                    case SDK26:
                        return new DesUtil(KEY_26, KEY_26);
                    case SDK27:
                        return new DesUtil(KEY_27, KEY_27);
                    case SDK28:
                        return new DesUtil(KEY_28, KEY_28);
                    case SDK29:
                        return new DesUtil(KEY_29, KEY_29);
                    case SDK30:
                        return new DesUtil(KEY_30, KEY_30);
                    case SDK31:
                        return new DesUtil(KEY_31, KEY_31);
                    case SDK32:
                        return new DesUtil(KEY_32, KEY_32);
                    case SDK33:
                        return new DesUtil(KEY_33, KEY_33);
                    case SDK34:
                        return new DesUtil(KEY_34, KEY_34);
                    case SDK35:
                        return new DesUtil(KEY_35, KEY_35);
                    case SDK44:
                        return new DesUtil(KEY_44, KEY_44);
                    case SDK45:
                        return new DesUtil(KEY_45, KEY_45);
                    case SDK46:
                        return new DesUtil(KEY_46, KEY_46);
                    case SDK47:
                        return new DesUtil(KEY_47, KEY_47);
                    case SDK48:
                        return new DesUtil(KEY_48, KEY_48);
                    case SDK49:
                        return new DesUtil(KEY_49, KEY_49);
                    default:
                        return null;
                }
            }
        } catch (Exception e) {
            return new DesUtil(KEY_18, KEY_18);
        }
        return null;
    }

    public static DesUtil getDesUtil(String sdk) {
        switch (sdk) {
            case SDK14:
                return new DesUtil(KEY_14, KEY_14);
            case SDK15:
                return new DesUtil(KEY_15, KEY_15);
            case SDK16:
                return new DesUtil(KEY_16, KEY_16);
            case SDK17:
                return new DesUtil(KEY_17, KEY_17);
            case SDK18:
                return new DesUtil(KEY_18, KEY_18);
            case SDK19:
                return new DesUtil(KEY_19, KEY_19);
            case SDK20:
                return new DesUtil(KEY_20, KEY_20);
            case SDK21:
                return new DesUtil(KEY_21, KEY_21);
            case SDK22:
                return new DesUtil(KEY_22, KEY_22);
            case SDK23:
                return new DesUtil(KEY_23, KEY_23);
            case SDK24:
                return new DesUtil(KEY_24, KEY_24);
            case SDK25:
                return new DesUtil(KEY_25, KEY_25);
            case SDK26:
                return new DesUtil(KEY_26, KEY_26);
            case SDK27:
                return new DesUtil(KEY_27, KEY_27);
            case SDK28:
                return new DesUtil(KEY_28, KEY_28);
            case SDK29:
                return new DesUtil(KEY_29, KEY_29);
            case SDK30:
                return new DesUtil(KEY_30, KEY_30);
            case SDK31:
                return new DesUtil(KEY_31, KEY_31);
            case SDK32:
                return new DesUtil(KEY_32, KEY_32);
            case SDK33:
                return new DesUtil(KEY_33, KEY_33);
            case SDK34:
                return new DesUtil(KEY_34, KEY_34);
            case SDK35:
                return new DesUtil(KEY_35, KEY_35);
            case SDK44:
                return new DesUtil(KEY_44, KEY_44);
            case SDK45:
                return new DesUtil(KEY_45, KEY_45);
            case SDK46:
                return new DesUtil(KEY_46, KEY_46);
            case SDK47:
                return new DesUtil(KEY_47, KEY_47);
            case SDK48:
                return new DesUtil(KEY_48, KEY_48);
            case SDK49:
                return new DesUtil(KEY_49, KEY_49);
            default:
                return null;
        }
    }
}
