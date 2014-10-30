/** 
* userDefinedSession.js
*
* History
*  11/18/2011 - Mike Martinet - Initial version
*                   Added User Defined Sessions (UDS) for graph export and import only.
*                   This uses client local storage.
*                   Future TODO: Extend to include other menu actions and use server-side storage for the UDS.
* Last updated 01/10/14 
**/

// Public Properties and Methods
//===================================================
var UDSIF = {

    // UDS - User Defined Sessions
    UDSession: {
        id: null,               // Internal id
        name: null,             // Session name
        userid: null,
        // FUTURE description: null,      // Optional user-specified description
        lastUpdated: null,      // Session creation/last updated date & time (in ms),
        replaying: false,       // Set to true when replaying
        sessionActions: [],     // User defined Actions and related data
        userPrefs: []           // holds user preferences and settings
    },

    UDSOptions: {
        showAll: false,         // if true shows the sessions from all users
        restoreButId: null
        // add other options here as needed
    },

    UDSRestoreWindow: null,
    
    graphVis: null,    // reference to the graph api (not the graph data)

    setSessionInfo: function(id, name, userid, lastupdated) {
        this.UDSession.id = id;
        this.UDSession.name = name;
        this.UDSession.userid = userid;
        this.UDSession.lastUpdated = lastupdated;
    },
    
    setSessionName: function(name) {
        this.UDSSession.name = name;
    },
    
    getUDS: function() {
        var scope = this;
        return scope.UDSSession;
    },
    
    setReplaying: function(rpflag) {
        this.UDSession.replaying = rpflag;
    },
    
    setUserid: function(userid) {
        this.UDSession.userid = userid;
    },
    
    setUserPrefs: function(up) {
        this.UDSSession.userPrefs = up; // expect an array
    },
    
    // set reference to the graph api
    setGraphVis: function(ivgr) {
        this.graphVis = ivgr;
    },
        
    // get reference to the graph api
    getGraphVis: function() {
        return this.graphVis;
    },
        
    init: function() {
        this.clearUDSession();
    },
    
    // This clears the user defined session object
    //--------------------------------------------
    clearUDSession: function() {
        this.setSessionInfo(null, null, null, null);
        this.UDSession.sessionActions = [];
        this.UDSession.replaying = false;
        this.UDSession.userPrefs = [];
    },
    
    // This adds mouse/menu actions and associated data to the user defined session
    // @param  action           
    //  action: { type: 'abbreviation for action type', data: Data or Object(can be anything) }
    //-----------------------------------------------
    addToUDSession: function(action) {
        var scope = this;
        if (this.UDSession.replaying == false) {
            if (action !== undefined && action != null) {
                if (action.type == 'sg') {  // export graph
                    // just record the action containing the exported graph
                }
                // TODO add other actions as needed
                else if (action.type == 'tbd') { // do something else TBD
                    // TODO
                }

                // Add timestamp to this action
                var vdt = new Date().getTime();
                action.ts = vdt;
                this.UDSession.sessionActions.push(action);
            }
        }
    },

    DEBUGUDSession: function() {
        console.log("UDSession = " + Ext.encode(this.UDSession));
    },

    showUDSStartMsg: function(name) {
        var scope = this;
        if (scope.UDSRestoreWindow == null) {
            scope.UDSRestoreWindow = new Ext.Window({
                title: 'User Defined Session',
                id: 'UDSStartWin',
                autoScroll: false,
                modal: true,
                closable: false,
                collapsible: false,
                resizable: false,
                draggable: false,
                html: '',   // set below
                width: 440,
                height: 100,
                y: 0
            });
        }
        scope.UDSRestoreWindow.html = '<span id="spUDSname" style="font-family: Arial, sans-serif; font-size: larger; font-weight: bold;">Restoring Session ' + name + ', please wait ...</span>';
        scope.UDSRestoreWindow.show();
        // make sure the session name is reflected in the form
        var spUDSname = document.getElementById('spUDSname');
        spUDSname.innerHTML = "Restoring Session " + name + ", please wait ...";

    },

    showUDSEndMsg: function() {
        var title = "User Defined Session";
        Ext.Msg.alert(title, 'Session restore completed.');
    },

    removeUDSMsg: function() {
        var scope = this;
        if (scope.UDSRestoreWindow) {
            scope.UDSRestoreWindow.hide();
        }
    },

    // This gets the user preference config settings
    // RETURNS: Array of settings
    //----------------------------------------------
    getUserPrefs: function() {
        var outPrefs = [];
        /** TODO
        var cfgApplication = top..config.application;

        var prefItem = { name: 'qAutoUpdate', value: cfgApplication.intervals.queueAutoUpdate };
        outPrefs.push(prefItem);
        prefItem = { name: 'gtAutoUpdate', value: cfgApplication.intervals.gtautoUpdate };
        outPrefs.push(prefItem);
        prefItem = { name: 'autoSaveUDS', value: cfgApplication.autoSaveUDS };
        outPrefs.push(prefItem);
        prefItem = { name: 'autoRestoreUDS', value: cfgApplication.autoRestoreUDS };
        outPrefs.push(prefItem);

        // Add others here as needed
        END TODO **/

        return outPrefs;
    },

    // This restores the user preference config settings
    // @param userPrefs - Array of preference settings
    //-------------------------------------------------
    restoreUserPrefs: function(userPrefs) {
        var pItem, p;
        /** TODO
        for (p = 0; p < userPrefs.length; p++) {
            pItem = userPrefs[p];
            switch (pItem.name) {
                case 'qAutoUpdate':
                    top.config.application.intervals.queueAutoUpdate = pItem.value;
                    break;

                case 'gtAutoUpdate':
                    top.config.application.intervals.gtautoUpdate = pItem.value;
                    break;


                case 'autoSaveUDS':
                    top.config.application.autoSaveUDS = pItem.value;
                    break;

                case 'autoRestoreUDS':
                    top.config.application.autoRestoreUDS = pItem.value;
                    break;

                // Add others here as needed                                            
                default:
                    break;
            }
        }
        END TODO **/
    },

    // This replays a user action
    // @param   replayItem      - One replay item/action
    //--------------------------------------
    replayAction: function(replayItem) {
        var rpData = replayItem.data;

        // DEBUG
        //console.log("replayAction: replayItem = " + Ext.encode(replayItem));

        switch (replayItem.type) {
            case 'sg':      // redisplay the saved graph
                var gJSON = rpData.graphJSON;
                var grGraphVis = UDSIF.getGraphVis();  // reference to the GraphVis api
                if (grGraphVis) {
                    grGraphVis.importGraph(gJSON);
                }
                
                break;
                
            /** TODO - others as needed
            case 'mm':  // - main menu selection
                // Action Template for Main menu selections: action: { menu: menuName, path: pageURL }
                UDSIF.showAppContent(rpData.menu, rpData.path);
                break;


            case 'sw':   // - launch window
                //   data: { menu: menuName, path: pageURL, wc: windowConfig, up: addURLparams, ko: keepOthersFlag }
                UDSIF.showAppContent(rpData.menu, rpData.path, rpData.wc, rpData.up, rpData.ko);
                break;

            case 'up':   // User preferences/config settings
                if (UDSIF.UDSession.userPrefs.length > 0) {
                    UDSIF.restoreUserPrefs(UDSIF.UDSession.userPrefs);
                }
                break;
             **/
            
            default:    // anything else - ignore
                break;
        }

    },

    // This replays a set of actions
    // @param   replayFrom   - Index into sessionActions where to start the replay from
    // @param   maxDelay     - Maximum delay between subsequent actions - in seconds
    //----------------------------------------------
    replayActions: function(replayFrom, maxDelay) {
        var scope = this;
        var sessionActions = scope.UDSession.sessionActions;
        var thisDelay, cumDelay = 0, numTimeouts = 0;
        var timeouts = [];

        if (scope.UDSession.replaying == true) {
            var actionsLen = sessionActions.length;
            var replayItem, i;

            // TODO should validate replayFrom if within range

            // replay each action
            for (i = replayFrom; i < actionsLen; i++) {
                replayItem = sessionActions[i];     // { type: type, action: actionObject, ts: timestamp }
                this.replayAction(replayItem);
            }
        }   
    },

    // This replays the user defined session
    // @pagam   sname       - User friendly Session name
    // @param   maxDelay    - Maximum delay between subsequent actions - in seconds
    //--------------------------------------
    replayUDSession: function(sname, maxDelay) {
        var scope = this;

        // DEBUG
        //console.log("replayUDSession: sessionActions.length = " + scope.UDSession.sessionActions.length);
   
        var replayFrom = 0;
        if (scope.UDSession.sessionActions.length > 0) {
            scope.showUDSStartMsg(sname);
            scope.UDSession.replaying = true;
            scope.replayActions(replayFrom, maxDelay);
            scope.removeUDSMsg();
        }
        else {
            alert("There is no Session data to restore.");
        }
        
        if (UDSIF.UDSOptions.restoreButId) {
            var vbrestore = Ext.getCmp(UDSIF.UDSOptions.restoreButId);
            if (vbrestore) {
                vbrestore.enable();
            }
        }
    },  // end of replay

    // Returns a panel allowing the user to choose the session to restore
    // @param   sessionNames    - Array store of session names
    // @param   inuserid        - This users userid
    // @param   source          - Source of data: local = Local Storage, else Server Storage
    //----------------------------------------------------------
    getUDSSessionPanel: function(inuserid, source) {

        // DEBUG
        //console.log("getUDSSessionPanel: source = " + source);

        var sessionNamesPanel = Ext.getCmp('sessionNamesPanel');
        if (sessionNamesPanel === undefined || sessionNamesPanel == null) {
            sessionNamesPanel = new Ext.grid.GridPanel({
                height: 300,
                width: 444,
                id: 'sessionNamesPanel',
                collapsible: false,
                titleCollapse: false,
                autoShow: false,
                //sm: new Ext.grid.RowSelectionModel({ singleSelect: true }),   // Row Selection Model by default
                stripeRows: true,
                autoWidth: true,
                autoScroll: true,   
                store: new Ext.data.ArrayStore({
                    fields: [
                    { name: 'id', type: 'int' },
                    { name: 'rawName', type: 'string' }, // this is the raw session name, format: <userid>_<session name>_<last updated in DTG>
                    { name: 'userid', type: 'string' },
                    { name: 'name', type: 'string' },
                    { name: 'lastUpdated', type: 'int' },    // millisecs GMT
                    ],
                    data: [],
                    sorters: [ 'userid', 'name', 'lastUpdated'],    // Added
                    proxy: new Ext.data.MemoryProxy()
                }),

                columns: [
                {
                    header: 'User',
                    id: 'userid',
                    width: 80,
                    sortable: true,
                    editable: false,
                    dataIndex: 'userid'
                },
                {
                    header: 'Session Name',
                    id: 'name',
                    width: 190,
                    sortable: true,
                    editable: false,
                    dataIndex: 'name'
                },
                {
                    header: 'Last Updated',
                    id: 'lastUpdated',
                    width: 150,
                    sortable: true,
                    editable: false,
                    autoAcroll: true,
                    dataIndex: 'lastUpdated',
                    renderer: function(value, metadata, record) {
                        if (isNaN(value) || value == undefined) {
                            return "unknown";
                        }
                        return new Date(value).toString();
                    }
                }
             ],
                
                listeners: {
                    rowDblClick: function(grid, rowIndex, e) {
                        var record = grid.getSelectionModel().getLastSelected();
                        var vId = record.data.id;
                        var vRawName = record.data.rawName;
                        var vName = record.data.name;

                        if (source == 'local') {        // Get the Session data from local storage
                            var udsItem = window.localStorage.getItem(vRawName);
                            if (udsItem !== undefined && udsItem != null) {
                                var rsWin = Ext.getCmp('mpRestoreSessionWin');
                                if (rsWin) {
                                    rsWin.close();
                                }
                                UDSIF.clearUDSession();
                                UDSIF.UDSession = Ext.decode(udsItem);
                                UDSIF.replayUDSession(vName, 1000);   // Worst case Max delay in secs between each playback action
                            }
                            else {
                                console.log("Failed to find the Session with name: " + vName + " for user id: " + userid);
                                alert("Failed to find the Session with name: " + vName + " for user id: " + userid);
                            }
                        }
                        /* NOT YET
                        else {      // Get the Session data from Server storage
                            ajax.request({
                                url: tbd.config.services.udsessions.getById.applyTemplate({ id: vId }),
                                method: 'GET',
                                success: function(response) {
                                    if (response.id !== undefined && response.id != null && response.id > 0) {
                                        var rsWin = Ext.getCmp('mpRestoreSessionWin');
                                        if (rsWin) {
                                            rsWin.close();
                                        }
                                        UDSIF.clearUDSession();
                                        UDSIF.UDSession = response;
                                        UDSIF.replayUDSession(vName,1000);   // Worst case Max delay in secs between each playback action
                                    }
                                    else {
                                        console.log("Failed to find the Session with name: " + vName + " for user id: " + userid);
                                        alert("Failed to find the Session with name: " + vName + " for user id: " + userid);
                                    }
                                },

                                failure: function(response) {
                                    console.log("Failed to find the Session with name: " + vName + " for user id: " + userid + " Response = " + Ext.encode(response));
                                    alert("Failed to retrieve the Session with name: " + vName + " for user id: " + userid);
                                }
                            });
                        }
                        **/
                    }
                }
            });

            // if showAll is false, filter the list of session names by the current userid
            var myFilter = function(record) {
                var vuserid = record.data.userid;
                // always filter out blank/empty items
                if (record.data.rawName == "") return false;

                if (UDSIF.UDSOptions.showAll == true) {
                    return true;
                }
                else {
                    if (vuserid == inuserid) {
                        return true;
                    }
                    else return false;
                }
            };

            sessionNamesPanel.store.addListener('load', function(store, records, options) {
                // Filter list of session names if necessary
                store.filterBy(myFilter);
                store.sort('lastUpdated', 'DESC');
                // TODO update the record counter  
            });
        }
        return sessionNamesPanel;
    },

    // display a window containing the list of sessions to restore
    // @param   snamesArr     - Array of session names
    // @param   userid        - Userid (sid)
    // @param   hasAdminRole  - boolean
    // @param   butid         - Optional: button id for button that invoked the restore
    // @param   source        - if 'local' use local storage, else server storage
    //--------------------------------------------------
    dispUDSWindow: function(snamesArr, userid, hasAdminRole, butid, source) {

        var sPanel = UDSIF.getUDSSessionPanel(userid, source);
        var numSessions = snamesArr.length;
        
        var rswin = new Ext.Window({
            title: 'Restore Session',
            id: 'mpRestoreSessionWin',
            collapsible: false,
            closable: true,
            draggable: true,
            modal: true,
            hideCollapseTool: true,
            resizable: true,
            maximized: false,
            minimizable: false,
            titleCollapse: false,
            height: 334,
            width: 480,
            //border:false,
            autoScroll: true,
            footer: true,
            plain: true,
            forceLayout: true,
            items: [sPanel],
            bbar: [
                    '->',
                    {
                        xtype: 'label',     // displays the total number of sessions available
                        text: ' ' + numSessions.toString() + ' Sessions',
                        style: { "margin-right": "5px" }
                    },
                    { xtype: 'tbseparator' },
                    {
                        xtype: 'checkbox',
                        id: 'chkUDSShowAll',
                        margin: '1 4 1 4',
                        instance: this,
                        listeners: {
                            check: function(cmp, checked) {
                                var gs = sPanel.getStore();
                                // clicked Show All
                                if (checked === true) {
                                    UDSIF.UDSOptions.showAll = true;
                                }
                                else {
                                    UDSIF.UDSOptions.showAll = false;
                                }
                                if (gs !== undefined && gs != null) {
                                    gs.loadData(snamesArr);
                                    sPanel.getView().refresh();
                                }
                            }
                        }
                    },
                    {
                        xtype: 'label',
                        text: ' Show All Sessions',
                        style: { "margin-right": "5px" },
                        listeners:
                        {
                            render: function(cmp) {
                                cmp.el.on('click', function() {
                                    var chkShowAll = Ext.getCmp('chkUDSShowAll');
                                    if (chkShowAll.getValue() === true)
                                        chkShowAll.setValue(false);
                                    else
                                        chkShowAll.setValue(true);
                                });
                            }
                        }
                    },
                    { xtype: 'tbseparator' },
                    {
                        id: 'btnUDSDelete',
                        text: '&nbsp;&nbsp;DELETE', // needs 2 spaces at the front
                        //icon: $images.choose,
                        disabled: !(hasAdminRole),  // enabled only if user has admin role
                        cls: 'x-toolbar-standardbutton',
                        listeners: {
                            click: function(but, e) {
                                var spGrid = Ext.getCmp('sessionNamesPanel');
                                if (spGrid) {
                                    var record = spGrid.getSelectionModel().getLastSelected();
                                    if (record) {
                                        var vselName = record.data.rawName;
                                        // DEBUG
                                        //console.log("Delete: Selected name = " + vselName);

                                        Ext.Msg.confirm("Confirm",
                                            "Are you sure you want to permanently delete this Session?",
                                            function(btn) {
                                                if (btn == "yes") {
                                                    window.localStorage.removeItem(vselName);   // removes the UDS itself
                                                    UDSIF.deleteUDSessionName(vselName);   // removes the name of the UDS from the list of names
                                                    spGrid.getStore().remove(record);
                                                    spGrid.getView().refresh();
                                                }
                                            }
                                        );
                                    }
                                    else {
                                        alert("Please select a Session first.");
                                    }
                                }
                            }
                        }
                    },
                    { xtype: 'tbseparator' },
                    {
                        id: 'btnUDSRestore',
                        text: '&nbsp;&nbsp;RESTORE', // needs 2 spaces at the front
                        //icon: jcms.config.images.choose, Add choose icon for restore
                        disabled: false,
                        cls: 'x-toolbar-standardbutton',
                        listeners: {
                            click: function(but, e) {
                                var spGrid = Ext.getCmp('sessionNamesPanel');
                                if (spGrid) {
                                    var record = spGrid.getSelectionModel().getLastSelected();
                                    if (record) {
                                        var vId = record.data.id;
                                        var vRawName = record.data.rawName; // internal raw session name
                                        var vName = record.data.name;       // user friendly name

                                        // DEBUG
                                        //console.log("Restore: Raw Session Name = " + vRawName);
                                        
                                        if (source == 'local') {
                                            var udsItem = window.localStorage.getItem(vRawName);

                                            if (udsItem !== undefined && udsItem != null) {
                                                // disable the button (if any) that triggered restore until the restore is completed
                                                if (but) {
                                                    but.disable();
                                                    UDSIF.UDSOptions.restoreButId = but.id; // save for later
                                                }

                                                var rsWin = Ext.getCmp('mpRestoreSessionWin');
                                                if (rsWin) {
                                                    rsWin.close();
                                                }
                                                UDSIF.clearUDSession();
                                                UDSIF.UDSession = Ext.decode(udsItem);
                                                // DEBUG
                                                //console.log("restoreUDSession: UDSession = " + Ext.encode(UDSIF.UDSession));

                                                UDSIF.replayUDSession(vName,1000);   // Worst case Max delay secs between each playback action
                                            }
                                            else {
                                                console.log("Failed to find the Session with name: " + vName + " for user id: " + userid);
                                                alert("Failed to find the Session with name: " + vName + " for user id: " + userid);
                                            }
                                        }
                                        /** NOT YET
                                        else {  // get from server storage
                                            ajax.request({
                                                url: tbd.config.services.udsessions.getById.applyTemplate({ id: vId }),
                                                method: 'GET',
                                                success: function(response) {
                                                    if (response.id !== undefined && response.id != null && response.id > 0) {
                                                        var rsWin = Ext.getCmp('mpRestoreSessionWin');
                                                        if (rsWin) {
                                                            rsWin.close();
                                                        }
                                                        UDSIF.clearUDSession();
                                                        UDSIF.UDSession = response;
                                                        UDSIF.replayUDSession(vName,1000);   // Worst case Max delay in secs between each playback action
                                                    }
                                                    else {
                                                        console.log("Failed to find the Session with name: " + vName + " for user id: " + userid);
                                                        alert("Failed to find the Session with name: " + vName + " for user id: " + userid);
                                                    }
                                                },

                                                failure: function(response) {
                                                    console.log("Failed to retrieve the Session with name: " + vName + " for user id: " + userid + " Response = " + Ext.encode(response));
                                                    alert("Failed to retrieve the Session with name: " + vName + " for user id: " + userid);
                                                }
                                            });
                                        }
                                        **/
                                    }
                                    else {
                                        alert("Please select a Session first.");
                                    }
                                }
                            }
                        }
                    },
                    { xtype: 'tbseparator' },
                    {
                        id: 'btnUDSCancel',
                        text: '&nbsp;&nbsp;CANCEL', // needs 2 spaces at the front
                        //icon: $images.choose,
                        disabled: false,
                        cls: 'x-toolbar-standardbutton',
                        listeners: {
                            click: function(but, e) {
                                var rsWin = Ext.getCmp('mpRestoreSessionWin');
                                if (rsWin) {
                                    rsWin.close();
                                }
                            }
                        }
                    }
               ]
        });

        // have to delay rendering of the window a bit
        var jto = window.setTimeout(function() {
            window.clearTimeout(jto);
            rswin.show();
            sPanel.store.loadData(snamesArr);
        }, 2000);

    },

    // Restore the user defined session from Server Storage and if that fails try from Local Storage
    // @param   userid      - User id
    // @param   graphVis    - reference to the graph API
    // @param   butid       - Optional. Ext id of button or menu item that invoked this function
    //---------------------------------------
    restoreUDSession: function(userid, graphVis, butid) {

        if (graphVis) {
            UDSIF.setGraphVis(graphVis);
        }
        // If user has an admin role allow Delete of a Session
        var hasAdminRole = true;

        // Get the session names from Server storage
        /** NOT YET
        ajax.request({
            url: tbd.config.services.udsessions.getAllNames,
            method: 'GET',
            success: function(response) {
                if (response.length > 0 && response[0].id !== undefined && response[0].id != null && response[0].id > 0) {
                    var snamesArr = [];
                    var n, sitem, rawName;
                    for (n = 0; n < response.length; n++) {
                        sitem = response[n];
                        rawName = sitem.userid + "_" + sitem.name + "_" + sitem.lastUpdated.toString();
                        // format of raw session name is:  <userid>_<session name>_<last updated datetime>
                        snamesArr.push([sitem.id, rawName, sitem.userid, sitem.name, sitem.lastUpdated]);
                    }

                    // Display Panel for user to select the session name
                    UDSIF.dispUDSWindow(snamesArr, userid, hasAdminRole, butid, 'server');

                }
                else {
                    console.log("Failed to get the list of sessions from Server Storage. Response = " + Ext.encode(response));
                    alert("INFO: The sessions could not be retrieved from Server Storage,\n will attempt retrieval from Client Storage. Hit Ok to continue.");

                    UDSIF.restoreUDSessionLS(userid, hasAdminRole, butid);
                }
            },

            failure: function(response) {
                console.log("Failed to get the list of sessions from Server Storage. Response = " + Ext.encode(response));
                alert("INFO: The sessions could not be retrieved from Server Storage,\n will attempt retrieval from Client Storage. Hit Ok to continue.");
       
                UDSIF.restoreUDSessionLS(userid, hasAdminRole, butid);
             }
        });
        ** END NOT YET */
       
       // Will attempt retrieval from Client Storage.
       UDSIF.restoreUDSessionLS(userid, hasAdminRole, butid);
    },

    // Restore the user defined session from local storage
    // @param   userid      - User id
    // @param   butid       - Optional. Ext id of button or menu item that invoked this function
    //-------------------------------------------------------
    restoreUDSessionLS: function(userid, hasAdminRole, butid) {
        var lsUDSNamesArr = this.getUDSessionNames();

        // DEBUG
        //console.log("restoreUDSession: userid = " + userid + ", lsUDSNamesArr = " + Ext.encode(lsUDSNamesArr));

        var wls = window.localStorage;

        // Check for the Session in local storage
        if (wls) {
            var sitemArr = [], snamesArr = [];
            var n, sitem;
            if (lsUDSNamesArr.length > 0) {
                for (n = 0; n < lsUDSNamesArr.length; n++) {
                    sitem = lsUDSNamesArr[n];
                    sitemArr = sitem.split("_");
                    // format of raw session name is:  <userid>_<session name>_<last updated datetime>
                    // first item below is the raw session name (mapped to id field)
                    if (sitem && sitem != "") {
                        // first item is a fake id number
                        snamesArr.push([n + 1, sitem, sitemArr[0], sitemArr[1], parseInt(sitemArr[2])]);
                    }
                }

                // Display Panel for user to select the session name
                UDSIF.dispUDSWindow(snamesArr, userid, hasAdminRole, butid, 'local');
            }
            else {
                alert("There are currently no Sessions saved");
            }
        }
        else {
            console.log("window.localStorage is not supported!");
            alert("Error: window.localStorage is not supported by your Browser! Please contact your System Administrator.");
        }
    },

    // RETURNS: array of session names for all users from Local Storage
    //-----------------------------------------------------------------
    getUDSessionNames: function() {
        var outNames = [];
        var lsUDSNamesArr = [];
        var udsName = "", n;

        if (window.localStorage) {
            var lcUDSNames = window.localStorage.udsNames;
            if (lcUDSNames) {   // just a comma separated list
                lsUDSNamesArr = lcUDSNames.split(",");
                for (n = 0; n < lsUDSNamesArr.length; n++) {
                    udsName = lsUDSNamesArr[n];
                    outNames.push(udsName);
                }
            }
        }
        return outNames;
    },

    // Deletes a session name from the list of session names in Local Storage
    // @param   inudsName - The user defined session name (id) to delete
    //----------------------------------------
    deleteUDSessionName: function(inudsName) {
        var outNames = [];
        var lsUDSNamesArr = [];
        var udsName = "", n;

        if (window.localStorage) {
            var lcUDSNames = window.localStorage.udsNames;
            if (lcUDSNames) {   // just a comma separated list
                lsUDSNamesArr = lcUDSNames.split(",");
                for (n = 0; n < lsUDSNamesArr.length; n++) {
                    udsName = lsUDSNamesArr[n];
                    if (udsName.indexOf(inudsName) < 0) {
                        outNames.push(udsName);
                    }
                }
                lcUDSNames = "";
                for (n = 0; n < outNames.length; n++) {
                    udsName = outNames[n];
                    lcUDSNames = lcUDSNames + "," + udsName;
                }
                window.localStorage.udsNames = lcUDSNames;
            }
        }
    },

    // Does the real work of saving a user defined session
    // @param   udsname     - The raw (fully qualified) session name
    // @parem   userid      - The user id (sid)
    // @param   name        - The user friendly name
    // @param   mode        - "silent" or "vocal" -> show an alert that the session was saved
    //-------------------------------------
    doSaveUDS: function(udsname, userid, name, mode) {
        ///var scope = this; // can't use reference to this here
        var uPrefs = [];
        var udss = UDSIF.UDSession; // getUDS() fails and returns undefined
        var wls = window.localStorage;

        // DEBUG
        //console.log("doSaveUDS: usdname = " + udsname);
        
        // Save the user preferences/config settings
        //uPrefs = scope.getUserPrefs();
        //var dconfig2 = { userPrefs: uPrefs };
        //var UDSaction = { appName: null, type: "up", action: { apply: true} };
        //scope.addToUDSession(dconfig2, UDSaction);

        var ts = new Date().getTime();  // timestamp
        UDSIF.setSessionInfo(name + "_" + userid + "_" + ts.toString(), name, userid, ts);

        // TODO - NEED TO trim the amount of actions in the session to only keep things since the most recent main menu selection

        // Attempt save in Server DB first, if that fails try saving in Client local storage
        /* NOT YET
        Ext.Ajax.request({
            url: Config.saveUDSessionUrl, 
            method: 'POST',
            headers: {
                    'Content-Type': header
            },
            jsonData: Ext.encode(udss), 

            success: function(response) {
                    // DEBUG
                    console.log("resp = " + resp.responseText);

                if (response.id !== undefined && response.id != null && response.id > 0) { 
STOP
                }
                else {
                   
STOP
                }      
            },
            failure: function(resp) {
                    var err = new Ext.Window({
                            title: 'Save Session Failure',
                            height: 300,
                            width: 600,
                            html: resp.responseText
                    });

                    err.show();
            }
        }); 
                                        
        // NOT YET FROM OLD PROJECT
        ajax.request({
            url: tbd.config.services.udsessions.save,
            method: 'POST',
            jsonData: this.UDSession,

            success: function(response) {
                if (response.uid !== undefined && response.uid != null && response.uid > 0) {   // Changed _id to uid
                    if (mode && mode != "silent") {
                        alert("The session was saved with name: " + name + " at " + new Date(scope.getUDS().lastUpdated).toString());
                    }
                }
                else {
                    try {   // May get too much recursion error from the encode
                        wls.setItem(udsname, Ext.encode(scope.getUDS()));   // write to local storage
                    }
                    catch (rexp) {
                        if (mode && mode != "silent") {
                            alert("Failed to save the session named " + name + " due to an error: " + rexp.name + ": " + rexp.message);
                        }
                        else {
                            console.log("Failed to save the session named " + name + " due to an error: " + rexp.name + ": " + rexp.message);
                        }
                    }

                    // Update the list of user defined session names
                    var lcUDSNames = wls.udsNames;
                    if (lcUDSNames) {   // just a comma separated list
                        if (lcUDSNames.indexOf(udsname) < 0) {
                            lcUDSNames = lcUDSNames + "," + udsname;
                            wls.udsNames = lcUDSNames;
                        }
                    }
                    else {
                        wls.udsNames = udsname;
                    }
                    alert("INFO: The session could not be saved in Server Storage but was saved in Client Storage with name: " + name + " at " + DateFormatter.toDTG(instance.UDSession.lastUpdated));
                }
            },

            failure: function(response) {
                console.log("Failed to save the session named " + name + " in Server Storage. Response = " + Ext.encode(response));

                try {   // May get too much recursion error from the encode
                    wls.setItem(udsname, Ext.encode(instance.UDSession));   // write to local storage
                }
                catch (rexp) {
                    if (mode && mode != "silent") {
                        alert("Failed to save the session named " + name + " due to an error: " + rexp.name + ": " + rexp.message);
                    }
                    else {
                        console.log("Failed to save the session named " + name + " due to an error: " + rexp.name + ": " + rexp.message);
                    }
                }

                // Update the list of user defined session names
                var lcUDSNames = wls.udsNames;
                if (lcUDSNames) {   // just a comma separated list
                    if (lcUDSNames.indexOf(udsname) < 0) {
                        lcUDSNames = lcUDSNames + "," + udsname;
                        wls.udsNames = lcUDSNames;
                    }
                }
                else {
                    wls.udsNames = udsname;
                }
                alert("INFO: The session could not be saved in Server Storage but was saved in Client Storage with name: " + name + " at " + DateFormatter.toDTG(instance.UDSession.lastUpdated));
            }
        });
        ** END NOT YET **/
       
       // Save in local storage
       try {   // May get too much recursion error from the encode
            wls.setItem(udsname, Ext.encode(udss));   // write to local storage
       }
       catch (rexp) {
            if (mode && mode != "silent") {
                alert("Failed to save the session named " + name + " due to an error: " + rexp.name + ": " + rexp.message);
            }
            else {
                console.log("Failed to save the session named " + name + " due to an error: " + rexp.name + ": " + rexp.message);
            }
       }

        // Update the list of user defined session names
        var lcUDSNames = wls.udsNames;
        if (lcUDSNames) {   // just a comma separated list
            if (lcUDSNames.indexOf(udsname) < 0) {
                lcUDSNames = lcUDSNames + "," + udsname;
                wls.udsNames = lcUDSNames;
            }
        }
        else {
            wls.udsNames = udsname;
        }
        
        var ld = udss.lastUpdated;
        alert("The session was saved with name: " + name + " at " + new Date(ld).toString());
    },

    // This saves the user defined session with the specified userid + name
    // @param   userid
    // @param   sessionname - optional, if null or missing prompt for the session name
    //-----------------------------------------
    saveUDSession: function(userid, sessionname) {
        var udsname;

        if (sessionname === undefined || sessionname == null) {
            if (this.UDSession.sessionActions.length == 0) {
                alert("There are no new actions to save. Please select an item from the Main Menu first.");
                return;
            }

            Ext.Msg.prompt('Session Name', 'Name:', function(btn, name) {
                if (btn == 'ok') {
                    // make sure the name does not contain any "_"
                    name = name.replace("_", ".");
                    
                    // Attempt save in Server DB first, if that fails try saving in Client local storage
                    udsname = userid + "_" + name + "_" + (new Date().getTime()).toString();
                    UDSIF.doSaveUDS(udsname, userid, name, "vocal");
                }
                else return;
            });
        }
        else {  // session name supplied by caller
            udsname = userid + "_" + sessionname + "_" + (new Date().getTime()).toString();
            UDSIF.doSaveUDS(udsname, userid, sessionname, "silent");
        }
    }  
}  // end UDS
