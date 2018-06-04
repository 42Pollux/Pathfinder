package org.uni.pathfinder.network;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MapViewInitializer {
    private double latitude;
    private double longitude;
    private int zoomLevel;

    public MapViewInitializer(double lat, double lon, int zoom){
        this.latitude = lat;
        this.longitude = lon;
        this.zoomLevel = zoom;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public MapViewInitializer(FileInputStream in){
        try {
            in.skip(62);
            getString(in);
            in.skip(1);
            int lat =  getInt(in);
            int lon = getInt(in);
            byte[] barry = new byte[1];
            in.read(barry);
            int zoom = barry[0];

            this.latitude = lat;
            this.longitude = lon;
            this.zoomLevel = zoom;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String getString(FileInputStream in) throws IOException {
        byte[] barry = new byte[1];

        LEB128UINT string_size = new LEB128UINT(in);
        byte[] tmp_buffer = new byte[string_size.getInteger()];
        for(int i=0; i<string_size.getInteger(); i++) {
            in.read(barry);
            tmp_buffer[i] = barry[0];
        }

        return new String(tmp_buffer, "UTF-8");
    }

    private static int getInt(FileInputStream in) throws IOException {
        byte[] barry = new byte[1];
        byte[] buffer = new byte[4];

        for(int i=0; i<4; i++) {
            in.read(barry);
            buffer[i] = barry[0];
        }
        ByteBuffer wrapped = ByteBuffer.wrap(buffer);
        return wrapped.getInt();
    }
}

class LEB128UINT {
    private int value = 0;
    private byte[] bytes;
    private int byte_length = 0;

    public LEB128UINT(byte[] array) {
        value = convertToJavaInt(array);
        bytes = array.clone();
        byte_length = array.length;
    }

    public LEB128UINT(int x) {
        bytes = convertToByteArray(x);
    }

    public LEB128UINT(FileInputStream in) {
        byte[] barry = new byte[1];
        byte[] buffer = new byte[32];
        int len = 0;
        try {
            for (int i = 0; i < buffer.length; i++) {
                in.read(barry);
                buffer[i] = barry[0];
                len++;
                if ((barry[0] & 128) == 0)
                    break;
            }
            byte[] ret = new byte[len];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = buffer[i];
            }
            value = convertToJavaInt(ret);
            bytes = ret;
            byte_length = ret.length;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int getByteCount() {
        return byte_length;
    }

    public int getInteger() {
        return value;
    }

    public byte[] getBytes() {
        return bytes;
    }

    private int convertToJavaInt(byte[] array) {
        int[] arr = new int[array.length];

        // reverse array in case of wrong order, should be LSB---MSB
        // &0xFF because java byte range is from -128 to 127
        if ((array[0] & 0xFF) < 128) {
            for (int i = 0; i < array.length / 2; i++) {
                byte tmp = array[i];
                array[i] = array[array.length - i - 1];
                array[array.length - i - 1] = tmp;
            }
        }

        // grab each 7-bit value as integer
        for (int i = 0; i < array.length; i++) {
            arr[i] = array[i] & 127;
        }

        // calculate the int value
        int value = 0;
        int exponent = 0;
        String tmp = "";
        int tm = 0;
        for (int i : arr) {
            for (int j = 0; j < 7; j++) {
                value += (int) ((i % 2) * Math.pow(2, exponent));
                tmp += "Value[" + tm + "] = (" + i + "%2)->" + (i % 2) + " * 2^" + exponent + "("
                        + (int) Math.pow(2, exponent) + ") = " + value + "\n";
                i = i / 2;
                exponent++;
            }
            tm++;
        }

        return value;

    }

    private byte[] convertToByteArray(int x) {
        int size = 0;
        int pad = x;

        if(x==0) {
            byte[] ret = new byte[1];
            ret[0] = 0;
            return ret;
        }
        while(pad>0) {
            pad = pad/2;
            size++;
        }
        int byte_count = (size/7+1);
        if(size%7==0) byte_count--;

        byte[] ret = new byte[byte_count];
        for(int i=byte_count-1; i>=0; i--) {
            int exponent = 0;
            if(i>0) {
                ret[i] += 128;
            }
            for(int j=0; j<7; j++) {
                ret[i] += (x%2)*(int) Math.pow(2,  exponent);
                x = x/2;
                exponent++;
            }
        }

        // reverse array LSB---MSB
        for (int i = 0; i < ret.length / 2; i++) {
            byte tmp = ret[i];
            ret[i] = ret[ret.length - i - 1];
            ret[ret.length - i - 1] = tmp;
        }
        return ret;

    }

}
