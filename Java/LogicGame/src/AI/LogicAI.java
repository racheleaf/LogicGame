package AI;

import java.io.IOException;
import java.util.*;

import app.ClientTransmitter;
import app.InternalMessage;

//import app.TwoWayChannel;
//import app.TwoWayChannelBlockingQueue;

// TODO EVERYTHING

public class LogicAI implements Runnable{
	
    private final int playerID;
    private final ClientTransmitter transmitter;
    
    public LogicAI(int playerID, ClientTransmitter transmitter){
        this.playerID = playerID;
        this.transmitter = transmitter;
        throw new RuntimeException("Unimplemented"); //TODO
    }

    @Override
    public void run() {
        
//    	try {
//    		assert(channel.listen().equals("Welcome to Logic! You are player #" + playerID + "."));
//        	assert(channel.listen().equals("Please wait for four players to arrive."));
//            
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        
    	
    	
        throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Checks to make sure an InternalMessage matches a desired type.
     * @param message InternalMessage being checked
     * @param type the type InternalMessage should match
     * @throws InterruptedException
     */
    private void checkMessage(InternalMessage message, String type) throws InterruptedException{
        assert(message.getType().equals(type));
    }
    
    private void handleConnection() throws InterruptedException {
    	
    }
    
    private void handleConnectionPhase() throws InterruptedException {
    	transmitter.informServer(false, "Finished connecting.");
    }
    
    private void handleSetupPhase() throws InterruptedException {
    	
//    	try {
//    		assert(channel.listen().equals("Please set up your cards.  \r\n"
//                + "Type 'view' to see your cards, "
//                + "'help' for help message, "
//                + "and 'swap x' to swap card x.  "
//                + "Type 'done' to finish."));
//    	} catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    
    private void handleMainandDeclarePhase() {
    	
    }
    
}