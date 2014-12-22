//
// Copyright 2010 GOT5 (GO Tapestry 5)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package graphene.web.test.pages;

import graphene.web.data.Celebrity;
import graphene.web.data.CelebritySource;
import graphene.web.data.IDataSource;

import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BeanModelSource;
import org.got5.tapestry5.jquery.internal.TableInformation;

@Import(stylesheet = {
		"context:core/js/plugin/datatables/media/css/TableTools_JUI.css",
		"context:core/js/plugin/datatables/media/css/TableTools.css" })

public class DataTables {
	@Inject
	private IDataSource dataSource;
	private Celebrity celebrity;
	private CelebritySource celebritySource;

	@Property
	private Celebrity current;

	public GridDataSource getCelebritySource() {
		if (celebritySource == null) {
			celebritySource = new CelebritySource(dataSource);
		}
		return celebritySource;
	}

	public List<Celebrity> getAllCelebrities() {
		System.out.println("Getting all celebrities...");
		return dataSource.getAllCelebrities();
	}

	public Celebrity getCelebrity() {
		return celebrity;
	}

	public void setCelebrity(Celebrity celebrity) {
		this.celebrity = celebrity;
	}

	@Inject
	private ComponentResources resources;

	@Inject
	private BeanModelSource beanModelSource;

	@SuppressWarnings("rawtypes")
	private BeanModel model;

	@SuppressWarnings("rawtypes")
	public BeanModel getModel() {
		this.model = beanModelSource.createDisplayModel(Celebrity.class,
				resources.getMessages());
		this.model.get("firstName").sortable(false);
		return model;
	}

	public TableInformation getInformation() {
		return new TableInformation() {

			public String getTableSummary() {
				// TODO Auto-generated method stub
				return "A summary description of table data";
			}

			public String getTableCaption() {
				// TODO Auto-generated method stub
				return "The table title";
			}

			public String getTableCSS() {
				// TODO Auto-generated method stub
				return "k-data-table";
			}
		};
	}

	public JSONObject getOptions() {

		JSONObject json = new JSONObject("bJQueryUI", "true", "bStateSave",
				"true", "sDom", "TC<\"clear\">Rlfrtip");
		return json;
	}

	@Property
	private int index;

	public String getCss() {
		if (index == 0)
			return "first";
		return "other";
	}
}
