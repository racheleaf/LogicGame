package game;

import java.util.*;

public class Deck {
	
	protected ArrayList<Card> deck = new ArrayList<Card>();
	
	public Deck() {
		
	}
	
	/*
	 * d = ArrayList of Cards, equal to deck
	 */
	public Deck(ArrayList<Card> d) {
		deck = d;
	}
	
	/* 
	 * returns ArrayList of cards in deck
	 */
	public ArrayList<Card> getDeck() {
		return deck;
	}
	
	/*
	 * c = Card to be added to deck
	 * returns true once c is added
	 */
	public boolean addCard(Card c) {
		deck.add(c);
		return true;
	}
	
	/*
	 * c = Card to be removed from deck
	 * returns true if c exists in deck and has been removed
	 */
	public boolean removeCard(Card c) {
		return deck.remove(c);
	}
	
	/*
	 * returns String containing the cards a player can see
	 */
	public String printDeck(int player) {
		String rep = "";
		for (Card c : deck) {
			rep += c.printCard(player) + " ";
		}
		return rep;
	}
	
	/*
	 * returns size of deck
	 */
	public int size() {
		return deck.size();
	}
	
	/*
	 * h = ArrayList of hands
	 * deals cards in deck to each of the hands in h (shuffle prior to dealing if want randomized)
	 * returns true if h.size() > 0 and when done
	 */
	public boolean deal(ArrayList<Hand> h) {
		if (h.size() == 0) {
			return false;
		}
		int sizeOfDeck = size(); // sizeOfDeck = number of cards in deck
		int numDecks = h.size(); // numDecks = number of decks dealing to
		for (int i = 0; i < sizeOfDeck; i++) {
			h.get(i%numDecks).addCard(deck.get(i));
		}
		return true;
	}
	
	/*
	 * randomizes deck
	 */
	public boolean shuffle() {
		Collections.shuffle(deck);
		return true;
	}
	
	/*
	 * sorts deck by rank (min to max)
	 */
	public boolean sortByRank() {
		ArrayList<Card> newDeck = new ArrayList<Card>();
		while (deck.size() > 0) {
			Card card = minCard(deck);
			newDeck.add(card);
			deck.remove(card);
		}
		deck = newDeck;
		return true;
	}
	
	/*
	 * tempDeck = ArrayList of Cards
	 * returns index of card of lowest rank in tempDeck
	 */
	private Card minCard(ArrayList<Card> tempDeck) {
		Card card = tempDeck.get(0);
		int r = card.getRank();
		for (Card c : tempDeck) {
			if (c.getRank() < r) {
				r = c.getRank();
				card = c;
			}
		}
		return card;
	}
	
	
	
}
