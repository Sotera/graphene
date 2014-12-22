package graphene.model.sql;

import java.io.Serializable;

import javax.annotation.Generated;

/**
 * FINFLOWYearly120 is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class FINFLOWYearly120 implements Serializable {

    private Double amount;

    private String fromEntityId;

    private String fromEntityType;

    private java.sql.Timestamp periodDate;

    private String toEntityId;

    private String toEntityType;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getFromEntityId() {
        return fromEntityId;
    }

    public void setFromEntityId(String fromEntityId) {
        this.fromEntityId = fromEntityId;
    }

    public String getFromEntityType() {
        return fromEntityType;
    }

    public void setFromEntityType(String fromEntityType) {
        this.fromEntityType = fromEntityType;
    }

    public java.sql.Timestamp getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(java.sql.Timestamp periodDate) {
        this.periodDate = periodDate;
    }

    public String getToEntityId() {
        return toEntityId;
    }

    public void setToEntityId(String toEntityId) {
        this.toEntityId = toEntityId;
    }

    public String getToEntityType() {
        return toEntityType;
    }

    public void setToEntityType(String toEntityType) {
        this.toEntityType = toEntityType;
    }

    public String toString() {
         return "amount = " + amount + ", fromEntityId = " + fromEntityId + ", fromEntityType = " + fromEntityType + ", periodDate = " + periodDate + ", toEntityId = " + toEntityId + ", toEntityType = " + toEntityType;
    }

}

