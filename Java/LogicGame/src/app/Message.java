package app;

import java.util.Optional;

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
    private final Optional<String> content; 
    
    private final Optional<InternalMessage> intMes;
    
    private final String messageType;
    
    /**
     * Constructor for String message
     * @param sender message sender
     * @param recipient message recipient
     * @param content message content
     */
    public Message(String sender, String recipient, boolean isExternal, String content){
        this.sender = sender;
        this.recipient = recipient;
        this.isExternal = isExternal;
        this.content = Optional.of(content);
        this.intMes = Optional.empty();
        this.messageType = "String";
    }
    
    /**
     * Constructor for InternalMessage
     * @param sender message sender
     * @param recipient message recipient
     * @param isExternal whether the message is meant for the client or not
     * @param intMes message internalmessage
     */
    public Message(String sender, String recipient, boolean isExternal, InternalMessage intMes) {
    	this.sender = sender;
    	this.recipient = recipient;
    	this.isExternal = isExternal;
    	this.content = Optional.empty();
    	this.intMes = Optional.of(intMes);
    	this.messageType = "InternalMessage";
    }
    
    /**
     * returns whether the message has string or internalmessage content
     * @return "String" if the content has String form 
     * or "InternalMessage" if the content has InternalMessage form
     */
    public String getMessageType() {
    	return messageType;
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
        return content.get();
    }
    
    /**
     * Get InternalMessage
     * @return the internalmessage of this message
     */
    public InternalMessage getInternalMessage() {
    	return intMes.get();
    }

    @Override
    public String toString(){
    	if (content.isPresent()) {
    		if (isExternal){
                return "EXT/FROM: " + sender + "/TO: " + recipient 
                        + "/" + content.get();             
            }
            else{
                return "INT/FROM: " + sender + "/TO: " + recipient 
                        + "/" + content.get();             
            }
    	}
        if (isExternal){
            return "EXT/FROM: " + sender + "/TO: " + recipient 
                    + "/" + intMes.get().toString();             
        }
        else{
            return "INT/FROM: " + sender + "/TO: " + recipient 
                    + "/" + intMes.get().toString();             
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
