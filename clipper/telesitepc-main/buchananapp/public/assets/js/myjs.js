var pageName;
$(function(){
	
	var $domain = "http://localhost";
    var assets_path = $("#assets_path").val();
    var base_url = $("#base_url").val();

    pageName = $("#pageName").val();
    var user_username = $("[name='username']").val();
    $(".change-password-btn").click(function(){
    	$(this).css("display","none");
    	$(".change-password").css("display","block");
    });
	
    $(".change-profile").click(function(){

    	$(".change-password-btn").css("display","inline");
    	$(".change-password").css("display","none");
    	var st1=0;
    	var st2=0;
    	var npassword, cpassword;
    	$(".change-password").each(function(){
    		if($(this).find('input').attr('name')=='npassword'){
    			npassword = $(this).find('input').val();
    		}
    		if($(this).find('input').attr('name')=='cpassword'){
    			cpassword = $(this).find('input').val();
    		}
    		if($(this).find('input').attr('name')=='opassword'){
    			opassword = $(this).find('input').val();
    		}
    		if(!$(this).find('input').val().length){
    			st1=1;
    		} else {
    			st2=1;
    		}
    		$(this).find('input').val("");
    	});
    	if(st1==1 && st2==1){
    		message(msg.ErrorChangePassword,"warning");
    		return false;
    	}
    	// alert($("[name='name']").val()+","+$("[name='cpassword']").val())
    	if(npassword!=cpassword){
    		message(msg.MisMatchPassword,"warning");
    		return false;
    	}

	    $.ajax({
            url: "saveProfile",
            type:'get',
            data:{
            	username:$("[name='username']").val(),
            	npassword:npassword,
            	opassword:opassword,
            },
            success: function(result){
            	if(result=="success"){
            		message(msg.SuccessSaveProfile,"success");
            	} else {
            		message(msg.FailedSaveProfile,"warning");
            	}
            }
        });
    });

    $(".modal-dismiss").click(function(){
    	$(".change-password-btn").css("display","inline");
    	$(".change-password").css("display","none");
    	$(".change-password").each(function(){
    		$(this).find('input').val("");
    	});
    	$("[name='username']").val(user_username);
	});
	

});

