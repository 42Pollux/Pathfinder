/**
 * @author pollux
 *
 */
package com.pathfinder.pathfindertestclient.shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class XMLObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private int value;

    public XMLObject(int _value) {
        this.value = _value;
    }

    public int getValue() {
        return value;
    }


    // Serialization
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(value);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        value = in.readInt();
    }
}
