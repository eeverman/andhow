package org.yarnandtail.andhow.sample;

import java.util.ArrayList;
import java.util.List;
import org.yarnandtail.andhow.util.TextUtil;

/**
 *
 * @author ericeverman
 */
public abstract class TextLine {
	
	/**
	 * Indicates if this line should be wrapped if too long.
	 * The caller must then decide to actually do the wrapping by calling the
	 * 'wrapped' methods.
	 */
	protected Boolean wrap;

	abstract String getLine(PrintFormat format);

	abstract String getLineComment(PrintFormat format);

	abstract String getBlockComment(PrintFormat format, boolean startComment, boolean endComment);

	abstract List<String> getWrappedLine(PrintFormat format);

	abstract List<String> getWrappedLineComment(PrintFormat format);

	abstract List<String> getWrappedBlockComment(PrintFormat format, boolean startComment, boolean endComment);
	
}
/**
 * A line of text.
 */
public static class StringLine extends TextLine {
	private String line;
	public StringLine(String line, boolean wrap) {
		this.line = line;
		this.wrap = wrap;
	}

	public StringLine(String line) {
		this.line = line;
		this.wrap = null;
	}

	@Override
	public String getLine(PrintFormat format) {
		return line;
	}

	@Override
	public String getLineComment(PrintFormat format) {
		return format.lineCommentPrefix + format.lineCommentPrefixSeparator + line;
	}

	@Override
	public String getBlockComment(PrintFormat format, boolean startComment, boolean endComment) {

		String out = line;

		if (startComment && format.blockCommentStart != null) {
			out = format.blockCommentStart + TextUtil.nullToEmpty(format.blockCommentSeparator) + out;
		}

		if (endComment && format.blockCommentEnd != null) {
			out = out + TextUtil.nullToEmpty(format.blockCommentSeparator) + format.blockCommentEnd;
		}

		return out;
	}

	@Override
	public List<String> getWrappedLine(PrintFormat format) {
		return TextUtil.wrap(line, format.lineWidth, "", format.secondLineIndent);
	}

	@Override
	public List<String> getWrappedLineComment(PrintFormat format) {
		return TextUtil.wrap(line, format.lineWidth, 
				TextUtil.nullToEmpty(format.lineCommentPrefix)
				+ TextUtil.nullToEmpty(format.lineCommentPrefixSeparator),
				format.secondLineIndent);
	}

	@Override
	public List<String> getWrappedBlockComment(PrintFormat format, boolean startComment, boolean endComment) {

		List<String> lines = TextUtil.wrap(line, format.lineWidth, "", format.secondLineIndent);

		if (startComment && endComment && lines.size() == 1) {
			lines.set(0, 
					TextUtil.nullToEmpty(format.blockCommentStart) 
							+ TextUtil.nullToEmpty(format.blockCommentSeparator)
							+ lines.get(0) 
							+ TextUtil.nullToEmpty(format.blockCommentSeparator)
							+ TextUtil.nullToEmpty(format.blockCommentEnd));
		} else {
			if (startComment && format.blockCommentStart != null) {
				lines.add(0, format.blockCommentStart);
			}

			if (endComment && format.blockCommentEnd != null) {
				lines.set(lines.size() - 1, lines.get(lines.size() - 1) + TextUtil.nullToEmpty(format.blockCommentSeparator) + format.blockCommentEnd);
			}
		}

		return lines;
	}

}

/**
 * A horizontal rule / separator line.
 */
public static class HRLine extends TextLine {
	public HRLine() {
		wrap = false;
	}

	@Override
	public String getLine(PrintFormat format) {
		return format.hr;
	}

	@Override
	public String getLineComment(PrintFormat format) {
		return TextUtil.nullToEmpty(format.lineCommentPrefix)
				+ TextUtil.nullToEmpty(format.lineCommentPrefixSeparator) + format.hr;
	}

	@Override
	public String getBlockComment(PrintFormat format, boolean startComment, boolean endComment) {

		String out = format.hr;

		if (startComment && format.blockCommentStart != null) {
			out = format.blockCommentStart + TextUtil.nullToEmpty(format.blockCommentSeparator) + out;
		}

		if (endComment && format.blockCommentEnd != null) {
			out = out + TextUtil.nullToEmpty(format.blockCommentSeparator) + format.blockCommentEnd;
		}

		return out;
	}

	@Override
	public List<String> getWrappedLine(PrintFormat format) {
		ArrayList<String> lines = new ArrayList();
		lines.add(format.hr);
		return lines;
	}

	@Override
	public List<String> getWrappedLineComment(PrintFormat format) {
		ArrayList<String> lines = new ArrayList();
		lines.add(
				TextUtil.nullToEmpty(format.lineCommentPrefix) 
				+ TextUtil.nullToEmpty(format.lineCommentPrefixSeparator)
				+ format.hr
		);
		return lines;
	}

	@Override
	public List<String> getWrappedBlockComment(PrintFormat format, boolean startComment, boolean endComment) {
		ArrayList<String> lines = new ArrayList();

		lines.add(getBlockComment(format, startComment, endComment));

		return lines;
	}
}


/**
 * A blank line will continue the comment or non-comment status of the block.
 * Use blankLineBefore/After of a TextBlock to get actual whitespace b/t
 * TextBlocks.
 */
public static class BlankLine extends TextLine {
	public BlankLine() {
		wrap = false;
	}

	@Override
	public String getLine(PrintFormat format) {
		return "";
	}

	@Override
	public String getLineComment(PrintFormat format) {
		return format.lineCommentPrefix + format.lineCommentPrefixSeparator + "";
	}

	@Override
	public String getBlockComment(PrintFormat format, boolean startComment, boolean endComment) {

		String out = "";


		if (startComment && format.blockCommentStart != null) {
			out = format.blockCommentStart + TextUtil.nullToEmpty(format.blockCommentSeparator) + out;
		}

		if (endComment && format.blockCommentEnd != null) {
			out = out + TextUtil.nullToEmpty(format.blockCommentSeparator) + format.blockCommentEnd;
		}

		return out;
	}

	@Override
	public List<String> getWrappedLine(PrintFormat format) {
		ArrayList<String> lines = new ArrayList();
		lines.add("");
		return lines;
	}

	@Override
	public List<String> getWrappedLineComment(PrintFormat format) {
		ArrayList<String> lines = new ArrayList();
		lines.add(format.lineCommentPrefix + format.lineCommentPrefixSeparator + "");
		return lines;
	}

	@Override
	public List<String> getWrappedBlockComment(PrintFormat format, boolean startComment, boolean endComment) {
		ArrayList<String> lines = new ArrayList();

		String out = "";

		if (startComment && format.blockCommentStart != null) {
			out = out + format.blockCommentStart + TextUtil.nullToEmpty(format.blockCommentSeparator);
		}

		if (endComment && format.blockCommentSeparator != null) {
			out = out + format.blockCommentSeparator + TextUtil.nullToEmpty(format.blockCommentEnd);
		}

		lines.add(out);

		return lines;
	}
}

