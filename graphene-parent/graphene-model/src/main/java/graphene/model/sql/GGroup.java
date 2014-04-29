package graphene.model.sql;

import java.io.Serializable;
import javax.annotation.Generated;

/**
 * GGroup is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class GGroup implements Serializable {

    private String groupname;

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String toString() {
         return "groupname = " + groupname;
    }

}

