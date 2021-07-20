package org.yarnandtail.andhow.valuetype;

import org.junit.Test;
import static org.junit.Assert.*;
import org.yarnandtail.andhow.api.ParsingException;

import java.time.ZonedDateTime;

public class ZonedDateTimeTypeTest {

    @Test
    public void testParseHappyPath() throws ParsingException {
        ZonedDateTimeType type = ZonedDateTimeType.instance();
        assertEquals(ZonedDateTime.parse("2019-10-31T03:52:46.267346Z[Etc/UTC]"), type.parse("2019-10-31T03:52:46.267346Z[Etc/UTC]"));
        assertNull(type.parse(null));
    }

    @Test(expected = ParsingException.class)
    public void testParseInvalidDateFormat() throws ParsingException {
        ZonedDateTimeType type = ZonedDateTimeType.instance();
        assertFalse(type.isParsable("abc"));
        type.parse("abc");
    }

    @Test(expected = ParsingException.class)
    public void testParseEmpty() throws ParsingException {
        ZonedDateTimeType type = ZonedDateTimeType.instance();
        assertFalse(type.isParsable(""));
        type.parse("");
    }

    @Test
    public void testCast() {
        ZonedDateTimeType type = ZonedDateTimeType.instance();
        Object o = ZonedDateTime.parse("2019-10-31T03:52:46.267346Z[Etc/UTC]");
        assertEquals(ZonedDateTime.parse("2019-10-31T03:52:46.267346Z[Etc/UTC]"), type.cast(o));
        assertTrue(type.cast(o) instanceof ZonedDateTime);
    }
}
