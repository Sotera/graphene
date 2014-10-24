/*
 * CHAT
 */
(function($) {
	$.MakeChat = function() {
		$.filter_input = $('#filter-chat-list');
		$.chat_users_container = $('#chat-container > .chat-list-body')
		$.chat_users = $('#chat-users')
		$.chat_list_btn = $('#chat-container > .chat-list-open-close');
		$.chat_body = $('#chat-body');

		/*
		 * LIST FILTER (CHAT)
		 */

		// custom css expression for a case-insensitive contains()
		jQuery.expr[':'].Contains = function(a, i, m) {
			return (a.textContent || a.innerText || "").toUpperCase().indexOf(
					m[3].toUpperCase()) >= 0;
		};

		function listFilter(list) { /*
									 * header is any element, list is an
									 * unordered list create and add the filter
									 * form to the header
									 */
			$.filter_input.change(
					function() {
						var filter = $(this).val();
						if (filter) {
							/*
							 * this finds all links in a list that contain the
							 * input, and hide the ones not containing the input
							 * while showing the ones that do
							 */
							$.chat_users.find(
									"a:not(:Contains(" + filter + "))")
									.parent().slideUp();
							$.chat_users.find("a:Contains(" + filter + ")")
									.parent().slideDown();
						} else {
							$.chat_users.find("li").slideDown();
						}
						return false;
					}).keyup(function() {
				// fire the above change event after every letter
				$(this).change();

			});

		}

		// on dom ready
		listFilter($.chat_users);

		// open chat list
		$.chat_list_btn.click(function() {
			$(this).parent('#chat-container').toggleClass('open');
		})
		if ($.chat_body[0]) {
			$.chat_body.animate({
				scrollTop : $.chat_body[0].scrollHeight
			}, 500);
		}
	};

	$.extend(Tapestry.Initializer, {
		makeChat : function(params) {
			$.MakeChat();
		}
	});
})(jQuery)