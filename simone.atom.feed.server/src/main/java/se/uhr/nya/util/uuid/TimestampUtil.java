package se.uhr.nya.util.uuid;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimestampUtil {

	private static final TimeZone UTC_TZ = TimeZone.getTimeZone("UTC");

	public static Calendar forUTCColumn(Date date) {
		Calendar c = Calendar.getInstance(UTC_TZ);
		c.setTime(date);
		return c;
	}

	public static Timestamp getUTCColumn(ResultSet rs, String column) throws SQLException {
		Calendar utcCalendar = Calendar.getInstance(UTC_TZ);
		return rs.getTimestamp(column, utcCalendar);
	}
}
