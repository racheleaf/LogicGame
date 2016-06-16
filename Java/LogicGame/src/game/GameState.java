package game;

import java.util.ArrayList;
import java.util.List;

// Class that generates messages to AI/Client
// only for messages to be sent to everyone
public class GameState {
	
	/*
	 * can equal --
	 * instructions: "setup" "begingame" 
	 * turns: "topass", "toshow", "toguess"
	 * moves: "pass" "show" "guess" 
	 * declare: "declare"
	 */
	String gamestate;
	
	// stores guess information
	// xyz = {target player x, position of card y, rank guessed z, 1 if guess was correct and 0 if not}
	private int[] xyz;
	
	// Stores move information ("pass"/"show")
	// x is the position of the card passed or showed.
	int x;
	
	// The playerID of the person who made the move
	private int playerID;
	
	// array of length 4 containing whether each player is an AI -
	protected List<Boolean> isAI;
	
	/**
	 * Sets the state of the game
	 * @param gamestate String associated with proper gamestate
	 */
	public void setState(String gamestate) {
		this.gamestate = gamestate;
	}
	
	/**
	 * Sets state of game to a turn or declare gamestate
	 * @param toMoveOrDeclare "topass"/"toshow"/"toguess"/"declare"
	 * @param playerID if a turn gamestate, the playerID of the player who is supposed to move / if "declare", playerID of player declaring
	 */
	public void setState(String toMoveOrDeclare, int playerID) {
		this.gamestate = toMoveOrDeclare;
		this.playerID = playerID;
	}
	
	/**
	 * Sets state of the game to "pass" or "show"
	 * @param move String, either "pass" or "show"
	 * @param playerID playerID of player who moved
	 * @param x the position of the card passed or showed
	 */
	public void setState(String move, int playerID, int x) {
		assert (move.equals("pass") || move.equals("show"));
		this.gamestate = move;
		this.x = x;
		this.playerID = playerID;
	}
	
	/**
	 * Sets state of the game to "guess"
	 * @param guess "guess"
	 * @param playerID the player who's guessing
	 * @param xyz {target player x, position of card y, rank guessed z, 1 if guess was correct and 0 if not}
	 */
	public void setState(String guess, int playerID, int[] xyz) {
		assert (guess.equals("guess"));
		this.gamestate = "guess";
		this.playerID = playerID;
		this.xyz = xyz;
	}
	
	public ArrayList<String> getMessages() {
		ArrayList<String> messages = new ArrayList<String>();
		for (int i = 0; i < 4; i++) {
			if (isAI.get(i)) {
				messages.add(getMessageAI());
			}
			else {
				messages.add(getMessageClient());
			}
		}
		return messages;
	}
	
	private String getMessageAI() {
		// instructions
		if (gamestate.equals("setup")) {
			return "setup";
		}
		if (gamestate.equals("begingame")) {
			return "begingame";
		}
		// turn
		if (gamestate.equals("topass")) {
			return "topass " + playerID;
		}
		if (gamestate.equals("toshow")) {
			return "toshow " + playerID;
		}
		if (gamestate.equals("toguess")) {
			return "toguess " + playerID;
		}
		// move
		if (gamestate.equals("pass")) {
			return "pass " + playerID + " " + x;
		}
		if (gamestate.equals("show")) {
			return "show " + playerID + " " + x;
		}
		if (gamestate.equals("guess")) {
			String rep = "guess " + playerID + " " + xyz[0] + " " + xyz[1] + " " + xyz[2] + " ";
			if (xyz[3] == 1) {
				rep += "correct";
				return rep;
			}
			rep += "incorrect";
			return rep;
		}
		if (gamestate.equals("declare")) {
			return "declare " + playerID;
		}
		return "";
	}
	
	private String getMessageClient() {
		// instructions
		if (gamestate.equals("setup")) {
			return "Please set up your cards.  \r\n"
	                + "Type 'view' to see your cards, "
	                + "'help' for help message, "
	                + "and 'swap x' to swap card x.  "
	                + "Type 'done' to finish.";
		}
		if (gamestate.equals("begingame")) {
			return "Game has begun! \r\n" 
	                + "Type 'view' to see your cards, "
	                + "'help' for help message, "
	                + "'pass x' to pass card x, " 
	                + "'guess x y z' to guess card y of player x is z," 
	                + "and 'show x' to show card x. "
	                + "Type 'declare' to declare.";
		}
		//turn
		if (gamestate.equals("topass")) {
			return "Player " + playerID + " to pass.";
		}
		if (gamestate.equals("toshow")) {
			return "Player " + playerID + " must show a card.";
		}
		if (gamestate.equals("toguess")) {
			return ("Player " + playerID + " to guess.");
		}
		// move
		if (gamestate.equals("pass")) {
			return "Player " + playerID + " passed card " + x + "!";
		}
		if (gamestate.equals("show")) {
			return "Player " + playerID + " revealed card " + x + "!";
		}
		if (gamestate.equals("guess")) {
			// guess is correct
			if (xyz[3] == 1) {
				return "Player " + playerID + " correctly guessed card " + 
						xyz[1] + " of player " + xyz[0] +": " + xyz[2] + "!";
			}
			// guess is incorrect
			return "Player " + playerID + " incorrectly guessed card "+
            		xyz[1] + " of player " + xyz[0] +": " + xyz[2] + "!";
		}
		if (gamestate.equals("declare")) {
			return "Player " + playerID + " is declaring!";
		}
		return "";
	}
	
	
	
}
