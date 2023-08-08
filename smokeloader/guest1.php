<?php

require_once "./inc/cfg.php";
require_once "./inc/funcs.php";

$id = $_GET["id"];
$key = $_GET["key"];

if (!empty($id) && !empty($key)){
	global $dbcon;
	mysql_init();
	$id = intval(mysqli_real_escape_string($dbcon,$id));
	$key = mysqli_real_escape_string($dbcon,$key);
	$r = mysqli_query($dbcon,"SELECT * FROM tasks WHERE id=$id");
	if (!$r) die(include "404.php");
	$v = mysqli_fetch_assoc($r);
	if (!$v) die(include "404.php");
	$date = date("d.m.Y H:i:s",$v["time"]);
	if ($key !== md5("{$config["guest"]}{$date}")) die(include "404.php");
} else die(include "404.php");

?>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title></title>
<style>
*{
	margin:0;
	padding:0;
}

body, html {
	font:13px Tahoma,Arial;
}

#task th{
	text-transform:uppercase;
	padding:3px;
	background-color:#a7abb0;
}

#task td{
	padding:3px;
	text-transform:uppercase;
	font-size:11px;
	vertical-align:middle;
}

#task img{
	padding:3px;
	vertical-align:middle;
}

.geopng {background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXBIWXMAAAsTAAALEwEAmpwYAAAABGdBTUEAALGOfPtRkwAAACBjSFJNAAB6JQAAgIMAAPn/AACA6QAAdTAAAOpgAAA6mAAAF2+SX8VGAAADpUlEQVR42mL8//8/AyUAIIBYQAR3VCGQ/Mvwh4GTgfHfdzF+vh9xUpL/PWWEfmiy/frA8PYz6/VPH77t+vXl2dKXPzWffPorz8AMVP1tRR8DQACxwEz6+4+ZgYXpi6uj/tOJjqasmlxs/xhYmdkYfv9kZfjw4b3kx89/nP78F01fe+hX7ofPf7cyM0P0AQQQE1jzfyYGPuYXrpnutzaGuPBo8nPyMDAx8TM8efqW4drNpww8/GIMH//yMrBzsimaGrKsYWJnd/7/D+J1gAACG/D/LwOvoJrmhK8MXJzv33xj+PzlO8O3r98YhISEGJRUFBiYGP4zyHG9ZTj7mIfhumwVh4ixTT/Dvz+8IL0AAQQ2gJ2PJ+y3drzWoR8hDO/fvmL4++c3w69fPxl+/fzJ8P/3D4YLd78zbHzlyfBYNJnh+29eBi4ZBV0WQfEwkF6AAAKHAZuYbCDT708M31gUGa68lWIwZXvO8OM/F8Pbj78YmICBeOxvMsMHER8G9r/vGBi/vmVgZvnHwC3IEQjUOhcggMAuEP17Spv1402Gf/9YGS4whjFcfMTM8O39U4ZHb34zPH7zg0Hi634GjvcXGf7++MHw5zdQ8/sTDOr/12mD9AIEENiAb6wq/D9/Apnf3jL8YhBkOP4/heH0Wz0GEfYvDIwsnAy8v28xSH9ew/D/ywuGXz/+M/z/8ZmB/f8HfpBegAACe+HbH8GPHL8YBf///QLkfWH4w8jNcJ05iuHe1ycMOn8XMqiJf2dQYjrNIPH5PsOZr6EMH1hlGd798/kI0gsQQGAX/Pn85uq/718Y/v34Dsb/v39iYP7xluHbP2GG7/94GX5++8rw6TPQq6zPGGyYexmYvz9lePdL9ipIL0AAgV3w+8un9X94PnsDIx8UqYh0Cozft/8kGL58vcjAyMzI8PfXb4avPzgYvn1nZvj3+eUGkBKAAAK74N/v36t+fnh7+T8w6v79/AHHjL8+Mzz7ZcDw9JMIUM0nhq9ffzBc+2rP8Pkz82Vg9K4E6QUIILABjIwMn39//Vz4+8vH7//AhsAM+gmMxk8MbIwfGH58/85w7bMdw5Mvmt//fX1TCNID0gsQQEwQtzICnft/76+vXwL+fPt6/d+vH8AE9Ivh969/DDz/bjNw/HnBcPuTKcP9LybX/317GwBSC9YDBAABxIKWO3f9+fXTgZGRMZ6JicmTgfG35sffogynf0Zd//hHevv//58XMjL8f4WsASDAAPVpmfkKRkfxAAAAAElFTkSuQmCC") no-repeat center;width:16px;height:16px;display:inline-block;vertical-align:middle;}
</style>
</head>
<body>
<?php
echo "
<table id=\"task\">
	<tr>
		<th align=\"center\">Size</th>
		<th align=\"center\">Date</th>
		<th align=\"center\">Loads</th>
		<th align=\"center\">Runs</th>
		<th align=\"center\">Limit</th>
		<th align=\"center\">URL</th>
		<th align=\"center\">GEO</th>
		<th align=\"center\">Run Type</th>
		<th align=\"center\">Bits</th>
	</tr>".allgexe($id)."</table>";
?>
</body>
</html>