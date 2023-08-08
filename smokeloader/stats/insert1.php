<?
session_start();
include('db.php');

ini_set('error_reporting', E_ALL);
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);

			
$gettfd = mysqli_query($link, "SELECT * FROM `stata`");
while($row = mysqli_fetch_array($gettfd)){
$tfd1 = $row['tfd1'];
$tfd3 = $row['tfd3'];
}
			




$oldstats= 165372;



$url = "http://83.136.232.5/guest1.php?id=9189&key=b46de2471442b26e4acc7db4ef6747cf";



$resp = response($url);
$pattern = "#<td align=\"center\">(.*?)</td>#is";
preg_match_all($pattern, $resp, $matches, PREG_PATTERN_ORDER);

// var_dump($matches[1]);
$newstats= $matches[1][3] - $oldstats;

function response($url) 
{
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, false);
    curl_setopt($ch, CURLOPT_HEADER, false);
    curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 10);
    curl_setopt($ch, CURLOPT_TIMEOUT, 40);

    $header[] = "Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3";
    $header[] = "Referer:";
    $header[] = "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0";

    curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_REFERER, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    $result = curl_exec($ch);
    return $result;
}


$newtfd2 = $newstats - $tfd1;
$today = date("Y-m-d");
$add_row2 = mysqli_query($link, "INSERT INTO pab1 (id, data, bot) VALUES (NULL, '$today', '$newtfd2')");
$change_rows2 = mysqli_query($link, "UPDATE stata SET tfd1 ='$newstats';");
echo $newstats;
echo $newtfd2;
echo $today;
	
	?>
	