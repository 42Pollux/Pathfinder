/**
 * @author pollux
 *
 */
package org.uni.pathfinder.shared;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class XMLObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<String> dataList;

    public XMLObject(){
        this.dataList = new ArrayList<String>();
    }

    public XMLObject(ArrayList<String> _value) {
        this.dataList = _value;
    }

    public void addElement(String ll){
        dataList.add(ll);
    }

    public ArrayList<String> getDataList() {
        return dataList;
    }


    // Serialization
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(dataList.size());
        for(int i=0; i<dataList.size(); i++){
            out.writeChars(dataList.get(i) + '\n');
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int size = in.readInt();
        for(int i=0; i<size; i++){
            dataList.add(in.readLine());
        }
    }
}
