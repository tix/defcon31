$(function(){
	var phonenumber = $("input[name='phonenumber']").val();
    var password = $("input[name='password']").val();
    $("#myform").submit(function(){

	    if(!$("input[name='phonenumber']").val()){
	    	message(msg.EmptyPhone,"warning");
	    	return false;
	    }	
	    if(!$("input[name='password']").val()){
	    	message(msg.EmptyPassword,"warning");
	    	return false;
	    }
    });
    if($("#message").val()=="FailedLogIn"){
    	message(msg.FailedLogIn,"warning");
    }
});