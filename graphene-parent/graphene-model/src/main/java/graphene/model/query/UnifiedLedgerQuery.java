package graphene.model.query;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class UnifiedLedgerQuery extends BasicQuery {
	/*
	 * A list of zero or more accounts to filter on. This may be a mixture of
	 * 'parent' and 'child' accounts
	 */
	private Set<String> accountList = new HashSet<String>();
	/*
	 * If true, we should look for parent account(s), and then find all the
	 * children of those parent(s).
	 */
	private boolean findRelatedAccounts = false;

	/*
	 * The maximum monetary amount (unit not specified)
	 */
	private double maxAmount = 0;

	/*
	 * The minimum monetary amount (unit not specified)
	 */
	private double minAmount = 0;

	/*
	 * tries to pass the old account number if searched for a new one.
	 */
	private String parentAccount = null;
	/*
	 * A string we will look for in the comments
	 */
	private String comments = null;

	/**
	 * Default constructor
	 */
	public UnifiedLedgerQuery() {

	}

	/**
	 * 
	 * @param accountNumber
	 * @param accountList
	 * @param start
	 * @param limit
	 * @param minSecs
	 * @param maxSecs
	 * @param minAmount
	 * @param maxAmount
	 * @param comments
	 */
	// public LedgerQuery(String accountNumber, List<String> accountList,
	// int start, int limit, long minSecs, long maxSecs, double minAmount,
	// double maxAmount, String comments) {
	// // TODO:Should this be if/else or should we allow both? Do we even need
	// // the account number, can we just force account to be in a list in the
	// // first place?
	// if (accountList != null && accountList.size() != 0)
	// this.accountList.addAll(accountList);
	// else if (accountNumber != null && accountNumber.length() != 0) {
	// // this.singleAccount = accountNumber;
	// this.accountList.add(accountNumber);
	// }
	// this.start = start;
	// this.limit = limit;
	// this.minSecs = minSecs;
	// this.maxSecs = maxSecs;
	// this.minAmount = minAmount;
	// this.maxAmount = maxAmount;
	// this.comments = comments;
	//
	// }

	/**
	 * Add a single account number to the list of accounts to search. This is
	 * the proper way of adding account numbers.
	 * 
	 * @param accountNumber
	 */
	public void addAccountNumber(String accountNumber) {
		if (accountNumber != null && accountNumber.length() != 0) {
			this.accountList.add(accountNumber);
			// this.singleAccount = accountNumber;
		}
	}

	/**
	 * This is the proper, safe way of adding account numbers. Use this instead
	 * of setting the collection, as we filter out bad values.
	 * 
	 * @param accountNumbers
	 */
	public void addAccountNumbers(Collection<String> accountNumbers) {
		if (accountNumbers != null && accountNumbers.size() != 0) {
			for (String ac : accountNumbers) {
				addAccountNumber(ac);
			}
		}
	}

	public void addAccountNumbers(String... accountNumbers) {
		if (accountNumbers != null && accountNumbers.length != 0) {
			for (String ac : accountNumbers) {
				addAccountNumber(ac);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((accountList == null) ? 0 : accountList.hashCode());
		result = prime * result + (findRelatedAccounts ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(maxAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((parentAccount == null) ? 0 : parentAccount.hashCode());
		result = prime * result
				+ ((comments == null) ? 0 : comments.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnifiedLedgerQuery other = (UnifiedLedgerQuery) obj;
		if (accountList == null) {
			if (other.accountList != null)
				return false;
		} else if (!accountList.equals(other.accountList))
			return false;
		if (findRelatedAccounts != other.findRelatedAccounts)
			return false;
		if (Double.doubleToLongBits(maxAmount) != Double
				.doubleToLongBits(other.maxAmount))
			return false;
		if (Double.doubleToLongBits(minAmount) != Double
				.doubleToLongBits(other.minAmount))
			return false;
		if (parentAccount == null) {
			if (other.parentAccount != null)
				return false;
		} else if (!parentAccount.equals(other.parentAccount))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		return true;
	}

	// public boolean equalsIgnoreLimits(LedgerQuery l) {
	// if (l == null)
	// return false;
	// if (this.maxSecs != l.maxSecs || this.minSecs != l.minSecs)
	// return false;
	//
	// if (Math.abs(this.minAmount - l.minAmount) > 0.00001
	// || Math.abs(this.maxAmount - l.maxAmount) > 0.00001)
	// return false;
	// if (!this.accountList.equals(l.accountList))
	// return false;
	//
	// if (this.comments == null)
	// return l.comments == null;
	//
	// if (l.comments == null)
	// return this.comments == null;
	//
	// return this.comments.equals(l.comments);
	// }

	/**
	 * @return the fromdtSecs
	 */
	// public long getFromdtSecs() {
	// return minSecs;
	// }

	public boolean equalsIgnoreLimits(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnifiedLedgerQuery other = (UnifiedLedgerQuery) obj;
		if (accountList == null) {
			if (other.accountList != null)
				return false;
		} else if (!accountList.equals(other.accountList))
			return false;
		if (findRelatedAccounts != other.findRelatedAccounts)
			return false;
		if (Double.doubleToLongBits(maxAmount) != Double
				.doubleToLongBits(other.maxAmount))
			return false;
		if (getMaxSecs() != other.getMaxSecs())
			return false;
		if (Double.doubleToLongBits(minAmount) != Double
				.doubleToLongBits(other.minAmount))
			return false;
		if (getMinSecs() != other.getMinSecs())
			return false;
		if (parentAccount == null) {
			if (other.parentAccount != null)
				return false;
		} else if (!parentAccount.equals(other.parentAccount))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (getSortColumn() == null) {
			if (other.getSortColumn() != null)
				return false;
		} else if (!getSortColumn().equals(other.getSortColumn()))
			return false;
		if (getFirstResult() != other.getFirstResult())
			return false;
		return true;
	}

	/**
	 * @return the accountList
	 */
	public Set<String> getAccountList() {
		return accountList;
	}

	/**
	 * @return the maxAmount
	 */
	public double getMaxAmount() {
		return maxAmount;
	}

	/**
	 * @return the minAmount
	 */
	public double getMinAmount() {
		return minAmount;
	}

	/**
	 * @return the parentAccount
	 */
	public String getParentAccount() {
		return parentAccount;
	}

	public String getComments() {
		return comments;
	}

	public String getSingleAccount() {
		return (String) this.accountList.toArray()[0];
	}

	/**
	 * @param fromdtSecs
	 *            the fromdtSecs to set
	 */
	// public void setFromdtSecs(long fromdtSecs) {
	// this.minSecs = fromdtSecs;
	// }

	/**
	 * An alternative setter for parameters given as a String
	 * 
	 * @param fromdtSecsStr
	 */
	// public void setFromdtSecs(String fromdtSecsStr) {
	// if (fromdtSecsStr != null && fromdtSecsStr.length() > 0)
	// minSecs = Long.parseLong(fromdtSecsStr);
	//
	// }

	/**
	 * @return the findRelatedAccounts
	 */
	public boolean isFindRelatedAccounts() {
		return findRelatedAccounts;
	}

	public boolean isSingleAccount() {
		return accountList.size() == 1;
	}

	/**
	 * @param accountList
	 *            the accountList to set
	 */
	public void setAccountList(Set<String> accountList) {
		this.accountList = accountList;
	}



	/**
	 * @param findRelatedAccounts
	 *            the findRelatedAccounts to set
	 */
	public void setFindRelatedAccounts(boolean findRelatedAccounts) {
		this.findRelatedAccounts = findRelatedAccounts;
	}

	/**
	 * @param todtSecs
	 *            the todtSecs to set
	 */
	// public void setTodtSecs(long todtSecs) {
	// this.maxSecs = todtSecs;
	// }

	/**
	 * @param maxAmount
	 *            the maxAmount to set
	 */
	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	}

	/**
	 * An alternative setter for parameters given as a String
	 * 
	 * @param maxAmountStr
	 */
	public void setMaxAmount(String maxAmountStr) {
		if (maxAmountStr != null && maxAmountStr.length() > 0)
			maxAmount = Double.parseDouble(maxAmountStr);

	}

	/**
	 * @param minAmount
	 *            the minAmount to set
	 */
	public void setMinAmount(double minAmount) {
		this.minAmount = minAmount;
	}

	/**
	 * An alternative setter for parameters given as a String
	 * 
	 * @param minAmountStr
	 */
	public void setMinAmount(String minAmountStr) {
		if (minAmountStr != null && minAmountStr.length() > 0)
			minAmount = Double.parseDouble(minAmountStr);

	}

	/**
	 * @param parentAccount
	 *            the parentAccount to set
	 */
	public void setParentAccount(String parentAccount) {
		this.parentAccount = parentAccount;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * An alternative setter for parameters given as a String
	 * 
	 * @param todtSecsStr
	 */
	// public void setTodtSecs(String todtSecsStr) {
	// if (todtSecsStr != null && todtSecsStr.length() > 0)
	// maxSecs = Long.parseLong(todtSecsStr);
	// }

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (String ac : accountList)
			b.append("Account: " + ac);
		b.append(" Start date: " + new Date(getMinSecs()));
		b.append(" End date: " + new Date(getMaxSecs()));
		b.append(" Start row: " + getFirstResult());
		b.append(" Max row: " + getMaxResult());
		b.append(" Minimum amount " + minAmount);
		b.append(" Maximum amount: " + maxAmount);
		return b.toString();

	}

}
