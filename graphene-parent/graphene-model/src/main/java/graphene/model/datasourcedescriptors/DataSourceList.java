package graphene.model.datasourcedescriptors;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DataSourceList {

	public List<DataSource> dataSources = new ArrayList<DataSource>();

	@XmlTransient
	static private Logger logger = LoggerFactory
			.getLogger(DataSourceList.class);

	public void addSource(DataSource d) {
		dataSources.add(d);
	}

	public List<DataSource> getDataSources() {
		return dataSources;
	}

	public DataSource getSourceById(String id) {
		for (DataSource s : dataSources) {
			if (s.id.equals(id))
				return s;
		}
		return null;
	}

	public DataSetField getField(String sourceId, String setId, String fieldName) {
		DataSetField f = null;

		DataSource d = getSourceById(sourceId);
		if (d != null) {
			DataSet s = d.getSet(setId);
			if (s != null) {
				f = s.findField(fieldName);
			}
		}else{
			logger.error("Could not find datasource with id "+sourceId);
		}
		if (f == null) {
			logger.error("Can not find field " + sourceId + " : " + setId
					+ " : " + fieldName);
		}

		return f;
	}
}
