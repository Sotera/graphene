package graphene.model.sql;

import java.io.Serializable;
import javax.annotation.Generated;

/**
 * DynamicClusterDataview100 is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class DynamicClusterDataview100 implements Serializable {

    private String clusterid;

    private String contextid;

    private String entityid;

    private String globalclusterid;

    private Integer hierarchylevel;

    private java.sql.Timestamp modifiedDate;

    private String parentid;

    private String rootid;

    public String getClusterid() {
        return clusterid;
    }

    public void setClusterid(String clusterid) {
        this.clusterid = clusterid;
    }

    public String getContextid() {
        return contextid;
    }

    public void setContextid(String contextid) {
        this.contextid = contextid;
    }

    public String getEntityid() {
        return entityid;
    }

    public void setEntityid(String entityid) {
        this.entityid = entityid;
    }

    public String getGlobalclusterid() {
        return globalclusterid;
    }

    public void setGlobalclusterid(String globalclusterid) {
        this.globalclusterid = globalclusterid;
    }

    public Integer getHierarchylevel() {
        return hierarchylevel;
    }

    public void setHierarchylevel(Integer hierarchylevel) {
        this.hierarchylevel = hierarchylevel;
    }

    public java.sql.Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(java.sql.Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getRootid() {
        return rootid;
    }

    public void setRootid(String rootid) {
        this.rootid = rootid;
    }

    public String toString() {
         return "clusterid = " + clusterid + ", contextid = " + contextid + ", entityid = " + entityid + ", globalclusterid = " + globalclusterid + ", hierarchylevel = " + hierarchylevel + ", modifiedDate = " + modifiedDate + ", parentid = " + parentid + ", rootid = " + rootid;
    }

}

