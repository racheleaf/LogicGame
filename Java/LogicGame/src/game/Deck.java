package game;

import java.util.*;

public class Deck {
	
	protected ArrayList<Card> deck = new ArrayList<Card>();
	
	public Deck() {
		
	}
	
	/**
	 * Create deck from ArrayList of Cards
	 * @param d ArrayList of Cards, ArrayList containing Cards in deck
	 */
	public Deck(ArrayList<Card> d) {
		deck = d;
	}
	
	/** 
	 * Create deck based on type of deck to be created
	 * Classic deck is all four suits, 1-13
	 * Logic deck is Diamonds and Spades, 1-12
	 * @param type String "Classic" or "Logic" corresponding to type of deck wished to be created
	 */
	public Deck (String type) {
		assert (type.equals("Classic") || type.equals("Logic"));
		if (type.equals("Classic")) {
			for (int i = 1; i <= 13; i++) {
				addCard(new Card("C", i));
				addCard(new Card("D", i));
				addCard(new Card("H", i));
				addCard(new Card("S", i));
			}
		}
		else if (type.equals("Logic")) {
			for (int i = 1; i <= 12; i++) {
				addCard(new Card("D", i));
				addCard(new Card("S", i));
			}
		}
	}
	
	/**
	 * Getter method for ArrayList of Cards in the deck
	 * @return ArrayList of Cards in deck
	 */
	public ArrayList<Card> getDeck() {
		return deck;
	}
	
	/**
	 * Adds Card to deck
	 * @param c Card to be added to deck
	 */
	public void addCard(Card c) {
		deck.add(c);
	}
	
	/**
	 * Removes Card from deck
	 * @param c Card to be removed from deck
	 */
	public boolean removeCard(Card c) {
		return deck.remove(c);
	}
	
	/**
	 * Gets card information (suit, maybe rank) a player can see
	 * @param playerID 0-3, number of player
	 * @return String with card information that a player can see
	 */
	public String printDeck(int playerID) {
		String rep = "";
		for (Card c : deck) {
			rep += c.printCard(playerID) + " ";
		}
		return rep;
	}
	
	/**
	 * Gets the number of cards in the deck
	 * @return size of deck
	 */
	public int size() {
		return deck.size();
	}
	
	/**
	 * Deals cards in deck to hands (shuffle first if want randomized)
	 * @param h ArrayList of hands, to which cards from the deck are going be dealt
	 */
	public void deal(ArrayList<Hand> h) {
		try {
			int sizeOfDeck = size(); // sizeOfDeck = number of cards in deck
			int numDecks = h.size(); // numDecks = number of decks dealing to
			for (int i = 0; i < sizeOfDeck; i++) {
				h.get(i%numDecks).addCard(deck.get(i));
			}
		}
		catch (IndexOutOfBoundsException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Randomizes/shuffles deck
	 */
	public void shuffle() {
		Collections.shuffle(deck);
	}
	
	/**
	 * Sorts deck by rank (min to max)
	 */
	public void sortByRank() {
		ArrayList<Card> newDeck = new ArrayList<Card>();
		while (deck.size() > 0) {
			Card card = minCard(deck);
			newDeck.add(card);
			deck.remove(card);
		}
		deck = newDeck;
	}
	
	/**
	 * Finds index of lowest ranked card in deck
	 * @param tempDeck ArrayList of Cards
	 * @return index of card of lowest rank in tempDeck
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
