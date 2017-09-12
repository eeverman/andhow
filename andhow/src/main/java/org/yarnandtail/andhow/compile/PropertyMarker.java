package org.yarnandtail.andhow.compile;

/**
 *
 * @author ericeverman
 */
public class PropertyMarker {

	private boolean builder = false;
	private boolean build = false;
	private boolean direct = false;
	
	public void markBuilder() {
		builder = true;
	}
	
	public void markBuild() {
		build = true;
	}
	
	public void markDirectConstruc() {
		direct = true;
	}

	public boolean isProperty() {
		return builder && build || direct;
	}

}
