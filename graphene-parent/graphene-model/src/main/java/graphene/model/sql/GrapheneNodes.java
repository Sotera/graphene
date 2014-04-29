package graphene.model.sql;

import java.io.Serializable;
import javax.annotation.Generated;

/**
 * GrapheneNodes is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class GrapheneNodes implements Serializable {

    private String customerNumber1;

    private String customerSourceTable;

    private Long nodeId;

    public String getCustomerNumber1() {
        return customerNumber1;
    }

    public void setCustomerNumber1(String customerNumber1) {
        this.customerNumber1 = customerNumber1;
    }

    public String getCustomerSourceTable() {
        return customerSourceTable;
    }

    public void setCustomerSourceTable(String customerSourceTable) {
        this.customerSourceTable = customerSourceTable;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public String toString() {
         return "customerNumber1 = " + customerNumber1 + ", customerSourceTable = " + customerSourceTable + ", nodeId = " + nodeId;
    }

}

