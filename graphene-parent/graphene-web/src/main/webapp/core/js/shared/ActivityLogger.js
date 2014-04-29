/*======================================================================
*======================JAVASCRIPT ACTIVITY LOGGER=======================
*=====================Draper Laboratory, June 2013======================
*
* This library is intended for use for javascrip software component and 
* webapp developers implemnting the XDATA Activity Logging API. To send 
* activity log messages using this libary, components must:
*          1) Instantiate an ActivityLogger object
*          2) Call registerActivityLogger(...) to pass in required networking 
*             and version information.
*          3) Call one of the logging functions:
*               logSystemActivity(...)
*               logUser(...)
*               logUILayout(...)
*
* An example use of this library is included below:
     
     <script src="ActivityLogger.js"></script>
     <script>
        //Instantiate the Activity Logger
        var ac = new activityLogger();


        //Register the logger.
        //In this case, we register the logger to look for the logging server
        //on port 1337 of the machine "localhost". The component name is 
        //left blank, so it will default to the address of this web app. 
        //The software component version is 3.04  and the session ID is 
        //"AC34523452345"
          ac.registerActivityLogger("http://localhost:1337", "", "3.04", 
        "AC34523452345");

          //Re-register the logger.
          //In this case, we register the logger to look for the logger on 
          //port 1337 of the machine "localhost", telling it that this 
          //software component is version 3.04 of the software named "Draper
          //Test Component" and that the session ID is "AC34523452345"
          ac.registerActivityLogger("http://localhost:1337", 
               "Draper Test Component", "3.04", "AC34523452345");
          
          //Send a System Activity Message with optional metadata included.
          //In this case, we send a System Activity message with the 
          //action description 'Testing System Activity Message' and optional
          //metadata with two key-value pairs of:
          // 'Test Window Val'='Main
          // 'Data Source'='healthcare'
          ac.logSystemActivity('Testing System Activity Message',  
               {'Test Window Val':'Main', 'Data Source':'healthCare'});
          
          //Send a System Activity Message
          //In this case, we send a System Activity message with the 
          //action description 'Testing System Activity Message'
          ac.logSystemActivity('Testing System Activity Message');
          
          //Send a User Activity Message
          //In this case, we send a User Activity message with the 
          //action description 'Testing User Activity Message', a 
          //developer-defined user action "watch", and the 
          //workflow constant WF_EXAMINE, defined in the Activity
          //Log API.
          ac.logUserActivity('Testing User Activity Message', 'Watch', ac.WF_EXAMINE );
          
          //Send a User Activity Message with optional metadata included
          //In this case, we send a User Activity message with the 
          //action description 'Testing User Activity Message', a 
          //developer-defined user action "watch", and a
          //the workflow constant WF_EXAMINE, defined in the Activity
          //Log API. This message also contains optional
          //metadata with two key-value pairs of:
          // 'Test Window Val'='Main
          // 'Data Source'='healthcare'
          ac.logUserActivity('Testing User Activity Message', 'watch', ac.WF_EXAMINE, 
               {'Test Window Val':'Main', 'Data Source':'healthCare'});
          
          //Send a UI Layout Message
          //In this case, we send a UI Layout message with action description of
          //'Testing User Activity Message'. The name of the UI element is 
          //'SearchWindow A', visibility=true, meaning SearchWindow A is currently
          //visible. The left, right, top and bottom bounds of the UI element are 
          // 234px, 256px, 33px, and 500px from the top right of the screen. 
          ac.logUILayout('Testing User Activity Message', 'SearchWindow A', true, 
               234, 256, 33, 500);
          
          //Send a UI Layout Message with optional metadata included
          //In this case, we send a UI Layout message with action description of
          //'Testing User Activity Message'. The name of the UI element is 
          //'SearchWindow A', visibility=true, meaning SearchWindow A is currently
          //visible. The left, right, top and bottom bounds of the UI element are 
          // 234px, 256px, 33px, and 500px from the top right of the screen. This 
          //message also contains optional metadata with two key-value pairs of:
          //    'Test Window Val'='Main
          //    'Data Source'='healthcare'
          ac.logUILayout('Testing User Activity Message', 'SearchWindow A', true, 
               234, 256, 33, 500, {'Test Window Val':'Main', 'Data Source':'healthCare'});
*
*/

