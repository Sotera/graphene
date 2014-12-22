package graphene.model.sql;

import java.io.Serializable;

import javax.annotation.Generated;

/**
 * FINENTITYDaily120 is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class FINENTITYDaily120 implements Serializable {

    private Double balance;

    private String entityId;

    private Double inboundAmount;

    private Integer inboundDegree;

    private Double outboundAmount;

    private Integer outboundDegree;

    private java.sql.Timestamp periodDate;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Double getInboundAmount() {
        return inboundAmount;
    }

    public void setInboundAmount(Double inboundAmount) {
        this.inboundAmount = inboundAmount;
    }

    public Integer getInboundDegree() {
        return inboundDegree;
    }

    public void setInboundDegree(Integer inboundDegree) {
        this.inboundDegree = inboundDegree;
    }

    public Double getOutboundAmount() {
        return outboundAmount;
    }

    public void setOutboundAmount(Double outboundAmount) {
        this.outboundAmount = outboundAmount;
    }

    public Integer getOutboundDegree() {
        return outboundDegree;
    }

    public void setOutboundDegree(Integer outboundDegree) {
        this.outboundDegree = outboundDegree;
    }

    public java.sql.Timestamp getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(java.sql.Timestamp periodDate) {
        this.periodDate = periodDate;
    }

    public String toString() {
         return "balance = " + balance + ", entityId = " + entityId + ", inboundAmount = " + inboundAmount + ", inboundDegree = " + inboundDegree + ", outboundAmount = " + outboundAmount + ", outboundDegree = " + outboundDegree + ", periodDate = " + periodDate;
    }

}

