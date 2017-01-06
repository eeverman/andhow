/*
 */
package yarnandtail.andhow.load;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import yarnandtail.andhow.ConfigSamplePrinter;
import yarnandtail.andhow.GroupInfo;
import yarnandtail.andhow.Property;
import yarnandtail.andhow.PropertyGroup;
import static yarnandtail.andhow.util.ReportGenerator.DEFAULT_LINE_WIDTH;
import yarnandtail.andhow.util.TextUtil;
import yarnandtail.andhow.Validator;

/**
 *
 * @author ericeverman
 */
public abstract class BaseSamplePrinter implements ConfigSamplePrinter {
	
	//private static final String LINE_PREFIX = "# ";
	
	protected void printIfNotNull(PrintStream out, String line) {
		if (line != null) {
			out.println(line);
		}
	}
	
	abstract Format getFormat();
	
	protected void print(PrintStream out, TextBlock block, Format format) {
		
		if (block == null || block.getLines().size() == 0) return;
		
		boolean useBlockComment = false;
		boolean useLineComment = false;
		
		
		if (block.isComment()) {
			useLineComment = format.usesLineComments();
			useBlockComment = format.usesBlockComments();
		}
		
		if (useBlockComment) {
			
			if (block.getLines().size() == 1) {
				
				Line line = block.getLine(0);
				
				if (block.wrap) {
					List<String> lines = line.getWrappedBlockComment(format, true, true);
					lines.stream().forEachOrdered(l -> out.println(l));
				} else {
					out.println(line.getBlockComment(format, true, true));
				}
			
				
			} else {

				int rowToPrint = 0;
				
				while (rowToPrint < block.getLines().size()) {

					Line line = block.getLine(rowToPrint);
					boolean isFirstLine = rowToPrint == 0;
					boolean isLastLine = (rowToPrint == (block.getLines().size() - 1));
					

					//need to add the end comment on the last row
					if (line.wrap) {
						List<String> lines = line.getWrappedBlockComment(format, isFirstLine, isLastLine);
						lines.stream().forEachOrdered(l -> out.println(l));
					} else {
						String lineStr = line.getBlockComment(format, isFirstLine, isLastLine);
						out.println(lineStr);
					}

					rowToPrint++;
				}
			}
			
		} else if (useLineComment) {
			
			for (Line line : block.getLines()) {
				if (line.wrap) {
					List<String> lines = line.getWrappedLineComment(format);
					lines.stream().forEachOrdered(l -> out.println(l));
				} else {
					out.println(line.getLineComment(format));
				}
			}
						
		} else {
			
			for (Line line : block.getLines()) {
				
				if (line instanceof HRLine) {
					if (format.usesBlockComments()) {
						out.println(line.getBlockComment(format, true, true));
					} else {
						out.println(line.getLineComment(format));
					}
				} else if (line.wrap) {
					List<String> lines = line.getWrappedLine(format);
					lines.stream().forEachOrdered(l -> out.println(l));
				} else {
					out.println(line.getLine(format));
				}
			}
			
		}
	}
	
	public abstract TextBlock getSampleFileStart();
	
	/**
	 * Must provide its own line comment prefix if the format has no block comment.
	 * @return 
	 */
	public abstract TextBlock getSampleStartComment();
	
	public abstract TextBlock getActualProperty(Class<? extends PropertyGroup> group, Property prop) throws Exception;
	
	public abstract String getSampleFileEnd();

	@Override
	public void printSampleStart(PrintStream out) {
		print(out, getSampleFileStart(), getFormat());
		print(out, getSampleStartComment(), getFormat());
	}
	
