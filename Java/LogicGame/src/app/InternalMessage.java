package app;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A class of messages from the LogicServer to ClientHandlerThreads
 * TODO not yet incorporated into the app
 * TODO all the get methods
 */

public class InternalMessage {
	
	/*
	 * can equal --
	 * instructions: "setup" "begingame" 
	 * turns: "topass", "toshow", "toguess"
	 * moves: "pass" "show" "guess" 
	 * declare: "declare"
	 * final: "disconnect"
	 * board view: "board"
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
	
	// for convenience
	private static final Set<String> VALID_TYPES = 
	        new HashSet<>(Arrays.asList("setup","begingame","topass","toshow","toguess",
	                "pass","show","guess","declare","disconnect","board"));
    private static final Set<Integer> VALID_PLAYER_IDS= 
            new HashSet<>(Arrays.asList(0,1,2,3));
    private static final Set<Integer> VALID_CARD_POSITIONS= 
            new HashSet<>(Arrays.asList(0,1,2,3,4,5));
    private static final Set<Integer> VALID_GUESS_RANKS= 
            new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12));

	/**
	 * A bunch of asserts to make sure an InternalMessage is well-formed.  
	 */
	private void checkRep(){
	    assert(VALID_TYPES.contains(type));
	    if (type.equals("setup") || type.equals("begingame") || type.equals("disconnect") ){
	        assert(!playerID.isPresent());
	        assert(!cardPosition.isPresent());
	        assert(!targetPlayer.isPresent());
	        assert(!guessRank.isPresent());
	        assert(!guessCorrect.isPresent());
	        assert(!boardView.isPresent());
	    }
	    else if (type.equals("topass") || type.equals("toshow") || type.equals("toguess") || type.equals("declare") ){
	        assert(playerID.isPresent() && VALID_PLAYER_IDS.contains(playerID.get()));
	        assert(!cardPosition.isPresent());
	        assert(!targetPlayer.isPresent());
	        assert(!guessRank.isPresent());
	        assert(!guessCorrect.isPresent());
	        assert(!boardView.isPresent());
	    }
	    else if (type.equals("pass") || type.equals("show")){
	        assert(playerID.isPresent() && VALID_PLAYER_IDS.contains(playerID.get()));
	        assert(cardPosition.isPresent() && VALID_CARD_POSITIONS.contains(cardPosition.get()));
	        assert(!targetPlayer.isPresent());
	        assert(!guessCorrect.isPresent());
	        assert(!boardView.isPresent());
	    }
	    else if (type.equals("guess")){
	        assert(playerID.isPresent() && VALID_PLAYER_IDS.contains(playerID.get()));
	        assert(cardPosition.isPresent() && VALID_CARD_POSITIONS.contains(cardPosition.get()));
            assert(targetPlayer.isPresent() && VALID_PLAYER_IDS.contains(targetPlayer.get()));
            assert(guessRank.isPresent() && VALID_GUESS_RANKS.contains(guessRank.get()));
            assert(guessCorrect.isPresent());
            assert(!boardView.isPresent());
            assert((playerID.get() + targetPlayer.get())%2 == 1); // guessing player and target on opposite teams 
	    }
	    else if(type.equals("board")){
            assert(!playerID.isPresent());
            assert(!cardPosition.isPresent());
            assert(!targetPlayer.isPresent());
            assert(!guessRank.isPresent());
            assert(!guessCorrect.isPresent());
            assert(boardView.isPresent());
        }
        else{
            System.err.println(this);
	        throw new RuntimeException("Should not get here");
	    }
	}
	
	/**
	 * Constructor for "setup", "begingame", or "disconnect
	 * @param type "setup"/"begingame"/"disconnect"
	 */
	public InternalMessage(String type) {
		this.type = type;
		this.playerID = Optional.empty();
		this.cardPosition = Optional.empty();
		this.targetPlayer = Optional.empty();
		this.guessRank = Optional.empty();
		this.guessCorrect = Optional.empty();
		this.boardView = Optional.empty();
		this.checkRep();
	}
	
	/**
	 * Constructor for toMove's or declare
	 * @param type "topass"/"toshow"/"toguess"/"declare"
	 * @param playerID if a turn type, the playerID of the player who is supposed to move / if "declare", playerID of player declaring
	 */
	public InternalMessage(String type, int playerID) {
		this.type = type;
		this.playerID = Optional.of(playerID);
		this.cardPosition = Optional.empty();
		this.targetPlayer = Optional.empty();
		this.guessRank = Optional.empty();
		this.guessCorrect = Optional.empty();
		this.boardView = Optional.empty();
		this.checkRep();
	}
	
