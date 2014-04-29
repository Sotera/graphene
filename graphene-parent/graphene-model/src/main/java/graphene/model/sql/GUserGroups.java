package graphene.model.sql;

import java.io.Serializable;
import javax.annotation.Generated;

/**
 * GUserGroups is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class GUserGroups implements Serializable {

    private String groupname;

    private String username;

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String toString() {
         return "groupname = " + groupname + ", username = " + username;
    }

}

