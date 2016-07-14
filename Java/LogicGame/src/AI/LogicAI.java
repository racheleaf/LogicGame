package AI;

import java.io.IOException;
import java.util.*;

//import app.TwoWayChannel;
//import app.TwoWayChannelBlockingQueue;

// TODO EVERYTHING

public class LogicAI implements Runnable{
	
    private final int playerID;
//    private final TwoWayChannel channel;
    
    public LogicAI(int playerID/*, TwoWayChannelBlockingQueue channel*/){
        this.playerID = playerID;
        throw new RuntimeException("Unimplemented"); //TODO
//        this.channel = channel;
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
        
    	runSetupPhase();
    	
        throw new RuntimeException("Unimplemented");
    }
    
    private void runSetupPhase() {
    	
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
    
}