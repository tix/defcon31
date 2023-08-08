<html>
<body>
<table border="1" cellspacing="0" style="border:solid black;line-height: 36px;">
    <tbody>
    <tr>
        <td colspan="1"><b>告警标题</b></td>
        <td style="width: 360px;" colspan="4">${title}</td>
    </tr>
    <tr>
        <td colspan="1"><b>告警信息</b></td>
        <td colspan="4">APP EPM 在时间段 ${beginTime}  ******  ${endTime} EPM降低值为(${record_rate} %) 大于阈值范围(${rate}%)。</td>
    </tr>
    <tr>
        <td colspan="1"><b>告警时间</b></td>
        <td colspan="4">${date}</td>
    </tr>
    <tr>
        <td colspan="1"><b>app名称</b></td>
        <td colspan="4">${appName}</td>
    </tr>
    <tr>
        <td colspan="1"><b>运营商</b></td>
        <td colspan="4">${operator}</td>
    </tr>
    <tr>
        <td colspan="1"><b>监控开始时间段数据</b></td>
        <td colspan="4">开始时间段${beginTime}, 收益: ${beforeRevenue}, 点击: ${beforeClick}, epm: ${beforeEpm}</td>
    </tr>

    <tr>
        <td colspan="1"><b>监控结束时间段数据</b></td>
        <td colspan="4">开始时间段${endTime}, 收益: ${nowRevenue}, 点击: ${nowClick}, epm: ${nowEpm}</td>
    </tr>
    </tbody>
</table>
</body>
</html>
