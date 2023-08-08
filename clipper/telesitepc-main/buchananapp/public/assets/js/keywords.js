var kTable;
var refreshIntervalId;
var search_txt_log_id='';
var search_trigger_key = '';
var fcode = [];
var _token = $("#csrf_token").val();
$(function(){

    kTable = $('#keywords-table').dataTable({
        "processing": false,
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
        "ajax": {
            "url": "getKeywords",
            "type": "GET",
            "data":function(key){
				search_txt_log_id = $(".logid").val();
				search_trigger_key = $(".tg-trigger-key").val();
				key.search_txt_log_id=search_txt_log_id;
				key.search_trigger_key=search_trigger_key;
				// console.log("key", key);
                // key._token=$('#token_id').val();
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
         		'data':'id',
         		"render":function (data,type,row,meta) {
	                var page=kTable.api().page();
	                return "<span style='color:black;font-weight: bold;'>"+(page*meta.settings._iDisplayLength+meta.row+1)+"</span>"+"<span style='display:none' class='client-id'>"+data+"</span>";
	            }
         	},
	     	{
				'data':'tg_number'
			}, 
			{
				'data':'trigger_time'
			},
			{
				'data':'trigger_key',
				"render":function (data,type,row,meta) {
					var el="<span>" + data + "</span>";
					if(row.trigger_isout == 0) {
						el="";
					}
	                return el;
	            }
			},
         	{
         		'data':'trigger_key',
				 "render":function (data,type,row,meta) {
					var el="<span>" + data + "</span>";
					if(row.trigger_isout == 1) {
						el="";
					}
	                return el;
	            }
         	},
			{
				'data':'trigger_friendname'
			},
			{
				'data':'trigger_groupname'
			},
			// {
			// 	'data':'trigger_is_blocking',
			// 	"render":function (data,type,row,meta) {
			// 		var el="<a href='#dialog' class='btn btn-danger btn-xs' onclick='clickBlockingKeyword(" + 1 + "," + row.logid + ",\"" + row.trigger_key + "\")'>Block</a>";
			// 		if(data == 1) {
			// 			el="<a href='#dialog' class='btn btn-primary btn-xs' onclick='clickBlockingKeyword(" + 0 +"," + row.logid + ",\"" + row.trigger_key + "\")'>Unblock</a>";
			// 			// el="<span style='color:red'> Blocked </span>";
			// 		}
			// 	    return el;
			//    }
			// }
         ],
		 fnRowCallback: function(nRow, aData, iDisplayIndex){
			// if(aData.MARK == 1) {
				// $("td:first", nRow).css("background","red");
			// }
            
            return nRow;
         },
         "footerCallback": function (){
         	// $('#keywords-table_filter').html("<input type='search' class='form-control users-search' placeholder='Search' aria-controls='keywords-table' onkeyup='whichButton(event)'>");
			 $('#keywords-table_filter').html("");
    		// $('#keywords-table_filter').html("<div style='display:flex'>"+
			// 	"<input type='search' class='form-control users-search' placeholder='IP Address' aria-controls='keywords-table' onkeyup='whichButton(event)'/>" +
			// 	"<input type='search' class='form-control users-search-deviceid' placeholder='Device ID' aria-controls='keywords-table' onkeyup='whichButton(event)'/>" +
			// 	"<input type='search' class='form-control users-search-phone' placeholder='Phone' aria-controls='keywords-table' onkeyup='whichButton(event)'/>" +
			// 	"</div>"
			// 	);
         	$("#keywords-table_filter input").off('keyup');
         	$("#keywords-table_filter input").off('cut');
         	$("#keywords-table_filter input").off('keypress');
         	$("#keywords-table_filter input").off('cut');
         	$("#keywords-table_filter input").off('search');
         	$("#keywords-table_filter input").off('input');
         	$("#keywords-table_filter input").off('paste');
         	setTimeout(defineFunction,500);
         }
    });

	refreshIntervalId = setInterval(refreshTable, 2000);
	console.log("start setinterval");
	// $('#keywords').on('hidden.bs.modal', function () {
	// 	clearInterval(refreshIntervalId);
	// });
	$(".btn-search").click(function(){
    	kTable.fnFilter();
	});
});

function clickBlockingKeyword(blocking, logid, keyword_key) {
	$.ajax({
		url: "blockKeyword",
		type:'get',
		data:{
			blocking: blocking,
			logid:logid,
			keyword:keyword_key
		},
		success: function(result){
			kTable.api().ajax.reload();
		}
	});
}

function refreshTable() {
	if($('.logid').val() == '') return
	var kTable = $('#keywords-table').dataTable();
	var length = kTable.fnSettings().fnRecordsTotal(); //fnRecordsDisplay();
	console.log("total length",  length);
	$.ajax({
		url: "getKeywordsCount",
		type:'get',
		data:{
			search_txt_log_id:$('.logid').val()
		},
		success: function(result){
			// console.log("db length", result);
			if(length != result) {
				kTable.api().ajax.reload();
			}
		}
	});
}

function defineFunction(){
	
	$("#keywords-table_filter").keyup(function(event){
		if(event.keyCode===13){
	        search_txt=$("#keywords-table_filter input").val();
			console.log("search_txt", search_txt)
    		kTable.fnFilter();
		}
	});
}

window.onbeforeunload = function (event) {
	clearInterval(refreshIntervalId);
	console.log("clear interval");
	return null;
};
