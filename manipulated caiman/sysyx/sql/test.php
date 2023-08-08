<?php

$url = "http://www.aventurs.com.mx/b_contact.php?lang=eng&cia=9unionallselect1,2,[t],4,5|MySQLUnion|5.7.27||[MX]Mexico||08/17/201920:24:25|"."&";
echo substr(preg_replace('/=(.*?)&/', '=1&', $url),0,-1);
?>