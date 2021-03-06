package server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Specialized server for sending files to special clients
 * 
 * @author Justin Nelson
 * 
 */
public class Server implements Runnable {

    /**
     * Byte representing that we have reached the end of a file
     */
    public static final byte EOF = 4;

    /**
     * The port to connect to
     */
    private int port;

    /**
     * Creates a default server listening on port 4860
     */
    public Server() {
        this(4860);
    }

    /**
     * Creates a server listening on a specified port
     * 
     * @param port
     *            the port to listen on
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Starts the server listening for new connections.
     */
    @Override
    public void run() {
        ServerSocket serverSock = null;
        try {
            // create a new socket to listen on the port
            serverSock = new ServerSocket(port);
        } catch (IOException e1) {
            System.out.println("Could not listen on port " + port);
            System.out.println("Exiting...");
            return;
        }
        // forever loop
        while (true) {
            final Socket connection;
            try {
                System.out.println("Server is now listening on port " + port);
                // wait for new connection
                connection = serverSock.accept();
            } catch (IOException e) {
                // try a few times
                System.out.println("Something went wrong...Try again...");
                continue;
            }
            // once we have a connection, talk with the client
            new Thread() {
                public void run() {
                    try {
                        System.out.println("Handeling new connection");
                        handleConnection(connection);
                    } catch (IOException e) {
                        System.err.println("Something went wrong while dealing with a connection.");
                    }
                }
            }.start();
        }
    }

    /**
     * Handles requests from the client
     * 
     * @param s
     *            the new Socket representing the connection to the client
     * @throws IOException
     *             if an error occurs during communication
     */
    private static void handleConnection(Socket s) throws IOException {
        // get the appropriate readers and writers from the connection
        InputStream clientReader = s.getInputStream();
        BufferedOutputStream clientWriter = new BufferedOutputStream(s.getOutputStream());

        // string keeps track of the current directory, initially null
        String currentDirectory = null;
        byte[] buffer = new byte[1024];
        // until we get a quit request...
        while (true) {
            int bytesRead = clientReader.read(buffer);
            String clientInput = new String(buffer, 0, bytesRead).trim();
            // GET case
            if (clientInput.startsWith("GET")) {
                if (currentDirectory == null)
                    throw new IllegalArgumentException(
                            "Cannot get file, directory has not been specified.");
                handleGet(clientInput, clientWriter, currentDirectory);
            } else if (clientInput.trim().equalsIgnoreCase("QUIT")) {
                // QUIT case
                handleQuit(s);
                break;
            } else {
                // Directory listing case
                currentDirectory = handleDirReq(clientInput, clientReader, clientWriter);
            }
        }
    }

    /**
     * 
     * @param requestedDir
     *            the path on the server of the requested directory
     * @param clientReader
     *            data from the client
     * @param clientWriter
     *            send data to the client
     * @return the space-delimited string of directories
     * @throws IOException
     *             if anything goes wrong with the communication
     */
    private static String handleDirReq(String requestedDir, InputStream clientReader,
            OutputStream clientWriter) throws IOException {
        // get the directory request
        System.out.println("Requested directory: " + requestedDir);
        File dir = new File(requestedDir);
        if (!dir.isDirectory())
            throw new IllegalArgumentException("The requested file needs to be a directory.");

        // send the list of directories to the client
        String listOfDirs = combineArr(dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isDirectory() || true;
            }
        }), ' ');
        clientWriter.write(listOfDirs.getBytes());
        clientWriter.write(EOF);
        clientWriter.flush();
        System.out.println("Done sending directory listing.");
        return requestedDir.trim();
    }

    /**
     * Handles a QUIT command from a client
     * 
     * @param s
     *            the socket to close
     */
    private static void handleQuit(Socket s) {
        System.out.println("Handling a QUIT command.");
        try {
            s.close();
        } catch (IOException e) {
            // not much we can do here
            e.printStackTrace();
        }
    }

    /**
     * Handles a GET request from the client
     * 
     * @param getReq
     *            the string representation of the request
     * @param clientWriter
     *            send data to the client
     * @param currentDirectory
     *            get data from the client
     * @throws IOException
     *             if anything goes wrong with communication
     */
    private static void handleGet(String getReq, OutputStream clientWriter, String currentDirectory)
            throws IOException {
        // get the file that the client wants
        System.out.println("File requested: " + getReq);
        String[] twoHalves = getReq.split(" ");
        if (twoHalves.length != 2)
            throw new IllegalArgumentException("The command needs to be in the form: GET FileName");
        if (!twoHalves[0].equalsIgnoreCase("GET"))
            throw new IllegalArgumentException("The command needs to be in the form: GET FileName");
        
        //Combine the file name with the current directory to get the full file name
        String requestedFile = currentDirectory.endsWith(File.separator) ? currentDirectory
                + twoHalves[1] : currentDirectory + File.separator + twoHalves[1];

        System.out.println("Requested file: " + requestedFile);
        File file = new File(requestedFile);
        if (file.isDirectory())
            throw new IllegalArgumentException("Cannot download an entire directory!");
        FileInputStream fin = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fin.read(buffer)) > 0) {
            clientWriter.write(buffer, 0, bytesRead);
        }
        clientWriter.write(EOF);
        clientWriter.flush();
        System.out.println("Done sending file!!");
    }

    /**
     * simple method that creates a space delimited string of the file names
     * 
     * @param arr
     *            the file arr to combine
     * @param separator
     *            the character to be used to delimit the string
     * @return a string representation of the file[]
     */
    private static String combineArr(File[] arr, char separator) {
        StringBuilder ret = new StringBuilder();
        for (File s : arr) {
            if (s.getName().contains(" "))
                ret.append("\"" + s.getName() + "\"").append(separator);
            else
                ret.append(s.getName()).append(separator);
        }
        return ret.substring(0, ret.length() - 1);
    }

    /**
     * Main entry point for the server
     * 
     * @param args
     *            not used
     */
    public static void main(String[] args) {
        Server s = new Server();
        s.run();
    }
}
