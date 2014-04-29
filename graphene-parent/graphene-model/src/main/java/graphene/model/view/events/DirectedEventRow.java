package graphene.model.view.events;

import graphene.util.DataFormatConstants;

import java.text.DecimalFormat;
import java.util.Date;

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
	public int accountGroup = 0;
	public String receiverId = "-1"; // XXX: In case the js was looking for
										// -1 as something special.
	public String senderId = "-1";
	@XmlTransient
	private double amountAsDouble = 0;
	@XmlTransient
	private double balanceDouble;
	public String comments;

	public String credit;

	public String date;
	public long dateMilliSeconds; // used in plotting
	@XmlTransient
	private int day_one_based;
	public String debit;

	public long id;

	@XmlTransient
	private double localBalanceDouble;
	public String localUnitBalance;

	@XmlTransient
	private double locAmountAsDouble = 0;
	@XmlTransient
	private int month_zero_based;

	public String unit;

	public String unitBalance;
	@XmlTransient
	private int year;

	public DirectedEventRow() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compareTo(final Object o) {
		// TODO Auto-generated method stub
		DirectedEventRow c = (DirectedEventRow) o;
		if (this.dateMilliSeconds > c.dateMilliSeconds)
			return 1;
		else if (this.dateMilliSeconds < c.dateMilliSeconds)
			return -1;
		else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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

	public double getBalanceDouble() {
		return balanceDouble;
	}

	public String getBalanceStr() {
		return unitBalance;
	}

	public String getComments() {
		return comments == null ? "" : comments;
	}

	public String getCredit() {
		return credit;
	}

	public double getCreditAsDouble() {
		return amountAsDouble > 0 ? amountAsDouble : 0;
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

	public double getDebitAsDouble() {
		return amountAsDouble < 0 ? -amountAsDouble : 0;
	}

	public long getId() {
		return id;
	}

	public String getLocalBalance() {
		return localUnitBalance;
	}

	public double getLocalBalanceDouble() {
		return localBalanceDouble;
	}

	public int getMonth_zero_based() {
		return month_zero_based;
	}

	public String getReceiverAccount() {
		return receiverId;
	}

	public String getReceiverAccountasString() {
		return new Long(receiverId).toString();
	}

	public String getSenderAccount() {
		return senderId;
	}

	public String getSenderAccountasString() {
		return new Long(senderId).toString();
	}

	public String getUnit() {
		return unit;
	}

	public int getYear() {
		return year;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	public void setAcnoReceiver(final String account) {
		this.receiverId = account;
	}

	public void setAcnoSender(final String account) {
		this.senderId = account;
	}

	public void setBalance(final double bal) {
		balanceDouble = bal;
		unitBalance = new DecimalFormat(DataFormatConstants.MONEY_FORMAT_STRING)
				.format(bal);
	}

	public void setBalanceDouble(final double balanceDouble) {
		this.balanceDouble = balanceDouble;
	}

	public void setComments(final String comments) {
		this.comments = comments;
	}

	public void setCredit(final double credit) {
		this.amountAsDouble = -credit;
		this.credit = new DecimalFormat(DataFormatConstants.MONEY_FORMAT_STRING)
				.format(credit);
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
		this.amountAsDouble = -debit;
		this.debit = new DecimalFormat(DataFormatConstants.MONEY_FORMAT_STRING)
				.format(debit);
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setLocalBalanceDouble(final double localBalanceDouble) {
		this.localBalanceDouble = localBalanceDouble;
	}

	public void setLocalUnitBalance(final double bal) {
		localBalanceDouble = bal;
		this.localUnitBalance = new DecimalFormat(
				DataFormatConstants.MONEY_FORMAT_STRING).format(bal);
	}

	public void setMonth_zero_based(final int month_zero_based) {
		this.month_zero_based = month_zero_based;
	}

	public void setUnit(final String unit) {
		this.unit = unit;
	}

	public void setYear(final int year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "DirectedEventRow [accountGroup=" + accountGroup
				+ ", receiverId=" + receiverId + ", senderId=" + senderId
				+ ", amountAsDouble=" + amountAsDouble + ", balanceDouble="
				+ balanceDouble + ", comments=" + comments + ", credit="
				+ credit + ", date=" + date + ", dateMilliSeconds="
				+ dateMilliSeconds + ", day_one_based=" + day_one_based
				+ ", debit=" + debit + ", id=" + id + ", localBalanceDouble="
				+ localBalanceDouble + ", localUnitBalance=" + localUnitBalance
				+ ", locAmountAsDouble=" + locAmountAsDouble
				+ ", month_zero_based=" + month_zero_based + ", unit=" + unit
				+ ", unitBalance=" + unitBalance + ", year=" + year + "]";
	}

	public Double updateBalance(final Double sofar) {

		this.balanceDouble = sofar + amountAsDouble;
		this.unitBalance = new DecimalFormat(
				DataFormatConstants.MONEY_FORMAT_STRING).format(sofar);
		return balanceDouble;

	}

	public Double updateLocalBalance(final Double sofar) {
		this.localBalanceDouble = sofar + locAmountAsDouble;
		this.localUnitBalance = new DecimalFormat(
				DataFormatConstants.MONEY_FORMAT_STRING).format(sofar);
		return localBalanceDouble;

	}

}
