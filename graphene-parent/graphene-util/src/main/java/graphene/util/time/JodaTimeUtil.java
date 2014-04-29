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
    public static final DateTimeFormatter localDateFormatter = DateTimeFormat.forPattern("YYYYMMDD");

    // ///////////////////////////////////////////
    // From JodaTime types to standard Java types
    // ///////////////////////////////////////////

    public static java.sql.Timestamp toSQLTimestamp(DateTime dt) {
        // TODO - confirm this conversion always works, esp. across timezones
        java.sql.Timestamp ts = (dt == null ? null : new java.sql.Timestamp(dt.getMillis()));
        return ts;
    }

    public static java.util.Date toJavaDate(DateTime dt) {
        // TODO - confirm this conversion always works, esp. across timezones
        java.util.Date ts = (dt == null ? null : new java.util.Date(dt.getMillis()));
        return ts;
    }

    public static String toTimeZoneID(DateTime dt) {
        String s = (dt == null ? null : dt.getZone().getID());
        return s;
    }

    public static java.sql.Date toSQLDate(LocalDate ld) {
        // TODO - confirm this conversion always works, esp. across timezones
        java.sql.Date d = (ld == null ? null : new java.sql.Date(ld.toDateTimeAtStartOfDay().getMillis()));
        return d;
    }

    public static java.util.Date toJavaDate(LocalDate ld) {
        // TODO - confirm this conversion always works, esp. across timezones
        java.util.Date d = (ld == null ? null : new java.util.Date(ld.toDateTimeAtStartOfDay().getMillis()));
        return d;
    }

    public static String toString(LocalDate ld) {
        // TODO - confirm this conversion always works, esp. across timezones
        String s = (ld == null ? null : localDateFormatter.withZone(DateTimeZone.UTC).print(ld));
        return s;
    }

    public static java.sql.Timestamp toSQLTimestamp(LocalDateTime ldt) {
        // TODO - confirm this conversion always works, esp. across timezones
        java.sql.Timestamp ts = (ldt == null ? null : new java.sql.Timestamp(ldt.toDateTime().getMillis()));
        return ts;
    }

    public static java.util.Date toJavaDate(LocalDateTime ldt) {
        // TODO - confirm this conversion always works, esp. across timezones
        java.util.Date d = (ldt == null ? null : new java.util.Date(ldt.toDateTime().getMillis()));
        return d;
    }

    public static java.sql.Time toSQLTime(LocalTime lt) {
        // TODO - confirm this conversion always works, esp. across timezones
        java.sql.Time t = (lt == null ? null : new java.sql.Time(lt.getMillisOfDay()));
        return t;
    }

    public static Integer toIntegerMillis(LocalTime lt) {
        Integer i = (lt == null ? null : new Integer(lt.getMillisOfDay()));
        return i;
    }

    public static String toString(LocalTime lt) {
        String s = (lt == null ? null : lt.toString());
        return s;
    }

    // ///////////////////////////////////////////
    // From standard Java types to JodaTime types
    // ///////////////////////////////////////////


    public static DateTime toDateTime(java.sql.Timestamp ts) {
        // TODO - confirm this conversion always works, esp. across timezones
        DateTime dt = (ts == null ? null : new DateTime(ts));
        return dt;
    }

    public static DateTime toDateTime(java.util.Date d) {
        // TODO - confirm this conversion always works, esp. across timezones
        DateTime dt = (d == null ? null : new DateTime(d));
        return dt;
    }
    public static DateTime toDateTime(long d) {
        return toDateTime(new java.util.Date(d));
    }
    public static DateTime toDateTime(java.sql.Timestamp ts, String timeZoneID) {
        // TODO - confirm this conversion always works, esp. across timezones
        DateTime dt = (ts == null ? null : new DateTime(ts, DateTimeZone.forID(timeZoneID)));
        return dt;
    }

    public static DateTime toDateTime(java.util.Date d, String timeZoneID) {
        // TODO - confirm this conversion always works, esp. across timezones
        DateTime dt = (d == null ? null : new DateTime(d, DateTimeZone.forID(timeZoneID)));
        return dt;
    }

    public static LocalDate toLocalDate(java.sql.Date d) {
        // TODO - confirm this conversion always works, esp. across timezones
        LocalDate ld = (d == null ? null : LocalDate.fromDateFields(d));
        return ld;
    }

    public static LocalDate toLocalDate(java.util.Date d) {
        // TODO - confirm this conversion always works, esp. across timezones
        LocalDate ld = (d == null ? null : LocalDate.fromDateFields(d));
        return ld;
    }

    public static LocalDate toLocalDate(String s) {
        // TODO - confirm this conversion always works, esp. across timezones
        LocalDate ld = (s == null ? null : localDateFormatter.withZone(DateTimeZone.UTC).parseDateTime(s).toLocalDate());
        return ld;
    }
    
    public static LocalDateTime toLocalDateTime(java.sql.Timestamp ts) {
        // TODO - confirm this conversion always works, esp. across timezones
        LocalDateTime ldt = (ts == null ? null : LocalDateTime.fromDateFields(ts));
        return ldt;
    }

    public static LocalDateTime toLocalDateTime(java.util.Date d) {
        // TODO - confirm this conversion always works, esp. across timezones
        LocalDateTime ldt = (d == null ? null : LocalDateTime.fromDateFields(d));
        return ldt;
    }

    public static LocalTime toLocalTime(java.sql.Time t) {
        // TODO - confirm this conversion always works, esp. across timezones
        LocalTime lt = (t == null ? null : new LocalTime(t, DateTimeZone.UTC));
        return lt;
    }

    public static LocalTime toLocalTime(Integer i) {
        LocalTime lt = (i == null ? null : LocalTime.fromMillisOfDay(i));
        return lt;
    }

    public static LocalTime toLocalTime(String s) {
        LocalTime lt = (s == null ? null : new LocalTime(s));
        return lt;
    }

    // ///////////////////////////////////////////
    // Tests
    // ///////////////////////////////////////////

    public static void main(String[] args) {
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

    public static DateTimeZone losAngeles = DateTimeZone.forID("America/Los_Angeles");
    public static DateTimeZone brisbane = DateTimeZone.forID("Australia/Brisbane");
    public static DateTimeZone perth = DateTimeZone.forID("Australia/Perth");

  

    public static void test_dateTime() {
        System.out.println("Test DateTime");
        DateTime dt1 = new DateTime();
        java.sql.Timestamp ts = toSQLTimestamp(dt1);
        DateTime dt2 = toDateTime(ts);
        System.out.println("DateTime 1 = " + dt1);
        System.out.println("Timestamp  = " + ts);
        System.out.println("DateTime 2 = " + dt2);
        if (!dt2.equals(dt1)) {
            throw new IllegalStateException();
        }
    }

    public static void test_dateTime_tz() {
        System.out.println("Test DateTime with timezone");
        DateTime dt1 = new DateTime(losAngeles);
        java.sql.Timestamp ts = toSQLTimestamp(dt1);
        String tzID = toTimeZoneID(dt1);
        DateTime dt2 = toDateTime(ts, tzID);
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
        LocalDate ld1 = new LocalDate();
        java.sql.Date d = toSQLDate(ld1);
        LocalDate ld2 = toLocalDate(d);
        System.out.println("LocalDate 1 = " + ld1);
        System.out.println("Date        = " + d);
        System.out.println("LocalDate 2 = " + ld2);
        if (!ld2.equals(ld1)) {
            throw new IllegalStateException();
        }
    }

   

    public static void test_localDate_shift_java_tz() {
        System.out.println("Test LocalDate with shifted Java timezone");

        TimeZone originalTZ = TimeZone.getDefault();
        TimeZone losAngelesTZ = TimeZone.getTimeZone("America/Los_Angeles");

        TimeZone.setDefault(losAngelesTZ);
        LocalDate ld1 = new LocalDate();
        System.out.println("ld1 LocalDate()   = " + ld1 + " when default TZ = " + TimeZone.getDefault());

        java.sql.Date d = toSQLDate(ld1);
        System.out.println("d toSQLDate(ld1)  = " + d + " when default TZ = " + TimeZone.getDefault());
        TimeZone.setDefault(originalTZ);
        System.out.println("d toSQLDate(ld1)  = " + d + " when default TZ = " + TimeZone.getDefault());

        LocalDate ld2 = toLocalDate(d);
        System.out.println("ld2 toLocalDate(d) = " + ld2 + " when default TZ = " + TimeZone.getDefault());

        TimeZone.setDefault(originalTZ);
        if (!ld2.equals(ld1)) {
            throw new IllegalStateException();
        }
    }

    public static void test_localDate_shift_joda_tz() {
        System.out.println("Test LocalDate with shifted JodaTime timezone");
        DateTimeZone originalTZ = DateTimeZone.getDefault();
        DateTimeZone losAngelesTZ = DateTimeZone.forID("America/Los_Angeles");

        DateTimeZone.setDefault(losAngelesTZ);
        LocalDate ld0 = new LocalDate(losAngelesTZ);
        System.out.println("ld0 LocalDate(losAngelesTZ) = " + ld0 + " when default TZ = " + DateTimeZone.getDefault());

        DateTimeZone.setDefault(losAngelesTZ);
        LocalDate ld1 = new LocalDate();
        System.out.println("ld1 LocalDate()             = " + ld1 + " when default TZ = " + DateTimeZone.getDefault());

        java.sql.Date d0 = toSQLDate(ld1);
        System.out.println("d0 toSQLDate(ld0)           = " + d0 + " when default TZ = " + DateTimeZone.getDefault());
        java.sql.Date d1 = toSQLDate(ld1);
        System.out.println("d1 toSQLDate(ld1)           = " + d1 + " when default TZ = " + DateTimeZone.getDefault());
        DateTimeZone.setDefault(originalTZ);
        System.out.println("d1 toSQLDate(ld1)           = " + d1 + " when default TZ = " + DateTimeZone.getDefault());

        DateTimeZone.setDefault(originalTZ);
        LocalDate ld2 = toLocalDate(d1);
        System.out.println("ld2 toLocalDate(d1)         = " + ld2 + " when default TZ = " + DateTimeZone.getDefault());

        DateTimeZone.setDefault(originalTZ);
        if (!ld2.equals(ld1)) {
            throw new IllegalStateException();
        }
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

    public static void test_localDateTime() {
        System.out.println("Test LocalDateTime");
        LocalDateTime ldt1 = new LocalDateTime();
        java.sql.Timestamp ts = toSQLTimestamp(ldt1);
        LocalDateTime ldt2 = toLocalDateTime(ts);
        System.out.println("LocalDateTime 1 = " + ldt1);
        System.out.println("Timestamp       = " + ts);
        System.out.println("LocalDateTime 2 = " + ldt2);
        if (!ldt2.equals(ldt1)) {
            throw new IllegalStateException();
        }
    }

    public static void test_localTime_as_integer() {
        System.out.println("Test LocalTime as Integer");
        LocalTime lt1 = new LocalTime();
        Integer i = toIntegerMillis(lt1);
        LocalTime lt2 = toLocalTime(i);
        System.out.println("LocalTime 1 = " + lt1);
        System.out.println("Integer     = " + i);
        System.out.println("LocalTime 2 = " + lt2);
        if (!lt2.equals(lt1)) {
            throw new IllegalStateException();
        }
    }

    public static void test_localTime_as_string() {
        System.out.println("Test LocalTime as String");
        LocalTime lt1 = new LocalTime();
        String t = toString(lt1);
        LocalTime lt2 = toLocalTime(t);
        System.out.println("LocalTime 1 = " + lt1);
        System.out.println("String      = " + t);
        System.out.println("LocalTime 2 = " + lt2);
        if (!lt2.equals(lt1)) {
            throw new IllegalStateException();
        }
    }

}
