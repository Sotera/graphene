package graphene.augment.snlp.model;

public class StringWithSentiment {

	public StringWithSentiment(String line, String css) {
		this.line = line;
		this.css = css;
	}

	private String line = "", css = "";

	/**
	 * @return the line
	 */
	public final String getLine() {
		return line;
	}

	/**
	 * @param line
	 *            the line to set
	 */
	public final void setLine(String line) {
		this.line = line;
	}

	/**
	 * @return the css
	 */
	public final String getCss() {
		return css;
	}

	/**
	 * @param css
	 *            the css to set
	 */
	public final void setCss(String css) {
		this.css = css;
	}
}
