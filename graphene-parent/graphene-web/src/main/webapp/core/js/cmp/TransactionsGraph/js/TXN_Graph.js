
Ext.define("DARPA.TransactionGraph", {

// Holds the graph itself and the details/settings tabpanel in an hbox
// Height was set in its container as flex:1 so as to maximize height

	extend:"Ext.panel.Panel",
	border:'1',
	title:'Graph',
        layout:{
    	 	align:'stretch',
    	 	type:'hbox'
        },
        resizable: true,    // MFM
        collapsible: false,  // MFM don't allow
	hidden:false,
	titleAlign:'center',
	width:'auto',
//	height:'auto',
	json:null,
	graphId:this.id+'TXNGraph', // may not be used
	currentNode:null,
        prevNode:null,  // MFM added for unpivot
	GraphVis:null,      // reference to "DARPA.GraphVis" defined in cytoGraphSubs.js
	json:null,
        json1Hop: null,     // 
        json1HopNode: null, // currently selected node to expand if any
        nodesExpanded: 0,   //
	fromAdvancedSearch:false,
        prevLoadParams: {searchNumber: null, number: null, prevNumber: null}, // MFM store the calling params for later Pivot

	items: [],
	
        
        listeners: {    // MFM
            resize: function(tab,width,height,oldWidth,oldHeight) {
                // DEBUG
                //console.log("resized to w = " + width + ", h = " + height);
                
                if (tab.GraphVis) {
                    tab.GraphVis.resize(width,height);
                }
            }          
        }, // listeners
        
	constructor:function(config)
	{
                var self = this;
	        // MFM Add a View menu to the top toolbar to allow changing the Graph Layout
                this.institution = config.institution;
        	this.tbar = Ext.create("DARPA.TXNGToolbar", { institution: this.institution }),
	
		this.GraphVis = Ext.create("DARPA.GraphVis", { 
			id: config.id + "-TXNcygraph",
                	setBusy:function(busy) {
                		var tab = Ext.getCmp(config.entityId).getTransactionGraph();
                		utils.setBlink(tab, busy);
                		tab.setStatus(busy ? "CALCULATING LAYOUT" : "CALCULATED LAYOUT");
               		}
			
			
			});
                this.graphStore = makeTXNGraphStore(); // made this part of the object and only created once per instance
                this.nodesExpanded = 0;
                
                // DEBUG
                //console.log("TransactionGraph constructor called");
                
                    
                // MFM
                var graphSettings = Ext.create("DARPA.GraphSettings", {
                	id:config.id+'-settings'
                });
                // Have to delay this call a bit since the Graph Frame (see PB_Frame) is not defined yet
                var wtimeout3 = window.setTimeout(function() {
                    window.clearTimeout(wtimeout3);
                    graphSettings.setGraph(self);   // MFM
                }, 7600);
                
                // MFM The Entity (customer) graph does not have a concept of time - so disable dates and animation
                var filterSettings = Ext.create("DARPA.FilterSettings", {id:config.id+'-TransactionGraphFilter'});
                
                // MFM JIRA-29 Additional filter settings. 
                /**
                {
                   dispFieldName:	string | "getfromkey"	// name to display in the grid
                                                            // getfromkey: get the display text from the attrs[j].key  value
                   dispFieldType:	"text" | "dropdown" | "other"
                   dispFieldWidth:	width of the input field (not yet used)
                   dispFieldChoices:    string | "getfromnode",  // comma separated choices, or get the choices from the node's field
                   dataSourceType:	"nodes" | "edges" | "other TBD"
                   dataSourceField:	"color" | "idType" | "name" | "amount" | "weight" | "attrs"
                                        // Special case: when "attrs" - this iterates over all of the attrs and builds the
                                        // fields in the grid from each attribute (key, value) pair
                }
                **/
           
                // Specify the additional attributes that can be used for filtering
                var addfilter1 = {      // NODE
                    dispFieldName:	"Node Color",
                    dispFieldType:	"dropdown",
                    dispFieldWidth:	100,
                    dispFieldChoices:   "getfromnode",       
                    dataSourceType:	"nodes",
                    dataSourceField:	"color"
                 };
                 
                 var addfilter2 = {     // NODE
                    dispFieldName:	"Node Name",
                    dispFieldType:	"dropdown",
                    dispFieldWidth:	100,
                    dispFieldChoices:   "getfromnode",   
                    dataSourceType:	"nodes",
                    dataSourceField:	"name"
                 };
               
                 var addfilter3 = {     // NODE
                    dispFieldName:	"Identifier Type",
                    dispFieldType:	"dropdown",
                    dispFieldWidth:	100,
                    dispFieldChoices:   "getfromnode",   
                    dataSourceType:	"nodes",
                    dataSourceField:	"idType"
                 };
                 
                 var addfilter4 = {     // NODE
                    dispFieldName:	"getfromkey",
                    dispFieldType:	"text",
                    dispFieldWidth:	100,
                    dispFieldChoices:   "",       
                    dataSourceType:	"nodes",
                    dataSourceField:	"attrs" // array of attributes
                 };
                 var addfilter5 = {     // EDGE
                    dispFieldName:	"Amount",   // TODO: maybe edge weight as another attribute
                    dispFieldType:	"text",
                    dispFieldWidth:	100,
                    dispFieldChoices:   "",       
                    dataSourceType:	"edges",
                    dataSourceField:	"amount"
                 };
                 var addfilter6 = {     // EDGE
                    dispFieldName:	"getfromkey",  
                    dispFieldType:	"text",
                    dispFieldWidth:	100,
                    dispFieldChoices:   "",       
                    dataSourceType:	"edges",
                    dataSourceField:	"attrs" // array of attributes
                 };
                 var addFilterFields = [addfilter1, addfilter2, addfilter3, addfilter4, addfilter5, addfilter6];
                 var ret = filterSettings.setAdditionalFields(addFilterFields);
                 // DEBUG
                 console.log("filterSettings.setAdditionalFields returned = " + ret);
                 
                 // END MFM JIRA-29 Additional filter settings
                 // 
                // Have to delay this call a bit since the Graph Frame (see PB_Frame) is not defined yet
                var wtimeout2 = window.setTimeout(function() {
                    window.clearTimeout(wtimeout2);
                    filterSettings.setGraph(self);  // must be self and not this
                    filterSettings.enableTimeFilter(false); // is enabled by default , disable it here 
                    filterSettings.setSearchFieldLabel("Identifier(s)");
                }, 7000);
                
                var graphContainer = Ext.create("Ext.Container", {
                            width: 'auto', 
                            height: 'auto',
                            id: config.id+'-TXNcygraph',  // This is the dom element that contains the graph. 
                            flex:1 
                });
                
		var details =  	Ext.create("Ext.tab.Panel", {
			width:320,
			height:'auto',
			collapsible: true, // MFM
			collapseDirection: 'right',
			items:[
				Ext.create("Ext.panel.Panel", {
					title:'DETAILS/ACTIONS',
					layout: 'fit',
					items: [
					        Ext.create("DARPA.TXNGNodeDisplay", {
					        	id:config.id+'-NodeDisplay',
					        	height: 'auto'
					        })
					]
				}),
				Ext.create("Ext.panel.Panel", {
					title:'SETTINGS/FILTER',
					items: [
						graphSettings,  // MFM
						// MFM Added Filtering Panel
						Ext.create("Ext.panel.Panel", { 
							title:'FILTER',
							collapsible: false,
							items: filterSettings
						})
					]   
				})
			]
		});
		this.items=[graphContainer, details];
		this.bbar = Ext.create("Ext.ProgressBar", {
			id:config.id+'-ProgressBar',
			text: 'Ready',
	        height: 20,
	        width: 200
		});
		this.callParent(arguments);
		
	},
	
	getProgressBar: function() {
		return Ext.getCmp(this.id+'-ProgressBar');
	},
	
	/*
	// replaced by getProgressBar for consistancy in the API between graphs
	getStatus:function()
	{
		var self=this;
		return Ext.getCmp(self.id+'-status');
	},
	*/
	
	setStatus:function(msg)
	{
		this.getProgressBar().updateText(msg);
	},
	
        // MFM
        appendTabTitle: function(appendText) {
            var p = this;
            if (appendText && appendText.length > 0) {
                var title = (p.originalTitle) ? p.originalTitle : p.title;
                p.setTitle(title + " " + appendText);
            }
        },
        
	load:function(accountList)
	{
		var self=this;
		utils.setBlink(this, true);
		self.setStatus("LOADING DATA FOR GRAPH");
	
		self.json=null;
		var graphStore=TGgraphInit();
		var hops = self.getSettings().getMaxHops();
		var degree = parseInt(hops) + 1;
		graphStore.proxy.extraParams.degree = degree;
		
		graphStore.proxy.url = Config.transferGraphCSUrl + accountList[0];
		graphStore.proxy.extraParams.Type = 'account';
		
                // Store the calling params for later Pivot
                self.prevLoadParams.searchNumber = accountList[0];
                
                // DEBUG
                //console.log("load: graphStore.proxy.url = " + graphStore.proxy.url);
                //console.log("self.prevLoadParams.searchNumber set to " + self.prevLoadParams.searchNumber);

                self.prevLoadParams.prevNumber = self.prevLoadParams.number;
                self.prevLoadParams.number = accountList[0];   // new number

                //self.prevLoadParams.fromDate = fromDate;
                //self.prevLoadParams.toDate = toDate;
                
                // MFM progress indicator
/*                
                var pb = Ext.getCmp("TGgraphpgb");
                if (pb) {
                    pb.wait({
                        interval: 1000,
                        duration: 90000,
                        increment: 10,
                        text: 'Searching ...',
                        scope: this,
                        fn: function() {
                            pb.updateText(".");
                        }
                    })
                }   
*/            
		graphStore.load(
		{
			scope:this, //?
			callback: function(records,operation,success) {
			
			
				utils.setBlink(this, false); 

                            
//                            var pb2 = Ext.getCmp(self.id+"-TGgraphpgb");
                            
                            if (success == false || records == null || records.length == 0) {
                            	if (success == false)
                            		this.setStatus("SERVER ERROR REQUESTING GRAPH");
                            	else if (records == null) 
                            		this.setStatus("SERVER RETURNED NULL GRAPH");
                            	else if (records.length == 0)
                            		this.setStatus("SERVER RETURNED EMPTY GRAPH");                            	

//                                if (pb2) {
//                                    pb2.updateText("Search failed due to server error.");
//                                    pb2.reset();
//                                }
                                self.clear();
                            }
                            else {
				this.setStatus("LOADED DATA");
				self.json=records[0].raw;
//                                var graph = xmlToGraph(records[0].raw); // read the xml into graph structure
//                                self.json= self.GraphVis.graphToJSON(graph);   // create the graph json object. 
                                    
                                // results could be empty, check for this here
                                if (self.json && self.json.nodes.length == 0) {
                                    this.setStatus("NO DATA FOUND TO PLOT");
                                    //self.clear(); // don't clear what is already shown
                                }
                                else { 
                                    self.clear();
                                    self.showjson(self.prevLoadParams.number);
                                }

                                var nodeCount = self.json.nodes.length;
                                self.appendTabTitle("(" + nodeCount.toString() + ")");
                                
                                // DRAPER API
                                // Send a System Activity Message with optional metadata
                                //activityLogger.logSystemActivity('Graph results returned and displayed', 
                                //    {'Tab':'Transaction Graph', 'searchResults': { 'nodesFound': graph.nodes.length.toString() }});
                            }
			}
		});
	},
        
	showJson:function() {                
            var self = this;            
            if (self.json != null && self.GraphVis.getGv() != null ) {				
            	utils.setBlink(this, true); // the graph code will turn this off when layout is complete
                self.GraphVis.showGraph(self.json, searchNumber);	// display the graph
            }  
	},
        
	afterLayout:function()
	{
		var self=this;
                
		if (self.GraphVis.getGv() == null) {                        
                        // TODO May want to override some styling attributes in this config
                        var config = { width: self.getWidth(), height: self.getHeight(),
                                       rightBorder: 320, leftBorder: 5, topBorder: 5, botBorder: 80
                        };
                        
			self.GraphVis.initGraph(config, self);  // initialize the graph display lib
                        var dgt = window.setTimeout(function() {
                            window.clearTimeout(dgt);
                            self.GraphVis.zoom(0.8);
                            self.showjson(self.prevLoadParams.number);
                        },5000);        
		}
		else {
                        self.showjson(self.prevLoadParams.number);
                        self.showjson1Hop(false, null);
                }
                
		this.callParent(arguments);
	},
	afterRender:function() 
	{
		var self = this;
		self.showJson(self.prevLoadParams.number);
		this.callParent(arguments);		
	},

        getPivotButton:function()
        {
        	var self = this;
        	return Ext.getCmp(self.id + '-NodeDisplay-Actions-PIVOT');
        },

        getUnPivotButton:function()
        {
        	var self = this;
        	return Ext.getCmp(self.id + '-NodeDisplay-Actions-UNPIVOT');
        },
        
        getHideButton:function()
        {
        	var self = this;
        	return Ext.getCmp(self.id + '-NodeDisplay-Actions-HIDE');
        },

        getUnHideButton:function()
        {
        	var self = this;
        	return Ext.getCmp(self.id + '-NodeDisplay-Actions-UNHIDE');
        },
        getExpandButton:function()
        {
        	var self = this;
        	return Ext.getCmp(self.id + '-NodeDisplay-Actions-EXPAND');
        },
        getUnExpandButton:function()
        {
        	var self = this;
        	return Ext.getCmp(self.id + '-NodeDisplay-Actions-UNEXPAND');
        },
        getShowButton:function()
        {
        	var self = this;
        	return Ext.getCmp(self.id + '-NodeDisplay-Actions-SHOW');
        },
        getStopButton: function() {
        	var self = this;
        	return Ext.getCmp(self.id + '-NodeDisplay-Actions-STOP');
        },

    // MFM
        importGraph: function() {
            importGraph(this);
        },
        
        exportGraph: function() {
            exportGraph(this,"Transaction"); 
        },
        
        saveGraph: function() {
            saveGraph(this,"unknown");
        },
        
        restoreGraph: function(butid) {
           restoreGraph(this, "unknown", butid);
        },
        
	showjson:function(searchNumber)
	{            
            var self = this;            
            if (self.json != null && self.GraphVis.getGv() != null) {				
                    self.GraphVis.showGraph(self.json, searchNumber);	// display the graph
            }        
	},
      
	nodeMouseOver: function(node) {
		var self = this;
		
		if (node.data().attrs.length <= 0) {
			// if there is no data, we do not want to create a popup display
			self.mouseOverPopUp = undefined;
			return;
		}
		
		var dataHTML = (function(attrs) {
			var html = "<span>";
			
			html += "<TABLE rules='rows'>";
			for (var i = 0; i < attrs.length; ++i) {
				var a = attrs[i];
				if (a.key.indexOf("node-prop") == -1) {
					html += "<TR><TD>" + a.key + "</TD> <TD>&nbsp;:&nbsp;</TD> <TD>" + a.val + "</TD></TR>";
				}
			}
			html += "</TABLE>";
			
			return html + "</span>";
		})(node.data().attrs);
		
		if (!self.mouseOverPopUp) {
			self.mouseOverPopUp = Ext.create("Ext.Window", {
				xtype:'panel',
				height:150,
				layout:'fit',
				autoScroll: true,	
				// title: "Right-Click for more options", // TODO: uncomment when right-click is available in transaction graph
				closable: false, // prevent X in top-right corner
				bodyStyle:"padding:10px"
			});
		}
		
		self.mouseOverPopUp.update(dataHTML);
		//self.mouseOverPopUp.show();
	},
	
	nodeClick:function(node)
	{
		var self=this;
		self.currentNode=node;
		var nodeDisp = self.getNodeDisplay();
		nodeDisp.setAttrs(node.data());
                
		nodeDisp.enablePivot(true); // currently all nodes are accounts
                nodeDisp.enableHide(true);
		
	},
	edgeClick:function(edge)
	{
		var self=this;
		var nodeDisp = self.getNodeDisplay();
		var d = edge.data();
		if (d && d.attrs)
			nodeDisp.setAttrs(d);
		
		// TODO: Enable "SHOW" for the transactions
		
	},
	
	edgeRightClick: function(edge) {
		var data = edge.data();
		var attrs = data.attrs;
		
		var to, from, subject, body, date;
		
		// use this edge's TO and FROM ids to find the nodes with those ids, then get their names
		to = this.GraphVis.gv.$("node[id = '" + data.target +"']").data().name;
		from = this.GraphVis.gv.$("node[id = '" + data.source +"']").data().name;
		
		for (var i = 0; i < attrs.length; i++) {
			var key = attrs[i].key;
			
			switch (key) {
				case "date":
					try {
						date = new Date(parseInt(attrs[i].val));
						date = date.toTimeString();
					} catch (e) {
						date = null;
					}
					break;
				case "payload":
					body = attrs[i].val;
					break;
				case "subject":
					subject = attrs[i].val;
					break;
				default: break;
			}
		}
		
		var window = Ext.create("DARPA.DetailsViewer", {
			timeStamp: date
		});
		
		window.show();
		
		window.setTo(to);
		window.setFrom(from);
		window.setSubject(subject);
		window.setBody(body);
		
	},
	
	nodeEnter:function(node)
	{
	// TODO: This does not work because for some reason self was initiated as the window instead of 
	// the object. Odd, because nodeClick for example works.
		//var self = this;
                //self.currentNode = node;
                // MFM - this works
                var self = Ext.getCmp('TransactionGraphInst');
                if (self) {
                    var nodeDisp =self.getNodeDisplay();
                    nodeDisp.setAttrs(node.data());
                }

//		var self=this;
//		self.currentNode = node;
//		var nodeDisp =self.getNodeDisplay();
//		nodeDisp.setAttrs(node.data.attrs);

	},
	nodeLeave:function(node)
	{
		var self=this;
//		self.currentNode = null;
	},

	showDetail:function(data) 
	{
	// Called when you click on "SHOW" in the graph
	// passes us the data associated with the node
	
	// TODO: We are showing the customer here, not the account. Need to create a function for this.
	
		for (var i = 0; i < data.attrs.length; ++i) {
			if (data.attrs[i].key =='entityId') {
				showEntityDetails(data.attrs[i].val);
				// TODO: set a flag to load the ledger when the entity has been loaded
				// However: if we eventually show all transactions automatically
				// this will be redundant.
				break;
			}
		}
	},
        
	getNodeDisplay:function()
	{
		var self = this;
		var tabPanel = this.items.items[1];
		var tabScreen = tabPanel.items.items[0];
		return tabScreen.items.items[0];
	},
	getSettings:function()
	{
		var self = this;
		var tabPanel = this.items.items[1];
		var settingsPanel = tabPanel.items.items[1];
		return settingsPanel.items.items[0];
	},
	
	pivot:function(node)
	{
		var self=this;
                self.prevLoadParams.type = 'account';
                self.prevLoadParams.prevNumber = [node.data().idVal];
                self.load(self.prevLoadParams.prevNumber);
	
	},
        // MFM added
        unpivot:function()
	{
		var self=this;  
                
                var idValue = self.prevLoadParams.prevNumber; 
                if (idValue) {
                    self.load(idValue);
                }	
	},
        
        // MFM Expand out 1 hop from the selected node
        // userInitiated will be true when the user specifically expands a node, else this will be false
        showjson1Hop: function(userInitiated) {
            var self = this;
            var node = self.json1HopNode;
            
            // DEBUG
            //console.log("showjson1Hop");
            
            if (self.json1Hop != null && node != null) {
                // Don't NEED: self.fd.canvas.resize(this.items.items[0].getWidth(),this.items.items[0].getHeight());				
                self.GraphVis.showGraph1Hop(self.json1Hop, node);	// display the graph
                if (userInitiated && userInitiated == true) {
                    self.nodesExpanded++;
                }
            }     
        },
        
        graphOneHopStoreInit: function()
        {
            var graph1HopStore = Ext.getCmp('TGgraph1HopDataStore');  // MFM - no need to recreate each time
            if (graph1HopStore) {
                return graph1HopStore;
            }
        
            graph1HopStore = Ext.create('Ext.data.Store', {
                id: 'TGgraph1HopDataStore', 
		proxy: {
			type:'ajax',
			model:'Graph',  
			timeout:120000,
			url:'', 
			reader: {
				type:'xml',
				record:'graph',
				root:'graphml'
			}
		}
            });
            return graph1HopStore;
        },
        
        // load data for specified node expanded 1 hop out
        loadOneHop:function(intype, node, pb, fromDate, toDate, butid)
	{	
		var self=this;
		var graphStore = self.graphOneHopStoreInit(); 
		var s = self.getSettings(); 
                var maxNewCallsAlertThresh = 30;    // Adjust as needed
                
                // DEBUG
                //console.log("loadOneHop");
                
                // Count the node's adjacencies and don't allow expand if the count is > 1
                var countAdjacent = 0;
                var edges = node.connectedEdges();
                if (edges) {
                    edges.each(function(indx, edge) {
                        countAdjacent++;
                    });
                }
                
                if (countAdjacent > 1) {
                    alert("This item can't be expanded because it already has more than 1 connection.");
                    
                    if (pb) {
                        pb.updateText(".");
                        pb.reset();
                    }
                    if (butid) {
                        var but = Ext.getCmp(butid);
                        if (but)
                            but.enable();
                    }
                    return;
                }
                
                // for feedback while the query and graph display is in progress
                var mbox = Ext.Msg.show({
                    title: 'Expand',
                    msg: 'The expanded data is being obtained and prepared for display. Please wait...',
                    buttons: Ext.Msg.OK
                });
                
		graphStore.proxy.extraParams.degree = 1; // labelled hops. only 1 hop out from this node
		graphStore.proxy.extraParams.maxEdgesPerNode = s.getMaxEdgesPerNode();
		graphStore.proxy.extraParams.maxNodes = s.getMaxNodes();
                if (graphStore.proxy.extraParams.maxNodes > 200) {
                    graphStore.proxy.extraParams.maxNodes = 200;    // hard limit for this case
                }
                if (maxNewCallsAlertThresh > graphStore.proxy.extraParams.maxEdgesPerNode) {
                    maxNewCallsAlertThresh = graphStore.proxy.extraParams.maxEdgesPerNode;
                }
		graphStore.proxy.extraParams.minWeight  = s.getMinWeight();
                // MFM The Rest Service expects this:
                // @QueryParam("fromdt") @DefaultValue(value = "0") String minSecs,
		// @QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
	
                // Dates not used yet
                //graphStore.proxy.extraParams.fromdt	= fromDate; 
		//graphStore.proxy.extraParams.todt	= toDate;
                
                //-------
                if (intype == null || intype.length == 0) {
                    intype = "customer";
                }
		graphStore.proxy.extraParams.Type	= intype;	
                
                // Type must be set in the URL
                // TODO - REVISIT THE URL
		//graphStore.proxy.url = Config.entityGraphUrl + intype + '/' + node.data().name;           
		graphStore.proxy.url = Config.transferGraphCSUrl + intype + '/' + node.data().id; 
                self.json1Hop=null; // prevents us from trying to display the previous graph if we switch to this tab
                                    // before we have fully loaded the new graph
                self.json1HopNode = node;
                                
		graphStore.load(
		{
			scope:this, //?
			callback: function(records,operation,success) {
                            
                            if (success == false || records == null || records.length == 0) {
                                alert("Failed to retrieve graph results due to a server error. Please contact your System Administrator.");  // MFM
                                self.json1HopNode = null;
                                if (pb) {
                                    pb.updateText("Search failed due to server error.");
                                    pb.reset();
                                }                                
                                // don't alter or clear the existing graph
                            }
                            else {
                                var graph = xmlToGraph(records[0].raw);     // read the xml into graph structure
                                self.json1Hop = self.GraphVis.graphToJSON(graph);   // create the graph json object. 
            
                                // results could be empty, check for this here
                                if (self.json1Hop && self.json1Hop.nodes.length <= 2) { //  don't include this node already connected to another node in the graph
                                    alert("No additional items were found for this id.");
                                    self.json1HopNode = null;
                                    // don't alter the existing graph
                                }
                                else {
                                    if (self.json1Hop.length > maxNewCallsAlertThresh) { 
                                        if (mbox) {
                                            mbox.close();
                                        }
                                        Ext.Msg.confirm('Confirm', 
                                            'This number has more than ' + maxNewCallsAlertThresh + ' items and may clutter the display. Do you want to continue displaying it?', 
                                            function(ans) {
                                                if (ans == 'yes') {
                                                    self.showjson1Hop(true);
                                                }
                                            }
                                        );
                                    }
                                    else {
                                        self.showjson1Hop(true);
                                    }
                                }
                                if (pb) {
                                    pb.updateText(graph.nodes.length + " nodes");
                                    pb.reset();
                                }
                                // Update title to display the communication id and number of nodes found 
                                //self.updateTitle(graph.nodes.length, self.prevLoadParams.number );
                            }
                            if (mbox) {
                                mbox.close();
                            }
                            if (butid) {
                                var but = Ext.getCmp(butid);
                                if (but)
                                    but.enable();
                            }
			}
		});
              
	},
        
        // Expand out 1 hop for selected node only
        // pb   - Reference to Progress bar (if any)
        doTGNumberExpand: function(intype, pb, node, butid)
        {
            var self = this;
            // DEBUG
            console.log("doTGNumberExpand: intype = " + intype + ", node.name = " + node.data().name);

            // MFM Progress bar
            if (pb) {
                pb.wait({
                    interval: 1000,
                    duration: 60000,
                    increment: 10,
                    text: 'Expand ' + node.data().name + ' Searching ...',
                    scope: this,
                    fn: function() {
                        pb.updateText(".");
                    }
                })
            }

            self.loadOneHop(intype, node, pb, null, null, butid);   // last 2 null params are placeholders for start and end date

        },

        // Expand out one hop from a selected node; On the resulting graph, 
        // when you select a leaf node it should expand out ONLY that node one more hop
        expand:function(node, butid)
	{                
            var self = this;
            // DEBUG
            //console.log("expand: node # = " + node.data().name);
                
            var idType = self.getNodeDisplay().getIdentifierType();
            var pb = Ext.getCmp('TGgraphpgb');
            self.doTGNumberExpand(idType, pb, node, butid); 
            // When the results are returned, this will eventually call the showjson1Hop() function (see above).
	
	},
        // Remove or hide the expanded nodes from the graph, 
        unexpand:function(node, butid)
	{
                // DEBUG
                //console.log("UNexpand: node # = " + node.data().name);
                var self = this;
				
                self.GraphVis.unexpand1Hop(node, butid);	// display the graph
                self.nodesExpanded--;
                
                if (butid) {
                    var but = Ext.getCmp(butid);
                    if (but)
                        but.enable();
                }
                
                // DEBUG
                //console.log("unexpand nodesExpanded is now = " + self.nodesExpanded);
                if (self.nodesExpanded <= 0) {
                   self.nodesExpanded = 0;
                   self.json1Hop = null;    // clear the json data
                }        
	},
        
	applySettings:function() {
        	// called by Apply button
        	
        	var self = this;
		self.load([self.prevLoadParams.number]); // this is the last one we loaded
        
        },
        
        // filter is meant to hide items in the graph canvas
        // searchItems is a string that may contain a partial or full string (anything), or a comma separated list of strings
        // fromDate and toDate are numeric (in millisecs)
        applyFilter: function(searchItems, amount, fromDate, toDate) {            
            applyFilter(this, searchItems, amount, fromDate, toDate);
        },  
        
        // MFM JIRA-29 
        // applyAdditionalFieldsFilter is meant to hide items in the graph canvas
        // filterItems   - array of additional attributes (fields) to filter on
        applyAdditionalFieldsFilter: function(filterItems, compareType) {            
            var self = this;
            applyAdditionalFieldsFilter(self, filterItems, compareType); 
        },  
          
        // clear filter is meant to unhide all items in the graph canvas
        clearFilter: function() {            
            clearFilter(this);
        },  
        
        clear:function()
        {
        	var self=this;
        	if (self.GraphVis.getGv() != null) {
                    self.GraphVis.clear();
                }
        },
        restore:function()
        {
        	var self=this;
        	// TODO. We get here after tabbing back to the Customer tab but I haven't been able to get the graph to reshow
        	
        }
	
	

}); // define

Ext.define("GraphTXN", {
	extend:'Ext.data.Model'
});

function makeTXNGraphStore()
{
	var graphStore = Ext.create('Ext.data.JsonStore', {
		proxy: {
			type:'ajax',
			model:'GraphTXN',
			timeout:120000,
			url:'',
			reader: {
				type:'json',
//				record:'graph',
//				root:'graphml'
			}
		}
	});
	return graphStore;
} // makeTXNGraphStore

function TGgraphInit()
{
// central search version
	Ext.define("Graph", {
		extend:'Ext.data.Model'
	});
	var graphStore = Ext.create('Ext.data.JsonStore', {
		proxy: {
			type:'ajax',
			model:'Graph',
			timeout:120000,
			url:'',
			reader: {
				type:'json',
//				record:'graph',
//				root:'graphml'
			}
		}
	});
	return graphStore;
} // graphInit

				
					