package yarnandtail.andhow;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Text utility used for formatting output for reports, auto generated descriptions
 * and Problems.
 * 
 * @author ericeverman
 */
public class TextUtil {
	
	public static final String NULL_PRINT = "[[NULL]]";
	public static final String SECOND_LINE_INDENT = "  ";
	
	
	private static final String PAD = "[[00PAD00]]";
	
	
	/**
	 * println to the passed PrintStream using the format {} patterning.
	 * @param out
	 * @param pattern
	 * @param args 
	 */
	public static void println(PrintStream out, String pattern, Object... args) {
		out.println(format(pattern, args));
	}
	
	/**
	 * println to the passed PrintStream using the format {} patterning and wrapping
	 * to a specified line length.
	 * 
	 * As each line is wrapped, linePrefix is added to the beginning of each line
	 * (this allows use as comments in things like properties files).
	 * 
	 * Additionally, each wrapped line is given SECOND_LINE_INDENT of indent after the prefix.
	 * 
	 * @param out
	 * @param maxLineLength Max characters to place on one line (best effort)
	 * @param linePrefix Each line will receive this prefix, which counts toward line length
	 * @param pattern Replacement pattern, using the format methods in this class
	 * @param args Arguments for replacement
	 */
	public static void println(PrintStream out, int maxLineLength, String linePrefix, String pattern, Object... args) {
		String content = format(pattern, args);
		List<String> chunks = wrap(content, maxLineLength, linePrefix, SECOND_LINE_INDENT);
		
		for (String s : chunks) {
			out.println(s);
		}
	}
	
	/**
	 * println to the passed PrintStream, wrapped to a specified line length.
	 * 
	 * As each line is wrapped, linePrefix is added to the beginning of each line
	 * (this allows use as comments in things like properties files).
	 * 
	 * Additionally, each wrapped line is given SECOND_LINE_INDENT of indent after the prefix.
	 * 
	 * @param out
	 * @param maxLineLength Max characters to place on one line (best effort)
	 * @param linePrefix Each line will receive this prefix, which counts toward line length
	 * @param content String to be split up.
	 */
	public static void println(PrintStream out, int maxLineLength, String linePrefix, String content) {
		List<String> chunks = wrap(content, maxLineLength, linePrefix, SECOND_LINE_INDENT);
		
		for (String s : chunks) {
			out.println(s);
		}
	}
	
	/**
	 * Creates a message with {} instances replaced with values from the args
	 * list, similar to how SLF4J formats messages.
	 * 
	 * LIMITATION:  \{} is recognized as escaping the {}, however, \\{} is not
	 * correctly recognized as preceding the {} with a '\' character.  In other
	 * words, the sequence '\{}' is the only exception that is looked for /
	 * handled.
	 * 
	 * @param pattern A string pattern with may contain {} references to items in the args list.
	 * @param args Values to call .toString on to fill in, in order, the {} refs.
	 * @return 
	 */
	public static String format(String pattern, Object... args) {
		
		//Its ambiguous after we split into tokens if there are start or end {}'s.
		//Adding padding solves the issue.
		String padded = PAD + pattern + PAD;
		
		//This regex starts with a negative backreference (?<!\\) meaning that
		//the matched expression must not follow the backslash literal.
		String[] tokens = padded.split("(?<!\\\\)(\\{})");
		
		if (tokens.length > 1) {
			
			StringBuilder sb = new StringBuilder();
			sb.append(tokens[0]);
			
			for (int i = 1; i < tokens.length; i++) {
				
				if (args != null && args[i - 1] != null) {
					sb.append(args[i - 1]);
				} else {
					sb.append(NULL_PRINT);
				}
				
				sb.append(tokens[i]);
			}
			
			return sb.substring(11, sb.length() - 11);
		} else {
			return pattern;
		}
	}
	
	public static String repeat(String base, int repeat) {
		return new String(new char[repeat]).replace("\0", base);
	}
	
	/**
	 * Pads the right side of the string with repeating repeatString's until the
	 * specified totalLength is reached.
	 * 
	 * If the initial base string is longer than the totalLength, it is not modified.
	 * If the repeatString is multi-character, the final string will be trimmed
	 * to the totalLength, thus, the repeat string may be partially truncated.
	 * 
	 * A null base string will return null.  A null repeatString will be treated
	 * as a single space.
	 * 
	 * @param base
	 * @param repeatString
	 * @param totalLength
	 * @return 
	 */
	public static String padRight(String base, String repeatString, int totalLength) {
		
		if (base == null) return null;
		
		if (base.length() < totalLength) {
			
			if (repeatString == null) repeatString = " ";
			
			StringBuilder sb = new StringBuilder(base);

			while (sb.length() < totalLength) {
				sb.append(repeatString);
			}
			
			return sb.substring(0, totalLength);
			
		} else {
			return base;
		}
		
		
	}
	
	public static String trimToNull(String source) {
		if (source == null) return null;
		
		source = source.trim();
		
		if (source.length() > 0) {
			return source;
		} else {
			return null;
		}
	}
	
