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

import graphene.rest.ws.ExportGraphRS;
import graphene.util.validator.ValidationUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletContext;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// History
// 09/02/13     A. Weller - Initial version
// 10/16/13     M. Martinet - Revised to use POST methods for export services,
//              removed the JSONArray painfull headaches, and added a getExportedGraph service,
//              and Inject for the ServletContext

public class ExportGraphRSImpl implements ExportGraphRS {
	static Logger logger = LoggerFactory.getLogger(ExportGraphRSImpl.class);
	@Inject
	private ApplicationGlobals globals; // For the ServletContext

	public ExportGraphRSImpl() {
		// constructor
	}

	// NOTES:
	// The graph export must be done in two stages and cannot be done using one
	// simple Get request
	// because the graph data sent from the client will typically be large ( >
	// 4K).
	//
	// 1. Graph data in JSON from the browser app is POSTed to this service
	// (/exportGraphAsJSON) using an AJAX request.
	// The graph data is stored in a temporary file on the web server. The
	// response contains the path to this file.
	//
	// 2. A subsequent GET request (/getExportedGraph) is sent from the Browser
	// app which prompts the user to
	// download the temporary file from the server and save it to their local
	// filesystem.
	// After the temporary file is downloaded it is deleted.
	//
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Path("/exportGraphAsJSON")
	// @Produces("text/plain")
	// @Override
	@Override
	public Response exportGraphAsJSON(@QueryParam("fileName") final String fileName,
			@QueryParam("fileExt") final String fileExt, @QueryParam("username") final String username,
			@QueryParam("timeStamp") final String timeStamp, // this is the
																// client
			// timestamp in
			// millisecs as a string
			final String graphJSONdata) {

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		final OutputStreamWriter writer = new OutputStreamWriter(outputStream);

		// DEBUG
		logger.debug("exportGraphAsJSON: fileName = " + fileName + ", fileExt = " + fileExt
				+ ", graphJSONdata length = " + graphJSONdata.length());

		try {
			writer.write(graphJSONdata);
		} catch (final IOException e) {
			logger.error("exportGraphAsJSON: Exception writing JSON");
			logger.error(e.getMessage());
		}

		try {
			writer.close();
			outputStream.flush();
			outputStream.close();
		} catch (final java.io.IOException ioe) {
			logger.error("exportGraphAsJSON: I/O Exception when attempting to close output. Details "
					+ ioe.getMessage());
		}

		// Create the file on the Web Server
		File file = null;
		ServletContext servletContext = null;

		try {
			servletContext = globals.getServletContext();
		} catch (final Exception se) {
			logger.error("exportGraphAsJSON: ServletContext is null.");
		}

		String path = null;
		final String serverfileName = "GraphExport" + "_" + username + "_" + timeStamp + "_" + fileName + fileExt;

		if (servletContext != null) {
			path = servletContext.getRealPath("/");
		}
		// TODO - get the path from the servlerContext or the request param
		// TODO the file should be placed under the webserver's dir
		if (path == null) {
			// TODO - handle case if the Server is Linux instead of Windows
			path = "C:/Windows/Temp"; // Temp hack
		}

		// DEBUG
		logger.debug("exportGraphAsJSON: file path = " + path);

		try {
			file = new File(path, serverfileName);

			// file.mkdirs();
			final FileOutputStream fout = new FileOutputStream(file);
			fout.write(outputStream.toByteArray());
			fout.close();
			String finalPath = file.toURI().toString();
			finalPath = finalPath.replace("file:/", ""); // remove leading

			// DEBUG
			// logger.debug("exportGraphAsJSON: file toURI = " + finalPath);

			final ResponseBuilder response = Response.ok(finalPath);
			response.type("text/plain");
			final Response responseOut = response.build();
			return responseOut;
		} catch (final Exception fe) {
			logger.error("exportGraphAsJSON: Failed to create file for export. Details: " + fe.getLocalizedMessage());
		}
		return null;

	}

