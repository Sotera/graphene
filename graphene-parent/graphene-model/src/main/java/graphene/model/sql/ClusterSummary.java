package graphene.model.sql;

import java.io.Serializable;
import javax.annotation.Generated;

/**
 * ClusterSummary is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class ClusterSummary implements Serializable {

    private String entityId;

    private String property;

    private Double stat;

    private String tag;

    private String type;

    private String value;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Double getStat() {
        return stat;
    }

    public void setStat(Double stat) {
        this.stat = stat;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
         return "entityId = " + entityId + ", property = " + property + ", stat = " + stat + ", tag = " + tag + ", type = " + type + ", value = " + value;
    }

}

