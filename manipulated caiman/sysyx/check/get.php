<?php
//die("EXIT;");
include_once('../db.php');

$ip = getUserIP();
$date = time();

$qmain = mysqli_query($con, "SELECT * FROM checker WHERE status = 0 LIMIT 100");
if(mysqli_num_rows($qmain) == 0){
	die("WAIT;");
}
while($row=mysqli_fetch_assoc($qmain)){
	echo $row['smtp']."\r\n";
	mysqli_query($con, "UPDATE checker SET status=1, ip='".$ip."', hour='".$date."' WHERE smtp='".$row['smtp']."'");
}
?>