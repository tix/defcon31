<?php

if(isset($_POST['sents']) || isset($_POST['fails']) )
{
    $host = "localhost";
    $dbname = "sp1";
    $dbuser = "postgres";
    $userpass = "123+++xxx";

    $con = pg_connect("host=$host dbname=$dbname user=$dbuser password=$userpass");
    if (!$con) die('Could not connect');
}
if(isset($_POST['sents']))
{
    $sents = explode("\n", str_replace("\r", "", $_POST['sents']));
    foreach($sents as $mail)
    {
        if(strstr($mail, "@")) pg_query($con,"UPDATE sptb SET status=2 WHERE mail='$mail'") or die(pg_last_error($con));
    }
}
if(isset($_POST['fails']))
{
    $fails = explode("\n", str_replace("\r", "", $_POST['fails']));
    foreach($fails as $mail)
    {
        if(strstr($mail, "@")) pg_query($con,"UPDATE sptb SET status=0 WHERE mail='$mail'") or die(pg_last_error($con));
    }
}

?>