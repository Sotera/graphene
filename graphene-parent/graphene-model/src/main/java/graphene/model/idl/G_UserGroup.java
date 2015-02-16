/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package graphene.model.idl;  
@SuppressWarnings("all")
/** A record indicating a user belongs to a group */
@org.apache.avro.specific.AvroGenerated
public class G_UserGroup extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"G_UserGroup\",\"namespace\":\"graphene.model.idl\",\"doc\":\"A record indicating a user belongs to a group\",\"fields\":[{\"name\":\"id\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"default\":null},{\"name\":\"groupId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"default\":null},{\"name\":\"userId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"default\":null},{\"name\":\"modified\",\"type\":\"long\",\"doc\":\"usergroups's modified datetime\",\"default\":0}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
   private java.lang.String id;
   private java.lang.String groupId;
   private java.lang.String userId;
  /** usergroups's modified datetime */
   private long modified;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public G_UserGroup() {}

  /**
   * All-args constructor.
   */
  public G_UserGroup(java.lang.String id, java.lang.String groupId, java.lang.String userId, java.lang.Long modified) {
    this.id = id;
    this.groupId = groupId;
    this.userId = userId;
    this.modified = modified;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id;
    case 1: return groupId;
    case 2: return userId;
    case 3: return modified;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id = (java.lang.String)value$; break;
    case 1: groupId = (java.lang.String)value$; break;
    case 2: userId = (java.lang.String)value$; break;
    case 3: modified = (java.lang.Long)value$; break;
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
   * Gets the value of the 'groupId' field.
   */
  public java.lang.String getGroupId() {
    return groupId;
  }

  /**
   * Sets the value of the 'groupId' field.
   * @param value the value to set.
   */
  public void setGroupId(java.lang.String value) {
    this.groupId = value;
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
   * usergroups's modified datetime   */
  public java.lang.Long getModified() {
    return modified;
  }

  /**
   * Sets the value of the 'modified' field.
   * usergroups's modified datetime   * @param value the value to set.
   */
  public void setModified(java.lang.Long value) {
    this.modified = value;
  }

  /** Creates a new G_UserGroup RecordBuilder */
  public static graphene.model.idl.G_UserGroup.Builder newBuilder() {
    return new graphene.model.idl.G_UserGroup.Builder();
  }
  
  /** Creates a new G_UserGroup RecordBuilder by copying an existing Builder */
  public static graphene.model.idl.G_UserGroup.Builder newBuilder(graphene.model.idl.G_UserGroup.Builder other) {
    return new graphene.model.idl.G_UserGroup.Builder(other);
  }
  
  /** Creates a new G_UserGroup RecordBuilder by copying an existing G_UserGroup instance */
  public static graphene.model.idl.G_UserGroup.Builder newBuilder(graphene.model.idl.G_UserGroup other) {
    return new graphene.model.idl.G_UserGroup.Builder(other);
  }
  
  /**
   * RecordBuilder for G_UserGroup instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<G_UserGroup>
    implements org.apache.avro.data.RecordBuilder<G_UserGroup> {

    private java.lang.String id;
    private java.lang.String groupId;
    private java.lang.String userId;
    private long modified;

    /** Creates a new Builder */
    private Builder() {
      super(graphene.model.idl.G_UserGroup.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(graphene.model.idl.G_UserGroup.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.groupId)) {
        this.groupId = data().deepCopy(fields()[1].schema(), other.groupId);
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
    }
    
    /** Creates a Builder by copying an existing G_UserGroup instance */
    private Builder(graphene.model.idl.G_UserGroup other) {
            super(graphene.model.idl.G_UserGroup.SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.groupId)) {
        this.groupId = data().deepCopy(fields()[1].schema(), other.groupId);
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
    }

    /** Gets the value of the 'id' field */
    public java.lang.String getId() {
      return id;
    }
    
    /** Sets the value of the 'id' field */
    public graphene.model.idl.G_UserGroup.Builder setId(java.lang.String value) {
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
    public graphene.model.idl.G_UserGroup.Builder clearId() {
      id = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'groupId' field */
    public java.lang.String getGroupId() {
      return groupId;
    }
    
    /** Sets the value of the 'groupId' field */
    public graphene.model.idl.G_UserGroup.Builder setGroupId(java.lang.String value) {
      validate(fields()[1], value);
      this.groupId = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'groupId' field has been set */
    public boolean hasGroupId() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'groupId' field */
    public graphene.model.idl.G_UserGroup.Builder clearGroupId() {
      groupId = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'userId' field */
    public java.lang.String getUserId() {
      return userId;
    }
    
    /** Sets the value of the 'userId' field */
    public graphene.model.idl.G_UserGroup.Builder setUserId(java.lang.String value) {
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
    public graphene.model.idl.G_UserGroup.Builder clearUserId() {
      userId = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'modified' field */
    public java.lang.Long getModified() {
      return modified;
    }
    
    /** Sets the value of the 'modified' field */
    public graphene.model.idl.G_UserGroup.Builder setModified(long value) {
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
    public graphene.model.idl.G_UserGroup.Builder clearModified() {
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    public G_UserGroup build() {
      try {
        G_UserGroup record = new G_UserGroup();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.String) defaultValue(fields()[0]);
        record.groupId = fieldSetFlags()[1] ? this.groupId : (java.lang.String) defaultValue(fields()[1]);
        record.userId = fieldSetFlags()[2] ? this.userId : (java.lang.String) defaultValue(fields()[2]);
        record.modified = fieldSetFlags()[3] ? this.modified : (java.lang.Long) defaultValue(fields()[3]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