	// @Override
	// @POST
	// @Path("/exportGraphAsXML")
	// @Produces("application/xml")
	@Override
	public Response exportGraphAsXML(@QueryParam("fileName") final String fileName,
			@QueryParam("fileExt") final String fileExt, @QueryParam("username") final String username,
			@QueryParam("timeStamp") final String timeStamp, // this is the
																// client
			// timestamp in
			// millisecs as a string
			final String graphJSONdata) {

		// ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// OutputStreamWriter writer = new OutputStreamWriter(outputStream);

		// DEBUG
		logger.debug("exportGraphAsXML: fileName = " + fileName + ", fileExt = " + fileExt);

		/* NOT YET IMPLEMENTED FOR XML */

		return exportGraphAsJSON(fileName, fileExt, username, timeStamp, graphJSONdata);
	}

	// This service is used to downloaded a file containing a previously
	// exported graph
	// @GET
	// @Path("/getExportedGraph")
	// @Produces("application/x-unknown")
	@Override
	public Response getExportedGraph(@QueryParam("filePath") final String filePath) {
		// DEBUG
		logger.debug("getExportedGraph: filePath = " + filePath);

		FileInputStream inStream = null;
		String fileContents = null;
		try {
			inStream = new FileInputStream(filePath);
			fileContents = IOUtils.toString(inStream);
			inStream.close();
			// delete the file
			final File f = new File(filePath);
			if (!f.delete()) {
				logger.error("getExportedGraph: Failed to delete temporary file: " + filePath);
			}

		} catch (final Exception gfe) {
			logger.error("getExportedGraph: Failed to read file. Details: " + gfe.getLocalizedMessage());
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

		// DEBUG
		// logger.debug("getExportedGraph: File contents = " + fileContents +
		// "\n");

		final ResponseBuilder response = Response.ok(fileContents);
		// THIS IS KEY:
		// Force the Browser to download/save locally rather than attempt to
		// render it
		response.type("application/x-unknown");
		response.header("Content-Disposition", "attachment; filename=\"" + filePath + "\"");
		final Response responseOut = response.build();

		return responseOut;
	}

	@PostInjection
	public void initialize() {
		logger.debug("Export Graph Server now available");
	};

	/*
	 * NOT YET private void parseNodeForXML(JSONObject node, OutputStreamWriter
	 * writer) throws Exception {
	 * 
	 * @SuppressWarnings("unchecked") Iterator<String> it = (Iterator<String>)
	 * node.keys();
	 * 
	 * while (it.hasNext()) {
	 * 
	 * String key = it.next(); Object value = node.get(key);
	 * 
	 * if (value instanceof String) {
	 * 
	 * writer.write("<" + key + ">"); writer.write(value.toString());
	 * writer.write("</" + key + ">\n");
	 * 
	 * } else if (value instanceof JSONArray) {
	 * 
	 * writer.write("<" + key + ">\n"); JSONArray ja = (JSONArray) value;
	 * 
	 * // iterate over each index of the JSONArray, calling parseNodeForXML for
	 * each for (int i = 0; i < ja.length(); i++) {
	 * 
	 * Object jArrayObj = ja.get(i);
	 * 
	 * if (jArrayObj instanceof JSONObject) { // Recursive case
	 * parseNodeForXML((JSONObject)jArrayObj, writer); } else if (jArrayObj
	 * instanceof JSONArray) {
	 * 
	 * //TODO: figure out what to do about arrays
	 * 
	 * @SuppressWarnings("unused") String str = ""; } }
	 * 
	 * writer.write("</" + key + ">\n");
	 * 
	 * } else if (value instanceof JSONObject) {
	 * 
	 * writer.write("<" + key + ">\n"); parseNodeForXML((JSONObject)value,
	 * writer); writer.write("</" + key + ">\n");
	 * 
	 * } } } ** END NOT YET *
	 */

}
