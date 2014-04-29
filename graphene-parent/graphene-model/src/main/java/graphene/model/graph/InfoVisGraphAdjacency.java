package graphene.model.graph;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "adjacencies", "data", "id", "name" })
public class InfoVisGraphAdjacency {
	@JsonProperty("data")
	HashMap<String, String> data;
	@JsonProperty("adjacencies")
	ArrayList<InfoVisEdge> edges;
	@JsonProperty("id")
	String id;
	@JsonProperty("name")
	String name;

	public InfoVisGraphAdjacency() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "InfoVisGraphAdjacency [data=" + data + ", edges=" + edges
				+ ", id=" + id + ", name=" + name + "]";
	}

	/**
	 * 
	 * @param id
	 * @param name
	 * @param d
	 */
	public InfoVisGraphAdjacency(String id, String name, String... d) {
		this.id = id;
		this.name = name;
		for (int i = 0; i < d.length; i += 2) {
			if (d.length > i + 1) {
				addData(d[i], d[i + 1]);
			}
		}
	}

	// keys are like [$dim, $color, $type]
	// ex {$dim=10, $color=#83548B, $type=circle}
	public void addData(String key, String value) {
		if (data == null) {
			data = new HashMap<String, String>(3);
		}
		data.put(key, value);
	}

	public void addEdge(InfoVisEdge a) {
		if (edges == null) {
			edges = new ArrayList<InfoVisEdge>(5);
		}
		edges.add(a);
	}

	public HashMap<String, String> getData() {
		return data;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setData(HashMap<String, String> data) {
		this.data = data;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

}
