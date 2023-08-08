<html>
<body>
	<table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
		<tbody>
			<tr>
				<td colspan="1"><b>告警标题</b></td>
				<td style="width: 360px;" colspan="4">${title}</td>
			</tr>
			<tr>
				<td colspan="1"><b>告警时间</b></td>
				<td colspan="4">${date}</td>
			</tr>
            <tr>
                <td colspan="1"><b>告警系统</b></td>
                <td colspan="4">${system}</td>
            </tr>
            <tr>
                <td colspan="1"><b>告警队列</b></td>
                <td colspan="4">${queueName}</td>
            </tr>
			<tr>
				<td colspan="1"><b>告警MESSAGE ID</b></td>
				<td colspan="4">${messageId}</td>
			</tr>
			<tr>
				<td colspan="1"><b>告警信息</b></td>
				<td colspan="4">${message}</td>
			</tr>
			<tr>
				<td colspan="1"><b>告警id</b></td>
				<td colspan="4">${uuid}</td>
			</tr>
		</tbody>
	</table>
	<br />
	<div>
		<h3>异常详情:</h3>
		<br />
		<pre>
			${exception}
		</pre>
	</div>
</body>
</html>