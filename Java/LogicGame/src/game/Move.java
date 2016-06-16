package game;

import java.util.*;

// Stores information for a move and generates messages for client/AI
public class Move {
	
	// is "pass", "guess", "show", or "declare"
	private String type;
	
	// stores move information
	// If type is "pass" or "show", xyz is an array of length 1 
	// containing just the position of the card passed or showed.
	// If type is "guess", xyz = {target player x, position of card y, 
	// rank guessed z, 1 if guess was correct and 0 if not}
	// If type is "declare", xyz is empty
	private int[] xyz;
	
	// playerID of player who made the move
	private int playerID;
	
	// array of length 4 containing whether each player is an AI -
	private List<Boolean> isAI;
	
	/**
	 * 
	 * @param type String, either "pass", "show", or "guess" depending on type of move
	 * @param xyz 
	 * @param playerID
	 * @param isAI
	 */
	public Move(String type, int[] xyz, int playerID, List<Boolean> isAI) {
		this.type = type;
		this.xyz = xyz;
		this.playerID = playerID;
		this.isAI = isAI;
		assert (type.equals("pass") || type.equals("guess") || type.equals("show") || type.equals("declare"));
	}
	
	/**
	 * Gets all four messages to be sent to each player, whether AI or client
	 * @return an ArrayList of messages to be sent to each player regarding move
	 */
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
	
	/**
	 * Gets message about move to be read by an AI
	 * Message begins with type of move ("pass"/"show"/"guess"/"declare")
	 * followed by the playerID of the player who made the move.
	 * If type is "show" or "pass", next is the index of the card shown/passed.
	 * If type is "guess", the String also contains the target playerID, position of card guessed, rank guessed, 
	 * and "correct" if the guess was correct and "incorrect" otherwise.
	 * @return String containing move information
	 */
	private String getMessageAI() {
		if (type.equals("pass")) {
			return "pass " + playerID + " " + xyz[0];
		}
		if (type.equals("show")) {
			return "show " + playerID + " " + xyz[0];
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
		return "";
	}
	
	/**
	 * Gets the message to be read by a client
	 * @return String with message informing about move
	 */
	private String getMessageClient() {
		if (type.equals("pass")) {
			return "Player " + playerID + " passed card " + xyz[0] + "!";
		}
		if (type.equals("show")) {
			return "Player " + playerID + " revealed card " + xyz[0] + "!";
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
		return "";
	}
	
	
	
}
