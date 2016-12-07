package yarnandtail.andhow;

import java.io.PrintStream;

/**
 * Text utility used for formatting output for reports, auto generated descriptions
 * and Problems.
 * 
 * @author ericeverman
 */
public class TextUtil {
	
	public static final String NULL_PRINT = "[[NULL]]";
	
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
	 * print to the passed PrintStream using the format {} patterning.
	 * @param out
	 * @param pattern
	 * @param args 
	 */
	public static void print(PrintStream out, String pattern, Object... args) {
		out.print(format(pattern, args));
	}
	
	/**
	 * println a Horizontal rule (separator) to the passed PrintStream
	 * @param out
	 * @param pattern
	 * @param args 
	 */
	public static void printlnHr(PrintStream out) {
		out.println(repeat("=", 80));
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
}
