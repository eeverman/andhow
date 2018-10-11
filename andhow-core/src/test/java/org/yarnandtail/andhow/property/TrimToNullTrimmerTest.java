package org.yarnandtail.andhow.property;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests TrimToNullTrimmer instances as they would be used in an app.
 *
 * @author shariktlt
 */
public class TrimToNullTrimmerTest {

    @Test
    public void testKnownVariants() {
        assertResult(null, "");
        assertResult(null, "          ");
        assertResult(null, null);
        assertResult("abc", "   abc    ");
        assertResult("a  b  c", "   a  b  c    ");
        assertResult("\"  abc  \"", "   \"  abc  \"   ");
    }

    /**
     * Shortcut of assertion call
     *
     * @param expected
     * @param source
     */
    private void assertResult(String expected, String source) {
        assertEquals(expected, TrimToNullTrimmer.instance().trim(source));
    }
}
