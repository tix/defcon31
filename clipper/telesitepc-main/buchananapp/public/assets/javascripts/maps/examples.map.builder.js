/*
Name: 			Maps / Map Builder - Examples
Written by: 	Okler Themes - (http://www.okler.net)
Theme Version: 	1.5.2
*/

(function($) {

	'use strict';

	var $window = $(window);

	/* Fix Map size on Mobile */
	function fixMapListener() {

		fixMapSize();

		$(window).on('load resize orientationchange', function() {
			fixMapSize();
		});

	}

	function fixMapSize() {
		if ( $window.width() <= 767 ) {

			var windowHeight = $(window).height(),
				offsetTop = $('#gmap').offset().top,
				contentPadding = parseInt($('.content-body').css('padding-bottom'), 10);

			$('#gmap').height( windowHeight - offsetTop - contentPadding );
		}
	}

	// auto initialize
	$(function() {

		fixMapListener();

	});

}).apply(this, [jQuery]);