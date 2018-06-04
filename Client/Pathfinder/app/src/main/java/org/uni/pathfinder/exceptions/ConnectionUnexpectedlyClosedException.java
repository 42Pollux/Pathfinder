package org.uni.pathfinder.exceptions;

import android.util.Log;


public class ConnectionUnexpectedlyClosedException extends Exception {

	public ConnectionUnexpectedlyClosedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
		Log.d("DEBUG1", "Connection closed (" + message + ")");
	}

}
