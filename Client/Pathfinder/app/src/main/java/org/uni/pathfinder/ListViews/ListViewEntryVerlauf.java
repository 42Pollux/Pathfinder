package org.uni.pathfinder.ListViews;

public class ListViewEntryVerlauf {
    private String name;
    private String datum;
    private String distance;

    public ListViewEntryVerlauf(String _name, String _datum, String _distance) {
        this.name = _name;
        this.datum = _datum;
        this. distance = _distance;
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
}