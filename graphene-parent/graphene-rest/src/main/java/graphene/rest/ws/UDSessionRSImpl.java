package graphene.rest.ws;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationGlobals;
//import org.apache.tapestry5.ioc.annotations.Inject;
//import org.apache.tapestry5.services.ApplicationGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// History
// 01/03/14     M. Martinet - Initial version - REST Services for User Defined Sessions
//
// TODO         Implement DB storage, no-sql, or Neo4J interfaces for saving and retrieving 
//              sessions instead of using the server filesystem.

public class UDSessionRSImpl implements UDSessionRS {

	static Logger logger = LoggerFactory.getLogger(UDSessionRSImpl.class);
        
        @Inject
	private ApplicationGlobals globals; // For the ServletContext
        private ServletContext servletContext = null;
        private final String ROOTNAME = "UDS";  // The root namespace of the File Store or the DB store for user defined sessions
        
        boolean useFileStore = true;    // set to True when using File storage for the sessions
                                        // set to False when using DB/no-sql storage for the sessions
        

	public UDSessionRSImpl() {
		// constructor
            /* This fails here
            if (servletContext == null) {
                try {
                    servletContext = globals.getServletContext();
                } catch (Exception se) {
                    logger.error("UDSessionRSImpl: ServletContext is null.");
                    // not a hard error, keep going
                }
            }
            */
	}
        
        // Returns a unique session id for the specified parameters
        private String createSessionId(String userid, String sessionName, String lastUpdated) {
            return userid.trim() + "_" + sessionName.trim() + "_" + lastUpdated.trim();
        }
        
        // Return the session data (from the File Store) for the specified sessionId
        private String getSessionFromFile(String rootName, String sessionId) {
            // sessionId has this format:
            // userid + "_" + sessionname + "_" + new Date().getTime()).toString()
            
            String sessionData = "{ }";
            String filePath = null;
            String fullFileName = null;
            FileInputStream inStream = null;

            int firstDelimPos = sessionId.indexOf("_");
            String userid = sessionId.substring(0,firstDelimPos);
            String sessionFileName = sessionId.substring(firstDelimPos + 1);
            
            if (servletContext != null) {
                filePath = servletContext.getRealPath("/");
            }

            // TODO the file should be placed under the webserver's dir
            if (filePath == null) {
                // TODO - handle case if the Server is Linux instead of Windows
                filePath = "C:/Windows/Temp"; // Temp hack
            }
            fullFileName = filePath + "/" + rootName + "/" + userid + "/" + sessionFileName + ".txt";
            
            // DEBUG
            logger.debug("getSessionFromFile: fullFileName = " + fullFileName);
            
            try {
                inStream = new FileInputStream(fullFileName);
                sessionData = IOUtils.toString(inStream);
                inStream.close();

            } catch (Exception gfe) {
                    logger.error("getSessionFromFile: Failed to read file. Details: " + gfe.getLocalizedMessage());
            }
                
            // TODO get the data for the sessionId
            return sessionData;
        }

        /* 
         * Returns the session data for the specified sessionId
         */
        private String getSessionFromDB(String rootName, String sessionId) {
            // sessionId has this format:
            // userid + "_" + sessionname + "_" + new Date().getTime()).toString()
            
            String sessionData = "{ }";
            
            // TODO Implement
            return sessionData;
        }
        
        // Returns the list of all userids having saved sessions
        private List<String> getAllSessionUserIds(String rootName) {
            List<String>  userids    = new ArrayList<String>();
            
            if (useFileStore) {
                String basePath = null, filePath;
                File FileFolder;
                
                if (servletContext != null) {
                    basePath = servletContext.getRealPath("/");
                }

                // The files should be placed under the webserver's UDS (rootName) dir
                if (basePath == null) {
                    // TODO - handle case if the Server is Linux instead of Windows
                    basePath = "C:/Windows/Temp"; // Temp hack
                } 
                filePath = basePath + "/" + rootName;   // within this folder there can be subfolders for earh userid
                FileFolder = new File(filePath);
                String[] fileNames = FileFolder.list();
                
                for (String fileName : fileNames) {
                    if (new File(FileFolder + "/" + fileName).isDirectory()) {
                        userids.add(fileName);
                    }
                }
                
                // DEBUG
                logger.debug("getAllSessionUserIds: userids = " + userids.toString());
            }
            else {
                 // TODO from DB or other store
            }
            return userids;
        }
        
