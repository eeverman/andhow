/*
 */
package org.yarnandtail.andhow.sample;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.util.TextUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ericeverman
 */
public class TextLineTest {
	

	@Test
	public void testBlankLineSubclass() {
		PrintFormat pf =  new PropFileFormat();
		TextLine tl = new TextLine.BlankLine();

		assertEquals("", tl.getLine(pf));
		assertEquals("# ", tl.getLineComment(pf));
		
		assertEquals("", tl.getBlockComment(pf, true, true));
		assertEquals("", tl.getBlockComment(pf, true, false));
		assertEquals("", tl.getBlockComment(pf, false, true));
		assertEquals("", tl.getWrappedLine(pf).get(0));
		assertEquals("# ", tl.getWrappedLineComment(pf).get(0));
		assertEquals("", tl.getWrappedBlockComment(pf, true, true).get(0));
		assertEquals("", tl.getWrappedBlockComment(pf, true, false).get(0));
		assertEquals("", tl.getWrappedBlockComment(pf, false, true).get(0));
			
	}
	
	@Test
	public void testHRLineSubclass() {
		PrintFormat pf =  new PropFileFormat();
		TextLine tl = new TextLine.HRLine();

		assertEquals(TextUtil.repeat("##", 45), tl.getLine(pf));
		assertEquals("# " + TextUtil.repeat("##", 45), tl.getLineComment(pf));
		
		assertEquals(TextUtil.repeat("##", 45), tl.getBlockComment(pf, true, true));
		assertEquals(TextUtil.repeat("##", 45), tl.getBlockComment(pf, true, false));
		assertEquals(TextUtil.repeat("##", 45), tl.getBlockComment(pf, false, true));
		assertEquals(TextUtil.repeat("##", 45), tl.getWrappedLine(pf).get(0));
		assertEquals("# " + TextUtil.repeat("##", 45), tl.getWrappedLineComment(pf).get(0));
		assertEquals(TextUtil.repeat("##", 45), tl.getWrappedBlockComment(pf, true, true).get(0));
		assertEquals(TextUtil.repeat("##", 45), tl.getWrappedBlockComment(pf, true, false).get(0));
		assertEquals(TextUtil.repeat("##", 45), tl.getWrappedBlockComment(pf, false, true).get(0));
			
	}
	
	@Test
	public void testStringLineSubclass() {
		PrintFormat pf =  new PropFileFormat();
		TextLine tl = new TextLine.StringLine(TextUtil.repeat("abc ", 24), true);

		assertEquals(TextUtil.repeat("abc ", 24), tl.getLine(pf));
		assertEquals("# " + TextUtil.repeat("abc ", 24), tl.getLineComment(pf));
		
		assertEquals(TextUtil.repeat("abc ", 24), tl.getBlockComment(pf, true, true));
		assertEquals(TextUtil.repeat("abc ", 24), tl.getBlockComment(pf, true, false));
		assertEquals(TextUtil.repeat("abc ", 24), tl.getBlockComment(pf, false, true));
		assertEquals(TextUtil.repeat("abc ", 21) + "abc", tl.getWrappedLine(pf).get(0));
		assertEquals("\tabc abc", tl.getWrappedLine(pf).get(1));
		assertEquals("# " + TextUtil.repeat("abc ", 21) + "abc", tl.getWrappedLineComment(pf).get(0));
		assertEquals("# " + "\tabc abc", tl.getWrappedLineComment(pf).get(1));
		assertEquals(TextUtil.repeat("abc ", 21) + "abc", tl.getWrappedBlockComment(pf, true, true).get(0));
		assertEquals("\tabc abc", tl.getWrappedBlockComment(pf, true, true).get(1));
		assertEquals(TextUtil.repeat("abc ", 21) + "abc", tl.getWrappedBlockComment(pf, true, false).get(0));
		assertEquals("\tabc abc", tl.getWrappedBlockComment(pf, true, false).get(1));
		assertEquals(TextUtil.repeat("abc ", 21) + "abc", tl.getWrappedBlockComment(pf, false, true).get(0));
		assertEquals("\tabc abc", tl.getWrappedBlockComment(pf, false, true).get(1));	
	}
	
}
