<?php
if(file_exists('stoped.txt'))
	die("EXIT;");
	
include_once('../db.php');

$ip = getUserIP();
$tday = date("H:i:s");
$time = microtime(true);

$q = mysqli_query($con, "SELECT * FROM logx WHERE ip = '$ip'");
if(mysqli_num_rows($q) == 0){
	mysqli_query($con, "INSERT INTO logx (ip, timelog, counterlog) VALUES ('$ip','$time',0)");
}else{
	$row = mysqli_fetch_assoc($q);
	$clog = $row['counterlog'];
	$oldtime = $row['timelog'];
	$lastvisit = number_format(microtime(true) - $oldtime, 0 , '', '');

	if($lastvisit < 30 || $clog >= 3){
		$clog++;
		mysqli_query($con, "UPDATE logx SET counterlog=".$clog." WHERE ip='$ip'");
		if($clog >= 3){
			@file_put_contents("banned111.txt", $ip."\r\n", FILE_APPEND);
			die("EXIT;");
		}
	}
	mysqli_query($con, "UPDATE logx SET timelog=".$time." WHERE ip='$ip'");
}

$qmain = mysqli_query($con, "DELETE FROM mails LIMIT 500 RETURNING mail");
if(mysqli_num_rows($qmain) == 0){
	die("EXIT;");
}
while($row=mysqli_fetch_assoc($qmain)){
	echo $row['mail']."\r\n";
}
?>