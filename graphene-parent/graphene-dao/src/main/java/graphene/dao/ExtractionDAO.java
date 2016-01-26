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

package graphene.dao;

import graphene.model.extracted.CrossReferenceResult;
import graphene.model.extracted.EncodedFile;
import graphene.model.extracted.ExtractedData;
import graphene.model.idl.G_CallBack;
import graphene.model.idl.G_EntityQuery;

public interface ExtractionDAO {
	long count(final G_EntityQuery q) throws Exception;

	public boolean exists(final String id);

	ExtractedData getExtractedData();

	public boolean performCallback(final long offset, final long maxResults, final G_CallBack cb, final G_EntityQuery q);

	/**
	 * TODO: Move this to a crossreference dao.
	 * 
	 * @param results
	 */
	void saveCrossReferenceResults(CrossReferenceResult results);

	void saveEncodedFile(final EncodedFile ef);

	void saveExtraction(ExtractedData ed);
}
