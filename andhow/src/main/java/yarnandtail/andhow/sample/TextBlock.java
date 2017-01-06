package yarnandtail.andhow.sample;

import java.util.ArrayList;

/**
 *
 * @author ericeverman
 */
public class TextBlock {
	
	private boolean wrap;
	private boolean comment;
	private boolean blankLineBefore = false;
	private boolean blankLineAfter = false;
	private ArrayList<TextLine> lines = new ArrayList();

	public TextBlock(boolean wrap, boolean comment) {
		this.wrap = wrap;
		this.comment = comment;
	}

	public TextBlock(boolean wrap, boolean comment, boolean blankLineBefore, boolean blankLineAfter) {
		this(wrap, comment);
		this.blankLineBefore = blankLineBefore;
		this.blankLineAfter = blankLineAfter;
	}

	public TextBlock addLine(TextLine line) {
		lines.add(line);
		if (line.wrap == null) {
			line.wrap = wrap;
		}
		return this;
	}

	public TextBlock addLine(String strLine) {
		TextLine.StringLine line = new TextLine.StringLine(strLine, wrap);
		lines.add(line);
		return this;
	}

	public TextBlock addHR() {
		lines.add(new TextLine.HRLine());
		return this;
	}

	public TextBlock addBlank() {
		lines.add(new TextLine.BlankLine());
		return this;
	}

	public boolean isWrap() {
		return wrap;
	}

	public boolean isComment() {
		return comment;
	}

	public ArrayList<TextLine> getLines() {
		return lines;
	}

	public TextLine getLine(int i) {
		return lines.get(i);
	}
	
	public boolean isBlankLineBefore() {
		return blankLineBefore;
	}

	public boolean isBlankLineAfter() {
		return blankLineAfter;
	}
	
	public void setBlankLineBefore(boolean blankLineBefore) {
		this.blankLineBefore = blankLineBefore;
	}

	public void setBlankLineAfter(boolean blankLineAfter) {
		this.blankLineAfter = blankLineAfter;
	}
	
}
