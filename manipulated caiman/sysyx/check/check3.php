<?php

$host = "localhost";
$dbname = "checkdb";
$dbuser = "check1";
$userpass = "101010a";

$con = pg_connect("host=$host dbname=$dbname user=$dbuser password=$userpass");
if (!$con) die('Could not connect');

if(isset($_POST['check']))
{
   // $checks = explode("\n", str_replace("\r", "", $_POST['check']));
   $check = '('.$_POST['check'].')';
   $check = pg_escape_string(str_replace(array("\r","\n"), "", $check));
    //foreach($checks as $check)
   // {
       // $v = mysqli_real_escape_string($con, $check);
       $r = pg_query($con, 'UPDATE checktb SET status=2,hour=0 WHERE id IN '.$check.';');
                       //die('UPDATE checktb SET status=2,hour=0 WHERE id IN '.$check.';  -->  '.var_dump($r));
                       die("11");
   // }
    
}
echo "1";