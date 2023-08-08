var msg = {
	EmptyName:"Please input your name.",
	EmptyPhone:"Please input username.",
	EmptyPassword:"Please input password.",
	ConfrimPassword:"Please confirm password.",
	FailedLogIn:"Username or password is incorrect.",
	SuccessLogIn:"Welcome!",
	ErrorChangePassword:"Please input password fields correctly!",	
	MisMatchPassword:"Please input your password correctly!",
	SuccessSaveProfile:"You have changed your profile successfully",
	SuccessSavePassword:"You have changed your password successfully",
	FailedSaveProfile:"You have failed",

	EmptyField:"Please input your field.",
	EmptyFields:"Please input your fields.",


	ExpiredSubscription:"Expired Subscription."
};

function message(msgcontent,msgtype){
	if(msgtype=="warning") {msgtitle="Warning"}
	if(msgtype=="success") {msgtitle="Success"}
	if(msgtype=="info") {msgtitle="Info"}
	toastr.options = {
	  "timeOut": 3800,
	  "showDuration": 700,
  	  "hideDuration": 1000
	}
	var $toast = toastr[msgtype](msgcontent, msgtitle);
}