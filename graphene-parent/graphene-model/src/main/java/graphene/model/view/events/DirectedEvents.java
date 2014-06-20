package graphene.model.view.events;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * TODO: look into replacing TransferRow with Object, so that customer specific
 * rows can be added, without having the core know about them.
 * 
 * TODO: remove unnecessary XML annotations
 * 
 * 
 * XXX: Replace with IDL generated classes --djue
 * 
 * @author not djue
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DirectedEvents {

	private boolean multiUnit = false;
	private long resultCount;

	private List<DirectedEventRow> rows = new Vector<DirectedEventRow>(3);

	public DirectedEvents() {
		// needed for JAXB
	}

	public void addRow(final DirectedEventRow row) {
		rows.add(row);
		resultCount = rows.size();
	}

	public void clearRows() {
		rows.clear();
		resultCount = 0;
	}

	public long getResultCount() {
		return resultCount;
	}

	public List<DirectedEventRow> getRows() {
		return rows;
	}

	public boolean isMultiUnit() {
		return multiUnit;
	}

	public void setMultiUnit(final boolean multiUnit) {
		this.multiUnit = multiUnit;
	}

	public void setResultCount(final long resultCount) {
		this.resultCount = resultCount;
	}

	public void setRows(final List<DirectedEventRow> rows) {
		this.rows = rows;
	}

}
