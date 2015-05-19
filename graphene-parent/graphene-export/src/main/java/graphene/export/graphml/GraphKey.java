package graphene.export.graphml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "attrType", "attrName", "for", "id" })
public class GraphKey {

	private String id;
	private String attrFor;
	private String attrName;
	private String attrType;
	
	public GraphKey(){}
	
	//------------------------------------------------------------------------------------------------------------------
	
	@XmlAttribute
    public void setid(String id) {
		this.id = id;
	}
	
	//------------------------------------------------------------------------------------------------------------------
	
	public String getid() {
		return this.id;
	}
	
	//------------------------------------------------------------------------------------------------------------------
	
	@XmlAttribute(name="for")
    public void setFor(String attrFor) {
		this.attrFor = attrFor;
	}
	
	//------------------------------------------------------------------------------------------------------------------
	
	public String getFor() {
		return this.attrFor;
	}
	
	//------------------------------------------------------------------------------------------------------------------
	
	@XmlAttribute(name="attr.name")
    public void setAttrName(String attrName) {
		this.attrName = attrName;
	}
	
	//------------------------------------------------------------------------------------------------------------------
	
	public String getAttrName() {
		return this.attrName;
	}
	
	//------------------------------------------------------------------------------------------------------------------
	
	@XmlAttribute(name="attr.type")
    public void setAttrType(String attrType) {
		this.attrType = attrType;
	}
	
	//------------------------------------------------------------------------------------------------------------------
	
	public String getAttrType() {
		return this.attrType;
	}
	
	//------------------------------------------------------------------------------------------------------------------
}
