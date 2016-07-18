package AI;

import java.util.*;

/**
 * Card as seen by an AI
 */
public class AICard {
	
	/* 
	 * suit = "S" (spades) or "D" (diamonds) 
	 * rank = 1, ..., 12
	 */
	private String suit; 
	private Optional<Integer> rank;
	
	// contains true in position i if player i can see card
	private boolean[] visibility = new boolean[4];
	
	/**
	 * Constructs a AICard of a given suit and rank
	 * @param suit "S" or "D" representing spades or diamonds
	 * @param rank an int in the range 1-12. 
	 */
	public AICard(String suit, int rank) {
		assert (suit.equals("S") || suit.equals("D") || suit.equals("H") || suit.equals("C"));
        assert (1 <= rank && rank <= 13); 
		this.suit = suit;
		this.rank = Optional.of(rank);
	}
	
	public AICard(String cardRep) {
		//setVisibilityOfCard(cardRep); TODO: make this work
		setSuitOfCard(cardRep);
		setRankOfCard(cardRep);
		
	}
	
	/**
	 * Gets the suit "S" or "D" of card
	 * @return "S" if the suit is Spades and "D" if the suit is Diamonds
	 */
	public String getSuit() {
		return suit;
	}
	
	/**
	 * Gets the rank (1-12) of card, if visible, and -1 if invisible
	 * @return rank of card if visible to AI, and -1 if not visible
	 */
	public int getRank() {
		if (rank.isPresent()) {
			return rank.get();
		}
		return -1;
	}
	
	/**
	 * Tells whether the rank of a card is known to LogicAI
	 * @return true if the rank is known to the LogicAI, false if the rank is unknown
	 */
	public boolean rankKnown() {
		if (rank.isPresent()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Sets rank of card to a number once its rank is known
	 * @param rank integer corresponding to the rank of the card
	 */
	public void updateRank(int rank) {
		assert (1 <= rank && rank <= 12);
		this.rank = Optional.of(rank);
	}
	
	/**
	 * Updates the visibility of the card to show that player playerID can now see the card
	 * @param playerID playerID of player who can now see the card
	 */
	public void updateVisibilityPlayer(int playerID) {
		assert (0 <= playerID && playerID < 4);
		visibility[playerID] = true;
	}
	
	/**
	 * Updates the visibility of the card to show that all players can see the card
	 */
	public void updateVisibilityAll() {
		for (int i = 0; i < 4; i++) {
			updateVisibilityPlayer(i);
		}
	}
	
	/**
     * Gets the suit of a card from its String form
     * @param cardRepString containing card information from Card's printCard method
     * @return String, either "S" if the suit is spades or "D" if the suit is diamonds
     */
    private void setSuitOfCard(String cardRep) {
    	char firstChar = cardRep.charAt(0);
    	if (firstChar == '[' || firstChar == '(') {
    		this.suit = String.valueOf(cardRep.charAt(1));
    	}
    	this.suit = String.valueOf(firstChar);
    }
    
    /**
     * Sets values to visibility, the array containing who can see the AICard
     * @param cardRep String from Card's printCard() method
     * @param cardOwner int, playerID of the card owner
     * @return Array of length 4 containing, in position p, true if the card is visible to player p and false otherwise
     */
    private void setVisibilityOfCard(String cardRep, int cardOwner) {
    	// card is visible to only this player
    	if (cardRep.charAt(0) == '[') {
    		visibility[cardOwner] = true;
    	}
    	// card is visible to only card owner and partner
    	else if (cardRep.charAt(0) == '(') {
    		visibility[cardOwner] = true;
    		visibility[(cardOwner + 2)%4] = true;
    	}
    	// card is visible to everyone
    	else if (cardRep.length() > 1) { 
    		for (int i = 0; i < 4; i++) {
    			visibility[i] = true;
    		}
    	}
    	// card is visible to only card owner
    	else if (cardRep.length() == 1) {
    		visibility[cardOwner] = true;
    	}
    }
    
    /**
     * Sets the rank of a card from its String representation
     * @param cardRep String containing card information from Card's printCard method
     * @param cardOwner int, playerId of the card owner
     * @return -1 if card is not visible to player and the rank otherwise
     */
    private void setRankOfCard (String cardRep) {
    	// card is visible to everyone
    	if (cardRep.charAt(0) != '[' && cardRep.charAt(0) != '(' && cardRep.length() > 1) {
    		this.rank = Optional.of(Integer.parseInt(cardRep.substring(1, cardRep.length())));
    	}
    	// card is visible to player and maybe partner
    	if (cardRep.charAt(0) == '[' || cardRep.charAt(0) == '(') {
    		this.rank = Optional.of(Integer.parseInt(cardRep.substring(2, cardRep.length()-1)));
    	}
    	// card is not visible to player
    	this.rank = Optional.empty();
    }
	
}
