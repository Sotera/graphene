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
 * 
 * A ledger is one side of an event/transfer which may have other participant
 * ids, and whose value is either a credit or debit.
 * 
 * 
 * FIXME: This is supposed to be a very simple serializable view object--why is
 * there business logic here??--djue
 * 
 * @author PWG for DARPA
 * 
 *         FIXME: This does not apply outside of Kiva, so change this to start
 *         using the generic Event model. It is also of limited use for
 *         displaying.--djue
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SingleSidedEventRow implements Comparable<Object> {

	static Logger logger = LoggerFactory.getLogger(SingleSidedEventRow.class);

	private String account;
	private int accountGroup = 0;
	@XmlTransient
	private double amountAsDouble = 0;
	private String balance;
	@XmlTransient
	private double balanceDouble;
	private String comments;
	private String credit;
	private String date;
	@XmlTransient
	private long dateMilliSeconds;

	@XmlTransient
	private int day_one_based;
	private String debit;

	private long id;
	private String localBalance;

	@XmlTransient
	private double localBalanceDouble;

	private String localCredit;
	private String localDebit;

	@XmlTransient
	private double locAmountAsDouble = 0;
	@XmlTransient
	private int month_zero_based;

	private String unit;

	@XmlTransient
	private int year;
	public SingleSidedEventRow() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compareTo(final Object o) {
		SingleSidedEventRow c = (SingleSidedEventRow) o;
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
		SingleSidedEventRow other = (SingleSidedEventRow) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getAccount() {
		return account;
	}

	public int getAccountGroup() {
		return accountGroup;
	}

	public double getBalanceDouble() {
		return balanceDouble;
	}

	public String getBalanceStr() {
		return balance;
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
		return localBalance;
	}

	public double getLocalBalanceDouble() {
		return localBalanceDouble;
	}

	public String getLocalCredit() {
		return localCredit;
	}

	public String getLocalDebit() {
		return localDebit;
	}

	public int getMonth_zero_based() {
		return month_zero_based;
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

	public void setAccount(final String account) {
		this.account = account;
	}

	public void setAccountGroup(final int accountGroup) {
		this.accountGroup = accountGroup;
	}

	public void setBalance(final double bal) {
		balanceDouble = bal;
		balance = new DecimalFormat(DataFormatConstants.MONEY_FORMAT_STRING)
				.format(bal);
	}

	public void setBalanceDouble(final double balanceDouble) {
		this.balanceDouble = balanceDouble;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setCredit(final double credit) {
		this.amountAsDouble = credit;
		this.credit = new DecimalFormat(DataFormatConstants.MONEY_FORMAT_STRING)
				.format(credit);
	}

	public void setCredit(final String credit) {
		setCredit(Double.parseDouble(credit));
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

	public void setDebit(final String debit) {
		setDebit(Double.parseDouble(debit));
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setLocalBalance(final double bal) {
		localBalanceDouble = bal;
		this.localBalance = new DecimalFormat(
				DataFormatConstants.MONEY_FORMAT_STRING).format(bal);
	}

	public void setLocalBalanceDouble(final double localBalanceDouble) {
		this.localBalanceDouble = localBalanceDouble;
	}

	public void setLocalCredit(final double amount) {
		this.locAmountAsDouble = amount;
		this.localCredit = new DecimalFormat(
				DataFormatConstants.MONEY_FORMAT_STRING).format(amount);
	}

	/**
	 * TODO:This is messy. It's obvious we want to keep the double variable as
	 * well as the string, we just need to use better conventions. ---djue
	 * 
	 * @param amount
	 */
	public void setLocalCredit(String amount) {
		setLocalCredit(Double.parseDouble(amount));
	}

	public void setLocalDebit(double amount) {
		this.locAmountAsDouble = -amount;
		this.localDebit = new DecimalFormat(
				DataFormatConstants.MONEY_FORMAT_STRING).format(amount);
	}

	/**
	 * TODO:This is messy. It's obvious we want to keep the double variable as
	 * well as the string, we just need to use better conventions. ---djue
	 * 
	 * @param amount
	 */
	public void setLocalDebit(final String amount) {
		setLocalDebit(Double.parseDouble(amount));
	}

	public void setMonth_zero_based(int month_zero_based) {
		if (month_zero_based > 11) {
			logger.error("Invalid month for zero based month");
			return;
		}
		this.month_zero_based = month_zero_based;
	}

	public void setUnit(final String unit) {
		this.unit = unit;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "EventRow [account=" + account + ", accountGroup="
				+ accountGroup + ", amountAsDouble=" + amountAsDouble
				+ ", balance=" + balance + ", balanceDouble=" + balanceDouble
				+ ", credit=" + credit + ", unit=" + unit + ", date=" + date
				+ ", dateMilliSeconds=" + dateMilliSeconds + ", day_one_based="
				+ day_one_based + ", debit=" + debit + ", id=" + id
				+ ", localBalance=" + localBalance + ", localBalanceDouble="
				+ localBalanceDouble + ", localCredit=" + localCredit
				+ ", localDebit=" + localDebit + ", locAmountAsDouble="
				+ locAmountAsDouble + ", month_zero_based=" + month_zero_based
				+ ", comments=" + comments + ", year=" + year + "]";
	}




	public Double updateBalance(Double sofar) {
		sofar += amountAsDouble;
		this.balanceDouble = sofar;
		this.balance = new DecimalFormat(
				DataFormatConstants.MONEY_FORMAT_STRING).format(sofar);
		return sofar;

	}

	public Double updateLocalBalance(Double sofar) {
		sofar += locAmountAsDouble;
		this.localBalanceDouble = sofar;
		this.localBalance = new DecimalFormat(
				DataFormatConstants.MONEY_FORMAT_STRING).format(sofar);
		return sofar;

	}

}