        private List<String> getSessionsForUserid(String rootName, String userid) {
            List<String>  sessions    = new ArrayList<String>();
            
            if (useFileStore) {
                String basePath = null, filePath;
                File FileFolder;
                
                if (servletContext != null) {
                    basePath = servletContext.getRealPath("/");
                }

                // The files should be placed under the webserver's UDS (rootName) dir
                if (basePath == null) {
                    // TODO - handle case if the Server is Linux instead of Windows
                    basePath = "C:/Windows/Temp"; // Temp hack
                } 
                filePath = basePath + "/" + rootName + "/" + userid;   // within this folder there can saved session files
                FileFolder = new File(filePath);
                String[] fileNames = FileFolder.list(); // list of session files
                String sessionId, sessionName, lastUpdated;
                String sessionInfoObject = "{ }";
                int indx1, indx2;
                
                if (fileNames != null) {
                    for (String fileName : fileNames) {
                        // we don't need to read the contents of each file, but just construct the session info from the filename
                        indx1 = fileName.indexOf("_");
                        indx2 = fileName.lastIndexOf(".");
                        if (indx2 < 0) {
                            indx2 = fileName.length();
                        }
                        if (indx1 > 0 && indx2 > 0) {
                            sessionName = fileName.substring(0, indx1);
                            lastUpdated = fileName.substring(indx1 + 1, indx2);
                            sessionId = this.createSessionId(userid, sessionName, lastUpdated);
                            sessionInfoObject = "{ id: \"" + sessionId + "\", userid: \"" + userid + "\", lastUpdated: \"" + lastUpdated + "\" }";
                            sessions.add(sessionInfoObject);
                        }
                    }
                }
                else {
                    sessions.add(sessionInfoObject);    // empty
                }
                
                // DEBUG
                logger.debug("getSessionsForUserid: sessions = " + sessions.toString());
            }
            else {
                 // TODO from DB or other store
            }
            return sessions;
        }
        
        /* 
         * Returns: Application/json. JSON object containing an array of info about saved session.
         * [ session1, session2, ... sessionN ]
         * 
         * Where each session object consists of:
	 * { 
	 *  id: 		<internal id>, 
	 *  userid: 	<user id>, 
	 *  name: 	<name of the session>, 
	 *  lastUpdated: 	<Date and timestamp (in milliseconds)> 
	 * }
	 *
         * Returns an empty array [] , if no user-defined sessions exist for the specified userid
         */
        private String getSessionsFromFile(String rootName, String userid) {  // userid can be "all"
            
            String response = "[ ]";
            List<String> userids    = new ArrayList<String>();
            List<String> sessionsList = new ArrayList<String>();
            StringBuilder responsesb = new StringBuilder();
            String sessionInfo;
            int i, j;
            
            responsesb.append("[");
            
            if (userid.equalsIgnoreCase("all")) {
                userids = this.getAllSessionUserIds(rootName);
                for (i = 0; i < userids.size(); i++) {
                    sessionsList = this.getSessionsForUserid(rootName, userids.get(i));
                    
                    for (j = 0; j < sessionsList.size(); j++) {
                        sessionInfo = sessionsList.get(j);
                        responsesb.append(sessionInfo);
                        if (j < sessionsList.size() - 1) {
                            responsesb.append(",");
                        }
                    }
                }
            }
            else {
                sessionsList = this.getSessionsForUserid(rootName, userid);
                
                for (j = 0; j < sessionsList.size(); j++) {
                    sessionInfo = sessionsList.get(j);
                    responsesb.append(sessionInfo);
                    if (j < sessionsList.size() - 1) {
                        responsesb.append(",");
                    }
                }
            }
            
            responsesb.append("]");
            response = responsesb.toString();
            return response;
        }
        
