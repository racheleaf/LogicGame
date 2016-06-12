package app;

import java.io.IOException;

public interface TwoWayChannel {

    /**
     * Sends a message
     * @param message message to be sent
     * @throws InterruptedException
     */
    public void send(String message) throws InterruptedException;

    /**
     * Listens for a message 
     * @return message sent by other end of the two-way channel
     * @throws InterruptedException
     */
    public String listen() throws InterruptedException, IOException;
    
    /**
     * Closes connection
     */
    public void close() throws IOException;

    /**
     * Closes output
     */
    public void closeOut();

    /**
     * Closes input
     */
    public void closeIn() throws IOException;

}
