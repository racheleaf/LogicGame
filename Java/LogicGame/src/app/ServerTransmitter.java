package app;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerTransmitter {
    // BlockingQueues are queues with the added functionality that if you
    // try to pop an empty queue, your thread will wait (terminology: "block") 
    // until something is placed on the BlockingQueue.  This is useful for 
    // communication between asynchronous threads, especially when one thread
    // has to wait for another to get to some point before it proceeds
    
    private final List<BlockingQueue<Message>> toClients = Arrays.asList(
            new LinkedBlockingQueue<>(), 
            new LinkedBlockingQueue<>(), 
            new LinkedBlockingQueue<>(), 
            new LinkedBlockingQueue<>());
    
    // Blocking queue for transmission from clieht handler threads to server.  
    // Each client's request is of the form "Client x: blah", so server knows 
    // who to respond to.  
    private final BlockingQueue<Message> fromClients = new LinkedBlockingQueue<>();
    
    public ServerTransmitter(){
    }
    
    /**
     * Sends a client a message
     * @param clientID ID of client (0-3)
     * @param isExternal true if message should be relayed to client, false if
     * message is directed to client handler to maintain gamestate
     * @param message message to be sent, in an appropriate
     * protocol
     * @throws InterruptedException 
     */
    public void informClient(int clientID, boolean isExternal, String message) throws InterruptedException{
        toClients.get(clientID).put(
                new Message("Server", "Client "+clientID, isExternal, message));
    }

    /**
     * Sends all clients a message
     * @param isExternal true if message should be relayed to client, false if
     * message is directed to client handler to maintain gamestate
     * @param message message to be sent, in an appropriate 
     * protocol
     * @throws InterruptedExceptionj
     */
    public void informAllClients(boolean isExternal, String message) throws InterruptedException{
        for (int i=0; i<4; i++){
            informClient(i, isExternal, message);
        }
    }
    
    /**
     * Listens for messages from clients
     * @return message from client
     * @throws InterruptedException
     */
    public Message listenClients() throws InterruptedException{
        Message message = fromClients.take();
        System.err.println("Received:" + message.toString());
        assert(message.getSender().matches("Client [0-3]"));
        assert(message.getRecipient().equals("Server"));
        return message;
    }
    
    /**
     * Get the ClientTransmitter of a certain player's handler
     * @param playerID ID of player
     * @return player playerID's client transmitter 
     */
    public ClientTransmitter getClientTransmitter(int playerID){
        return new ClientTransmitter(playerID, toClients.get(playerID), fromClients);
    }
    
    
    
}