        // userid can be "all"
        private String getSessionsFromDB(String rootName, String userid) {
            
            String sessions = "[ ]";
            // TODO Implement
            return sessions;
        }
        
        private String getSessionsByDateFromFile(String rootName, String fromDatems, String toDatems) {
            
            String sessions = "[ ]";
            // TODO Implement
            return sessions;
        }
        
        private String getSessionsByDateFromDB(String rootName, String fromDatems, String toDatems) {
            
            String sessions = "[ ]";
            // TODO Implement
            return sessions;
        }
        
        private String getSessionsByNameFromFile(String rootName, String sessionName) {
            
            String sessions = "[ ]";
            // TODO Implement
            return sessions;
        }
        
        private String getSessionsByNameFromDB(String rootName, String sessionName) {
            
            String sessions = "[ ]";
            // TODO Implement
            return sessions;
        }
        
        private String deleteSessionFromFile(String rootName, String sessionId) {
            
            String sessions = "ok";
            // TODO Implement
            return sessions;
        }
        
        private String deleteSessionFromDB(String rootName, String sessionId) {
            
            String sessions = "ok";
            // TODO Implement
            return sessions;
        }
        
        // Save sessionData to the File Store
        // Returns: Application/json
        // {
        //    id:           - System assigned id of the saved session
        //    error:        - Populated with an error message if a server error
        // }
        private String saveSessionToFile(String rootName, String sessionData) {
            
            String response = "{ id: \"TBD\", error:\"no error\" }";
            String errormsg = null;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);

            // DEBUG
            logger.debug("saveSessionToFile: sessionData received =");
            logger.debug(sessionData);

            // Create the file on the Web Server
            File filedir = null;
            File file = null;

            // sessionId has this format:
            //    userid + "_" + sessionname + "_" + new Date().getTime()).toString()
            
            String basepath = null;
            
            // sessionData should contain the following at the beginning
            // {
            //    name:    "xxx",     - Name of the session
            //    userid:  "yyy",     - unique user id for the associated user
            //    lastUpdated: "zzz", - Date and timestamp (in milliseconds) when the session was saved by the user
            //    sessionActions: ....
            
            int indxName = sessionData.indexOf("name");
            int indxUserid = sessionData.indexOf("userid");
            int indxDate = sessionData.indexOf("lastUpdated");
            int indxAfterDate = sessionData.indexOf("sessionActions");
            if (indxName < 0 || indxUserid < 0 || indxDate < 0 || indxAfterDate < 0) {
                errormsg = "saveSessionToFile: Invalid session data was received, unable to save it.";
                logger.error(errormsg);
                response = "{ id: \"-1\", error:\"" + errormsg + "\" }";
                return response;
            }
            
            String sessionName = sessionData.substring(indxName + 5, indxUserid - 1);
            sessionName = sessionName.replace("\"", "");
            sessionName = sessionName.replace(",", "");
            sessionName = sessionName.trim(); 
            
            String userid = sessionData.substring(indxUserid + 7, indxDate - 1);
            userid = userid.replace("\"", "");
            userid = userid.replace(",", "");
            userid = userid.trim(); 
            
            String lastUpdated = sessionData.substring(indxDate + 12, indxAfterDate - 1);
            lastUpdated = lastUpdated.replace("\"", "");
            lastUpdated = lastUpdated.replace(",", "");
            lastUpdated = lastUpdated.trim(); 
                  
            if (servletContext != null) {
                basepath = servletContext.getRealPath("/");
            }

            // TODO the file should be placed under the webserver's dir
            if (basepath == null) {
                // TODO - handle case if the Server is Linux instead of Windows
                basepath = "C:/Windows/Temp"; // Temp hack
            }

