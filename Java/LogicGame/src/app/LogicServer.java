package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import game.GameBoard;

/**
 * A server for Logic
 */
public class LogicServer {

    /*
     * Since this code is very heavily commented: 
     * comments of form // ... are explanatory
     * comments of form /* ... are for things to be changed/maintained
     * 
     * ^lol oops if this is documentation of documentation, does that make it
     * meta-documentation ><
     */
    
    // ports are where clients connect to the server.  
    // A server socket has 65535 ports, numbered 0-65535
    private static final int DEFAULT_PORT = 24601;
    private static final int MAX_PORT = 65535;
    
    private final ServerSocket serverSocket;

    // this stores all game data.  This server processes I/O and sends the 
    // approprgate instructions / retrieves the appropriate information from
    // gameBoard
    private final GameBoard gameBoard = new GameBoard();
        
    private final List<BlockingQueue<String>> toHandlers = Arrays.asList(
            new LinkedBlockingQueue<>(), 
            new LinkedBlockingQueue<>(), 
            new LinkedBlockingQueue<>(), 
            new LinkedBlockingQueue<>());
    
    private final LinkedBlockingQueue<String> fromHandlers = new LinkedBlockingQueue<>();
    
    private final List<String> status = Arrays.asList("Inactive","Inactive","Inactive","Inactive");
    
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
     * Sends a client a message
     * @param clientID ID of client (0-3)
     * @param message message to be sent, in an appropriate
     * protocol
     * @throws InterruptedException 
     */
    private void informClient(int clientID, String message) throws InterruptedException{
        toHandlers.get(clientID).put(message);
    }

    /**
     * Sends all clients a message
     * @param message message to be sent, in an appropriate 
     * protocol
     * @throws InterruptedException
     */
    private void informAllClients(String message) throws InterruptedException{
        for (BlockingQueue<String> channel : toHandlers){
            channel.put(message);
        }
    }
    
    /**
     * Listens for messages from clients
     * @return message from client
     * @throws InterruptedException
     */
    private String listenClients() throws InterruptedException{
        String message = fromHandlers.take();
        System.err.println("Received by master: " + message);
        assert(message.substring(0, 10).matches("Client [0-3]: "));
        return message;
    }
    
    /**
     * Sender ID of a message from a client 
     * @param messageFromClient a message sent by a 
     * ClientHandlerThread's informMaster method
     * @return ID of sender
     */
    private static int getSenderID(String messageFromClient){
        return Character.getNumericValue(messageFromClient.charAt(7));
    }

    /**
     * Content of message from client
     * @param messageFromClient a message sent by a 
     * ClientHandlerThread's informMaster method
     * @return message, without the prepended client ID
     * information
     */
    private static String getMessageText(String messageFromClient){
        return messageFromClient.substring(10);
    }
    
