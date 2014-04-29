/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package graphene.model.idl;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class G_DataSource extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"G_DataSource\",\"namespace\":\"graphene.model.idl\",\"fields\":[{\"name\":\"id\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"id of the data source\"},{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"name of the data source\"},{\"name\":\"friendlyName\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"],\"doc\":\"user-friendly short-text for key (displayable)\",\"default\":null},{\"name\":\"isEntity\",\"type\":\"boolean\",\"default\":true},{\"name\":\"isTransaction\",\"type\":\"boolean\",\"default\":true},{\"name\":\"dataSets\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"G_DataSet\",\"fields\":[{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"name of the dataset\"},{\"name\":\"isEntity\",\"type\":\"boolean\",\"default\":true},{\"name\":\"isTransaction\",\"type\":\"boolean\",\"default\":true},{\"name\":\"fields\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"G_DataSetField\",\"doc\":\"* A field within a DataSet, which in turn is a real or virtual table within\\r\\n\\t * a DataSource. From Non Avro version by PWG.\",\"fields\":[{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"name of the field\"},{\"name\":\"friendlyName\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"],\"doc\":\"user-friendly short-text for key (displayable)\",\"default\":null},{\"name\":\"type\",\"type\":{\"type\":\"enum\",\"name\":\"G_PropertyType\",\"doc\":\"* The 21st century meaning, \\\"conceal from unauthorized\\r\\n\\t\\t\\t\\t\\t\\t * view; censor but do not destroy\\\"\",\"symbols\":[\"DOUBLE\",\"LONG\",\"BOOLEAN\",\"STRING\",\"DATE\",\"GEO\",\"IMAGE\",\"URI\",\"OTHER\",\"REDACTED\"]},\"doc\":\"One of DOUBLE, LONG, BOOLEAN, STRING, DATE, GEO, OTHER\"},{\"name\":\"sortable\",\"type\":\"boolean\",\"default\":true},{\"name\":\"searchable\",\"type\":\"boolean\",\"default\":true},{\"name\":\"reportable\",\"type\":\"boolean\",\"default\":true}]}}}]}}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  /** id of the data source */
   private java.lang.String id;
  /** name of the data source */
   private java.lang.String name;
  /** user-friendly short-text for key (displayable) */
   private java.lang.String friendlyName;
   private boolean isEntity;
   private boolean isTransaction;
   private java.util.List<graphene.model.idl.G_DataSet> dataSets;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public G_DataSource() {}

  /**
   * All-args constructor.
   */
  public G_DataSource(java.lang.String id, java.lang.String name, java.lang.String friendlyName, java.lang.Boolean isEntity, java.lang.Boolean isTransaction, java.util.List<graphene.model.idl.G_DataSet> dataSets) {
    this.id = id;
    this.name = name;
    this.friendlyName = friendlyName;
    this.isEntity = isEntity;
    this.isTransaction = isTransaction;
    this.dataSets = dataSets;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id;
    case 1: return name;
    case 2: return friendlyName;
    case 3: return isEntity;
    case 4: return isTransaction;
    case 5: return dataSets;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id = (java.lang.String)value$; break;
    case 1: name = (java.lang.String)value$; break;
    case 2: friendlyName = (java.lang.String)value$; break;
    case 3: isEntity = (java.lang.Boolean)value$; break;
    case 4: isTransaction = (java.lang.Boolean)value$; break;
    case 5: dataSets = (java.util.List<graphene.model.idl.G_DataSet>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'id' field.
   * id of the data source   */
  public java.lang.String getId() {
    return id;
  }

  /**
   * Sets the value of the 'id' field.
   * id of the data source   * @param value the value to set.
   */
  public void setId(java.lang.String value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'name' field.
   * name of the data source   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * Sets the value of the 'name' field.
   * name of the data source   * @param value the value to set.
   */
  public void setName(java.lang.String value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'friendlyName' field.
   * user-friendly short-text for key (displayable)   */
  public java.lang.String getFriendlyName() {
    return friendlyName;
  }

  /**
   * Sets the value of the 'friendlyName' field.
   * user-friendly short-text for key (displayable)   * @param value the value to set.
   */
  public void setFriendlyName(java.lang.String value) {
    this.friendlyName = value;
  }

  /**
   * Gets the value of the 'isEntity' field.
   */
  public java.lang.Boolean getIsEntity() {
    return isEntity;
  }

  /**
   * Sets the value of the 'isEntity' field.
   * @param value the value to set.
   */
  public void setIsEntity(java.lang.Boolean value) {
    this.isEntity = value;
  }

  /**
   * Gets the value of the 'isTransaction' field.
   */
  public java.lang.Boolean getIsTransaction() {
    return isTransaction;
  }

  /**
   * Sets the value of the 'isTransaction' field.
   * @param value the value to set.
   */
  public void setIsTransaction(java.lang.Boolean value) {
    this.isTransaction = value;
  }

  /**
   * Gets the value of the 'dataSets' field.
   */
  public java.util.List<graphene.model.idl.G_DataSet> getDataSets() {
    return dataSets;
  }

  /**
   * Sets the value of the 'dataSets' field.
   * @param value the value to set.
   */
  public void setDataSets(java.util.List<graphene.model.idl.G_DataSet> value) {
    this.dataSets = value;
  }

  /** Creates a new G_DataSource RecordBuilder */
  public static graphene.model.idl.G_DataSource.Builder newBuilder() {
    return new graphene.model.idl.G_DataSource.Builder();
  }
  
  /** Creates a new G_DataSource RecordBuilder by copying an existing Builder */
  public static graphene.model.idl.G_DataSource.Builder newBuilder(graphene.model.idl.G_DataSource.Builder other) {
    return new graphene.model.idl.G_DataSource.Builder(other);
  }
  
  /** Creates a new G_DataSource RecordBuilder by copying an existing G_DataSource instance */
  public static graphene.model.idl.G_DataSource.Builder newBuilder(graphene.model.idl.G_DataSource other) {
    return new graphene.model.idl.G_DataSource.Builder(other);
  }
  
  /**
   * RecordBuilder for G_DataSource instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<G_DataSource>
    implements org.apache.avro.data.RecordBuilder<G_DataSource> {

    private java.lang.String id;
    private java.lang.String name;
    private java.lang.String friendlyName;
    private boolean isEntity;
    private boolean isTransaction;
    private java.util.List<graphene.model.idl.G_DataSet> dataSets;

    /** Creates a new Builder */
    private Builder() {
      super(graphene.model.idl.G_DataSource.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(graphene.model.idl.G_DataSource.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
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
      if (isValidValue(fields()[3], other.isEntity)) {
        this.isEntity = data().deepCopy(fields()[3].schema(), other.isEntity);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.isTransaction)) {
        this.isTransaction = data().deepCopy(fields()[4].schema(), other.isTransaction);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.dataSets)) {
        this.dataSets = data().deepCopy(fields()[5].schema(), other.dataSets);
        fieldSetFlags()[5] = true;
      }
    }
    
    /** Creates a Builder by copying an existing G_DataSource instance */
    private Builder(graphene.model.idl.G_DataSource other) {
            super(graphene.model.idl.G_DataSource.SCHEMA$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
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
      if (isValidValue(fields()[3], other.isEntity)) {
        this.isEntity = data().deepCopy(fields()[3].schema(), other.isEntity);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.isTransaction)) {
        this.isTransaction = data().deepCopy(fields()[4].schema(), other.isTransaction);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.dataSets)) {
        this.dataSets = data().deepCopy(fields()[5].schema(), other.dataSets);
        fieldSetFlags()[5] = true;
      }
    }

    /** Gets the value of the 'id' field */
    public java.lang.String getId() {
      return id;
    }
    
    /** Sets the value of the 'id' field */
    public graphene.model.idl.G_DataSource.Builder setId(java.lang.String value) {
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
    public graphene.model.idl.G_DataSource.Builder clearId() {
      id = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'name' field */
    public java.lang.String getName() {
      return name;
    }
    
    /** Sets the value of the 'name' field */
    public graphene.model.idl.G_DataSource.Builder setName(java.lang.String value) {
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
    public graphene.model.idl.G_DataSource.Builder clearName() {
      name = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'friendlyName' field */
    public java.lang.String getFriendlyName() {
      return friendlyName;
    }
    
    /** Sets the value of the 'friendlyName' field */
    public graphene.model.idl.G_DataSource.Builder setFriendlyName(java.lang.String value) {
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
    public graphene.model.idl.G_DataSource.Builder clearFriendlyName() {
      friendlyName = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'isEntity' field */
    public java.lang.Boolean getIsEntity() {
      return isEntity;
    }
    
    /** Sets the value of the 'isEntity' field */
    public graphene.model.idl.G_DataSource.Builder setIsEntity(boolean value) {
      validate(fields()[3], value);
      this.isEntity = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'isEntity' field has been set */
    public boolean hasIsEntity() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'isEntity' field */
    public graphene.model.idl.G_DataSource.Builder clearIsEntity() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'isTransaction' field */
    public java.lang.Boolean getIsTransaction() {
      return isTransaction;
    }
    
    /** Sets the value of the 'isTransaction' field */
    public graphene.model.idl.G_DataSource.Builder setIsTransaction(boolean value) {
      validate(fields()[4], value);
      this.isTransaction = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'isTransaction' field has been set */
    public boolean hasIsTransaction() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'isTransaction' field */
    public graphene.model.idl.G_DataSource.Builder clearIsTransaction() {
      fieldSetFlags()[4] = false;
      return this;
    }

    /** Gets the value of the 'dataSets' field */
    public java.util.List<graphene.model.idl.G_DataSet> getDataSets() {
      return dataSets;
    }
    
    /** Sets the value of the 'dataSets' field */
    public graphene.model.idl.G_DataSource.Builder setDataSets(java.util.List<graphene.model.idl.G_DataSet> value) {
      validate(fields()[5], value);
      this.dataSets = value;
      fieldSetFlags()[5] = true;
      return this; 
    }
    
    /** Checks whether the 'dataSets' field has been set */
    public boolean hasDataSets() {
      return fieldSetFlags()[5];
    }
    
    /** Clears the value of the 'dataSets' field */
    public graphene.model.idl.G_DataSource.Builder clearDataSets() {
      dataSets = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    @Override
    public G_DataSource build() {
      try {
        G_DataSource record = new G_DataSource();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.String) defaultValue(fields()[0]);
        record.name = fieldSetFlags()[1] ? this.name : (java.lang.String) defaultValue(fields()[1]);
        record.friendlyName = fieldSetFlags()[2] ? this.friendlyName : (java.lang.String) defaultValue(fields()[2]);
        record.isEntity = fieldSetFlags()[3] ? this.isEntity : (java.lang.Boolean) defaultValue(fields()[3]);
        record.isTransaction = fieldSetFlags()[4] ? this.isTransaction : (java.lang.Boolean) defaultValue(fields()[4]);
        record.dataSets = fieldSetFlags()[5] ? this.dataSets : (java.util.List<graphene.model.idl.G_DataSet>) defaultValue(fields()[5]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
