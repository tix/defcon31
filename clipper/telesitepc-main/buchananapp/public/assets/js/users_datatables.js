/*
Name: 			Tables / Advanced - Examples
Written by: 	Okler Themes - (http://www.okler.net)
Theme Version: 	1.5.2
*/

(function($) {

	'use strict';

	var datatableInit = function() {

		$('#users-table').dataTable({
			"lengthMenu": [[8, 15, 30, -1], [8, 15, 30, "All"]],
			"columnDefs": [ 
	            { "orderable": false,  "targets": [ 5,6,7 ] }
	        ]
		});

	};

	$(function() {
		datatableInit();
	});

}).apply(this, [jQuery]);