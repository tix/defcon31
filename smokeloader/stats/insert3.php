<?
session_start();
include('db.php');

			
$gettfd = mysqli_query($link, "SELECT * FROM `stata`");
while($row = mysqli_fetch_array($gettfd)){
$tfd2 = $row[tfd2];
$tfd3 = $row[tfd3];
}
			




$oldstats= 412601;

$url = "http://83.136.232.5/guest1.php?id=3104&key=b516fc747375b139148be5f227a50663";

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

$newtfd3 = $newstats - $tfd3;
$today = date("Y-m-d");
$add_row = mysqli_query($link, "INSERT INTO pab3 (id, data, bot) VALUES (NULL, '$today', '$newtfd3')");
$change_rows = mysqli_query($link, "UPDATE stata SET tfd3 ='$newstats';");
	
	?>
	