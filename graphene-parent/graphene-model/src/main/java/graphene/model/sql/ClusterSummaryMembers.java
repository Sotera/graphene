package graphene.model.sql;

import java.io.Serializable;

import javax.annotation.Generated;

/**
 * ClusterSummaryMembers is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class ClusterSummaryMembers implements Serializable {

    private String entityId;

    private String summaryId;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(String summaryId) {
        this.summaryId = summaryId;
    }

    public String toString() {
         return "entityId = " + entityId + ", summaryId = " + summaryId;
    }

}

