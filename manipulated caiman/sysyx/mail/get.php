<?php
if(file_exists('stoped.txt'))
	die("EXIT;");
	
include_once('../db.php');
if(isset($_POST['emailex']))
{
	$list = explode("\r\n", $_POST['emailex']);
    foreach($list as $row) {
        if(empty($row)) continue;
        $mail = mysqli_real_escape_string($con, $row);
        $query_values[] = "('".$mail."')";
    }
    mysqli_query($con, "INSERT INTO emailex_list (mail) VALUES ".implode(',',$query_values)."");
	@file_put_contents("Mailos.txt", implode("\r\n",$query_values)."\r\n" , FILE_APPEND);
	die("1");
}

$ip = getUserIP();
$tday = date("H:i:s");
$time = microtime(true);

$q = mysqli_query($con, "SELECT * FROM logx_emailex WHERE ip = '$ip'");
if(mysqli_num_rows($q) == 0){
	mysqli_query($con, "INSERT INTO logx_emailex (ip, timelog, counterlog) VALUES ('$ip','$time',0)");
}else{
	$row = mysqli_fetch_assoc($q);
	$clog = $row['counterlog'];
	$oldtime = $row['timelog'];
	$lastvisit = number_format(microtime(true) - $oldtime, 0 , '', '');

	if($lastvisit < 30 || $clog >= 3){
		$clog++;
		mysqli_query($con, "UPDATE logx_emailex SET counterlog=".$clog." WHERE ip='$ip'");
		if($clog >= 3){
			die("EXIT;");
		}
	}
	mysqli_query($con, "UPDATE logx_emailex SET timelog=".$time." WHERE ip='$ip'");
}
$qmain = mysqli_query($con, "DELETE FROM emailex LIMIT 10 RETURNING dork");
if(mysqli_num_rows($qmain) == 0){
	die("EXIT;");
}
while($row=mysqli_fetch_assoc($qmain)){
	echo $row['dork']."\r\n";
}
?>
