// Various helper utilities

var utils = {

	dedupeArray: function(arry, equalfn)
	{
		if (undefined == equalfn)
			equalfn = function(a,b) {return a===b};
		var results = [];
		for (var i = 0; i < arry.length; ++i) {
			if (!utils.arrayHas(results,arry[i], equalfn))
				results.push(arry[i]);
		}
		return results;
	},
	
	arrayHas: function(arry, target, equalfn)
	{
		if (undefined == equalfn)
			equalfn = function(a,b) {return a===b};

		for (var i = 0; i < arry.length; ++i) {
			if (equalfn(arry[i],target))
				return true;
		}

		return false;	
	},
	setHasData:function(frame, has)
	{
		if (undefined==frame.originalTitle)
			frame.originalTitle=frame.title;
		frame.setTitle(has ? frame.originalTitle  + "*": frame.originalTitle);
	},
	
	setBlink:function(frame, blink)
	{
		if (undefined==frame.originalTitle)
			frame.originalTitle=frame.title;
		if (blink)
			frame.setIcon("extjs/resources/themes/images/access/grid/wait.gif");
		else
			frame.setIcon("");
	},
	
	formatDate:function(date)
	{
	    	var yr = date.getFullYear();
	    	var month = date.getMonth();
	    	var day = date.getDate();
		    ++month;
		    if (month < 10)
			month = '0' + month;
		    if (day < 10)
			day = '0' + day;
	
		    return yr + '/' + month + '/' + day;
	    
	}
 

};