package graphene.model.datasourcedescriptors;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Encapsulates information about a particular real or virtual table such as a
 * list of entities or a list of transactions. <BR/>
 * Will generally be stored as one of a set of DataSets for a particular
 * DataSource. <BR/>
 * Will contain a list of DataSetFields, each of which generally corresponds to
 * a column in the table.
 * 
 * @author PWG for DARPA, modified by djue
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DataSet {
	private List<DataSetField> fields = new ArrayList<DataSetField>();
	private boolean isEntity;
	private boolean isTransaction;
	private String name;

	public void addField(DataSetField f) {
		fields.add(f);
	}

	public DataSetField findField(String name) {
		for (DataSetField d : fields) {
			if (d.name.equals(name))
				return d;
		}
		return null;
	}

	public List<DataSetField> getFields() {
		return fields;
	}

	public String getName() {
		return name;
	}

	public boolean isEntity() {
		return isEntity;
	}

	public boolean isTransaction() {
		return isTransaction;
	}

	public void setEntity(boolean isEntity) {
		this.isEntity = isEntity;
	}

	public void setFields(List<DataSetField> fields) {
		this.fields = fields;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTransaction(boolean isTransaction) {
		this.isTransaction = isTransaction;
	}
}
