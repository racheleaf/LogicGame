/**
 * 
 */
package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A thread that handles a client's connection
 */
public class ClientHandlerThread implements Runnable{

    private final int playerID;

    // Controls communication to/from client
    private final TwoWayChannel clientChannel;
    
    // Controls communication to/from server
    private final ClientTransmitter transmitter; 
    
    private boolean gameIsOver = false; 
    
    
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
     * @throws IOException 
     */
    public ClientHandlerThread(Socket socket,  
            int playerID, ClientTransmitter transmitter) throws IOException{
        this.clientChannel = new TwoWayChannelSocket(socket);
        this.playerID = playerID;
        this.transmitter = transmitter;
        
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
                clientChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }  
    }
    

    
    /**
     * Asserts that a message is External and relays it to the client
     * @param message a message
     * @param content expected content
     * @throws InterruptedException 
     */
    private void relayExternalMessage(Message message) throws InterruptedException{
        assert(message.isExternal());
        clientChannel.send(message.getContent());
    }

    
    /**
     * Handle client connection. Returns when client disconnects.
     * 
     * @throws IOException if the connection encounters an error or terminates unexpectedly
     * @throws InterruptedException 
     */
    private void handleConnection() throws IOException, InterruptedException {
        
        try {
            handleConnectionPhase();
            handleSetupPhase();
            handleMainAndDeclarePhase();            
        } finally {
            clientChannel.closeOut();
            clientChannel.closeIn();
        }
    }
    
    /**
     * Handles connection phase
     * @throws InterruptedException
     */
    private void handleConnectionPhase() throws InterruptedException{
        clientChannel.send("Welcome to Logic! You are player #" + playerID + ".");
        clientChannel.send("Please wait for four players to arrive.");

        // phase while clients are connecting
        // all threads go to sleep until the main server thread begins the game
        // by calling threadControls[playerID].notify()
        transmitter.informServer(false, "Finished connecting.");

        Message.verifyInternalMessage(transmitter.listenServer(), 
                "Connection phase done.");
    }
    
    /**
     * Handles setup phase
     * @throws IOException
     * @throws InterruptedException
     */
    private void handleSetupPhase() throws IOException, InterruptedException{
        // instructions
        relayExternalMessage(transmitter.listenServer());
        // show each player own cards
        relayExternalMessage(transmitter.listenServer());
        
        
        // clients are shown only their own cards, and can request
        // swaps of adjacent cards.  
        
        // this loop continues until client enters "done" 
        for (String line = clientChannel.listen(); line != null; line = clientChannel.listen()) {
            // Client handler sends request to server and receives response
            if (line.equals("done")){
                clientChannel.send("Yay! Wait for other players to finish setup...");
                transmitter.informServer(false, "Finished setup.");
                break;
            }
            
            
            /*
             * message = listenServer();
            assert(message.equals("Setup is complete.  Game has begun!"));
            out.println(message);
            
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
                    out.println("Type anything to quit...");
                    gameIsOver = true;
                }
            }).start();
            */
            transmitter.informServer(true, line);
            relayExternalMessage(transmitter.listenServer());
        }
        
        Message.verifyInternalMessage(transmitter.listenServer(), 
                "Setup phase done.");

    }
    
    /**
     * Handles main and declare phase
     * @throws IOException
     * @throws InterruptedException
     */
    private void handleMainAndDeclarePhase() throws IOException, InterruptedException{
        // from here on, receiving messages from main server
        // and sending requests to main server happens 
        // asynchronously, so all clients receive updates
        // from server real-time
        new Thread(new Runnable(){
            public void run(){
                try{
                    for (Message message = transmitter.listenServer(); 
                            message != null; 
                            message = transmitter.listenServer()){
                        if (message.isExternal()){
                            clientChannel.send(message.getContent());                                
                        }
                        else{
                            Message.verifyInternalMessage(message, "Disconnect.");
                            clientChannel.send("Press enter to disconnect.");
                            break;
                        }
                    }                        
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
                gameIsOver = true;
            }
        }).start();

        // reads client input and sends to main server
        for (String line = clientChannel.listen(); line != null && !gameIsOver; 
                line = clientChannel.listen()) {
            transmitter.informServer(true,line);
        }
    }
    
}
