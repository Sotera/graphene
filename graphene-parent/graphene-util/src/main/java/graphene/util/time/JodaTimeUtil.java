/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.util.time;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class JodaTimeUtil {
	public static final DateTimeFormatter localDateFormatter = DateTimeFormat
			.forPattern("YYYYMMDD");

	// ///////////////////////////////////////////
	// From JodaTime types to standard Java types
	// ///////////////////////////////////////////

	public static DateTimeZone losAngeles = DateTimeZone
			.forID("America/Los_Angeles");

	public static DateTimeZone brisbane = DateTimeZone
			.forID("Australia/Brisbane");

	public static DateTimeZone perth = DateTimeZone.forID("Australia/Perth");

	public static void main(final String[] args) {
		test_dateTime();
		System.out.println(" ");
		test_dateTime_tz();
		System.out.println(" ");
		test_localDate();
		System.out.println(" ");
		test_localDateTime();
		System.out.println(" ");
		test_localDate_shift_java_tz();
		System.out.println(" ");
		test_localDate_shift_joda_tz();
		System.out.println(" ");
		test_localTime_as_integer();
		System.out.println(" ");
		test_localTime_as_string();
		System.out.println(" ");
	}

	public static void test_dateTime() {
		System.out.println("Test DateTime");
		final DateTime dt1 = new DateTime();
		final java.sql.Timestamp ts = toSQLTimestamp(dt1);
		final DateTime dt2 = toDateTime(ts);
		System.out.println("DateTime 1 = " + dt1);
		System.out.println("Timestamp  = " + ts);
		System.out.println("DateTime 2 = " + dt2);
		if (!dt2.equals(dt1)) {
			throw new IllegalStateException();
		}
	}

	public static void test_dateTime_tz() {
		System.out.println("Test DateTime with timezone");
		final DateTime dt1 = new DateTime(losAngeles);
		final java.sql.Timestamp ts = toSQLTimestamp(dt1);
		final String tzID = toTimeZoneID(dt1);
		final DateTime dt2 = toDateTime(ts, tzID);
		System.out.println("DateTime 1 = " + dt1);
		System.out.println("Timestamp  = " + ts);
		System.out.println("TimeZoneID = " + tzID);
		System.out.println("DateTime 2 = " + dt2);
		if (!dt2.equals(dt1)) {
			throw new IllegalStateException();
		}
	}

	public static void test_localDate() {
		System.out.println("Test LocalDate");
		final LocalDate ld1 = new LocalDate();
		final java.sql.Date d = toSQLDate(ld1);
		final LocalDate ld2 = toLocalDate(d);
		System.out.println("LocalDate 1 = " + ld1);
		System.out.println("Date        = " + d);
		System.out.println("LocalDate 2 = " + ld2);
		if (!ld2.equals(ld1)) {
			throw new IllegalStateException();
		}
	}

	public static void test_localDate_shift_java_tz() {
		System.out.println("Test LocalDate with shifted Java timezone");

		final TimeZone originalTZ = TimeZone.getDefault();
		final TimeZone losAngelesTZ = TimeZone
				.getTimeZone("America/Los_Angeles");

		TimeZone.setDefault(losAngelesTZ);
		final LocalDate ld1 = new LocalDate();
		System.out.println("ld1 LocalDate()   = " + ld1 + " when default TZ = "
				+ TimeZone.getDefault());

		final java.sql.Date d = toSQLDate(ld1);
		System.out.println("d toSQLDate(ld1)  = " + d + " when default TZ = "
				+ TimeZone.getDefault());
		TimeZone.setDefault(originalTZ);
		System.out.println("d toSQLDate(ld1)  = " + d + " when default TZ = "
				+ TimeZone.getDefault());

		final LocalDate ld2 = toLocalDate(d);
		System.out.println("ld2 toLocalDate(d) = " + ld2
				+ " when default TZ = " + TimeZone.getDefault());

		TimeZone.setDefault(originalTZ);
		if (!ld2.equals(ld1)) {
			throw new IllegalStateException();
		}
	}

	public static void test_localDate_shift_joda_tz() {
		System.out.println("Test LocalDate with shifted JodaTime timezone");
		final DateTimeZone originalTZ = DateTimeZone.getDefault();
		final DateTimeZone losAngelesTZ = DateTimeZone
				.forID("America/Los_Angeles");

		DateTimeZone.setDefault(losAngelesTZ);
		final LocalDate ld0 = new LocalDate(losAngelesTZ);
		System.out.println("ld0 LocalDate(losAngelesTZ) = " + ld0
				+ " when default TZ = " + DateTimeZone.getDefault());

		DateTimeZone.setDefault(losAngelesTZ);
		final LocalDate ld1 = new LocalDate();
		System.out.println("ld1 LocalDate()             = " + ld1
				+ " when default TZ = " + DateTimeZone.getDefault());

		final java.sql.Date d0 = toSQLDate(ld1);
		System.out.println("d0 toSQLDate(ld0)           = " + d0
				+ " when default TZ = " + DateTimeZone.getDefault());
		final java.sql.Date d1 = toSQLDate(ld1);
		System.out.println("d1 toSQLDate(ld1)           = " + d1
				+ " when default TZ = " + DateTimeZone.getDefault());
		DateTimeZone.setDefault(originalTZ);
		System.out.println("d1 toSQLDate(ld1)           = " + d1
				+ " when default TZ = " + DateTimeZone.getDefault());

		DateTimeZone.setDefault(originalTZ);
		final LocalDate ld2 = toLocalDate(d1);
		System.out.println("ld2 toLocalDate(d1)         = " + ld2
				+ " when default TZ = " + DateTimeZone.getDefault());

		DateTimeZone.setDefault(originalTZ);
		if (!ld2.equals(ld1)) {
			throw new IllegalStateException();
		}
	}

	public static void test_localDateTime() {
		System.out.println("Test LocalDateTime");
		final LocalDateTime ldt1 = new LocalDateTime();
		final java.sql.Timestamp ts = toSQLTimestamp(ldt1);
		final LocalDateTime ldt2 = toLocalDateTime(ts);
		System.out.println("LocalDateTime 1 = " + ldt1);
		System.out.println("Timestamp       = " + ts);
		System.out.println("LocalDateTime 2 = " + ldt2);
		if (!ldt2.equals(ldt1)) {
			throw new IllegalStateException();
		}
	}

	public static void test_localTime_as_integer() {
		System.out.println("Test LocalTime as Integer");
		final LocalTime lt1 = new LocalTime();
		final Integer i = toIntegerMillis(lt1);
		final LocalTime lt2 = toLocalTime(i);
		System.out.println("LocalTime 1 = " + lt1);
		System.out.println("Integer     = " + i);
		System.out.println("LocalTime 2 = " + lt2);
		if (!lt2.equals(lt1)) {
			throw new IllegalStateException();
		}
	}

	// ///////////////////////////////////////////
	// From standard Java types to JodaTime types
	// ///////////////////////////////////////////

	public static void test_localTime_as_string() {
		System.out.println("Test LocalTime as String");
		final LocalTime lt1 = new LocalTime();
		final String t = toString(lt1);
		final LocalTime lt2 = toLocalTime(t);
		System.out.println("LocalTime 1 = " + lt1);
		System.out.println("String      = " + t);
		System.out.println("LocalTime 2 = " + lt2);
		if (!lt2.equals(lt1)) {
			throw new IllegalStateException();
		}
	}

	public static DateTime toDateTime(final java.sql.Timestamp ts) {
		// TODO - confirm this conversion always works, esp. across timezones
		final DateTime dt = (ts == null ? null : new DateTime(ts));
		return dt;
	}

	public static DateTime toDateTime(final java.sql.Timestamp ts,
			final String timeZoneID) {
		// TODO - confirm this conversion always works, esp. across timezones
		final DateTime dt = (ts == null ? null : new DateTime(ts,
				DateTimeZone.forID(timeZoneID)));
		return dt;
	}

	public static DateTime toDateTime(final java.util.Date d) {
		// TODO - confirm this conversion always works, esp. across timezones
		final DateTime dt = (d == null ? null : new DateTime(d));
		return dt;
	}

	public static DateTime toDateTime(final java.util.Date d,
			final String timeZoneID) {
		// TODO - confirm this conversion always works, esp. across timezones
		final DateTime dt = (d == null ? null : new DateTime(d,
				DateTimeZone.forID(timeZoneID)));
		return dt;
	}

	public static DateTime toDateTime(final long d) {
		return new DateTime(d);
	}

	public static Integer toIntegerMillis(final LocalTime lt) {
		final Integer i = (lt == null ? null : new Integer(lt.getMillisOfDay()));
		return i;
	}

	public static java.util.Date toJavaDate(final DateTime dt) {
		// TODO - confirm this conversion always works, esp. across timezones
		final java.util.Date ts = (dt == null ? null : new java.util.Date(
				dt.getMillis()));
		return ts;
	}

	public static java.util.Date toJavaDate(final LocalDate ld) {
		// TODO - confirm this conversion always works, esp. across timezones
		final java.util.Date d = (ld == null ? null : new java.util.Date(ld
				.toDateTimeAtStartOfDay().getMillis()));
		return d;
	}

	public static java.util.Date toJavaDate(final LocalDateTime ldt) {
		// TODO - confirm this conversion always works, esp. across timezones
		final java.util.Date d = (ldt == null ? null : new java.util.Date(ldt
				.toDateTime().getMillis()));
		return d;
	}

	public static LocalDate toLocalDate(final java.sql.Date d) {
		// TODO - confirm this conversion always works, esp. across timezones
		final LocalDate ld = (d == null ? null : LocalDate.fromDateFields(d));
		return ld;
	}

	public static LocalDate toLocalDate(final java.util.Date d) {
		// TODO - confirm this conversion always works, esp. across timezones
		final LocalDate ld = (d == null ? null : LocalDate.fromDateFields(d));
		return ld;
	}

	public static LocalDate toLocalDate(final String s) {
		// TODO - confirm this conversion always works, esp. across timezones
		final LocalDate ld = (s == null ? null : localDateFormatter
				.withZone(DateTimeZone.UTC).parseDateTime(s).toLocalDate());
		return ld;
	}

	// ///////////////////////////////////////////
	// Tests
	// ///////////////////////////////////////////

	public static LocalDateTime toLocalDateTime(final java.sql.Timestamp ts) {
		// TODO - confirm this conversion always works, esp. across timezones
		final LocalDateTime ldt = (ts == null ? null : LocalDateTime
				.fromDateFields(ts));
		return ldt;
	}

	public static LocalDateTime toLocalDateTime(final java.util.Date d) {
		// TODO - confirm this conversion always works, esp. across timezones
		final LocalDateTime ldt = (d == null ? null : LocalDateTime
				.fromDateFields(d));
		return ldt;
	}

	public static LocalTime toLocalTime(final Integer i) {
		final LocalTime lt = (i == null ? null : LocalTime.fromMillisOfDay(i));
		return lt;
	}

	public static LocalTime toLocalTime(final java.sql.Time t) {
		// TODO - confirm this conversion always works, esp. across timezones
		final LocalTime lt = (t == null ? null : new LocalTime(t,
				DateTimeZone.UTC));
		return lt;
	}

	public static LocalTime toLocalTime(final String s) {
		final LocalTime lt = (s == null ? null : new LocalTime(s));
		return lt;
	}

	public static java.sql.Date toSQLDate(final LocalDate ld) {
		// TODO - confirm this conversion always works, esp. across timezones
		final java.sql.Date d = (ld == null ? null : new java.sql.Date(ld
				.toDateTimeAtStartOfDay().getMillis()));
		return d;
	}

	public static java.sql.Time toSQLTime(final LocalTime lt) {
		// TODO - confirm this conversion always works, esp. across timezones
		final java.sql.Time t = (lt == null ? null : new java.sql.Time(
				lt.getMillisOfDay()));
		return t;
	}

	public static java.sql.Timestamp toSQLTimestamp(final DateTime dt) {
		// TODO - confirm this conversion always works, esp. across timezones
		final java.sql.Timestamp ts = (dt == null ? null
				: new java.sql.Timestamp(dt.getMillis()));
		return ts;
	}

	public static java.sql.Timestamp toSQLTimestamp(final LocalDateTime ldt) {
		// TODO - confirm this conversion always works, esp. across timezones
		final java.sql.Timestamp ts = (ldt == null ? null
				: new java.sql.Timestamp(ldt.toDateTime().getMillis()));
		return ts;
	}

	// public static void test_localDate_tz() {
	// System.out.println("Test LocalDate with different timezones");
	// LocalDate ld1 = new LocalDate(losAngeles);
	// java.sql.Date d1 = toSQLDate(ld1);
	// LocalDate ld2 = new LocalDate(perth);
	// java.sql.Date d2 = toSQLDate(ld2);
	// LocalDate ld3 = new LocalDate();
	// java.sql.Date d3 = toSQLDate(ld3);
	// System.out.println("LocalDate 1 = " + ld1);
	// System.out.println("Date        = " + d);
	// System.out.println("LocalDate 2 = " + ld2);
	// if (!ld2.equals(ld1)) {
	// throw new IllegalStateException();
	// }
	// }

	public static String toString(final LocalDate ld) {
		// TODO - confirm this conversion always works, esp. across timezones
		final String s = (ld == null ? null : localDateFormatter.withZone(
				DateTimeZone.UTC).print(ld));
		return s;
	}

	public static String toString(final LocalTime lt) {
		final String s = (lt == null ? null : lt.toString());
		return s;
	}

	public static String toTimeZoneID(final DateTime dt) {
		final String s = (dt == null ? null : dt.getZone().getID());
		return s;
	}

}
