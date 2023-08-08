<?php

include_once("../db.php");

if(isset($_POST['Button1']))
{
	mysqli_query($con, "UPDATE emailex SET sent=0");
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
if(isset($_POST['Button2']))
{
	mysqli_query($con, "TRUNCATE TABLE emailex");
	header("Location: control.php");
	exit;
}
if(isset($_POST['Cleanban']))
{
	mysqli_query($con, "TRUNCATE TABLE logx_emailex");
	header("Location: control.php");
	exit;
}
if(isset($_POST['loadfileinfile']) && $_POST['list'] && !empty($_POST['list']))
{
	$list = dirname(__FILE__).'/dorks/'.$_POST['list'];
	if(!file_exists($list))
		die("List file no exists");

	mysqli_query($con, "LOAD DATA local INFILE '$list' INTO TABLE emailex (dork)");
	die("Done!. reloead.");
	header("Location: control.php");
	exit;
}
if(isset($_POST['loadfile']) && $_POST['list'] && !empty($_POST['list']))
{
	$info = file_get_contents('./dorks/'.$_POST['list']);
	$info = explode("\n", str_replace("\r","",$info));

	for($i = 0; $i < count($info); $i++)
	{
		$ss = mysqli_real_escape_string($con, $info[$i]);
		mysqli_query($con, "INSERT INTO emailex (dork) VALUES ('".$ss."')");
	}
	header("Location: control.php");
	exit;
}

$result = mysqli_query($con, "SELECT COUNT(1) FROM emailex");
$row = mysqli_fetch_array($result);
$total = $row[0];

$result = mysqli_query($con, "SELECT * FROM logx_emailex");
$totalbans = mysqli_num_rows($result);

$result = mysqli_query($con, "SELECT COUNT(1) FROM emailex_list");
$row = mysqli_fetch_array($result);
$vulns = $row[0];

// $result = mysqli_query($con, "SELECT COUNT(1) FROM emailex WHERE sent=1");
// $row = mysqli_fetch_array($result);
// $checked = $row[0];
$checked ='';
// $result = mysqli_query($con, "SELECT COUNT(1) FROM emailex WHERE sent=0");
// $row = mysqli_fetch_array($result);
// $unchecked = $row[0];
$unchecked ='';
?>

<html>

<head>
<title>Emailex</title>
</head>
<body>
<?php include("../head.php"); ?>
<form id="form1" action="" method="post">

	<input name="Button1" type="submit" value="Reset" /> 
	<input name="Button2" type="submit" value="Truncate" />
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
	<input name="download" type="submit" value="Download List" />
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
<?php echo "Title: <b>EMAILEX</b><br>Total: $total<br>Mails: $vulns <br>"; ?>
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
</body>

</html>
