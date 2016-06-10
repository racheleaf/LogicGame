package app;

import java.util.concurrent.BlockingQueue;

/**
 * A class for a client handler thread to listen to and request information from 
 * a server thread
 */
public class ClientTransmitter {

    private final BlockingQueue<String> fromServer;
    private final BlockingQueue<String> toServer;
    private final int playerID; // in range 0-3
    
    /**
     * Constructs a ClientTransmitter for player playerID given playerID and 
     * the necessary BlockingQueues
     * @param playerID ID of player
     * @param fromServer channel for communication from server to a ClientHandler
     * @param toServer channel for communication from a ClientHandler to a server
     */
    public ClientTransmitter(int playerID, BlockingQueue<String> fromServer, 
            BlockingQueue<String> toServer){
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
    public void informServer(String message) throws InterruptedException{
        toServer.put("Client " + playerID + ": " + message);
    }
    
    /**
     * Listens for a message from the server. Blocks until
     * a message is received.  
     * @return message from server
     * @throws InterruptedException
     */
    public String listenServer() throws InterruptedException{
        String message = fromServer.take();
        System.err.println("Received by Client " + playerID + ": \n" + message);
        return message;
    }


}
