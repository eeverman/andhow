package org.yarnandtail.andhow.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author ericeverman
 */
public class TextUtilTest {
	ByteArrayOutputStream baos;
	PrintStream ps;
	private static String FIND_INSTANCE_STRING_BROWNFOX = "The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog";

	@Before
	public void setup() {
		baos = new ByteArrayOutputStream();
		ps = new PrintStream(baos) {
			
			//Hack to prevent newlines at the end of the lines to make testing easier
			@Override
			public void println(Object x) {
				super.print(x);
			}
			
			@Override
			public void println(String x) {
				super.print(x);
			}
			
		};
	}
	
	protected String getStreamContent() {
		return new String(baos.toByteArray(), StandardCharsets.UTF_8);
	}
	
	@Test
	public void testFormat() {
		assertEquals("abcXXXxyz", TextUtil.format("abc{}xyz", "XXX"));
		assertEquals("abcXXXxyz", TextUtil.format("abc{}xyz", "XXX", "YYY", "ZZZ"));
		assertEquals("AAAabcBBBxyzCCC", TextUtil.format("{}abc{}xyz{}", "AAA", "BBB", "CCC"));
		assertEquals("AAAabcBBBxyzCCC", TextUtil.format("{}abc{}xyz{}", "AAA", "BBB", "CCC"));
		assertEquals("abc\\{}xyz", TextUtil.format("abc\\{}xyz", "XXX"));
		
		//Some more edge cases
		assertEquals("abc[[NULL]]xyz", TextUtil.format("abc{}xyz", (String)null));
		assertEquals("abc[[NULL]]xyz", TextUtil.format("abc{}xyz", null, null, null));
		assertEquals("abcXXXdef[[NULL]]xyz", TextUtil.format("abc{}def{}xyz", "XXX", null, null));
	}
	
	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testFormatException() {
		assertEquals("abcXXXxyz", TextUtil.format("abc{}xyz{}", "XXX"));
	}


	@Test
	public void testSimplePatternsPrintln() {
		TextUtil.println(ps, "abc{}xyz", "XXX");
		assertEquals("abcXXXxyz", getStreamContent());
		
	}
	
	@Test
	public void testPatternsWithCurlyBraceLiteralsPrintln() {
		TextUtil.println(ps, "abc\\{}xyz", "XXX");
		assertEquals("abc\\{}xyz", getStreamContent());
	}

	@Test
	public void testPatternWithBreak() {
		TextUtil.println(ps, 5, "//", "abc{}def {}xyz", "XXX", "YYY");
		assertEquals("//abcXXXdef//  YYYxyz", getStreamContent());
	}
	
	@Test
	public void testRepeat() {
		assertEquals("=====", TextUtil.repeat("=", 5));
		assertEquals(".....", TextUtil.repeat(".", 5));
		assertEquals("abab", TextUtil.repeat("ab", 2));
	}
	
	@Test
	public void testTrimToNull() {
		assertEquals("bob", TextUtil.trimToNull("bob"));
		assertEquals("carl", TextUtil.trimToNull("  carl\t "));
		assertNull(TextUtil.trimToNull("   \t "));
		assertNull(TextUtil.trimToNull(""));
		assertNull(TextUtil.trimToNull(null));
	}
	
	@Test
	public void testTrimToEmpty() {
		assertEquals("bob", TextUtil.trimToEmpty("bob"));
		assertEquals("carl", TextUtil.trimToEmpty("  carl\t "));
		assertEquals("", TextUtil.trimToEmpty("   \t "));
		assertEquals("", TextUtil.trimToEmpty(""));
		assertEquals("", TextUtil.trimToEmpty(null));
	}
	
	@Test
	public void testPadRight() {
		assertEquals("abcXX", TextUtil.padRight("abc", "X", 5));
		assertEquals("abc", TextUtil.padRight("abc", "X", 2));
		assertEquals("abc", TextUtil.padRight("abc", "X", -99));
		assertEquals("abcAB", TextUtil.padRight("abc", "ABCDEFG", 5));
		assertEquals("abc  ", TextUtil.padRight("abc", null, 5));
		assertNull(TextUtil.padRight(null, "ABCDEFG", 5));
	}
	
	@Test
	public void testToBoolean() {
		assertEquals(true, TextUtil.toBoolean("True"));
		assertEquals(true, TextUtil.toBoolean("TRUE"));
		assertEquals(true, TextUtil.toBoolean("YES"));
		assertEquals(true, TextUtil.toBoolean("yEs"));
		assertEquals(true, TextUtil.toBoolean("on"));
		assertEquals(true, TextUtil.toBoolean("oN"));
		assertEquals(true, TextUtil.toBoolean("t"));
		assertEquals(true, TextUtil.toBoolean("Y"));
		
		//
		assertEquals(false, TextUtil.toBoolean("false"));
		assertEquals(false, TextUtil.toBoolean("No"));
		assertEquals(false, TextUtil.toBoolean(""));
		assertEquals(false, TextUtil.toBoolean(".asef3"));
		assertEquals(false, TextUtil.toBoolean(null));
	}
	
