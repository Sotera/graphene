package graphene.model.view.events;

import graphene.util.DataFormatConstants;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an event with two sides, and an implied direction, i.e.
 * sender/receiver, source/target, from/to, produced/consumed, etc..
 * 
 * @author PWG for DARPA, djue
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DirectedEventRow implements Comparable<Object> {

	static Logger logger = LoggerFactory.getLogger(DirectedEventRow.class);
	private String comments;
	private String credit;

	private Map<String, String> data = new HashMap<String, String>();

	private String date;
	private long dateMilliSeconds; // used in plotting
	@XmlTransient
	private int day_one_based;

	private String debit;

	private long id;
	private String localUnitBalance;

	@XmlTransient
	private int month_zero_based;

	private String receiverId = "-1"; // XXX: In case the js was looking for

	// -1 as something special.
	private String senderId = "-1";

	private String unit;

	private String unitBalance;

	@XmlTransient
	private int year;

	public DirectedEventRow() {

	}

	public void addData(final String key, final String value) {
		data.put(key, value);
	}

	@Override
	public int compareTo(final Object o) {
		DirectedEventRow c = (DirectedEventRow) o;
		if (this.dateMilliSeconds > c.dateMilliSeconds)
			return 1;
		else if (this.dateMilliSeconds < c.dateMilliSeconds)
			return -1;
		else
			return 0;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DirectedEventRow other = (DirectedEventRow) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getComments() {
		return comments == null ? "" : comments;
	}

	public String getCredit() {
		return credit;
	}

	/**
	 * @return the data
	 */
	public Map<String, String> getData() {
		return data;
	}

	public String getDate() {
		return date;
	}

	public long getDateMilliSeconds() {
		return dateMilliSeconds;
	}

	public int getDay_one_based() {
		return day_one_based;
	}

	public String getDebit() {
		return debit;
	}

	/**
	 * @param date the date to set
	 */
	public final void setDate(String date) {
		this.date = date;
	}

	public long getId() {
		return id;
	}

	public String getLocalBalance() {
		return localUnitBalance;
	}



	/**
	 * @return the receiverId
	 */
	public final String getReceiverId() {
		return receiverId;
	}


	/**
	 * @return the senderId
	 */
	public final String getSenderId() {
		return senderId;
	}

	public String getUnit() {
		return unit;
	}

	public int getYear() {
		return year;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	public void setBalance(final double bal) {
		unitBalance = new DecimalFormat(DataFormatConstants.MONEY_FORMAT_STRING)
				.format(bal);
	}

	public void setComments(final String comments) {
		this.comments = comments;
	}

	public void setCredit(final double credit) {
		this.credit = new DecimalFormat(DataFormatConstants.MONEY_FORMAT_STRING)
				.format(credit);
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public void setDate(final Date date) {
		setDateMilliSeconds(date.getTime());
		this.date = FastDateFormat.getInstance(
				DataFormatConstants.DATE_FORMAT_STRING).format(date);
	}

	public void setDateMilliSeconds(final long seconds) {
		this.dateMilliSeconds = seconds;
	}

	public void setDay_one_based(final int day_one_based) {
		this.day_one_based = day_one_based;
	}

	public void setDebit(final double debit) {
		this.debit = new DecimalFormat(
				DataFormatConstants.WHOLE_NUMBER_FORMAT_STRING).format(debit);
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setLocalUnitBalance(final double bal) {
		this.localUnitBalance = new DecimalFormat(
				DataFormatConstants.WHOLE_NUMBER_FORMAT_STRING).format(bal);
	}

	public void setMonth_zero_based(final int month_zero_based) {
		this.month_zero_based = month_zero_based;
	}

	/**
	 * @param receiverId
	 *            the receiverId to set
	 */
	public final void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	/**
	 * @param senderId
	 *            the senderId to set
	 */
	public final void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public void setUnit(final String unit) {
		this.unit = unit;
	}

	public void setYear(final int year) {
		this.year = year;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DirectedEventRow ["
				+ (comments != null ? "comments=" + comments + ", " : "")
				+ (credit != null ? "credit=" + credit + ", " : "")
				+ (data != null ? "data=" + data + ", " : "")
				+ (date != null ? "date=" + date + ", " : "")
				+ "dateMilliSeconds="
				+ dateMilliSeconds
				+ ", day_one_based="
				+ day_one_based
				+ ", "
				+ (debit != null ? "debit=" + debit + ", " : "")
				+ "id="
				+ id
				+ ", "
				+ (localUnitBalance != null ? "localUnitBalance="
						+ localUnitBalance + ", " : "")
				+ "month_zero_based="
				+ month_zero_based
				+ ", "
				+ (receiverId != null ? "receiverId=" + receiverId + ", " : "")
				+ (senderId != null ? "senderId=" + senderId + ", " : "")
				+ (unit != null ? "unit=" + unit + ", " : "")
				+ (unitBalance != null ? "unitBalance=" + unitBalance + ", "
						: "") + "year=" + year + "]";
	}

}
