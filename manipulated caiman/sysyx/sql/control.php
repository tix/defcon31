<?php

include_once("../db.php");

if(isset($_POST['Button1']))
{
	mysqli_query($con, "UPDATE sqlier SET sent=0");
	header("Location: control.php");
	exit;
}
if(isset($_POST['Stop']))
{
	file_put_contents("stoped.txt", "");
	header("Location: control.php");
	exit;
}

if(isset($_POST['Play']))
{
	@unlink("stoped.txt");
	header("Location: control.php");
	exit;
}
if(isset($_POST['truncatevulns']))
{
	mysqli_query($con, "TRUNCATE TABLE sqlier_vulns");
	header("Location: control.php");
	exit;
}
if(isset($_POST['Button2']))
{
	mysqli_query($con, "TRUNCATE TABLE sqlier");
	header("Location: control.php");
	exit;
}
if(isset($_POST['Cleanban']))
{
	mysqli_query($con, "TRUNCATE TABLE logx_sqlier");
	header("Location: control.php");
	exit;
}
if(isset($_POST['loadfileinfile']))
{
	$list = dirname(__FILE__).'/dorks/'.$_POST['list'];
	if(!file_exists($list))
		die("List file no exists");
	
	mysqli_options($con, MYSQLI_OPT_LOCAL_INFILE, true);
	mysqli_query($con, "LOAD DATA local INFILE '$list' INTO TABLE sqlier (dork)");
	//die("LOAD DATA local INFILE '$list' INTO TABLE sqlier (dork)");
	header("Location: control.php");
	exit;
}
if(isset($_POST['loadfile']))
{
	$info = file_get_contents('dork.txt');
	$info = explode("\n", str_replace("\r","",$info));

	for($i = 0; $i < count($info); $i++)
	{
		$ss = mysqli_real_escape_string($con, $info[$i]);
		mysqli_query($con, "INSERT INTO sqlier (dork) VALUES ('".$ss."')");
		//@file_put_contents("dork3.txt", $ss."\r\n", FILE_APPEND);
		// $q = mysqli_query($con, "SELECT * FROM sqlier WHERE dork='".$ss."' LIMIT 1") or die(mysqli_error($con)));
		// if(mysqli_num_rows($q) == 0)
		// {
		// 	mysqli_query($con, "INSERT INTO sqlier (dork) VALUES ('".$ss."')");
		// }
	}
	header("Location: control.php");
	exit;
}

if(isset($_POST['Button3']))
{
	$info = $_POST['TextArea1'];
	$info = explode("\n", str_replace("\r","",$info))	;
	for($i = 0; $i < count($info); $i++)
	{
		$ss = mysqli_real_escape_string($con, $info[$i]);
		$q = mysqli_query($con, "SELECT * FROM sqlier WHERE dork='".$ss."' LIMIT 1");
		if(mysqli_num_rows($q) == 0)
		{
			mysqli_query($con, "INSERT INTO sqlier (dork) VALUES ('".$ss."')");
		}
	}
	header("Location: control.php");
	exit;
}


$result = mysqli_query($con, "SELECT COUNT(1) FROM sqlier");
$row = mysqli_fetch_array($result);
$total = $row[0];

$result = mysqli_query($con, "SELECT * FROM logx_sqlier");
$totalbans = mysqli_num_rows($result);

$result2 = mysqli_query($con, "SELECT * FROM sqlier_vulns");
$vulns = mysqli_num_rows($result2);

// $result = mysqli_query($con, "SELECT COUNT(1) FROM sqlier WHERE sent=1");
// $row = mysqli_fetch_array($result);
// $checked = $row[0];
$checked = '';
// $result = mysqli_query($con, "SELECT COUNT(1) FROM sqlier WHERE sent=0");
// $row = mysqli_fetch_array($result);
// $unchecked = $row[0];
$unchecked = '';
?>

<html>

<head>
<title>lqsInj</title>
</head>
<body>
<?php include("../head.php"); ?>
<form id="form1" action="" method="post">

	<input name="Button1" type="submit" value="Reset" /> 
	<input name="Button2" type="submit" value="Truncate dorks" />
	<input name="truncatevulns" type="submit" value="Truncate vulns" />
	<select name="list">
	<?php
		if ($handle = opendir('./dorks/')) {
			while (false !== ($entry = readdir($handle))) {
				if ($entry != "." && $entry != ".."){
					echo "<option value=\"$entry\">$entry</option>\n";
				}
			}
			closedir($handle);
		}
  		?>
	</select>
	<input name="loadfile" type="submit" value="Load data from file INSERT" />
	<input name="loadfileinfile" type="submit" value="Load data from file INFILE" />
	<input name="Cleanban" type="submit" value="Clean Ban" />
	<?php
		if(file_exists('stoped.txt'))
		{
			echo '<input name="Play" type="submit" value="PLAY !" />';
		}else{
			echo '<input name="Stop" type="submit" value="Stop" />';
		}
	?>	
</form><hr /><br />	
<?php echo "Title: <b>SQLING</b><br>Total: $total<br>Possible Vulns: $vulns <a href=?vulns>show</a><br>"; ?>
<?php
		if(file_exists('stoped.txt'))
		{
			echo "Status: <font color=red><b>Stoped</b></font><br>";
		}else{
			echo "Status: <font color=limegreen><b>Play</b></font><br>";
		}
?>
<?php echo "Total IPs checkings: ".$totalbans."<br>Banneds:<br>"; 

while ($row = mysqli_fetch_assoc($result)) {
	$counterlog = $row["counterlog"];
	if($counterlog >= 3)
	{
		echo $row["ip"]." | ".$row["timelog"]." | ".$counterlog." <br>";
	}
    
}


?>
<?php echo "Vulns: <br>"; 

while ($row = mysqli_fetch_assoc($result2)) {
		echo $row["urls"]."<br>\r\n";

}


?>
</body>

</html>