	@Test
	public void testWrapWithObviousBreakLocations() {
		
		List<String> result;
		
		//......................12345678901234567890123456789012345678901234567890
		result = TextUtil.wrap("abcdefghij klmnopqrstuvwxyz", 10);
		assertEquals(2, result.size());
		assertEquals("abcdefghij", result.get(0));
		assertEquals("klmnopqrstuvwxyz", result.get(1));
		
		//......................12345678901234567890123456789012345678901234567890
		result = TextUtil.wrap("abcdefghijk lmnopqrstuvwxyz", 10);
		assertEquals(2, result.size());
		assertEquals("abcdefghijk", result.get(0));
		assertEquals("lmnopqrstuvwxyz", result.get(1));
		
		//......................12345678901234567890123456789012345678901234567890
		result = TextUtil.wrap("abcdefghijk lmnopqrstuvwxyzabcdefghijklmnop", 10);
		assertEquals(2, result.size());
		assertEquals("abcdefghijk", result.get(0));
		assertEquals("lmnopqrstuvwxyzabcdefghijklmnop", result.get(1));
		
		//......................12345678901234567890123456789012345678901234567890
		result = TextUtil.wrap("abcdefghijk lmnopqrstuvwxyzabcdefghijklmnop qrs tuv", 10);
		assertEquals(3, result.size());
		assertEquals("abcdefghijk", result.get(0));
		assertEquals("lmnopqrstuvwxyzabcdefghijklmnop", result.get(1));
		assertEquals("qrs tuv", result.get(2));
	}
	

	@Test
	public void testWrapWithCompromiseBreakLocations() {
		
		List<String> result;
		
		//......................12345678901234567890123456789012345678901234567890
		result = TextUtil.wrap("abcdefgh ijklmnopqrstuvwxyzabcdefgh", 10);
		assertEquals(2, result.size());
		assertEquals("abcdefgh", result.get(0));
		assertEquals("ijklmnopqrstuvwxyzabcdefgh", result.get(1));
		
		//......................12345678901234567890123456789012345678901234567890
		result = TextUtil.wrap("abcdefgh ijklmnopqrstuvwxyz abcdefgh ijklmnopqrstuv", 10);
		assertEquals(4, result.size());
		assertEquals("abcdefgh", result.get(0));
		assertEquals("ijklmnopqrstuvwxyz", result.get(1));
		assertEquals("abcdefgh", result.get(2));
		assertEquals("ijklmnopqrstuv", result.get(3));
		
		//In this one the first chunk is just a little too short to justify an
		//early break.
		//......................12345678901234567890123456789012345678901234567890
		result = TextUtil.wrap("abcdefg hijklmnopqrstuvwxyzabcdefgh", 10);
		assertEquals(1, result.size());
		
		//A worst case scenario
		//......................12345678901234567890123456789012345678901234567890
		result = TextUtil.wrap("abcdefg hijklmnopqrstuvwxyzabcdefgh i", 10);
		assertEquals("abcdefg hijklmnopqrstuvwxyzabcdefgh", result.get(0));
		assertEquals("i", result.get(1));
		
	}

	@Test
	public void testWrapWithEmptyString() {
		List<String> result;

		result = TextUtil.wrap("", 10);
		assertEquals(0, result.size());
	}

	@Test
	public void testWrapWithNewlineChar() {
		List<String> result;

		result = TextUtil.wrap("\nabc", 10);
		assertEquals(1, result.size());
		assertEquals("abc", result.get(0));

		result = TextUtil.wrap("abc \ndefg hijklmnopqrstuvwxyzabcdefgh", 10);
		assertEquals(2, result.size());
		assertEquals("abc", result.get(0));
		assertEquals("defg hijklmnopqrstuvwxyzabcdefgh", result.get(1));
	}

	@Test
	public void testWrapWithPrefix() {
		
		List<String> result;
		
		//......................12345678901234567890123456789012345678901234567890
		result = TextUtil.wrap("abcdefghij klmnopqrstuvwxyz", 10, "# ", "xxx");
		assertEquals(2, result.size());
		assertEquals("# abcdefghij", result.get(0));
		assertEquals("# xxxklmnopqrstuvwxyz", result.get(1));
		
		//......................12345678901234567890123456789012345678901234567890
		result = TextUtil.wrap("abcdefghijk lmnopqrstuvwxyz", 10, "# ", "xxx");
		assertEquals(2, result.size());
		assertEquals("# abcdefghijk", result.get(0));
		assertEquals("# xxxlmnopqrstuvwxyz", result.get(1));
		
		//......................12345678901234567890123456789012345678901234567890
		result = TextUtil.wrap("abcdefghijk lmnopqrstuvwxyzabcdefghijklmnop qrs tuv", 10, "# ", "xxx");
		assertEquals(3, result.size());
		assertEquals("# abcdefghijk", result.get(0));
		assertEquals("# xxxlmnopqrstuvwxyzabcdefghijklmnop", result.get(1));
		assertEquals("# xxxqrs tuv", result.get(2));
		
	}

