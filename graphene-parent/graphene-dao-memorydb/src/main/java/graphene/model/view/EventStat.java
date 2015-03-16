package graphene.model.view;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * An individual stat
 * 
 * @author pwg
 * 
 */
@Deprecated
@XmlAccessorType(XmlAccessType.FIELD)
public class EventStat {

	public int year = 0; // e.g. 2006
	public int month = 0; // 0 to 11
	// FIXME XXX: Why in the world are there always 31 days? C'mon people --djue
	public int day = 0; // 1 to 31 when populated. Only used for daily
						// statistics
	public int nbrTransactions = 0;
	public long totalCredits = 0; // We are rounding because we are not
									// interested in fractions
	public long totalDebits = 0;
	public Double closingBalance = null;

	public void addCredit(final double amount) {
		totalCredits += amount;
	}

	public void addDebit(final double amount) {
		totalDebits += amount;
	}

	public Double getClosingBalance() {
		return closingBalance;
	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getNbrTransactions() {
		return nbrTransactions;
	}

	public long getTotalCredits() {
		return totalCredits;
	}

	public long getTotalDebits() {
		return totalDebits;
	}

	public int getYear() {
		return year;
	}

	public void incrementTransactionCount() {
		++nbrTransactions;
	}

	public void setClosingBalance(final double closingBalance) {
		this.closingBalance = new Double(closingBalance);
	}

	public void setDay(final int day) {
		this.day = day;
	}

	public void setMonth(final int month) {
		this.month = month;
	}

	public void setYear(final int year) {
		this.year = year;
	}

}
