package app;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A class of messages from the LogicServer to ClientHandlerThreads
 */

public class InternalMessage {
	
    private final String recipient;
    
	/*
	 * can equal --
	 * instructions: "setup" "begingame" 
	 * turns: "topass", "toshow", "toguess"
	 * moves: "pass" "show" "guess" 
	 * declare: "declare"
	 * final: "disconnect"
	 * board view: "board"
	 * misc text: "misc"
	 */
	private final String type;
	
	/* 
	 * Basically Optional<Type> is an object that's either an object of 
	 * type Type, or absent 
	 * You can check if an Optional is present with .isPresent(), and if 
	 * it's present you can get the value with .get() (which throws an
	 * exception when not present)  
	 * You create an empty Optional with Optional.empty(), and a nonempty
	 * Optional with value blah with Optional.of(blah) 
	 */
	// present for topass, tosow, toguess, pass, show, guess, declare
	private final Optional<Integer> playerID; 
	// present for pass, show, guess
	private final Optional<Integer> cardPosition;
	// present for guess
	private final Optional<Integer> targetPlayer;
	private final Optional<Integer> guessRank;
	private final Optional<Boolean> guessCorrect;
	// present only for board
    private final Optional<String> boardView;
    // present only for misc
    private final Optional<String> miscContent;
	
