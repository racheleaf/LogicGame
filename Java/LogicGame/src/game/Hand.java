package game;

import java.util.ArrayList;

public class Hand extends Deck {
	
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
