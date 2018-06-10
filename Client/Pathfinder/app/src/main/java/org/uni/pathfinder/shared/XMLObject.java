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
    private ArrayList<ArrayList<ReferenceObject>> referenceList;

    public XMLObject(){
        this.dataList = new ArrayList<String>();
        this.referenceList = new ArrayList<>();
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

    public ArrayList<ArrayList<ReferenceObject>> getReferenceList() {
        return referenceList;
    }

    public void setReferenceList(ArrayList<ArrayList<ReferenceObject>> referenceList) {
        this.referenceList = referenceList;
    }

    // Serialization
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(dataList.size());
        for(int i=0; i<dataList.size(); i++){
            out.writeChars(dataList.get(i) + '\n');
        }
        out.writeInt(referenceList.size());
        for(int i=0; i<referenceList.size(); i++){
            out.writeInt(referenceList.get(i).size());
            for(int j=0; j<referenceList.get(i).size(); j++) {
                out.writeObject(referenceList.get(i).get(j));
            }
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int size = in.readInt();
        for(int i=0; i<size; i++){
            dataList.add(in.readLine());
        }
        int size2 = in.readInt();
        for(int i=0; i<size2; i++){
            int size3 = in.readInt();
            ArrayList<ReferenceObject> ref = new ArrayList<>();
            for(int j=0; j<size3; j++) {
                ref.add((ReferenceObject)in.readObject());
            }
            referenceList.add(ref);
        }
    }
}
