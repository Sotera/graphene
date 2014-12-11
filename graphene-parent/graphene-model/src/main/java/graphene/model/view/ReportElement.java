package graphene.model.view;

import graphene.model.query.BasicQuery;

public class ReportElement {

	private String display;
	private Object object;
	private BasicQuery pivotQuery;

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public BasicQuery getPivotQuery() {
		return pivotQuery;
	}

	public void setPivotQuery(BasicQuery pivotQuery) {
		this.pivotQuery = pivotQuery;
	}
}
