/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package graphene.model.idl;  
@SuppressWarnings("all")
/** Defines a pattern, either for input by the user, or as part of a pattern library. */
@org.apache.avro.specific.AvroGenerated
public class G_PatternDescriptor extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"G_PatternDescriptor\",\"namespace\":\"graphene.model.idl\",\"doc\":\"Defines a pattern, either for input by the user, or as part of a pattern library.\",\"fields\":[{\"name\":\"uid\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"unique ID of the pattern *\"},{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"displayable name of the pattern *\"},{\"name\":\"description\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"displayable long description of the pattern *\",\"default\":null},{\"name\":\"entities\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"G_EntityMatchDescriptor\",\"doc\":\"Description of entity match criteria within a pattern.\\n\\t \\n\\t CHANGED IN 1.5:\\n\\t  - removed sameAs\\n\\t  - replaced weight with constraint\",\"fields\":[{\"name\":\"uid\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"The UID of the PATTERN node (NOT the underlying matched entity ID).\\n\\t\\t Will be referenced by G_LinkMatchDescriptors as source or target, and in results.\\n\\t\\t Cannot be null.\"},{\"name\":\"role\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"Optional role name, for labeling the pattern for human understanding\",\"default\":null},{\"name\":\"entities\",\"type\":[{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},\"null\"],\"doc\":\"entities should match AT LEAST ONE OF the given entity IDs, if provided\",\"default\":null},{\"name\":\"tags\",\"type\":[{\"type\":\"array\",\"items\":{\"type\":\"enum\",\"name\":\"G_EntityTag\",\"doc\":\"This is the current list of tags for Entities:\\n\\t \\n\\t CHANGED in 1.6:\\n\\t   - added ACCOUNT_OWNER, CLUSTER_SUMMARY\",\"symbols\":[\"ACCOUNT_OWNER\",\"ACCOUNT\",\"GROUP\",\"CLUSTER\",\"CLUSTER_SUMMARY\",\"FILE\",\"ANONYMOUS\",\"PROMPT_FOR_DETAILS\",\"OTHER\"]}},\"null\"],\"doc\":\"entities should match AT LEAST ONE OF the given tags (e.g ACCOUNT), if provided\",\"default\":null},{\"name\":\"properties\",\"type\":[{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"G_PropertyMatchDescriptor\",\"doc\":\"A PropertyDescriptor is used to describe a possible property that can be present in an entity or link. It describes \\n\\t a single property that can be used in a property search. It can optionally include example or suggested values \\n\\t for searching on.\\n\\t \\n\\t CHANGED IN 1.8\",\"fields\":[{\"name\":\"key\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"field name or G_PropertyTag (or G_PathMatchTag) that could be searched on\"},{\"name\":\"singletonRange\",\"type\":{\"type\":\"record\",\"name\":\"G_SingletonRange\",\"doc\":\"Single value\\n\\t\\n\\tADDED IN 1.5\",\"fields\":[{\"name\":\"value\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"int\",\"float\",\"double\",\"long\",\"boolean\",{\"type\":\"record\",\"name\":\"G_GeoData\",\"doc\":\"Structured representation of geo-spatial data.\",\"fields\":[{\"name\":\"text\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"an address or other place reference; unstructured text field\",\"default\":null},{\"name\":\"lat\",\"type\":[\"double\",\"null\"],\"doc\":\"latitude\",\"default\":null},{\"name\":\"lon\",\"type\":[\"double\",\"null\"],\"doc\":\"longitude\",\"default\":null},{\"name\":\"cc\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"ISO 3 digit country code\",\"default\":null}]}]},{\"name\":\"type\",\"type\":{\"type\":\"enum\",\"name\":\"G_PropertyType\",\"doc\":\"Allowed types for Property values.\\n\\n\\t CHANGED in 1.5\",\"symbols\":[\"DOUBLE\",\"LONG\",\"BOOLEAN\",\"STRING\",\"DATE\",\"GEO\",\"OTHER\"]},\"doc\":\"One of DOUBLE, LONG, BOOLEAN, STRING, DATE, GEO, OTHER\"}]},\"doc\":\"value of the Property to search on\",\"default\":null},{\"name\":\"listRange\",\"type\":{\"type\":\"record\",\"name\":\"G_ListRange\",\"doc\":\"List of values\\n\\t\\n\\tADDED IN 1.5\",\"fields\":[{\"name\":\"values\",\"type\":{\"type\":\"array\",\"items\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"int\",\"float\",\"double\",\"long\",\"boolean\",\"G_GeoData\"]}},{\"name\":\"type\",\"type\":\"G_PropertyType\",\"doc\":\"One of DOUBLE, LONG, BOOLEAN, STRING, DATE, GEO, OTHER\"}]},\"default\":null},{\"name\":\"boundedRange\",\"type\":{\"type\":\"record\",\"name\":\"G_BoundedRange\",\"doc\":\"Bounded or unbounded range values\\n\\t\\n\\tADDED IN 1.5\",\"fields\":[{\"name\":\"start\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"int\",\"float\",\"double\",\"long\",\"boolean\",\"G_GeoData\",\"null\"],\"doc\":\"start of range, or null if unbounded start\"},{\"name\":\"end\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"int\",\"float\",\"double\",\"long\",\"boolean\",\"G_GeoData\",\"null\"],\"doc\":\"end of range, or null if unbounded start\"},{\"name\":\"inclusive\",\"type\":\"boolean\",\"doc\":\"If true, range includes specified endpoint. If false, range is exclusive.\"},{\"name\":\"type\",\"type\":\"G_PropertyType\",\"doc\":\"One of DOUBLE, LONG, BOOLEAN, STRING, DATE, GEO, OTHER\"}]},\"default\":null},{\"name\":\"variable\",\"type\":[{\"type\":\"string\",\"avro.java.string\":\"String\"},\"null\"],\"doc\":\"If not null/empty, the value is relative to a logical variable specified here (e.g. \\\"X\\\")\\n\\t\\t *  Other parameters using the same logical variable name are relative to this value.\\n\\t\\t *  For instance, for a {key=\\\"amount\\\", value=\\\"0.98\\\", variable=\\\"A\\\"} means that the value\\n\\t\\t *  of amount is 0.98A.  Another amount might be 0.55A, and the property match engine\\n\\t\\t *  (e.g. search engine, database query or pattern match algorithm) should understand\\n\\t\\t *  the relative values.\\n\\t\\t *  \\n\\t\\t *  If no variable is specified, then the value is an absolute number. For example,\\n\\t\\t *  {key=\\\"amount\\\", value=\\\"0.98\\\"} refers to an amount of exactly 0.98.\",\"default\":\"\"},{\"name\":\"weight\",\"type\":[\"float\",\"null\"],\"doc\":\"Relative importance of this match criteria, where the default is 1.0.\",\"default\":1.0},{\"name\":\"similarity\",\"type\":[\"float\",\"null\"],\"doc\":\"Require similarity for fuzzy searches, the default is null.\",\"default\":1.0},{\"name\":\"include\",\"type\":\"boolean\",\"doc\":\"If true, INCLUDE all values matching this descriptor. If false, EXCLUDE all values matching this descriptor.\",\"default\":true},{\"name\":\"constraint\",\"type\":[{\"type\":\"enum\",\"name\":\"G_Constraint\",\"doc\":\"Property value matching constraints\\n\\n\\t ADDED IN 1.8\",\"symbols\":[\"EQUALS\",\"NOT\",\"OPTIONAL_EQUALS\",\"FUZZY\",\"CONTAINS\",\"ENDS_WITH\",\"REGEX\",\"STARTS_WITH\",\"BETWEEN\",\"FUZZY_PARTIAL_OPTIONAL\"]},\"null\"],\"doc\":\"MUST_EQUALS, FUZZY_PARTIAL_OPTIONAL, MUST_NOT\"},{\"name\":\"typeMappings\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"G_TypeMapping\",\"doc\":\"Used to describe how an G_PropertyDescriptor maps to given a type.\\n\\n\\t\\tADDED IN 1.8\\n\\t *\",\"fields\":[{\"name\":\"type\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"The type that the mapping applies to *\"},{\"name\":\"memberKey\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"field that the G_Property maps to in the type *\"}]}},\"doc\":\"List of type mappings to match in *\",\"default\":null}]}},\"null\"],\"doc\":\"entities should match ALL of the provided property descriptors (e.g. LABEL, GEO, etc)\\n\\t\\t    e.g. logical \\\"AND\\\". Partial matches may be returned, if scoring is provided.\",\"default\":null},{\"name\":\"examplars\",\"type\":[{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},\"null\"],\"doc\":\"used for QBE -- not used to match like the entities list, this list of entities\\n\\t\\t    should be used by the system to infer the above constraints when the user does not\\n\\t\\t    provide them.\",\"default\":null},{\"name\":\"constraint\",\"type\":[\"G_Constraint\",\"null\"],\"doc\":\"MUST_EQUALS, FUZZY_PARTIAL_OPTIONAL, MUST_NOT\"}]}},\"doc\":\"- ordered list of entity criteria for the pattern\\n\\t\\t - the first nodes in the ordered list represent stages earlier in the sequence of events\\n\\t\\t - CHANGED in 1.5: graphs may have cycles\"},{\"name\":\"links\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"G_LinkMatchDescriptor\",\"doc\":\"* CHANGED in 1.5 - The PathMatchDescriptor was removed, and replaced with G_PathMatchTags,\\n\\t *   which can be used as they key in a PropertyMatch Descriptor. Set these to require\\n\\t *   certain path lengths or path time window matches.\",\"fields\":[{\"name\":\"uid\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"doc\":\"The UID of the PATTERN link (NOT the underlying matched link ID). Will be referenced in results.\\n\\t\\t Cannot be null.\"},{\"name\":\"role\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"Optional role name,"," for labeling the pattern for human understanding\",\"default\":null},{\"name\":\"source\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"The UID of a G_EntityMatchDescriptor node (not an underlying Entity UID) \\n\\t\\t\\tBeing null indicates that matched links should have no source node\"},{\"name\":\"target\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"The UID of a G_EntityMatchDescriptor node (not an underlying Entity UID) \\n\\t\\t\\tBeing null indicates that matched links should have no target node\"},{\"name\":\"tags\",\"type\":[{\"type\":\"array\",\"items\":{\"type\":\"enum\",\"name\":\"G_LinkTag\",\"doc\":\"This is the current list of tags for Links:\",\"symbols\":[\"FINANCIAL\",\"SOCIAL\",\"COMMUNICATION\",\"OTHER\"]}},\"null\"],\"doc\":\"entities should match AT LEAST ONE OF the given tags (e.g FINANCIAL, COMMUNICATION, SOCIAL), if provided\",\"default\":null},{\"name\":\"properties\",\"type\":[{\"type\":\"array\",\"items\":\"G_PropertyMatchDescriptor\"},\"null\"],\"doc\":\"entities should match ALL of the provided property descriptors (e.g. LABEL, etc), if provided\",\"default\":null},{\"name\":\"stage\",\"type\":\"int\",\"doc\":\"If not negative, indicates the relative order of events within the pattern. Need not be unique.\\n\\t\\t    If not provided, then transaction order must be inferred from the graph structure.\",\"default\":-1},{\"name\":\"constraint\",\"type\":[\"G_Constraint\",\"null\"],\"doc\":\"MUST_EQUALS, FUZZY_PARTIAL_OPTIONAL, MUST_NOT\"}]}},\"doc\":\"- link criteria between the entities above\\n\\t\\t - CHANGED in 1.5: graphs may have cycles\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  /** unique ID of the pattern * */
   private java.lang.String uid;
  /** displayable name of the pattern * */
   private java.lang.String name;
  /** displayable long description of the pattern * */
   private java.lang.String description;
  /** - ordered list of entity criteria for the pattern
		 - the first nodes in the ordered list represent stages earlier in the sequence of events
		 - CHANGED in 1.5: graphs may have cycles */
   private java.util.List<graphene.model.idl.G_EntityMatchDescriptor> entities;
  /** - link criteria between the entities above
		 - CHANGED in 1.5: graphs may have cycles */
   private java.util.List<graphene.model.idl.G_LinkMatchDescriptor> links;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public G_PatternDescriptor() {}

  /**
   * All-args constructor.
   */
  public G_PatternDescriptor(java.lang.String uid, java.lang.String name, java.lang.String description, java.util.List<graphene.model.idl.G_EntityMatchDescriptor> entities, java.util.List<graphene.model.idl.G_LinkMatchDescriptor> links) {
    this.uid = uid;
    this.name = name;
    this.description = description;
    this.entities = entities;
    this.links = links;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return uid;
    case 1: return name;
    case 2: return description;
    case 3: return entities;
    case 4: return links;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: uid = (java.lang.String)value$; break;
    case 1: name = (java.lang.String)value$; break;
    case 2: description = (java.lang.String)value$; break;
    case 3: entities = (java.util.List<graphene.model.idl.G_EntityMatchDescriptor>)value$; break;
    case 4: links = (java.util.List<graphene.model.idl.G_LinkMatchDescriptor>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'uid' field.
   * unique ID of the pattern *   */
  public java.lang.String getUid() {
    return uid;
  }

  /**
   * Sets the value of the 'uid' field.
   * unique ID of the pattern *   * @param value the value to set.
   */
  public void setUid(java.lang.String value) {
    this.uid = value;
  }

  /**
   * Gets the value of the 'name' field.
   * displayable name of the pattern *   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * Sets the value of the 'name' field.
   * displayable name of the pattern *   * @param value the value to set.
   */
  public void setName(java.lang.String value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'description' field.
   * displayable long description of the pattern *   */
  public java.lang.String getDescription() {
    return description;
  }

  /**
   * Sets the value of the 'description' field.
   * displayable long description of the pattern *   * @param value the value to set.
   */
  public void setDescription(java.lang.String value) {
    this.description = value;
  }

  /**
   * Gets the value of the 'entities' field.
   * - ordered list of entity criteria for the pattern
		 - the first nodes in the ordered list represent stages earlier in the sequence of events
		 - CHANGED in 1.5: graphs may have cycles   */
  public java.util.List<graphene.model.idl.G_EntityMatchDescriptor> getEntities() {
    return entities;
  }

  /**
   * Sets the value of the 'entities' field.
   * - ordered list of entity criteria for the pattern
		 - the first nodes in the ordered list represent stages earlier in the sequence of events
		 - CHANGED in 1.5: graphs may have cycles   * @param value the value to set.
   */
  public void setEntities(java.util.List<graphene.model.idl.G_EntityMatchDescriptor> value) {
    this.entities = value;
  }

  /**
   * Gets the value of the 'links' field.
   * - link criteria between the entities above
		 - CHANGED in 1.5: graphs may have cycles   */
  public java.util.List<graphene.model.idl.G_LinkMatchDescriptor> getLinks() {
    return links;
  }

  /**
   * Sets the value of the 'links' field.
   * - link criteria between the entities above
		 - CHANGED in 1.5: graphs may have cycles   * @param value the value to set.
   */
  public void setLinks(java.util.List<graphene.model.idl.G_LinkMatchDescriptor> value) {
    this.links = value;
  }

  /** Creates a new G_PatternDescriptor RecordBuilder */
  public static graphene.model.idl.G_PatternDescriptor.Builder newBuilder() {
    return new graphene.model.idl.G_PatternDescriptor.Builder();
  }
  
  /** Creates a new G_PatternDescriptor RecordBuilder by copying an existing Builder */
  public static graphene.model.idl.G_PatternDescriptor.Builder newBuilder(graphene.model.idl.G_PatternDescriptor.Builder other) {
    return new graphene.model.idl.G_PatternDescriptor.Builder(other);
  }
  
  /** Creates a new G_PatternDescriptor RecordBuilder by copying an existing G_PatternDescriptor instance */
  public static graphene.model.idl.G_PatternDescriptor.Builder newBuilder(graphene.model.idl.G_PatternDescriptor other) {
    return new graphene.model.idl.G_PatternDescriptor.Builder(other);
  }
  
  /**
   * RecordBuilder for G_PatternDescriptor instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<G_PatternDescriptor>
    implements org.apache.avro.data.RecordBuilder<G_PatternDescriptor> {

    private java.lang.String uid;
    private java.lang.String name;
    private java.lang.String description;
    private java.util.List<graphene.model.idl.G_EntityMatchDescriptor> entities;
    private java.util.List<graphene.model.idl.G_LinkMatchDescriptor> links;

    /** Creates a new Builder */
    private Builder() {
      super(graphene.model.idl.G_PatternDescriptor.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(graphene.model.idl.G_PatternDescriptor.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.uid)) {
        this.uid = data().deepCopy(fields()[0].schema(), other.uid);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.name)) {
        this.name = data().deepCopy(fields()[1].schema(), other.name);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.description)) {
        this.description = data().deepCopy(fields()[2].schema(), other.description);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.entities)) {
        this.entities = data().deepCopy(fields()[3].schema(), other.entities);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.links)) {
        this.links = data().deepCopy(fields()[4].schema(), other.links);
        fieldSetFlags()[4] = true;
      }
    }
    
    /** Creates a Builder by copying an existing G_PatternDescriptor instance */
    private Builder(graphene.model.idl.G_PatternDescriptor other) {
            super(graphene.model.idl.G_PatternDescriptor.SCHEMA$);
      if (isValidValue(fields()[0], other.uid)) {
        this.uid = data().deepCopy(fields()[0].schema(), other.uid);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.name)) {
        this.name = data().deepCopy(fields()[1].schema(), other.name);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.description)) {
        this.description = data().deepCopy(fields()[2].schema(), other.description);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.entities)) {
        this.entities = data().deepCopy(fields()[3].schema(), other.entities);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.links)) {
        this.links = data().deepCopy(fields()[4].schema(), other.links);
        fieldSetFlags()[4] = true;
      }
    }

    /** Gets the value of the 'uid' field */
    public java.lang.String getUid() {
      return uid;
    }
    
    /** Sets the value of the 'uid' field */
    public graphene.model.idl.G_PatternDescriptor.Builder setUid(java.lang.String value) {
      validate(fields()[0], value);
      this.uid = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'uid' field has been set */
    public boolean hasUid() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'uid' field */
    public graphene.model.idl.G_PatternDescriptor.Builder clearUid() {
      uid = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'name' field */
    public java.lang.String getName() {
      return name;
    }
    
    /** Sets the value of the 'name' field */
    public graphene.model.idl.G_PatternDescriptor.Builder setName(java.lang.String value) {
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
    public graphene.model.idl.G_PatternDescriptor.Builder clearName() {
      name = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'description' field */
    public java.lang.String getDescription() {
      return description;
    }
    
    /** Sets the value of the 'description' field */
    public graphene.model.idl.G_PatternDescriptor.Builder setDescription(java.lang.String value) {
      validate(fields()[2], value);
      this.description = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'description' field has been set */
    public boolean hasDescription() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'description' field */
    public graphene.model.idl.G_PatternDescriptor.Builder clearDescription() {
      description = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'entities' field */
    public java.util.List<graphene.model.idl.G_EntityMatchDescriptor> getEntities() {
      return entities;
    }
    
    /** Sets the value of the 'entities' field */
    public graphene.model.idl.G_PatternDescriptor.Builder setEntities(java.util.List<graphene.model.idl.G_EntityMatchDescriptor> value) {
      validate(fields()[3], value);
      this.entities = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'entities' field has been set */
    public boolean hasEntities() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'entities' field */
    public graphene.model.idl.G_PatternDescriptor.Builder clearEntities() {
      entities = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'links' field */
    public java.util.List<graphene.model.idl.G_LinkMatchDescriptor> getLinks() {
      return links;
    }
    
    /** Sets the value of the 'links' field */
    public graphene.model.idl.G_PatternDescriptor.Builder setLinks(java.util.List<graphene.model.idl.G_LinkMatchDescriptor> value) {
      validate(fields()[4], value);
      this.links = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'links' field has been set */
    public boolean hasLinks() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'links' field */
    public graphene.model.idl.G_PatternDescriptor.Builder clearLinks() {
      links = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    @Override
    public G_PatternDescriptor build() {
      try {
        G_PatternDescriptor record = new G_PatternDescriptor();
        record.uid = fieldSetFlags()[0] ? this.uid : (java.lang.String) defaultValue(fields()[0]);
        record.name = fieldSetFlags()[1] ? this.name : (java.lang.String) defaultValue(fields()[1]);
        record.description = fieldSetFlags()[2] ? this.description : (java.lang.String) defaultValue(fields()[2]);
        record.entities = fieldSetFlags()[3] ? this.entities : (java.util.List<graphene.model.idl.G_EntityMatchDescriptor>) defaultValue(fields()[3]);
        record.links = fieldSetFlags()[4] ? this.links : (java.util.List<graphene.model.idl.G_LinkMatchDescriptor>) defaultValue(fields()[4]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
