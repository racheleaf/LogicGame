package AI;

import java.io.IOException;

import app.TwoWayChannel;
import app.TwoWayChannelBlockingQueue;

public class LogicAI implements Runnable{
    private final int playerID;
    private final TwoWayChannel channel;
    
    
    public LogicAI(int playerID, TwoWayChannelBlockingQueue channel){
        this.playerID = playerID;
        this.channel = channel;
    }

    @Override
    public void run() {
        
        try {
            assert(channel.listen().equals("Welcome to Logic! You are player #" + playerID + "."));
            assert(channel.listen().equals("Please wait for four players to arrive."));

            // continue implementing here
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        throw new RuntimeException("Unimplemented");
    }
}
