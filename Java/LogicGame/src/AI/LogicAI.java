package AI;

import app.TwoWayChannel;
import app.TwoWayChannelBlockingQueue;

public class LogicAI implements Runnable{
    private final TwoWayChannel channel;
    
    public LogicAI(TwoWayChannelBlockingQueue channel){
        this.channel = channel;
    }

    @Override
    public void run() {
        throw new RuntimeException("Unimplemented");
    }
}
