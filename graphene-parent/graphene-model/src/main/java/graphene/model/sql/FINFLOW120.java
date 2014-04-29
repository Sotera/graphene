package graphene.model.sql;

import java.io.Serializable;
import javax.annotation.Generated;

/**
 * FinFlow120 is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class FINFLOW120 implements Serializable {

    private Double amount;

    private java.sql.Timestamp firstTransaction;

    private String fromEntityId;

    private String fromEntityType;

    private java.sql.Timestamp lastTransaction;

    private String toEntityId;

    private String toEntityType;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public java.sql.Timestamp getFirstTransaction() {
        return firstTransaction;
    }

    public void setFirstTransaction(java.sql.Timestamp firstTransaction) {
        this.firstTransaction = firstTransaction;
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

    public java.sql.Timestamp getLastTransaction() {
        return lastTransaction;
    }

    public void setLastTransaction(java.sql.Timestamp lastTransaction) {
        this.lastTransaction = lastTransaction;
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
         return "amount = " + amount + ", firstTransaction = " + firstTransaction + ", fromEntityId = " + fromEntityId + ", fromEntityType = " + fromEntityType + ", lastTransaction = " + lastTransaction + ", toEntityId = " + toEntityId + ", toEntityType = " + toEntityType;
    }

}

