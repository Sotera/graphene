package graphene.business.commons;

import org.joda.time.DateTime;

public class ReportViewEvent {
	String id = null;
	String reportId;

	String reportType;

	private long timeInitiated = DateTime.now().getMillis();

	private String userId = "None";

	private String userName = "None";

	public String getId() {
		return id;
	}

	public ReportViewEvent(String reportId, String reportType, String userId,
			String userName) {
		super();
		this.reportId = reportId;
		this.reportType = reportType;
		this.userId = userId;
		this.userName = userName;
	}

	public String getReportId() {
		return reportId;
	}

	public String getReportType() {
		return reportType;
	}

	public long getTimeInitiated() {
		return timeInitiated;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public void setTimeInitiated(long timeInitiated) {
		this.timeInitiated = timeInitiated;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