	public static String trimToEmpty(String source) {
		if (source == null) {
			return "";
		} else {
			return source.trim();
		}
	}

	/**
	 * Parses a string to a boolean.
	 * 
	 * This implementation matches the Commons-lang documentation:
	 * <i>'true', 'on', 'y', 't' or 'yes' (case insensitive) will return true. Otherwise, false is returned.</i>
	 * 
	 * Null returns false.
	 * @param value
	 * @return
	 */
	public static boolean toBoolean(String value) {
		String v = trimToNull(value);
		
		if (v == null) return false;
		
		v = v.toLowerCase();
		
		return v.equals("true") || v.equals("t") || v.equals("yes") || v.equals("y") || v.equals("on");
	}
	
	/**
	 * Wraps text to be no longer than the specified length, if possible, and adds
	 * a prefix to each line, which is subtracted from the overall length.
	 * 
	 * Long words may continue on the same line - there is no guarentee that
	 * the text will be wrapped to be less than the length parameter.
	 * 
	 * The wrappedLineIndent is a character string that is added to each wrapped
	 * line (ie not the first line) after the prefix.  This allows multi-lines to
	 * be clearly readable in the code formatting way.  For simplicity, the
	 * wrappedLineIndent is not subtracted from the line length.
	 * 
	 * @param in
	 * @param length
	 * @param prefix
	 * @param wrappedLineIndent Added to each wrapped line after the prefix, not including the first line.
	 * @return 
	 */
	public static List<String> wrap(String in, int length, String prefix, String wrappedLineIndent) {
		if (prefix == null) {
			prefix = "";
		}
		
		if (wrappedLineIndent == null) {
			wrappedLineIndent = "";
		}
		
		List<String> result = wrap(in, length - prefix.length());
		
		for (int i = 0; i < result.size(); i++) {
			if (i == 0) {
				result.set(i, prefix + result.get(i));
			} else {
				result.set(i, prefix + wrappedLineIndent + result.get(i));
			}
		}
		
		return result;
	}
	
	
	/**
	 * Wraps text to be no longer than the specified length, if possible.
	 * 
	 * Long words may continue on the same line - there is no guarentee that
	 * the text will be wrapped to be less than the length parameter.
	 * @param in
	 * @param length
	 * @return 
	 */
	public static List<String> wrap(String in, int length) {
		
		in = in.trim();
		
		if (in.length() == 0) {
			return Collections.emptyList();
		} else {
			ArrayList<String> result = new ArrayList(2);

			if (in.length() < length) {
				result.add(in);
				return result;
			} else if (in.substring(0, length).contains("\n")) {
				result.addAll(wrap(in.substring(0, in.indexOf("\n")).trim(), length));
				result.addAll(wrap(in.substring(in.indexOf("\n") + 1).trim(), length));
				return result;
			} else {
				
				//look a little to the left for a break point first (20% of the characters to the left)
				
				//shortest chunk allowed to be left on a line so that a longer chunk
				//can be on the next line.
				//Avoid:  abc [wrap] asdfasdfasdfasdfasdfasdfasdfasdfasdf
				//Don't break after abc b/c its too short
				int allowedShortLength = (int)(.8D * length);
				
				int effectiveBreak = -1;
				
				int	leftBreak = findLastInstanceOf(in, length, " ", "\t", "-");
				int rightBreak = findFirstInstanceOf(in, length, " ", "\t", "-");
				
				if (leftBreak >= allowedShortLength) {
					effectiveBreak = leftBreak;
				} else {
					effectiveBreak = rightBreak;
				}
				
				
				if (effectiveBreak > -1) {
					result.add(in.substring(0, effectiveBreak).trim());
					result.addAll(wrap(in.substring(effectiveBreak + 1).trim(), length));
				} else {
					result.add(in);
				}
				
				return result;
			}
	
		}

	}
	
	/**
	 * Find the first occurrence of one of an array of possible strings in another string,
	 * starting at the specified position.
	 * 
	 * @param toBeSearched
	 * @param searchFrom Same symantics as String.indexOf(String, int)
	 * @param toBeFound
	 * @return 
	 */
	public static int findFirstInstanceOf(String toBeSearched, int searchFrom, String... toBeFound) {
		int result = -1;
		
		if (toBeSearched != null || toBeFound.length > 0) {
			for (String s : toBeFound) {
				int i = toBeSearched.indexOf(s, searchFrom);
				if (i > -1 && (i < result || result == -1)) {
					result = i;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param toBeSearched
	 * @param searchFrom Start looking from this position and to the left
	 * @param toBeFound
	 * @return 
	 */
	public static int findLastInstanceOf(String toBeSearched, int searchFrom, String... toBeFound) {
		int result = -1;
		
		if (toBeSearched != null || toBeFound.length > 0) {
			for (String s : toBeFound) {
				int i = toBeSearched.lastIndexOf(s, searchFrom);
				if (i > result) {
					result = i;
				}
			}
		}
		
		return result;
	}
}
