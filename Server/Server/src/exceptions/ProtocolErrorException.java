package exceptions;

import helper.OSDepPrint;

public class ProtocolErrorException extends Exception {

	public ProtocolErrorException(String message, int thread) {
		super(message);
		// TODO Auto-generated constructor stub
		OSDepPrint.error("Communication failed, unexcepted protocol behaviour (" + message + ")", thread);
	}

}
