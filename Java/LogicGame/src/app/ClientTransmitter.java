package app;

import java.util.concurrent.BlockingQueue;

/**
 * A class for a client handler thread to listen to and request information from 
 * a server thread
 */
public class ClientTransmitter {

    private final BlockingQueue<Message> fromServer;
    private final BlockingQueue<Message> toServer;
    private final int playerID; // in range 0-3
    
    /**
     * Constructs a ClientTransmitter for player playerID given playerID and 
     * the necessary BlockingQueues
     * @param playerID ID of player
     * @param fromServer channel for communication from server to a ClientHandler
     * @param toServer channel for communication from a ClientHandler to a server
     */
    public ClientTransmitter(int playerID, BlockingQueue<Message> fromServer, 
            BlockingQueue<Message> toServer){
        this.playerID = playerID;
        this.fromServer = fromServer;
        this.toServer = toServer;
    }
    
    /**
     * Send a message to the server   
     * @param isExternal true if message is relayed from client, false if 
     * message is between handler and server for maintaining gamestate
     * @param message a message to be sent to the server, 
     * written in an appropriate protocol 
     * @throws InterruptedException
     */
    public void informServer(boolean isExternal, String message) throws InterruptedException{
        toServer.put(new Message("Client " + playerID,"Server",isExternal, message));
    }
    
    /**
     * Listens for a message from the server. Blocks until
     * a message is received.  
     * @return message from server
     * @throws InterruptedException
     */
    public Message listenServer() throws InterruptedException{
        Message message = fromServer.take();
        System.err.println("Received:" + message.toString());
        assert(message.getSender().equals("Server"));
        assert(message.getRecipient().equals("Client "+playerID));
        return message;
    }


}
