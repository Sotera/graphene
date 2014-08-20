Ext.define("DARPA.PBCSToolbar", {  // allows the user to change the graph layout
    extend: "Ext.toolbar.Toolbar",
    height: 24,
    border: false,
    margin: '0,2,0,10',
    items:[],
    constructor:function(config) {
    
    this.items =  [  
        '-',
        {
            text: 'File',
            menu: Ext.create("DARPA.GraphManagerMenu", {
				style: { backgroundColor: "FloralWhite", fontSize: "medium" }
			})
        },
        '-',
        {
            text: 'Change View',
            menu: Ext.create("DARPA.GraphLayoutMenu", {
				style: { backgroundColor: "FloralWhite", fontSize: "medium" }
			})
        },
        '-',    // separator
        // button to refresh the display
         {
            xtype:'button',
            text:'Refresh', 
            disabled:false, 
            margin:0,   
            width:70,
            height: 24,
            maxHeight: 24,
            style: { backgroundColor: "FloralWhite", fontSize: "medium" }, 
            listeners: {
                click: function(self)
                {
                        var toolbar = self.up();
                        var gr = toolbar.up();
                        gr.GraphVis.refreshLayout();
                }
            } // listeners
        },
        '-',
        // display the institution
        {
            xtype:'label',
            text:'INSTITUTION: ', 
            disabled:false, 
            margin: '2 2 2 300',   
            width:170,
            listeners: {
                beforerender: function(self, opts)
                {
                    var toolbar = self.up();                        
                    self.setText("INSTITUTION: " + toolbar.institution);
                }
            } // listeners
        }
        
    ]; // toolbar items	
    this.callParent(arguments);
    } // constructor
 }); // define
 
//-------------------------------------