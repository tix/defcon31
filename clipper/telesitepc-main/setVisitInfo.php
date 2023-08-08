<?php
header('Content-Type: text/html; charset=utf-8');
if(isset($_GET['IS_PC'])) $is_pc = $_GET['IS_PC'];
else $is_pc = '0';
if($is_pc == 0) $is_pc = '0';
if($is_pc == 1) $is_pc = '1';
function isMobile() {
    return preg_match("/(android|avantgo|blackberry|bolt|boost|cricket|docomo|fone|hiptop|mini|mobi|palm|phone|pie|tablet|up\.browser|up\.link|webos|wos)/i", $_SERVER["HTTP_USER_AGENT"]);
}

if(($is_pc == '0') && (isMobile() == false)) {
	echo "New record updated successfully";
	return;
}

if(($is_pc != '0') && ($is_pc != '1')) {
   echo "New record updated successfully";
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

if(isset($_GET['INSTALL_DATE'])) $install_date = $_GET['INSTALL_DATE'];
else $install_date = '';
if(isset($_GET['INSTALL_COUNTRY'])) $install_country = $_GET['INSTALL_COUNTRY'];
else $install_country = '';
$install_country_cn = '';
if(isset($_GET['IP_ADDRESS'])) $ip_address = $_GET['IP_ADDRESS'];
else $ip_address = '';
if(isset($_GET['DEVICEID'])) $deviceId = $_GET['DEVICEID'];
else $deviceId = '';
if(isset($_GET['PHONENUMBER'])) $phonenumber = $_GET['PHONENUMBER'];
else $phonenumber = '';

if (isset($_GET['INSTALL_DATE']) && (DateTime::createFromFormat('Y-m-d H:i:s', $install_date) == false)) {
  	return;
}
if(isset($_GET['INSTALL_COUNTRY']) && (strlen($install_country) > 60) ) {
	return;
}
if(isset($_GET['IP_ADDRESS']) && (ip2long($ip_address) == false))	{
  	return;
}
if(isset($_GET['DEVICEID']) && strlen($deviceId) > 16 ) {
	return;
}
if(isset($_GET['PHONENUMBER']) && strlen($phonenumber) > 15 ) {
	return;
}

// Edit the four values below
//$PROXY_HOST = "proxy.iproyal.com"; // Proxy server address
//$PROXY_PORT = "12323";    // Proxy server port
//$PROXY_USER = "Kamalsaeed0099";    // Username
//$PROXY_PASS = "Ph13981398_country";   // Password
// Username and Password are required only if your proxy server needs basic authentication

//$auth = base64_encode("$PROXY_USER:$PROXY_PASS");
//stream_context_set_default(
// array(
//  'http' => array(
//   'proxy' => "tcp://$PROXY_HOST:$PROXY_PORT",
//   'request_fulluri' => true,
//   'header' => "Proxy-Authorization: Basic $auth"
   // Remove the 'header' option if proxy authentication is not required
//  )
// )
//);

ini_set("allow_url_fopen", 1);

if(1){ // for public server
   $gotCountry = 0;
   if(strlen($ip_address) > 0)
   {
       //$sql = "SELECT INSTALL_COUNTRY FROM logs WHERE IP_ADDRESS='$ip_address'";
       //$result = $conn->query($sql);
      $stmt = $conn->prepare('SELECT INSTALL_COUNTRY_CN FROM logs WHERE IP_ADDRESS = ? or INSTALL_COUNTRY = ?');
      $stmt->bind_param('ss', $ip_address, $install_country);
      $stmt->execute();
      $result = $stmt->get_result();
      
      if ($result->num_rows > 0) {
   	 	while(($row = $result->fetch_assoc())  && ($gotCountry == 0)) {
   	 	 if(preg_match("/\p{Han}+/u", $row["INSTALL_COUNTRY_CN"])) {
   	 	 	 $install_country_cn = $row["INSTALL_COUNTRY_CN"];
   	 	 	 $gotCountry  = 1;
   	 	 }
   	 	}
      }
      /*
      if($gotCountry == 0) {
	      $url = 'http://api.ipstack.com/'.$ip_address.'?access_key=3ca923e1feab04f6f79a6e1d793cd41c&format=1';
	      $json = file_get_contents($url);
	      $data = json_decode($json);
	      if(isset($data->country_name))
	         $install_country = $data->country_name;

	      if(isset($data->region_name))
	         $install_country = $install_country." ".$data->region_name;

	      if(isset($data->city))
	         $install_country = $install_country." ".$data->city;
	}
	*/
   }
   
   
   if($gotCountry == 0) {
	   $url = 'https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyAQQzUkbjT1wR8lKHZKMdmFSSuO07_6XGA&address='.urlencode($install_country).'&language=zh&sensor=false&result_type=locality';
	   $json = file_get_contents($url);
	   //echo $json;
	   $data = json_decode($json);
	   $status = $data->status;
	   if($status=="OK") {
	   	$install_country_cn = $data->results[0]->formatted_address;
	   } else {
	   	   
	   }
   }
}
else{
   $install_country = "vietname";
}

if($install_country_cn != '') {
   $stmt = $conn->prepare("UPDATE logs SET INSTALL_COUNTRY_CN=? WHERE IP_ADDRESS = ? or INSTALL_COUNTRY = ?");
   $stmt->bind_param('sss', $install_country_cn, $ip_address, $install_country);
   $stmt->execute();
}

//$sql = "SELECT ID FROM logs WHERE INSTALL_DATE='$install_date' and INSTALL_COUNTRY='$install_country'";
//$result = $conn->query($sql);

if($phonenumber != '') $login_status = '1';
else $login_status = '0';

// $stmt = $conn->prepare( "SELECT ID FROM logs WHERE INSTALL_DATE=? and INSTALL_COUNTRY=? and TG_NUMBER=?");
// $stmt->bind_param('sss', $install_date, $install_country, $phonenumber);

$stmt = $conn->prepare( "SELECT ID FROM logs WHERE IP_ADDRESS=? and TG_NUMBER=?");
$stmt->bind_param('ss', $ip_address, $phonenumber);
$stmt->execute();
$result = $stmt->get_result();
if ($result->num_rows > 0) {
   $row = $result->fetch_assoc();
   
   //$sql = "UPDATE logs SET IP_ADDRESS='$ip_address', DEVICEID='$deviceId', updated_at= now() WHERE ID=".$row["ID"];
   $stmt = $conn->prepare("UPDATE logs SET INSTALL_DATE=?,  IP_ADDRESS=?, DEVICEID=?, TG_NUMBER=?, LOGIN_STATUS=?, updated_at= now() WHERE ID=?");
   $stmt->bind_param('ssssss', $install_date, $ip_address, $deviceId, $phonenumber, $login_status, $row["ID"]);
   //if ($conn->query($sql) === TRUE) {
   if($stmt->execute() === TRUE) {
   	//echo "New record updated successfully";
      echo $row["ID"];
   } else {
   	//echo "Update Error: " . $conn->error;
      echo "-1";
   }
} else {
   // $sql = "SELECT SEND_DELAY, RECEIVE_DELAY FROM logs WHERE DEVICEID='$deviceId";
   // $result = $conn->query($sql);
   // if ($result && $result->num_rows > 0) {
   //    $sendDelay = $result["SEND_DELAY"];
   //    $recvDelay = $result["RECEIVE_DELAY"];
   // }
   // else{
   //    $sendDelay = 0;
   //    $recvDelay = 0;
   // }
   $sendDelay = 0;
   $recvDelay = 0;

   $stmt = $conn->prepare("INSERT INTO logs (INSTALL_DATE, INSTALL_COUNTRY_CN, INSTALL_COUNTRY, DEVICEID, IP_ADDRESS, TG_NUMBER, LOGIN_STATUS, SEND_DELAY, RECEIVE_DELAY, created_at, updated_at)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now() );");
   $stmt->bind_param('sssssssii', $install_date, $install_country_cn, $install_country, $deviceId, $ip_address, $phonenumber, $login_status, $sendDelay, $recvDelay);
   //if ($conn->query($sql) === TRUE) {
   if($stmt->execute() === TRUE) {
      //echo "New record created successfully";
      echo $conn->insert_id;
   } else {
      //echo "Insert Error: " . $conn->error;
      echo "-1";
   }
}

$conn->close();
 ?>