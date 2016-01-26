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
package graphene.export.anb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Chart {
	
	private Boolean attrIdReferenceLinking;
	private Boolean attrRigorous;
	private Boolean attrUseLocalTimeZone;
	private Font font;
	private AttributeClassCollection attributeClassCollection;
	private LinkTypeCollection linkTypeCollection;
	private CurrentStyleCollection currentStyleCollection;
	private ChartItemCollection chartItemCollection;
	
	//------------------------------------------------------------------------------------------------------------------
    
    public Chart() {}
    
	//------------------------------------------------------------------------------------------------------------------
	
	@XmlAttribute
    public void setAttrIdReferenceLinking(Boolean attrIdReferenceLinking) {
		this.attrIdReferenceLinking = attrIdReferenceLinking;
	}
	
	//------------------------------------------------------------------------------------------------------------------
	
	public Boolean getAttrIdReferenceLinking() {
		return this.attrIdReferenceLinking;
	}
	
	//------------------------------------------------------------------------------------------------------------------

	@XmlAttribute
	public void setAttrRigorous(Boolean attrRigorous) {
		this.attrRigorous = attrRigorous;
	}
	
	//------------------------------------------------------------------------------------------------------------------

	public Boolean getAttrRigorous() {
		return this.attrRigorous;
	}

	//------------------------------------------------------------------------------------------------------------------

	@XmlAttribute
	public void setAttrUseLocalTimeZone(Boolean attrUseLocalTimeZone) {
		this.attrUseLocalTimeZone = attrUseLocalTimeZone;
	}

	//------------------------------------------------------------------------------------------------------------------

	public Boolean getAttrUseLocalTimeZone() {
		return this.attrUseLocalTimeZone;
	}

	//------------------------------------------------------------------------------------------------------------------

	@XmlElement
	public void setFont(Font font) {
		this.font = font;
	}
	
	//------------------------------------------------------------------------------------------------------------------

	public Font getFont() {
		return this.font;
	}

	//------------------------------------------------------------------------------------------------------------------

	@XmlElement
	public void setAttributeClassCollection(AttributeClassCollection attributeClassCollection) {
		this.attributeClassCollection = attributeClassCollection;
	}
	
	//------------------------------------------------------------------------------------------------------------------

	public AttributeClassCollection getAttributeClassCollection() {
		return this.attributeClassCollection;
	}

	//------------------------------------------------------------------------------------------------------------------

	@XmlElement
	public void setLinkTypeCollection(LinkTypeCollection linkTypeCollection) {
		this.linkTypeCollection = linkTypeCollection;
	}
	
	//------------------------------------------------------------------------------------------------------------------

	public LinkTypeCollection getLinkTypeCollection() {
		return this.linkTypeCollection;
	}

	//------------------------------------------------------------------------------------------------------------------

	@XmlElement
	public void setCurrentStyleCollection(CurrentStyleCollection currentStyleCollection) {
		this.currentStyleCollection = currentStyleCollection;
	}
	
	//------------------------------------------------------------------------------------------------------------------

	public CurrentStyleCollection getCurrentStyleCollection() {
		return this.currentStyleCollection;
	}

	//------------------------------------------------------------------------------------------------------------------

	@XmlElement
	public void setChartItemCollection(ChartItemCollection chartItemCollection) {
		this.chartItemCollection = chartItemCollection;
	}
	
	//------------------------------------------------------------------------------------------------------------------

	public ChartItemCollection getChartItemCollection() {
		return this.chartItemCollection;
	}
}
