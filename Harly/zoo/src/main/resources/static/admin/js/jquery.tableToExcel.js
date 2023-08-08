/* ===========================================================
 * jquery.tableToExcel.js v1.0
 * ===========================================================
 * jQuery Table to Excel Plugin
 * Copyright 2012 Gabriel Dromard
 *
 * @see https://github.com/gdromard/jquery.tableToExcel for more details
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================== */
(function ($) {
  $.tableToExcel = {
		version: "1.0",
		uri: 'data:application/vnd.ms-excel;base64,',
		template: '<html xmlns:v="urn:schemas-microsoft-com:vml"xmlns:o="urn:schemas-microsoft-com:office:office"xmlns:x="urn:schemas-microsoft-com:office:excel"xmlns="http://www.w3.org/TR/REC-html40"><head><meta http-equiv=Content-Type content="text/html; charset=UTF-8"><meta name=ProgId content=Excel.Sheet><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table border=1>{table}</table></body></html>',
		base64: function(s) { return window.btoa(unescape(encodeURIComponent(s))); },
		format: function(s, c) { return s.replace(/{(\w+)}/g, function(m, p) { return c[p]; }); },
	};

	$.fn.extend({
		tableToExcel: function(options) {
			//options = $.extend({ name: 'xxxx' }, options);
			return $(this).each(function() {
				var ctx = { worksheet: options.sheetName, table: $(this).html() };
				//window.location.href = $.tableToExcel.uri + $.tableToExcel.base64($.tableToExcel.format($.tableToExcel.template, ctx));
				$("#" + options.aid).attr("href", $.tableToExcel.uri + $.tableToExcel.base64($.tableToExcel.format($.tableToExcel.template, ctx)));
				$("#" + options.aid).attr("download", options.fileName + ".xls");
				$("#" + options.aid).get(0).click();
			});
		}
	});
}(window.jQuery));
