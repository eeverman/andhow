package org.yarnandtail.andhow.property;

import org.yarnandtail.andhow.Trimmer;
import org.yarnandtail.andhow.util.TextUtil;

/**
 * This trimmer trims to null and treats double quotes as whitespace
 * preserving markers.
 * 
 * This is the default for String Properties - Other property types should use
 * the TrimToNullTrimmer.
 * 
 * Whitespace is handled differently across source formats.  The QuotedSpacePreservingTrimmer
 * uses a quoted text convention that 'solves' whitespace in most situations.
 * If, after removing all beginning and ending whitespace,
 * the first and last characters are double quotes, those quotes are considered to
 * be whitespace preserving string delineators.  The quotes are stripped off
 * and the string inside the quotes, including whitespace, will be preserved as the
 * actual value.
 * 
 * A few examples of trimming behavior, using ... to represent whitespace and
 * :: to separate the raw value on the left from the trimmed value on the right.
 * <ul>
 * <li>... :: [[null]] (An all whitespace raw value is trimmed to null)
 * <li>[[null]] :: [[null]]
 * <li>...abc... :: abc (whitespace on either side of text removed)
 * <li>"...abc..." :: ...abc... (Quotes are removed and all characters inside preserved)
 * <li>..."...abc..."... :: ...abc... (same result - whitespace outside the quotes is removed
 * <li>...some "words" you said... :: some "words" you said (No special quote handling here)
 * <li>..".some "words" you said.".. :: .some "words" you said.
 * <li>.."".. :: [[Empty String]] (Using quotes, it is possible to assign an empty string)
 * </ul>
 * 
 * @author ericeverman
 */
public class QuotedSpacePreservingTrimmer implements Trimmer {
	
	static final QuotedSpacePreservingTrimmer instance = new QuotedSpacePreservingTrimmer();
	
	public static QuotedSpacePreservingTrimmer instance() {
		return instance;
	}
	
	@Override
	public String trim(String raw) {
		String s =  TextUtil.trimToNull(raw);

		if (s != null && s.length() > 1 && s.startsWith("\"") && s.endsWith("\"")) {
			return s.substring(1, s.length() - 1);
		} else {
			return s;
		}
	}
}
