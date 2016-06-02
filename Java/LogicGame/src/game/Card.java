package game;

/**
 * Mutable class.  Represents a card in Logic.   
 */
public class Card {
	
	/* 
	 * suit = "S" (spades) or "D" (diamonds) 
	 * rank = 1, ..., 12
	 * show = true if face up, false if face down (0,1,2,3 refer to different players)
	 */
	private final String suit; 
	private final int rank;
	private boolean[] show = new boolean[4];
	/*
	 * TODO maybe implement visibility control at the gameboard level instead of at the card
	 * level? idk mutable cards make me queasy -Brice
	*/
	
	/**
	 * Constructs a Card of a given suit and rank
	 * @param suit "S" or "D" representing spades or diamonds
	 * @param rank an int in the range 1-12. 
	 */
	public Card(String suit, int rank) {
		assert (suit.equals("S") || suit.equals("D"));
        assert (1 <= rank && rank <= 12); 
		this.suit = suit;
		this.rank = rank;
		show[0] = false;
		show[1] = false;
		show[2] = false;
		show[3] = false;
	}
	
	/**
	 * Mutator.  Makes a Card visible to a certain player.  
	 * @param player 0-3, number of player
	 */
	public void makeVisible(int player) {
		show[player] = true;
	}
	
	/**
	 * Returns the card, as seen by a certain player.  
	 * @param player must be 0,1,2,3
	 * @return String containing suit and rank if face up for player, else just the suit
	 */
	public String printCard(int player) {
	    assert(player >= 0 && player <= 3);
		if (show[player]) {
			return suit + rank;
		}
		return suit;
	}
	
	/**
	 * Getter method for suit
	 * @return suit suit of this card ("S" for spades or "D" for diamonds)
	 */
	public String getSuit() {
		return suit;
	}
	
	/**
	 * Getter method for rank
	 * @return rank rank of this card (1-12)
	 */
	public int getRank() {
		return rank;
	}	
}
