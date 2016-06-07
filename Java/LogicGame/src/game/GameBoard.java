package game;

import java.util.*;

public class GameBoard {
    
    private final ArrayList<Hand> hands;
    private final Deck deck;
    
    /**
     * Creates a gameboard
     */
    public GameBoard(){
    	// makes deck and hands, deals deck to hands
    	deck = new Deck("Logic");
    	hands = new ArrayList<Hand>();
    	for (int player = 0; player < 4; player++) {
    		hands.add(new Hand());
    	}
    	deck.shuffle();
    	deck.deal(hands);
    	
		// makes each player's own cards visible to them
		// also sorts each player's cards by rank
		for (int i = 0; i < 4; i++) {
			for (Card c : hands.get(i).getDeck()) {
				c.makeVisible(i);
			}
			hands.get(i).sortByRank();
		}
    	
		//TODO other constructor stuff??
    	//throw new RuntimeException("Unimplemented");
    }
    
    /********************************
     *      SETUP PHASE OF GAME     *
     ********************************/

    /**
     * Swaps a selected card with the adjacent card of the same rank,
     * if such a card exists; else do nothing.  
     * @param playerID 0-3, ID of player
     * @param card 0-5, position of one of the cards to be swapped
     */
    public void swapTwoEqualCards(int playerID, int card){
    	Hand playerHand = hands.get(playerID);
    	int rankOfCard = playerHand.getCardAt(card).getRank();
    	
    	// compares rank of each card at adjacent positions to the rank of chosen card
    	// if the ranks are equal, then the cards are swapped
    	if (card > 0) {
    		if (playerHand.getCardAt(card-1).getRank() == rankOfCard) {
    			playerHand.swapTwoCards(card-1, card);
    		}
    	}
    	else if (card < playerHand.size()-1) {
    		if (playerHand.getCardAt(card+1).getRank() == rankOfCard) {
    			playerHand.swapTwoCards(card, card+1);
    		}
    	}
    }

    /**
     * Shows a player a view of his own six cards
     * @param playerID 0-3, number of player
     * @return a String with player's own cards in increasing order,
     * separated by spaces
     */
    public String showPlayerOwnCards(int playerID){
    	Hand playerHand = hands.get(playerID);
    	return playerHand.printDeck(playerID);
    }
    
    /********************************
     *      MAIN PHASE OF GAME      *
     ********************************/
    
    /**
     * Reveals a card of a certain player to player's partner
     * @param playerID 0-3, ID of player
     * @param card 0-5, position of card to be revealed
     */
    public void revealCardToPartner(int playerID, int card){
    	Card c = hands.get(playerID).getCardAt(card);
    	if (playerID == 0 || playerID == 1) {
    		c.makeVisible(playerID+2);
    	}
    	else if (playerID == 2 || playerID == 3) {
    		c.makeVisible(playerID-2);
    	}
    }

    /**
     * Reveals a card of a certain player to everyone
     * @param playerID 0-3, ID of player
     * @param card 0-5, representing position of card to be revealed
     */
    public void revealCardToAll(int playerID, int card){
    	Card c = hands.get(playerID).getCardAt(card);
    	for (int i = 0; i < 4; i++) {
    		c.makeVisible(i);
    	}
    }
    

    /**
     * Returns true if player guesses card correctly.  
     * @param playerGuesser 0-3, representing number of player doing guess
     * @param playerTarget 0-3, representing number of player whose card is guessed
     * @param card 0-5, representing position of card to be revealed.  Must be a card
     * not currently visible to playerGuesser
     * @param rank 1-12, representing the rank guessed
     * @return true if guess is correct.  
     */
    public boolean guess(int playerGuesser, int playerTarget, int card, int rank){
        //TODO make sure player cannot click unguessable card, or right now the program will throw an exception
    	Card c = hands.get(playerTarget).getCardAt(card);
    	assert (playerGuesser%2 != playerTarget%2);
    	assert (!c.isVisible(playerGuesser));
    	
    	if (c.getRank() == rank) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    /**
     * Shows a player the game state, as he would currently see it
     * @param playerID 0-3, ID of player
     * @return a String representing the game state
     */
    public String showPlayerViewOfBoard(int playerID){
    	String rep = "";
    	for (int viewPlayer = 0; viewPlayer < 4; viewPlayer++) {
    		rep += viewPlayer + "\t";
    		rep += hands.get(viewPlayer).printDeck(playerID);
    		rep += "\r\n";
    	}
    	return rep;
    }
    
    /********************************
     *    DECLARE PHASE OF GAME     *
     ********************************/

    //TODO should declarations be all cards at once or one card at a time? 
    
    /**
     * Makes all cards visible to player visible to everyone
     * @param playerID int 0-3, ID of player declaring cards
     */
    public void makePlayerGameViewPublic(int playerID) {
    	for (int p = 0; p < 4; p++) {
    		for (int i = 0; i < 6; i++) {
    			if (hands.get(p).getCardAt(i).isVisible(playerID)) {
    				revealCardToAll(p, i);
    			}
    		}
    	}
    }
    
    /**
     * Makes all cards visible to all players. To be used at end of game.
     */
    public void makeAllCardsPublic() {
    	for (int p = 0; p < 4; p++) {
    		for (int i = 0; i < 6; i++) {
    			revealCardToAll(p, i);
    		}
    	}
    }
    
    /**
     * Returns true if player has more to declare, or false if player has finished
     * @return true if there are still cards undeclared (invisible to players)
     */
    public boolean isMoreToDeclare() {
    	for (int p = 0; p < 4; p++) {
    		for (int i = 0; i < 6; i++) {
    			// selects random player (0) to check if card is visible to
    			if (!hands.get(p).getCardAt(i).isVisible(0)) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    /**
     * Returns true if declaration is correct
     * @param declaredHands four hands, the values the player guesses
     * @return true if guess is correct
     */
    public boolean declare(List<Hand> declaredHands){
        //TODO
        throw new RuntimeException("Unimplemented");
    }
}

