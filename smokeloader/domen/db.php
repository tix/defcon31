<?php

$nameDB = "panel3000";
$nameSERVER = "localhost";
$nameUSER = "panel3000";
$passUSER = "Galaperedol@1051982";
$link = mysqli_connect($nameSERVER,$nameUSER,$passUSER);
mysqli_select_db($link,$nameDB) or die("Нет соединения с БД " . mysqli_connect_error());
mysqli_query($link, "SET name UTF-8");
?>