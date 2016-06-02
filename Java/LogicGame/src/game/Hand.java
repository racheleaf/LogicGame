package game;

import java.util.ArrayList;

public class Hand extends Deck {
	
	private Player owner;
	
	public Hand() {
		
	}
	
	/**
	 * Creates new Hand of Cards
	 * @param d ArrayList of Cards in hand
	 */
	public Hand(ArrayList<Card> d) {
		deck = d;
	}
	
	/**
	 * Makes new Hand based on Hand's owner
	 * @param o Player, owner of hand
	 */
	public Hand(Player o) {
		owner = o;
	}
	
	/**
	 * Getter method for the Player who holds the hand
	 * @return owner, Player who owns the hand
	 */
	public Player getOwner() {
		return owner;
	}
	
	/**
	 * Sets the owner of the hand to a Player
	 * @param o the Player who will own the hand
	 */
	public void setOwner(Player o) {
		owner = o;
	}
	
	
	
}