	@Test
	public void testWrapNullPrefix() {
		List<String> result;

		result = TextUtil.wrap("abcdefghij klmnopqrstuvwxyz", 10, null, "xxx");
		assertEquals(2, result.size());
		assertEquals("abcdefghij", result.get(0));
		assertEquals("xxxklmnopqrstuvwxyz", result.get(1));
	}

	@Test
	public void testWrapNullLineIndent() {
		List<String> result;

		result = TextUtil.wrap("abcdefghij klmnopqrstuvwxyz", 10, "# ", null);
		assertEquals(2, result.size());
		assertEquals("# abcdefghij", result.get(0));
		assertEquals("# klmnopqrstuvwxyz", result.get(1));
	}
	
	@Test
	public void testEscapeXml() {
		assertEquals("&lt;some text&gt;", TextUtil.escapeXml("<some text>"));
		assertEquals("&lt;some&amp;text&quot;", TextUtil.escapeXml("<some&text\""));
		assertEquals("&apos;tis the s&apos;son", TextUtil.escapeXml("'tis the s'son"));

		assertNull(TextUtil.escapeXml(null));
		assertEquals("copyrighted&#169;", TextUtil.escapeXml("copyrighted©"));
	}

	/**
	 * Test of nullToEmpty method, of class TextUtil.
	 */
	@Test
	public void testNullToEmpty() {
		assertEquals("", TextUtil.nullToEmpty(null));
		assertEquals("", TextUtil.nullToEmpty(""));		//no change
		assertEquals("a", TextUtil.nullToEmpty("a"));	//no change
	}

	@Test
	public void testFindFirstInstanceOf() {
		assertEquals(31, TextUtil.findFirstInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 3, "the", "lazy"));
		assertEquals(40, TextUtil.findFirstInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 6, "dog"));
		assertEquals(20, TextUtil.findFirstInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, -1, "jumps", "over"));
		assertEquals(0, TextUtil.findFirstInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 0, "The", "fox"));
		assertEquals(65, TextUtil.findFirstInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 30, "jumps", "over"));
		assertEquals(49, TextUtil.findFirstInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 30, "brown", "quick"));
	}

	@Test
	public void testFindFirstInstanceOfNotFound() {
		assertEquals(-1, TextUtil.findFirstInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 80, "jumps", "over"));
		assertEquals(-1, TextUtil.findFirstInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 44, "dog."));
		assertEquals(-1, TextUtil.findFirstInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 0, "excuse"));
	}

	@Test
	public void testFindFirstInstanceOfNullToBeSearched() {
		assertEquals(-1, TextUtil.findFirstInstanceOf(null, 10, " ", "\t", "-"));
	}

	@Test
	public void testFindFirstInstanceOfWithEmptyToBeFound() {
		assertEquals(-1, TextUtil.findFirstInstanceOf("abcd", 10));
	}

	@Test
	public void testFindLastInstanceOf() {
		assertEquals(35, TextUtil.findLastInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 50, "the", "lazy"));
		assertEquals(85, TextUtil.findLastInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, FIND_INSTANCE_STRING_BROWNFOX.length(), "dog"));
		assertEquals(16, TextUtil.findLastInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 43, "The", "fox"));
		assertEquals(26, TextUtil.findLastInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 40, "jumps", "over"));
		assertEquals(10, TextUtil.findLastInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 40, "brown", "quick"));
	}

	@Test
	public void testFindLastInstanceOfNotFound() {
		assertEquals(-1, TextUtil.findLastInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, -1, "jumps", "over"));
		assertEquals(-1, TextUtil.findLastInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 15, "lazy"));
		assertEquals(-1, TextUtil.findLastInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, 0, "quick"));
		assertEquals(-1, TextUtil.findLastInstanceOf(FIND_INSTANCE_STRING_BROWNFOX, FIND_INSTANCE_STRING_BROWNFOX.length(), "notfound"));
	}


	@Test
	public void testFindLastInstanceOfNullToBeSearched() {
		assertEquals(-1, TextUtil.findLastInstanceOf(null, 10, " ", "\t", "-"));
	}

	@Test
	public void testFindLastInstanceOfWithEmptyToBeFound() {
		assertEquals(-1, TextUtil.findLastInstanceOf("abcd", 10));
	}
}
