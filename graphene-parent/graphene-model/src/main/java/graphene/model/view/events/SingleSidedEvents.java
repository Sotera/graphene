package graphene.model.view.events;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * 
 * @author pwg
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SingleSidedEvents {

	private boolean multiUnit = false;
	private long resultCount;

	private List<SingleSidedEventRow> rows = new Vector<SingleSidedEventRow>();

	public SingleSidedEvents() {
		// needed for JAXB
	}

	public void addRow(final SingleSidedEventRow row) {
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

	public List<SingleSidedEventRow> getRows() {
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

	public void setRows(final List<SingleSidedEventRow> rows) {
		this.rows = rows;
	}

}
