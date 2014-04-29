/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package graphene.model.idl;  
@SuppressWarnings("all")
/** A PropertyDescriptor is used to describe a possible property that can be present in an entity or link. It describes 
	 a single property that can be used in a property search. It can optionally include example or suggested values 
	 for searching on. */
@org.apache.avro.specific.AvroGenerated
public class G_PropertyMatchDescriptor extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"G_PropertyMatchDescriptor\",\"namespace\":\"graphene.model.idl\",\"doc\":\"A PropertyDescriptor is used to describe a possible property that can be present in an entity or link. It describes \\r\\n\\t a single property that can be used in a property search. It can optionally include example or suggested values \\r\\n\\t for searching on.\",\"fields\":[{\"name\":\"key\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"field name or G_PropertyTag (or G_PathMatchTag) that could be searched on\"},{\"name\":\"range\",\"type\":[{\"type\":\"record\",\"name\":\"G_SingletonRange\",\"doc\":\"* Single value\\r\\n\\t *\",\"fields\":[{\"name\":\"value\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"int\",\"float\",\"double\",\"long\",\"boolean\",{\"type\":\"record\",\"name\":\"G_GeoData\",\"doc\":\"* Structured representation of geo-spatial data.\",\"fields\":[{\"name\":\"text\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"],\"doc\":\"an address or other place reference; unstructured text field\",\"default\":null},{\"name\":\"lat\",\"type\":[\"double\",\"null\"],\"doc\":\"latitude\",\"default\":null},{\"name\":\"lon\",\"type\":[\"double\",\"null\"],\"doc\":\"longitude\",\"default\":null},{\"name\":\"cc\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"],\"doc\":\"ISO 3 digit country code\",\"default\":null}]}]},{\"name\":\"type\",\"type\":{\"type\":\"enum\",\"name\":\"G_PropertyType\",\"doc\":\"* The 21st century meaning, \\\"conceal from unauthorized\\r\\n\\t\\t\\t\\t\\t\\t * view; censor but do not destroy\\\"\",\"symbols\":[\"DOUBLE\",\"LONG\",\"BOOLEAN\",\"STRING\",\"DATE\",\"GEO\",\"IMAGE\",\"URI\",\"OTHER\",\"REDACTED\"]},\"doc\":\"One of DOUBLE, LONG, BOOLEAN, STRING, DATE, GEO, OTHER\"}]},{\"type\":\"record\",\"name\":\"G_ListRange\",\"doc\":\"* List of values\\r\\n\\t *\",\"fields\":[{\"name\":\"values\",\"type\":{\"type\":\"array\",\"items\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"int\",\"float\",\"double\",\"long\",\"boolean\",\"G_GeoData\"]}},{\"name\":\"type\",\"type\":\"G_PropertyType\",\"doc\":\"One of DOUBLE, LONG, BOOLEAN, STRING, DATE, GEO, OTHER\"}]},{\"type\":\"record\",\"name\":\"G_BoundedRange\",\"doc\":\"* Bounded or unbounded range values\\r\\n\\t *\",\"fields\":[{\"name\":\"start\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"int\",\"float\",\"double\",\"long\",\"boolean\",\"G_GeoData\",\"null\"],\"doc\":\"start of range, or null if unbounded start\"},{\"name\":\"end\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"int\",\"float\",\"double\",\"long\",\"boolean\",\"G_GeoData\",\"null\"],\"doc\":\"end of range, or null if unbounded start\"},{\"name\":\"inclusive\",\"type\":\"boolean\",\"doc\":\"* If true, range includes specified endpoint. If false, range is\\r\\n\\t\\t * exclusive.\"},{\"name\":\"type\",\"type\":\"G_PropertyType\",\"doc\":\"One of DOUBLE, LONG, BOOLEAN, STRING, DATE, GEO, OTHER\"}]}],\"doc\":\"value of the Property to search on\",\"default\":null},{\"name\":\"variable\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"],\"doc\":\"If not null/empty, the value is relative to a logical variable specified here (e.g. \\\"X\\\")\\r\\n\\t\\t *  Other parameters using the same logical variable name are relative to this value.\\r\\n\\t\\t *  For instance, for a {key=\\\"amount\\\", value=\\\"0.98\\\", variable=\\\"A\\\"} means that the value\\r\\n\\t\\t *  of amount is 0.98A.  Another amount might be 0.55A, and the property match engine\\r\\n\\t\\t *  (e.g. search engine, database query or pattern match algorithm) should understand\\r\\n\\t\\t *  the relative values.\\r\\n\\t\\t *  \\r\\n\\t\\t *  If no variable is specified, then the value is an absolute number. For example,\\r\\n\\t\\t *  {key=\\\"amount\\\", value=\\\"0.98\\\"} refers to an amount of exactly 0.98.\",\"default\":\"\"},{\"name\":\"include\",\"type\":\"boolean\",\"doc\":\"If true, INCLUDE all values matching this descriptor. If false, EXCLUDE all values matching this descriptor.\",\"default\":true},{\"name\":\"constraint\",\"type\":[{\"type\":\"enum\",\"name\":\"G_Constraint\",\"doc\":\"Property value matching constraints\",\"symbols\":[\"REQUIRED_EQUALS\",\"FUZZY_PARTIAL_OPTIONAL\",\"NOT\"]},\"null\"],\"doc\":\"MUST_EQUALS, FUZZY_PARTIAL_OPTIONAL, MUST_NOT\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  /** field name or G_PropertyTag (or G_PathMatchTag) that could be searched on */
   private java.lang.String key;
  /** value of the Property to search on */
   private java.lang.Object range;
  /** If not null/empty, the value is relative to a logical variable specified here (e.g. "X")
		 *  Other parameters using the same logical variable name are relative to this value.
		 *  For instance, for a {key="amount", value="0.98", variable="A"} means that the value
		 *  of amount is 0.98A.  Another amount might be 0.55A, and the property match engine
		 *  (e.g. search engine, database query or pattern match algorithm) should understand
		 *  the relative values.
		 *  
		 *  If no variable is specified, then the value is an absolute number. For example,
		 *  {key="amount", value="0.98"} refers to an amount of exactly 0.98. */
   private java.lang.String variable;
  /** If true, INCLUDE all values matching this descriptor. If false, EXCLUDE all values matching this descriptor. */
   private boolean include;
  /** MUST_EQUALS, FUZZY_PARTIAL_OPTIONAL, MUST_NOT */
   private graphene.model.idl.G_Constraint constraint;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public G_PropertyMatchDescriptor() {}

  /**
   * All-args constructor.
   */
  public G_PropertyMatchDescriptor(java.lang.String key, java.lang.Object range, java.lang.String variable, java.lang.Boolean include, graphene.model.idl.G_Constraint constraint) {
    this.key = key;
    this.range = range;
    this.variable = variable;
    this.include = include;
    this.constraint = constraint;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return key;
    case 1: return range;
    case 2: return variable;
    case 3: return include;
    case 4: return constraint;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: key = (java.lang.String)value$; break;
    case 1: range = (java.lang.Object)value$; break;
    case 2: variable = (java.lang.String)value$; break;
    case 3: include = (java.lang.Boolean)value$; break;
    case 4: constraint = (graphene.model.idl.G_Constraint)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'key' field.
   * field name or G_PropertyTag (or G_PathMatchTag) that could be searched on   */
  public java.lang.String getKey() {
    return key;
  }

  /**
   * Sets the value of the 'key' field.
   * field name or G_PropertyTag (or G_PathMatchTag) that could be searched on   * @param value the value to set.
   */
  public void setKey(java.lang.String value) {
    this.key = value;
  }

  /**
   * Gets the value of the 'range' field.
   * value of the Property to search on   */
  public java.lang.Object getRange() {
    return range;
  }

  /**
   * Sets the value of the 'range' field.
   * value of the Property to search on   * @param value the value to set.
   */
  public void setRange(java.lang.Object value) {
    this.range = value;
  }

  /**
   * Gets the value of the 'variable' field.
   * If not null/empty, the value is relative to a logical variable specified here (e.g. "X")
		 *  Other parameters using the same logical variable name are relative to this value.
		 *  For instance, for a {key="amount", value="0.98", variable="A"} means that the value
		 *  of amount is 0.98A.  Another amount might be 0.55A, and the property match engine
		 *  (e.g. search engine, database query or pattern match algorithm) should understand
		 *  the relative values.
		 *  
		 *  If no variable is specified, then the value is an absolute number. For example,
		 *  {key="amount", value="0.98"} refers to an amount of exactly 0.98.   */
  public java.lang.String getVariable() {
    return variable;
  }

  /**
   * Sets the value of the 'variable' field.
   * If not null/empty, the value is relative to a logical variable specified here (e.g. "X")
		 *  Other parameters using the same logical variable name are relative to this value.
		 *  For instance, for a {key="amount", value="0.98", variable="A"} means that the value
		 *  of amount is 0.98A.  Another amount might be 0.55A, and the property match engine
		 *  (e.g. search engine, database query or pattern match algorithm) should understand
		 *  the relative values.
		 *  
		 *  If no variable is specified, then the value is an absolute number. For example,
		 *  {key="amount", value="0.98"} refers to an amount of exactly 0.98.   * @param value the value to set.
   */
  public void setVariable(java.lang.String value) {
    this.variable = value;
  }

  /**
   * Gets the value of the 'include' field.
   * If true, INCLUDE all values matching this descriptor. If false, EXCLUDE all values matching this descriptor.   */
  public java.lang.Boolean getInclude() {
    return include;
  }

  /**
   * Sets the value of the 'include' field.
   * If true, INCLUDE all values matching this descriptor. If false, EXCLUDE all values matching this descriptor.   * @param value the value to set.
   */
  public void setInclude(java.lang.Boolean value) {
    this.include = value;
  }

  /**
   * Gets the value of the 'constraint' field.
   * MUST_EQUALS, FUZZY_PARTIAL_OPTIONAL, MUST_NOT   */
  public graphene.model.idl.G_Constraint getConstraint() {
    return constraint;
  }

  /**
   * Sets the value of the 'constraint' field.
   * MUST_EQUALS, FUZZY_PARTIAL_OPTIONAL, MUST_NOT   * @param value the value to set.
   */
  public void setConstraint(graphene.model.idl.G_Constraint value) {
    this.constraint = value;
  }

  /** Creates a new G_PropertyMatchDescriptor RecordBuilder */
  public static graphene.model.idl.G_PropertyMatchDescriptor.Builder newBuilder() {
    return new graphene.model.idl.G_PropertyMatchDescriptor.Builder();
  }
  
  /** Creates a new G_PropertyMatchDescriptor RecordBuilder by copying an existing Builder */
  public static graphene.model.idl.G_PropertyMatchDescriptor.Builder newBuilder(graphene.model.idl.G_PropertyMatchDescriptor.Builder other) {
    return new graphene.model.idl.G_PropertyMatchDescriptor.Builder(other);
  }
  
  /** Creates a new G_PropertyMatchDescriptor RecordBuilder by copying an existing G_PropertyMatchDescriptor instance */
  public static graphene.model.idl.G_PropertyMatchDescriptor.Builder newBuilder(graphene.model.idl.G_PropertyMatchDescriptor other) {
    return new graphene.model.idl.G_PropertyMatchDescriptor.Builder(other);
  }
  
  /**
   * RecordBuilder for G_PropertyMatchDescriptor instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<G_PropertyMatchDescriptor>
    implements org.apache.avro.data.RecordBuilder<G_PropertyMatchDescriptor> {

    private java.lang.String key;
    private java.lang.Object range;
    private java.lang.String variable;
    private boolean include;
    private graphene.model.idl.G_Constraint constraint;

    /** Creates a new Builder */
    private Builder() {
      super(graphene.model.idl.G_PropertyMatchDescriptor.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(graphene.model.idl.G_PropertyMatchDescriptor.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.key)) {
        this.key = data().deepCopy(fields()[0].schema(), other.key);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.range)) {
        this.range = data().deepCopy(fields()[1].schema(), other.range);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.variable)) {
        this.variable = data().deepCopy(fields()[2].schema(), other.variable);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.include)) {
        this.include = data().deepCopy(fields()[3].schema(), other.include);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.constraint)) {
        this.constraint = data().deepCopy(fields()[4].schema(), other.constraint);
        fieldSetFlags()[4] = true;
      }
    }
    
    /** Creates a Builder by copying an existing G_PropertyMatchDescriptor instance */
    private Builder(graphene.model.idl.G_PropertyMatchDescriptor other) {
            super(graphene.model.idl.G_PropertyMatchDescriptor.SCHEMA$);
      if (isValidValue(fields()[0], other.key)) {
        this.key = data().deepCopy(fields()[0].schema(), other.key);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.range)) {
        this.range = data().deepCopy(fields()[1].schema(), other.range);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.variable)) {
        this.variable = data().deepCopy(fields()[2].schema(), other.variable);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.include)) {
        this.include = data().deepCopy(fields()[3].schema(), other.include);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.constraint)) {
        this.constraint = data().deepCopy(fields()[4].schema(), other.constraint);
        fieldSetFlags()[4] = true;
      }
    }

    /** Gets the value of the 'key' field */
    public java.lang.String getKey() {
      return key;
    }
    
    /** Sets the value of the 'key' field */
    public graphene.model.idl.G_PropertyMatchDescriptor.Builder setKey(java.lang.String value) {
      validate(fields()[0], value);
      this.key = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'key' field has been set */
    public boolean hasKey() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'key' field */
    public graphene.model.idl.G_PropertyMatchDescriptor.Builder clearKey() {
      key = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'range' field */
    public java.lang.Object getRange() {
      return range;
    }
    
    /** Sets the value of the 'range' field */
    public graphene.model.idl.G_PropertyMatchDescriptor.Builder setRange(java.lang.Object value) {
      validate(fields()[1], value);
      this.range = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'range' field has been set */
    public boolean hasRange() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'range' field */
    public graphene.model.idl.G_PropertyMatchDescriptor.Builder clearRange() {
      range = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'variable' field */
    public java.lang.String getVariable() {
      return variable;
    }
    
    /** Sets the value of the 'variable' field */
    public graphene.model.idl.G_PropertyMatchDescriptor.Builder setVariable(java.lang.String value) {
      validate(fields()[2], value);
      this.variable = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'variable' field has been set */
    public boolean hasVariable() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'variable' field */
    public graphene.model.idl.G_PropertyMatchDescriptor.Builder clearVariable() {
      variable = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'include' field */
    public java.lang.Boolean getInclude() {
      return include;
    }
    
    /** Sets the value of the 'include' field */
    public graphene.model.idl.G_PropertyMatchDescriptor.Builder setInclude(boolean value) {
      validate(fields()[3], value);
      this.include = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'include' field has been set */
    public boolean hasInclude() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'include' field */
    public graphene.model.idl.G_PropertyMatchDescriptor.Builder clearInclude() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'constraint' field */
    public graphene.model.idl.G_Constraint getConstraint() {
      return constraint;
    }
    
    /** Sets the value of the 'constraint' field */
    public graphene.model.idl.G_PropertyMatchDescriptor.Builder setConstraint(graphene.model.idl.G_Constraint value) {
      validate(fields()[4], value);
      this.constraint = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'constraint' field has been set */
    public boolean hasConstraint() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'constraint' field */
    public graphene.model.idl.G_PropertyMatchDescriptor.Builder clearConstraint() {
      constraint = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    @Override
    public G_PropertyMatchDescriptor build() {
      try {
        G_PropertyMatchDescriptor record = new G_PropertyMatchDescriptor();
        record.key = fieldSetFlags()[0] ? this.key : (java.lang.String) defaultValue(fields()[0]);
        record.range = fieldSetFlags()[1] ? this.range : (java.lang.Object) defaultValue(fields()[1]);
        record.variable = fieldSetFlags()[2] ? this.variable : (java.lang.String) defaultValue(fields()[2]);
        record.include = fieldSetFlags()[3] ? this.include : (java.lang.Boolean) defaultValue(fields()[3]);
        record.constraint = fieldSetFlags()[4] ? this.constraint : (graphene.model.idl.G_Constraint) defaultValue(fields()[4]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
