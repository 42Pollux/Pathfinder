package ui.pathfinder;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.support.v4.app.NotificationCompat.Builder;
import ui.pathfinder.ConnectionCodes;

/**
 * Created by pollux on 29.01.18.
 */

public class Connector {
    public Connector(){
    }

    public static int request(Context context, byte requestCode){
        new Thread(new ThreadRequest(context, requestCode)).start();
        return 0;
    }

    public static int request(Context context, byte requestCode, String filepath, boolean retry){
        new Thread(new ThreadRequest(context, requestCode, filepath, retry)).start();
        return 0;
    }
}

class ThreadRequest implements Runnable {
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;
    private DataOutputStream out_data;
    private DataInputStream in_data;
    private String filepath = "";
    private byte requestCode;
    private boolean is_retry = false;
    private int retry_anticounter = 0;
    private Context context = null;
    private String public_key = "123456";
    private NotificationManager notificationManager = null;
    private NotificationCompat.Builder notificationCompatBuilder = null;

    public ThreadRequest(Context _context, byte _code) {
        requestCode = _code;
        context = _context;
    }
    public ThreadRequest(Context _context, byte _code, String _filepath, boolean _retry) {
        requestCode = _code;
        filepath = _filepath;
        is_retry = _retry;
        context = _context;
    }

    private void debug_log(String text){
        Log.d("DEBUG1", text);
    }



    // ##################################################################################
    // AUTHENTICATION


    @Override
    public void run() {

       if(initialize(false)<0){ return; }


        // exchange public keys
        keyExchange();

        // send request code
        writeCode(ConnectionCodes.REQUEST);

        // process request code
        switch(requestCode){

            // MAPS ##########
            case ConnectionCodes.MAP_B:     // Berlin
                                            requestMap(ConnectionCodes.MAP_B);
                                            break;
            case ConnectionCodes.MAP_BA:    // Bayern
                                            requestMap(ConnectionCodes.MAP_BA);
                                            break;
            case ConnectionCodes.MAP_BR:    // Brandenburg
                                            requestMap(ConnectionCodes.MAP_BR);
                                            break;
            case ConnectionCodes.MAP_BW:    // Baden-Württemberg
                                            requestMap(ConnectionCodes.MAP_BW);
                                            break;
            case ConnectionCodes.MAP_HB:    // Bremen
                                            requestMap(ConnectionCodes.MAP_HB);
                                            break;
            case ConnectionCodes.MAP_HH:    // Hamburg
                                            requestMap(ConnectionCodes.MAP_HH);
                                            break;
            case ConnectionCodes.MAP_HS:    // Hessen
                                            requestMap(ConnectionCodes.MAP_HS);
                                            break;
            case ConnectionCodes.MAP_MV:    // Mecklenburg-Vorpommern
                                            requestMap(ConnectionCodes.MAP_MV);
                                            break;
            case ConnectionCodes.MAP_NRW:   // Nordrhein-Westpfahlen
                                            requestMap(ConnectionCodes.MAP_NRW);
                                            break;
            case ConnectionCodes.MAP_NS:    // Niedersachsen
                                            requestMap(ConnectionCodes.MAP_NS);
                                            break;
            case ConnectionCodes.MAP_RP:    // Rheinland-Pfalz
                                            requestMap(ConnectionCodes.MAP_RP);
                                            break;
            case ConnectionCodes.MAP_SA:    // Sachsen-Anhalt
                                            requestMap(ConnectionCodes.MAP_SA);
                                            break;
            case ConnectionCodes.MAP_SH:    // Schleswig-Holstein
                                            requestMap(ConnectionCodes.MAP_SH);
                                            break;
            case ConnectionCodes.MAP_SL:    // Saarland
                                            requestMap(ConnectionCodes.MAP_SL);
                                            break;
            case ConnectionCodes.MAP_SS:    // Sachsen
                                            requestMap(ConnectionCodes.MAP_SS);
                                            break;
            case ConnectionCodes.MAP_TH:    // Thüringen
                                            requestMap(ConnectionCodes.MAP_TH);
                                            break;
            case ConnectionCodes.MAP_GER:   // Deutschland
                                            requestMap(ConnectionCodes.MAP_GER);
                                            break;

            // COMMANDS ##########
            case ConnectionCodes.REGISTER:  break;

            case ConnectionCodes.MAPPART:   // Kartenausschnitt
                                            break;
        }

    }

    /**
     * Sends everything needed to start the download of the selected map.
     *
     * @param mapcode	        the map described as byte code
     */
    private void requestMap(int mapcode){
        writeCode(ConnectionCodes.MAP);
        writeCode(mapcode);
        debug_log("SENT request code " + mapcode);

        // respond to average delay calculations
        calcAvgDelayResponse();

        int ret = receiveFile(mapcode, filepath, is_retry);
        if(ret==-2){
            debug_log("Download failed, no stable connection");
        } else if(ret==-1){
            debug_log("Download failed");
        }

    }

