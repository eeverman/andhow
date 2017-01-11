package yarnandtail.andhow;

/**
 * An alternate name for a Property, either incoming (when read by a property
 * Loader) or outgoing (when properties are exported to some destination).
 *
 * @author ericeverman
 */
public class Alias {

	boolean in;
	boolean out;
	String name;
	
	public Alias(String name, boolean in, boolean out) {
		this.in = in;
		this.out = out;
		this.name = name;
	}
	

	public boolean isIn() {
		return in;
	}

	public boolean isOut() {
		return out;
	}

	public String getName() {
		return name;
	}

}
