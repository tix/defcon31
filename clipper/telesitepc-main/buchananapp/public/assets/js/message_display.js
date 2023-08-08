$(function(){
	var mymsg=$("#message").val();
    if(mymsg=="SuccessLogIn"){
    	message(msg.SuccessLogIn,"success");
    }
    if(mymsg=="RepeatedPhonenumber"){
    	message(msg.RepeatedPhonenumber,"warning");
    }    
    if(mymsg=="SuccessSignUp"){
    	message(msg.SuccessSignUp,"success");
    }
    if(mymsg=="ExpiredSubscription"){
    	message(msg.ExpiredSubscription,"warning");
    }
    if(mymsg=="UserNotActived"){
    	message(msg.UserNotActived,"warning");
    }
    if(mymsg.includes("FailedBuyNow")){
        var m = mymsg.split('|');
        message(m[1],"warning");
    }

    if(mymsg=="SuccessSendSMS"){
        message(msg.SuccessSendSMS,"success");
    }
});