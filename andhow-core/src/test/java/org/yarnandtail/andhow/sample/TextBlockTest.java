/*
 */
package org.yarnandtail.andhow.sample;

import org.junit.jupiter.api.Test;
import org.yarnandtail.andhow.util.TextUtil;

import static org.junit.jupiter.api.Assertions.*;

public class TextBlockTest {
	

	@Test
	public void testWrappedComment() {
		TextBlock tb = new TextBlock(true, true);
		tb.addBlank();
		tb.addLine(TextUtil.repeat("abc ", 24));
		tb.addHR();
		tb.addBlank();

		assertTrue(tb.getLine(0) instanceof TextLine.BlankLine);
		assertTrue(tb.getLine(1) instanceof TextLine.StringLine);
		assertTrue(tb.getLine(2) instanceof TextLine.HRLine);
		assertTrue(tb.getLine(3) instanceof TextLine.BlankLine);
		assertEquals(4, tb.getLines().size());

		assertFalse(tb.isBlankLineBefore());
		assertFalse(tb.isBlankLineAfter());

		tb.setBlankLineBefore(true);
		assertTrue(tb.isBlankLineBefore());

		tb.setBlankLineAfter(true);
		assertTrue(tb.isBlankLineAfter());

		assertTrue(tb.isComment());
		assertTrue(tb.isWrap());

	}


	@Test
	public void testUnwrappedNoncomment() {
		TextBlock tb = new TextBlock(false, false);

		assertFalse(tb.isComment());
		assertFalse(tb.isWrap());

	}

}
