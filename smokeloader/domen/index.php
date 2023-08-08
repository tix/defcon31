<?
session_start();
include('db.php');

if(!empty($_POST)){
	$type = $_POST['type'];
	if($type == 'add-row'){
		$url = $_POST['url'];
		$third = $_POST['third'];

		$add_row = mysqli_query($link, "INSERT INTO `domen` (domen) VALUES ('$url')");
		if($add_row){
			header('Location: /domen/index.php');
			exit();
		}else{
			exit('Ошибка при добавлении строки <br><a href="/domen/index.php">(Вернуться назад)</a>');
		}
		
	}
	if($type == 'change-rows'){
		$domain_from = $_POST['domain_from'];
		$domain_to = $_POST['domain_to'];

		$change_rows = mysqli_query($link, "UPDATE tasks SET `from` = REPLACE(`from`, '$domain_from', '$domain_to')");
		$change_rows = mysqli_query($link, "UPDATE domen SET ubit ='+' WHERE domen = '$domain_from';");
		$change_rows = mysqli_query($link, "UPDATE domen SET run ='+' WHERE domen = '$domain_to';");
		if($change_rows){
			header('Location: /domen/index.php');
			exit();
		}else{
			exit('Ошибка при добавлении строки <br><a href="/domen/index.php">(Вернуться назад)</a>');
		}
	}
}
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
	<form action="" method="POST" enctype="multipart/form-data">
		<input type="hidden" name="type" value="change-rows">
		<input type="text" name="domain_from" placeholder="Что нужно поменять">
		<input type="text" name="domain_to" placeholder="На что нужно менять">
		<button type="submit">Заменить убитый домен</button>
	</form>
	<br>
	<form action="" method="POST" enctype="multipart/form-data">
		<input type="hidden" name="type" value="add-row">
		<input type="text" name="url" placeholder="URL">
		<input type="text" name="third" placeholder="THIRD">
		<button type="submit">Добавить Новый домен в список доступных</button>
	</form>
	<br><br>
	<table class="table">
		<thead>
			<tr>
				<td>id</td>
				<td>Домен</td>
				<td>Работает</td>
				<td>Убит</td>
			</tr>
		</thead>
		<tbody>
			<?
			$getTable = mysqli_query($link, "SELECT * FROM `domen`");
			while($row = mysqli_fetch_array($getTable)){
				echo '<tr>
						<th>'.$row[id].'</th>
						<th>'.$row[domen].'</th>
						<th>'.$row[run].'</th>
						<th>'.$row[ubit].'</th>
					</tr>';
			}
			?>
		</tbody>
	</table>
</body>
</html>