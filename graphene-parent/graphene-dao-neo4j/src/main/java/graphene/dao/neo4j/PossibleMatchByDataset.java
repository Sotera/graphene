package graphene.dao.neo4j;

import org.neo4j.kernel.impl.core.NodeProxy;

public class PossibleMatchByDataset {

	private String customerNumber, identifiers, accounts, datasetId;

	private String internalNodeId;

	private NodeProxy node;

	private int score;
	public String getAccounts() {
		return accounts;
	}
	public String getCustomerNumber() {
		return customerNumber;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public String getIdentifiers() {
		return identifiers;
	}

	public String getInternalNodeId() {
		return internalNodeId;
	}

	public NodeProxy getNode() {
		return node;
	}

	public int getScore() {
		return score;
	}

	public void setAccounts(String accounts) {
		this.accounts = accounts;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public void setIdentifiers(String identifiers) {
		this.identifiers = identifiers;
	}

	public void setInternalNodeId(String internalNodeId) {
		this.internalNodeId = internalNodeId;
	}

	public void setNode(NodeProxy n) {
		// TODO Auto-generated method stub
		this.node = n;
	}

	public void setScore(int score) {
		this.score = score;
	}

}
