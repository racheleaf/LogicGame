package game;

import java.util.*;

public class Player {
	
	/*
	 * number = 0,1,2,3
	 */
	private int number;
	private Hand hand;
	
	/*
	 * num = the number (0,1,2,3) associated with the player
	 */
	public Player(int num) {
		number = num;
	}
	
	/*
	 * num = the number (0,1,2,3) associated with the player
	 * h = player's hand
	 */
	public Player(int num, Hand h) {
		number = num;
		hand = h;
	}
	
	/*
	 * returns number (0,1,2,3) associated with player
	 */
	public int getNumber() {
		return number;
	}
	
	/*
	 * returns player's hand
	 */
	public Hand getHand() {
		return hand;
	}
	
	/*
	 * h = hand of player
	 * sets hand to h
	 */
	public boolean setHand(Hand h) {
		hand = h;
		return true;
	}
	
	/*
	 * returns index in hand of card that player wishes to show partner
	 */
	public int getMoveLogicPartner() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Card: ");
		int cardShown = scan.nextInt();
		scan.close();
		return cardShown;
	}
	
	/*
	 * main move: guessing
	 * returns array containing [player #, index of card guessed, rank guessed]
	 */
	public int[] getMoveLogicGuess() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Player to Guess: ");
		int player = scan.nextInt();
		System.out.println("Card: ");
		int guess = scan.nextInt();
		System.out.println("Rank: ");
		int r = scan.nextInt();
		scan.close();
		return new int[] {player, guess, r};
	}
	
	
}
