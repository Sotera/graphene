package graphene.model.datasourcedescriptors;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DataSourceList {

	public List<DataSource> dataSources = new ArrayList<DataSource>();

	@XmlTransient
	static private Logger logger = LoggerFactory.getLogger(DataSourceList.class);

	public void addSource(final DataSource d) {
		dataSources.add(d);
	}

	public List<DataSource> getDataSources() {
		return dataSources;
	}

	public DataSetField getField(final String sourceId, final String setId, final String fieldName) {
		DataSetField f = null;

		final DataSource d = getSourceById(sourceId);
		if (d != null) {
			final DataSet s = d.getSet(setId);
			if (s != null) {
				f = s.findField(fieldName);
			}
		} else {
			logger.error("Could not find datasource with id " + sourceId);
		}
		if (f == null) {
			logger.error("Can not find field " + sourceId + " : " + setId + " : " + fieldName);
		}

		return f;
	}

	public DataSource getSourceById(final String id) {
		for (final DataSource s : dataSources) {
			if (s.id.equals(id)) {
				return s;
			}
		}
		return null;
	}
}
