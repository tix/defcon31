<?php

//include_once("../db.php");

if(isset($_POST['Stop']))
{
	@file_put_contents("stoped.txt", "");
	header("Location: control2.php");
	exit;
}

if(isset($_POST['Play']))
{
	@unlink("stoped.txt");
	header("Location: control2.php");
	exit;
}
if(isset($_POST['Offsetreset']))
{
	if(file_exists("./lists/offset.txt"))
		file_put_contents("./lists/offset.txt", '0');
	header("Location: control2.php");
	exit;
}
if(isset($_POST['Statsreset']))
{
	if(file_exists("./lists/given.txt"))
		file_put_contents("./lists/given.txt", '');
	if(file_exists("./lists/sents.txt"))
		file_put_contents("./lists/sents.txt", '');
	if(file_exists("./lists/fails.txt"))
		file_put_contents("./lists/fails.txt", '');
		
	header("Location: control2.php");
	exit;
}

if(isset($_POST['Button1']))
{
	//mysqli_query($con, "UPDATE checker SET sent=0");
	header("Location: control2.php");
	exit;
}

if(isset($_POST['Button2']))
{
	mysqli_query($con, "TRUNCATE TABLE mails");
	header("Location: control2.php");
	exit;
}
if(isset($_POST['Cleanban']))
{
	mysqli_query($con, "TRUNCATE TABLE logx");
	header("Location: control2.php");
	exit;
}
if(isset($_POST['loadfile']) && $_POST['list'] && !empty($_POST['list']))
{
	$list = dirname(__FILE__).'/lists/'.$_POST['list'];
	if(!file_exists($list))
		die("List file no exists");

	mysqli_query($con, "LOAD DATA LOCAL INFILE '$list' INTO TABLE mails (mail);");

	header("Location: control2.php");
	exit;
}

if(isset($_POST['senderName']) && isset($_POST['subject']) && isset($_POST['letter']) && isset($_POST['data2']))
{
	$senderName = base64_encode($_POST['senderName']);
	$subject = base64_encode($_POST['subject']);
	$letter = base64_encode($_POST['letter']);
	$smtps = base64_encode($_POST['smtps']);
	$list = $_POST['list'];
	if(!isset($_POST['links']) || empty($_POST['links']))
	{
		$links = '';
	}else{
		$links = base64_encode($_POST['links']);
	}
	if( (filesize("./lists/$list") != filesize("./lists/list.txt")) && (md5_file("./lists/$list") != md5_file("./lists/list.txt")))
	{
		@unlink("./lists/list.txt");
		copy("./lists/$list", "./lists/list.txt");
		@file_put_contents("./lists/offset.txt", '0');
	}

	$xml = new SimpleXMLElement('<xml/>');

    $track = $xml->addChild('datas');
    $track->addChild('data1', $senderName	);
    $track->addChild('data2', '%FromMail%'	);
    $track->addChild('data3', $subject		);
   	$track->addChild('data4', $letter		);
    $track->addChild('data5', $links		);
    $track->addChild('data6', '%HostName%'	);
    $track->addChild('data7', '%EmailList%'	);

	file_put_contents("data.template.xml", $xml->asXML());
	header("Location: control2.php");
}

$total = getLines("./lists/list.txt");
$given = getLines("./lists/given.txt");
$sents = getLines("./lists/sents.txt");
$fails = getLines("./lists/fails.txt");
$unknown = $given-($sents+$fails);
$offset = file_get_contents("./lists/offset.txt");

function getLines($file)
{
	if(!file_exists($file)) return 0;
	$f = fopen($file, 'rb');
    $lines = 0;
    while (!feof($f)) {
        $lines += substr_count(fread($f, 8192), "\n");
    }
    fclose($f);
    return $lines;
}

?>

<html>

<head>
<title>Spm</title>
</head>
<body>
<?php include("../head.php"); ?>
<form id="form1" action="" method="post">

	<input name="Button1" type="submit" value="Reset" /> 
	<input name="Button2" type="submit" value="Truncate" />
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
	<input name="loadfile" type="submit" value="Load this list" />
	<input name="Cleanban" type="submit" value="Clean Ban" />
	<input name="Offsetreset" type="submit" value="Offset reset" />
	<input name="Statsreset" type="submit" value="Stats reset" />
	<?php
		if(file_exists('stoped.txt'))
		{
			echo '<input name="Play" type="submit" value="PLAY !" />';
		}else{
			echo '<input name="Stop" type="submit" value="Stop" />';
		}
	?>

</form><hr /><br />
Title: <b>SPM</b>
<?php
		if(file_exists('stoped.txt'))
		{
			echo "Status: <font color=red><b>Stoped</b></font><br>";
		}else{
			echo "Status: <font color=limegreen><b>Play</b></font><br>";
		}
	?>
<?php echo "<br>Total: $total<br>Given: $given<br>Sents: $sents<br>Fails: $fails<br>Unknown: $unknown<br>Offset: $offset<br><br>"; 

// while ($row = mysqli_fetch_assoc($result)) {
// 	$counterlog = $row["counterlog"];
// 	if($counterlog >= 3)
// 	{
// 		echo $row["ip"]." | ".$row["timelog"]." | ".$counterlog." <br>";
// 	}
// }
if(file_exists('data.template.xml'))
{
	$datas = new SimpleXMLElement(file_get_contents('data.template.xml'));
	$senderName_ = base64_decode($datas->datas->data1);
	$subject_ = base64_decode($datas->datas->data3);
	$letter_ = base64_decode($datas->datas->data4);
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
</form>
</body>
</html>
