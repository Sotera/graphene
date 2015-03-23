package graphene.augment.mitie.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "text,tag" })
public class MitieEntity {
	@JsonProperty("tag")
	private String tag;
	@JsonProperty("text")
	private String text;

	@Override
	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}

	/**
	 * @return the tag
	 */
	@JsonProperty("tag")
	public final String getTag() {
		return tag;
	}

	/**
	 * @return the text
	 */
	@JsonProperty("text")
	public final String getText() {
		return text;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	@JsonProperty("tag")
	public final void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	@JsonProperty("text")
	public final void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
