package graphene.model.view;

public class LegendItem {
	private String title;
	private String style;
	private String label;

	/**
	 * 
	 * @param label
	 *            the text that will be visible
	 * @param style
	 *            the css style
	 * @param title
	 *            the html title value, usually a longer explanation
	 */
	public LegendItem(final String label, final String style, final String title) {
		super();
		this.title = title;
		this.style = style;
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(final String label) {
		this.label = label;
	}

	/**
	 * @param style
	 *            the style to set
	 */
	public void setStyle(final String style) {
		this.style = style;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}
}
