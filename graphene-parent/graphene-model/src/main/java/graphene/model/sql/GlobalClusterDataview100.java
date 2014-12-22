package graphene.model.sql;

import java.io.Serializable;

import javax.annotation.Generated;

/**
 * GlobalClusterDataview100 is a Querydsl bean type
 */
@Generated("com.mysema.query.codegen.BeanSerializer")
public class GlobalClusterDataview100 implements Serializable {

    private String clusterid;

    private String entityid;

    private Integer hierarchylevel;

    private String parentid;

    private String rootid;

    public String getClusterid() {
        return clusterid;
    }

    public void setClusterid(String clusterid) {
        this.clusterid = clusterid;
    }

    public String getEntityid() {
        return entityid;
    }

    public void setEntityid(String entityid) {
        this.entityid = entityid;
    }

    public Integer getHierarchylevel() {
        return hierarchylevel;
    }

    public void setHierarchylevel(Integer hierarchylevel) {
        this.hierarchylevel = hierarchylevel;
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
         return "clusterid = " + clusterid + ", entityid = " + entityid + ", hierarchylevel = " + hierarchylevel + ", parentid = " + parentid + ", rootid = " + rootid;
    }

}

