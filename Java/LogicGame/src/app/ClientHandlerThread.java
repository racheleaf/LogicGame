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
    private final BlockingQueue<String> fromMaster;
    private final BlockingQueue<String> toMaster;
    
    /**
     * Constructs a ClientServerThread
     * @param socket Socket through which client connects
     * @param playerID number of player (0-3)
     * @param fromMaster BlockingQueue of messages from 
     * the server; ONLY this ClientHandlerThread should 
     * read from this BlockingQueue, and ONLY the server 
     * should write to it 
     * @param toMaster BlockingQueue of messages to the 
     * server; All four ClientHandlerThreads should write
     * to this BlockingQueue, and ONLY the server should 
     * read from it.  
     */
    public ClientHandlerThread(Socket socket, int playerID, 
            BlockingQueue<String> fromMaster, BlockingQueue<String>toMaster){
        this.socket = socket;
        this.playerID = playerID;
        this.fromMaster = fromMaster;
        this.toMaster = toMaster;
    }
    
    /**
     * Precede a message with this client's ID and send
     * it to the server  
     * @param message a message to be sent to the server, 
     * written in an appropriate protocol 
     * @throws InterruptedException
     */
    private void informMaster(String message) throws InterruptedException{
        toMaster.put("Client " + playerID + ": " + message);
    }

    /**
     * Listens for a message from the server. Blocks until
     * a message is received.  
     * @return message from server
     * @throws InterruptedException
     */
    private String listenMaster() throws InterruptedException{
        String message = fromMaster.take();
        System.err.println("Received by Client " + playerID + ": " + message);
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
            informMaster("Ready.");

            String message = listenMaster();
            assert(message.equals("Game started, proceed."));
            
            out.println("Game has begun! Please set up your cards.");
            
            // SETUP PHASE
            
            // show each player own cards
            String cardLayout = listenMaster();
            out.println(cardLayout);
            
            // clients are shown only their own cards, and can request
            // swaps of adjacent cards.  
            
            // this loop continues until client enters "done" 
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                // Client handler sends request to server and receives response
                informMaster(line);
                String output = listenMaster();
                if (output.equals("done")){
                    out.println("Yay! Wait for other players to finish setup...");
                    break;
                }
                if (output != null){
                    out.println(output);
                }
            }
            
            /*TODO main phase of game*/

        } finally {
            out.close();
            in.close();
        }
    }
    
}
