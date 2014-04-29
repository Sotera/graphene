package graphene.model.datasourcedescriptors;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;


/**
 * Encapsulates information about a particular data source such as 
 * an institution. 
 * It will typically be populated either from a set of hard-coded attributes
 * or from a table.
 * Hierarchy is: 
 * @author PWG for DARPA
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DataSource {

	public String id;
	public String name;
	public String friendlyName;
	List<DataSet> dataSets = new ArrayList<DataSet>();
	Properties properties = new Properties();
	
	public void addDataSet(DataSet d)
	{
		this.dataSets.add(d);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFriendlyName() {
		return friendlyName;
	}
	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}
	public List<DataSet> getDataSets() {
		return dataSets;
	}
	public void setDataSets(List<DataSet> list) {
		this.dataSets = list;
	}
	public DataSet getSet(String id)
	{
		for (DataSet d:dataSets)
			if (d.getName().equals(id))
				return d;
		return null;
	}
	/**
	 * Adds a named property to the object.
	 * Will depend on implementation  
	 * @param name String name
	 * @param value String value
	 */
	public void addProperty(String name, String value)
	{
		properties.setProperty(name, value);
		
	}
	public Properties getProperties() {
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
