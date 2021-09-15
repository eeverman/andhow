package org.yarnandtail.andhow.valuetype;

import java.time.LocalDateTime;
import org.yarnandtail.andhow.api.ParsingException;

/**
 * Type representation of Java LocalDateTime objects.
 * 
 * The text format used is the default for LocalDateTime objects, which uses the
 * ISO format like this: <code>2011-12-03T10:15:30</code>
 * 
 * See the parse() method for more parsing examples.
 * 
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely use the same instance.
 * 
 * @author eeverman
 */
public class LocalDateTimeType extends BaseValueType<LocalDateTime> {

	private static final LocalDateTimeType instance = new LocalDateTimeType();
	
	private LocalDateTimeType() {
		super(LocalDateTime.class);
	}

	/**
	 * @deprecated since 0.4.1. Use {@link #instance()} instead
	 *
	 * @return An instance of the {@link #LocalDateTimeType()}
	 */
	@Deprecated(forRemoval = true)
	public static LocalDateTimeType get() {
		return instance();
	}

	/**
	 * @return An instance of the {@link #LocalDateTimeType()}
	 */
	public static LocalDateTimeType instance() {
		return instance;
	}

	/**
	 * The text format used is the default for LocalDateTime objects, which uses the
	 * ISO format like this: <code>2011-12-03T10:15:30</code>.  Full documentation
	 * is here:  https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_LOCAL_DATE
	 * Here are some sample valid values:
	 * <ul>
	 * <li><code>2011-12-03T10:15:30</code> - Year, month, day, 'T', Hour, minute, second
	 * <li><code>0001-01-03T01:01:01</code> - All values must be zero padded
	 * <li><code>2011-12-03T10:15</code> - Seconds are optional
	 * <li><code>2011-12-03T10:15:25.123456789</code> - Decimal seconds up to 9 decimal places are allowed
	 * <li><code>2011-12-03T00:15:30</code> - The first hour is hour zero
	 * <li><code>2011-12-03T23:00:00</code> - The last hour is hour 23
	 * </ul>
	 * @param sourceValue The @{@link String} value to be parsed into a @{@link LocalDateTime}
	 * @return A valid @{@link LocalDateTime}
	 * @throws ParsingException If the @{@link String} can't be parsed into a @{@link LocalDateTime}
	 */
	@Override
	public LocalDateTime parse(String sourceValue) throws ParsingException {
		
		if (sourceValue != null) {
			try {
				return LocalDateTime.parse(sourceValue);
			} catch (Exception e) {
				throw new ParsingException("Unable to convert to a LocalDateTime", sourceValue, e);
			}
		} else {
			return null;
		}
	}
	
	@Override
	public LocalDateTime cast(Object o) throws RuntimeException {
		return (LocalDateTime)o;
	}
	
}
