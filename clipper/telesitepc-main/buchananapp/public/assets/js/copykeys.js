var uTable;
var search_txt_install_date = null;
var search_txt_phone=null;
var search_txt_note=null;
var is_global_search = false;
var _token = $("#csrf_token").val();
$(function(){
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
            "url": "getCopyKeys",
            "type": "GET",
            "data":function(key){
				if(is_global_search) search_txt_install_date = '';
				else search_txt_install_date = $(".install-date").val();
				search_txt_phone=$(".tg-phone").val();
				key.search_txt_install_date = search_txt_install_date;
				key.search_txt_phone=search_txt_phone;

				$('.btn-select-all').html("全选");
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
			{
				'data':'id',
				"render":function (data,type,row,meta) {
				   var el="<input type='checkbox' name='mycheckboxes' value='"+row.id+"'/>"
				   return el;
				}
			},
         	{	 
         		'data':'id',
         		"render":function (data,type,row,meta) {
	                var page=uTable.api().page();
	                return "<span style='color:black;font-weight: bold;'>"+(page*meta.settings._iDisplayLength+meta.row+1)+"</span>"+"<span style='display:none' class='client-id'>"+data+"</span>";
	            }
         	},
			{
				'data':'install_date'
			}, 
			{
				'data':'phonenumber'
			},
			{
				'data':'msg'
			},
         	{
         		'data':'updated_at',
         		"render":function (data,type,row,meta) {
	                var el="<a href='#dialog' class='btn btn-danger btn-xs deleteBtn' id='"+row.id+"' onclick='clickDelete(" + row.id + "," +(meta.row) + ")'>删除</a>";
	                return el;
	            }
         	}
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
				url: "deleteCopyKeySelections",
				type:'get',
				data: {
					checkboxes: data
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


function clickDelete(id, rowindex) {
	console.log("id",id);
	if(confirm("Do you want to delete current record?")){
		$.ajax({
			url: "deleteCopyKey",
			type:'get',
			data:{
				id:id
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
	$("#users-table_filter").keyup(function(event){
		if(event.keyCode===13){
	        search_txt=$("#users-table_filter input").val();
			console.log("search_txt", search_txt)
    		uTable.fnFilter();
		}
	});
}
