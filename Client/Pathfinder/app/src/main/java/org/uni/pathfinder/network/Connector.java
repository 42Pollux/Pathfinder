/**
 * @author pollux
 * Created on 29.01.18.
 * TODO implement retry feature button from failed progressbar?
 */
package org.uni.pathfinder.network;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.uni.pathfinder.R;
import org.uni.pathfinder.exceptions.ConnectionTimeoutException;
import org.uni.pathfinder.exceptions.ConnectionUnexpectedlyClosedException;
import org.uni.pathfinder.exceptions.ProtocolErrorException;
import org.uni.pathfinder.shared.XMLObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Connector {
    private NetworkingThreadFinishListener onNetworkingFinished = null;
    private NetworkingConsoleListener onNetworkingProgress = null;
    private int queue_counter = 0; // TODO implement
    private Context app_context;

    public void setOnNetworkingFinished(NetworkingThreadFinishListener onNetworkingFinished) {
        this.onNetworkingFinished = onNetworkingFinished;
    }

    public void setOnNetworkingProgress(NetworkingConsoleListener onNetworkingProgress) {
        this.onNetworkingProgress = onNetworkingProgress;
    }

    public Connector(Context _app_context){
        this.app_context = _app_context;
    }

    // test method
    public void requestTest(int thread_id){
        new Thread(new ThreadRequest(this.app_context, onNetworkingFinished, onNetworkingProgress, thread_id)).start();
    }

    /**
     * Requests a complete map.
     *
     * @param thread_id session-unique id that will be returned in the onNetworkingFinished callback to identify the request
     * @param map_code  code of the map that should be downloaded, see ConnectionCodes.java
     * @param retry     set this true for a manual retry
     */
    // request methods for complete maps
    public void requestCompleteMap(int thread_id, byte map_code, boolean retry){
        new Thread(new ThreadRequest(this.app_context, onNetworkingFinished, onNetworkingProgress, thread_id, map_code, retry)).start();
    }

    /**
     * Requests a sector of the global map.
     *
     * @param thread_id session-unique id that will be returned in the onNetworkingFinished callback to identify the request
     * @param sector    coordinates specifying the sector
     * @param retry     set this true for a manual retry
     */
    // request methods for map sectors
    public void requestMapSector(int thread_id, double[] sector, boolean retry){
        new Thread(new ThreadRequest(this.app_context, onNetworkingFinished, onNetworkingProgress, thread_id, ConnectionCodes.SECTOR, sector, retry)).start();
    }

    /**
     * Requests a xml file.
     *
     * @param thread_id session-unique id that will be returned in the onNetworkingFinished callback to identify the request
     * @param xml_id    unique id specifying the file on the server
     */
    // request methods for custom xml files
    public void requestXml(int thread_id, long xml_id){ // locally saved by default
        new Thread(new ThreadRequest(this.app_context, onNetworkingFinished, onNetworkingProgress, thread_id, ConnectionCodes.XML, xml_id)).start();
    }

    /**
     * Requests an image.
     *
     * @param thread_id session-unique id that will be returned in the onNetworkingFinished callback to identify the request
     * @param image_id  unique id specifying the file on the server
     */
    // request methods for images
    public void requestImage(int thread_id, long image_id){ // locally saved by default
        new Thread(new ThreadRequest(this.app_context, onNetworkingFinished, onNetworkingProgress, thread_id, ConnectionCodes.IMAGE, image_id)).start();
    }

    /**
     * Requests a text file.
     *
     * @param thread_id session-unique id that will be returned in the onNetworkingFinished callback to identify the request
     * @param text_id   unique id specifying the file on the server
     */
    // request methods for text
    public void requestText(int thread_id, long text_id){ // locally saved by default
        new Thread(new ThreadRequest(this.app_context, onNetworkingFinished, onNetworkingProgress, thread_id, ConnectionCodes.TEXT, text_id)).start();
    }

    /**
     * Requests a custom object.
     *
     * @param thread_id session-unique id that will be returned in the onNetworkingFinished callback to identify the request
     * @param object_id unique id specifying the object located on the server, if created by the server
     */
    // request methods for custom objects
    public void requestObject(int thread_id, long object_id){
        new Thread(new ThreadRequest(this.app_context, onNetworkingFinished, onNetworkingProgress, thread_id, ConnectionCodes.OBJECT, object_id)).start();
    }

    /**
     * Requests a list of XMLObject containing route information calculated by the server. The list returned
     * by the onNetworkingFinished callback is of type java.util.ArrayList.
     *
     * @param thread_id session-unique id that will be returned in the onNetworkingFinished callback to identify the request
     * @param xml       XMLObject containing the path calculation parameters
     */
    // request methods for pathing
    public void requestPaths(int thread_id, XMLObject xml){
        new Thread(new ThreadRequest(this.app_context, onNetworkingFinished, onNetworkingProgress, thread_id, ConnectionCodes.PATH, xml)).start();
    }
}

