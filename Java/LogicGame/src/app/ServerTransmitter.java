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
    
    private final List<BlockingQueue<String>> toClients = Arrays.asList(
            new LinkedBlockingQueue<>(), 
            new LinkedBlockingQueue<>(), 
            new LinkedBlockingQueue<>(), 
            new LinkedBlockingQueue<>());
    
    // Blocking queue for transmission from clieht handler threads to server.  
    // Each client's request is of the form "Client x: blah", so server knows 
    // who to respond to.  
    private final BlockingQueue<String> fromClients = new LinkedBlockingQueue<>();
    
    public ServerTransmitter(){
    }
    
    /**
     * Sends a client a message
     * @param clientID ID of client (0-3)
     * @param message message to be sent, in an appropriate
     * protocol
     * @throws InterruptedException 
     */
    public void informClient(int clientID, String message) throws InterruptedException{
        toClients.get(clientID).put(message);
    }

    /**
     * Sends all clients a message
     * @param message message to be sent, in an appropriate 
     * protocol
     * @throws InterruptedException
     */
    public void informAllClients(String message) throws InterruptedException{
        for (BlockingQueue<String> channel : toClients){
            channel.put(message);
        }
    }
    
    /**
     * Listens for messages from clients
     * @return message from client
     * @throws InterruptedException
     */
    public String listenClients() throws InterruptedException{
        String message = fromClients.take();
        System.err.println("Received by master: \n" + message);
        assert(message.substring(0, 10).matches("Client [0-3]: "));
        return message;
    }

    /**
     * Sender ID of a message from a client 
     * @param messageFromClient a message sent by a 
     * ClientHandlerThread's informMaster method
     * @return ID of sender
     */
    public static int getSenderID(String messageFromClient){
        return Character.getNumericValue(messageFromClient.charAt(7));
    }

    /**
     * Content of message from client
     * @param messageFromClient a message sent by a 
     * ClientHandlerThread's informMaster method
     * @return message, without the prepended client ID
     * information
     */
    public static String getMessageText(String messageFromClient){
        return messageFromClient.substring(10);
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
