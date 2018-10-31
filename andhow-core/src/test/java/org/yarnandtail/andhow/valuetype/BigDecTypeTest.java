package org.yarnandtail.andhow.valuetype;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.yarnandtail.andhow.api.ParsingException;
import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Unit tests for BigDecType
 *
 * @author chace86
 */
public class BigDecTypeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private static final String PARSE_ERROR_MSG = "Unable to convert to a BigDecimal numeric value";
    private BigDecType type = BigDecType.instance();

    @Test
    public void testParse() throws ParsingException {
        assertNull(type.parse(null));
        assertEquals(BigDecimal.ZERO, type.parse("0"));
        assertEquals(BigDecimal.ONE, type.parse("1"));
        assertEquals(new BigDecimal("12.34567"), type.parse("12.34567"));
        assertEquals(new BigDecimal("-300.724578"), type.parse("-300.724578"));
    }

    @Test
    public void testParseEmpty() throws ParsingException {
        expectParsingException();
        assertFalse(type.isParsable(""));
        type.parse("");
    }

    @Test
    public void testParseNotANumber() throws ParsingException {
        expectParsingException();
        assertFalse(type.isParsable("apple"));
        type.parse("apple");
    }

    @Test
    public void testCast() {
        Object o = new BigDecimal("123.456");
        assertEquals(new BigDecimal("123.456"), o);
        assertNotNull(type.cast(o));
    }

    private void expectParsingException() {
        thrown.expect(ParsingException.class);
        thrown.expectMessage(PARSE_ERROR_MSG);
    }
}
