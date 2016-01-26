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

public class Font
{
    private Boolean attrBold;
    private String attrFaceName;
    private Integer attrPointSize;
    private String attrBackColour;
    
    //------------------------------------------------------------------------------------------------------------------
    
    public Font() {}
    
    //------------------------------------------------------------------------------------------------------------------

  	@XmlAttribute
    public void setAttrBold(Boolean attrBold) {
  		this.attrBold = attrBold;
  	}
  	
  	//------------------------------------------------------------------------------------------------------------------

  	public Boolean getAttrBold() {
  		return this.attrBold;
  	}
  	
  //------------------------------------------------------------------------------------------------------------------

  	@XmlAttribute
    public void setAttrFaceName(String attrFaceName) {
  		this.attrFaceName = attrFaceName;
  	}
  	
  	//------------------------------------------------------------------------------------------------------------------

  	public String getAttrFaceName() {
  		return this.attrFaceName;
  	}
  	
  //------------------------------------------------------------------------------------------------------------------

  	@XmlAttribute
    public void setAttrPointSize(Integer attrPointSize) {
  		this.attrPointSize = attrPointSize;
  	}
  	
  	//------------------------------------------------------------------------------------------------------------------

  	public Integer getAttrPointSize() {
  		return this.attrPointSize;
  	}
  	
  //------------------------------------------------------------------------------------------------------------------

  	@XmlAttribute
    public void setAttrBackColour(String attrBackColour) {
  		this.attrBackColour = attrBackColour;
  	}
  	
  	//------------------------------------------------------------------------------------------------------------------

  	public String getAttrBackColour() {
  		return this.attrBackColour;
  	}
}
