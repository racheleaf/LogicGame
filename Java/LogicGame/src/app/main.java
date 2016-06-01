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
		
		/*
		 * create four players with numbers 0,1,2,3 with their hands
		 */
		Player[] players = new Player[4];
		ArrayList<Hand> hands = new ArrayList<Hand>();
		for (int i = 0; i < 4; i++) {
			players[i] = new Player(i);
			players[i].setHand(new Hand(players[i])); // give each player a hand 
			hands.add(players[i].getHand());
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
			for (Card c : players[i].getHand().getDeck()) { // for Card c in player i's hand's ArrayList of cards
				c.makeVisible(i);
			}
			players[i].getHand().sortByRank(); //sort by rank
		}
		
		for (int i = 0; i < 4; i++) {
			System.out.println(players[i].getHand().printDeck(i));
		}
		
		
		
		
		
	}

}
