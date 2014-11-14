package graphene.model.idl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class G_Report {
	Set<String> dates;
	String amount;
	Collection<String> iconList;
	Collection<String> addressList;
	Collection<String> cIdentifierList;
	Collection<String> identifierList;
	Collection<String> nameList;
	String reportId;
	String reportPageLink;
	String reportType;
	String searchValue;

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public G_Report() {
		dates = new TreeSet<String>();
		iconList = new HashSet<String>();
		addressList = new HashSet<String>();
		cIdentifierList = new HashSet<String>();
		identifierList = new HashSet<String>();
		nameList = new HashSet<String>();
	}

	public Set<String> getDates() {
		return dates;
	}

	public void setDates(Set<String> dates) {
		this.dates = dates;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Collection<String> getIconList() {
		return iconList;
	}

	public void setIconList(Collection<String> iconList) {
		this.iconList = iconList;
	}

	public Collection<String> getAddressList() {
		return addressList;
	}

	public void setAddressList(Collection<String> addressList) {
		this.addressList = addressList;
	}

	public Collection<String> getcIdentifierList() {
		return cIdentifierList;
	}

	public void setcIdentifierList(Collection<String> cIdentifierList) {
		this.cIdentifierList = cIdentifierList;
	}

	public Collection<String> getIdentifierList() {
		return identifierList;
	}

	public void setIdentifierList(Collection<String> identifierList) {
		this.identifierList = identifierList;
	}

	public Collection<String> getNameList() {
		return nameList;
	}

	public void setNameList(Collection<String> nameList) {
		this.nameList = nameList;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getReportPageLink() {
		return reportPageLink;
	}

	public void setReportPageLink(String reportPageLink) {
		this.reportPageLink = reportPageLink;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
}