function activityLogger()
{
     /*========================INTERNAL CONSTANTS========================
     *
     * These constant define values associated with this specific version
     * of this library, and should not be changed by the implementor.
     */

     // The version number of the Draper Activity Logging API implemented
     // by this library.
     var apiVersion = 2;

     //The workflow coding version used by this Activity Logging API. 
     var workflowCodingVersion = 1;
     
     /*      WORKFLOW CODES
     * These constants specify the workflow codes defined in the Draper
     * Activity Logging API version <apiVersion>. One of these
     * constants must be passed in the parameter <userWorkflowState>
     * in the function <logUserActivity> below. 
     */
     this.WF_OTHER       = 0;
     this.WF_PLAN        = 1;
     this.WF_SEARCH      = 2;
     this.WF_EXAMINE     = 3;
     this.WF_MARSHAL     = 4;
     this.WF_REASON      = 5;
     this.WF_COLLABORATE = 6;
     this.WF_REPORT      = 7;


     // The domain for all structured data elements necessary to send
     // IETF RCF 5424 compliant Syslog messages. 15038 is Draper Lab's 
     // IANA Private Enterprise Number.
     var structuredDataDomain = 15038;

     // The language in which this helper library is implemented
     var implementationLanguage = "JavaScript";

     // If true, this library has updated the IP address of the 
     // Computer on which it is running with information from 
     // the Activity Logging Server, or <registerActivityLogger>. 
     var clientsHostnameUpdated = false;

     //====================END INTERNAL CONSTANTS========================


     /*======================== REGISTRATION ============================
     * These variables are assigned by calling the 
     * <registerActivityLogger> function below. They are persistent until
     * a new ActivityLogger object is instantiated, or until modification
     * by the <registerActivityLogger> function. 
     */

     /* Register this event logger. <registerActivityLogger> MUST be 
     * called before log messages can be sent with this library. 
     * 
     * PARAMETERS:
     *  @activityLogServerIN: String. The address of the logging server.           
     *    See documentation for <activityLogServerURL> below. 
     *  @componentNameIN: String. The name of the app or component using 
     *    this library. See documentation for <componentName> below. If 
    *    the empty string, defaults to the hostname of the web app that 
    *    loaded this library
     *  @componentVersionIN String. The version of this app or 
       *    component. See documentation for <componentVersion> below. 
    *    If the empty string, defaults to 'unknown'.
     *  @sessionIdIN String. A unique ID for the current user session. 
     *    See documentation for <sessionID> below.  If the empty string, 
    *    defaults to a random integer.
     *  @clientHostnameIN: Optional. String. The hostname or IP address 
          of this machine or VM. See documentation for 
          <clientHostname> below. 
     */
     this.registerActivityLogger = registerActivityLogger;
     function registerActivityLogger(activityLogServerIN, componentNameIN, componentVersionIN, sessionIdIN, clientHostnameIN)
     {
          activityLogServerURL = activityLogServerIN;
          if(componentNameIN != "")
          {
                   componentName= componentNameIN;
          }

          if(componentVersionIN != "")
          {
                   componentVersion = componentVersionIN;
          }

          if(sessionIdIN != "")
          {
                   sessionID = sessionIdIN;
          }

          if (clientHostnameIN) 
          {
              clientHostname = clientHostnameIN;
              clientsHostnameUpdated = true;
          }
     }

     /* The fully-qualified address of the logging server that will 
     * collect messages dispatched by this library.
     *
     * Example: "http://example.com:1337"
     * 
     * During XDATA Summer Camp 2013, contact James Remeika at 
     * jremeika@draper.com for the address of the Activity Log Server
     */
     var activityLogServerURL;

     /* The hostname of the computer or VM on which the software 
     * component using this library is runing. In the case of a web-            
     * based user interface component, this should be the host name of
     * the computer on which the web browser displaying the UI is
     * running. By default, this field will be populated with the 
        * IP address of the client machine as seen by the Logging Server.
     *
     * Ideally, this hostname should describe a physical terminal or 
     * experimental setup as persistently as possible.
      */
     var clientHostname = "xdataClient";

     // The name of the software component or application sending log 
     // messages from this library. Defaults to the location of the 
         // web app loading this library.
     var componentName = location.host;

     // The version number of the software component or application 
     // specified in <clientHostname> that is sending log  messages from 
     // this library.
     var componentVersion = "unknown";

     /* The unique session ID used for communication between client and
     * sever-side software components during use of this component. 
     * Defaults to a random integer.
     *
     * Ideally, this session ID will identify log messages from all
     * software components used to execute a unique user session.
     */ 
     var sessionID = Math.floor(Math.random() * 10,000 );

     //========================END REGISTRATION==========================

     /*====================DEVELOPMENT FUNCTIONALITY=====================
     * The properties and function in this section allow developers to 
     * echo log messages to the console, and disable the generation and 
     * transmission of logging messages by this library. 
     */
     
     //Set to <true> to echo log messages to the console, even if they 
     //are sent sucessfully to the Logging Server.
     var echoLogsToConsole = false;

     this.echo = function(_) {
          if (!arguments.length) return echoLogsToConsole;
          echoLogsToConsole = _;
          return this;
     };
     // var echoLogsToConsole = false;

     //Set to <true> to disable System Activity log messages.
     this.muteSystemActivityLogging = muteSystemActivityLogging;
     var muteSystemActivityLogging = false;

     //Set to <true> to disable User Activity log messages
     this.muteUserActivityLogging = muteUserActivityLogging;
     var muteUserActivityLogging = false;

     //Set to <true> to disable UI Layout log messages
     this.muteUILayoutLogging = muteUILayoutLogging;
     var muteUILayoutLogging = true;

     // Disable all log messages
     this.muteAllLogging = muteAllLogging;
     function muteAllLogging()
     {
          muteSystemActivityLogging = true;
          muteUserActivityLogging = true;
          muteUILayoutLogging = true;
     }

     //Enable all log messages
     this.unmuteAllLogging = unmuteAllLogging;
     function unmuteAllLogging()
     {
          muteSystemActivityLogging = false;
          muteUserActivityLogging = false;
          muteUILayoutLogging = false;
     }
     
     //=================END DEVELOPMENT FUNCTIONALITY====================

     
     /*==================ACTIVITY LOGGING FUNCTIONS======================
     * The 3 functions in this section are used to send Activity Log
     * Mesages to an Activity Logging Server. Seperate functions are used
     * to log System Activity, User Activity, and UI Layout Events. See 
     * the Activity Logging API by Draper Laboratory for more details 
     * about the use of these messages.
     */
     
     /* Log a System Activity. <registerActivityLogger> MUST be 
     * called before calling this function. Use <logSystemActivity> to 
     * log software actions that are not explicitly invoked by the user.
     * For example, if a software component refreshes a data store after 
     * a pre-determined time span, the refresh event should be logged as 
     * a system activity. However, if the datastore was refreshed in 
     * response to a user clicking a Reshresh UI element, that activity
     * should NOT be logged as a System Activity, but rather as a User 
     * Activity.
     * 
     * PARAMETERS:
     *  @actionDescription: String. A string describing the System 
     *    Activity performed by the component. Example: 
          "CustomerAccountTableView component refreshed datasource"
     *  @softwareMetadata: JSON String. Optional. Any key/value 
     *    pairs that will clarify or paramterize this system activity.
     *    Example: "{'rowsAdded':'3', 'dataSource':'CheckingAccounts'}"
     */
     this.logSystemActivity = logSystemActivity;
     function logSystemActivity(actionDescription, softwareMetadata)
     {
          var encodedSystemActivityMessage = "";
          if(!muteSystemActivityLogging)
          {
               msg = writeHead();
               msg.type = "SYSACTION";
               msg.parms = {
                    desc: actionDescription
               }
               msg.meta = softwareMetadata;
               sendHttpMsg(msg);
          }

          return msg;
     }

     /* Log a User Activity. <registerActivityLogger> MUST be 
     * called before calling this function. Use <logUserActivity> to 
     * log actions initiated by an explicit user action. For example, 
     * if a software component refreshes a data store when the user
     * clicks a Reshresh UI element, that activity should be logged 
     * as a User Activity. However, if the datastore was refreshed 
     * automatically after a certain time span, that activity should 
     * NOT be logged as a User Activity, but rather as a System 
     * Activity.
     * 
     * PARAMETERS:
     *  @actionDescription: String. A string describing the System 
     *    Activity performed by the component. Example: 
     *    "CustomerAccountTableView component refreshed datastore."
     *  @userActivity: String. A key word defined by each software 
     *    component or application indicating which software-centric
     *    function is is most likely indicated by the this user 
     *    activity. See the Activity Logging API for a standard 
    *    set of user activity key words. 
     *  @userWorkflowState: Integer. This value must be one of the 
     *    Workflow Codes defined in this library. See the Activity
     *    Logging API for definitions of each workflow code. 
     *    Example: 
     *         var ac = new ActivityLogger();
     *       ...
     *       var userWorkflowState = ac.WF_SEARCH;
     *  @softwareMetadata: JSON String. Optional. Any key/value 
     *    pairs that will clarify or paramterize this system activity.
     *    Example: "{'rowsAdded':'3', 'dataSource':'CheckingAccounts'}"
     */
     this.logUserActivity = logUserActivity;
     function logUserActivity(actionDescription, userActivity, userWorkflowState, softwareMetadata)
     {
          var encodedSystemActivityMessage = "";

          if(!muteUserActivityLogging)
          {
               msg = writeHead();
               msg.type = "USERACTION";
               msg.parms = {
                    desc: actionDescription,
                    activity: userActivity,
                    wf_state: userWorkflowState,
                    wf_version: workflowCodingVersion                     
               }
               msg.meta = softwareMetadata;
               sendHttpMsg(msg);
          }
          return msg;
     }

     //
     /* Log the Layout of a UI Element. <registerActivityLogger> 
     * MUST be called before calling this function. Use 
     * <logUILayout> to record any changes to the position or
     * visibility of User Interface elements on screen.
     * 
     * PARAMETERS:
     *  @actionDescription: String. A string describing the System 
     *    Activity performed by the component. Example: 
     *    "CustomerAccountTableView moved in User_Dashboard"
     *  @uiElementName: String. The name of the UI component that 
     *    has changed position or visibility.
     *  @visibility: Boolean. True if the element is currently 
     *    visibile. False if the element is currently hidden.
     *  @leftBound: Integer. The absolute position on screen, in pixels
          of the leftmost boundary of the UI element.  
     *  @rightBound: Integer. The absolute position on screen, in pixels
          of the rightmost boundary of the UI element. 
     *  @topBound: Integer. The absolute position on screen, in pixels
          of the top boundary of the UI element. 
     *  @bottomBound: Integer. The absolute position on screen, in pixels
          of the bottom boundary of the UI element. 
     *  @softwareMetadata: JSON String. Optional. Any key/value 
     *    pairs that will clarify or paramterize this system activity.
     *    Example: "{'currentDashboardRow':'3', 'movementMode':'Snap_To_Grid'}"
     */
     this.logUILayout = logUILayout;
     function logUILayout(actionDescription, uiElementName, visibility, leftBound, rightBound, topBound, bottomBound, softwareMetadata)
     {
          var encodedSystemActivityMessage = "";

          if(!muteUILayoutLogging)
          {
               msg = writeHead();
               msg.type = "UILAYOUT";
               msg.parms = {
                    desc: actionDescription,
                    visibility: visibility,
                    leftBound: leftBound,
                    rightBound: rightBound,
                    topBound: topBound,
                    bottomBound: bottomBound
               }
               msg.meta = softwareMetadata;
               sendHttpMsg(msg);
          }
          // return msg;
     }

     //=================END ACTIVITY LOGGING FUNCTIONS========================

     /*=========================INTERNAL FUNCTIONS============================
     * These functions are used internally by the Activity Logger helper 
     * library to generate RCF5424 Syslog messages, and transmit them via 
     * HTTP POST messages to an Activity Logging server. 
     */

     //basic class for sending HTTP messages to Activity Logging server
     var httpConnection = new XMLHttpRequest();
     httpConnection.timeout = 300;
     httpConnection.addEventListener("load", doneAlert, false);
     httpConnection.addEventListener("error", errorAlert, false);
     httpConnection.addEventListener("abort", errorAlert, false);
     
     var busy = false;
     var nextPlaceInLine = 0;
     var ticketServed = 0;
     var waitTimeMS = 3

     var currentXHRPayload = "";

     function sendHttpMsg(encodedLogMessage, placeInLine)
     {
          if(!placeInLine)
          {
               placeInLine = nextPlaceInLine++;
          }

          // console.log(echoLogsToConsole)
          if(echoLogsToConsole){
               console.log(encodedLogMessage);
          }

          if(busy){
               setTimeout(function(){sendHttpMsg(encodedLogMessage, placeInLine);}, waitTimeMS);
          }else if (placeInLine>ticketServed)
          {
               setTimeout(function(){sendHttpMsg(encodedLogMessage, placeInLine);}, (placeInLine-ticketServed)*waitTimeMS );
          }else{

               if (activityLogServerURL) {
                    currentXHRPayload = encodedLogMessage;
                    ticketServed++;
                    busy = true;
                    httpConnection.open("POST", activityLogServerURL, true);
                    httpConnection.send(JSON.stringify(encodedLogMessage));
               }
          }
     }

     function doneAlert(evt)
     {
          busy = false;
          if(!clientsHostnameUpdated)
          {
               var oldClientHostname = clientHostname;
               clientHostname = this.responseText;
               console.log(clientHostname);
               clientsHostnameUpdated = true;
               logSystemActivity("Client hostname changed from " + oldClientHostname + " to " + clientHostname);
          }
     }

     function errorAlert(evt)
     {
          console.log(currentXHRPayload);
          busy = false;
     }

     function writeHead() {
          var msg = {}

          msg.timestamp = new Date();
          msg.client = clientHostname;
          msg.component = {name:componentName, version:componentVersion};
          msg.sessionID = sessionID;
          msg.impLanguage = implementationLanguage;
          msg.apiVersion = apiVersion;

          return msg;
     }

     function writeHeader()
     {
          var currentTimestamp = new Date();
          var encodedClientHostname = "-";
          var encodedComponentName = "-";

          var encodedSystemActivityMessage =      "<134>1 " 
                                   + currentTimestamp.toISOString() + " "
                                   + (clientHostname != "" ? removeWhiteSpace(clientHostname) : "-") + " " 
                                   + (componentName != "" ? removeWhiteSpace(componentName) : "-") + " ";
          return encodedSystemActivityMessage;

     }

     //Write the required API version structured data element
     function writeVersionData()
     {
          var versionNumbers = new Object();
          versionNumbers["componentVersion"] = componentVersion;
          versionNumbers["apiVersion"] = apiVersion;
          versionNumbers["implentationLanguage"] = implementationLanguage;
          return writeSDE("versions", versionNumbers);
     }

     //Write the required Activity structured data element
     function writeWorkflowCode(userActivity, userWorkflow)
     {
          var workflowCode = new Object();

          workflowCode["USER_ACTIVITY"] = userActivity;
          workflowCode["USER_WF"] = userWorkflow;
          workflowCode["wfCodeVersion"] = workflowCodingVersion;

          return writeSDE("USER_ACTION", workflowCode);
     }

     //Write the UI Layout structured data element
     function writeUILayoutData(uiElementName, visibility, leftBound, rightBound, topBound, bottomBound)
     {
          var UILayout = new Object();
          UILayout["uiElementName"] = uiElementName;
          
          if(visibility){
               UILayout["visibility"] = "true";
          }
          else{
               UILayout["visibility"] = "false";
          }
          UILayout["leftBound"] = leftBound;
          UILayout["rightBound"] = rightBound;
          UILayout["topBound"] = topBound;
          UILayout["bottomBound"] = bottomBound;

          return writeSDE("uiLayoutData", UILayout);
     }

     //Write any metadata included by the software developer
     function writeSWMetadata(swMetadata)
     {
          return writeSDE(removeWhiteSpace(componentName), swMetadata);
     }

     //Internal function to encode a single structured data element
     function writeSDE(sdeName, metaData)
     {
          var sdeString = "";
          if (sdeName && metaData)
          {
               sdeString += "[" + sdeName + "@" + structuredDataDomain;

               for (var i in Object.keys(metaData))
               {
                    var keyName = Object.keys(metaData)[i];
                    sdeString += " " + removeWhiteSpace(keyName) + "=\"" + metaData[keyName] + "\"";
               }
               
               sdeString += "]";
          }
          return sdeString;
    }

    //If the action description string is not empty, append a UTF-8 BOM.
    //Otherwise return the empty string. 
    function appendActionDescription(actionDescription) {
        var encodedActionDescription = "";
        if (actionDescription && actionDescription != "") {
            encodedActionDescription += ' \357\273\277';
            encodedActionDescription += actionDescription;
        }

        return encodedActionDescription;
    }

     //Internal function to remove white space from an incoming value
     function removeWhiteSpace(inputString)
     {
          if(typeof inputString == 'string' || inputString instanceof String)
          {
               return inputString.replace(/\s/g,"");
          } else
          {     
               return inputString;
          }
     }

     //=======================END INTERNAL FUNCTIONS==========================
     
     
}

