/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
