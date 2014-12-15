// Legend definitions for all tabs
// MFM Updated 6/19/14 
// - Added groupName for support of different legends in different tabs and
// - added display of colored icons instead of colored text

function Legend() {

	// private fields.  can only be manipulated by public functions
	var _legendItems = {};
	
	var _defaultGroupName = "";
	
	var _makeLegendItem = function(iconPath, desc, color) {
		var imageHTML = "";
		if (typeof color == "undefined") {
			imageHTML = "<img src='" + iconPath + "'/>";
		} else {
			imageHTML = "<svg height='25' width='25'>" +
							"<circle cx='12' cy='12' r='12' stroke='black' stroke-width='0' fill='" + color + "'/>" +
						"</svg>";
		}

		return {
			xtype: 'fieldset',
			layout: {
				type: 'hbox',
				pack: 'start',
				align: 'middle'
			},
			identifier: imageHTML + desc, // used to check for duplicates
			width: 'auto',
			height: 'auto',
			margin: 0,
			padding: 0,
			border: 0,
			items: [{
				xtype: 'panel',
				border: false,
				html: imageHTML
			}, {
				xtype: 'panel',
				border: false,
				html: desc
			}]
		};
	};
	
	/*
	 * Get this legend's default group's name (not the legend itself)
	 * 
	 * returns String _defaultGroupName
	 */
	this.getDefaultGroupName = function() {
		return _defaultGroupName;
	};
	
	/*
	 * Set's the legend's default group's name.
	 * If the legend does not currently have a groupName matching
	 * the passed name, _defaultGroupName will not be changed.
	 * 
	 * name - String: groupName key to be made the default legend
	 */
	this.setDefaultGroupName = function(name) {
		if (_legendItems.hasOwnProperty(name)) {
			_defaultGroupName = name;
		}
	};
	
	/*
	 *	Return a name/value map of all registered legends.
	 *	
	 *	returns {
	 *		"groupName1" : [ legendItems1, legendItems2, ... ],
	 *		"groupName2" : [ legendItems1, legendItems2, ... ],
	 *		...
	 *	};
	 */
	this.getAllLegendItems = function() {
		return _legendItems;
	};
	
	/*
	 *	Return an array of legend items matching the passed groupName
	 *	groupName - String name of the group to be returned.
	 *	returns Array
	 */
	this.getLegendByGroup = function(groupName) {
		var gn = (typeof groupName == "undefined") ? this.getDefaultGroupName() : groupName;
		
		if (_legendItems.hasOwnProperty(gn)) {
			return _legendItems[gn];
		} else {
			console.error("Can not find a legend group with the name '" + groupName + "'");
			return [];
		}
	};
	
	/*
	 *	Add a single legend item to the specified legend group
	 *		groupName - String: name of the group to be added to.
	 *		text - String: text description following the icon of this legend item
	 *		iconPath - (Optional) String: path to the icon resource representing this legend item
	 *		color - (Optional) String: hex value or name of a color
	 *
	 *	Note: requires either color or iconPath
	 */
	this.addLegendItem = function(groupName, text, iconPath, color) {
		var desc = "";
		
		if (typeof text !== "undefined") {
			desc = "&nbsp;- " + text;
		}
		
		var legendItem = _makeLegendItem(iconPath, desc, color);
		
		if (_legendItems.hasOwnProperty(groupName)) {
			try {
				// prevents duplicate legend items from being added to this legend set
				var legendSet = _legendItems[groupName];
				var exists = false;
				for (var i = 0; i < legendSet.length; i++) {
					if (legendSet[i].identifier == legendItem.identifier) {
						exists = true;
						break;
					}
				}
				// if not a duplicate, add it to this legend set
				if (!exists) _legendItems[groupName].push(legendItem);
			} catch (e) {
				// TODO: handle exception
			}
		} else {
			_legendItems[groupName] = [];
			_legendItems[groupName].push(legendItem);
		}
	};
	
	/*
	 *	Iterates over an array of objects, each one containing parameters to define a legend item
	 *	items - Array: each item is an Object with three fields...
	 *			groupName - String or Array<String>: groupName(s) to insert into
	 *			text - String: text description following the icon of this legend item
	 *			iconPath - (Optional) String: path to the icon resource representing this legend item
	 *			color - (Optional) String: hex value or name of a color
	 *
	 *	Note: the legend item constructor requires either color or iconPath
	 */
	this.addLegendItems = function(items) {
		var i, l, j, groupNames, iconPath, text, color;
		
		l = items.length;
		
		for (i = 0; i < l; i++) {
		
			// TODO: verify and fail softly
			groupNames = items[i].groupNames;
			iconPath = items[i].iconPath;
			text = items[i].text;
			color = items[i].color;
			
			if ($.isArray(groupNames)) {
				for (j = 0; j < groupNames.length; j++) {
					this.addLegendItem(groupNames[j], text, iconPath, color);
				}
			} else {
				this.addLegendItem(groupNames, text, iconPath, color);
			}
		}
	};
}