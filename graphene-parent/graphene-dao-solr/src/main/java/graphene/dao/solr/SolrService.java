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

package graphene.dao.solr;

import graphene.util.FastNumberUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;

public class SolrService {

	// Thresold to check double value
	public static final double DEFAULT_THRESHOLD = 0.00000001;

	private SolrRequestParameters defaultSolrParams;

	private Logger logger;
	private SolrServer server;
	private double threshold = DEFAULT_THRESHOLD;

	private String url;

	public SolrService(String url, Logger log) {
		logger = log;
		loadDefaultConfigProps();

		server = new HttpSolrServer(url);
	}

	/**
	 * @return the defaultSolrParams
	 */
	public SolrRequestParameters getDefaultSolrParams() {
		return defaultSolrParams;
	}

	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @return the server
	 */
	public SolrServer getServer() {
		return server;
	}

	/**
	 * @return the threshold
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * load default configuration for the service
	 */
	void loadDefaultConfigProps() {
		try {
			// look at local directory first, if it doesn't exist, look at
			// classloader classpath
			InputStream is = null;

			try {

				is = new FileInputStream("./solrConfig.properties");

			} catch (Exception ex) {

			}
			if (is == null) {
				logger.info("can't find .property in local directory.. load property from class loader..");
				is = getClass().getClassLoader().getResourceAsStream(
						"solrConfig.properties");
			} else {
				logger.info("load .property from local directory..");
			}
			Properties props = new Properties();
			props.load(is);
			is.close();

			url = props.getProperty("URL");
			logger.debug("using URL: " + url);
			defaultSolrParams = new SolrRequestParameters();

			defaultSolrParams.setStartRow(FastNumberUtils
					.parseIntWithCheck(props.getProperty("STARTING_ROW", "0")));
			defaultSolrParams.setNumberOfRows(FastNumberUtils
					.parseIntWithCheck(props.getProperty(
							"NUMBER_OF_ROWS_PER_QUERY", "10")));

			String defaultFields = props.getProperty("DEFAULT_RETURN_FIELDS");
			if (defaultFields != null) {
				if (defaultFields.indexOf(",") > 0) {
					String[] fields = defaultFields.split(",");
					defaultSolrParams.setResultFields(Arrays.asList(fields));

				} else {
					List<String> fields = new ArrayList<String>();
					fields.add(defaultFields);
					defaultSolrParams.setResultFields(fields);
				}
			}
			if (props.getProperty("THRESOLD") != null) {
				threshold = Double.parseDouble(props.getProperty("THRESOLD"));
			}
			String orderFields = props.getProperty("ORDER_FIELDS");
			if (orderFields != null) {
				String[] fields = null;
				if (orderFields.indexOf(",") > 0) {
					fields = orderFields.split(",");
				} else {
					fields = new String[1];
					fields[0] = orderFields;
				}
				for (String field : fields) {
					String[] pair = field.split(" ");
					if (pair.length < 2
							|| !(pair[1].equalsIgnoreCase("asc") || pair[1]
									.equalsIgnoreCase("desc"))) {
						logger.error("ORDER_FIELDS property for field"
								+ field
								+ " is invalid, "
								+ "\n valid syntax: \"fieldname asc|desc, fieldname2 asc|desc\", order entry will be ignore");
					} else {
						ORDER order = ORDER.asc;
						if (pair[1].equalsIgnoreCase("desc")) {
							order = ORDER.desc;
						}
						defaultSolrParams.addSortField(pair[0].trim(), order);
					}

				}

			}

		} catch (IOException ioe) {
			logger.error("IOException in loadProps");
			for (StackTraceElement ste : ioe.getStackTrace())
				logger.error(ste.toString());
		}
	}

	/**
	 * @param defaultSolrParams
	 *            the defaultSolrParams to set
	 */
	public void setDefaultSolrParams(SolrRequestParameters defaultSolrParams) {
		this.defaultSolrParams = defaultSolrParams;
	}

	/**
	 * @param logger
	 *            the logger to set
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public void setServer(SolrServer server) {
		this.server = server;
	}

	/**
	 * @param threshold
	 *            the threshold to set
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}
