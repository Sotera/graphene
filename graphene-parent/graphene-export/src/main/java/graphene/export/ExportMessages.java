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

package graphene.export;

public class ExportMessages {
	public static final String SUGGEST_CVS = "Try the export again in CSV format to get all the records.";
	public static final String SHEETS_EXCEEDED_WARNING = "The number of results exceeded the numer of sheets the application can write, but here are some of the results.";
	public static final String RESULTS_EXCEEDED_WARNING = "There were too many results for this query, please narrow your selection or add constraints on times or amounts";
	// arbitrary selection of 64
	public static final int MAX_SHEETS = 64;
	public static final String SUGGEST_CONTACT_SUPPORT = "Please contact support for help with this issue, and include the parameters you queried for.";

}
