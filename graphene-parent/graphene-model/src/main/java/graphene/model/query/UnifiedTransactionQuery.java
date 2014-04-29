package graphene.model.query;

import graphene.model.idl.G_SearchTuple;

import java.util.ArrayList;
import java.util.List;

public class UnifiedTransactionQuery extends BasicQuery implements
		IntersectionQuery {
	private double minAmount, maxAmount;

	private List<G_SearchTuple<String>> from = new ArrayList<G_SearchTuple<String>>(
			2);
	private List<G_SearchTuple<String>> to = new ArrayList<G_SearchTuple<String>>(
			2);
	private List<G_SearchTuple<String>> payloadKeywords = new ArrayList<G_SearchTuple<String>>(
			1);

	private boolean intersectionOnly = false;

	public boolean isIntersectionOnly() {
		return intersectionOnly;
	}

	public void setIntersectionOnly(boolean intersectionOnly) {
		this.intersectionOnly = intersectionOnly;
	}

	public double getMaxAmount() {
		return maxAmount;
	}

	public double getMinAmount() {
		return minAmount;
	}

	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	}

	public void setMinAmount(double minAmount) {
		this.minAmount = minAmount;
	}

	public List<G_SearchTuple<String>> getFrom() {
		return from;
	}

	public void setFrom(List<G_SearchTuple<String>> from) {
		this.from = from;
	}

	public List<G_SearchTuple<String>> getTo() {
		return to;
	}

	public void setTo(List<G_SearchTuple<String>> to) {
		this.to = to;
	}

	public List<G_SearchTuple<String>> getPayloadKeywords() {
		return payloadKeywords;
	}

	public void setPayloadKeywords(List<G_SearchTuple<String>> payloadKeywords) {
		this.payloadKeywords = payloadKeywords;
	}

}
