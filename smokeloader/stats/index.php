<?
session_start();
include('db.php');



		
?>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Document</title>
	<style type="text/css">
		.table{
			border: 1px solid #eee;
		}
		.table th {
			font-weight: bold;
			padding: 5px;
			background: #efefef;
			border: 1px solid #dddddd;
		}
		.table td{
			padding: 5px 10px;
			border: 1px solid #eee;
			text-align: center;
		}
		.table tbody tr:nth-child(odd){
			background: #fff;
		}
		.table tbody tr:nth-child(even){
			background: #F7F7F7;
		}
	</style>
</head>
<body>
	Stats for Pub3
	<table class="table">
		<thead>
			<tr>
				
				<td>Date</td>
				<td>Count</td>
				
			</tr>
		</thead>
		<tbody>
			<?
			$getTable = mysqli_query($link, "SELECT * FROM `pab3`");
			while($row = mysqli_fetch_array($getTable)){
				echo '<tr>
						<th>'.$row[data].'</th>
						<th>'.$row[bot].'</th>
					</tr>';
			}
			?>
			<tr><th>Total full days</th>
			<th>
			<?
			
			$gettfd3 = mysqli_query($link, "SELECT * FROM `stata`");
			while($row = mysqli_fetch_array($gettfd3)){
				echo $row[tfd3];
			}
			
			
			?></th>
			</tr>
			<tr><th>
			    Total <? $todayy = date("m-Y");  
			    echo $todayy; 
			    
			    
			    ?></th><th>
			
			<? $oldstats= 1435;



$url = "http://83.136.232.5/guest1.php?id=3104&key=b516fc747375b139148be5f227a50663";



$resp = response($url);
$pattern = "#<td align=\"center\">(.*?)</td>#is";
preg_match_all($pattern, $resp, $matches, PREG_PATTERN_ORDER);

// var_dump($matches[1]);
$newstats= $matches[1][3] - $oldstats;
print $newstats;

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

			
			
			
			?></th></tr>
		</tbody>
	</table>
	
	
	
</body>
</html>