class ThreadRequest implements Runnable, NetworkingThreadFinishListener, NetworkingConsoleListener {
    private Socket client;
    private ObjectOutputStream out_data;
    private ObjectInputStream in_data;
    private String client_uid = "0000";
    private String app_path;

    private byte request_code_global;
    private Context context;
    private int thread_id_global;
    private long item_id_global;
    private boolean is_retry_global = false;
    private double[] sector = new double[4];
    private XMLObject xml_global;

    private NotificationManager notificationManager = null;
    private NotificationCompat.Builder notificationCompatBuilder = null;

    private NetworkingThreadFinishListener onNetworkingFinished = null;
    private NetworkingConsoleListener onNetworkingProgress = null;
    private int retry_anticounter = 0;

    // constructor for complete map requests
    public ThreadRequest(Context _context, NetworkingThreadFinishListener _onNetworkingFinished, NetworkingConsoleListener _onNetworkingProgress, int _thread_id, byte _map_code, boolean _retry) {
        this.context = _context;
        this.thread_id_global = _thread_id;
        this.onNetworkingFinished = _onNetworkingFinished;
        this.onNetworkingProgress = _onNetworkingProgress;
        this.request_code_global = _map_code;
        this.is_retry_global = _retry;
    }

    // constructor for map sector requests
    public ThreadRequest(Context _context, NetworkingThreadFinishListener _onNetworkingFinished, NetworkingConsoleListener _onNetworkingProgress, int _thread_id, byte _code, double[] _sector, boolean _retry) {
        this.context = _context;
        this.thread_id_global = _thread_id;
        this.onNetworkingFinished = _onNetworkingFinished;
        this.onNetworkingProgress = _onNetworkingProgress;
        this.request_code_global = _code;
        this.is_retry_global = _retry;
        this.sector = _sector;
    }

    // constructor for resource file requests
    public ThreadRequest(Context _context, NetworkingThreadFinishListener _onNetworkingFinished, NetworkingConsoleListener _onNetworkingProgress, int _thread_id, byte _code, long _item_id) {
        this.context = _context;
        this.thread_id_global = _thread_id;
        this.onNetworkingFinished = _onNetworkingFinished;
        this.onNetworkingProgress = _onNetworkingProgress;
        this.request_code_global = _code;
        this.item_id_global = _item_id;
    }

    // constructor for path requests
    public ThreadRequest(Context _context, NetworkingThreadFinishListener _onNetworkingFinished, NetworkingConsoleListener _onNetworkingProgress, int _thread_id, byte _code, XMLObject _xml) {
        this.context = _context;
        this.thread_id_global = _thread_id;
        this.onNetworkingFinished = _onNetworkingFinished;
        this.onNetworkingProgress = _onNetworkingProgress;
        this.request_code_global = _code;
        this.xml_global = _xml;
    }

    // constructor for tests
    public ThreadRequest(Context _context, NetworkingThreadFinishListener _onNetworkingFinished, NetworkingConsoleListener _onNetworkingProgress, int _thread_id){
        this.thread_id_global = _thread_id;
        this.onNetworkingFinished = _onNetworkingFinished;
        this.onNetworkingProgress = _onNetworkingProgress;
    }

    private void debug_log(String text){
        Log.d("DEBUG1", text);
        //onNetworkingProgress.onNetworkingProgress(text);
    }



