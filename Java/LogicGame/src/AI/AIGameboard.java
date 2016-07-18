package AI;

import java.util.*;

// contains game board state/information as known to an AI

public class AIGameboard {
	
	int playerID;
	int partnerID;
	
	// contains 
    private ArrayList<AIHand> hands = new ArrayList<AIHand>();
    
    public AIGameboard(int playerID) {
    	this.playerID = playerID;
    	partnerID = (playerID+2)%4;
    	
    }
    
    public void updateGameState() {
    	
    }
	
    /**
     * Gets the suit of a card from its String form
     * @param cardRepString containing card information from Card's printCard method
     * @return String, either "S" if the suit is spades or "D" if the suit is diamonds
     */
    private String getSuitOfCard(String cardRep) {
    	char firstChar = cardRep.charAt(0);
    	if (firstChar == '[' || firstChar == '(') {
    		return String.valueOf(cardRep.charAt(1));
    	}
    	return String.valueOf(firstChar);
    }
    
    /**
     * Returns array containing true in position p if player p can see the card and false otherwise
     * @param cardRep String from Card's printCard() method
     * @param cardOwner int, playerID of the card owner
     * @return Array of length 4 containing, in position p, true if the card is visible to player p and false otherwise
     */
    private boolean[] getVisibilityOfCard(String cardRep, int cardOwner) {
    	boolean[] visibility = new boolean[4];
    	// card is visible to only this player
    	if (cardRep.charAt(0) == '[') {
    		visibility[playerID] = true;
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
    	return visibility;
    }
    
    /**
     * Gets the rank of a card from its String form
     * @param cardRep String containing card information from Card's printCard method
     * @param cardOwner int, playerId of the card owner
     * @return -1 if card is not visible to player and the rank otherwise
     */
    private int getRankOfCard (String cardRep, int cardOwner) {
    	boolean[] visibility = getVisibilityOfCard(cardRep, cardOwner);
    	int numPeopleVisible = 0;
    	for (int i = 0; i < 4; i++) {
    		if (visibility[i]) {
    			numPeopleVisible++;
    		}
    	}
    	// card is visible to everyone
    	if (numPeopleVisible == 4) {
    		return Integer.parseInt(cardRep.substring(1, cardRep.length()));
    	}
    	// card is visible to player and maybe partner
    	if (visibility[playerID]) {
    		return Integer.parseInt(cardRep.substring(2, cardRep.length()-1));
    	}
    	// card is not visible to player
    	return -1;
    }
}

