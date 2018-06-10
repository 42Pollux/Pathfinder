package org.uni.pathfinder;

import org.uni.pathfinder.network.MapViewInitializer;
import org.uni.pathfinder.shared.ReferenceObject;

import java.util.ArrayList;

public class SessionStorage {

    public static ArrayList<ReferenceObject> referenceObjects;
    public static String selectionMap = null;
    public static MapViewInitializer selectionMapInit = null;
    public static String currentMap = null;
    public static ArrayList<String> currentPath = null;
}
