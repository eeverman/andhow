package org.yarnandtail.andhow.sample;

import java.util.ArrayList;

/**
 * A block of Text made up of multiple TextLines.
 */
public class TextBlock {
	
	private boolean wrap;
	private boolean comment;
	private boolean blankLineBefore = false;
	private boolean blankLineAfter = false;
	private ArrayList<TextLine> lines = new ArrayList();

	/**
	 * New instance
	 *
	 * @param wrap Should contained text be wrapped to the max column width?
	 * @param comment Should the text in this block be printed as a comment?
	 */
	public TextBlock(boolean wrap, boolean comment) {
		this.wrap = wrap;
		this.comment = comment;
	}

	/**
	 * Add a String, which will be added as TextLine with the wrap setting inherited from this block.
	 *
	 * @param strLine
	 * @return
	 */
	public TextBlock addLine(String strLine) {
		TextLine.StringLine line = new TextLine.StringLine(strLine, wrap);
		lines.add(line);
		return this;
	}

	/**
	 * Add a Horizontal Rule line, added as a TextLine.HRLine.
	 *
	 * @return
	 */
	public TextBlock addHR() {
		lines.add(new TextLine.HRLine());
		return this;
	}

	/**
	 * Add a blank line, added as a TextLine.BlankLine.
	 *
	 * @return
	 */
	public TextBlock addBlank() {
		lines.add(new TextLine.BlankLine());
		return this;
	}

	/**
	 * Should lines in this block be wrapped to the max column width?
	 *
	 * @return
	 */
	public boolean isWrap() {
		return wrap;
	}

	/**
	 * Should the text in this block be printed as a comment?
	 * @return
	 */
	public boolean isComment() {
		return comment;
	}

	/**
	 * Return all the TextLines in this block.
	 *
	 * @return
	 */
	public ArrayList<TextLine> getLines() {
		return lines;
	}

	/**
	 * Return a single TextLine
	 *
	 * @param i index is zero based.
	 * @return
	 */
	public TextLine getLine(int i) {
		return lines.get(i);
	}

	/**
	 * Should a blank line be printed before this block?
	 *
	 * @return
	 */
	public boolean isBlankLineBefore() {
		return blankLineBefore;
	}

	/**
	 * Should a blank line be printed after this block?
	 * @return
	 */
	public boolean isBlankLineAfter() {
		return blankLineAfter;
	}

	/**
	 * Specify if a blank line be printed before this block.
	 * @param blankLineBefore
	 */
	public void setBlankLineBefore(boolean blankLineBefore) {
		this.blankLineBefore = blankLineBefore;
	}

	/**
	 * Specify if a blank line be printed after this block.
	 * @param blankLineAfter
	 */
	public void setBlankLineAfter(boolean blankLineAfter) {
		this.blankLineAfter = blankLineAfter;
	}
	
}
