var operatorUtil = {
    //把时间转化为字符串
    getOperatorNameByCountry: function(country){
        var operators;
        if (country == "Thailand") {
            operators = {"TH_TRUEMOVE":"52099|52004|52000|52088","TH_DTAC":"52018|52005","TH_AIS":"52001|52003|52023"};
        } else if (country == "Malysia") {
            operators = {"MY_CELCOM":"50219|50213|502156","MY_DIGI":"50216|50210|502155","MY_UMOBILE":"50218|502157","MY_MAXIS":"50212|50217","MY_TUNETALK":"50219|502145|502150","MY_XOX":"50299|502195"};
        } else if (country == "SouthAfrica") {
            operators = {"ZA_VODACOM":"65501","ZA_CELLC":"65507","ZA_MTN":"65510|65512","ZA_TELKOM":"65502"};
        } else if (country == "UnitedArabEmirates") {
            operators = {"AE_DU":"42403","AE_ETISALAT":"42402"};
        } else if (country == "Poland") {
            operators = {"PL_PLAY":"26007|26098|26006","PL_PLUS":"26001","PL_TMOBILE":"26034|26002","PL_ORANGE":"26005|26003"};
        } else if (country == "Vietnam") {
            operators = {"VN_GTEL":"45207","VN_MOBIFONE":"45201","VN_VIETNAMMOBILE":"45205","VN_VIETTEL":"45204","VN_VINAPHONE":"45202"};
        } else if (country == "Indonesia") {
            operators = {"ID_INDOSAT":"51001|51021","ID_EXCELCOM":"51011","ID_TELKOMSEL":"51010"};
        } else if (country == "Ghana") {
            operators = {"GH_MTN":"62001","GH_VODAFONE":"62002","GH_AIRTEL":"62006"};
        } else if (country == "HongKong") {
            operators = {"HK_PEOPLES":"54F421","HK_HUTCHISON":"54F440","HK_VODAFONE":"54F460"};
        } else if (country == "China") {
            operators = {"CN_CUCC":"46001|46006","CN_CMCC":"46007|46002|46000","CN_CT":"46003|46005"};
        } else if (country == "India") {
            operators = {"IN_VODAFONE":"40403|405754|40420|40405|40415|405755|40401|405756|40411|40427|405751|40486|40430|4055750|40460|405742|40413|40488|40446|405753|40484|40566","IN_AIRCEL":"40414","IN_AIRTEL":"404450|40495|40470|40403|40402|40451|40456|40440|40431|40445|40454|40410|40490|40496|40494|40453|40452|40449|40492|40498|40416|40455|40497|40493","IN_BSNL":"40471|40481|40454|40472|40480|40466|40453|40457|40476|40438|40459|40462|40451|40458|40464|40473|40455|40434|40474","IN_DOCOMO":"405042|405032|405047|405027|405041|405045|405025|405037|405033|405044|405034|405043|405026|405031|405046|405038|404030|404036|405035|405029|405039","IN_IDEA":"40444|40487|40456|40419|405911|40412|405852|405853|405847|40414|405909|40404|405846|40407|405848|405799|405849|405845|40570|405850|40422|405908|40424|405910|405851","IN_UNINOR":"405880|405818|405927|405819|405875|405822|405844|405821|405820|405929"};
        } else if (country == "Britain") {
            operators = {"GB_O2":"23410|23411|23402","GB_TMOBILE":"23430","GB_VODAFONE":"23415","GB_THREE":"23420","GB_ORANGE":"23433|23434"};
        } else if (country == "Serbia") {
            operators = {"RS_MTS":"22003","RS_TELENOR":"22001","RS_VIP":"22005"};
        } else if (country == "Switzerland"){
            operators = {"CH_SUNRISE":"22802","CH_SWISSCOM":"22801","CH_SALT":"22803"};
        } else if (country == "Netherlands"){
            operators = {"NL_VODAFONE":"20404","NL_TELE2":"20402","NL_KPN":"20408|20469|20410","NL_TMOBILE":"20416|20420","NL_TELEENA":"20407","NL_TELFORT":"20412","BL_PROXIMUS":20601,"BL_MOBISTAR":20610};
        } else if (country == "Singapore"){
            operators = {"SG_SINGTEL":"52501","SG_MOBILEONE":"52503","SG_STARHUB":"52505"};
        } else if (country == "Australia"){
            operators = {"AU_OPTUS":"50502|50590","AU_TELSTRA":"50501|50571|50572"};
        } else if (country == "France"){
            operators = {"FR_BOUYGUES":"20821|20888|20820","FR_FREE":"20815|20816","FR_ORANGE":"20800|20801|20802","FR_SFR":"20810|20811|20813"};
        } else if (country == "German"){
            operators = {"DE_EPLUS":"26205|26203|26277","DE_O2":"26207","DE_TMOBILE":"26201","DE_VODAFONE":"26202","DE_MOBILCOM":"26213"};
        } else if (country == "Italy"){
            operators = {"IT_TIM":"22201","IT_VODAFONE":"22210","IT_WIND":"22288"};
        } else if (country == "Belgium"){
            operators = {"BE_BASE":"20620","BE_MOBISTAR":"20610","BE_PROXIMUS":"20601","BE_TELENET":"20605"};
        } else if (country == "Austria"){
            operators = {"AT_A1":"23201|23211","AT_H3G":"23205|23210","AT_TMOBILE":"23207|23203"};
        } else if (country == "Portugal"){
            operators = {"PT_OPTIMUS":"26803","PT_VODAFONE":"26801","PT_MEO":"26806|26802|26880|2682|2686","PT_NOS":"26803|2683"};
        } else if (country == "Spain"){
            operators = {"ES_MOVISTAR":"21405|21407","ES_ORANGE":"21403|21409|214035","ES_VODAFONE":"21401|214011|21406","ES_YOIGO":"21404"};
        } else if (country == "Turkey"){
            operators = {"TR_AVEA":"28604|28603","TR_TURKCELL":"28601","TR_VODAFONE":"28602"};
        } else if (country == "Czech Republic"){
            operators = {"CZ_O2":"23002","CZ_TMOBILE":"23001","CZ_VODAFONE":"23003"};
        } else if (country == "Brazil"){
            operators = {"BR_CLARO":"72405","BR_OI":"72416|72431","BR_TIM":"72402|72403|72404|72408","BR_VIVO":"72406|72410|72411|72423"};
        } else if (country == "Argentina"){
            operators = {"AR_CLARO":"722320|722310|722330","AR_MOVISTAR":"722010","AR_PERSONAL":"722340"};
        } else if (country == "Saudi"){
            operators = {"SA_MOBILY":"42003","SA_STC":"42001","SA_ZAIN":"42004"};
        } else if (country == "Spain"){
            operators = {"ES_TELEFONICA":"21405|21407","ES_ORANGE":"21403|21409|214035","ES_VODAFONE":"21401|214011|21406","ES_YOIGO":"21404"};
        } else if (country == "Russia"){
            operators = {"RU_AKOS":"25092","RU_BAIKAL":"25012","RU_BEELINE":"25099","RU_ETK":"25005","RU_MEGAFON":"25002|25030|25014"
                ,"RU_MOTIV":"25035","RU_MTS":"25001","RU_NCC":"25003","RU_NTK":"25016","RU_ROSTELECOM":"25039","RU_DALSVAJZ":"25022"
                ,"RU_ELAJN":"25031","RU_SKAJLINK":"25006","RU_UTEL":"25017","RU_SMARTS":"25007","RU_TELE2":"2509|25020|25047"};
        } else if (country == "Mexico"){
            operators = {"MX_IUSACELL":"33405|334050|334040","MX_MOVISTAR":"33403|334030","MX_NEXTEL":"33401","MX_TELCEL":"33402|334020"};
        } else if (country == "Pakistan"){
            operators = {"PK_MOBILINK":"41001","PK_TELENOR":"41006","PK_UFONE":"41003","PK_WARID":"41007","PK_ZONG":"41004"};
        } else if (country == "Hungary"){
            operators = {"HU_TELENOR":"21601","HU_TMOBILE":"21630","HU_VODAFONE":"21670", "HU_UPC":"21671"};
        } else if (country == "Greece"){
            operators = {"GR_COSMOTE":"20201|20203","GR_VODAFONE":"20205","GR_WIND":"20209|20210"};
        } else if (country == "Kenya"){
            operators = {"KE_AIRTEL":"63903|63905","KE_ORANGE":"63907","KE_SAFARICOM":"63902"};
        } else if (country == "Croatia"){
            operators = {"HR_TMOBILE":"21901","HR_TELE2":"21902","HR_VIP":"21910"};
        } else if (country == "Norway"){
            operators = {"NO_NETCOM":"24202","NO_NETWORK":"24205","NO_TDC":"24208", "NO_TELE2":"24204", "NO_TELENOR":"24201", "NO_VENTELO":"24207"};
        } else if (country == "Romania"){
            operators = {"RO_ORANGE":"22610","RO_VODAFONE":"22601", "RO_TELECOM":"22603"};
        } else if (country == "Slovakia"){
            operators = {"SK_O2":"23106","SK_ORANGE":"23101","SK_TMOBILE":"23102", "SK_SWAN":"23103"};
        } else if (country == "Slovenia"){
            operators = {"SI_TELEKOM":"29341","SI_MOBITELBI":"29399","SI_MONETA":"29390", "SI_TELEMACH":"29370", "SI_SIMOBIL":"29340"};
        } else if (country == "Finland"){
            operators = {"FI_DNA":"24412|24403","FI_ELISA":"24405","FI_SAUNALAHTI":"24421", "FI_SONERA":"24491"};
        } else if (country == "Egypt"){
            operators = {"EG_MOBINIL":"60201","EG_VODAFONE":"60202","EG_ETISALAT":"60203"};
        }else if(country=="Sri Lanka"){
            operators={"LK_DIALOG":"41302"};
        }

        return operators;
    },

    //把时间转化为字符串
    getOperatorNameByCountryCode: function(country){
        var operators;
        if (country == "th") {
            operators = {"TH_TRUEMOVE":"52099|52004|52000|52088","TH_DTAC":"52018|52005","TH_AIS":"52001|52003|52023"};
        } else if (country == "my") {
            operators = {"MY_CELCOM":"50219|50213|502156","MY_DIGI":"50216|50210|502155","MY_UMOBILE":"50218|502157","MY_MAXIS":"50212|50217","MY_TUNETALK":"50219|502145|502150","MY_XOX":"50299|502195"};
        } else if (country == "za") {
            operators = {"ZA_VODACOM":"65501","ZA_CELLC":"65507","ZA_MTN":"65510|65512","ZA_TELKOM":"65502"};
        } else if (country == "ae") {
            operators = {"AE_DU":"42403","AE_ETISALAT":"42402"};
        } else if (country == "pl") {
            operators = {"PL_PLAY":"26007|26098|26006","PL_PLUS":"26001","PL_TMOBILE":"26034|26002","PL_ORANGE":"26005|26003"};
        } else if (country == "vn") {
            operators = {"VN_MOBIFONE":"45201","VN_VINAPHONE":"45202","VN_VIETTEL":"45204","VN_VIETNAMMOBILE":"45205","VN_GTEL":"45207"};
        } else if (country == "id") {
            operators = {"ID_INDOSAT":"51001|51021","ID_EXCELCOM":"51011","ID_TELKOMSEL":"51010"};
        } else if (country == "gh") {
            operators = {"GH_MTN":"62001","GH_VODAFONE":"62002","GH_AIRTEL":"62006"};
        } else if (country == "hk") {
            operators = {"HK_PEOPLES":"54F421","HK_HUTCHISON":"54F440","HK_VODAFONE":"54F460"};
        } else if (country == "cn") {
            operators = {"CN_CUCC":"46001|46006","CN_CMCC":"46007|46002|46000","CN_CT":"46003|46005"};
        } else if (country == "in") {
            operators = {"IN_VODAFONE":"40403","IN_AIRCEL":"40414"};
        } else if (country == "gb") {
            operators = {"GB_O2":"23410|23411|23402","GB_TMOBILE":"23430","GB_VODAFONE":"23415","GB_THREE":"23420","GB_ORANGE":"23433|23434"};
        } else if (country == "rs") {
            operators = {"RS_MTS":"22003","RS_TELENOR":"22001","RS_VIP":"22005"};
        } else if (country == "ch") {
            operators = {"CH_SUNRISE":"22802","CH_SWISSCOM":"22801","CH_SALT":"22803"};
        } else if (country == "nl") {
            operators = {"NL_VODAFONE":"20404","NL_TELE2":"20402","NL_KPN":"20408|20469|20410","NL_TMOBILE":"20416|20420","NL_TELEENA":"20407","NL_TELFORT":"20412","NL_PROXIMUS":20601,"NL_MOBISTAR":20610};
        } else if (country == "au"){
            operators = {"AU_OPTUS":"50502|50590","AU_TELSTRA":"50501|50571|50572"};
        } else if (country == "sg"){
            operators = {"SG_MOBILEONE":"52503","SG_SINGTEL":"52501","SG_STARHUB":"52505"};
        } else if (country == "fr"){
            operators = {"FR_BOUYGUES":"20821|20888|20820","FR_FREE":"20815|20816","FR_ORANGE":"20800|20801|20802","FR_SFR":"20810|20811|20813"};
        } else if (country == "de"){
            operators = {"DE_EPLUS":"26205|26203|26277","DE_O2":"26207","DE_TMOBILE":"26201","DE_VODAFONE":"26202","DE_MOBILCOM":"26213"};
        } else if (country == "it"){
            operators = {"IT_TIM":"22201","IT_VODAFONE":"22210","IT_WIND":"22288"};
        } else if (country == "be"){
            operators = {"BE_BASE":"20620","BE_MOBISTAR":"20610","BE_PROXIMUS":"20601","BE_TELENET":"20605"};
        } else if (country == "at"){
            operators = {"AT_A1":"23201|23211","AT_H3G":"23205|23210","AT_TMOBILE":"23207|23203"};
        } else if (country == "pt"){
            operators = {"PT_OPTIMUS":"26803","PT_VODAFONE":"26801","PT_MEO":"26806|26802|26880|2682|2686","PT_NOS":"26803|2683"};
        } else if (country == "es"){
            operators = {"ES_MOVISTAR":"21405|21407","ES_ORANGE":"21403|21409|214035","ES_VODAFONE":"21401|214011|21406","ES_YOIGO":"21404"};
        } else if (country == "tr"){
            operators = {"TR_AVEA":"28604|28603","TR_TURKCELL":"28601","TR_VODAFONE":"28602"};
        } else if (country == "cz"){
            operators = {"CZ_O2":"23002","CZ_TMOBILE":"23001","CZ_VODAFONE":"28603"};
        } else if (country == "br"){
            operators = {"BR_CLARO":"72405","BR_OI":"72416|72431","BR_TIM":"72402|72403|72404|72408","BR_VIVO":"72406|72410|72411|72423"};
        } else if (country == "ar"){
            operators = {"AR_CLARO":"722320|722310|722330","AR_MOVISTAR":"722010","AR_PERSONAL":"722340"};
        } else if (country == "sa"){
            operators = {"SA_MOBILY":"42003","SA_STC":"42001","SA_ZAIN":"42004"};
        } else if (country == "es"){
            operators = {"ES_TELEFONICA":"21405|21407","ES_ORANGE":"21403|21409|214035","ES_VODAFONE":"21401|214011|21406","ES_YOIGO":"21404"};
        } else if (country == "ru"){
            operators = {"RU_AKOS":"25092","RU_BAIKAL":"25012","RU_BEELINE":"25099","RU_ETK":"25005","RU_MEGAFON":"25002|25030|25014"
                ,"RU_MOTIV":"25035","RU_MTS":"25001","RU_NCC":"25003","RU_NTK":"25016","RU_ROSTELECOM":"25039","RU_DALSVAJZ":"25022"
                ,"RU_ELAJN":"25031","RU_SKAJLINK":"25006","RU_UTEL":"25017","RU_SMARTS":"25007","RU_TELE2":"2509|25020|25047"};
        } else if (country == "mx"){
            operators = {"MX_IUSACELL":"33405|334050|334040","MX_MOVISTAR":"33403|334030","MX_NEXTEL":"33401","MX_TELCEL":"33402|334020"};
        } else if (country == "pk"){
            operators = {"PK_MOBILINK":"41001","PK_TELENOR":"41006","PK_UFONE":"41003","PK_WARID":"41007","PK_ZONG":"41004"};
        } else if (country == "hu"){
            operators = {"HU_TELENOR":"21601","HU_TMOBILE":"21630","HU_VODAFONE":"21670", "HU_UPC":"21671"};
        } else if (country == "gr"){
            operators = {"GR_COSMOTE":"20201|20203","GR_VODAFONE":"20205","GR_WIND":"20209|20210"};
        } else if (country == "ke"){
            operators = {"KE_AIRTEL":"63903|63905","KE_ORANGE":"63907","KE_SAFARICOM":"63902"};
        } else if (country == "hr"){
            operators = {"HR_TMOBILE":"21901","HR_TELE2":"21902","HR_VIP":"21910"};
        } else if (country == "no"){
            operators = {"NO_NETCOM":"24202","NO_NETWORK":"24205","NO_TDC":"24208", "NO_TELE2":"24204", "NO_TELENOR":"24201", "NO_VENTELO":"24207"};
        } else if (country == "ro"){
            operators = {"RO_ORANGE":"22610","RO_VODAFONE":"22601", "RO_TELECOM":"22603"};
        } else if (country == "sk"){
            operators = {"SK_O2":"23106","SK_ORANGE":"23101","SK_TMOBILE":"23102", "SK_SWAN":"23103"};
        } else if (country == "si"){
            operators = {"SI_TELEKOM":"29341","SI_MOBITELBI":"29399","SI_MONETA":"29390", "SI_TELEMACH":"29370", "SI_SIMOBIL":"29340"};
        } else if (country == "fi"){
            operators = {"FI_DNA":"24412|24403","FI_ELISA":"24405","FI_SAUNALAHTI":"24421", "FI_SONERA":"24491"};
        } else if (country == "eg"){
            operators = {"EG_MOBINIL":"60201","EG_VODAFONE":"60202","EG_ETISALAT":"60203"};
        }else if(country=="lk"){
            operators={"LK_DIALOG":"41302"};
        }else if(country=="bd"){
            operators={"BD_ROBI":"47002"};
        }else if(country == 'qa') {
            operators={"QA_OOREDOO":"42701", "QA_VODAFONE":"42702"}
        }

        return operators;
    },

    getMccByCountry : function(country){
        var mcc = "";
        if (country == "Thailand") {
            mcc = "520";
        } else if (country == "Malysia") {
            mcc = "502";
        } else if (country == "SouthAfrica") {
            mcc = "655";
        } else if (country == "UnitedArabEmirates") {
            mcc = "424";
        } else if (country == "Poland") {
            mcc = "260";
        } else if (country == "Vietnam") {
            mcc = "452";
        } else if (country == "Indonesia") {
            mcc = "510";
        } else if (country == "Ghana") {
            mcc = "620";
        } else if (country == "China") {
            mcc = "460";
        } else if (country == "HongKong") {
            mcc = "454";
        } else if (country == "India") {
            mcc = "404";
        } else if (country == "Britain") {
            mcc = "234";
        } else if (country == "Serbia") {
            mcc = "220";
        } else if (country == "Switzerland") {
            mcc = "228";
        } else if (country == "Netherlands") {
            mcc = "204";
        } else if (country == "Singapore") {
            mcc = "525";
        } else if (country == "Australia") {
            mcc = "505";
        } else if (country == "France") {
            mcc = "208";
        } else if (country == "German") {
            mcc = "262";
        } else if (country == "Italy") {
            mcc = "222";
        } else if (country == "Belgium") {
            mcc = "206";
        } else if (country == "Austria") {
            mcc = "232";
        } else if (country == "Portugal") {
            mcc = "268";
        } else if (country == "Spain") {
            mcc = "214";
        } else if (country == "Turkey") {
            mcc = "286";
        } else if (country == "Czech Republic") {
            mcc = "230";
        } else if (country == "Brazil") {
            mcc = "724";
        } else if (country == "Argentina") {
            mcc = "722";
        } else if (country == "Saudi") {
            mcc = "420";
        } else if (country == "Spain") {
            mcc = "214";
        } else if (country == "Russia") {
            mcc = "250";
        } else if (country == "Mexico") {
            mcc = "334";
        } else if (country == "Pakistan") {
            mcc = "410";
        } else if (country == "Hungary"){
            mcc = "216";
        } else if (country == "Greece"){
            mcc = "202";
        } else if (country == "Kenya"){
            mcc = "639";
        } else if (country == "Croatia"){
            mcc = "219";
        } else if (country == "Norway"){
            mcc = "242";
        } else if (country == "Romania"){
            mcc = "226";
        } else if (country == "Slovakia"){
            mcc = "231";
        } else if (country == "Slovenia"){
            mcc = "293";
        } else if (country == "Finland"){
            mcc = "244";
        } else if (country == "Egypt"){
            mcc = "602";
        }else if(country=="Sri Lanka"){
            mcc="413";
        }else if(country=="Bengal"){
            mcc="470";
        }else if(country == 'Qatar'){
            mcc = "634";
        }
        return mcc;
    },

    getCountryByShort : function(shortCountry){
        var country = "";
        if(shortCountry == "gb"){
            country = "Britain";
        }else if(shortCountry == "ch"){
            country = "Switzerland";
        }else if(shortCountry == "cn"){
            country = "China";
        }else if(shortCountry == "rs"){
            country = "Serbia";
        }else if(shortCountry == "cz"){
            country = "Czech Republic";
        }else if(shortCountry == "in"){
            country = "India";
        }else if(shortCountry == "au"){
            country = "Australia";
        }else if(shortCountry == "de"){
            country = "German";
        }else if(shortCountry == "hu"){
            country = "Hungary";
        }else if(shortCountry == "id"){
            country = "Indonesia";
        }else if(shortCountry == "sa"){
            country = "Saudi";
        }else if(shortCountry == "pt"){
            country = "Portugal";
        }else if(shortCountry == "pl"){
            country = "Poland";
        }else if(shortCountry == "pk"){
            country = "Pakistan";
        }else if(shortCountry == "nl"){
            country = "Netherlands";
        }else if(shortCountry == "sg"){
            country = "Singapore";
        }else if(shortCountry == "it"){
            country = "Italy";
        }else if(shortCountry == "ar"){
            country = "Argentina";
        }else if(shortCountry == "fr"){
            country = "France";
        }else if(shortCountry == "ru"){
            country = "Russia";
        }else if(shortCountry == "tr"){
            country = "Turkey";
        }else if(shortCountry == "es"){
            country = "Spain";
        }else if(shortCountry == "mx"){
            country = "Mexico";
        }else if(shortCountry == "my"){
            country = "Malysia";
        }else if(shortCountry == "at"){
            country = "Austria";
        }else if(shortCountry == "gh"){
            country = "Ghana";
        }else if(shortCountry == "vn"){
            country = "Vietnam";
        }else if(shortCountry == "br"){
            country = "Brazil";
        }else if(shortCountry == "za"){
            country = "SouthAfrica";
        }else if(shortCountry == "be"){
            country = "Belgium";
        }else if(shortCountry == "hk"){
            country = "Hong Kong";
        }else if(shortCountry == "th"){
            country = "Thailand";
        }else if(shortCountry == "ae"){
            country = "UnitedArabEmirates";
        }else if(shortCountry == "gr"){
            country = "Greece";
        }else if(shortCountry == "ke"){
            country = "Kenya";
        }else if(shortCountry == "hr"){
            country = "Croatia";
        }else if(shortCountry == "no"){
            country = "Norway";
        }else if(shortCountry == "ro"){
            country = "Romania";
        }else if(shortCountry == "sk"){
            country = "Slovakia";
        }else if(shortCountry == "si"){
            country = "Slovenia";
        }else if(shortCountry == "fi"){
            country = "Finland";
        }else if(shortCountry == "eg"){
            country = "Egypt";
        }else if(shortCountry=="lk"){
            country = "Sri Lanka";
        }else if(shortCountry=="bd"){
            country = "Bengal";
        } else if(shortCountry == 'qa') {
            country = "Qatar";
        }
        return country;
    }
};