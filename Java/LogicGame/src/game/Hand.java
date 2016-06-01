package game;

import java.util.ArrayList;

public class Hand extends Deck {
	
	private Player owner;
	
	public Hand() {
		
	}
	
	/*
	 * d = ArrayList of Cards, equal to deck
	 */
	public Hand(ArrayList<Card> d) {
		deck = d;
	}
	
	/*
	 * o = hand's owner
	 */
	public Hand(Player o) {
		owner = o;
	}
	
	/*
	 * returns owner
	 */
	public Player getOwner() {
		return owner;
	}
	
	public boolean setOwner(Player o) {
		owner = o;
		return true;
	}
	
	
	
}
