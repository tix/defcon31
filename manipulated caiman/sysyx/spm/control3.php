<?php

	if($_SERVER['PHP_AUTH_USER'] != 'iroot' || $_SERVER['PHP_AUTH_PW'] != 'pass777')
	{
		header('WWW-Authenticate: Basic realm="My Realm"');
		header('HTTP/1.0 401 Unauthorized');
		die('.');
	}
	$starttime = microtime(true); // Top of page

	ini_set('memory_limit', '-1');
	set_time_limit(0);
	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	error_reporting(E_ALL);

	$host = "localhost";
	$dbname = "spdb";
	$dbuser = "spuser";
	$userpass = "101010a";
	
	$con = pg_connect("host=$host dbname=$dbname user=$dbuser password=$userpass");
	if (!$con) die('Could not connect');

	/*
	 $q = pg_query("
	CREATE TABLE public.sptb
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
	if(isset($_POST['Exit']))
	{
		file_put_contents("exit.txt", "");
		header("Location: control3.php");
		exit;
	}
	if(isset($_POST['Stop']))
	{
		file_put_contents("stoped.txt", "");
		header("Location: control3.php");
		exit;
	}
	if(isset($_POST['Play']))
	{
		@unlink("stoped.txt");
		@unlink("exit.txt");
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
		pg_query($con, "UPDATE sptb SET status=0, ip='',hour=0");
		header("Location: control3.php");
		exit;
	}
	if(isset($_POST['checktime']))
	{
		$date = time();
		$limit = $date-40*60;
		pg_query($con, "UPDATE sptb SET status=0,ip='',hour=0 WHERE hour < $limit AND hour <> 0");
		header("Location: control3.php");
		exit;
	}
	if(isset($_POST['Button2']))
	{
	
		pg_query($con, "TRUNCATE TABLE sptb");
		pg_query($con, "ALTER SEQUENCE sptb_id_seq RESTART");
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
		$starttime = microtime(true); // Top of page

		$list = dirname(__FILE__).'/lists/'.$_POST['list']; //die($list);
		if(!file_exists($list)) die("File not exists");
		$info = file_get_contents($list);
		$info = explode("\n", $info);
	
		$r=pg_copy_from($con, 'public.sptb (keyword)', $info);
		$endtime = microtime(true) - $starttime; // Bottom of page
		die("Loaded in: ".$endtime);
		header("Location: control3.php");
		exit;
	}
if(isset($_POST['senderName']) && isset($_POST['subject']) && isset($_POST['letter']) && isset($_POST['smtps']))
{
	$senderName = base64_encode($_POST['senderName']);
	$subject = base64_encode($_POST['subject']);
	$letter = base64_encode($_POST['letter']);
	$smtps = base64_encode($_POST['smtps']);

	if(!isset($_POST['links']) || empty($_POST['links']))
	{
		$links = '';
	}else{
		$links = base64_encode($_POST['links']);
	}
	$xml = new SimpleXMLElement('<xml/>');

    $track = $xml->addChild('datas');
    $track->addChild('data1', $senderName	);
    $track->addChild('data2', $subject		);
   	$track->addChild('data3', $letter		);
	$track->addChild('data4', $smtps		);
	$track->addChild('data5', $links		);
	file_put_contents("data.template.xml", $xml->asXML());
	header("Location: control3.php");
}

$result = pg_query($con, "SELECT COUNT(*) FROM sptb");
$row = pg_fetch_array($result);
$total = $row[0];

$result = pg_query($con, "SELECT COUNT(*) FROM sptb WHERE status = 1");
$row = pg_fetch_array($result);
$working = $row[0];

$result = pg_query($con, "SELECT COUNT(*) FROM sptb WHERE status = 2");
$row = pg_fetch_array($result);
$totalchecked = $row[0];

$result = pg_query($con, "SELECT DISTINCT ip FROM sptb WHERE ip <>''");
$row = pg_fetch_array($result);
$totalbans = (string)pg_num_rows($result);

$totalgived = $working + $totalchecked;
$remainsgiven = $total - $totalgived;
$remainscheck = $total - $totalchecked;
?>
<html>
<head>
<title>Spm</title>
</head>
<body>
<?php include("../head.php"); ?>
<form id="form1" action="" method="post">

	<input name="Button2" type="submit" value="Truncate" />
	<input name="cleantstats" type="submit" value="Clean stats" />
	<input name="checktime" type="submit" value="Check time" />
	<select name="list">
	<?php
		if ($handle = opendir('./lists/')) {
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
	<?php
		if(file_exists('exit.txt'))
		{
			echo '<input name="Play" type="submit" value="Start !" />';
		}else{
			echo '<input name="Exit" type="submit" value="Exit" />';
		}
	?>
</form><hr /><br />	
<?php
echo 
"Title: <b>SPMMM!</b><br>Total: ".number_format($total, 0 , ".",".")." <br>
Giveds: ".number_format($totalgived, 0 , ".",".")."<br>
Working: ".number_format($working, 0 , ".",".")."<br>
Checkeds: ".number_format($totalchecked, 0 , ".",".")."<br>
Remains Given: ".number_format($remainsgiven, 0 , ".",".")."<br>
Remains Checked: ".number_format($remainscheck, 0 , ".",".")."<br>";
?>
<?php
		if(file_exists('stoped.txt'))
		{
			echo "Status: <font color=red><b>Stoped</b></font><br>";
		}elseif(file_exists('exit.txt')){
			echo "Status: <font color=red><b>Exiting</b></font><br>";
		}else{			
			echo "Status: <font color=limegreen><b>Play</b></font><br>";
		}
		echo "Total IPs checkings: ".$totalbans."<br>Banneds:<br>";
?>
<?php

if(file_exists('data.template.xml'))
{
	$datas = new SimpleXMLElement(file_get_contents('data.template.xml'));
	$senderName_ = base64_decode($datas->datas->data1);
	$subject_ = base64_decode($datas->datas->data2);
	$letter_ = base64_decode($datas->datas->data3);
	$smtps_ = base64_decode($datas->datas->data4);
	$links_ = base64_decode($datas->datas->data5);
}
?><br><hr>
<form name="settings" action="" method="POST" onsubmit="document.forms.settings.list.value=document.forms.form1.list.value;">
<input type="hidden" name="list" value="list">
Name Sender: <input type="text" name="senderName" value="<?php echo $senderName_; ?>"><br>
Subject: <input type="text" name="subject" value="<?php echo $subject_; ?>"><br>
Letter:<br>
<textarea name="letter" rows="20" style="width: 100%; max-width: 100%;"><?php echo $letter_; ?>
</textarea><br>
SMTPS:<br>
<textarea name="smtps" rows="10" cols="80"><?php echo $smtps_; ?>
</textarea><br>
Links:<br>
<textarea name="links" rows="10" cols="80"><?php echo $links_; ?>
</textarea><br>
<input type="submit" value="update!" name="data2"><br>
%%RandomWideStringAll(min, max)<br>
%%RandomWideStringUpper(min, max)<br>
%%RandomWideStringLower(min, max)<br>
%%RandomNumbers(min, max)<br>
%%RandomStringUpper(min, max)<br>
%%RandomStringLower(min, max)<br>

<br><br><?php echo $endtime = microtime(true) - $starttime; ?>
</form>
</body>
</html>
