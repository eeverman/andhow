package org.yarnandtail.andhow.property;

import org.yarnandtail.andhow.util.TextUtil;
import org.yarnandtail.andhow.api.Trimmer;

/**
 * This trimmer trims to null.
 * 
 * This is the default for all non-String Properties.  String properties default
 * to using the QuotedSpacePreservingTrimmer to allow whitespace to be preserved.
 * 
 * A few examples of trimming behavior, using ... to represent whitespace and
 * :: to separate the raw value on the left from the trimmed value on the right.
 * <ul>
 * <li>... :: [[null]] (An all whitespace raw value is trimmed to null)
 * <li>[[null]] :: [[null]]
 * <li>...abc... :: abc (whitespace on either side of text removed)
 * <li>..."...abc..."... :: "...abc..." (No special handing for quotes)
 * </ul>
 * 
 * @author ericeverman
 */
public class TrimToNullTrimmer implements Trimmer {
	
	static final TrimToNullTrimmer instance = new TrimToNullTrimmer();
	
	public static TrimToNullTrimmer instance() {
		return instance;
	}
	
	@Override
	public String trim(String raw) {
		return  TextUtil.trimToNull(raw);
	}
}
