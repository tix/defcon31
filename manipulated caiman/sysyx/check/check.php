<?php

//die("EXIT;");
include_once('../db.php');

if(isset($_POST['check']))
{
    $checks = explode("\n", str_replace("\r", "", $_POST['check']));
    foreach($checks as $check)
    {
        $v = mysqli_real_escape_string($con, $check);
        mysqli_query($con, "UPDATE checker SET status=2,hour=0 WHERE smtp='$check'");
    }
    
}
echo "1";