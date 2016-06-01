package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import game.GameBoard;

public class LogicServer {

    // ports are where clients connect to the server.  
    // A server socket has 65535 ports, numbered 0-65535
    private static final int DEFAULT_PORT = 1337;
    private static final int MAX_PORT = 65535;
    
    private final ServerSocket serverSocket;

    // this stores all game data.  This server processes I/O and sends the 
    // approprgate instructions / retrieves the appropriate information from
    // gameBoard
    private final GameBoard gameBoard = new GameBoard();
    
    // player counter, used in the initial phase while players are connecting.  
    // when 4 players arrive the game starts 
    private int numPlayers = 0;
    
    // since clients work asynchronously we use a different thread to handle
    // each client.  This array provides a reference to these threads
    private final Thread[] clientThreads = new Thread[4];
    
    // Sometimes a player's thread has to "wait" - e.g. when it isn't his turn.  
    // This is achieved by the .wait() function - for any object obj, if a thread
    // calls obj.wait() it goes to sleep until another thread calls obj.notify()
    // 
    // Here, basically player x's handler thread calls threadControls[x].wait when 
    // he has to wait, and the master server thread calls threadControls[x].notify 
    // when player x should stop waiting
    /* TODO this is jank */
    private final Object[] threadControls = {new Object(), new Object(), new Object(), new Object()}; 
    
    /**
     * Make a LogicServer that listens for connections on a specified port.   
     * 
     * @param port port number, requires 0 <= port <= 65535
     * @throws IOException if an error occurs opening the server socket
     */
    public LogicServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
    
    /**
     * Run the server, listening for client connections and handling them.
     * Never returns unless an exception is thrown.
     * 
     * @throws IOException if the main server socket is broken
     *                     (IOExceptions from individual clients do *not* terminate serve())
     */
    public void serve() throws IOException {
        //TODO bug: numPlayers is very very not threadsafe
        
        // While there are fewer than 4 players, wait for more connections, and
        // when a connection comes make a new thread that handles it
        while (numPlayers<4) {
            // wait for a client to connect.  socket is the client's I/O socket
            Socket socket = serverSocket.accept();
            
            // handle a connection
            // Basically this creates and starts a new thread that handles the 
            // connection with a connecting client.  Don't worry about this, this 
            // is boilerplate code.
            Thread thread = new Thread(new Runnable(){
                public void run(){
                    try {
                        handleConnection(socket, numPlayers-1);  
                    } catch (IOException ioe) {
                        ioe.printStackTrace(); // but don't terminate serve()
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }                                    
                }
            });

            // Remembers the handler threads
            
            clientThreads[numPlayers] = thread;
            numPlayers++; //TODO synchronization issues here, this is jank
            
            thread.start();
        }
        
        // After displaying hello message, handler threads sleep until 
        // the master server thread begins the game.  
        // This code wakes up the clients.  
        for (Object obj: threadControls){
            synchronized(obj){
                obj.notify();                
            }
        }
        //TODO bug: last player to enter gets his threadControl object notified before it's put on wait,
        //so he gets put on wait again and gets stuck

        //TODO main phase of game
        
    }
    
    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket socket where the client is connected
     * @param playerID 0-3 ID of player
     * @throws IOException if the connection encounters an error or terminates unexpectedly
     */
    private void handleConnection(Socket socket, int playerID) throws IOException {
        // I/O streams from player
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        try {
            out.println("Welcome to Logic! You are player #" + playerID + ".");
            out.println("Please wait for four players to arrive.");

            // phase while clients are connecting
            // all threads go to sleep until the main server thread begins the game
            // by calling threadControls[playerID].notify()
            synchronized(threadControls[playerID]){
                try{
                    threadControls[playerID].wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            
            out.println("Game has begun! Please set up your cards.");
            out.println(gameBoard.showPlayerOwnCards(playerID));
            
            // setup phase: clients are shown only their own cards, and can request
            // swaps of adjacent cards.  

            // this loop continues until client enters "done" 
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                // server reads client's input, performs the necessary computations,
                // and returns a message
                String output = handleRequestSetupPhase(line, playerID);
                if (output.equals("done")){
                    out.println("Yay! Wait for other players to finish setup...");
                    break;
                }
                if (output != null){
                    out.println(output);
                }
            }
            
            // TODO main phase of game

        } finally {
            out.close();
            in.close();
        }
    }

    /**
     * Handler for client input in the setup phase. 
     * @param in client message.  Should be of form "swap [position]" to swap
     * the card at position x with the adjacent card of same value, "view" to 
     * view cards, "help" to get help message, or "done".  
     * @return message to client.  If input was swap, returns the player's 
     * cards after the swap; if input was view, returns the player's cards; if 
     * input was help, returns a help message; if input was done, returns "done,"
     * which gets processed by the connection handler.  Any other inputs are 
     * diregarded and return a help message.  
     */
    private String handleRequestSetupPhase(String in, int playerID){
        String regex = "(view)|(help)|(done)|(swap [0-5])";
        String helpMessage = "same";//TODO
        if (!in.matches(regex)){
            // invalid input
            return helpMessage;
        }
        else if(in.equals("view")){
            return gameBoard.showPlayerOwnCards(playerID);
        }
        else if(in.equals("help")){
            return helpMessage;
        }
        else if(in.equals("done")){
            return "done";
        }
        else if(in.matches("swap [0-5]")){
            Integer position = Character.getNumericValue(in.charAt(5));
            gameBoard.swapTwoEqualCards(playerID, position);
            return gameBoard.showPlayerOwnCards(playerID);
        }
        else{
            // should not get here
            throw new UnsupportedOperationException("Should not get here");
        }
    }
    
    /**
     * Handler for client input in the main game phase
     * @param in client message.  Should be of form "swap [position]", "view", "help", or "done".  
     * @return message to client, or null
     */
    private String handleRequestMainPhase(String in, int playerID){
        //TODO
         throw new RuntimeException("Unimplemented");
    }
    
    /**
     * Main
     * @param args bleh
     */
    public static void main(String[] args){
        // TODO provide command line interface??
        try{
            runLogicServer(DEFAULT_PORT);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Starts a Logic Server 
     * @param port the port at which this LogicServer listens
     * @throws IOException
     */
    private static void runLogicServer(int port) throws IOException{
        LogicServer server = new LogicServer(port);
        // starts the main server thread
        server.serve();
    }

}
