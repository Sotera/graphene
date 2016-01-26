/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.rest.ws.impl;

import graphene.rest.ws.UDSessionRS;
import graphene.util.FastNumberUtils;
import graphene.util.validator.ValidationUtils;

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
import org.apache.tapestry5.ioc.annotations.PostInjection;
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
	private final String ROOTNAME = "UDS"; // The root namespace of the File
											// Store or the DB store for user
											// defined sessions
	boolean useFileStore = true; // set to True when using File storage for the
									// sessions
									// set to False when using DB/no-sql storage
									// for the sessions

	public UDSessionRSImpl() {
		// constructor
		/*
		 * This fails here if (servletContext == null) { try { servletContext =
		 * globals.getServletContext(); } catch (Exception se) {
		 * logger.error("UDSessionRSImpl: ServletContext is null."); // not a
		 * hard error, keep going } }
		 */
	}

	// Returns a unique session id for the specified parameters
	private String createSessionId(final String userId, final String sessionName, final String lastUpdated) {
		return userId.trim() + "_" + sessionName.trim() + "_" + lastUpdated.trim();
	}

	// deleteSession - Delete the specified session for the specified user id
	// @DELETE
	// @Path("/deleteSession/{id}")
	// @Produces("application/json")
	@Override
	public Response deleteSession(@PathParam("id") final String sessionId) {

		String sessionData = "{ }";
		ResponseBuilder response = null;

		if (servletContext == null) {
			try {
				servletContext = globals.getServletContext();
			} catch (final Exception se) {
				logger.error("delete: ServletContext is null.");
				// not a hard error, keep going
			}
		}

		// Verify that the sessionId is not null and valid
		// sessionId has this format:
		// userId + "_" + sessionname + "_" + new Date().getTime()).toString()
		if ((sessionId == null) || (sessionId.length() < 5) || (sessionId.indexOf("_") < 0)) {
			final String errormsg = "deleteSession: The specified session id is null or invalid";
			logger.error(errormsg);
			// response = Response.serverError();
			response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
		} else {
			if (useFileStore) {
				sessionData = deleteSessionFromFile(ROOTNAME, sessionId);
			} else {
				sessionData = deleteSessionFromDB(ROOTNAME, sessionId);
			}
			response = Response.ok(sessionData);
		}

		response.type("application/json");
		final Response responseOut = response.build();
		return responseOut;
	}

	private String deleteSessionFromDB(final String rootName, final String sessionId) {

		final String sessions = "ok";
		// TODO Implement
		return sessions;
	}

	private String deleteSessionFromFile(final String rootName, final String sessionId) {

		final String sessions = "ok";
		// TODO Implement
		return sessions;
	}

	// Returns the list of all userIds having saved sessions
	private List<String> getAllSessionuserIds(final String rootName) {
		final List<String> userIds = new ArrayList<String>();

		if (useFileStore) {
			String basePath = null, filePath;
			File FileFolder;

			if (servletContext != null) {
				basePath = servletContext.getRealPath("/");
			}

			// The files should be placed under the webserver's UDS (rootName)
			// dir
			if (basePath == null) {
				// TODO - handle case if the Server is Linux instead of Windows
				basePath = "C:/Windows/Temp"; // Temp hack
			}
			filePath = basePath + "/" + rootName; // within this folder there
													// can be subfolders for
													// earh userId
			FileFolder = new File(filePath);
			final String[] fileNames = FileFolder.list();

			for (final String fileName : fileNames) {
				if (new File(FileFolder + "/" + fileName).isDirectory()) {
					userIds.add(fileName);
				}
			}

			// DEBUG
			logger.debug("getAllSessionuserIds: userIds = " + userIds.toString());
		} else {
			// TODO from DB or other store
		}
		return userIds;
	}

	// getSession - Get session data for the specified session id
	// @GET
	// @Path("/getSession/{id}")
	// @Produces("application/json")
	/*
	 * Input/Query parameters: id: Unique session identifier
	 * 
	 * Returns: Application/json. JSON object containing the session data. { id:
	 * - Internal id name: - Name of the session userId: - unique user id (or
	 * username) for the associated user lastUpdated: - Date and timestamp (in
	 * milliseconds) when the session was saved by the user sessionActions:
	 * [action1, action2, ... actionN] - List of user defined Actions and
	 * related data (JSON)
	 * 
	 * Each action will consist of the following object: { type: - UI defined
	 * abbreviation for the action or data type. data: - String, Int, Array, or
	 * Object (can be anything) }
	 * 
	 * userPrefs: [] - List of UI preferences and settings (name,value pairs) }
	 * 
	 * Returns an empty object { } , if no matches found
	 */
	@Override
	public Response getSession(@PathParam("id") final String sessionId) {

		String sessionData = "{ }";
		ResponseBuilder response = null;

		if (servletContext == null) {
			try {
				servletContext = globals.getServletContext();
			} catch (final Exception se) {
				logger.error("getSession: ServletContext is null.");
				// not a hard error, keep going
			}
		}

		// Verify that the sessionId is not null and valid
		// sessionId has this format:
		// userId + "_" + sessionname + "_" + new Date().getTime()).toString()
		if ((sessionId == null) || (sessionId.length() < 5) || (sessionId.indexOf("_") < 0)) {
			final String errormsg = "getSession: The specified session id is null or invalid";
			logger.error(errormsg);
			// response = Response.serverError();
			response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
		} else {
			if (useFileStore) {
				sessionData = getSessionFromFile(ROOTNAME, sessionId);
			} else {
				sessionData = getSessionFromDB(ROOTNAME, sessionId);
			}
			response = Response.ok(sessionData);
		}

		response.type("application/json");
		final Response responseOut = response.build();
		return responseOut;
	}

	/*
	 * Returns the session data for the specified sessionId
	 */
	private String getSessionFromDB(final String rootName, final String sessionId) {
		// sessionId has this format:
		// userId + "_" + sessionname + "_" + new Date().getTime()).toString()

		final String sessionData = "{ }";

		// TODO Implement
		return sessionData;
	}

	// Return the session data (from the File Store) for the specified sessionId
	private String getSessionFromFile(final String rootName, final String sessionId) {
		// sessionId has this format:
		// userId + "_" + sessionname + "_" + new Date().getTime()).toString()

		String sessionData = "{ }";
		String filePath = null;
		String fullFileName = null;
		FileInputStream inStream = null;

		final int firstDelimPos = sessionId.indexOf("_");
		final String userId = sessionId.substring(0, firstDelimPos);
		final String sessionFileName = sessionId.substring(firstDelimPos + 1);

		if (servletContext != null) {
			filePath = servletContext.getRealPath("/");
		}

		// TODO the file should be placed under the webserver's dir
		if (filePath == null) {
			// TODO - handle case if the Server is Linux instead of Windows
			filePath = "C:/Windows/Temp"; // Temp hack
		}
		fullFileName = filePath + "/" + rootName + "/" + userId + "/" + sessionFileName + ".txt";

		// DEBUG
		logger.debug("getSessionFromFile: fullFileName = " + fullFileName);

		try {
			inStream = new FileInputStream(fullFileName);
			sessionData = IOUtils.toString(inStream);

		} catch (final Exception gfe) {
			logger.error("getSessionFromFile: Failed to read file. Details: " + gfe.getLocalizedMessage());
		} finally {
			if (ValidationUtils.isValid(inStream)) {
				try {
					inStream.close();
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// TODO get the data for the sessionId
		return sessionData;
	}

	// getSessions - Get the list of All user-defined sessions
	// @GET
	// @Path("/getSessions")
	// @Produces("application/json")
	/*
	 * Input/Query parameters: NONE
	 * 
	 * Returns: Application/json. JSON object containing an array of saved
	 * sessions.
	 * 
	 * [ session1, session2, ... sessionN ]
	 * 
	 * Where each session object consists of: { id: <internal id>, userId: <user
	 * id>, name: <name of the session>, lastUpdated: <Date and timestamp (in
	 * milliseconds)> }
	 * 
	 * Returns an empty array [] , if no user-defined sessions have been saved
	 */
	@Override
	public Response getSessions() {

		String sessionList = "[]";

		if (servletContext == null) {
			try {
				servletContext = globals.getServletContext();
			} catch (final Exception se) {
				logger.error("getSessions: ServletContext is null.");
				// not a hard error, keep going
			}
		}

		// Get the list of all user defined sessions (not including any session
		// data)
		if (useFileStore) {
			sessionList = getSessionsFromFile(ROOTNAME, "all");
		} else {
			sessionList = getSessionsFromDB(ROOTNAME, "all");
		}
		final ResponseBuilder response = Response.ok(sessionList);
		response.type("application/json");
		final Response responseOut = response.build();
		return responseOut;
	}

	// getSessionsByDate - Get the list of All user-defined sessions for the
	// date range specified by the from and to dates
	// @GET
	// @Path("/getSessionsByDate/{fromdt}/{todt}")
	// @Produces("application/json")
	/*
	 * Input/Query parameters: fromdt: From date timestamp in millisecs todt: To
	 * date timestamp in millisecs
	 * 
	 * Returns: Application/json. JSON object containing an array of saved
	 * sessions.
	 * 
	 * [ session1, session2, ... sessionN ]
	 * 
	 * Where each session object consists of: { id: <internal id>, userId: <user
	 * id>, name: <name of the session>, lastUpdated: <Date and timestamp (in
	 * milliseconds)> }
	 * 
	 * Returns an empty array [] , if no user-defined sessions exist for the
	 * specified date range
	 */
	@Override
	public Response getSessionsByDate(@PathParam("fromdt") @DefaultValue(value = "0") final String fromdt,
			@PathParam("todt") @DefaultValue(value = "0") final String todt) {

		ResponseBuilder response = null;
		String sessionList = "[]";

		if (servletContext == null) {
			try {
				servletContext = globals.getServletContext();
			} catch (final Exception se) {
				logger.error("getSessionsByDate: ServletContext is null.");
				// not a hard error, keep going
			}
		}

		// Verify that the from and to dates are not null and valid
		// if both are 0 use a date range of today(now) - 1 year
		if (((fromdt == null) || (fromdt.length() == 0)) && ((todt == null) || (todt.length() == 0))) {
			final String errormsg = "getSessionsByDate: The specified From and To dates are null or invalid";
			logger.error(errormsg);
			// response = Response.serverError();
			response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
		} else if ((fromdt == null) || (fromdt.length() == 0)) {
			final String errormsg = "getSessionsByDate: The specified From date is null or invalid";
			logger.error(errormsg);
			// response = Response.serverError();
			response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
		} else if ((todt == null) || (todt.length() == 0)) {
			final String errormsg = "getSessionsByDate: The specified To date is null or invalid";
			logger.error(errormsg);
			// response = Response.serverError();
			response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
		}
		final long fromDatems = FastNumberUtils.parseLongWithCheck(fromdt);
		final long toDatems = FastNumberUtils.parseLongWithCheck(todt);

		if (toDatems < fromDatems) {
			final String errormsg = "getSessionsByDate: The To date must be greater than or equal to the From date";
			logger.error(errormsg);
			// response = Response.serverError();
			response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
		}

		if (response == null) { // => no validation error
			// get the list of user defined sessions (not including any session
			// data) for the specified date range
			if (useFileStore) {
				sessionList = getSessionsByDateFromFile(ROOTNAME, fromdt, todt);
			} else {
				sessionList = getSessionsByDateFromDB(ROOTNAME, fromdt, todt);
			}
			response = Response.ok(sessionList);
		}

		response.type("application/json");
		final Response responseOut = response.build();
		return responseOut;
	}

	private String getSessionsByDateFromDB(final String rootName, final String fromDatems, final String toDatems) {

		final String sessions = "[ ]";
		// TODO Implement
		return sessions;
	}

	private String getSessionsByDateFromFile(final String rootName, final String fromDatems, final String toDatems) {

		final String sessions = "[ ]";
		// TODO Implement
		return sessions;
	}

	// getSessionsByName - Get the list of user-defined sessions for the
	// specified session name
	// @GET
	// @Path("/getSessionsByName/{name}")
	// @Produces("application/json")
	/*
	 * Input/Query parameters: name: Session name
	 * 
	 * Returns: Application/json. JSON object containing an array of saved
	 * sessions.
	 * 
	 * [ session1, session2, ... sessionN ]
	 * 
	 * Where each session object consists of: { id: <internal id>, userId: <user
	 * id>, name: <name of the session>, lastUpdated: <Date and timestamp (in
	 * milliseconds)> }
	 * 
	 * Returns an empty array [] , if no user-defined sessions exist for the
	 * specified session name
	 */
	@Override
	public Response getSessionsByName(@PathParam("name") final String sessionName) {

		String sessionList = "[]";
		ResponseBuilder response = null;

		if (servletContext == null) {
			try {
				servletContext = globals.getServletContext();
			} catch (final Exception se) {
				logger.error("getSessionsByName: ServletContext is null.");
				// not a hard error, keep going
			}
		}

		// Verify that the name is not null and valid
		if ((sessionName == null) || (sessionName.length() < 1)) {
			final String errormsg = "getSessionsByName: The specified session name is null or invalid";
			logger.error(errormsg);
			// response = Response.serverError();
			response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
		} else {
			// get the list of user defined sessions (not including any session
			// data) for the session name
			// NOTE: This may return more than 1 row as session names by
			// themselves are NOT unique
			if (useFileStore) {
				sessionList = getSessionsByNameFromFile(ROOTNAME, sessionName);
			} else {
				sessionList = getSessionsByNameFromDB(ROOTNAME, sessionName);
			}
			response = Response.ok(sessionList);
		}

		response.type("application/json");
		final Response responseOut = response.build();
		return responseOut;
	}

	private String getSessionsByNameFromDB(final String rootName, final String sessionName) {

		final String sessions = "[ ]";
		// TODO Implement
		return sessions;
	}

	private String getSessionsByNameFromFile(final String rootName, final String sessionName) {

		final String sessions = "[ ]";
		// TODO Implement
		return sessions;
	}

	// getSessionsByuserId - Get the list of user-defined sessions for the
	// specified userId
	// @GET
	// @Path("/getSessionsByuserId/{userId}")
	// @Produces("application/json")
	/*
	 * Input/Query parameters: userId: userId
	 * 
	 * Returns: Application/json. JSON object containing an array of saved
	 * sessions.
	 * 
	 * [ session1, session2, ... sessionN ]
	 * 
	 * Where each session object consists of: { id: <internal id>, userId: <user
	 * id>, name: <name of the session>, lastUpdated: <Date and timestamp (in
	 * milliseconds)> }
	 * 
	 * Returns an empty array [] , if no user-defined sessions exist for the
	 * specified userId
	 */
	@Override
	public Response getSessionsByuserId(@PathParam("userId") final String userId) {

		String sessionList = "[]";
		ResponseBuilder response = null;

		if (servletContext == null) {
			try {
				servletContext = globals.getServletContext();
			} catch (final Exception se) {
				logger.error("getSessionsByuserId: ServletContext is null.");
				// not a hard error, keep going
			}
		}

		// Verify that the userId is not null and valid
		if ((userId == null) || (userId.length() < 1)) {
			final String errormsg = "getSessionsByuserId: The specified user id is null or invalid";
			logger.error(errormsg);
			// response = Response.serverError();
			response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
		} else {
			// get the list of user defined sessions (not including any session
			// data) for the userId
			if (useFileStore) {
				sessionList = getSessionsFromFile(ROOTNAME, userId);
			} else {
				sessionList = getSessionsFromDB(ROOTNAME, userId);
			}
			response = Response.ok(sessionList);
		}

		response.type("application/json");
		final Response responseOut = response.build();
		return responseOut;
	}

	// =============== Public Methods ================

	private List<String> getSessionsForuserId(final String rootName, final String userId) {
		final List<String> sessions = new ArrayList<String>();

		if (useFileStore) {
			String basePath = null, filePath;
			File FileFolder;

			if (servletContext != null) {
				basePath = servletContext.getRealPath("/");
			}

			// The files should be placed under the webserver's UDS (rootName)
			// dir
			if (basePath == null) {
				// TODO - handle case if the Server is Linux instead of Windows
				basePath = "C:/Windows/Temp"; // Temp hack
			}
			filePath = basePath + "/" + rootName + "/" + userId; // within this
																	// folder
																	// there can
																	// saved
																	// session
																	// files
			FileFolder = new File(filePath);
			final String[] fileNames = FileFolder.list(); // list of session
															// files
			String sessionId, sessionName, lastUpdated;
			String sessionInfoObject = "{ }";
			int indx1, indx2;

			if (fileNames != null) {
				for (final String fileName : fileNames) {
					// we don't need to read the contents of each file, but just
					// construct the session info from the filename
					indx1 = fileName.indexOf("_");
					indx2 = fileName.lastIndexOf(".");
					if (indx2 < 0) {
						indx2 = fileName.length();
					}
					if ((indx1 > 0) && (indx2 > 0)) {
						sessionName = fileName.substring(0, indx1);
						lastUpdated = fileName.substring(indx1 + 1, indx2);
						sessionId = createSessionId(userId, sessionName, lastUpdated);
						sessionInfoObject = "{ id: \"" + sessionId + "\", userId: \"" + userId + "\", lastUpdated: \""
								+ lastUpdated + "\" }";
						sessions.add(sessionInfoObject);
					}
				}
			} else {
				sessions.add(sessionInfoObject); // empty
			}

			// DEBUG
			logger.debug("getSessionsForuserId: sessions = " + sessions.toString());
		} else {
			// TODO from DB or other store
		}
		return sessions;
	}

	// userId can be "all"
	private String getSessionsFromDB(final String rootName, final String userId) {

		final String sessions = "[ ]";
		// TODO Implement
		return sessions;
	}

	/*
	 * Returns: Application/json. JSON object containing an array of info about
	 * saved session. [ session1, session2, ... sessionN ]
	 * 
	 * Where each session object consists of: { id: <internal id>, userId: <user
	 * id>, name: <name of the session>, lastUpdated: <Date and timestamp (in
	 * milliseconds)> }
	 * 
	 * Returns an empty array [] , if no user-defined sessions exist for the
	 * specified userId
	 */
	private String getSessionsFromFile(final String rootName, final String userId) { // userId
																						// can
																						// be
																						// "all"

		String response = "[ ]";
		List<String> userIds = new ArrayList<String>();
		List<String> sessionsList = new ArrayList<String>();
		final StringBuilder responsesb = new StringBuilder();
		String sessionInfo;
		int i, j;

		responsesb.append("[");

		if (userId.equalsIgnoreCase("all")) {
			userIds = getAllSessionuserIds(rootName);
			for (i = 0; i < userIds.size(); i++) {
				sessionsList = getSessionsForuserId(rootName, userIds.get(i));

				for (j = 0; j < sessionsList.size(); j++) {
					sessionInfo = sessionsList.get(j);
					responsesb.append(sessionInfo);
					if (j < (sessionsList.size() - 1)) {
						responsesb.append(",");
					}
				}
			}
		} else {
			sessionsList = getSessionsForuserId(rootName, userId);

			for (j = 0; j < sessionsList.size(); j++) {
				sessionInfo = sessionsList.get(j);
				responsesb.append(sessionInfo);
				if (j < (sessionsList.size() - 1)) {
					responsesb.append(",");
				}
			}
		}

		responsesb.append("]");
		response = responsesb.toString();
		return response;
	}

	@PostInjection
	public void initialize() {
		logger.debug("UD Session Server now available");
	}

	// save - Save a user-defined session or graph layout
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Path("/save")
	// @Produces("application/json")
	/*
	 * Input/Query parameters: NONE POST Data: JSON object containing the
	 * session data to save. { name: - Name of the session userId: - unique user
	 * id for the associated user lastUpdated: - Date and timestamp (in
	 * milliseconds) when the session was saved by the user sessionActions:
	 * [action1, action2, ... actionN] - List of user defined Actions and
	 * related data (JSON)
	 * 
	 * Each action will consist of the following object: { type: - UI defined
	 * abbreviation for the action or data type data: - String, Int, Array, or
	 * Object (can be anything) }
	 * 
	 * userPrefs: [] - List of UI preferences and settings (name,value pairs) }
	 * 
	 * Returns: Application/json { id: - System assigned id of the saved session
	 * error: - Populated with an error message if a server error }
	 */
	@Override
	public Response save(final String sessionJSONdata) {

		String responseData = "{ }";
		ResponseBuilder response = null;

		if (servletContext == null) {
			try {
				servletContext = globals.getServletContext();
			} catch (final Exception se) {
				logger.error("save: ServletContext is null.");
				// not a hard error, keep going
			}
		}

		if ((sessionJSONdata == null) || (sessionJSONdata.length() < 50)) {
			final String errormsg = "save: The specified session data is null or invalid";
			logger.error(errormsg);
			// response = Response.serverError();
			response = Response.status(Response.Status.BAD_REQUEST).entity(errormsg);
		} else {
			if (useFileStore) {
				responseData = saveSessionToFile(ROOTNAME, sessionJSONdata);
			} else {
				responseData = saveSessionToDB(ROOTNAME, sessionJSONdata);
			}
			if (responseData.indexOf("-1") > 0) {
				response = Response.status(Response.Status.BAD_REQUEST).entity(responseData);
			} else {
				response = Response.ok(responseData);
			}
		}

		response.type("application/json");
		final Response responseOut = response.build();
		return responseOut;
	}

	// Returns: Application/json
	// {
	// id: - System assigned id of the saved session
	// error: - Populated with an error message if a server error
	// }
	private String saveSessionToDB(final String rootName, final String sessionJSONdata) {

		final String response = "{ id: \"TBD\", error:\"no error\" }";
		// TODO Implement

		// TODO Update the list of user defined sessions to include the new
		// session
		return response;
	}

	// Save sessionData to the File Store
	// Returns: Application/json
	// {
	// id: - System assigned id of the saved session
	// error: - Populated with an error message if a server error
	// }
	private String saveSessionToFile(final String rootName, final String sessionData) {

		String response = "{ id: \"TBD\", error:\"no error\" }";
		String errormsg = null;

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final OutputStreamWriter writer = new OutputStreamWriter(outputStream);

		// DEBUG
		logger.debug("saveSessionToFile: sessionData received =");
		logger.debug(sessionData);

		// Create the file on the Web Server
		File filedir = null;
		File file = null;

		// sessionId has this format:
		// userId + "_" + sessionname + "_" + new Date().getTime()).toString()

		String basepath = null;

		// sessionData should contain the following at the beginning
		// {
		// name: "xxx", - Name of the session
		// userId: "yyy", - unique user id for the associated user
		// lastUpdated: "zzz", - Date and timestamp (in milliseconds) when the
		// session was saved by the user
		// sessionActions: ....

		final int indxName = sessionData.indexOf("name");
		final int indxuserId = sessionData.indexOf("userId");
		final int indxDate = sessionData.indexOf("lastUpdated");
		final int indxAfterDate = sessionData.indexOf("sessionActions");
		if ((indxName < 0) || (indxuserId < 0) || (indxDate < 0) || (indxAfterDate < 0)) {
			errormsg = "saveSessionToFile: Invalid session data was received, unable to save it.";
			logger.error(errormsg);
			response = "{ id: \"-1\", error:\"" + errormsg + "\" }";
			return response;
		}

		String sessionName = sessionData.substring(indxName + 5, indxuserId - 1);
		sessionName = sessionName.replace("\"", "");
		sessionName = sessionName.replace(",", "");
		sessionName = sessionName.trim();

		String userId = sessionData.substring(indxuserId + 7, indxDate - 1);
		userId = userId.replace("\"", "");
		userId = userId.replace(",", "");
		userId = userId.trim();

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
		} catch (final IOException e) {
			errormsg = "saveSessionToFile: Server Exception writing session JSON data";
			logger.error(errormsg);
			logger.error(e.getMessage());
			response = "{ id: \"-1\", error:\"" + errormsg + "\" }";
			return response;
		}

		try {
			writer.close();
			outputStream.flush();
			outputStream.close();
		} catch (final java.io.IOException e) {
			errormsg = "saveSessionToFile: I/O Exception when attempting to close output after write. Details "
					+ e.getMessage();
			logger.error(errormsg);
			logger.error(e.getMessage());
			response = "{ id: \"-1\", error:\"" + errormsg + "\" }";
			return response;
		}

		// Files are written as:
		// <basepath>/UDS/<userId>/<sessionname>_<date>
		final String serverPathName = basepath + "/" + rootName + "/" + userId;
		final String serverfileName = sessionName + "_" + lastUpdated + ".txt";

		// DEBUG
		logger.debug("saveSessionToFile: serverPathName = " + serverPathName + ", serverfileName = " + serverfileName);

		try {
			filedir = new File(serverPathName);
			filedir.setWritable(true);
			filedir.mkdirs();

			file = new File(serverPathName + "/" + serverfileName);
			final FileOutputStream fout = new FileOutputStream(file);
			fout.write(outputStream.toByteArray());
			fout.close();
			// String finalPath = file.toURI().toString();
			// finalPath = finalPath.replace("file:/", ""); // remove leading

		} catch (final Exception fe) {
			errormsg = "saveSessionToFile: Failed to create file for session data. Details: "
					+ fe.getLocalizedMessage();
			logger.error(errormsg);
			response = "{ id: \"-1\", error:\"" + errormsg + "\" }";
			return response;
		}

		// set the unique id of the response
		final String sessionId = createSessionId(userId, sessionName, lastUpdated);

		response = "{ id: \"" + sessionId + "\", error:\"no error\" }";
		return response;
	}

}
