package yarnandtail.andhow;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author ericeverman
 */
public class TextUtilTest {
	ByteArrayOutputStream baos;
	PrintStream ps;
	
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
		assertEquals("abc[[NULL]]xyz", TextUtil.format("abc{}xyz", null));
		assertEquals("abc[[NULL]]xyz", TextUtil.format("abc{}xyz", null, null, null));
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
	
}
