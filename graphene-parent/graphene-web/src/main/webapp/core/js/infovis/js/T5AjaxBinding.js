function setupEvent(id, theurl) {
	$("#"+id).click(function() {
		var dataObj = {};
		dataObj["queryParameter1"] = "Daniel";
		dataObj["queryParameter2"] = "Jue";
		$.ajax({
			url : theurl,
			data : dataObj,
			dataType: "json",
			complete : function() {
				//alert("Completed Loading");
				init();
			},
			success : function(result) {
				//alert("AJAX - success setup by T5() " + result.adjacencies);
				json=result;
				
			},
			error : function() {
				alert("Error loading graph");
			},
			timeout : function() {
				alert("Timeout loading graph");
			},
			abort : function() {
				alert("Abort loading graph");
			},
			parseerror : function() {
				alert("Error parsing json");
			},
		});
	});
}