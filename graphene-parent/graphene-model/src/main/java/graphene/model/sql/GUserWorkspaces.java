package graphene.model.sql;

import java.io.Serializable;
import javax.annotation.Generated;

/**
 * GUserWorkspaces is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class GUserWorkspaces implements Serializable {

    private String username;

    private Integer workspaceId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Integer workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String toString() {
         return "username = " + username + ", workspaceId = " + workspaceId;
    }

}

