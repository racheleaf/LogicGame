package app;
import game.*;
import java.util.*;

public class main {

	public static void main(String[] args) {
		
		/*
		 * create deck of 24 cards: 1-12 for both S(pades) and D(iamonds)
		 */
		Deck deck = new Deck();
		for (int i = 1; i <= 12; i++) {
			deck.addCard(new Card("S", i));
			deck.addCard(new Card("D", i));
		}
		
		
		ArrayList<Hand> hands = new ArrayList<Hand>();
		for (int i = 0; i < 4; i++) {
			hands.add(new Hand());
		}
		
		/*
		 * shuffle deck and deal cards to hands
		 */
		deck.shuffle();
		deck.deal(hands);
		
		/*
		 * makes each player's own cards visible to them
		 * also sorts each player's cards by rank
		 */
		for (int i = 0; i < 4; i++) {
			for (Card c : hands.get(i).getDeck()) { // for Card c in player i's hand's ArrayList of cards
				c.makeVisible(i);
			}
			hands.get(i).sortByRank(); //sort by rank
		}
		
		Card cardtemp = hands.get(2).getCardAt(4);
		cardtemp.makeVisible(0);
		
		for (int i = 0; i < 4; i++) {
			System.out.println(hands.get(i).printDeck(0));
		}
		
		
		
		
		
	}

}
