package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TwoWayChannelSocket implements TwoWayChannel{
    private final BufferedReader in;
    private final PrintWriter out;
    
    public TwoWayChannelSocket(Socket socket) throws IOException{
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void send(String message){
        this.out.println(message);
    }
    
    @Override
    public String listen() throws IOException{
        return this.in.readLine();
    }
    
}
