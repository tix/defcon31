<?
session_start();
include('db.php');

			
$gettfd = mysqli_query($link, "SELECT * FROM `stata`");
while($row = mysqli_fetch_array($gettfd)){
$tfd1 = $row[tfd1];
$tfd2 = $row[tfd2];
$tfd3 = $row[tfd3];
$tfd4 = $row[tfd4];
}
			




$oldstats= 15540;

$url = "http://83.136.232.5/guest1.php?id=70947&key=b2ac8ca522df305d4d45e7913621f32e";

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

$newtfd4 = $newstats - $tfd4;
$today = date("Y-m-d");
$add_row = mysqli_query($link, "INSERT INTO pab4 (id, data, bot) VALUES (NULL, '$today', '$newtfd4')");
$change_rows = mysqli_query($link, "UPDATE stata SET tfd4 ='$newstats';");
	
	?>
	