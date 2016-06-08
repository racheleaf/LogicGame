/**
 * 
 */
package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * A thread that handles a client's connection
 */
public class ClientHandlerThread implements Runnable{

    private final int playerID;

    // Controls communication to/from client
    private final Socket socket;
    
    // Controls communication to/from server
    private final BlockingQueue<String> fromServer;
    private final BlockingQueue<String> toServer;
        
    /**
     * Constructs a ClientServerThread
     * @param socket Socket through which client connects
     * @param playerID number of player (0-3)
     * @param fromServer BlockingQueue of messages from 
     * the server; ONLY this ClientHandlerThread should 
     * read from this BlockingQueue, and ONLY the server 
     * should write to it 
     * @param toServer BlockingQueue of messages to the 
     * server; All four ClientHandlerThreads should write
     * to this BlockingQueue, and ONLY the server should 
     * read from it.  
     */
    public ClientHandlerThread(Socket socket, int playerID, 
            BlockingQueue<String> fromServer, BlockingQueue<String>toServer){
        this.socket = socket;
        this.playerID = playerID;
        this.fromServer = fromServer;
        this.toServer = toServer;
    }
    
    /**
     * Precede a message with this client's ID and send
     * it to the server  
     * @param message a message to be sent to the server, 
     * written in an appropriate protocol 
     * @throws InterruptedException
     */
    private void informServer(String message) throws InterruptedException{
        toServer.put("Client " + playerID + ": " + message);
    }

    /**
     * Listens for a message from the server. Blocks until
     * a message is received.  
     * @return message from server
     * @throws InterruptedException
     */
    private String listenServer() throws InterruptedException{
        String message = fromServer.take();
        System.err.println("Received by Client " + playerID + ": \n" + message);
        return message;
    }

    
    @Override
    public void run() {
        try {
            handleConnection();  
        } catch (IOException ioe) {
            ioe.printStackTrace(); 
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }  
    }
    
        
    /**
     * Handle client connection. Returns when client disconnects.
     * 
     * @throws IOException if the connection encounters an error or terminates unexpectedly
     * @throws InterruptedException 
     */
    private void handleConnection() throws IOException, InterruptedException {
        // I/O streams from player
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        try {
            
            // CONNECTION PHASE
            
            out.println("Welcome to Logic! You are player #" + playerID + ".");
            out.println("Please wait for four players to arrive.");

            // phase while clients are connecting
            // all threads go to sleep until the main server thread begins the game
            // by calling threadControls[playerID].notify()
            informServer("Ready for setup.");

            String message = listenServer();
            assert(message.equals("Game started, proceed."));
            
            out.println("Please set up your cards.");
            
            // SETUP PHASE
            
            // show each player own cards
            String cardLayout = listenServer();
            out.println(cardLayout);
            
            String instructions = listenServer();
            out.println(instructions);
            // clients are shown only their own cards, and can request
            // swaps of adjacent cards.  
            
            // this loop continues until client enters "done" 
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                // Client handler sends request to server and receives response
                informServer(line);
                if (line.equals("done")){
                    out.println("Yay! Wait for other players to finish setup...");
                    break;
                }
                String output = listenServer();
                if (output != null){
                    out.println(output);
                }
            }
            
            message = listenServer();
            assert(message.equals("Setup is complete.  Game has begun!"));
            out.println(message);
            
            // whether or not the game has ended
            boolean gameEnded = false;
            
            // from here on, receiving messages from main server
            // and sending requests to main server happens 
            // asynchronously, so all clients receive updates
            // from server real-time
            new Thread(new Runnable(){
                public void run(){
                	String message = "";
                    while (!message.matches("Players [0-3] and [0-3] win!")){
                        try {
                        	message = listenServer();
                            out.println(message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }                        
                    }
                }
            }).start();
            
            // reads client input and sends to main server
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                informServer(line);
            }
            
        } finally {
            out.close();
            in.close();
        }
    }
    
}
