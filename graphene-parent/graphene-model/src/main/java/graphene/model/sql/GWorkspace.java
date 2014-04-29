package graphene.model.sql;

import java.io.Serializable;
import javax.annotation.Generated;

/**
 * GWorkspace is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class GWorkspace implements Serializable {

    private String creatorUsername;

    private String json;

    private String title;

    private Integer workspaceId;

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Integer workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String toString() {
         return "creatorUsername = " + creatorUsername + ", json = " + json + ", title = " + title + ", workspaceId = " + workspaceId;
    }

}

