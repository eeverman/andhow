package org.yarnandtail.andhow.sample;

import java.util.ArrayList;
import java.util.List;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * A line of text, which may need to be wrapped and/or commented.
 */
public abstract class TextLine {
	
	/**
	 * Indicates if this line should be wrapped if too long.
	 * The caller must then decide to actually do the wrapping by calling the
	 * 'wrapped' methods.  This is tri-state:  null means default to no-wrap, but
	 * if embedded in a TextBlock, use the TB setting.
	 */
	protected Boolean wrap;

	abstract String getLine(PrintFormat format);

	/**
	 * Write this single line as a line comment.
	 *
	 * The comment marker and the content of the line are separated by the
	 * format.lineCommentPrefixSeparator.
	 * Calling this on a format that doesn't have line comments (eg xml)
	 * results in a line that is not a comment.
	 *
	 * @param format
	 * @return
	 */
	abstract String getLineComment(PrintFormat format);

	/**
	 * Within a comment block, write this single line as a block comment.
	 *
	 * Calling this on a format that doesn't have block comments (eg properties files)
	 * result in uncommented text.
	 *
	 * @param format
	 * @param startComment If true, this is the first block line, so write block start.
	 * @param endComment If true, this is the last block line, so write block end.
	 * @return
	 */
	abstract String getBlockComment(PrintFormat format, boolean startComment, boolean endComment);

	/**
	 * Wrap a line to the max line length.
	 *
	 * @param format
	 * @return
	 */
	abstract List<String> getWrappedLine(PrintFormat format);

	/**
	 * Wrap a line and format it as a line comment (not a block comment)
	 *
	 * Calling this on a format that doesn't have line comments (eg xml)
	 * results in a line that is not a comment.
	 * @param format
	 * @return
	 */
	abstract List<String> getWrappedLineComment(PrintFormat format);

	/**
	 * Within a comment block, write this line and wrap it if it is too long.
	 *
	 * Calling this on a format that doesn't have block comments (eg properties files)
	 * result in uncommented text.
	 *
	 * @param format
	 * @param startComment If true, this is the first block line, so write block start.
	 * @param endComment If true, this is the last block line, so write block end.
	 * @return
	 */
	abstract List<String> getWrappedBlockComment(PrintFormat format, boolean startComment, boolean endComment);
	
	/**
	 * A line of text.
	 */
	public static class StringLine extends TextLine {
		String line;
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
			return
					TextUtil.nullToEmpty(format.lineCommentPrefix) +
					TextUtil.nullToEmpty(format.lineCommentPrefixSeparator) + line;
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
			return
					TextUtil.nullToEmpty(format.lineCommentPrefix) +
					TextUtil.nullToEmpty(format.lineCommentPrefixSeparator) +
					"";
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
			lines.add(
					TextUtil.nullToEmpty(format.lineCommentPrefix) +
					TextUtil.nullToEmpty(format.lineCommentPrefixSeparator) + "");
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
}
