package graphene.model.view.entities;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
/**
 * Contains the data returned in the search results
 * 
 * Currently based on entityref but could be expanded to include additional
 * data from the customer record
 * 
 * @author PWG for DARPA
 *
 * Will be replaced by Entity
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerDetails implements Cloneable {

	private Set<String> accountSet = new HashSet<String>();
	private String address = null;
	private String customerName = "";
	private String customerNumber = null;
	private String email = "";
	private String erm = "";
	/**
	 * Might have additional information
	 */
	private String expandedName = "";
	/**
	 * used in search results. We show the string that matched the query as it
	 * is sorted this way
	 */
	private String matchString = null;
	private int status;

	public CustomerDetails() {
		// needed
	}

	public CustomerDetails(String nbr) {
		setCustomerNumber(nbr);
	}

	public void addAccount(String ac) {
		accountSet.add(ac);
	}

	public void addIdentifier(IdType id, String value) {
		if (value == null || value.length() == 0 || id == null) {
			return;
		}

		String shortName = id.getShortName();
		String family = id.getFamily();

		if (id.getColumnSource().equals("Name,ResAddress1")) // kludge but
																// needed as
																// they used
																// this for D/O
																// etc which we
																// consider
			// part of the name
			setExpandedName(value);

		else if (family.equals("name")) { // use the shortest version
			int oldLen = customerName.length();
			// if (oldLen == 0 || oldLen > value.length())
			if (oldLen == 0)
				setCustomerName(value);
		} else if (shortName.contains("Address")) {
			setAddress(value);
		} else if (family.equals("email")) {
			if (email.contains(value))
				return;
			if (value.contains(" ")) // Fix for bad email addresses
				value = value.replace(" ", "");
			if (email.length() > 0)
				email += ", ";
			setEmail(email + value);
		}
	}

	public CustomerDetails clone() throws CloneNotSupportedException {
		super.clone();
		CustomerDetails cnew = new CustomerDetails(this.customerNumber);
		cnew.setAddress(this.address);
		cnew.setEmail(email);
		cnew.setCustomerName(customerName);
		cnew.setExpandedName(expandedName);
		return cnew;
	}

	public Set<String> getAccountSet() {
		return accountSet;
	}

	public String getAddress() {
		return address;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public String getEmail() {
		return email;
	}

	public String getErm() {
		return erm;
	}

	public String getExpandedName() {
		return expandedName;
	}

	public String getMatchString() {
		return matchString;
	}

	public int getStatus() {
		return status;
	}

	public void setAccountSet(final Set<String> accountSet) {
		this.accountSet = accountSet;
	}

	public void setAddress(final String addr) {
		this.address = addr;
	}

	public void setCustomerName(final String customerName) {
		this.customerName = customerName;
	}

	public void setCustomerNumber(final String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public void setErm(final String erm) {
		this.erm = erm;
	}

	public void setExpandedName(final String expandedName) {
		this.expandedName = expandedName;
	}

	public void setMatchString(final String matchString) {
		this.matchString = matchString;
	}

	public void setStatus(final int status) {
		this.status = status;
	}

}
