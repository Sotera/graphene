package graphene.export.graphml;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GraphDataXML {
	private String key;
	private String value;
	
	//------------------------------------------------------------------------------------------------------------------
    
    public GraphDataXML() {}
    
	//------------------------------------------------------------------------------------------------------------------

	@XmlAttribute
    public void setkey(String key) {
		this.key = key;
	}
	
	//------------------------------------------------------------------------------------------------------------------

	public String getkey() {
		return this.key;
	}
	
	//------------------------------------------------------------------------------------------------------------------

	@XmlAnyElement(JAXBXMLHandler.class)
    public void setvalue(String value) {
		this.value = value;
	}
	
	//------------------------------------------------------------------------------------------------------------------

	public String getvalue() {
		return this.value;
	}
	
	//------------------------------------------------------------------------------------------------------------------
	
}