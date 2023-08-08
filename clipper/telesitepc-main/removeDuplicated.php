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
$conn->set_charset('utf8mb4');

$sql = "DELETE FROM keywords WHERE logid=0";
if ($conn->query($sql) === TRUE) {
   echo "removing 0 is successfully done.\n";
}
else{
   echo "removing 0 is faild.\n" . $conn->error;
}
echo "<br/>";

// $sql = "DELETE FROM keywords WHERE id NOT IN(SELECT t.id FROM (SELECT MIN(id) AS id FROM keywords GROUP BY logid, trigger_key, trigger_isout, trigger_friendname, trigger_groupname) AS t)";

// $sql = "DELETE m
//          FROM keywords m LEFT JOIN
//                (SELECT id
//                FROM keywords
//                GROUP BY logid, trigger_key, trigger_isout, trigger_friendname, trigger_groupname
//                ) mm
//                ON mm.id = m.id
//          WHERE mm.id IS NULL";

// $sql = "DELETE m FROM keywords m JOIN (SELECT id, COUNT(*) as cnt FROM keywords GROUP BY logid, trigger_time, trigger_key, trigger_isout, trigger_friendname, trigger_groupname) mm ON mm.id = m.id WHERE cnt > 1";
$sql = "DELETE m FROM keywords m JOIN (SELECT keywords.id, COUNT(*) as cnt FROM keywords JOIN logs ON keywords.logid=logs.ID  GROUP BY logs.TG_NUMBER, keywords.trigger_key, keywords.trigger_isout, keywords.trigger_friendname, keywords.trigger_groupname) mm ON mm.id = m.id WHERE cnt > 1";

// $stmt = $conn->prepare($sql);
if ($conn->query($sql) === TRUE) {
// if($stmt->execute() === TRUE) {
   echo "removingDuplicated is successfully done.\n";
}
else{
   echo "removing is faild.\n" . $conn->error;
}
echo "<br/>";
$conn->close();

?>
