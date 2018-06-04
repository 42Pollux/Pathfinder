package org.uni.pathfinder.exceptions;

import android.util.Log;


public class ProtocolErrorException extends Exception {

	public ProtocolErrorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
		Log.d("DEBUG1", "Communication failed, unexcepted protocol behaviour (" + message + ")");
	}

}
