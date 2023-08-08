<?php

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

// $sql = "ALTER TABLE logs DROP RECEIVE_DELAY";
// if ($conn->query($sql) === TRUE) {
//    echo "s is successfully dropped.\n";
// }
// else{
//    echo "s is faild to drop.\n";
// }


$sql = "ALTER TABLE logs ADD DISCONNECTING int(1) DEFAULT 0";
if ($conn->query($sql) === TRUE) {
   echo "d is successfully added.\n";
}
else{
   echo "d is faild to add.\n";
}
echo "<br/>";
$conn->close();

?>
