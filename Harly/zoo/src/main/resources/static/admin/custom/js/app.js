//配置设置
var sysConfig = {
    author: 'kindy',
    version: '0.01',
   
    lang:"cn",
    applicationId: 'mobgkt_1_0_0_en',
    apiUrl: 'http://localhost:8088'
    //apiUrl: 'http://report.mobgkt.com/'
    //apiUrl: 'http://172.30.20.211:8080/statistics'
    //apiUrl: 'http://127.0.0.1:8080/statistics'
};

var dmn = document.domain;
if(dmn.indexOf("mobgkt.com")>0) {//是域名方式
	sysConfig.apiUrl = window.location.protocol+"//"+document.domain;
}else { //ip 方式
    //test
    var  webroot=document.location.href;
    webroot=webroot.substring(webroot.indexOf('//')+2,webroot.length);
    webroot=webroot.substring(webroot.indexOf('/')+1,webroot.length);
    webroot=webroot.substring(0,webroot.indexOf('/'));
    var rootpath = "";
    if(webroot.indexOf("admin")!=0){
    	rootpath="/"+webroot;
    }
    sysConfig.apiUrl = window.location.protocol+"//"+window.location.host+rootpath;
}

//长久存储保存数据
var sysStorage = {
    setParam: function(name, value) {
        localStorage.setItem(name, value);
    },
    getParam: function(name) {
        return localStorage.getItem(name);
    }
};

//同一个会话中的页面才能访问保存数据
var sysSessionStorage = {
    setParam: function(name, value) {
        sessionStorage.setItem(name, value);
    },
    getParam: function(name) {
        return sessionStorage.getItem(name);
    },
    removeItem: function(name){
        sessionStorage.removeItem(name);
    },
    clearItem: function(){
        sessionStorage.clear();
    }
};

//url操作工具
var urlTools = {
	//获取RUL参数值
	getUrlParam: function(name) {
	    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	    var r = window.location.search.substr(1).match(reg);
	    if (r!=null) return unescape(r[2]); return null;
	},
	urlRoute: function(){
		var gotoPage = urlTools.getUrlParam("goto");
		if(gotoPage == null || gotoPage == ""){
			$("#content").html("URL参数不对!");
		}else{
			$("#content").load("tpl/"+gotoPage+".html");
		}
	}
};

// 一些文字操作的工具
var utils = {
	//把时间转化为字符串
	time2String: function(time){
	    var datetime = new Date(time);
	    var year = datetime.getFullYear();
	    var month = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1) : datetime.getMonth() + 1;
	    var date = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
	    //var hour = datetime.getHours()< 10 ? "0" + datetime.getHours() : datetime.getHours();
	    //var minute = datetime.getMinutes()< 10 ? "0" + datetime.getMinutes() : datetime.getMinutes();
	    //var second = datetime.getSeconds()< 10 ? "0" + datetime.getSeconds() : datetime.getSeconds();
	    //return year + "-" + month + "-" + date+" "+hour+":"+minute+":"+second;
	    return year + "-" + month + "-" + date;
	}
};

//用户的常用的一些操作
var userCommon = {
    
};

// 日期format
Date.prototype.format = function(fmt) {
  var o = {
    "M+" : this.getMonth()+1,                 //月份   
    "d+" : this.getDate(),                    //日   
    "H+" : this.getHours(),                   //小时   
    "m+" : this.getMinutes(),                 //分   
    "s+" : this.getSeconds(),                 //秒   
    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
    "S"  : this.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
};
