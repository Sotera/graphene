package graphene.model.graph.cytoscapejs;

import graphene.model.graph.GenericEdge;
import graphene.model.graph.GraphObjectData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CSEdgeData {

	public String id;
	public String source;
	public String target;
	public String amount;
	public String weight = "0";

	public List<CSAttr> attrs = new ArrayList<CSAttr>();

	public int linewidth = 1;
	public String type = "";
	public boolean visible = true;
	public String[] direction = null;

	public CSEdgeData() {

	}

	public CSEdgeData(GenericEdge e) {
		if (e.isDirected()) {
			direction = new String[2];
			direction[0] = e.getSource();
			direction[1] = e.getTarget();
			type = "arrow";
		}

		source = e.getSource();
		target = e.getTarget();

		amount = e.getAmount();
		weight = Integer.toString(e.getWeight());

		Set<GraphObjectData> s = e.getDataSet();
		for (GraphObjectData d : s) {
			this.attrs.add(new CSAttr(d.getKey(), d.getKeyVal()));
		}
	}

}
