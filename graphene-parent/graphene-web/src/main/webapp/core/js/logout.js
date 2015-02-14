(function($) {
	$.MakeLogout = function() {

        // LOGOUT BUTTON
        $('#logout a')
            .click(function (e) {
                //get the link
                $.loginURL = $(this)
                    .attr('href');

                // ask verification
                $.SmartMessageBox({
                    title: "<i class='fa fa-sign-out txt-color-orangeDark'></i> Logout <span class='txt-color-orangeDark'><strong>" +
                        $('#show-shortcut')
                        .text() + "</strong></span> ?",
                    content: "You can improve your security further after logging out by closing this opened browser",
                    buttons: '[No][Yes]'

                }, function (ButtonPressed) {
                    if (ButtonPressed == "Yes") {
                        $.root_.addClass('animated fadeOutUp');
                        setTimeout(logout, 1000);
                    }
                });
                e.preventDefault();
            });

        /*
         * LOGOUT ACTION
                 function logout() {
        
            window.location = $.loginURL;
        }
         */
		
		
		};

	$.extend(Tapestry.Initializer, {
		makeLogout : function(params) {
			$.MakeLogout();
		}
	});
})(jQuery);
	