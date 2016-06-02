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
	
	/**
	 * Retrieves Card at desired position in hand
	 * @param card 0-(size of hand), position of card to be retrived
	 * @return Card at position card
	 */
	public Card getCardAt(int card) {
		return deck.get(card);
	}
	
	/**
	 * Swaps the cards at two indices specified
	 * @param card1 0-(size of hand), index of first card to be swapped
	 * @param card2 0-(size of hand), index of second card to be swapped
	 */
	public void swapTwoCards(int card1, int card2) {
		Card c1 = deck.get(card1);
		deck.set(card1, deck.get(card2));
		deck.set(card2, c1);
	}
	
}