    /**
     * Run the server, listening for client connections and handling them.
     * Never returns unless an exception is thrown.
     * 
     * @throws IOException if the main server socket is broken
     *                     (IOExceptions from individual clients do *not* terminate serve())
     * @throws InterruptedException 
     */
    public void serve() throws IOException, InterruptedException {

        // CONNECTION PHASE
        
        int numPlayers = 0;
        
        // While there are fewer than 4 players, wait for more connections, and
        // when a connection comes make a new thread that handles it
        while (numPlayers<4) {
            // wait for a client to connect.  socket is the client's I/O socket
            Socket socket = serverSocket.accept();
            
            // handle a connection
            Thread thread = new Thread(new ClientHandlerThread(socket, numPlayers, 
                    toHandlers.get(numPlayers), fromHandlers));                
            
            thread.start();
            
            String message = listenClients();
            assert(message.matches("Client [0-3]: Ready for setup."));
            numPlayers++;
        }
        
        // all players connected!
        
        informAllClients("Game started, proceed.");
        
        // SETUP PHASE
        
        for (int playerID=0; playerID<4; playerID++){
            informClient(playerID, 
                    gameBoard.showPlayerOwnCards(playerID));
        }
        
        // tracks which players are done setting up
        Set<Integer> players = new HashSet<>(Arrays.asList(0,1,2,3));
        Set<Integer> isDone = new HashSet<>();
        
        // this loop continues until all clients are done
        for (String line = listenClients(); line!=null; line = listenClients()){
            
            int senderID = getSenderID(line);
            String message = getMessageText(line);
            if (message.equals("done")){
                isDone.add(senderID);
                if (isDone.equals(players)){
                    break;
                }
            }
            else{
                handleRequestSetupPhase(message, senderID);                
            }
        }

        // MAIN PHASE
        
        informAllClients("Setup is complete.  Game has begun!");
        
        for (int playerID=0; playerID<4; playerID++){
            informClient(playerID, 
                    gameBoard.showPlayerViewOfBoard(playerID));
        }
        
        status.set(2, "Pass");
        informAllClients("Player 2 to pass.");
        
        // this loop continues until someone declares
        for (String line = listenClients(); line!=null; line = listenClients()){
            
            int senderID = getSenderID(line);
            String message = getMessageText(line);
            
            if (message.equals("declare")){
                informAllClients("Player " + senderID + " declared!");
                // DO STUFF HERE
                break;
            }
            
            handleRequestMainPhase(message, senderID);
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
     * @throws InterruptedException 
     */
    private void handleRequestSetupPhase(String in, int playerID) throws InterruptedException{
        String regex = "(view)|(help)|(swap [0-5])";
        String helpMessage = "same";//TODO
        if (!in.matches(regex)){
            // invalid input
            informClient(playerID, helpMessage);
        }
        else if(in.equals("view")){
            informClient(playerID, gameBoard.showPlayerOwnCards(playerID));
        }
        else if(in.equals("help")){
            informClient(playerID, helpMessage);
        }
        else if(in.matches("swap [0-5]")){
            Integer position = Character.getNumericValue(in.charAt(5));
            gameBoard.swapTwoEqualCards(playerID, position);
            informClient(playerID, gameBoard.showPlayerOwnCards(playerID));
        }
        else{
            // should not get here
            throw new UnsupportedOperationException("Should not get here");
        }
    }
    
    /**
     * Handler for client input in the main game phase
     * @param in client message.   
     * @param playerID 0-3
     * @param playerState "Inactive", "Pass", "Guess", or "Show"
     * @return message to client, or null
     * @throws InterruptedException 
     */
    private void handleRequestMainPhase(String in, int playerID) throws InterruptedException{
        String playerState = status.get(playerID);
        
        String regex = "(view)|(help)|(pass [0-5])|(guess [0-3] [0-5] [1-12])|(show [0-5])";
        String helpMessage = "same";//TODO
        
        if (!in.matches(regex)){
            // invalid input
            informClient(playerID, helpMessage);
        }
        else if (in.equals("view")){
            informClient(playerID, gameBoard.showPlayerViewOfBoard(playerID));
        }
        else if (in.equals("help")){
            informClient(playerID, helpMessage);
        }
        else if (in.matches("pass [0-5]")){
            if (!playerState.equals("Pass")){
                informClient(playerID, "Your state is: "+playerState+
                        ". You cannot pass right now.");
            }
            else{
                // TODO enforce: this card cannot be already faceup
                Integer position = Character.getNumericValue(in.charAt(5));
                gameBoard.revealCardToPartner(playerID, position);
                informAllClients("Player " + playerID + " passed card " + position + "!");
                for (int player=0; player<4; player++){
                    informClient(player, gameBoard.showPlayerViewOfBoard(player));
                }
                status.set(playerID, "Inactive");
                int partnerID = (playerID+2)%4;
                informAllClients("Player " + partnerID + " to guess!");
                status.set(partnerID, "Guess");
            }
        }
        else if (in.matches("guess [0-3] [0-5] [1-12]")){
            if (!playerState.equals("Guess")){
                informClient(playerID, "Your state is: "+playerState+
                        ". You cannot guess right now.");
            }     
            else{
                // TODO enforce: player cannot guess unguessable card
                String[] tokenizedInput = in.split(" ");
                int targetPlayer = Integer.valueOf(tokenizedInput[1]);
                int guessPosition = Integer.valueOf(tokenizedInput[2]);
                int guessRank = Integer.valueOf(tokenizedInput[3]);
                boolean guessCorrect = gameBoard.guess(playerID, targetPlayer, 
                        guessPosition, guessRank);
                if (guessCorrect){
                    informAllClients("Player " + playerID + " correctly guessed card "+
                            guessPosition + " of player " + targetPlayer+": " + guessRank + "!");
                    gameBoard.revealCardToAll(targetPlayer, guessPosition);
                    for (int player=0; player<4; player++){
                        informClient(player, gameBoard.showPlayerViewOfBoard(player));
                    }
                    status.set(playerID, "Inactive");
                    int nextPlayerID = (playerID+3)%4;
                    informAllClients("Player " + nextPlayerID + " to pass!");
                    status.set(nextPlayerID, "Pass");
                }
                else{
                    informAllClients("Player " + playerID + " incorrectly guessed card "+
                            guessPosition + " of player " + targetPlayer+": " + guessRank + "!");
                    for (int player=0; player<4; player++){
                        informClient(player, gameBoard.showPlayerViewOfBoard(player));
                    }
                    status.set(playerID, "Show");
                    informAllClients("Player " + playerID + " must show a card!");
                }
            }
        }
        else if (in.matches("show [0-5]")){
            if (!playerState.equals("Show")){
                informClient(playerID, "Your state is: "+playerState+
                        ". You cannot show right now.");
            }            
            else{
                // TODO enforce: this card cannot be already faceup
                Integer position = Character.getNumericValue(in.charAt(5));
                gameBoard.revealCardToAll(playerID, position);
                informAllClients("Player " + playerID + " revealed card " + position + "!");
                for (int player=0; player<4; player++){
                    informClient(player, gameBoard.showPlayerViewOfBoard(player));
                }
                status.set(playerID, "Inactive");
                int nextPlayerID = (playerID+3)%4;
                informAllClients("Player " + nextPlayerID + " to pass!");
                status.set(nextPlayerID, "Pass");
            }
        }
        else{
            throw new UnsupportedOperationException("Should not get here");            
        }
    }
    
    /**
     * Main
     * @param args bleh
     */
    public static void main(String[] args){
        /*TODO provide command line interface??*/
        try{
            runLogicServer(DEFAULT_PORT);
        } catch (IOException e){
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Starts a Logic Server 
     * @param port the port at which this LogicServer listens
     * @throws IOException
     * @throws InterruptedException 
     */
    private static void runLogicServer(int port) throws IOException, InterruptedException{
        LogicServer server = new LogicServer(port);
        // starts the main server thread
        server.serve();
    }

}
