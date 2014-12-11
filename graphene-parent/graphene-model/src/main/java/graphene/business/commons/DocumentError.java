package graphene.business.commons;

import org.apache.tapestry5.alerts.Severity;

/**
 * A class for recording errors we see in a document based on ingest, or on the
 * fly during search.
 * 
 * @author djue
 * 
 */
public class DocumentError {
	private String title;
	private String description;
	private Severity severity;

	public DocumentError(String title, String description, Severity severity) {
		super();
		this.title = title;
		this.description = description;
		this.severity = severity;
	}

	/**
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public final void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public final void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the severity
	 */
	public final Severity getSeverity() {
		return severity;
	}

	/**
	 * @param severity
	 *            the severity to set
	 */
	public final void setSeverity(Severity severity) {
		this.severity = severity;
	}
}
