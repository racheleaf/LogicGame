package app;

import java.util.ArrayList;
import java.util.List;

public class InternalMessage {
	
	/*
	 * can equal --
	 * instructions: "setup" "begingame" 
	 * turns: "topass", "toshow", "toguess"
	 * moves: "pass" "show" "guess" 
	 * declare: "declare"
	 * final: "disconnect"
	 */
	String type;
	
	// stores guess information
	// xyz = {target player x, position of card y, rank guessed z, 1 if guess was correct and 0 if not}
	private int[] xyz;
	
	// Stores move information ("pass"/"show")
	// x is the position of the card passed or showed.
	int x;
	
	// The playerID of the person who made the move
	private int playerID;
	
	/**
	 * Constructor for generic message type
	 * @param type String associated with proper type
	 */
	public InternalMessage(String type) {
		this.type = type;
	}
	
	/**
	 * Constructor for toMove's or declare
	 * @param toMoveOrDeclare "topass"/"toshow"/"toguess"/"declare"
	 * @param playerID if a turn type, the playerID of the player who is supposed to move / if "declare", playerID of player declaring
	 */
	public InternalMessage(String toMoveOrDeclare, int playerID) {
		this.type = toMoveOrDeclare;
		this.playerID = playerID;
	}
	
	/**
	 * Constructor for types "pass" or "show"
	 * @param move String, either "pass" or "show"
	 * @param playerID playerID of player who moved
	 * @param x the position of the card passed or showed
	 */
	public InternalMessage(String move, int playerID, int x) {
		assert (move.equals("pass") || move.equals("show"));
		this.type = move;
		this.x = x;
		this.playerID = playerID;
	}
	
	/**
	 * Constructor for type "guess"
	 * @param guess "guess"
	 * @param playerID the player who's guessing
	 * @param xyz {target player x, position of card y, rank guessed z, 1 if guess was correct and 0 if not}
	 */
	public InternalMessage(String guess, int playerID, int[] xyz) {
		assert (guess.equals("guess"));
		this.type = "guess";
		this.playerID = playerID;
		this.xyz = xyz;
	}
	
	public ArrayList<String> getMessages(List<Boolean> isAI) {
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
		if (type.equals("setup")) {
			return "setup";
		}
		if (type.equals("begingame")) {
			return "begingame";
		}
		// turn
		if (type.equals("topass")) {
			return "topass " + playerID;
		}
		if (type.equals("toshow")) {
			return "toshow " + playerID;
		}
		if (type.equals("toguess")) {
			return "toguess " + playerID;
		}
		// move
		if (type.equals("pass")) {
			return "pass " + playerID + " " + x;
		}
		if (type.equals("show")) {
			return "show " + playerID + " " + x;
		}
		if (type.equals("guess")) {
			String rep = "guess " + playerID + " " + xyz[0] + " " + xyz[1] + " " + xyz[2] + " ";
			if (xyz[3] == 1) {
				rep += "correct";
				return rep;
			}
			rep += "incorrect";
			return rep;
		}
		if (type.equals("declare")) {
			return "declare " + playerID;
		}
		// final
		if (type.equals("disconnect")) {
			return "disconnect";
		}
		return "";
	}
	
	private String getMessageClient() {
		// instructions
		if (type.equals("setup")) {
			return "Please set up your cards.  \r\n"
	                + "Type 'view' to see your cards, "
	                + "'help' for help message, "
	                + "and 'swap x' to swap card x.  "
	                + "Type 'done' to finish.";
		}
		if (type.equals("begingame")) {
			return "Game has begun! \r\n" 
	                + "Type 'view' to see your cards, "
	                + "'help' for help message, "
	                + "'pass x' to pass card x, " 
	                + "'guess x y z' to guess card y of player x is z," 
	                + "and 'show x' to show card x. "
	                + "Type 'declare' to declare.";
		}
		//turn
		if (type.equals("topass")) {
			return "Player " + playerID + " to pass.";
		}
		if (type.equals("toshow")) {
			return "Player " + playerID + " must show a card.";
		}
		if (type.equals("toguess")) {
			return ("Player " + playerID + " to guess.");
		}
		// move
		if (type.equals("pass")) {
			return "Player " + playerID + " passed card " + x + "!";
		}
		if (type.equals("show")) {
			return "Player " + playerID + " revealed card " + x + "!";
		}
		if (type.equals("guess")) {
			// guess is correct
			if (xyz[3] == 1) {
				return "Player " + playerID + " correctly guessed card " + 
						xyz[1] + " of player " + xyz[0] +": " + xyz[2] + "!";
			}
			// guess is incorrect
			return "Player " + playerID + " incorrectly guessed card "+
            		xyz[1] + " of player " + xyz[0] +": " + xyz[2] + "!";
		}
		if (type.equals("declare")) {
			return "Player " + playerID + " is declaring!";
		}
		if (type.equals("disconnect")) {
			return "Disconnect.";
		}
		return "";
	}
	
}
