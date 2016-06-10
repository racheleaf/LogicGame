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
    
    // clients connect to this
    private final ServerSocket serverSocket;

    // for transmitting information between server and client handlers
    private final ServerTransmitter transmitter = new ServerTransmitter();
    
    // this stores all game data.  This server processes I/O and sends the 
    // appropriate instructions / retrieves the appropriate information from
    // gameBoard
    private final GameBoard gameBoard = new GameBoard();

    
    // status.get(x) keeps track of status of player x.  Possible statuses are: 
    // Inactive: not player's turn
    // Pass: player's turn to pass
    // Guess: player's turn to guess
    // Show: player has to reveal a card
    // Declare: player is declaring all cards
    // IMPORTANT: to avoid situations where two players can potentially (for a
    // short period of time) both be non-Inactive, when updating turns we always 
    // change the currently-Pass/Guess/Show player to Inactive before changing the 
    // next player to non-Inactive.  
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
     * Sends all clients a message containing their latest 
     * view of the board
     * @throws InterruptedException 
     */
    private void refreshAllClientsViews() throws InterruptedException{
        for (int player=0; player<4; player++){
            transmitter.informClient(player, 
                    gameBoard.showPlayerViewOfBoard(player));
        }
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
    	
        serveConnectionPhase();
        
        serveSetupPhase();

        serveMainAndDeclarationPhase();
        
    }
    
    /**
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    private void serveConnectionPhase() throws IOException, InterruptedException{
        int numPlayers = 0;
        
        // While there are fewer than 4 players, wait for more connections, and
        // when a connection comes make a new thread that handles it
        while (numPlayers<4) {
            // wait for a client to connect.  socket is the client's I/O socket
            Socket socket = serverSocket.accept();
            
            // handle a connection
            Thread thread = new Thread(new ClientHandlerThread(socket, numPlayers,
                    transmitter.getClientTransmitter(numPlayers)));                
            
            thread.start();
            
            // Connected client thread will send back a message
            // saying "Ready for setup."  Wait for this message 
            // before proceeding, so that the clients are 
            // numbered correctly.  
            String message = transmitter.listenClients();
            assert(ServerTransmitter.getSenderID(message)==numPlayers); 
            assert(ServerTransmitter.getMessageText(message).equals("Ready for setup."));
            numPlayers++;
        }
        
        // all players connected!
        
        transmitter.informAllClients("Game started, proceed.");
    }

    /**
     * 
     * @throws InterruptedException
     */
    private void serveSetupPhase() throws InterruptedException{
        for (int player=0; player<4; player++){
            transmitter.informClient(player, 
                    gameBoard.showPlayerOwnCards(player));
        }
        transmitter.informAllClients("Type 'view' to see your cards, "
                + "'help' for help message, "
                + "and 'swap x' to swap card x. "
                + "Type 'done' to finish.");
        
        // isDone tracks which players are done setting up
        Set<Integer> players = new HashSet<>(Arrays.asList(0,1,2,3));
        Set<Integer> isDone = new HashSet<>();
        
        // this loop continues until all clients are done setting up
        for (String line = transmitter.listenClients(); line!=null; 
                line = transmitter.listenClients()){
            
            int senderID = ServerTransmitter.getSenderID(line);
            String message = ServerTransmitter.getMessageText(line);
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
        transmitter.informAllClients("Setup is complete.  Game has begun!");
    }
    
    private void serveMainAndDeclarationPhase() throws InterruptedException{
        // ID of player who declares, to be set when declaration occurs
        int declarer = -1;

        refreshAllClientsViews();
        
        transmitter.informAllClients("Type 'view' to see your cards, "
                + "'help' for help message, "
                + "'pass x' to pass card x, " 
                + "'guess x y z' to guess card y of player x is z," 
                + "and 'show x' to show card x. "
                + "Type 'declare' to declare.");
        
        // Starts the game.  Player 0 is first to guess, so 
        // Player 2 passes first.  
        status.set(2, "Pass");
        transmitter.informAllClients("Player 2 to pass.");
        
        // continually listens for and responds to clients' 
        // requests until someone declares
        for (String line = transmitter.listenClients(); line!=null; 
                line = transmitter.listenClients()){
            // parse requests
            int senderID = ServerTransmitter.getSenderID(line);
            String message = ServerTransmitter.getMessageText(line);
            
            if (message.equals("declare")){
                transmitter.informAllClients("Player " + senderID + " is declaring!");
                
                // changes all other players to Inactive mode, and marks declarer in state Declare 
                for (int i = 0; i < 4; i++) {
                    status.set(i, "Inactive");
                }
                status.set(senderID, "Declare");
                declarer = senderID;
                
                // refreshes board view so that all cards that declarer can see are visible to everyone
                gameBoard.makePlayerGameViewPublic(declarer);
                refreshAllClientsViews();
                break;
            }
            
            handleRequestMainPhase(message, senderID);
        }
        
        // DECLARATION 
        
        for (String line = transmitter.listenClients(); line != null; 
                line = transmitter.listenClients()) {
            // parse requests
            int senderID = ServerTransmitter.getSenderID(line);
            String message = ServerTransmitter.getMessageText(line);
            
            // shouldContinue is false only if declarer declares wrong
            boolean shouldContinue = handleRequestDeclarationPhase(message, senderID);
            
            // if declarer declares wrong, declarer and partner lose
            if (!shouldContinue) {
                gameBoard.makeAllCardsPublic();
                transmitter.informAllClients("Here is a view of all players' cards:");
                refreshAllClientsViews();
                transmitter.informAllClients("Players " + Integer.toString((declarer+1)%4) 
                    + " and " + Integer.toString((declarer+3)%4) + " win!");
                break;
            }
            
            // if all cards have been declared correctly, declarer and partner win
            if (!gameBoard.isMoreToDeclare()) {
                transmitter.informAllClients("Player " + declarer + " has declared all cards correctly."); 
                transmitter.informAllClients("Players " + Integer.toString(declarer) + " and " + Integer.toString((declarer+2)%4) + " win!");
                break;
            }
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
        String helpMessage = "Type 'view' to see your cards, "
                + "'help' for help message, "
                + "and 'swap x' to swap card x. "
                + "Type 'done' to finish.";
        if (!in.matches(regex)){
            // invalid input
            // discard input and return help message
            transmitter.informClient(playerID, helpMessage);
        }
        else if(in.equals("view")){
            transmitter.informClient(playerID, gameBoard.showPlayerOwnCards(playerID));
        }
        else if(in.equals("help")){
            transmitter.informClient(playerID, helpMessage);
        }
        else if(in.matches("swap [0-5]")){
            Integer position = Character.getNumericValue(in.charAt(5));
            gameBoard.swapTwoEqualCards(playerID, position);
            transmitter.informClient(playerID, gameBoard.showPlayerOwnCards(playerID));
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
        
        String regex = "(view)|(help)|(pass [0-5])|(guess [0-3] [0-5] ([1-9]|1[0-2]))|(show [0-5])";
        String helpMessage = "Type 'view' to see your cards, "
                + "'help' for help message, "
                + "'pass x' to pass card x, " 
                + "'guess x y z' to guess card y of player x is z," 
                + "and 'show x' to show card x. "
                + "Type 'declare' to declare.";
        
        if (!in.matches(regex)){
            // invalid input
            // discard input and return help message
            transmitter.informClient(playerID, helpMessage);
        }
        else if (in.equals("view")){
            transmitter.informClient(playerID, gameBoard.showPlayerViewOfBoard(playerID));
        }
        else if (in.equals("help")){
            transmitter.informClient(playerID, helpMessage);
        }
        else if (in.matches("pass [0-5]")){
            // in is of form "pass x" for x in 0-5
            
            // player must be in "Pass" state
            if (!playerState.equals("Pass")){
                
                transmitter.informClient(playerID, "Your state is: "+playerState+
                        ". You cannot pass right now.");
            }
            else{
                // TODO enforce: this card cannot be already faceup
                // parse input
                Integer position = Character.getNumericValue(in.charAt(5));
                
                // alters game state
                gameBoard.revealCardToPartner(playerID, position);
                
                // announce result of action to players
                transmitter.informAllClients("Player " + playerID + " passed card " + position + "!");
                refreshAllClientsViews();
                
                // update and announce whose turn it is 
                status.set(playerID, "Inactive");
                int partnerID = (playerID+2)%4;
                transmitter.informAllClients("Player " + partnerID + " to guess!");
                status.set(partnerID, "Guess");
            }
        }
        else if (in.matches("guess [0-3] [0-5] ([1-9]|1[0-2])")){
            // in is of form guess x y z for x in 0-3, y in 0-5, z in 1-12
            
            // player must be in "Guess" state
            if (!playerState.equals("Guess")){
                transmitter.informClient(playerID, "Your state is: "+playerState+
                        ". You cannot guess right now.");
            }     
            else{
                // TODO enforce: player cannot guess unguessable card
                // parse input
                String[] tokenizedInput = in.split(" ");
                int targetPlayer = Integer.valueOf(tokenizedInput[1]);
                int guessPosition = Integer.valueOf(tokenizedInput[2]);
                int guessRank = Integer.valueOf(tokenizedInput[3]);
                
                boolean guessCorrect = gameBoard.guess(playerID, targetPlayer, 
                        guessPosition, guessRank);
                
                if (guessCorrect){
                    // alter game state
                    gameBoard.revealCardToAll(targetPlayer, guessPosition);
                    
                    // announce result of action to players
                    transmitter.informAllClients("Player " + playerID + " correctly guessed card "+
                            guessPosition + " of player " + targetPlayer+": " + guessRank + "!");
                    refreshAllClientsViews();
                    
                    // update and announce whose turn it is
                    status.set(playerID, "Inactive");
                    int nextPlayerID = (playerID+3)%4;
                    transmitter.informAllClients("Player " + nextPlayerID + " to pass!");
                    status.set(nextPlayerID, "Pass");
                }
                else{
                    // announce result of action to players
                    transmitter.informAllClients("Player " + playerID + " incorrectly guessed card "+
                            guessPosition + " of player " + targetPlayer+": " + guessRank + "!");
                    refreshAllClientsViews();
                    
                    // update player's status, player must now show a card
                    status.set(playerID, "Show");
                    transmitter.informAllClients("Player " + playerID + " must show a card!");
                }
            }
        }
        else if (in.matches("show [0-5]")){
            // player must be in "Show" state
            if (!playerState.equals("Show")){
                transmitter.informClient(playerID, "Your state is: "+playerState+
                        ". You cannot show right now.");
            }            
            else{
                // TODO enforce: this card cannot be already faceup
                // parse input
                Integer position = Character.getNumericValue(in.charAt(5));
                
                // update game state
                gameBoard.revealCardToAll(playerID, position);
                
                // announce result of action to players
                transmitter.informAllClients("Player " + playerID + " revealed card " + position + "!");
                refreshAllClientsViews();
                
                // update and announce whose turn it is
                status.set(playerID, "Inactive");
                int nextPlayerID = (playerID+3)%4;
                transmitter.informAllClients("Player " + nextPlayerID + " to pass!");
                status.set(nextPlayerID, "Pass");
            }
        }
        else{
            throw new UnsupportedOperationException("Should not get here");            
        }
    }
    
    /**
     * Handler for client input in declaration phase
     * @param in client message
     * @param playerID 0-3
     * @return true if player did not declare wrong, false if player declares wrong
     * @throws InterruptedException
     */
    private boolean handleRequestDeclarationPhase(String in, int playerID) throws InterruptedException {
    	String playerState = status.get(playerID);
    	String regex = "(view)|(help)|(declare [0-3] [0-5] ([1-9]|1[0-2]))";
        String helpMessage = "Type 'view' to see your cards, "
                + "'help' for help message, "
                + "'pass x' to pass card x, " 
                + "'declare x y z' to declare card y of player x is z, if it is your turn to declare.";
        
        if (!in.matches(regex)){
            // invalid input
            // discard input and return help message
            transmitter.informClient(playerID, helpMessage);
            return true;
        }
        else if (in.equals("view")){
            transmitter.informClient(playerID, gameBoard.showPlayerViewOfBoard(playerID));
            return true;
        }
        else if (in.equals("help")){
            transmitter.informClient(playerID, helpMessage);
            return true;
        }
        else if (in.matches("declare [0-3] [0-5] ([1-9]|1[0-2])")) {
        	// check that player can actually declare
        	if (!playerState.equals("Declare")){
        	    transmitter.informClient(playerID, "Your state is: "+playerState+
                        ". You cannot declare right now.");
                return true;
            }
        	
        	else {
        		String[] tokenizedInput = in.split(" ");
                int targetPlayer = Integer.valueOf(tokenizedInput[1]);
                int guessPosition = Integer.valueOf(tokenizedInput[2]);
                int guessRank = Integer.valueOf(tokenizedInput[3]);
                
                boolean guessCorrect = gameBoard.guess(playerID, targetPlayer, 
                        guessPosition, guessRank);
                
                if (!guessCorrect) {
                    transmitter.informAllClients("Player " + playerID + " incorrectly declared card " + 
                			guessPosition + " of player " + targetPlayer + ": " + guessRank 
                			+ ".");
                	return false;
                }
                
                // alter game view
                gameBoard.revealCardToAll(targetPlayer, guessPosition);
                
                // announce result of action to players
                transmitter.informAllClients("Player " + playerID + " correctly declared card "+
                        guessPosition + " of player " + targetPlayer+": " + guessRank + "!");
                refreshAllClientsViews();
                
                return true;
                
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
