/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package graphene.model.idl;  
@SuppressWarnings("all")
/** * Describes a date range at a specific resolution.
	 * */
@org.apache.avro.specific.AvroGenerated
public class G_DateRange extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"G_DateRange\",\"namespace\":\"graphene.model.idl\",\"doc\":\"* Describes a date range at a specific resolution.\\r\\n\\t *\",\"fields\":[{\"name\":\"startDate\",\"type\":\"long\"},{\"name\":\"numBins\",\"type\":\"long\",\"doc\":\"number of bins to return, e.g. 12 monthly bins for 1 year of data\"},{\"name\":\"durationPerBin\",\"type\":{\"type\":\"record\",\"name\":\"G_Duration\",\"doc\":\"* A temporal duration\\r\\n\\t *\",\"fields\":[{\"name\":\"interval\",\"type\":{\"type\":\"enum\",\"name\":\"G_DateInterval\",\"doc\":\"* Temporal resolution of a duration\\r\\n\\t *\",\"symbols\":[\"SECONDS\",\"HOURS\",\"DAYS\",\"WEEKS\",\"MONTHS\",\"QUARTERS\",\"YEARS\"]},\"doc\":\"time aggregation level, e.g. use monthly data\"},{\"name\":\"numIntervals\",\"type\":\"long\",\"doc\":\"number of intervals, e.g. 12 monthly intervals is a 1 year duration\"}]},\"doc\":\"* number of intervals in a bin, e.g. 2 months/bin in 12 bins for 2\\r\\n\\t\\t * years of data\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
   private long startDate;
  /** number of bins to return, e.g. 12 monthly bins for 1 year of data */
   private long numBins;
  /** * number of intervals in a bin, e.g. 2 months/bin in 12 bins for 2
		 * years of data */
   private graphene.model.idl.G_Duration durationPerBin;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public G_DateRange() {}

  /**
   * All-args constructor.
   */
  public G_DateRange(java.lang.Long startDate, java.lang.Long numBins, graphene.model.idl.G_Duration durationPerBin) {
    this.startDate = startDate;
    this.numBins = numBins;
    this.durationPerBin = durationPerBin;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return startDate;
    case 1: return numBins;
    case 2: return durationPerBin;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: startDate = (java.lang.Long)value$; break;
    case 1: numBins = (java.lang.Long)value$; break;
    case 2: durationPerBin = (graphene.model.idl.G_Duration)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'startDate' field.
   */
  public java.lang.Long getStartDate() {
    return startDate;
  }

  /**
   * Sets the value of the 'startDate' field.
   * @param value the value to set.
   */
  public void setStartDate(java.lang.Long value) {
    this.startDate = value;
  }

  /**
   * Gets the value of the 'numBins' field.
   * number of bins to return, e.g. 12 monthly bins for 1 year of data   */
  public java.lang.Long getNumBins() {
    return numBins;
  }

  /**
   * Sets the value of the 'numBins' field.
   * number of bins to return, e.g. 12 monthly bins for 1 year of data   * @param value the value to set.
   */
  public void setNumBins(java.lang.Long value) {
    this.numBins = value;
  }

  /**
   * Gets the value of the 'durationPerBin' field.
   * * number of intervals in a bin, e.g. 2 months/bin in 12 bins for 2
		 * years of data   */
  public graphene.model.idl.G_Duration getDurationPerBin() {
    return durationPerBin;
  }

  /**
   * Sets the value of the 'durationPerBin' field.
   * * number of intervals in a bin, e.g. 2 months/bin in 12 bins for 2
		 * years of data   * @param value the value to set.
   */
  public void setDurationPerBin(graphene.model.idl.G_Duration value) {
    this.durationPerBin = value;
  }

  /** Creates a new G_DateRange RecordBuilder */
  public static graphene.model.idl.G_DateRange.Builder newBuilder() {
    return new graphene.model.idl.G_DateRange.Builder();
  }
  
  /** Creates a new G_DateRange RecordBuilder by copying an existing Builder */
  public static graphene.model.idl.G_DateRange.Builder newBuilder(graphene.model.idl.G_DateRange.Builder other) {
    return new graphene.model.idl.G_DateRange.Builder(other);
  }
  
  /** Creates a new G_DateRange RecordBuilder by copying an existing G_DateRange instance */
  public static graphene.model.idl.G_DateRange.Builder newBuilder(graphene.model.idl.G_DateRange other) {
    return new graphene.model.idl.G_DateRange.Builder(other);
  }
  
  /**
   * RecordBuilder for G_DateRange instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<G_DateRange>
    implements org.apache.avro.data.RecordBuilder<G_DateRange> {

    private long startDate;
    private long numBins;
    private graphene.model.idl.G_Duration durationPerBin;

    /** Creates a new Builder */
    private Builder() {
      super(graphene.model.idl.G_DateRange.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(graphene.model.idl.G_DateRange.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.startDate)) {
        this.startDate = data().deepCopy(fields()[0].schema(), other.startDate);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.numBins)) {
        this.numBins = data().deepCopy(fields()[1].schema(), other.numBins);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.durationPerBin)) {
        this.durationPerBin = data().deepCopy(fields()[2].schema(), other.durationPerBin);
        fieldSetFlags()[2] = true;
      }
    }
    
    /** Creates a Builder by copying an existing G_DateRange instance */
    private Builder(graphene.model.idl.G_DateRange other) {
            super(graphene.model.idl.G_DateRange.SCHEMA$);
      if (isValidValue(fields()[0], other.startDate)) {
        this.startDate = data().deepCopy(fields()[0].schema(), other.startDate);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.numBins)) {
        this.numBins = data().deepCopy(fields()[1].schema(), other.numBins);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.durationPerBin)) {
        this.durationPerBin = data().deepCopy(fields()[2].schema(), other.durationPerBin);
        fieldSetFlags()[2] = true;
      }
    }

    /** Gets the value of the 'startDate' field */
    public java.lang.Long getStartDate() {
      return startDate;
    }
    
    /** Sets the value of the 'startDate' field */
    public graphene.model.idl.G_DateRange.Builder setStartDate(long value) {
      validate(fields()[0], value);
      this.startDate = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'startDate' field has been set */
    public boolean hasStartDate() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'startDate' field */
    public graphene.model.idl.G_DateRange.Builder clearStartDate() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'numBins' field */
    public java.lang.Long getNumBins() {
      return numBins;
    }
    
    /** Sets the value of the 'numBins' field */
    public graphene.model.idl.G_DateRange.Builder setNumBins(long value) {
      validate(fields()[1], value);
      this.numBins = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'numBins' field has been set */
    public boolean hasNumBins() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'numBins' field */
    public graphene.model.idl.G_DateRange.Builder clearNumBins() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'durationPerBin' field */
    public graphene.model.idl.G_Duration getDurationPerBin() {
      return durationPerBin;
    }
    
    /** Sets the value of the 'durationPerBin' field */
    public graphene.model.idl.G_DateRange.Builder setDurationPerBin(graphene.model.idl.G_Duration value) {
      validate(fields()[2], value);
      this.durationPerBin = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'durationPerBin' field has been set */
    public boolean hasDurationPerBin() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'durationPerBin' field */
    public graphene.model.idl.G_DateRange.Builder clearDurationPerBin() {
      durationPerBin = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    public G_DateRange build() {
      try {
        G_DateRange record = new G_DateRange();
        record.startDate = fieldSetFlags()[0] ? this.startDate : (java.lang.Long) defaultValue(fields()[0]);
        record.numBins = fieldSetFlags()[1] ? this.numBins : (java.lang.Long) defaultValue(fields()[1]);
        record.durationPerBin = fieldSetFlags()[2] ? this.durationPerBin : (graphene.model.idl.G_Duration) defaultValue(fields()[2]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
