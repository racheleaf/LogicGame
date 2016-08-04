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

    private TextField input;
    private TextField output1, output2, output3, output4, output5, output6, output7, output8, output9, output10;

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
            serverSocket.setSoTimeout(0); //<-- THIS IS SUPER JANK 
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException();
        }
        // Construct the TextFields
        this.input = new TextField(40);
        this.output1 = new TextField(40);
        this.output2 = new TextField(40);
        this.output3 = new TextField(40);
        this.output4 = new TextField(40);
        this.output5 = new TextField(40);
        this.output6 = new TextField(40);
        this.output7 = new TextField(40);
        this.output8 = new TextField(40);
        this.output9 = new TextField(40);
        this.output10 = new TextField(40);
        this.output1.setEditable(false);
        this.output2.setEditable(false);
        this.output3.setEditable(false);
        this.output4.setEditable(false);
        this.output5.setEditable(false);
        this.output6.setEditable(false);
        this.output7.setEditable(false);
        this.output8.setEditable(false);
        this.output9.setEditable(false);
        this.output10.setEditable(false);
        Button b = new Button("Enter");
        b.addActionListener(this);
        
        // add elements
        this.add(input);
        this.add(b);
        this.add(output1);
        this.add(output2);
        this.add(output3);
        this.add(output4);
        this.add(output5);
        this.add(output6);
        this.add(output7);
        this.add(output8);
        this.add(output9);
        this.add(output10);

        // sets size of applet
        setSize(APPLET_WIDTH, APPLET_HEIGHT);
        
    }

    @Override
    public void start() {
        // a loop that listens for stuff from server and 
        // writes it to the applet
        new Thread(new Runnable(){
            public void run(){
                try{
                    for (String line = in.readLine(); line!= null; 
                            line = in.readLine()){
                        //System.out.println(line);
                        output10.setText(output9.getText());
                        output9.setText(output8.getText());
                        output8.setText(output7.getText());
                        output7.setText(output6.getText());
                        output6.setText(output5.getText());
                        output5.setText(output4.getText());
                        output4.setText(output3.getText());
                        output3.setText(output2.getText());
                        output2.setText(output1.getText());
                        output1.setText(line);
                    }
                } catch (IOException ioe){
                    ioe.printStackTrace();
                    throw new RuntimeException();
                }
                
            }
        }).start();
    }
    
    @Override
    public void actionPerformed(ActionEvent a) {
        out.println(input.getText());
    }
    
}