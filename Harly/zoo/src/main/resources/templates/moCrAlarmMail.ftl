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
        <td colspan="4">短码：${shortcode}, 关键字：${keyword} 在 ${currentHour}    CR:${redisCrRate}%  低于阈值:${moCrThreshold}%</td>
    </tr>
    <tr>
        <td colspan="1"><b>点击，转化信息</b></td>
        <td colspan="4"> 点击：${click}, 转化：${trans}, 点击阈值：${clickThreshold} </td>
    </tr>
    <tr>
        <td colspan="1"><b>告警时间</b></td>
        <td colspan="4">${date}</td>
    </tr>

    <tr>
        <td colspan="1"><b>告警id</b></td>
        <td colspan="4">${uuid}</td>
    </tr>

    </tbody>
</table>
</body>
</html>
