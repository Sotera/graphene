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

/**
 * Copyright (c) 2013-2014 Oculus Info Inc.
 * http://www.oculusinfo.com/
 *
 * Released under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package graphene.export.graphml;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "edgedefault", "id", "key", "data", "node", "edge" })
public class Graph {
	
	private String id;
	private String edgedefault;
	private Collection<GraphKey> graphAttrCollection;
	private Collection<GraphNode> graphNodeCollection;
	private Collection<GraphEdge> graphEdgeCollection;
	private Collection<GraphDataXML> graphDataCollection;
	
	//------------------------------------------------------------------------------------------------------------------
    
    public Graph() {}
    
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

	@XmlAttribute
	public void setedgedefault(String edgedefault) {
		this.edgedefault = edgedefault;
	}
	
	//------------------------------------------------------------------------------------------------------------------

	public String getedgedefault() {
		return this.edgedefault;
	}

	//------------------------------------------------------------------------------------------------------------------
	
	@XmlElement
	public void setkey(Collection<GraphKey> graphAttrCollection) {
		this.graphAttrCollection = graphAttrCollection;
	}

	//------------------------------------------------------------------------------------------------------------------

	public Collection<GraphKey> getkey() {
		return this.graphAttrCollection;
	}
	
	//------------------------------------------------------------------------------------------------------------------
	
	@XmlElement
	public void setdata(Collection<GraphDataXML> graphDataCollection) {
		this.graphDataCollection = graphDataCollection;
	}

	//------------------------------------------------------------------------------------------------------------------

	public Collection<GraphDataXML> getdata() {
		return this.graphDataCollection;
	}
	
	//------------------------------------------------------------------------------------------------------------------

	@XmlElement
	public void setnode(Collection<GraphNode> graphNodeCollection) {
		this.graphNodeCollection = graphNodeCollection;
	}

	//------------------------------------------------------------------------------------------------------------------

	public Collection<GraphNode> getnode() {
		return this.graphNodeCollection;
	}

	//------------------------------------------------------------------------------------------------------------------
	
	@XmlElement
	public void setedge(Collection<GraphEdge> graphEdgeCollection) {
		this.graphEdgeCollection = graphEdgeCollection;
	}

	//------------------------------------------------------------------------------------------------------------------

	public Collection<GraphEdge> getedge() {
		return this.graphEdgeCollection;
	}
	
	//------------------------------------------------------------------------------------------------------------------
}
