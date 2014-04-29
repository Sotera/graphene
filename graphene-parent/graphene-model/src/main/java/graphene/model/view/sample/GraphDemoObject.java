package graphene.model.view.sample;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "data")
public class GraphDemoObject {
	// @node = {:details_html =>
	// "<h2>Neo ID: #{node_id(node)}</h2>\n<p class='summary'>\n#{get_properties(node)}</p>\n",
	// :data => {:attributes => attributes,
	// :name => node["data"]["name"],
	// :id => node_id(node)}
	// }

	// attributes = [{"name" => "No Relationships","name" =>
	// "No Relationships","values" => [{"id" => "#{params[:id]}","name" =>
	// "No Relationships "}]}] if attributes.empty?

	private String details_html;
	private NodeData data;

	/**
	 * @return the details_html
	 */
	public final String getDetails_html() {
		return details_html;
	}

	/**
	 * @param details_html
	 *            the details_html to set
	 */
	public final void setDetails_html(final String details_html) {
		this.details_html = details_html;
	}

	/**
	 * @return the data
	 */
	public NodeData getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(final NodeData data) {
		this.data = data;
	}

}
