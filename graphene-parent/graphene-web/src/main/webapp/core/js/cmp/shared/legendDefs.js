// Legend definitions for all tabs
// MFM Updated 6/19/14 
// - Added groupName for support of different legends in different tabs and
// - added display of colored icons instead of colored text

var GLegend = {    
    legendItems: [],
    
    // This function creates and returns a single line item (Ext fieldset) for the Legend.
    // groupName     - Name of the group this legend item belongs to
    // colorDispName - The color name to display to the user (can be anything) ** NO LONGER USED/IGNORED **
    // textDesc      - Short description of the legend item
    // color         - HTML color name or hex value. The color of the icon for this item
    // fontSize      - text font size (default is 'small')
    // Returns:      JSON object containing the Ext fieldset for the legend item
    makeLegendItem: function(groupName, colorDispName, textDesc, color, fontSize) {

        //var cnameMaxLen = 9;       // max length of a specified color display name - not a hard limit
        
        // NOTE: Placing everything in a table would align things better at the expense of additional markup and code.
        //       Decided to keep it simple for now.
        //var spacingLen = cnameMaxLen - colorDispName.length;
        //if (spacingLen < 1) {
        //    spacingLen = 1;
        //}
        var spacing = "&nbsp;"; 
        //for (var s = 0; s < spacingLen; s++) {
        //    spacing = spacing + "&nbsp;&nbsp;"; // good for now
            //if (s % 2 == 0) {
            //    spacing = spacing + "&nbsp;";
            //}
        //}
        
        var iconName = Config.imagesUrl + groupName + "_" + color + ".png"; // MFM 6/19/14
        
        // DEBUG
        //console.log("makeLegendItem: iconName = " + iconName);
        
	var legendItem = {
            xtype: 'fieldset',
            layout:{
               type:'hbox'
            },
            width:'auto',
            height:'auto',
            groupName: groupName,
            margin: 0,
            padding: 0,
            border: 0,
            items:[
                {
                    xtype: 'label',
                    margin: 2,
                    padding: 1,
                    border: false,
                    html: '<img src="' + iconName + '" align="top">' + spacing + '- ' + textDesc + ' <br/>',
                    style: { fontsize: (fontSize && fontSize.length > 3)? fontSize: 'small' }
                }
            ]
        };
	
        return legendItem;
    },
    
    // MFM 6/19/14
    // groupName    - The group name
    // RETURNS      - An array of legend items for the specified group name
    getLegendByGroup: function(groupName) {
         var self = this;
         var litemsOut = [];
         var litem;
         
         for (var i = 0; i < self.legendItems.length; i++) {
             litem = self.legendItems[i];
             if (litem.groupName == groupName) {
                 litemsOut.push(litem);
             }
         }
         return litemsOut;
    },
    
    // Returns:     an array of legend items
    getLegend: function(groupName) {
        
        var self = this;
        
        if (groupName == undefined) {
        	groupName = "default";
        }
        
        if (self.legendItems.length == 0 ) {
            self.createLegend("default"); 
        }
        
        return self.getLegendByGroup(groupName);
    },
    
    // TODO FUTURE - generate the legend from a config file at startup time
    // create the legend items - singleton
    // groupName    - The group this legend belongs to
    // slitems      - Optional. Legend items for this group. Each item is a string containing comma separated fields
    // Returns:     - an array of legend items
    createLegend: function(groupName, slitems) {        
        var self = this;
        
        if (self.legendItems.length == 0 ) {    // Create only once
            var legendItem = self.makeLegendItem(groupName, "Red", "Item you searched for.", "red", "small");
            self.legendItems.push(legendItem);

            legendItem = self.makeLegendItem(groupName, "Green", "Email Address.", "palegreen", "small");
            self.legendItems.push(legendItem);
           
            legendItem = self.makeLegendItem(groupName, "Gray", "Shared Attribute.", "gray", "small");         
            self.legendItems.push(legendItem);
            
            legendItem = self.makeLegendItem(groupName, "Navy Blue", "Selected Item(s).", "darkblue", "small");         
            self.legendItems.push(legendItem);
            
            legendItem = self.makeLegendItem(groupName, "Magenta", "Expanded Nodes.", "magenta", "small");         
            self.legendItems.push(legendItem);
            
            legendItem = self.makeLegendItem(groupName, "Black", "Selected Link.", "blackline", "small");         
            self.legendItems.push(legendItem);
            
            // ADD MORE
        }
        
        // Avoid creation of duplicate legends
        var litems = self.getLegendByGroup(groupName);
        if (litems.length > 0) {
            return litems;
        }
        else { // Create legend for a new group
            // slitems - Optional legend items for this group. Each item is a string containing comma separated fields
            if (slitems && slitems.length > 0) {
                var slitem;
                for (var i = 0; i < slitems.length; i++) {
                    slitem = slitems[i];
                    var sliParts = slitem.split(",");
                    if (sliParts.length == 4) {
                        legendItem = self.makeLegendItem(groupName, sliParts[0], sliParts[1], sliParts[2], sliParts[3]);
                        self.legendItems.push(legendItem);
                    }
                }
            }
        }
        return self.getLegendByGroup(groupName);
    }
};