            try {
                writer.write(sessionData);
            } catch (IOException e) {
                errormsg = "saveSessionToFile: Server Exception writing session JSON data";
                logger.error(errormsg);
                e.printStackTrace();
                response = "{ id: \"-1\", error:\"" + errormsg + "\" }";
                return response;
            }

            try {
                writer.close();
                outputStream.flush();
                outputStream.close();
            } catch (java.io.IOException ioe) {
                errormsg = "saveSessionToFile: I/O Exception when attempting to close output after write. Details " + ioe.getMessage();
                logger.error(errormsg);
                response = "{ id: \"-1\", error:\"" + errormsg + "\" }";
                return response;
            }
            
            // Files are written as:
            //    <basepath>/UDS/<userid>/<sessionname>_<date>
            String serverPathName = basepath + "/" + rootName + "/" + userid;
            String serverfileName = sessionName + "_" + lastUpdated + ".txt";
            
            // DEBUG
            logger.debug("saveSessionToFile: serverPathName = " + serverPathName + ", serverfileName = " + serverfileName);

            try {
                filedir = new File(serverPathName);
                filedir.setWritable(true);
                filedir.mkdirs();
                
                file = new File(serverPathName + "/" + serverfileName);
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(outputStream.toByteArray());
                fout.close();
                //String finalPath = file.toURI().toString();
                //finalPath = finalPath.replace("file:/", ""); // remove leading

            } catch (Exception fe) {
                errormsg ="saveSessionToFile: Failed to create file for session data. Details: " + fe.getLocalizedMessage();
                logger.error(errormsg);
                response = "{ id: \"-1\", error:\"" + errormsg + "\" }";
                return response;
            }
            
            // set the unique id of the response
            String sessionId = createSessionId(userid,sessionName,lastUpdated);
            
            response = "{ id: \"" + sessionId + "\", error:\"no error\" }";
            return response;
        }
        
        // Returns: Application/json
        // {
        //    id:           - System assigned id of the saved session
        //    error:        - Populated with an error message if a server error
        // }
        private String saveSessionToDB(String rootName, String sessionJSONdata) {
            
            String response = "{ id: \"TBD\", error:\"no error\" }";
            // TODO Implement
            
            // TODO Update the list of user defined sessions to include the new session
            return response;
        }
        
