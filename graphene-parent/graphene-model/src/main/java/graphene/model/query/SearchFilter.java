package graphene.model.query;

import graphene.model.datasourcedescriptors.DataSetField;
import graphene.model.idl.G_SearchType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines one test in selecting data. Forms part of a set of tests in
 * AdvancedSearch Created by the JSON parser from a JSON advanced search spec
 * 
 * @author PWG for DARPA
 * 
 */
public class SearchFilter {
	private static Logger logger = LoggerFactory.getLogger(SearchFilter.class);
	private boolean caseSensitive = false;
	@XmlTransient
	private G_SearchType compareType = G_SearchType.COMPARE_INVALID;
	private String dmcomp = null; // soundalike
	private DataSetField field = null; // looked up after the remaining
	private String fieldName = null;
	private String fieldType = null; // string, date, number,
	private String operator = null;
	private Pattern pattern = null; // regex

	// TODO: set family and use it for comparisons

	private String value = null; // blank space delimited

	public boolean doCompare(String v, String family) {
		if (!family.equals(fieldName)){
			return false;}

		String myval = caseSensitive ? value : value.toLowerCase();
		boolean result = false;

		if (!caseSensitive){
			v = v.toLowerCase();}
		switch (compareType) {
		case COMPARE_EQUALS:
			result = v.equals(myval);
			break;
		case COMPARE_INCLUDE:
			result = v.indexOf(myval) >= 0;
			break;
		case COMPARE_CONTAINS:
			result = v.indexOf(myval) >= 0;
			break;
		case COMPARE_NOTINCLUDE:
			result = v.indexOf(myval) == -1;
			break;
		case COMPARE_SOUNDSLIKE:
			//TODO: Improve the efficiency of this
			DoubleMetaphone dm = new DoubleMetaphone();
			String dm2;
			if (dmcomp == null)
				dmcomp = (String) dm.encode(myval);
			dm2 = dm.encode(v);
			result = dm2.equals(dmcomp);
			break;

		case COMPARE_REGEX:
			if (pattern == null) {
				int flags;
				if (caseSensitive) {
					flags = 0;
				} else {
					flags = Pattern.CASE_INSENSITIVE;
				}
				logger.trace("Compiling regex pattern " + value);
				pattern = Pattern.compile(value, flags);
			}
			Matcher ms = pattern.matcher(v);
			result = ms.find(0);
			break;
		default:
			break;
		}
		return result;
	}

	public G_SearchType getCompareType() {
		return compareType;
	}

	public String getDmcomp() {
		return dmcomp;
	}

	public DataSetField getField() {
		return field;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public String getOperator() {
		return operator;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public String getValue() {
		return value;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public void setCompareType(G_SearchType compareType) {
		this.compareType = compareType;
	}

	public void setDmcomp(String dmcomp) {
		this.dmcomp = dmcomp;
	}

	public void setField(DataSetField field) {
		this.field = field;
	}

	public void setFieldName(String field) {
		this.fieldName = field;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public void setOperator(String operator) {
		this.operator = operator;
		this.compareType = G_SearchType.fromValue(operator);
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "SearchFilter [caseSensitive="
				+ caseSensitive
				+ ", "
				+ (compareType != null ? "compareType=" + compareType + ", "
						: "")
				+ (dmcomp != null ? "dmcomp=" + dmcomp + ", " : "")
				+ (field != null ? "field=" + field + ", " : "")
				+ (fieldName != null ? "fieldName=" + fieldName + ", " : "")
				+ (fieldType != null ? "fieldType=" + fieldType + ", " : "")
				+ (operator != null ? "operator=" + operator + ", " : "")
				+ (pattern != null ? "pattern=" + pattern + ", " : "")
				+ (value != null ? "value=" + value : "") + "]";
	}

}
