<?php

$nameDB = "stats";
$nameSERVER = "localhost";
$nameUSER = "stats";
$passUSER = "Gruber@1051982";
$link = mysqli_connect($nameSERVER,$nameUSER,$passUSER);
mysqli_select_db($link,$nameDB) or die("Нет соединения с БД " . mysqli_connect_error());
mysqli_query($link, "SET name UTF-8");
?>