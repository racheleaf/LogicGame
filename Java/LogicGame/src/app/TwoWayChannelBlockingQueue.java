package app;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class representing one end of a two-way communication channel
 */
public class TwoWayChannelBlockingQueue implements TwoWayChannel {
    private final BlockingQueue<String> out;
    private final BlockingQueue<String> in;
    
    /**
     * Standard constructor
     */
    public TwoWayChannelBlockingQueue(){
        this.out = new LinkedBlockingQueue<>();
        this.in = new LinkedBlockingQueue<>();
    }
    
    /**
     * Internally-used constructor, only for constructing getReverseChannel 
     * @param out
     * @param in
     */
    private TwoWayChannelBlockingQueue(BlockingQueue<String> out, BlockingQueue<String> in){
        this.out = out;
        this.in = in;
    }
    
    @Override
    public void send(String message) throws InterruptedException{
        this.out.put(message);
    }
    
    @Override
    public String listen() throws InterruptedException{
        return this.in.take();
    }
    
    /**
     * Get the reversed TwoWayChannel
     * @return the TwoWayChannel in the opposite direction
     */
    public TwoWayChannelBlockingQueue getReverseChannel(){
        return new TwoWayChannelBlockingQueue(in, out);
    }
    
}
