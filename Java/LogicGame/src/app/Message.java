package app;

/**
 * An immutable class representing messages sent internally between LogicServer 
 * and ClientHandlerThreads
 */
public class Message {
    private final String sender;
    private final String recipient;
    
    private final boolean isExternal; // external messages are messages from
    // the client or or to be sent to the client; internal messages are 
    // messages between server and client handler to control gameflow
    private final String content; 
    
    /**
     * Constructor 
     * @param sender message sender
     * @param recipient message recipient
     * @param content message content
     */
    public Message(String sender, String recipient, boolean isExternal, String content){
        this.sender = sender;
        this.recipient = recipient;
        this.isExternal = isExternal;
        this.content = content;
    }
    
    /**
     * Get sender
     * @return the sender of this message
     */
    public String getSender(){
        return sender;
    }
    
    /**
     * Get recipient
     * @return the recipient of this message
     */
    public String getRecipient(){
        return recipient;
    }

    /**
     * Get content
     * @return the content of this message
     */
    public boolean isExternal(){
        return isExternal;
    }
    
    /**
     * Get content
     * @return the content of this message
     */
    public String getContent(){
        return content;
    }

    @Override
    public String toString(){
        if (isExternal){
            return "EXT/FROM: " + sender + "/TO: " + recipient 
                    + "/" + content;             
        }
        else{
            return "INT/FROM: " + sender + "/TO: " + recipient 
                    + "/" + content;             
        }
    }
    
    /**
     * Asserts that a message is Internal and has a specified content
     * @param message a message
     * @param content expected content
     */
    public static void verifyInternalMessage(Message message, String content){
        assert(!message.isExternal());
        assert(message.getContent().equals(content));
    }
}