//=============== Public Methods ================
        
        // getSession - Get session data for the specified session id
	//@GET
	//@Path("/getSession/{id}")
	//@Produces("application/json")
        /*
         * Input/Query parameters:
	 * id:	Unique session identifier	
         * 
         * Returns: Application/json. JSON object containing the session data.  
         * {	
         *   id:               	- Internal id
         *   name:               - Name of the session
         *   userid:		- unique user id (or username) for the associated user
         *   lastUpdated: 	- Date and timestamp (in milliseconds) when the session was saved by the user
         *   sessionActions: [action1, action2, ... actionN] - List of user defined Actions and related data (JSON)
         * 
	 * Each action will consist of the following object: 
	 *  { 
	 *	type: 	- UI defined abbreviation for the action or data type. 
	 *	data:  	- String, Int, Array, or Object (can be anything)
         *  }
	 *	    
         *   userPrefs: []     - List of UI preferences and settings (name,value pairs)
         * }
         * 
         * Returns an empty object { } , if no matches found
         */
        @Override
	public Response getSession(@PathParam("id") String sessionId) {
            
            String sessionData = "{ }";
            ResponseBuilder response = null;
            
            if (servletContext == null) {
                try {
                    servletContext = globals.getServletContext();
                } catch (Exception se) {
                    logger.error("getSession: ServletContext is null.");
                    // not a hard error, keep going
                }
            }
            
            // Verify that the sessionId is not null and valid
            // sessionId has this format:
            // userid + "_" + sessionname + "_" + new Date().getTime()).toString()
            if (sessionId == null || sessionId.length() < 5 || sessionId.indexOf("_") < 0) {
                String errormsg = "getSession: The specified session id is null or invalid";
                logger.error(errormsg);
                //response = Response.serverError();
                response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
            }
            else {
                if (useFileStore) {
                    sessionData = this.getSessionFromFile(ROOTNAME, sessionId);
                }
                else {
                    sessionData = this.getSessionFromDB(ROOTNAME, sessionId);
                }
                response = Response.ok(sessionData);
            }
            
            response.type("application/json");
            Response responseOut = response.build();
            return responseOut;
        }

        // getSessions - Get the list of All user-defined sessions
	//@GET
	//@Path("/getSessions")
	//@Produces("application/json")
        /*
         * Input/Query parameters: NONE
         * 
         * Returns: Application/json. JSON object containing an array of saved sessions.
         * 
         * [ session1, session2, ... sessionN ]
         * 
         * Where each session object consists of:
	 * { 
	 *   id: 		<internal id>, 
	 *   userid: 	<user id>, 
	 *   name: 	<name of the session>, 
	 *   lastUpdated: 	<Date and timestamp (in milliseconds)> 
         * }
         * 
         * Returns an empty array [] , if no user-defined sessions have been saved
         */
        @Override
	public Response getSessions() {
              
            String sessionList = "[]";
            
            if (servletContext == null) {
                try {
                    servletContext = globals.getServletContext();
                } catch (Exception se) {
                    logger.error("getSessions: ServletContext is null.");
                    // not a hard error, keep going
                }
            }
            
            // Get the list of all user defined sessions (not including any session data)
            if (useFileStore) {
                sessionList = this.getSessionsFromFile(ROOTNAME, "all");
            }
            else {
                sessionList = this.getSessionsFromDB(ROOTNAME, "all");
            }
            ResponseBuilder response = Response.ok(sessionList);
            response.type("application/json");
            Response responseOut = response.build();
            return responseOut;
        }

        // getSessionsByDate - Get the list of All user-defined sessions for the date range specified by the from and to dates 
	//@GET
	//@Path("/getSessionsByDate/{fromdt}/{todt}")
	//@Produces("application/json")
        /*
         * Input/Query parameters:
	 *  fromdt:	From date timestamp in millisecs
         *  todt:	To date timestamp in millisecs
         * 
         * Returns: Application/json. JSON object containing an array of saved sessions.
         * 
         * [ session1, session2, ... sessionN ]
         * 
         * Where each session object consists of:
	 * { 
	 *  id: 		<internal id>, 
	 *  userid: 	<user id>, 
	 *  name: 	<name of the session>, 
	 *  lastUpdated: 	<Date and timestamp (in milliseconds)> 
	 * }
	 *
         * Returns an empty array [] , if no user-defined sessions exist for the specified date range
         */
        @Override
	public Response getSessionsByDate(
                @PathParam("fromdt") @DefaultValue(value = "0") String fromdt,
                @PathParam("todt") @DefaultValue(value = "0") String todt
                ) {
            
            ResponseBuilder response = null;
            String sessionList = "[]";
            
            if (servletContext == null) {
                try {
                    servletContext = globals.getServletContext();
                } catch (Exception se) {
                    logger.error("getSessionsByDate: ServletContext is null.");
                    // not a hard error, keep going
                }
            }
            
            // Verify that the from and to dates are not null and valid
            // if both are 0 use a date range of today(now) - 1 year
            if ((fromdt == null || fromdt.length() == 0) && (todt == null || todt.length() == 0)) {
                String errormsg = "getSessionsByDate: The specified From and To dates are null or invalid";
                logger.error(errormsg);
                //response = Response.serverError();
                response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
            }
            else
            if (fromdt == null || fromdt.length() == 0) {
                String errormsg = "getSessionsByDate: The specified From date is null or invalid";
                logger.error(errormsg);
                //response = Response.serverError();
                response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
            }
            else
            if (todt == null || todt.length() == 0) {
                String errormsg = "getSessionsByDate: The specified To date is null or invalid";
                logger.error(errormsg);
                //response = Response.serverError();
                response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
            }
            Long fromDateL = Long.parseLong(fromdt);
            long fromDatems = fromDateL.longValue();
            
            Long toDateL = Long.parseLong(todt);
            long toDatems = toDateL.longValue();

            if (toDatems < fromDatems) {
                String errormsg = "getSessionsByDate: The To date must be greater than or equal to the From date";
                logger.error(errormsg);
                //response = Response.serverError();
                response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg); 
            }
            
            if (response == null) { // => no validation error
                // get the list of user defined sessions (not including any session data) for the specified date range
                if (useFileStore) {
                    sessionList = this.getSessionsByDateFromFile(ROOTNAME, fromdt,todt);
                }
                else {
                    sessionList = this.getSessionsByDateFromDB(ROOTNAME, fromdt,todt);
                }
                response = Response.ok(sessionList);
            }
            
            response.type("application/json");
            Response responseOut = response.build();
            return responseOut;
        }
        
        // getSessionsByUserId - Get the list of user-defined sessions for the specified userid 
	//@GET
	//@Path("/getSessionsByUserId/{userid}")
	//@Produces("application/json")
        /*
         * Input/Query parameters:
	 *  userid:     Userid
         * 
         * Returns: Application/json. JSON object containing an array of saved sessions.
         * 
         * [ session1, session2, ... sessionN ]
         * 
         * Where each session object consists of:
	 * { 
	 *  id: 		<internal id>, 
	 *  userid: 	<user id>, 
	 *  name: 	<name of the session>, 
	 *  lastUpdated: 	<Date and timestamp (in milliseconds)> 
	 * }
	 *
         * Returns an empty array [] , if no user-defined sessions exist for the specified userid
         */
        @Override
	public Response getSessionsByUserId(@PathParam("userid") String userId) {
            
            String sessionList = "[]";
            ResponseBuilder response = null;
            
            if (servletContext == null) {
                try {
                    servletContext = globals.getServletContext();
                } catch (Exception se) {
                    logger.error("getSessionsByUserId: ServletContext is null.");
                    // not a hard error, keep going
                }
            }
            
            // Verify that the userid is not null and valid
            if (userId == null || userId.length() < 1) {
                String errormsg = "getSessionsByUserId: The specified user id is null or invalid";
                logger.error(errormsg);
                //response = Response.serverError();
                response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
            }
            else {
                // get the list of user defined sessions (not including any session data) for the userid
                if (useFileStore) {
                    sessionList = this.getSessionsFromFile(ROOTNAME, userId);
                }
                else {
                    sessionList = this.getSessionsFromDB(ROOTNAME, userId);
                }
                response = Response.ok(sessionList);
            }
            
            response.type("application/json");
            Response responseOut = response.build();
            return responseOut;
        }
        
        // getSessionsByName - Get the list of user-defined sessions for the specified session name 
	//@GET
	//@Path("/getSessionsByName/{name}")
	//@Produces("application/json")
        /*
         * Input/Query parameters:
	 *  name:       Session name
         * 
         * Returns: Application/json. JSON object containing an array of saved sessions.
         * 
         * [ session1, session2, ... sessionN ]
         * 
         * Where each session object consists of:
	 * { 
	 *  id: 		<internal id>, 
	 *  userid: 	<user id>, 
	 *  name: 	<name of the session>, 
	 *  lastUpdated: 	<Date and timestamp (in milliseconds)> 
	 * }
	 *
         * Returns an empty array [] , if no user-defined sessions exist for the specified session name
         */
        @Override
	public Response getSessionsByName(@PathParam("name") String sessionName) {
            
            String sessionList = "[]";
            ResponseBuilder response = null;

            if (servletContext == null) {
                try {
                    servletContext = globals.getServletContext();
                } catch (Exception se) {
                    logger.error("getSessionsByName: ServletContext is null.");
                    // not a hard error, keep going
                }
            }
            
            // Verify that the name is not null and valid
            if (sessionName == null || sessionName.length() < 1) {
                String errormsg = "getSessionsByName: The specified session name is null or invalid";
                logger.error(errormsg);
                //response = Response.serverError();
                response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
            }
            else {
                // get the list of user defined sessions (not including any session data) for the session name
                // NOTE: This may return more than 1 row as session names by themselves are NOT unique
                if (useFileStore) {
                    sessionList = this.getSessionsByNameFromFile(ROOTNAME, sessionName);
                }
                else {
                    sessionList = this.getSessionsByNameFromDB(ROOTNAME, sessionName);
                }
                response = Response.ok(sessionList);
            }
            
            response.type("application/json");
            Response responseOut = response.build();
            return responseOut;
        }
        
        // deleteSession - Delete the specified session for the specified user id 
	//@DELETE
	//@Path("/deleteSession/{id}")
	//@Produces("application/json")
        @Override
	public Response deleteSession(@PathParam("id") String sessionId) {
            
            String sessionData = "{ }";
            ResponseBuilder response = null;
            
            if (servletContext == null) {
                try {
                    servletContext = globals.getServletContext();
                } catch (Exception se) {
                    logger.error("delete: ServletContext is null.");
                    // not a hard error, keep going
                }
            }
            
            // Verify that the sessionId is not null and valid
            // sessionId has this format:
            // userid + "_" + sessionname + "_" + new Date().getTime()).toString()
            if (sessionId == null || sessionId.length() < 5 || sessionId.indexOf("_") < 0) {
                String errormsg = "deleteSession: The specified session id is null or invalid";
                logger.error(errormsg);
                //response = Response.serverError();
                response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
            }
            else {
                if (useFileStore) {
                    sessionData = this.deleteSessionFromFile(ROOTNAME, sessionId);
                }
                else {
                    sessionData = this.deleteSessionFromDB(ROOTNAME, sessionId);
                }
                response = Response.ok(sessionData);
            }
            
            response.type("application/json");
            Response responseOut = response.build();
            return responseOut;
        }
        
        // save - Save a user-defined session or graph layout
	//@POST
	//@Consumes(MediaType.APPLICATION_JSON)
	//@Path("/save")
	//@Produces("application/json")
        /*
         * Input/Query parameters: NONE
         * POST Data: JSON object containing the session data to save.
         * {
         *    name:         - Name of the session
         *    userid:       - unique user id for the associated user
         *    lastUpdated:  - Date and timestamp (in milliseconds) when the session was saved by the user
         *    sessionActions: [action1, action2, ... actionN] - List of user defined Actions and related data (JSON)
         * 
         *    Each action will consist of the following object:
         *      {
         *          type:   - UI defined abbreviation for the action or data type
         *          data:   - String, Int, Array, or Object (can be anything)
         *      }
         * 
         *    userPrefs: [] - List of UI preferences and settings (name,value pairs)
         * }
         * 
         * Returns: Application/json
         * {
         *    id:           - System assigned id of the saved session
         *    error:        - Populated with an error message if a server error
         * }
         */
        @Override
	public Response save(String sessionJSONdata) {
          
            String responseData = "{ }";
            ResponseBuilder response = null;
            
            if (servletContext == null) {
                try {
                    servletContext = globals.getServletContext();
                } catch (Exception se) {
                    logger.error("save: ServletContext is null.");
                    // not a hard error, keep going
                }
            }
            
            if (sessionJSONdata == null || sessionJSONdata.length() < 50) {
                String errormsg = "save: The specified session data is null or invalid";
                logger.error(errormsg);
                //response = Response.serverError();
                response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
            }
            else {
                if (useFileStore) {
                    responseData = this.saveSessionToFile(ROOTNAME, sessionJSONdata);
                }
                else {
                    responseData = this.saveSessionToDB(ROOTNAME, sessionJSONdata);
                }
                if (responseData.indexOf("-1") > 0) {
                   response = Response.status(Response.Status.BAD_REQUEST).entity(responseData); 
                }
                else {
                    response = Response.ok(responseData);
                }
            }
             
            response.type("application/json");
            Response responseOut = response.build();
            return responseOut;
        }

}
