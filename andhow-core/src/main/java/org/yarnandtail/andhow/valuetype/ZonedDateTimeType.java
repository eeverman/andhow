package org.yarnandtail.andhow.valuetype;

import org.yarnandtail.andhow.api.ParsingException;

import java.time.ZonedDateTime;

/**
 * Type representation of a Java ZonedDateTime objects.
 *
 * This class is threadsafe and uses a singleton pattern to prevent multiple
 * instances, since all users can safely use the same instance.
 *
 * @author chace86
 */
public class ZonedDateTimeType extends BaseValueType<ZonedDateTime> {

    private static final ZonedDateTimeType INSTANCE = new ZonedDateTimeType();

    private ZonedDateTimeType() { super(ZonedDateTime.class); }

    /**
     * @return An instance of the {@link #ZonedDateTimeType()}
     */
    public static ZonedDateTimeType instance() { return INSTANCE; }

    /**
     * The text format used is the default for ZonedDateTime objects, which uses the
     * ISO format like this: <code>2019-10-31T03:16:15.149Z[Europe/Paris]</code>. Full documentation
     * is here: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_ZONED_DATE_TIME
     * @param sourceValue The {@link String} value to be parsed into a @{@link ZonedDateTime}
     * @return A valid @{@link ZonedDateTime}
     * @throws ParsingException If the @{@link String} cannot be parsed into a @{@link ZonedDateTime}
     */
    @Override
    public ZonedDateTime parse(String sourceValue) throws ParsingException {

        if (sourceValue != null) {
            try {
                return ZonedDateTime.parse(sourceValue);
            } catch (Exception e) {
                throw new ParsingException("Unable to convert to a LocalDateTime", sourceValue, e);
            }
        } else {
            return null;
        }
    }

    @Override
    public ZonedDateTime cast(Object o) throws RuntimeException {
        return (ZonedDateTime)o;
    }
}
