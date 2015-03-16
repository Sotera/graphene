package graphene.model.view.events;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * Note: We will be deprecating this class in favor of a more generic statistics
 * class. Also we will be precomputing more stats instead of calculating on the
 * fly. There is too much business logic in this classes and it doesn't apply to
 * much beside Kiva. --djue
 * 
 * @author pwg
 * 
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EventStatistics {

	private double balance = 0;

	public String account = null;

	public boolean useUnit = true; // defaults to true. Use local if
									// multi-unit data set

	public List<EventStat> stats = new Vector<EventStat>();

	public EventStatistics() {
		// needed for JAXB
	}

	private boolean transitionFound = false;

	public List<EventStat> getStats() {
		return stats;
	}

	/**
	 * 
	 * @param r
	 *            SingleSidedEventRow
	 * @param daily
	 *            true if daily, false if monthly
	 */
	public void updateFromEntry(final SingleSidedEventRow r, final boolean daily) {
		int yr = r.getYear();
		int mo = r.getMonth_zero_based();
		int day = daily ? r.getDay_one_based() : 0;

		EventStat m = getStat(yr, mo, day);
		if (m == null) {
			m = new EventStat();
			m.setYear(yr);
			m.setMonth(mo);
			m.setDay(day);
			stats.add(m);
		}
		m.incrementTransactionCount();
		m.addCredit(r.getCreditAsDouble());
		m.addDebit(r.getDebitAsDouble());
		m.setClosingBalance(r.getBalanceDouble()); // will be replaced each time
		// and we assume they come in in transaction order
	}

	public EventStat getStat(final int year, final int month, final int day) {
		for (EventStat m : stats) {
			if (m.month == month && m.year == year && m.day == day)
				return m;

		}
		return null;
	}

	public boolean isUseUnit() {
		return useUnit;
	}

	public void setUseUnit(final boolean useUnit) {
		this.useUnit = useUnit;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(final double balance) {
		this.balance = balance;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(final String account) {
		this.account = account;
	}

}
