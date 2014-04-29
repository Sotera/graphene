package graphene.model.graphserver;

import graphene.model.graph.DirectableObject;
import graphene.model.view.entities.IdProperty;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to store distinct pairs of ids that have events involving both of them
 * 
 * @author PWG
 * 
 */
public class TransactionDistinctAccountPair implements DirectableObject {

	private String src = "";
	private String dest = "";

	private Double total = 0D;
	private int nbr = 0;
	private List<IdProperty> edgeAttributeList = new ArrayList<IdProperty>();
	private List<IdProperty> srcAttributeList = new ArrayList<IdProperty>();
	private List<IdProperty> destAttributeList = new ArrayList<IdProperty>();
	static DecimalFormat fmt = new DecimalFormat("###,###,###,##0.00");

	// Note that src is the source *Account*. We store its *owner* in
	// the attribute list to enable pivoting and drill-down details
	public void setSrcEntityId(String id) {
		this.srcAttributeList.add(new IdProperty("entity ID", id));
	}

	public void setDestEntityId(String id) {
		this.destAttributeList.add(new IdProperty("entity ID", id));
	}

	public void setSrcEntityName(String name) {
		this.srcAttributeList.add(new IdProperty("entity Name", name));
	}

	public void setDestEntityName(String name) {
		this.destAttributeList.add(new IdProperty("entity Name", name));
	}

	public List<IdProperty> getEdgeAttributes() {
		return edgeAttributeList;
	}

	public List<IdProperty> getSrcAttributes() {
		return srcAttributeList;
	}

	public List<IdProperty> getDestAttributes() {
		return destAttributeList;
	}

	public TransactionDistinctAccountPair() {

	}

	public void setSrc(String src) {
		this.src = src;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public int getNbr() {
		return nbr;
	}

	public void setNbr(int nbr) {
		this.nbr = nbr;
	}

	@Override
	public String getSrc() {
		return src;
	}

	@Override
	public String getDest() {
		// TODO Auto-generated method stub
		return dest;
	}

	@Override
	public String getValue() {
		return fmt.format(total);
	}

	@Override
	public int getWeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addEdgeAttribute(IdProperty attr) {
		edgeAttributeList.add(attr);
	}

	public void addSrcAttribute(IdProperty attr) {
		srcAttributeList.add(attr);
	}

	public void addDestAttribute(IdProperty attr) {
		destAttributeList.add(attr);
	}

	/**
	 * This was changed so it overrides the equals(Object) instead of inheriting
	 * it as a covariant of equals(TransactionDistinctAccountPair). This was
	 * labeled by findbugs as scary. --djue
	 */
	public boolean equals(Object o) {
		if (o instanceof TransactionDistinctAccountPair) {
			TransactionDistinctAccountPair t = (TransactionDistinctAccountPair) o;
			if (!src.equals(t.getSrc()))
				return false;
			if (!dest.equals(t.getDest()))
				return false;
			return true;
		}
		return false;
	}

	public int hashCode() {
		return (src + dest).hashCode();
	}

}
