package yarnandtail.andhow;

/**
 * An exception that causes configuration processing to stop b/c it cannot proceed.
 * 
 * @author eeverman
 */
public class FatalException extends RuntimeException {

	String fatalReason;
			
	public FatalException(Exception base, String fatalReason) {
		super(base);
		this.fatalReason = fatalReason;
	}
	
	public  String getFatalReason() {
		return fatalReason;
	}
}
