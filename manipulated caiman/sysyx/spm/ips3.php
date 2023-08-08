<?php

$host = "localhost";
$dbname = "spdb";
$dbuser = "spuser";
$userpass = "101010a";

$con = pg_connect("host=$host dbname=$dbname user=$dbuser password=$userpass");
if (!$con) die('Could not connect');


$date = time();

$q = pg_query($con, 'select ip, count(*) from sptb group by ip ORDER BY COUNT(*) DESC;');

$s="";
$c = 0;
while($row=pg_fetch_assoc($q)){
    $s .= "'".$row['ip']."'|||".$row['count']."<br>\n";
    $c=$c+$row['count'];
}
echo $c."<br><br><br>";
echo $s;