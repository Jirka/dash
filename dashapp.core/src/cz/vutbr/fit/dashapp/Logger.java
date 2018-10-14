package cz.vutbr.fit.dashapp;

/**
 * 
 * @author Jiri Hynek
 *
 */
public class Logger {
	
	/**
	 * Method logs error and print exception.
	 * 
	 * @param message
	 * @param e
	 */
	public static void logError(String message, Exception e) {
		logError(message);
		e.printStackTrace();
	}

	/**
	 * Method logs error.
	 * 
	 * @param message
	 */
	public static void logError(String message) {
		System.err.println(message);
	}

}
