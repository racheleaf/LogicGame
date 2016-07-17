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
	
	/**
	 * Constructs a Card of a given suit and rank
	 * @param suit "S" or "D" representing spades or diamonds
	 * @param rank an int in the range 1-12. 
	 */
	public Card(String suit, int rank) {
		assert (suit.equals("S") || suit.equals("D") || suit.equals("H") || suit.equals("C"));
        assert (1 <= rank && rank <= 13); 
		this.suit = suit;
		this.rank = rank;
		show[0] = false;
		show[1] = false;
		show[2] = false;
		show[3] = false;
	}
	
	/**
	 * Mutator.  Makes a Card visible to a certain player.  
	 * @param playerID 0-3, ID of player
	 */
	public void makeVisible(int playerID) {
		show[playerID] = true;
	}
	
	/**
	 * Shows if Card is visible to a certain player
	 * @param playerID 0-3, ID of player
	 * @return true if the card is visible to player, else false
	 */
	public boolean isVisible(int playerID) {
		return show[playerID];
	}
	
	/**
	 * Returns the card, as seen by a certain player
	 * If visible to everyone, returns String containing just suit and rank
	 * If not visible to player, then only suit is displayed
	 * If visible to just card owner and card owner's partner, then card String is parenthesized ()
	 * If visible to just player, then card String is bracketed []
	 * @param playerID 0-3, ID of player
	 * @return String containing suit and rank if face up for player, else just the suit
	 */
	public String printCard(int playerID) {
	    assert(playerID >= 0 && playerID <= 3);
	    
	    int numPlayersVisible = 0;
	    for (int i = 0; i < 4; i++) {
	    	if (show[i]) {
	    		numPlayersVisible++;
	    	}
	    }
	    
	    //visible to everyone
		if (numPlayersVisible == 4) {
			return suit + rank;
		}
		
		//visible to only card owner and card owner's partner
		if (numPlayersVisible == 2) {
			if (show[playerID]) {
				return "(" + suit + rank + ")";
			}
			return "(" + suit + ")";
		}
		
		//visible to only player
		if (show[playerID]) {
			return "[" + suit + rank + "]";
		}
		
		//not visible to anyone but card owner (who's not player)
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
