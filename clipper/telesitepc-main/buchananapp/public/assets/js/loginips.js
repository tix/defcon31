var uTable;
var search_txt_ip=null;
var _token = $("#csrf_token").val();
$(function(){
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
            "url": "getLoginIps",
            "type": "GET",
            "data":function(key){
				// $('.btn-select-all').html("全选");
				search_txt_ip = $(".ip-address").val();
				key.search_txt_ip = search_txt_ip;
            }
        }, 
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
			// {
			// 	'data':'id',
			// 	"render":function (data,type,row,meta) {
			// 	   var el="<input type='checkbox' name='mycheckboxes' value='"+row.id+"'/>"
			// 	   return el;
			// 	}
			// },
         	{
         		'data':'id',
         		"render":function (data,type,row,meta) {
	                var page=uTable.api().page();
	                return "<span style='color:black;font-weight: bold;'>"+(page*meta.settings._iDisplayLength+meta.row+1)+"</span>"+"<span style='display:none' class='client-id'>"+data+"</span>";
	            }
         	},
			{
				'data':'ipaddress'
			},
         	{
         		'data':'username'
         	},
			{
				'data':'role',
				"render":function (data,type,row,meta) {
	                var el="<span>Admin</span>";
					if(data == 2) el="<span>Tester</span>";
					else if(data != 1) el="<span>...</span>";
	                return el;
	            }
			},
			{
				'data':'country_cn'
			},
			{
				'data':'country_en'
			},
			{
				'data':'updated_at'
			}
         	// {
         	// 	'data':'updated_at',
         	// 	"render":function (data,type,row,meta) {
	        //         var el="<a href='#dialog' class='btn btn-danger btn-xs deleteBtn' id='"+row.id+"' onclick='clickDelete(" + row.id + "," +(meta.row) + ")'>删除</a>";
	        //         return el;
	        //     }
         	// }
         ],
		 fnRowCallback: function(nRow, aData, iDisplayIndex){
            return nRow;
         },
         "footerCallback": function (){
			$('#users-table_filter').html("");

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
    	uTable.fnFilter();
	});

	// $(".btn-select-all").click(function(){
	// 	if($('.btn-select-all').text() == '全选') {
	// 		$('.btn-select-all').html("取消全选");
	// 		// $("input[type='checkbox']").prop("checked", true);
	// 		for (const checkbox of document.querySelectorAll('input[name=mycheckboxes]')) {
	// 			checkbox.checked = true //for selection
	// 		}
	// 	} else {
	// 		$('.btn-select-all').html("全选");
	// 		// $("input[type='checkbox']").prop("checked", false);
	// 		for (const checkbox of document.querySelectorAll('input[name=mycheckboxes]')) {
	// 			checkbox.checked = false //for selection
	// 		}
	// 	} 
	// });

	// $(".btn-delete-sel").click(function(){
	// 	const data = [...document.querySelectorAll('input[name=mycheckboxes]:checked')].map(e => e.value);
	// 	console.log("checkboxed", data);
	// 	if(data !== null && data.length >0) {
	// 		$.ajax({
	// 			url: "deleteLoginIpSelections",
	// 			type:'get',
	// 			data: {
	// 				checkboxes: data
	// 			},
	// 			success: function(result){
	// 				// uTable.api().ajax.reload();
	// 				for(var i=0;i<data.length;i++) {
	// 					$("#"+data[i]).closest('tr').remove();
	// 				}

	// 			}
	// 		});
	// 	}
	// });
});

// function clickDelete(id, rowindex) {
// 	console.log("id",id);
// 	if(confirm("Do you want to delete current record?")){
// 		$.ajax({
// 			url: "deleteLoginIp",
// 			type:'get',
// 			data:{
// 				id:id
// 			},
// 			success: function(result){
// 				// uTable.api().ajax.reload();

// 				// var table = $('#users-table').DataTable();
// 				// table.row(rowindex).remove().draw();

// 				$("#"+id).closest('tr').remove();
// 			}
// 		});
// 	}
// }

function defineFunction(){
	$("#users-table_filter").keyup(function(event){
		if(event.keyCode===13){
	        search_txt=$("#users-table_filter input").val();
			console.log("search_txt", search_txt)
    		uTable.fnFilter();
		}
	});
}