	@Override
	public void printPropertyGroupStart(PrintStream out, Class<? extends PropertyGroup> group) {
		
		TextBlock tb = new TextBlock(true, true);
		tb.addBlank();
		tb.addHR();
		
		String name = null;
		String desc = null;
		
		GroupInfo groupDesc = group.getAnnotation(GroupInfo.class);
		if (groupDesc != null) {
			name = TextUtil.trimToNull(groupDesc.name());
			desc = TextUtil.trimToNull(groupDesc.desc());
		}
		
		if (name != null || desc != null) {
			if (name != null && desc != null) {
				
				if (! desc.endsWith(".")) desc = desc + ".";
				
				tb.addLine(TextUtil.format("Property Group '{}' - {}  Defined in interface {}", 
						name, desc, group.getCanonicalName()));

			} else {
				tb.addLine(TextUtil.format("Property Group {}", group.getCanonicalName()));
				tb.addLine(TextUtil.format("Description: {}", (name != null)?name:desc));
			}
			
		} else {
			tb.addLine(TextUtil.format("Property Group {}", group.getCanonicalName()));
		}
		
		print(out, tb, getFormat());
		
	}
	
	
	@Override
	public void printProperty(PrintStream out, Class<? extends PropertyGroup> group,
			Property<?> prop) throws Exception {
		
		TextBlock tb = new TextBlock(true, true);
		tb.addBlank();

		
		String propFieldName = PropertyGroup.getFieldName(group, prop);
		
		tb.addLine(TextUtil.format("{} ({}) {}{}", 
				propFieldName,
				prop.getValueType().getDestinationType().getSimpleName(),
				(prop.isRequired())?ConfigSamplePrinter.REQUIRED_KEYWORD:"",
				(TextUtil.trimToNull(prop.getShortDescription()) == null)?"":" - " + prop.getShortDescription()));
		
		if (prop.getDefaultValue() != null) {
			tb.addLine(DEFAULT_VALUE_TEXT + ": " + prop.getDefaultValue());
		}
		
		if (TextUtil.trimToNull(prop.getHelpText()) != null) {
			tb.addLine(prop.getHelpText());
		}
		
		if (prop.getValidators().size() == 1) {
			tb.addLine(
				TextUtil.format(THE_VALUE_MUST_TEXT + " " + prop.getValidators().get(0).getTheValueMustDescription())
			);
		}
		
		if (prop.getValidators().size() > 1) {
			tb.addLine(THE_VALUE_MUST_TEXT + ":");	
			for (Validator v : prop.getValidators()) {
				tb.addLine("\t- " + v.getTheValueMustDescription());
			}
		}
		
		print(out, tb, getFormat());
		print(out, getActualProperty(group, prop), getFormat());
		
	}
	
	

	@Override
	public void printPropertyGroupEnd(PrintStream out, Class<? extends PropertyGroup> group) {
	}
	
	@Override
	public void printSampleEnd(PrintStream out) {
		out.println();
		
		printIfNotNull(out, getSampleFileEnd());
	}
	
	public static class Format {
		
		/** either block comments or lineComment should be non-null, but not both */
		public String blockCommentStart;
		public String blockCommentEnd;
		public String blockCommentSeparator = "\t"; //Use between the last text and the comment end if on same line
		public String lineCommentPrefix;
		public String lineCommentPrefixSeparator = " ";	//Separate the opening line comment from the text
		public String secondLineIndent;
		public String hr;
		public int lineWidth;
		
		public boolean usesLineComments() {
			return (TextUtil.trimToNull(lineCommentPrefix) != null);
		}
		
		public boolean usesBlockComments() {
			return ! usesLineComments();
		}
	}
	
	public abstract static class Line {
		protected Boolean wrap;

		abstract String getLine(Format format);
		abstract String getLineComment(Format format);
		abstract String getBlockComment(Format format, boolean startComment, boolean endComment);
		
		abstract List<String> getWrappedLine(Format format);
		abstract List<String> getWrappedLineComment(Format format);
		abstract List<String> getWrappedBlockComment(Format format, boolean startComment, boolean endComment);
	}
	
	public static class StringLine extends Line {
		String line;
		public StringLine(String line, boolean wrap) {
			this.line = line;
			this.wrap = wrap;
		}
		
		public StringLine(String line) {
			this.line = line;
			this.wrap = null;
		}
		
		public String getLine(Format format) {
			return line;
		}
		
		public String getLineComment(Format format) {
			return format.lineCommentPrefix + format.lineCommentPrefixSeparator + line;
		}
		
		public String getBlockComment(Format format, boolean startComment, boolean endComment) {
			
			String out = line;

				
			if (startComment) {
				out = format.blockCommentStart + format.blockCommentSeparator + out;
			}

			if (endComment) {
				out = out + format.blockCommentSeparator + format.blockCommentEnd;
			}
			
			return out;
		}
		
		public List<String> getWrappedLine(Format format) {
			return TextUtil.wrap(line, format.lineWidth, "", format.secondLineIndent);
		}
		
		public List<String> getWrappedLineComment(Format format) {
			return TextUtil.wrap(line, format.lineWidth, 
					format.lineCommentPrefix + format.lineCommentPrefixSeparator, format.secondLineIndent);
		}
		
		public List<String> getWrappedBlockComment(Format format, boolean startComment, boolean endComment) {
			
			List<String> lines = TextUtil.wrap(line, format.lineWidth, "", format.secondLineIndent);

			if (startComment && endComment && lines.size() == 1) {
				lines.set(0, format.blockCommentStart + format.blockCommentSeparator + 
						lines.get(0) + format.blockCommentSeparator + format.blockCommentEnd);
			} else {
				if (startComment) {
					lines.add(0, format.blockCommentStart);
				}
				
				if (endComment) {
					lines.set(lines.size() - 1, lines.get(lines.size() - 1) + format.blockCommentSeparator + format.blockCommentEnd);
				}
			}
			
			return lines;
		}

	}
	
