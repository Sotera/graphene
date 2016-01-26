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

package graphene.dao.es;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;

import java.util.List;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class RunMe {
	private static final String NOTES_TYPE_NAME = "notes";
	private static final String DIARY_INDEX_NAME = "diary";
	private static String defaultESTimeout = "30s";

	private static void createTestIndex(final JestClient jestClient) throws Exception {
		// create new index (if u have this in elasticsearch.yml and prefer
		// those defaults, then leave this out
		final ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();
		settings.put("number_of_shards", 3);
		settings.put("number_of_replicas", 0);
		jestClient.execute(new CreateIndex.Builder(DIARY_INDEX_NAME).setParameter("timeout", defaultESTimeout)
				.settings(settings.build().getAsMap()).build());
	}

	private static void deleteTestIndex(final JestClient jestClient) throws Exception {
		final DeleteIndex deleteIndex = new DeleteIndex.Builder(DIARY_INDEX_NAME).build();
		jestClient.execute(deleteIndex);
	}

	private static void indexSomeData(final JestClient jestClient) throws Exception {
		// Blocking index
		final Note note1 = new Note("mthomas", "Note1: do u see this - " + System.currentTimeMillis());
		Index index = new Index.Builder(note1).index(DIARY_INDEX_NAME).type(NOTES_TYPE_NAME).build();
		jestClient.execute(index);

		// Asynch index
		final Note note2 = new Note("mthomas", "Note2: do u see this - " + System.currentTimeMillis());
		index = new Index.Builder(note2).index(DIARY_INDEX_NAME).type(NOTES_TYPE_NAME).build();
		jestClient.executeAsync(index, new JestResultHandler<JestResult>() {
			@Override
			public void completed(final JestResult result) {
				note2.setId((String) result.getValue("_id"));
				System.out.println("completed==>>" + note2);
			}

			@Override
			public void failed(final Exception ex) {
			}
		});

		// bulk index
		final Note note3 = new Note("mthomas", "Note3: do u see this - " + System.currentTimeMillis());
		final Note note4 = new Note("mthomas", "Note4: do u see this - " + System.currentTimeMillis());
		final Bulk bulk = new Bulk.Builder()
				.addAction(new Index.Builder(note3).index(DIARY_INDEX_NAME).type(NOTES_TYPE_NAME).build())
				.addAction(new Index.Builder(note4).index(DIARY_INDEX_NAME).type(NOTES_TYPE_NAME).build()).build();
		final JestResult result = jestClient.execute(bulk);

		Thread.sleep(2000);

		System.out.println(result.toString());
	}

	public static void main(final String[] args) {
		try {
			// Get Jest client
			final HttpClientConfig clientConfig = new HttpClientConfig.Builder("http://localhost:9200").multiThreaded(
					true).build();
			final JestClientFactory factory = new JestClientFactory();
			factory.setHttpClientConfig(clientConfig);
			final JestClient jestClient = factory.getObject();

			try {
				// run test index & searching
				RunMe.deleteTestIndex(jestClient);
				RunMe.createTestIndex(jestClient);
				RunMe.indexSomeData(jestClient);
				RunMe.readAllData(jestClient);
			} finally {
				// shutdown client
				jestClient.shutdownClient();
			}

		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void readAllData(final JestClient jestClient) throws Exception {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.termQuery("note", "see"));

		final Search search = new Search.Builder(searchSourceBuilder.toString())
				.setParameter("timeout", defaultESTimeout).addIndex(DIARY_INDEX_NAME).addType(NOTES_TYPE_NAME).build();
		System.out.println(searchSourceBuilder.toString());
		final JestResult result = jestClient.execute(search);
		final List<Note> notes = result.getSourceAsObjectList(Note.class);
		for (final Note note : notes) {
			System.out.println(note);
		}
	}
}