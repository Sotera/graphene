/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package graphene.model.idl;  
@SuppressWarnings("all")
/** A record indicating a user has access to a workspace */
@org.apache.avro.specific.AvroGenerated
public class G_UserWorkspace extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"G_UserWorkspace\",\"namespace\":\"graphene.model.idl\",\"doc\":\"A record indicating a user has access to a workspace\",\"fields\":[{\"name\":\"id\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"default\":null},{\"name\":\"workspaceId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"default\":null},{\"name\":\"userId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"default\":null},{\"name\":\"modified\",\"type\":\"long\",\"doc\":\"userworkspace's modified datetime\",\"default\":0},{\"name\":\"role\",\"type\":{\"type\":\"enum\",\"name\":\"G_UserSpaceRelationshipType\",\"symbols\":[\"CREATOR_OF\",\"EDITOR_OF\",\"REVIEWER_OF\"]}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
   private java.lang.String id;
   private java.lang.String workspaceId;
   private java.lang.String userId;
  /** userworkspace's modified datetime */
   private long modified;
   private graphene.model.idl.G_UserSpaceRelationshipType role;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public G_UserWorkspace() {}

  /**
   * All-args constructor.
   */
  public G_UserWorkspace(java.lang.String id, java.lang.String workspaceId, java.lang.String userId, java.lang.Long modified, graphene.model.idl.G_UserSpaceRelationshipType role) {
    this.id = id;
    this.workspaceId = workspaceId;
    this.userId = userId;
    this.modified = modified;
    this.role = role;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id;
    case 1: return workspaceId;
    case 2: return userId;
    case 3: return modified;
    case 4: return role;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id = (java.lang.String)value$; break;
    case 1: workspaceId = (java.lang.String)value$; break;
    case 2: userId = (java.lang.String)value$; break;
    case 3: modified = (java.lang.Long)value$; break;
    case 4: role = (graphene.model.idl.G_UserSpaceRelationshipType)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id' field.
   */
  public java.lang.String getId() {
    return id;
  }

  /**
   * Sets the value of the 'id' field.
   * @param value the value to set.
   */
  public void setId(java.lang.String value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'workspaceId' field.
   */
  public java.lang.String getWorkspaceId() {
    return workspaceId;
  }

  /**
   * Sets the value of the 'workspaceId' field.
   * @param value the value to set.
   */
  public void setWorkspaceId(java.lang.String value) {
    this.workspaceId = value;
  }

  /**
   * Gets the value of the 'userId' field.
   */
  public java.lang.String getUserId() {
    return userId;
  }

  /**
   * Sets the value of the 'userId' field.
   * @param value the value to set.
   */
  public void setUserId(java.lang.String value) {
    this.userId = value;
  }

  /**
   * Gets the value of the 'modified' field.
   * userworkspace's modified datetime   */
  public java.lang.Long getModified() {
    return modified;
  }

  /**
   * Sets the value of the 'modified' field.
   * userworkspace's modified datetime   * @param value the value to set.
   */
  public void setModified(java.lang.Long value) {
    this.modified = value;
  }

  /**
   * Gets the value of the 'role' field.
   */
  public graphene.model.idl.G_UserSpaceRelationshipType getRole() {
    return role;
  }

  /**
   * Sets the value of the 'role' field.
   * @param value the value to set.
   */
  public void setRole(graphene.model.idl.G_UserSpaceRelationshipType value) {
    this.role = value;
  }

  /** Creates a new G_UserWorkspace RecordBuilder */
  public static graphene.model.idl.G_UserWorkspace.Builder newBuilder() {
    return new graphene.model.idl.G_UserWorkspace.Builder();
  }
  
  /** Creates a new G_UserWorkspace RecordBuilder by copying an existing Builder */
  public static graphene.model.idl.G_UserWorkspace.Builder newBuilder(graphene.model.idl.G_UserWorkspace.Builder other) {
    return new graphene.model.idl.G_UserWorkspace.Builder(other);
  }
  
  /** Creates a new G_UserWorkspace RecordBuilder by copying an existing G_UserWorkspace instance */
  public static graphene.model.idl.G_UserWorkspace.Builder newBuilder(graphene.model.idl.G_UserWorkspace other) {
    return new graphene.model.idl.G_UserWorkspace.Builder(other);
  }
  
  /**
   * RecordBuilder for G_UserWorkspace instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<G_UserWorkspace>
    implements org.apache.avro.data.RecordBuilder<G_UserWorkspace> {

    private java.lang.String id;
    private java.lang.String workspaceId;
    private java.lang.String userId;
    private long modified;
    private graphene.model.idl.G_UserSpaceRelationshipType role;

    /** Creates a new Builder */
    private Builder() {
      super(graphene.model.idl.G_UserWorkspace.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(graphene.model.idl.G_UserWorkspace.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.workspaceId)) {
        this.workspaceId = data().deepCopy(fields()[1].schema(), other.workspaceId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.userId)) {
        this.userId = data().deepCopy(fields()[2].schema(), other.userId);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.modified)) {
        this.modified = data().deepCopy(fields()[3].schema(), other.modified);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.role)) {
        this.role = data().deepCopy(fields()[4].schema(), other.role);
        fieldSetFlags()[4] = true;
      }
    }
    
    /** Creates a Builder by copying an existing G_UserWorkspace instance */
    private Builder(graphene.model.idl.G_UserWorkspace other) {
            super(graphene.model.idl.G_UserWorkspace.SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.workspaceId)) {
        this.workspaceId = data().deepCopy(fields()[1].schema(), other.workspaceId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.userId)) {
        this.userId = data().deepCopy(fields()[2].schema(), other.userId);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.modified)) {
        this.modified = data().deepCopy(fields()[3].schema(), other.modified);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.role)) {
        this.role = data().deepCopy(fields()[4].schema(), other.role);
        fieldSetFlags()[4] = true;
      }
    }

    /** Gets the value of the 'id' field */
    public java.lang.String getId() {
      return id;
    }
    
    /** Sets the value of the 'id' field */
    public graphene.model.idl.G_UserWorkspace.Builder setId(java.lang.String value) {
      validate(fields()[0], value);
      this.id = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'id' field has been set */
    public boolean hasId() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'id' field */
    public graphene.model.idl.G_UserWorkspace.Builder clearId() {
      id = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'workspaceId' field */
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }
    
    /** Sets the value of the 'workspaceId' field */
    public graphene.model.idl.G_UserWorkspace.Builder setWorkspaceId(java.lang.String value) {
      validate(fields()[1], value);
      this.workspaceId = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'workspaceId' field has been set */
    public boolean hasWorkspaceId() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'workspaceId' field */
    public graphene.model.idl.G_UserWorkspace.Builder clearWorkspaceId() {
      workspaceId = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'userId' field */
    public java.lang.String getUserId() {
      return userId;
    }
    
    /** Sets the value of the 'userId' field */
    public graphene.model.idl.G_UserWorkspace.Builder setUserId(java.lang.String value) {
      validate(fields()[2], value);
      this.userId = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'userId' field has been set */
    public boolean hasUserId() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'userId' field */
    public graphene.model.idl.G_UserWorkspace.Builder clearUserId() {
      userId = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'modified' field */
    public java.lang.Long getModified() {
      return modified;
    }
    
    /** Sets the value of the 'modified' field */
    public graphene.model.idl.G_UserWorkspace.Builder setModified(long value) {
      validate(fields()[3], value);
      this.modified = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'modified' field has been set */
    public boolean hasModified() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'modified' field */
    public graphene.model.idl.G_UserWorkspace.Builder clearModified() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'role' field */
    public graphene.model.idl.G_UserSpaceRelationshipType getRole() {
      return role;
    }
    
    /** Sets the value of the 'role' field */
    public graphene.model.idl.G_UserWorkspace.Builder setRole(graphene.model.idl.G_UserSpaceRelationshipType value) {
      validate(fields()[4], value);
      this.role = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'role' field has been set */
    public boolean hasRole() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'role' field */
    public graphene.model.idl.G_UserWorkspace.Builder clearRole() {
      role = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    @Override
    public G_UserWorkspace build() {
      try {
        G_UserWorkspace record = new G_UserWorkspace();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.String) defaultValue(fields()[0]);
        record.workspaceId = fieldSetFlags()[1] ? this.workspaceId : (java.lang.String) defaultValue(fields()[1]);
        record.userId = fieldSetFlags()[2] ? this.userId : (java.lang.String) defaultValue(fields()[2]);
        record.modified = fieldSetFlags()[3] ? this.modified : (java.lang.Long) defaultValue(fields()[3]);
        record.role = fieldSetFlags()[4] ? this.role : (graphene.model.idl.G_UserSpaceRelationshipType) defaultValue(fields()[4]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