    // ##################################################################################
    // AUTHENTICATION + REQUEST-PIPE

    @Override
    public void run() {

        // initialize readers/writers
       try {
           initialize(false);
       } catch (ConnectionUnexpectedlyClosedException e) {
           debug_log("failed to initialize");
           onNetworkingFinished.onNetworkingResult(thread_id_global, "Failed to connect to server", null, true);
           return;
       }
       app_path = context.getFilesDir().getAbsolutePath();
       debug_log(app_path);
       debug_log("Connected to server");

        // process request
        try {
            request(request_code_global);
        } catch (ConnectionTimeoutException e) {
            debug_log(ConnectionCodes.ERR_TIMEOUT);
            onNetworkingFinished.onNetworkingResult(thread_id_global, ConnectionCodes.ERR_TIMEOUT, null, true);
        } catch (ConnectionUnexpectedlyClosedException e) {
            debug_log(ConnectionCodes.ERR_CONLOST);
            onNetworkingFinished.onNetworkingResult(thread_id_global, ConnectionCodes.ERR_CONLOST, null, true);
        } catch (ProtocolErrorException e) {
            debug_log(ConnectionCodes.ERR_PROTO);
            onNetworkingFinished.onNetworkingResult(thread_id_global, ConnectionCodes.ERR_PROTO, null, true);
        } catch (IOException e) {
            debug_log(ConnectionCodes.ERR_PROTO);
            onNetworkingFinished.onNetworkingResult(thread_id_global, ConnectionCodes.ERR_PROTO, null, true);
        }

    }