	public static class HRLine extends Line {
		public HRLine() {
			wrap = false;
		}
		
		public String getLine(Format format) {
			return format.hr;
		}
		
		public String getLineComment(Format format) {
			return format.lineCommentPrefix + format.lineCommentPrefixSeparator + format.hr;
		}
		
		public String getBlockComment(Format format, boolean startComment, boolean endComment) {
			
			String out = format.hr;

				
			if (startComment) {
				out = format.blockCommentStart + format.blockCommentSeparator + out;
			}

			if (endComment) {
				out = out + format.blockCommentSeparator + format.blockCommentEnd;
			}
			
			return out;
		}
		
		public List<String> getWrappedLine(Format format) {
			ArrayList<String> lines = new ArrayList();
			lines.add(format.hr);
			return lines;
		}
		
		public List<String> getWrappedLineComment(Format format) {
			ArrayList<String> lines = new ArrayList();
			lines.add(format.lineCommentPrefix + format.lineCommentPrefixSeparator + format.hr);
			return lines;
		}
		
		public List<String> getWrappedBlockComment(Format format, boolean startComment, boolean endComment) {
			ArrayList<String> lines = new ArrayList();

			if (startComment && endComment) {
				lines.add(format.blockCommentStart + format.blockCommentSeparator + 
						format.hr + format.blockCommentSeparator + format.blockCommentEnd);
			} else {
				if (startComment) {
					lines.add(format.blockCommentStart + format.blockCommentSeparator + format.hr);
				}
				
				if (endComment) {
					lines.add(format.hr + format.blockCommentSeparator + format.blockCommentEnd);
				}
			}
			
			return lines;
		}
	}
	

	public static class BlankLine extends Line {
		public BlankLine() {
			wrap = false;
		}
		
		public String getLine(Format format) {
			return "";
		}
		
		public String getLineComment(Format format) {
			return format.lineCommentPrefix + format.lineCommentPrefixSeparator + "";
		}
		
		public String getBlockComment(Format format, boolean startComment, boolean endComment) {
			
			String out = "";

				
			if (startComment) {
				out = format.blockCommentStart + format.blockCommentSeparator + out;
			}

			if (endComment) {
				out = out + format.blockCommentSeparator + format.blockCommentEnd;
			}
			
			return out;
		}
		
		public List<String> getWrappedLine(Format format) {
			ArrayList<String> lines = new ArrayList();
			lines.add("");
			return lines;
		}
		
		public List<String> getWrappedLineComment(Format format) {
			ArrayList<String> lines = new ArrayList();
			lines.add(format.lineCommentPrefix + format.lineCommentPrefixSeparator + "");
			return lines;
		}
		
		public List<String> getWrappedBlockComment(Format format, boolean startComment, boolean endComment) {
			ArrayList<String> lines = new ArrayList();

			if (startComment && endComment) {
				lines.add(format.blockCommentStart + format.blockCommentSeparator + 
						"" + format.blockCommentSeparator + format.blockCommentEnd);
			} else {
				if (startComment) {
					lines.add(format.blockCommentStart + format.blockCommentSeparator + "");
				}
				
				if (endComment) {
					lines.add("" + format.blockCommentSeparator + format.blockCommentEnd);
				}
			}
			
			return lines;
		}
	}
	
	public static class TextBlock {
		boolean wrap;
		boolean comment;
		
		ArrayList<Line> lines = new ArrayList();
		
		public TextBlock(boolean wrap, boolean comment) {
			this.wrap = wrap;
			this.comment = comment;
		}
		
		public TextBlock addLine(Line line) {
			lines.add(line);
			
			if (line.wrap == null) {
				line.wrap = wrap;
			}
			
			return this;
		}
		
		public TextBlock addLine(String strLine) {
			StringLine line = new StringLine(strLine, wrap);
			lines.add(line);
			return this;
		}
		
		public TextBlock addHR() {
			lines.add(new HRLine());
			return this;
		}
		
		public TextBlock addBlank() {
			lines.add(new BlankLine());
			return this;
		}

		public boolean isWrap() {
			return wrap;
		}

		public boolean isComment() {
			return comment;
		}

		public ArrayList<Line> getLines() {
			return lines;
		}
		
		public Line getLine(int i) {
			return lines.get(i);
		}
		
		
		
		
	}
	
}
