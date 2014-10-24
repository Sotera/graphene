function getSelected() {
	if (window.getSelection) {
		return window.getSelection();
	} else if (document.getSelection) {
		return document.getSelection();
	} else {
		var selection = document.selection && document.selection.createRange();
		if (selection.text) {
			return selection.text
		}
		return false;
	}
	return false;
};

var selectedText = "";
(function($) {
	$.MakeTextSelect = function() {
		$("#narrative").highlighter({
			"selector" : ".holder",
			'minWords' : 1,
			'complete' : function(data) {
				// Here we have the selected string for use.
				console.log(data);
				selectedText = data;
				if (data.length > 50) {
					$(".holder input").val("");
					$(".holder textarea").val(data);
				} else {
					$(".holder input").val(data);
					$(".holder textarea").val("");
				}
			}
		});

		$('.holder').mousedown(function() {
			return false;
		});

		$('.btn-right').click(function() {
			$('.holder').hide();
			return false;
		});

		// end document ready
	};

	$.extend(Tapestry.Initializer, {
		makeTextSelect : function(params) {
			$.MakeTextSelect();
		}
	});
})(jQuery)