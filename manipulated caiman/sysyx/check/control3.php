<?php

	if($_SERVER['PHP_AUTH_USER'] != 'iroot' || $_SERVER['PHP_AUTH_PW'] != 'pass777')
	{
		header('WWW-Authenticate: Basic realm="My Realm"');
		header('HTTP/1.0 401 Unauthorized');
		die('.');
	}
	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	error_reporting(E_ALL);

$host = "localhost";
$dbname = "checkdb";
$dbuser = "check1";
$userpass = "101010a";

$con = pg_connect("host=$host dbname=$dbname user=$dbuser password=$userpass");
if (!$con) die('Could not connect');

/* $q = pg_query("
CREATE TABLE IF NOT EXISTS public.checktb
                (
                id serial,
                keyword character varying(521),
                status smallint NOT NULL DEFAULT 0,
                ip character varying(64) DEFAULT NULL,
                hour integer NOT NULL DEFAULT 0,
            CONSTRAINT sptb_pkey2 PRIMARY KEY (id)
                )
                WITH (
                    OIDS = FALSE
                );");
	die(var_dump($q));					
*/			

//include_once("../db.php");

if(isset($_POST['Stop']))
{
	file_put_contents("stoped.txt", "");
	header("Location: control3.php");
	exit;
}
if(isset($_POST['Play']))
{
	@unlink("stoped.txt");
	header("Location: control3.php");
	exit;
}
if(isset($_POST['cleantstats']))
{
	pg_query($con, "UPDATE checktb SET status=0, ip='',hour=0");
	header("Location: control3.php");
	exit;
}
if(isset($_POST['checktime']))
{
	$date = time();
	$limit = $date-40*60;
	pg_query($con, "UPDATE checktb SET status=0,ip='',hour=0 WHERE hour < $limit AND hour <> 0");
	header("Location: control3.php");
	exit;
}
if(isset($_POST['Button2']))
{

	pg_query($con, "TRUNCATE TABLE checktb");
	pg_query($con, "ALTER SEQUENCE checktb_id_seq RESTART");
	header("Location: control3.php");
	exit;
}
if(isset($_POST['Cleanban']))
{
	pg_query($con, "TRUNCATE TABLE logx");
	header("Location: control3.php");
	exit;
}
if(isset($_POST['loadfile']) && isset($_POST['list']))
{
	$list = dirname(__FILE__).'/smtps/'.$_POST['list']; //die($list);
	if(!file_exists($list)) die("File not exists");
	$info = file_get_contents($list);
	$info = explode("\n", str_replace(array("\r","\t"),"", pg_escape_string($info)));

	$r=pg_copy_from($con, 'public.checktb (keyword)', $info);
	header("Location: control3.php");
	exit;
}
if(isset($_POST['Button3']))
{
	$info = $_POST['TextArea1'];
	$info = explode("\n", str_replace("\r","",$info))	;
	for($i = 0; $i < count($info); $i++)
	{
		$ss = mysqli_real_escape_string($info[$i]);
		$q = pg_query($con, "SELECT * FROM checktb WHERE smtp='".$ss."' LIMIT 1");
		if(mysqli_num_rows($q) == 0)
		{
			pg_query($con, "INSERT INTO checktb (smtp) VALUES ('".$ss."')");
		}
	}
	header("Location: control3.php");
	exit;
}

//check time //
if(isset($_GET['check']))
{
	$date = time();
	$limit = $date-40*60;
	pg_query($con, "UPDATE checktb SET status=0,ip='',hour=0 WHERE hour < $limit AND hour <> 0");
}
//

$result = pg_query($con, "SELECT COUNT(1) FROM checktb");
$row = pg_fetch_array($result);
$total = $row[0];

$result = pg_query($con, "SELECT COUNT(1) FROM checktb WHERE status = 1");
$row = pg_fetch_array($result);
$working = $row[0];

$result = pg_query($con, "SELECT COUNT(1) FROM checktb WHERE status = 2");
$row = pg_fetch_array($result);
$totalchecked = $row[0];

$result = pg_query($con, "SELECT DISTINCT ip FROM checktb WHERE ip <>''");
$row = pg_fetch_array($result);
$totalbans = (string)pg_num_rows($result);

$totalgived = $working + $totalchecked;
$remainsgiven = $total - $totalgived;
$remainscheck = $total - $totalchecked;

?>

<html>

<head>
<title>Checking</title>
</head>
<body>
<?php include("../head.php"); ?>
<form id="form1" action="" method="post">

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
<?php echo "Title: <b>CHECKING</b><br>Total: $total <br>Giveds: $totalgived<br>Working: $working<br>Checkeds: $totalchecked<br>
Remains Given: $remainsgiven<br>Remains Checked: $remainscheck<br>"; ?>
<?php
		if(file_exists('stoped.txt'))
		{
			echo "Status: <font color=red><b>Stoped</b></font><br>";
		}else{
			echo "Status: <font color=limegreen><b>Play</b></font><br>";
		}
?>
<?php echo "Total IPs checkings: ".$totalbans."<br>Banneds:<br>"; 

// while ($row = mysqli_fetch_assoc( $result)) {
// 	$counterlog = $row["counterlog"];
// 	if($counterlog >= 3)
// 	{
// 		echo $row["ip"]." | ".$row["timelog"]." | ".$counterlog." <br>";
// 	}
// }
?>
</body>
</html>