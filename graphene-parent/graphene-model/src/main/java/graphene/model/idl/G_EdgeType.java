/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package graphene.model.idl;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class G_EdgeType extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"G_EdgeType\",\"namespace\":\"graphene.model.idl\",\"fields\":[{\"name\":\"type\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"friendlyName\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"index\",\"type\":\"long\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
   private java.lang.String type;
   private java.lang.String name;
   private java.lang.String friendlyName;
   private long index;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public G_EdgeType() {}

  /**
   * All-args constructor.
   */
  public G_EdgeType(java.lang.String type, java.lang.String name, java.lang.String friendlyName, java.lang.Long index) {
    this.type = type;
    this.name = name;
    this.friendlyName = friendlyName;
    this.index = index;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return type;
    case 1: return name;
    case 2: return friendlyName;
    case 3: return index;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: type = (java.lang.String)value$; break;
    case 1: name = (java.lang.String)value$; break;
    case 2: friendlyName = (java.lang.String)value$; break;
    case 3: index = (java.lang.Long)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'type' field.
   */
  public java.lang.String getType() {
    return type;
  }

  /**
   * Sets the value of the 'type' field.
   * @param value the value to set.
   */
  public void setType(java.lang.String value) {
    this.type = value;
  }

  /**
   * Gets the value of the 'name' field.
   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * Sets the value of the 'name' field.
   * @param value the value to set.
   */
  public void setName(java.lang.String value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'friendlyName' field.
   */
  public java.lang.String getFriendlyName() {
    return friendlyName;
  }

  /**
   * Sets the value of the 'friendlyName' field.
   * @param value the value to set.
   */
  public void setFriendlyName(java.lang.String value) {
    this.friendlyName = value;
  }

  /**
   * Gets the value of the 'index' field.
   */
  public java.lang.Long getIndex() {
    return index;
  }

  /**
   * Sets the value of the 'index' field.
   * @param value the value to set.
   */
  public void setIndex(java.lang.Long value) {
    this.index = value;
  }

  /** Creates a new G_EdgeType RecordBuilder */
  public static graphene.model.idl.G_EdgeType.Builder newBuilder() {
    return new graphene.model.idl.G_EdgeType.Builder();
  }
  
  /** Creates a new G_EdgeType RecordBuilder by copying an existing Builder */
  public static graphene.model.idl.G_EdgeType.Builder newBuilder(graphene.model.idl.G_EdgeType.Builder other) {
    return new graphene.model.idl.G_EdgeType.Builder(other);
  }
  
  /** Creates a new G_EdgeType RecordBuilder by copying an existing G_EdgeType instance */
  public static graphene.model.idl.G_EdgeType.Builder newBuilder(graphene.model.idl.G_EdgeType other) {
    return new graphene.model.idl.G_EdgeType.Builder(other);
  }
  
  /**
   * RecordBuilder for G_EdgeType instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<G_EdgeType>
    implements org.apache.avro.data.RecordBuilder<G_EdgeType> {

    private java.lang.String type;
    private java.lang.String name;
    private java.lang.String friendlyName;
    private long index;

    /** Creates a new Builder */
    private Builder() {
      super(graphene.model.idl.G_EdgeType.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(graphene.model.idl.G_EdgeType.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.type)) {
        this.type = data().deepCopy(fields()[0].schema(), other.type);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.name)) {
        this.name = data().deepCopy(fields()[1].schema(), other.name);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.friendlyName)) {
        this.friendlyName = data().deepCopy(fields()[2].schema(), other.friendlyName);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.index)) {
        this.index = data().deepCopy(fields()[3].schema(), other.index);
        fieldSetFlags()[3] = true;
      }
    }
    
    /** Creates a Builder by copying an existing G_EdgeType instance */
    private Builder(graphene.model.idl.G_EdgeType other) {
            super(graphene.model.idl.G_EdgeType.SCHEMA$);
      if (isValidValue(fields()[0], other.type)) {
        this.type = data().deepCopy(fields()[0].schema(), other.type);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.name)) {
        this.name = data().deepCopy(fields()[1].schema(), other.name);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.friendlyName)) {
        this.friendlyName = data().deepCopy(fields()[2].schema(), other.friendlyName);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.index)) {
        this.index = data().deepCopy(fields()[3].schema(), other.index);
        fieldSetFlags()[3] = true;
      }
    }

    /** Gets the value of the 'type' field */
    public java.lang.String getType() {
      return type;
    }
    
    /** Sets the value of the 'type' field */
    public graphene.model.idl.G_EdgeType.Builder setType(java.lang.String value) {
      validate(fields()[0], value);
      this.type = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'type' field has been set */
    public boolean hasType() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'type' field */
    public graphene.model.idl.G_EdgeType.Builder clearType() {
      type = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'name' field */
    public java.lang.String getName() {
      return name;
    }
    
    /** Sets the value of the 'name' field */
    public graphene.model.idl.G_EdgeType.Builder setName(java.lang.String value) {
      validate(fields()[1], value);
      this.name = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'name' field has been set */
    public boolean hasName() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'name' field */
    public graphene.model.idl.G_EdgeType.Builder clearName() {
      name = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'friendlyName' field */
    public java.lang.String getFriendlyName() {
      return friendlyName;
    }
    
    /** Sets the value of the 'friendlyName' field */
    public graphene.model.idl.G_EdgeType.Builder setFriendlyName(java.lang.String value) {
      validate(fields()[2], value);
      this.friendlyName = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'friendlyName' field has been set */
    public boolean hasFriendlyName() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'friendlyName' field */
    public graphene.model.idl.G_EdgeType.Builder clearFriendlyName() {
      friendlyName = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'index' field */
    public java.lang.Long getIndex() {
      return index;
    }
    
    /** Sets the value of the 'index' field */
    public graphene.model.idl.G_EdgeType.Builder setIndex(long value) {
      validate(fields()[3], value);
      this.index = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'index' field has been set */
    public boolean hasIndex() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'index' field */
    public graphene.model.idl.G_EdgeType.Builder clearIndex() {
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    public G_EdgeType build() {
      try {
        G_EdgeType record = new G_EdgeType();
        record.type = fieldSetFlags()[0] ? this.type : (java.lang.String) defaultValue(fields()[0]);
        record.name = fieldSetFlags()[1] ? this.name : (java.lang.String) defaultValue(fields()[1]);
        record.friendlyName = fieldSetFlags()[2] ? this.friendlyName : (java.lang.String) defaultValue(fields()[2]);
        record.index = fieldSetFlags()[3] ? this.index : (java.lang.Long) defaultValue(fields()[3]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}