    /**
     * Initializes the socket connection and the corresponding input
     * and output streams. Reinitializes everything if 'reset' is true.
     *
     * @param reset reinitializes everything if set to true
     * @throws ConnectionUnexpectedlyClosedException
     */
    private void initialize(boolean reset) throws ConnectionUnexpectedlyClosedException {
        // TODO set client uid from shared preferences, only on first call not for retry
        client_uid = "asUdwSgQDDfd5FeWR6";

        try {
            if(reset){
                out_data.close();
                in_data.close();
                client.close();
            }
            client = new Socket(ConnectionCodes.serverIP, ConnectionCodes.port);

            out_data = new ObjectOutputStream(client.getOutputStream());
            in_data = new ObjectInputStream(client.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectionUnexpectedlyClosedException(getLineNumber() + ", initialize");
        }
    }

    /**
     * Handles the initial request pattern and performs the key exchange. Calls the
     * corresponding functions for each requests.
     *
     * @param code  byte code containing the type of request, see ConnectionCodes.java
     * @throws ConnectionTimeoutException
     * @throws ConnectionUnexpectedlyClosedException
     * @throws ProtocolErrorException
     * @throws FileNotFoundException
     */
    private void request(byte code) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException, IOException {

        // send request code
        writeCode(ConnectionCodes.REQUEST);

        // key exchange for everything except register requests
        keyExchange(client_uid);

        // process request code
        if((code>100)&&(code<118)){
            requestMap(code);
        } else {
            switch(code){
                case ConnectionCodes.REGISTER:
                    requestRegister();
                    break;
                case ConnectionCodes.SECTOR:
                    requestSector(sector);
                    break;
                case ConnectionCodes.XML:
                    requestResource(item_id_global, code);
                    break;
                case ConnectionCodes.IMAGE:
                    requestResource(item_id_global, code);
                    break;
                case ConnectionCodes.TEXT:
                    requestResource(item_id_global, code);
                    break;
                case ConnectionCodes.OBJECT:
                    requestObject(item_id_global);
                    break;
                case ConnectionCodes.PATH:
                    requestPaths(xml_global);
                    break;
            }
        }
    }

    /**
     * Sends/receives everything needed to start the download of the specified map.
     *
     * @param map_code  the map described as byte code, see ConnectionCodes.java
     * @throws ConnectionTimeoutException
     * @throws ConnectionUnexpectedlyClosedException
     * @throws FileNotFoundException
     */
    private void requestMap(int map_code) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, FileNotFoundException {
        debug_log("Requested: Map");

        // request pattern for maps
        writeCode(ConnectionCodes.MAP);
        writeCode(map_code);
        String file_name = getTextResponse();
        String file_path = app_path + "/map/" + file_name;

        // respond to average delay calculations
        //calcAvgDelayResponse();

        // download map
        String result = receiveMapFile(file_path, file_name, map_code, is_retry_global);
        onNetworkingFinished.onNetworkingResult(thread_id_global, result, null, false);
    }

    /**
     * Sends/receives everything needed the register a new UID.
     *
     * @throws ConnectionTimeoutException
     * @throws ConnectionUnexpectedlyClosedException
     */
    private void requestRegister() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException {
        debug_log("Requested: UID");

        // request pattern for registering
        writeCode(ConnectionCodes.REGISTER);
        client_uid = getTextResponse();
    }

    /**
     * Sends/receives everything needed to start the download of the specified resource.
     *
     * @param item_id   unique id specifying the resource
     * @param res_code  type of the resource as byte code, see ConnectionCodes.java
     * @throws ConnectionTimeoutException
     * @throws ConnectionUnexpectedlyClosedException
     * @throws ProtocolErrorException
     * @throws FileNotFoundException
     */
    private void requestResource(long item_id, int res_code) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException, FileNotFoundException {
        debug_log("Requested: Resource");

        // request pattern for resources
        writeCode(res_code);
        writeLong(item_id);

        String file_name = getTextResponse();
        String file_path = app_path + "/res/" + file_name;

        String result = receiveResource(file_path);
        onNetworkingFinished.onNetworkingResult(thread_id_global, result, null, false);
    }

    /**
     * Sends/receives everything needed to start the download of the specified object.
     *
     * @param item_id   unique id specifying the resource
     * @throws ConnectionTimeoutException
     * @throws ConnectionUnexpectedlyClosedException
     * @throws ProtocolErrorException
     * @throws FileNotFoundException
     */
    private void requestObject(long item_id) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException, FileNotFoundException {
        debug_log("Requested: Object");

        // request pattern for objects
        writeCode(ConnectionCodes.OBJECT);
        writeLong(item_id);

        Object result = receiveObject();
        onNetworkingFinished.onNetworkingResult(thread_id_global, null, result, false);
    }

    /**
     * Sends/receives everything needed to start the download of the specified map sector.
     *
     * @param _sector   coordinates specifying the sector
     * @throws ConnectionTimeoutException
     * @throws ConnectionUnexpectedlyClosedException
     * @throws ProtocolErrorException
     * @throws FileNotFoundException
     */
    private void requestSector(double[] _sector) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException, IOException {
        debug_log("Requested: Sector");

        // request pattern for objects
        writeCode(ConnectionCodes.SECTOR);
        writeSector(_sector);

        String file_name = getTextResponse();
        String file_path = app_path + "/tmp/" + file_name;
        debug_log(file_name);
        // grab the map initializing data

        String result = receiveMapSector(file_path, _sector, false);
        debug_log(file_path);
        FileInputStream in = new FileInputStream(file_path);
        MapViewInitializer initializer = new MapViewInitializer(in);
        in.close();
        debug_log("hier2");
        onNetworkingFinished.onNetworkingResult(thread_id_global, result, initializer, false);
    }

    /**
     * Sends/receives everything needed to start the download of the server-side calculated
     * list of XMLObjects containing different paths.
     *
     * @param xml   XMLObject containing the path calculation parameters
     * @throws ConnectionTimeoutException
     * @throws ConnectionUnexpectedlyClosedException
     * @throws ProtocolErrorException
     * @throws FileNotFoundException
     */
    private void requestPaths(XMLObject xml) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException, FileNotFoundException {
        debug_log("Requested: Paths");

        // request pattern for paths
        writeCode(ConnectionCodes.PATH);
        writeObject(xml);

        List<XMLObject> result = (ArrayList<XMLObject>) receiveObject();
        onNetworkingFinished.onNetworkingResult(thread_id_global, null, result, false);
    }

    /**
     * Performs the key exchange of the RSA public keys and the AES key.
     * Current sequence of events:
     * 1. Client sends his public key base64 encoded.
     * 2. Server sends his public key base64 encoded.
     * 3. Client sends the RSA encrypted and base64 encoded AES key (session key).
     * 4. Client sends the AES encrypted and base64 encoded UID.
     *
     * @throws ConnectionUnexpectedlyClosedException
     * @throws ConnectionTimeoutException
     */
    private void keyExchange(String uid) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException {
        String server_public_key = null;
        String encrypted_uid = "";
        Cryptography.initialize();

        // send the public key
        writeText(Cryptography.getRSAPublicKey_base64Format());
        debug_log("Our Key: " + shortenKey(Cryptography.getRSAPublicKey_base64Format()));

        // wait for the servers public keyhelper_mapvinit
        server_public_key = getTextResponse();
        Cryptography.setServerPublicKey_base64Format(server_public_key);
        debug_log("Server Public Key: " + shortenKey(server_public_key));

        // encrypt the session key
        // send the session key
        debug_log("Encrypted aes key: " + shortenKey(Cryptography.getAESEncryptedKey_base64Format())); // TODO shouldnt be called two times
        writeText(Cryptography.getAESEncryptedKey_base64Format());

        // encrypt the uid
        encrypted_uid = Cryptography.encryptText(uid);

        // send the encrypted uid
        writeText(encrypted_uid);
    }

    private String shortenKey(String key){
        char[] charKey = key.toCharArray();
        String retKey = "";
        if(key.length()>99){
            retKey = "" + charKey[0]+charKey[1]+charKey[2]+charKey[3]+".."+charKey[50]+charKey[51]+charKey[52]+charKey[53]+".."+charKey[96]+charKey[97]+charKey[98]+charKey[99];
        } else if(key.length()>49){
            retKey = "" + charKey[0]+charKey[1]+charKey[2]+charKey[3]+".."+charKey[25]+charKey[26]+charKey[27]+charKey[28]+".."+charKey[46]+charKey[47]+charKey[48]+charKey[49];
        } else if(key.length()>29){
            retKey = "" + charKey[0]+charKey[1]+charKey[2]+charKey[3]+".."+charKey[10]+charKey[11]+charKey[12]+charKey[13]+".."+charKey[26]+charKey[27]+charKey[28]+charKey[29];
        }

        return retKey;
    }






    // ##################################################################################
    // NETWORKING


    /**
     * Waits a certain amount of time for a response from the
     * connected client. The response should be in form of a code
     * as seen in class 'ConnectionCodes'.
     *
     * @param timeout_ms    time we wait for the response in milliseconds
     * @return				the received code
     * @throws ConnectionTimeoutException
     * @throws ConnectionUnexpectedlyClosedException
     */
    private int getCode(int timeout_ms) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException {
        int timeout = 0;

        while(timeout<timeout_ms){
            try {
                while(in_data.available()>0){
                    return in_data.read();
                }
            } catch (IOException e) {
                // Assuming IOException is only thrown for "Stream closed"
                throw new ConnectionUnexpectedlyClosedException(getLineNumber() + ", getCode");
            }
            sleep(10);
            timeout += 10;

        }
        throw new ConnectionTimeoutException(getLineNumber() + ", getCode");
    }

    /**
     * Reads in all characters from the current stream until the
     * new line character appears.
     *
     * @return Text response as string.
     * @throws ConnectionTimeoutException
     * @throws ConnectionUnexpectedlyClosedException
     */
    private String getTextResponse() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException{ // TODO why is this not called readText?
        String response = "";
        int timeout = 0;

        while(timeout<10000){
            try {
                while(in_data.available()>0){
                    char a = in_data.readChar();
                    response = response + a;
                    if(a=='\n') return response.substring(0, response.length()-1);
                }
            } catch (IOException e) {
                // Assuming IOException is only thrown for "Stream closed"
                throw new ConnectionUnexpectedlyClosedException(getLineNumber() + ", getTextResponse");
            }

            sleep(10);
            timeout += 10;

        }
        throw new ConnectionTimeoutException(getLineNumber() + ", getTextResponse");
    }

    /**
     * Writes a single byte to the output stream and flushes.
     *
     * @param code			the byte written
     */
    private void writeCode(int code){
        try {
            out_data.write(code);
            out_data.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes a string to the output stream and flushes.
     *
     * @param str			the string written
     */
    private void writeText(String str){
        str = str + "\n";
        try {
            out_data.writeChars(str);
            out_data.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes an 8-byte sized long to the output stream and flushes.
     *
     * @param l
     */
    private void writeLong(long l) {
        try {
            out_data.writeLong(l);
            out_data.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes four 4-byte sized floats to the output stream and flushes.
     *
     * @param sector
     */
    private void writeSector(double[] sector){
        try {
            out_data.writeDouble(sector[0]);
            out_data.writeDouble(sector[1]);
            out_data.writeDouble(sector[2]);
            out_data.writeDouble(sector[3]);
            out_data.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes an serializable object to the output stream and flushes.
     *
     * @param obj
     */
    private void writeObject(Object obj){
        try {
            out_data.writeObject(obj);
            out_data.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads an object from the input stream.
     *
     * @return  the read object
     */
    private Object readObject(){
        try {
            Object obj = in_data.readObject();
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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
     * DEPRECATED
     * Calculates the average delay in milliseconds a package needs to travel from
     * server to client and opposite. Five 'pings' are being sent to the client.
     *
     * @return	0 on success, -1 on error
     * @throws ConnectionTimeoutException
     * @throws ConnectionUnexpectedlyClosedException
     */
    private int calcAvgDelayResponse() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, ProtocolErrorException {
        for(int i = 0; i<5; i++){
            if(getCode(5000)!=ConnectionCodes.PING) throw new ProtocolErrorException(getLineNumber() + ", calcAvgDelayResponse");
            writeCode(ConnectionCodes.PING);
        }
        return 0;
    }

    /**
     * Waits for the next 8 bytes containing the size of the requested file.
     *
     * @return	the operation size as long
     * @throws ConnectionUnexpectedlyClosedException
     * @throws ConnectionTimeoutException
     */
    private long getOperationSize() throws ConnectionUnexpectedlyClosedException, ConnectionTimeoutException {
        int timeout = 0;

        while(timeout<5000){
            try {
                while(in_data.available()>7){
                    return in_data.readLong();
                }
            } catch (IOException e) {
                // Assuming IOException is only thrown for "Stream closed"
                throw new ConnectionUnexpectedlyClosedException(getLineNumber() + ", getOperationSize");
            }

            sleep(10);
            timeout += 10;

        }
        throw new ConnectionTimeoutException(getLineNumber() + ", getOperationSize");
    }

    /**
     * Sets the progress bar to the specified values.
     *
     * @param max           the number that represents 100%
     * @param current       the current progress
     * @param percent       current progress in percent
     */
    private void progressBar(int max, int current, int percent){
        notificationCompatBuilder.setProgress(max, current, false).setContentText(percent+"%");
        if(percent==100) notificationCompatBuilder.setSmallIcon(R.drawable.ic_check);
        if(percent==-1) notificationCompatBuilder.setSmallIcon(R.drawable.ic_check).setContentText("Download failed");
        notificationManager.notify(1, notificationCompatBuilder.build()); // TODO corrupt id
    }

    /**
     * Creates the progress bar and sets default values.
     *
     * @param title
     */
    private void initializeProgressBar(String title){
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationCompatBuilder = new NotificationCompat.Builder(context);
        notificationCompatBuilder.setContentTitle(title);
        notificationCompatBuilder.setSmallIcon(R.drawable.ic_arrow_down);
        progressBar(100, 0, 0);
    }

    /**
     * Grabs the file size first and then initializes a loop of getSegment()
     * calls. After an interrupted connection, a retry is being performed.
     * The connection is being reinitialized and a retry request is being sent
     * with the remainingBytes attached as type long. After 7 failed retries
     * (about 60 seconds) we break out of the loop and return with an error.
     *
     * @param map_code      the map described as byte code, see ConnectionCodes.java
     * @param file_path		the location the map should be saved to
     * @param retry 		set this true if you want to retry the download
     * @return	            path to received file
     */
    private String receiveMapFile(String file_path, String file_name, int map_code, boolean retry) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, FileNotFoundException {
        long file_size = 0;
        long curr = 0;
        int retries_visual = 1;
        int retries_real = 0;
        boolean init_append = false;

        // cleanup
        if(retry){
            // append mode
            init_append = true;
        } else {
            File file = new File(file_path);
            file.delete();
        }

        // get file size
        file_size = getOperationSize();

        // initialise notification manager
        initializeProgressBar(file_name);


        // grabbing data, retry loop
        curr += getSegment(file_path, init_append, file_size, 0, false);
        while((retries_real < 7)&&(curr<file_size)){
            long progress = curr;
            debug_log("[R] Retry no. " + retries_visual);
            initialize(true);
            writeCode(ConnectionCodes.REQUEST);
            try {keyExchange(client_uid);}  catch (ConnectionTimeoutException | ConnectionUnexpectedlyClosedException e) { /* ignore in this case */ }
            writeCode(ConnectionCodes.RETRY);
            writeLong(file_size-curr);
            writeCode(map_code);
            debug_log("[R] Grabbing file size...");
            try {getOperationSize();}  catch (ConnectionTimeoutException | ConnectionUnexpectedlyClosedException e) { /* ignore in this case */ }
            debug_log("[R] Awaiting bytes");
            curr += getSegment(file_path, true, file_size-curr, retries_visual, true);
            retries_visual++;
            retries_real++;
            if(progress < curr) retries_real = 0;
        }

        retry_anticounter = 0;

        if(curr==file_size){
            debug_log("Map download complete");
            return file_path;

        } else {
            throw new ConnectionTimeoutException(getLineNumber() + ", receiveMapFile");
        }
    }

    /**
     * Grabs the file size first and then initializes a loop of getSegment()
     * calls. After an interrupted connection, a retry is being performed.
     * The connection is being reinitialized and a retry request is being sent
     * with the remainingBytes attached as type long. After 7 failed retries
     * (about 60 seconds) we break out of the loop and return with an error.
     * No progress bar is being displayed in this function.
     *
     * @param file_path the location the map should be saved to
     * @param sector    coordinates specifying the sector
     * @param retry     set this true if you want to retry the download
     * @return          path to received file
     * @throws ConnectionTimeoutException
     * @throws ConnectionUnexpectedlyClosedException
     * @throws FileNotFoundException
     */
    private String receiveMapSector(String file_path, double[] sector, boolean retry) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, FileNotFoundException {
        long file_size = 0;
        long curr = 0;
        int retries_visual = 1;
        int retries_real = 0;
        boolean init_append = false;
        debug_log("gogo");
        // cleanup
        if(retry){
            // append mode
            init_append = true;
        } else {
            File file = new File(file_path);
            file.delete();
        }

        // get file size
        file_size = getOperationSize();
        debug_log("gogo2");
        // grabbing data, retry loop
        curr += getSegment(file_path, init_append, file_size, 0, true);
        while((retries_real < 7)&&(curr<file_size)){
            long progress = curr;
            debug_log("vvvvvvvvvv RETRY " + retries_visual + " vvvvvvvvvv");
            initialize(true);
            writeCode(ConnectionCodes.REQUEST);
            try {keyExchange(client_uid);}  catch (ConnectionTimeoutException | ConnectionUnexpectedlyClosedException e) { /* ignore in this case */ }
            writeCode(ConnectionCodes.RETRY);
            writeLong(file_size-curr);
            writeCode(ConnectionCodes.SECTOR);
            writeSector(sector);
            debug_log("[R] Grabbing file size...");
            try {getOperationSize();}  catch (ConnectionTimeoutException | ConnectionUnexpectedlyClosedException e) { /* ignore in this case */ }
            debug_log("[R] Awaiting bytes");
            curr += getSegment(file_path, true, file_size-curr, retries_visual, true);
            retries_visual++;
            retries_real++;
            if(progress < curr) retries_real = 0;
        }

        retry_anticounter = 0;

        if(curr==file_size){
            debug_log("Sector download complete");
            return file_path;

        } else {
            throw new ConnectionTimeoutException(getLineNumber() + ", receiveMapSector");
        }
    }

    /**
     * Grabs the file size of the resource and tries exactly once to grab the data.
     * No progress bar is being displayed in this function.
     *
     * @param file_path the location the resource should be saved to
     * @return          path to received file
     * @throws ConnectionTimeoutException
     * @throws ConnectionUnexpectedlyClosedException
     * @throws FileNotFoundException
     */
    private String receiveResource(String file_path) throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException, FileNotFoundException {
        long file_size = 0;
        long curr = 0;

        // get file size
        file_size = getOperationSize();

        // grabbing data
        curr += getSegment(file_path, false, file_size, 0, true);

        if(curr==file_size){
            debug_log("Resource download complete");
            return file_path;

        } else {
            throw new ConnectionTimeoutException(getLineNumber() + ", receiveResource");
        }
    }

    /**
     * Receives an object. TODO.
     *
     * @return  the received object
     * @throws ConnectionTimeoutException
     * @throws ConnectionUnexpectedlyClosedException
     */
    private Object receiveObject() throws ConnectionTimeoutException, ConnectionUnexpectedlyClosedException {
        Object obj = readObject();
        return obj;
        // TODO timeout in case of really big objects needed
    }

    /**
     * Waits for incoming bytes with a timeout of 10 seconds. The timeout
     * is being reset every time a cycle finished with more bytes received
     * than before. If the whole file isn't received yet we return with the
     * number of bytes received.
     *
     * @param file_path		the location the bytes should be written to
     * @param append		set this true if you want to append to an existing file
     * @param file_size		the final size of the file
     * @param retries		the amount of failed previous tries
     * @return	            number of bytes received
     * @throws FileNotFoundException
     */
    private int getSegment(String file_path, boolean append, long file_size, int retries, boolean silent) throws FileNotFoundException {
        FileOutputStream output = null;
        int timeout = 0;

        // create or append to file
        output = new FileOutputStream(file_path, append);

        // receive file
        int received_bytes = 0;
        int prev_received_bytes = 0;
        while(timeout<10000){
            try {
                byte[] buffer = new byte[1024 * 1024 * 2];
                while(in_data.available()>0){ // TODO creates a lot of errors for retry cases, just visible in logcat
                    int curr = 0;
                    while((curr = in_data.read(buffer))>0) {
                        output.write(buffer, 0, curr);
                        received_bytes += curr;
                        if((retries-retry_anticounter)>0){                                          // Diese Bedingung ist immer bei empfangen der ersten
                            writeCode(ConnectionCodes.ACK);                                         // Bytes in einem Retry-Versuch wahr. Da der Server
                            retry_anticounter++;                                                    // potenziell weniger als 2MB versendet und wir damit
                        }                                                                           // sonst timeouten.
                        if((received_bytes-prev_received_bytes)>=(1024 * 250)){
                            float value = (100.0f / (float)file_size) * (float) received_bytes;
                            if(!silent) progressBar(100, (int)value, (int)value);
                        }
                        if((received_bytes-prev_received_bytes)>=(1024 * 1024 * 2)){
                            writeCode(ConnectionCodes.ACK);
                            float value = (100.0f / (float)file_size) * (float) received_bytes;
                            if(!silent) progressBar(100, (int)value, (int)value);
                            prev_received_bytes = received_bytes;

                        }
                        if(received_bytes == file_size) {
                            writeCode(ConnectionCodes.ACK);
                            if(!silent) progressBar(0, 0, 100);
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
                if(!silent) progressBar(0, 0, 100);
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
            progressBar(0, 0, -1);
            // notifcationManager.cancel(1);
            return 0;
        }
    }

    private static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[4].getLineNumber();
    }




    // Interface override methods

    /**
     * @param request_id    by calling class specified integer
     * @param message       containing the path to the requested file/resource
     * @param data          object containing received data, must be cast to expected type
     * @param failure       false for successful networking, true for failure
     */
    @Override
    public void onNetworkingResult(int request_id, String message, Object data, boolean failure) {
        // default implementation
    }

    @Override
    public void onNetworkingProgress(String message) {
        // default implementation
    }
}
