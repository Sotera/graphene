#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.model.media;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "created_time",
    "id",
    "text",
    "username"
})
public class CommentData {

    @JsonProperty("created_time")
    private Object createdTime;
    @JsonProperty("id")
    private String id;
    @JsonProperty("text")
    private String text;
    @JsonProperty("username")
    private String username;
    @JsonProperty("profile_picture")
    private String profilePicture;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The createdTime
     */
    @JsonProperty("created_time")
    public Object getCreatedTime() {
        return createdTime;
    }

    /**
     * 
     * @param createdTime
     *     The created_time
     */
    @JsonProperty("created_time")
    public void setCreatedTime(Object createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The text
     */
    @JsonProperty("text")
    public String getText() {
        return text;
    }

    /**
     * 
     * @param text
     *     The text
     */
    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 
     * @return
     *     The username
     */
    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    /**
     * 
     * @param username
     *     The username
     */
    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("profile_picture")
    public String getProfilePicture() {
		return profilePicture;
	}

    @JsonProperty("profile_picture")
    public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String getTextSample() {
    	String sample = this.text;
    	int maxLength = 15;
    	if (sample.length() > maxLength) {
    		sample = sample.substring(0, maxLength) + "...";
    	}
    	return sample;
    }
}
