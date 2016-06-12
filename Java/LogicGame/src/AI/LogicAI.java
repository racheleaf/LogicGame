package AI;

import app.TwoWayChannel;

public class LogicAI implements Runnable{
    private final TwoWayChannel channel;
    
    public LogicAI(TwoWayChannel channel){
        this.channel = channel;
    }

    @Override
    public void run() {
        throw new RuntimeException("Unimplemented");
    }
}
