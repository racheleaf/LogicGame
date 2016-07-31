package app;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class LogicApplet extends Applet implements ActionListener{
    private static final String LOCALHOST = "127.0.0.1";
    private static final int PORT = 24601;

    private static final int APPLET_WIDTH = 500;
    private static final int APPLET_HEIGHT = 400;

    private static final int MAX_CONNECTION_ATTEMPTS = 10;
    
    private Socket serverSocket;
    private BufferedReader in;
    private PrintWriter out;

    private TextField input, output;

//    private LogicFrame logicframe;
    
    /**
     * Connect to a LogicServer and return the connected socket.
     * @param server abort connection attempts if the server thread dies
     * @return socket connected to the server
     * @throws IOException if the connection fails
     */
    private static Socket connectToLogicServer() throws IOException {
        int attempts = 0;
        while (true) {
            try {
                Socket socket = new Socket(LOCALHOST, PORT);
                socket.setSoTimeout(3000);
                return socket;
            } catch (ConnectException ce) {
                if (++attempts > MAX_CONNECTION_ATTEMPTS) {
                    throw new IOException("Exceeded max connection attempts", ce);
                }
                try { Thread.sleep(attempts * 10); } catch (InterruptedException ie) { }
            }
        }
    }
    
    
    
    @Override
    public void init() {
        super.init();
        
        // connect to LogicServer
        try {
            this.serverSocket = connectToLogicServer();
            this.in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            this.out = new PrintWriter(serverSocket.getOutputStream(), true);
            //serverSocket.setSoTimeout(0); <-- THIS IS SUPER JANK 
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException();
        }
        // Construct the TextFields
        this.input = new TextField(40);
        this.output = new TextField(40);
        this.output.setEditable(false);
        Button b = new Button("Enter");
        b.addActionListener(this);
        
        // add elements
        this.add(input);
        this.add(b);
        this.add(output);

        // sets size of applet
        setSize(APPLET_WIDTH, APPLET_HEIGHT);
        
    }

    @Override
    public void start() {
        // for some reason this doesn't work
//        try{
//            for (String line = in.readLine(); line!= null; 
//                    line = in.readLine()){
//                output.setText(line);
//            }
//        } catch (IOException ioe){
//            ioe.printStackTrace();
//            throw new RuntimeException();
//        }
    }
    
    @Override
    public void actionPerformed(ActionEvent a) {
        out.println(input.getText());
    }
    
}