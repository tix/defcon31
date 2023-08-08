<?php

header('Expires: Sun, 01 Jan 2014 00:00:00 GMT');
header('Cache-Control: no-store, no-cache, must-revalidate');
header('Cache-Control: post-check=0, pre-check=0', FALSE);
header('Pragma: no-cache');

ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
ini_set('error_log', "/usr/share/nginx/html/sysy/php_error.log");

$user = "root";
$pass = "123+++xxx";
$name = "sss";

@set_time_limit(0);

$con = mysqli_connect( "127.0.0.1", "root", "123+++xxx");
if (!$con) {
  die("Connection failed: " . mysqli_connect_error());
}

$db_selected = mysqli_select_db($con , $name);

if (!$db_selected)
{
  if (mysqli_query($con, "CREATE DATABASE $name")) {
    //die( "Database created successfully" );
  } else {
    die( "Error creating database: " . mysqli_error($con));
  }
}

/** SPAM */
// mysqli_query($con, "CREATE TABLE IF NOT EXISTS mails ( id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
// 									  mail VARCHAR(256))") or die(mysqli_error($con));

// mysqli_query($con, "CREATE TABLE IF NOT EXISTS logx ( id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
//                     ip VARCHAR(128), 
//                     timelog VARCHAR(128),
//                     counterlog TINYINT)") or die(mysqli_error($con));
/** CHEKER */                   
mysqli_query($con, "CREATE TABLE IF NOT EXISTS checker ( id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
										smtp VARCHAR(512),
                    status TINYINT default 0,
                    ip VARCHAR(128),
                    hour INT(11) default 0)") or die(mysqli_error($con));

mysqli_query($con, "CREATE TABLE IF NOT EXISTS logx_checker ( id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
                    ip VARCHAR(128), 
                    timelog VARCHAR(128),
                    counterlog TINYINT)") or die(mysqli_error($con));
/** SQLIER */
// mysqli_query($con, "CREATE TABLE IF NOT EXISTS sqlier ( id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
//                     dork VARCHAR(512), 
//                     sent bool NOT NULL DEFAULT FALSE)") or die(mysqli_error($con));

// mysqli_query($con, "CREATE TABLE IF NOT EXISTS sqlier_vulns ( id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
//                     urls VARCHAR(512))") or die(mysqli_error($con));                       

// mysqli_query($con, "CREATE TABLE IF NOT EXISTS logx_sqlier ( id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
//                     ip VARCHAR(128), 
//                     timelog VARCHAR(128),
//                     counterlog TINYINT)") or die(mysqli_error($con));
// /** EMAILEX */
// mysqli_query($con, "CREATE TABLE IF NOT EXISTS emailex ( id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
//                     dork VARCHAR(512), 
//                     sent bool NOT NULL DEFAULT FALSE)") or die(mysqli_error($con));

// mysqli_query($con, "CREATE TABLE IF NOT EXISTS emailex_list ( id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
//                     mail VARCHAR(128))") or die(mysqli_error($con));                       

// mysqli_query($con, "CREATE TABLE IF NOT EXISTS logx_emailex ( id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
//                     ip VARCHAR(128), 
//                     timelog VARCHAR(128),
//                     counterlog TINYINT)") or die(mysqli_error($con));

//mysqli_query($con, "ALTER TABLE emailex_list ADD UNIQUE (mail)");
////////////////
function getUserIP() {
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
?>