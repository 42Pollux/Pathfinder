package org.uni.pathfinder.exceptions;

import android.util.Log;

public class ConnectionTimeoutException extends Exception {

	public ConnectionTimeoutException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
		Log.d("DEBUG1", "Connection timed out (" + message + ")");
	}

}
