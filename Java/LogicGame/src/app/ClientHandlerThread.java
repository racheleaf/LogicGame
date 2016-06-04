/**
 * 
 */
package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * @author Brice
 *
 */
public class ClientHandlerThread implements Runnable{

    private final Socket socket;
    private final int playerID;
    
    private final BlockingQueue<String> fromMaster;
    private final BlockingQueue<String> toMaster;
    
    public ClientHandlerThread(Socket socket, int playerID, 
            BlockingQueue<String> fromMaster, BlockingQueue<String>toMaster){
        this.socket = socket;
        this.playerID = playerID;
        this.fromMaster = fromMaster;
        this.toMaster = toMaster;
    }
    
    @Override
    public void run() {
        try {
            handleConnection();  
        } catch (IOException ioe) {
            ioe.printStackTrace(); 
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }  
    }
    
    /**
     * Handle client connection. Returns when client disconnects.
     * 
     * @throws IOException if the connection encounters an error or terminates unexpectedly
     * @throws InterruptedException 
     */
    private void handleConnection() throws IOException, InterruptedException {
        // I/O streams from player
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        try {
            out.println("Welcome to Logic! You are player #" + playerID + ".");
            out.println("Please wait for four players to arrive.");

            // phase while clients are connecting
            // all threads go to sleep until the main server thread begins the game
            // by calling threadControls[playerID].notify()
            toMaster.put("Client "+playerID+" ready.");
            assert(fromMaster.take().equals("Game started, proceed."));
            
            out.println("Game has begun! Please set up your cards.");
//            out.println(gameBoard.showPlayerOwnCards(playerID));
            
            // setup phase: clients are shown only their own cards, and can request
            // swaps of adjacent cards.  

            // this loop continues until client enters "done" 
//            for (String line = in.readLine(); line != null; line = in.readLine()) {
//                // server reads client's input, performs the necessary computations,
//                // and returns a message
//                String output = handleRequestSetupPhase(line, playerID);
//                if (output.equals("done")){
//                    out.println("Yay! Wait for other players to finish setup...");
//                    break;
//                }
//                if (output != null){
//                    out.println(output);
//                }
//            }
            
            /*TODO main phase of game*/

        } finally {
            out.close();
            in.close();
        }
    }

    
    
        
}
