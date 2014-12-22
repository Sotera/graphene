package graphene.model.sql;

import java.io.Serializable;

import javax.annotation.Generated;

/**
 * FinEntity120 is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class FINENTITY120 implements Serializable {

    private String entityId;

    private Integer inboundDegree;

    private Integer outboundDegree;

    private Integer uniqueInboundDegree;

    private Integer uniqueOutboundDegree;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Integer getInboundDegree() {
        return inboundDegree;
    }

    public void setInboundDegree(Integer inboundDegree) {
        this.inboundDegree = inboundDegree;
    }

    public Integer getOutboundDegree() {
        return outboundDegree;
    }

    public void setOutboundDegree(Integer outboundDegree) {
        this.outboundDegree = outboundDegree;
    }

    public Integer getUniqueInboundDegree() {
        return uniqueInboundDegree;
    }

    public void setUniqueInboundDegree(Integer uniqueInboundDegree) {
        this.uniqueInboundDegree = uniqueInboundDegree;
    }

    public Integer getUniqueOutboundDegree() {
        return uniqueOutboundDegree;
    }

    public void setUniqueOutboundDegree(Integer uniqueOutboundDegree) {
        this.uniqueOutboundDegree = uniqueOutboundDegree;
    }

    public String toString() {
         return "entityId = " + entityId + ", inboundDegree = " + inboundDegree + ", outboundDegree = " + outboundDegree + ", uniqueInboundDegree = " + uniqueInboundDegree + ", uniqueOutboundDegree = " + uniqueOutboundDegree;
    }

}