    /**
     * Initializes the socket connection and the corresponding input
     * and output streams. Reinitializes everything if 'reset' is true.
     *
     * @param reset	        reinitializes everything if set to true
     * @return				0 on succes, -1 on error
     */
    private int initialize(boolean reset){
        try {
            if(reset){
                client.close();
                out_data.close();
                in_data.close();
            }
            client = new Socket(ConnectionCodes.serverIP, ConnectionCodes.port);

            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            out_data = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
            in_data = new DataInputStream(new BufferedInputStream(client.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
            return -1;
        }
        return 0;
    }

    /**
     * Sends the public key to the server and waits for a response containing
     * the servers public key.
     * @return	0 on success, -1 on error
     */
    public int keyExchange() {
        String server_public_key = null;
        String encrypted_uid = "";

        // send the public key
        writeText(public_key);

        // wait for the servers public key
        if((server_public_key = getTextResponse())==null) return -1;

        // encrypt the uid
        // TODO implement

        // send the encrypted uid
        writeText(encrypted_uid);

        return 0;
    }








    // ##################################################################################
    // NETWORKING


    /**
     * Waits a certain amount of time for a response from the
     * connected client. The response should be in form of a code
     * as seen in class 'ConnectionCodes'. Transmissions are always
     * terminated by the END-sequence.
     *
     * @param timeout_ms	time we wait for the response in milliseconds
     * @return				the code on success, -1 for timeout, -2 for error
     */
    private int getCode(int timeout_ms){
        int timeout = 0;
        int ret_byte = -2;

        while(timeout<timeout_ms){
            try {
                while(in_data.available()>0){
                    int tmp_byte = in_data.read();
                    if(tmp_byte==ConnectionCodes.END) return ret_byte;
                    ret_byte = tmp_byte;

                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
                return -2;
            }

            sleep(10);
            timeout += 10;

        }
        return -1;
    }

    /**
     * Reads in all characters from the current stream until the
     * new line character appears.
     *
     * @return				Text response as string.
     */
    private String getTextResponse(){
        String response = "";
        int timeout = 0;

        while(timeout<5000){
            try {
                while(in.ready()){
                    int tmp_char = in.read();
                    String tmp_string = Character.toString((char) tmp_char);
                    response += tmp_string;
                    if("\n".equals(tmp_string)) return response.substring(0, response.length()-1);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
                return null;
            }

            sleep(10);
            timeout += 10;

        }
        return null;
    }

    /**
     * Writes a single byte to the output stream and flushes
     *
     * @param code			the byte written
     */
    private void writeCode(int code){
        try {
            out_data.write(code);
            out_data.write(ConnectionCodes.END);
            out_data.flush();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }

    }

    /**
     * Writes one byte to the output stream and 8 bytes of type
     * long. Flushes the stream afterwards.
     *
     * @param code			the byte written
     * @param bytes         the long written
     */
    private void writeCode(int code, long bytes){
        try {
            out_data.write(code);
            out_data.write(ConnectionCodes.END);
            out_data.writeLong(bytes);
            out_data.write(ConnectionCodes.END);
            out_data.flush();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }

    }

    /**
     * Writes a string to the output stream and flushes
     *
     * @param str			the string written
     */
    private void writeText(String str){
        try {
            out_data.writeChars(str + "\n");
            out_data.flush();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }

    }

    /**
     * Puts the thread to sleep for a certain amount of time.
     *
     * @param ms	time in milliseconds
     */
    private void sleep(int ms){
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }
    }

    /**
     * Calculates the average delay in milliseconds a package needs to travel from
     * server to client and opposite. Five 'pings' are being sent to the client.
     *
     * @return	0 on success, -1 on error
     */
    private int calcAvgDelayResponse(){
        for(int i = 0; i<5; i++){
            if(getCode(5000)!=ConnectionCodes.PING) return -1;
            writeCode(ConnectionCodes.PING);
        }
        return 0;
    }

    /**
     * Waits for the next 9 bytes containing the file size of the requested
     * map and the terminating END-sequence.
     *
     * @return	the file size as long or -1/-2 on error
     */
    private long getFileSize(){
        int timeout = 0;
        long fileSize = -2;

        // get file size
        while(timeout<5000){
            try {
                while(in_data.available()>8){
                    //Log.d("DEBUG1", "available size: " + in_data.available());
                    long tmp_byte = in_data.readLong();
                    //Log.d("DEBUG1", "byte size: " + tmp_byte);
                    fileSize = tmp_byte;
                    tmp_byte = in_data.read();
                    if(tmp_byte!=ConnectionCodes.END){
                        fileSize = -2;
                    }
                    timeout = 5000;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
            }

            if(timeout<5000) sleep(10);
            timeout += 10;

        }
        if(fileSize<0) {
            return -1;
        }

        return fileSize;
    }

    /**
     * Sets the progress bar to the specified values
     *
     * @param max           the number that represents 100%
     * @param current       the current progress
     */
    private void progressBar(int max, int current){
        notificationCompatBuilder.setProgress(max, current, false);
        notificationManager.notify(1, notificationCompatBuilder.build());
    }

    /**
     * Sets the progress bar to the specified values
     *
     * @param max           the number that represents 100%
     * @param current       the current progress
     * @param text          the label text
     */
    private void progressBar(int max, int current, String text){
        notificationCompatBuilder.setProgress(max, current, false).setContentText(text);
        notificationManager.notify(1, notificationCompatBuilder.build());
    }

    /**
     * Waits for incoming bytes with a timeout of 10 seconds. The timeout
     * is being reset everytime a cycle finished with more bytes received
     * than before. If the whole file isn't received yet we return with the
     * number of bytes received
     *
     * @param filepath		the location the bytes should be written to
     * @param append		set this true if you want to append to an existing file
     * @param file_size		the final size of the file
     * @param retries		the amount of failed previous tries
     * @return	            number of bytes received, -1 on error
     */
    private int getSegment(String filepath, boolean append, long file_size, int retries){
        FileOutputStream output = null;
        int timeout = 0;

        // create or append to file
        try {
            output = new FileOutputStream(filepath, append);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // TODO Fehlerbehandlung
        }

        // receive file
        int received_bytes = 0;
        int prev_received_bytes = 0;
        while(timeout<10000){
            try {
                byte[] buffer = new byte[1024 * 1024 * 2];
                while(in_data.available()>0){
                    int curr = 0;
                    while((curr = in_data.read(buffer))>0) {
                        output.write(buffer, 0, curr);
                        received_bytes += curr;
                        if((retries-retry_anticounter)>0){                                          // Diese Bedingung ist immer bei empfangen der ersten
                            writeCode(ConnectionCodes.ACK);                                         // Bytes in einem Retry-Versuch wahr. Da der Server
                            retry_anticounter++;                                                    // potenziell weniger als 2MB versendet und wir damit
                        }                                                                           // sonst timeouten.
                        if((received_bytes-prev_received_bytes)>=(1024 * 1024 * 2)){
                            writeCode(ConnectionCodes.ACK);
                            float value = (100.0f / (float)file_size) * (float) received_bytes;
                            progressBar(100, (int)value);
                            prev_received_bytes = received_bytes;

                        }
                        if(received_bytes == file_size) {
                            writeCode(ConnectionCodes.ACK);
                            debug_log("File download complete");
                            progressBar(0, 0, "Download complete");
                            return received_bytes;
                        }
                    }

                    timeout = 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Fehlerbehandlung
            }

            if(received_bytes == file_size) {
                writeCode(ConnectionCodes.ACK);
                debug_log("File download complete");
                progressBar(0, 0, "Download complete");
                return received_bytes;
            }

            sleep(10);
            timeout += 10;

        }

        // close the file output
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // evaluate the received bytes
        if(received_bytes==0){
            Log.d("DEBUG1", "no connection, retry (" + retries + ")");
            debug_log("no connection, retry (" +  retries + ")");
            return received_bytes;
        } else if(received_bytes < file_size){
            debug_log("file partially downloaded, retry (" +  retries + ")");
            return received_bytes;
        } else {
            debug_log("file download failed, retry (" +  retries + ")");
            progressBar(0, 0, "Download failed");
            // notifcationManager.cancel(1);
            return -1;
        }
    }

    /**
     * Grabs the file size first and then initializes a loop of getSegment()
     * calls. After an interrupted connection a retry is being performed.
     * The connection is being reinitialized and a retry request is being sent
     * with the remainingBytes attached as type long. After 7 failed retries
     * (about 60 seconds) we break out of the loop and return with an error.
     * TODO on retry only try with valid file size
     *
     * @param mapcode       the map described as byte code
     * @param filepath		the location the bytes should be written to
     * @param retry 		set this true if you want to retry the download
     * @return	            0 on success, -1/-2 on error
     */
    private int receiveFile(int mapcode, String filepath, boolean retry){
        long fileSize = getFileSize();
        long curr = 0;
        int retries_visual = 1;
        int retries_real = 0;
        boolean initAppend = false;

        if(fileSize<0) {
            // TODO Fehlerbehandlung
            return -1;
        }

        // cleanup
        if(retry){
            // append mode
            initAppend = true;
        } else {
            File file = new File(filepath);
            file.delete();
        }

        // initialise notification manager
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationCompatBuilder = new NotificationCompat.Builder(context);
        notificationCompatBuilder.setContentTitle("Map Download");
        notificationCompatBuilder.setContentText("Download in progress");
        notificationCompatBuilder.setSmallIcon(R.drawable.download);


        // grabbing data, retry loop
        curr += getSegment(filepath, initAppend, fileSize, 0);
        while((retries_real < 7)&&(curr<fileSize)){
            long progress = curr;
            debug_log("vvvvvvvvvv RETRY " + retries_visual + " vvvvvvvvvv");
            initialize(true);
            writeCode(ConnectionCodes.REQUEST);
            writeCode(ConnectionCodes.RETRY, fileSize-curr);
            writeCode(mapcode);
            debug_log("[R] Grabbing file size...");
            getFileSize();
            debug_log("[R] Awaiting bytes");
            curr += getSegment(filepath, true, fileSize-curr, retries_visual);
            retries_visual++;
            retries_real++;
            if(progress < curr) retries_real = 0;
        }

        retry_anticounter = 0;

        if(curr<fileSize){
            return -2;
        } else {
            return 0;
        }
    }
}
