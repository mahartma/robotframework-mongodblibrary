package de.codecentric.robot.mongodblibrary;

/**
 * 
 * 
 * @author Max Hartmann
 *
 */
public class MongodbLibraryException extends RuntimeException {

	private static final long serialVersionUID = 42L;

	public MongodbLibraryException() {
		super();
	}

	public MongodbLibraryException(String message, Throwable cause) {
		super(message, cause);
	}

	public MongodbLibraryException(String message) {
		super(message);
	}

	public MongodbLibraryException(Throwable cause) {
		super(cause);
	}

}
