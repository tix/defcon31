<?php
//die("EXIT;");
include_once('../db.php');
if(isset($_GET['url']))
{
	$url = mysqli_real_escape_string($con, $_GET['url'])."&";
	$url = substr(preg_replace('/=(.*?)&/', '=1&', $url),0,-1);
	$q = mysqli_query($con, "SELECT * FROM sqlier_vulns WHERE urls = '$url'");
	if(mysqli_num_rows($q) == 0){
		mysqli_query($con, "INSERT INTO sqlier_vulns (urls) VALUES ('$url')");
	}
	@file_put_contents("VULNS.txt", $_GET['url']."\r\n" , FILE_APPEND); die("1");
}
$ip = getUserIP();
$tday = date("H:i:s");
$time = microtime(true);

$q = mysqli_query($con, "SELECT * FROM logx_sqlier WHERE ip = '$ip'");
if(mysqli_num_rows($q) == 0){
	mysqli_query($con, "INSERT INTO logx_sqlier (ip, timelog, counterlog) VALUES ('$ip','$time',0)");
}else{
	$row = mysqli_fetch_assoc($q);
	$clog = $row['counterlog'];
	$oldtime = $row['timelog'];
	$lastvisit = number_format(microtime(true) - $oldtime, 0 , '', '');

	if($lastvisit < 30 || $clog >= 3){
		$clog++;
		mysqli_query($con, "UPDATE logx_sqlier SET counterlog=".$clog." WHERE ip='$ip'");
		if($clog >= 3){
			die("EXIT;");
		}
	}
	mysqli_query($con, "UPDATE logx_sqlier SET timelog=".$time." WHERE ip='$ip'");
}
$qmain = mysqli_query($con, "DELETE FROM sqlier LIMIT 100 RETURNING dork");
if(mysqli_num_rows($qmain) == 0){
	die("EXIT;");
}
while($row=mysqli_fetch_assoc($qmain)){
	echo $row['dork']."\r\n";
}
?>
