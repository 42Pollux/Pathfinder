package org.uni.pathfinder.ListViews;

import org.uni.pathfinder.shared.ReferenceObject;

import java.util.ArrayList;

public class ListViewEntry {
    private String name;
    private String datum;
    private String distance;
    private ArrayList<String> path;
    private ArrayList<ReferenceObject> references;

    private int image;

    public ListViewEntry(String _name, String _datum, String _distance, int _image) {
        this.name = _name;
        this.datum = _datum;
        this.distance = _distance;
        this.image = _image;
    }

    public ListViewEntry(String _name, String _datum, String _distance) {
        this.name = _name;
        this.datum = _datum;
        this.distance = _distance;
    }

    public String getName() {
        return name;
    }

    public String getDatum() {
        return datum;
    }

    public String getDistance() {
        return distance;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public ArrayList<String> getPath() {
        return path;
    }

    public void setPath(ArrayList<String> path) {
        this.path = path;
    }

    public ArrayList<ReferenceObject> getReferences() {
        return references;
    }

    public void setReferences(ArrayList<ReferenceObject> references) {
        this.references = references;
    }
}
