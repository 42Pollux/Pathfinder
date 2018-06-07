/**
 * @author pollux
 *
 */
package org.uni.pathfinder.network;

public final class ConnectionCodes {
    public static final String serverIP = "62.113.206.126"; // "192.168.188.55";
    public static final int port = 55555;

    // connection control commands
    public static final byte REQUEST = 10;  // a simple request
    public static final byte END = 11;      // terminating sequence of our byte streams
    public static final byte RETRY = 12;    // retry of downloads
    public static final byte ACK = 13;      // acknowleding packages
    public static final byte PING = 14;     // ping package
    public static final byte MAP = 15;		// request a map

    // codes used in the request method of Connector class
    public static final byte REGISTER = 20; // register a new uid
    public static final byte SECTOR = 21;   // a part of a map
    public static final byte XML = 22;      // request an xml
    public static final byte IMAGE = 23;    // request a ressource image
    public static final byte TEXT = 24;     // request a ressource text file
    public static final byte PATH = 25;     // request a pathing calculation
    public static final byte OBJECT = 26;   // request a custom object
    public static final byte HISTORY = 27;	// request old paths

    // specific map codes used in the request method
    public static final byte MAP_MV = 101;	// Mecklenburg-Vorpommern
    public static final byte MAP_SH = 102;	// Schleswig-Holstein
    public static final byte MAP_NS = 103;	// Niedersachsen
    public static final byte MAP_HH = 104;	// Hamburg
    public static final byte MAP_HB = 105;	// Bremen
    public static final byte MAP_BR = 106;	// Brandenburg
    public static final byte MAP_B = 107;	// Berlin
    public static final byte MAP_SA = 108;	// Sachsen Anhalt
    public static final byte MAP_NRW = 109;	// Nordrhein-Westfalen
    public static final byte MAP_HS = 110;	// Hessen
    public static final byte MAP_RP = 111;	// Rheinland-Pfalz
    public static final byte MAP_SL = 112;	// Saarland
    public static final byte MAP_TH = 113;	// Thürigen
    public static final byte MAP_SS = 114;	// Sachsen
    public static final byte MAP_BW = 115;	// Baden-Württemberg
    public static final byte MAP_BA = 116;	// Bayern
    public static final byte MAP_GER = 117;	// Deutschland, komplett

    // Error texts
    public static final String ERR_TIMEOUT = "Connection timed out";
    public static final String ERR_CONLOST = "Connection lost";
    public static final String ERR_PROTO = "Stream out of sync";

    // TODO bessere Werte
}