	/**
	 * Constructor for types "pass" or "show"
	 * @param move String, either "pass" or "show"
	 * @param playerID playerID of player who moved
	 * @param cardPosition the position of the card passed or showed
	 */
	public InternalMessage(String type, int playerID, int cardPosition){
		this.type = type;
		this.playerID = Optional.of(playerID);
        this.cardPosition = Optional.of(cardPosition);
		this.targetPlayer = Optional.empty();
		this.guessRank = Optional.empty();
		this.guessCorrect = Optional.empty();
		this.boardView = Optional.empty();
		this.checkRep();
	}
	
	/**
	 * Constructor for type "guess"
	 * @param type "guess"
	 * @param playerID the player who's guessing
	 * @param cardPosition position of card being guessed
	 * @param targetPlayer ID of player being guessed on
	 * @param guessRank rank of guess 
	 */
	public InternalMessage(String type, int playerID, int cardPosition, int targetPlayer, int guessRank, boolean guessCorrect) {
		this.type = type;
		this.playerID = Optional.of(playerID);
		this.cardPosition = Optional.of(cardPosition);
		this.targetPlayer = Optional.of(targetPlayer);
		this.guessRank = Optional.of(guessRank);
		this.guessCorrect = Optional.of(guessCorrect);
		this.boardView = Optional.empty();
		this.checkRep();
	}
	
	 /**
     * Constructor for type "board"
     * @param type String associated with proper type
     * @param boardView String rep of board
     */
    public InternalMessage(String type, String boardView) {
        this.type = type;
        this.playerID = Optional.empty();
        this.cardPosition = Optional.empty();
        this.targetPlayer = Optional.empty();
        this.guessRank = Optional.empty();
        this.guessCorrect = Optional.empty();
        this.boardView = Optional.of(boardView);
    }
    
    
    public String getType() {
		return type;
	}
    
    public int getPlayerID() {
    	if (playerID.isPresent()) {
    		return playerID.get();
    	}
    	throw new RuntimeException("Player ID does not exist");
    }
    
    public int getCardPosition() {
    	if (cardPosition.isPresent()) {
    		return cardPosition.get();
    	}
    	throw new RuntimeException("Card position does not exist");
    }
    
    public int getTargetPlayer() {
    	if (targetPlayer.isPresent()) {
    		return targetPlayer.get();
    	}
    	throw new RuntimeException("Target player does not exist");
    }
    
    public int getGuessRank() {
    	if (guessRank.isPresent()) {
    		return guessRank.get();
    	}
    	throw new RuntimeException("Guess rank does not exist");
    }
    
    public boolean getGuessCorrect() {
    	if (guessCorrect.isPresent()) {
    		return guessCorrect.get();
    	}
    	throw new RuntimeException("GuessCorrect does not exist");
    }
    
    public String getBoardView() {
    	if (boardView.isPresent()) {
    		return boardView.get();
    	}
    	throw new RuntimeException("Board view does not exist");
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
		else{
		    throw new RuntimeException("Should not get here.");
		}
	}
	
	
	
// AI can now parse directly, so this isn't necessary -- also clients can parse via toString
    
//  public ArrayList<String> getMessages(List<Boolean> isAI) {
//      ArrayList<String> messages = new ArrayList<String>();
//      for (int i = 0; i < 4; i++) {
//          if (isAI.get(i)) {
//              messages.add(getMessageAI());
//          }
//          else {
//              messages.add(getMessageClient());
//          }
//      }
//      return messages;
//  }

//  private String getMessageAI() {
//      // instructions
//      if (type.equals("setup")) {
//          return "setup";
//      }
//      if (type.equals("begingame")) {
//          return "begingame";
//      }
//      // turn
//      if (type.equals("topass")) {
//          return "topass " + playerID;
//      }
//      if (type.equals("toshow")) {
//          return "toshow " + playerID;
//      }
//      if (type.equals("toguess")) {
//          return "toguess " + playerID;
//      }
//      // move
//      if (type.equals("pass")) {
//          return "pass " + playerID + " " + x;
//      }
//      if (type.equals("show")) {
//          return "show " + playerID + " " + x;
//      }
//      if (type.equals("guess")) {
//          String rep = "guess " + playerID + " " + xyz[0] + " " + xyz[1] + " " + xyz[2] + " ";
//          if (xyz[3] == 1) {
//              rep += "correct";
//              return rep;
//          }
//          rep += "incorrect";
//          return rep;
//      }
//      if (type.equals("declare")) {
//          return "declare " + playerID;
//      }
//      // final
//      if (type.equals("disconnect")) {
//          return "disconnect";
//      }
//      return "";
//  } 

	
}
