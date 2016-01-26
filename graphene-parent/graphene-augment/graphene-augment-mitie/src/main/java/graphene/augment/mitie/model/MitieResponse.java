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

package graphene.augment.mitie.model;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MitieResponse {
	@JsonProperty("entities")
	private List<MitieEntity> entities;
	@JsonProperty("html")
	private String html;

	@Override
	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}

	/**
	 * @return the entities
	 */
	@JsonProperty("entities")
	public final List<MitieEntity> getEntities() {
		return entities;
	}

	/**
	 * @return the html
	 */
	@JsonProperty("html")
	public final String getHtml() {
		return html;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	/**
	 * @param entities
	 *            the entities to set
	 */
	@JsonProperty("entities")
	public final void setEntities(List<MitieEntity> entities) {
		this.entities = entities;
	}

	/**
	 * @param html
	 *            the html to set
	 */
	@JsonProperty("html")
	public final void setHtml(String html) {
		this.html = html;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
