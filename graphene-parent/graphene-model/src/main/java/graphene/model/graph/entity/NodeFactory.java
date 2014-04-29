package graphene.model.graph.entity;

import graphene.model.graph.DirectedNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeFactory {

	private int hiNode = 0;

	static Logger logger = LoggerFactory.getLogger(NodeFactory.class);

	/**
	 * @param type
	 *            char one of C, A or I
	 * @param value
	 *            the value - customer number, account number, or identifier
	 * @param degree
	 *            how far from the center of the graph.
	 * 
	 */
	public EntityRefNode makeNode(char type, String value, int degree) {
		EntityRefNode node = new EntityRefNode(type, value,
				Integer.toString(++hiNode));
		node.setDegree(degree);
		return node;
	}

	/**
	 * @param type
	 *            char one of C, A or I
	 * @param value
	 *            the value - customer number, account number, or identifier
	 * @param degree
	 *            how far from the center of the graph.
	 * 
	 */
	public DirectedNode makeDirectedNode(String value, int degree) {
		DirectedNode node = new DirectedNode(value, Integer.toString(++hiNode));
		node.setDegree(degree);
		node.setValue(value);
		node.setLabel(value);
		node.setKey(value);
		node.setEntityType('N'); // needed for scan
		return node;
	}

	/**
	 * Make a Node from an Identifier
	 * 
	 * @param ident
	 *            Identifier
	 */

	public EntityRefNode makeIdentifierNode(String value, int degree) {
		EntityRefNode node = makeNode(EntityRefNode.ENTITY_IDENTIFIER, value,
				degree);
		if (value != null && value.length() > 0) {
			node.setLabel(value);
			node.setValue(value);
		}
		node.setLeaf(false); // we never create leaf identifier nodes
		node.setNbrLinks(0);
		return node;
	}

	public EntityRefNode makePlaceholderNode(String value, int count, int degree) {
		EntityRefNode node = makeNode(EntityRefNode.ENTITY_IDENTIFIER, value,
				degree);
		node.setLabel(value + " (" + count + "links)");
		node.setLeaf(true);
		node.setPlaceholder(true);
		node.addData("Placeholder", "Represents multiple nodes");
		return node;
	}

	public EntityRefNode makeAccountNode(String acno, int degree) {
		EntityRefNode node = makeNode(EntityRefNode.ENTITY_ACCOUNT, acno,
				degree);
		node.setValueType("account");
		node.setLabel(acno);
		node.setValue(acno);
		return node;
	}

	public EntityRefNode makeCustomerNode(String customerNumber, int degree) {
		EntityRefNode node = makeNode(EntityRefNode.ENTITY_CUSTOMER,
				customerNumber, degree);
		node.setCustomerNumber(customerNumber);
		node.setValue(customerNumber);
		node.setValueType("customer");
		return node;
	}

	public void reset() {
		hiNode = 0;
	}

}
