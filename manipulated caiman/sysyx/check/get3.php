<?php

if(file_exists("stoped.txt"))
{
    die("WAIT;");
}
	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
    error_reporting(E_ALL);

function getUserIP()
{
        if( array_key_exists('HTTP_X_FORWARDED_FOR', $_SERVER) && !empty($_SERVER['HTTP_X_FORWARDED_FOR']) ) {
            if (strpos($_SERVER['HTTP_X_FORWARDED_FOR'], ',')>0) {
                $addr = explode(",",$_SERVER['HTTP_X_FORWARDED_FOR']);
                return trim($addr[0]);
            } else {
                return $_SERVER['HTTP_X_FORWARDED_FOR'];
            }
        }
        else {
            return $_SERVER['REMOTE_ADDR'];
        }
}


$ip = getUserIP();

@file_put_contents("ips.txt", $ip."\r\n", FILE_APPEND);

$host = "localhost";
$dbname = "checkdb";
$dbuser = "check1";
$userpass = "101010a";

$con = pg_connect("host=$host dbname=$dbname user=$dbuser password=$userpass");
if (!$con) die('Could not connect');


$date = time();

$q = pg_query($con, 'UPDATE "checktb" SET status = 1, ip=\''.$ip.'\', hour = '.$date.' WHERE id in (SELECT id FROM "checktb" WHERE status = 0 LIMIT 50 FOR UPDATE) RETURNING id,keyword;');
if(pg_num_rows($q) == 0){
	die("WAIT;");
}
$s="";
while($row=pg_fetch_assoc($q)){
	$s .= $row['id']."|||".$row['keyword']."\n";
}
echo $s;
?>