	// for convenience
    private static final Set<String> VALID_RECIPIENTS = 
            new HashSet<>(Arrays.asList("CLIENT_0","CLIENT_1","CLIENT_2","CLIENT_3","ALL_CLIENTS"));
    private static final Set<String> VALID_TYPES = 
	        new HashSet<>(Arrays.asList("setup","begingame","topass","toshow","toguess",
	                "pass","show","guess","declare","disconnect","board","misc"));
    private static final Set<Integer> VALID_PLAYER_IDS= 
            new HashSet<>(Arrays.asList(0,1,2,3));
    private static final Set<Integer> VALID_CARD_POSITIONS= 
            new HashSet<>(Arrays.asList(0,1,2,3,4,5));
    private static final Set<Integer> VALID_GUESS_RANKS= 
            new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12));

	/**
	 * Checks that an InternalMessage is well-formed.  
	 */
	private void checkRep(){
	    assert(VALID_RECIPIENTS.contains(recipient));
	    assert(VALID_TYPES.contains(type));
	    System.err.println(this);
	    if (type.equals("setup") || type.equals("begingame") || type.equals("disconnect") ){
	        assert(!playerID.isPresent());
	        assert(!cardPosition.isPresent());
	        assert(!targetPlayer.isPresent());
	        assert(!guessRank.isPresent());
	        assert(!guessCorrect.isPresent());
	        assert(!boardView.isPresent());
	        assert(!miscContent.isPresent());
	    }
	    else if (type.equals("topass") || type.equals("toshow") || type.equals("toguess") || type.equals("declare") ){
	        assert(playerID.isPresent() && VALID_PLAYER_IDS.contains(playerID.get()));
	        assert(!cardPosition.isPresent());
	        assert(!targetPlayer.isPresent());
	        assert(!guessRank.isPresent());
	        assert(!guessCorrect.isPresent());
	        assert(!boardView.isPresent());
	        assert(!miscContent.isPresent());
	    }
	    else if (type.equals("pass") || type.equals("show")){
	        assert(playerID.isPresent() && VALID_PLAYER_IDS.contains(playerID.get()));
	        assert(cardPosition.isPresent() && VALID_CARD_POSITIONS.contains(cardPosition.get()));
	        assert(!targetPlayer.isPresent());
	        assert(!guessCorrect.isPresent());
	        assert(!boardView.isPresent());
	        assert(!miscContent.isPresent());
	    }
	    else if (type.equals("guess")){
	        assert(playerID.isPresent() && VALID_PLAYER_IDS.contains(playerID.get()));
	        assert(cardPosition.isPresent() && VALID_CARD_POSITIONS.contains(cardPosition.get()));
            assert(targetPlayer.isPresent() && VALID_PLAYER_IDS.contains(targetPlayer.get()));
            assert(guessRank.isPresent() && VALID_GUESS_RANKS.contains(guessRank.get()));
            assert(guessCorrect.isPresent());
            assert(!boardView.isPresent());
            assert(!miscContent.isPresent());
            assert((playerID.get() + targetPlayer.get())%2 == 1); // guessing player and target on opposite teams 
	    }
	    else if(type.equals("board")){
            assert(!playerID.isPresent());
            assert(!cardPosition.isPresent());
            assert(!targetPlayer.isPresent());
            assert(!guessRank.isPresent());
            assert(!guessCorrect.isPresent());
            assert(boardView.isPresent());
            assert(!miscContent.isPresent());
        }
	    else if(type.equals("misc")){
            assert(!playerID.isPresent());
            assert(!cardPosition.isPresent());
            assert(!targetPlayer.isPresent());
            assert(!guessRank.isPresent());
            assert(!guessCorrect.isPresent());
            assert(!boardView.isPresent());
            assert(miscContent.isPresent());	        
	    }
        else{
	        throw new RuntimeException("Should not get here");
	    }
	}
	
	/**
	 * Constructor for "setup", "begingame", or "disconnect"
	 * @param recipient 0-3 for a particular client, or -1 for all
	 * @param type "setup"/"begingame"/"disconnect"
	 */
	public InternalMessage(int recipient, String type) {
	    if (recipient == -1){
	        this.recipient = "ALL_CLIENTS";
	    }
	    else{
	        this.recipient = "CLIENT_"+recipient;
	    }
		this.type = type;
		this.playerID = Optional.empty();
		this.cardPosition = Optional.empty();
		this.targetPlayer = Optional.empty();
		this.guessRank = Optional.empty();
		this.guessCorrect = Optional.empty();
		this.boardView = Optional.empty();
		this.miscContent = Optional.empty();
		this.checkRep();
	}
	
	/**
	 * Constructor for toMove's or declare
	 * @param recipient 0-3 for a particular client, or -1 for all
	 * @param type "topass"/"toshow"/"toguess"/"declare"
	 * @param playerID if a turn type, the playerID of the player who is supposed to move / if "declare", playerID of player declaring
	 */
	public InternalMessage(int recipient, String type, int playerID) {
        if (recipient == -1){
            this.recipient = "ALL_CLIENTS";
        }
        else{
            this.recipient = "CLIENT_"+recipient;
        }	    
		this.type = type;
		this.playerID = Optional.of(playerID);
		this.cardPosition = Optional.empty();
		this.targetPlayer = Optional.empty();
		this.guessRank = Optional.empty();
		this.guessCorrect = Optional.empty();
		this.boardView = Optional.empty();
		this.miscContent = Optional.empty();
		this.checkRep();
	}
	
	/**
	 * Constructor for types "pass" or "show"
	 * @param recipient 0-3 for a particular client, or -1 for all
	 * @param type "pass"/"show"
	 * @param playerID playerID of player who moved
	 * @param cardPosition the position of the card passed or showed
	 */
	public InternalMessage(int recipient, String type, int playerID, int cardPosition){
        if (recipient == -1){
            this.recipient = "ALL_CLIENTS";
        }
        else{
            this.recipient = "CLIENT_"+recipient;
        }
		this.type = type;
		this.playerID = Optional.of(playerID);
        this.cardPosition = Optional.of(cardPosition);
		this.targetPlayer = Optional.empty();
		this.guessRank = Optional.empty();
		this.guessCorrect = Optional.empty();
		this.boardView = Optional.empty();
		this.miscContent = Optional.empty();
		this.checkRep();
	}
	
	/**
	 * Constructor for type "guess"
	 * @param recipient 0-3 for a particular client, or -1 for all
	 * @param type "guess"
	 * @param playerID the player who's guessing
	 * @param cardPosition position of card being guessed
	 * @param targetPlayer ID of player being guessed on
	 * @param guessRank rank of guess 
	 */
	public InternalMessage(int recipient, String type, int playerID, int cardPosition, int targetPlayer, int guessRank, boolean guessCorrect) {
        if (recipient == -1){
            this.recipient = "ALL_CLIENTS";
        }
        else{
            this.recipient = "CLIENT_"+recipient;
        }
		this.type = type;
		this.playerID = Optional.of(playerID);
		this.cardPosition = Optional.of(cardPosition);
		this.targetPlayer = Optional.of(targetPlayer);
		this.guessRank = Optional.of(guessRank);
		this.guessCorrect = Optional.of(guessCorrect);
		this.boardView = Optional.empty();
		this.miscContent = Optional.empty();
		this.checkRep();
	}
	
	 /**
     * Constructor for type "board"/"misc"
     * @param recipient 0-3 for a particular client, or -1 for all
     * @param type "board"/"misc"
     * @param boardView String rep of board
     */
    public InternalMessage(int recipient, String type, String content) {
        if (recipient == -1){
            this.recipient = "ALL_CLIENTS";
        }
        else{
            this.recipient = "CLIENT_"+recipient;
        }
        this.type = type;
        this.playerID = Optional.empty();
        this.cardPosition = Optional.empty();
        this.targetPlayer = Optional.empty();
        this.guessRank = Optional.empty();
        this.guessCorrect = Optional.empty();
        if (type.equals("board")){
            this.boardView = Optional.of(content);
            this.miscContent = Optional.empty();
        }
        else if (type.equals("misc")){
            this.boardView = Optional.empty();
            this.miscContent = Optional.of(content);
        }
        else{
            throw new RuntimeException("Should not get here");
        }
        this.checkRep();
    }
    
    /**
     * Gets recipient of this message
     * @return ''
     */
    public String getRecipient(){
        return recipient;
    }
    
    /**
     * Gets type of this message
     * @return ''
     */
    public String getType() {
		return type;
	}
    
    /**
     * Gets playerID if type = topass, toshow, toguess, declare, pass, show, guess
     * @return ''
     */
    public int getPlayerID() {
    	if (playerID.isPresent()) {
    		return playerID.get();
    	}
    	throw new RuntimeException("Player ID does not exist");
    }
    
    /**
     * Gets cardPosition if type = pass, show, guess; else throws exception
     * @return ''
     */
    public int getCardPosition() {
    	if (cardPosition.isPresent()) {
    		return cardPosition.get();
    	}
    	throw new RuntimeException("Card position does not exist");
    }
    
    /**
     * Gets the target player if type = guess; else throws exception
     * @return ''
     * 
     */
    public int getTargetPlayer() {
    	if (targetPlayer.isPresent()) {
    		return targetPlayer.get();
    	}
    	throw new RuntimeException("Target player does not exist");
    }
    
    /**
     * Gets the value of guessRank if type = guess; else throws exception
     * @return ''
     */
    public int getGuessRank() {
    	if (guessRank.isPresent()) {
    		return guessRank.get();
    	}
    	throw new RuntimeException("Guess rank does not exist");
    }
    
    /**
     * Gets the value of guessCorrect if type = guess; else throws exception
     * @return ''
     */
    public boolean getGuessCorrect() {
    	if (guessCorrect.isPresent()) {
    		return guessCorrect.get();
    	}
    	throw new RuntimeException("GuessCorrect does not exist");
    }
    
    /**
     * Gets board view if this is a board message; else throws exception
     * @return ''
     */
    public String getBoardView() {
    	if (boardView.isPresent()) {
    		return boardView.get();
    	}
    	throw new RuntimeException("Board view does not exist");
    }
    
    /**
     * Gets content if this is a misc message; else throws exception. 
     * @return ''
     */
    public String getMiscContent(){
        if (miscContent.isPresent()){
            return miscContent.get();
        }
        throw new RuntimeException("Misc content does not exist");
    }
	
	
	/* 
	 * Overriding the native toString() does some nice things - in particular 
	 * automatic string conversion when appropriate.
	 * e.g. System.out.println(an InternalMessage) will now automatically invoke this 
	 * method to convert the InternalMessage to a string
	 */
	/**
	 * A string representation of an InternalMessage 
	 */
	@Override
	public String toString() {
		// instructions
		if (type.equals("setup")) {
			return "Please set up your cards.  \r\n"
	                + "Type 'view' to see your cards, "
	                + "'help' for help message, "
	                + "and 'swap x' to swap card x.  "
	                + "Type 'done' to finish.";
		}
		else if (type.equals("begingame")) {
			return "Game has begun! \r\n" 
	                + "Type 'view' to see your cards, "
	                + "'help' for help message, "
	                + "'pass x' to pass card x, " 
	                + "'guess x y z' to guess card y of player x is z," 
	                + "and 'show x' to show card x. "
	                + "Type 'declare' to declare.";
		}
		//turn
		else if (type.equals("topass")) {
			return "Player " + playerID.get() + " to pass.";
		}
		else if (type.equals("toshow")) {
			return "Player " + playerID.get() + " must show a card.";
		}
		else if (type.equals("toguess")) {
			return ("Player " + playerID.get() + " to guess.");
		}
		// move
		else if (type.equals("pass")) {
			return "Player " + playerID.get() + " passed card " + cardPosition.get() + "!";
		}
		else if (type.equals("show")) {
			return "Player " + playerID.get() + " revealed card " + cardPosition.get() + "!";
		}
		else if (type.equals("guess")) {
			// guess is correct
			if (guessCorrect.get()) {
				return "Player " + playerID.get() + " correctly guessed card " + 
						cardPosition.get() + " of player " + targetPlayer.get() +": " + guessRank.get() + "!";
			}
			// guess is incorrect
			return "Player " + playerID.get() + " incorrectly guessed card " +
            		cardPosition.get() + " of player " + targetPlayer.get() +": " + guessRank.get() + "!";
		}
		else if (type.equals("declare")) {
			return "Player " + playerID.get() + " is declaring!";
		}
		else if (type.equals("disconnect")) {
			return "Disconnect.";
		}
		// board
		else if (type.equals("board")){
		    return boardView.get();
		}
		// misc
		else if (type.equals("misc")){
		    return miscContent.get();
		}
		else{
		    throw new RuntimeException("Should not get here.");
		}
	}
}
