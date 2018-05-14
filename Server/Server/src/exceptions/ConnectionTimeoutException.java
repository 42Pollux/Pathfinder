package exceptions;

import helper.OSDepPrint;

public class ConnectionTimeoutException extends Exception {

	public ConnectionTimeoutException(String message, int thread) {
		super(message);
		// TODO Auto-generated constructor stub
		OSDepPrint.error("Connection timed out (" + message + ")", thread);
	}

}
