// Legend definitions for all tabs

var GLegend = {
    
    legendItems: [],
    
    // This function creates a single line item (Ext label) for the Legend.
    // colorDispName - The color name to display to the user (can be anything)
    // textDesc      - Short description of the legend item
    // color         - HTML color name or hex value. The text specified in colorDispName will be rendered in this color
    // fontSize      - text font size (default is 'small')
    // Returns:      JSON object containing the Ext label for the legend item
    addLegendItem: function(colorDispName, textDesc, color, fontSize) {

        var cnameMaxLen = 9;       // max length of a specified color display name - not a hard limit
        
        // NOTE: Placing everything in a table would align things better at the expense of additional markup and code.
        //       Decided to keep it simple for now.
        var spacingLen = cnameMaxLen - colorDispName.length;
        if (spacingLen < 1) {
            spacingLen = 1;
        }
        var spacing = "&nbsp;"; 
        for (var s = 0; s < spacingLen; s++) {
            spacing = spacing + "&nbsp;&nbsp;"; // good for now
            //if (s % 2 == 0) {
            //    spacing = spacing + "&nbsp;";
            //}
        }
        
	var legendItem = 
	{
            xtype: 'label',
            margin: 5,
            padding: 1,
            html: "<span style='color:" + color + "'><b>" + colorDispName.toUpperCase() + "</b></span>" + spacing + "= " + textDesc + " <br/>",
            style: { fontsize: (fontSize && fontSize.length > 3)? fontSize: 'small' }
        }
        return legendItem;
    },
    
    // Returns:     an array of legend items
    getLegend: function() {
        
        var self = this;
        if (self.legendItems.length == 0 ) {
            self.createLegend(); 
        }
        return this.legendItems;
    },
    
    // TODO FUTURE - generate the legend from a config file at startup time
    // create the legend items - singleton
    // Returns:     an array of legend items
    createLegend: function() {        
        var self = this;

        if (self.legendItems.length == 0 ) {    // Create only once
            var legendItem = self.addLegendItem("Red", "Item you searched for.", "red", "small");
            self.legendItems.push(legendItem);

            legendItem = self.addLegendItem("Lite Green", "Customer Account.", "palegreen", "small");
            self.legendItems.push(legendItem);

            legendItem = self.addLegendItem("Cyan", "Communication Id.", "cyan", "small");         
            self.legendItems.push(legendItem);
            
            legendItem = self.addLegendItem("Gold", "Alternate Number/Id.", "gold", "small");         
            self.legendItems.push(legendItem);

            legendItem = self.addLegendItem("Salmon", "Customer.", "sandybrown", "small");         
            self.legendItems.push(legendItem);
           
            legendItem = self.addLegendItem("Gray", "Shared Attribute.", "gray", "small");         
            self.legendItems.push(legendItem);
            
            legendItem = self.addLegendItem("Navy Blue", "Selected Item(s).", "darkblue", "small");         
            self.legendItems.push(legendItem);
            
            legendItem = self.addLegendItem("Magenta", "Expanded Nodes.", "magenta", "small");         
            self.legendItems.push(legendItem);
            
            legendItem = self.addLegendItem("Black", "Selected Link.", "black", "small");         
            self.legendItems.push(legendItem);
            
            // ADD MORE
        }
        
        return self.legendItems;
    }
};
