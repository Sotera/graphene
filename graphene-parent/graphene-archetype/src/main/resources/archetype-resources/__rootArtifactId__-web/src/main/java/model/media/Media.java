#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.model.media;

import graphene.util.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    "caption_text",
    "comments",
    "created_time",
    "id",
    "likes",
    "link",
    "location",
    "user_id",
    "username"
})
public class Media {

    @JsonProperty("caption_text")
    private String captionText;
    
    @JsonProperty("comments")
    private Comments comments;
    
    @JsonProperty("created_time")
    private Object createdTime;
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("likes")
    private Likes likes;
    
    @JsonProperty("link")
    private String link;
    
    @JsonProperty("location")
    private Location location;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("profile_picture")
    private String profilePicture;
    
    @JsonProperty("thumbnail")
    private String thumbnail;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The captionText
     */
    @JsonProperty("caption_text")
    public String getCaptionText() {
        return captionText;
    }

    /**
     * 
     * @param captionText
     *     The caption_text
     */
    @JsonProperty("caption_text")
    public void setCaptionText(String captionText) {
        this.captionText = captionText;
    }

    /**
     * 
     * @return
     *     The comments
     */
    @JsonProperty("comments")
    public Comments getComments() {
        return comments;
    }

    /**
     * 
     * @param comments
     *     The comments
     */
    @JsonProperty("comments")
    public void setComments(Comments comments) {
        this.comments = comments;
    }

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
     *     The likes
     */
    @JsonProperty("likes")
    public Likes getLikes() {
        return likes;
    }

    /**
     * 
     * @param likes
     *     The likes
     */
    @JsonProperty("likes")
    public void setLikes(Likes likes) {
        this.likes = likes;
    }

    /**
     * 
     * @return
     *     The link
     */
    @JsonProperty("link")
    public String getLink() {
        return link;
    }

    /**
     * 
     * @param link
     *     The link
     */
    @JsonProperty("link")
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * 
     * @return
     *     The location
     */
    @JsonProperty("location")
    public Location getLocation() {
        return location;
    }

    /**
     * 
     * @param location
     *     The location
     */
    @JsonProperty("location")
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * 
     * @return
     *     The userId
     */
    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    /**
     * 
     * @param userId
     *     The user_id
     */
    @JsonProperty("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
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

    private Set<Tuple<String, String>> getSpecialTokens(char delimiter, String text) {
    	Pattern MY_PATTERN = Pattern.compile(delimiter + "(${symbol_escape}${symbol_escape}w+|${symbol_escape}${symbol_escape}W+)");
    	Matcher mat = MY_PATTERN.matcher(text);
    	Set<Tuple<String, String>> l = new HashSet<Tuple<String, String>>();
    	while (mat.find()) {
    	  l.add(new Tuple<String, String>(delimiter + mat.group(1), mat.group(1)));
    	}
    	return l;
    }
    
    public Set<Tuple<String, String>> getAtsInCaption() {
    	return getSpecialTokens('@', this.getCaptionText());
    }
    
    public Set<Tuple<String, String>> getAtsInComments() {
    	String comments = "";
    	for (CommentData cd : this.getComments().getCommentsData()) {
    		comments += cd.getText() + " ";
    	}
    	return getSpecialTokens('@', comments);
    }
    
    public Set<Tuple<String, String>> getHashTagsInCaption() {
    	return getSpecialTokens('${symbol_pound}', this.getCaptionText());
    }
    
    public Set<Tuple<String, String>> getHashTagsInComments() {
    	String comments = "";
    	for (CommentData cd : this.getComments().getCommentsData()) {
    		comments += cd.getText() + " ";
    	}
    	return getSpecialTokens('${symbol_pound}', comments);
    }
    
    public Set<Tuple<String, String>> getAllAts() {
    	Set<Tuple<String, String>> allAts = new HashSet<Tuple<String, String>>();
    	if (allAts.addAll(getAtsInCaption())) {
    		allAts.addAll(getAtsInComments());
    	}
    	
    	return allAts;
    }
    
    public Set<Tuple<String, String>> getAllHashTags() {
    	Set<Tuple<String, String>> allHashTags = new HashSet<Tuple<String, String>>();
    	if (allHashTags.addAll(getHashTagsInCaption())) {
    		allHashTags.addAll(getHashTagsInComments());
    	}
    	
    	return allHashTags;
    }
}
