package exceptions;

import helper.OSDepPrint;

public class ConnectionUnexpectedlyClosedException extends Exception {

	public ConnectionUnexpectedlyClosedException(String message, int thread) {
		super(message);
		// TODO Auto-generated constructor stub
		OSDepPrint.error("Connection closed (" + message + ")", thread);
	}

}
