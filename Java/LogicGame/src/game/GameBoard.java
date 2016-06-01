package game;

import java.util.List;

public class GameBoard {
    
    private final List<Hand> hands;
    
    /**
     * Creates a gameboard
     */
    public GameBoard(){
        throw new RuntimeException("Unimplemented");
    }
    
    /********************************
     *      SETUP PHASE OF GAME     *
     ********************************/

    /**
     * Swaps a selected card with the adjacent card of the same rank,
     * if such a card exists; else do nothing.  
     * @param player 0-3, number of player
     * @param card 0-5, position of one of the cards to be swapped
     */
    public void swapTwoEqualCards(int player, int card){
        //TODO
        throw new RuntimeException("Unimplemented");
    }

    /**
     * Shows a player a view of his own six cards
     * @param player 0-3, number of player
     * @return a String with player's own cards in increasing order,
     * separated by spaces
     */
    public String showPlayerOwnCards(int player){
        //TODO
        throw new RuntimeException("Unimplemented");
    }
    
    /********************************
     *      MAIN PHASE OF GAME      *
     ********************************/
    
    /**
     * Reveals a card of a certain player to player's partner
     * @param player 0-3, number of player
     * @param card 0-5, position of card to be revealed
     */
    public void revealCardToPartner(int player, int card){
        //TODO
        throw new RuntimeException("Unimplemented");
    }

    /**
     * Reveals a card of a certain player to everyone
     * @param player 0-3, representing number of player
     * @param card 0-5, representing position of card to be revealed
     */
    public void revealCardToAll(int player, int card){
        //TODO
        throw new RuntimeException("Unimplemented");
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
    public boolean guess(int player, int card, int rank){
        //TODO
        throw new RuntimeException("Unimplemented");
    }
    
    //TODO do we want to display passed-but-facedown cards differently
    //from faceup cards? 
    /**
     * Shows a player the game state, as he would currently see it
     * @param player 0-3, number of player
     * @return a String representing the game state
     */
    public String showPlayerViewOfBoard(int player){
        //TODO
        throw new RuntimeException("Unimplemented");
    }
    
    /********************************
     *    DECLARE PHASE OF GAME     *
     ********************************/

    //TODO should declarations be all cards at once or one card at a time? 
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

