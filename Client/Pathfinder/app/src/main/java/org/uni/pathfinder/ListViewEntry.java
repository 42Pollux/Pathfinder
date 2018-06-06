package org.uni.pathfinder;

public class ListViewEntry {
    private String name;
    private String heigth;
    private String distance;

    public ListViewEntry(String _name, String _heigth, String _distance) {
        this.name = _name;
        this.heigth = _heigth;
        this. distance = _distance;
    }

    public String getName() {
        return name;
    }

    public String getHeigth() {
        return heigth;
    }

    public String getDistance() {
        return distance;
    }
}
