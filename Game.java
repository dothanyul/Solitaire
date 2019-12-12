import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Game {

	private ArrayList<Card>[] board;  // hold the seven piles of cards that make up the game
	private Stack<Card>[] top;    // the four finished stacks that go at the top
	private Deck deck;
	private Deck flip;  // the deck that cards are flipped to from the flip stack
	private int height;  // the height of the printed game
	private String[] error; // the error message for the most recent turn 

	@SuppressWarnings("unchecked")
	public Game() {
		deck = new Deck();
		deck.shuffle();

		board = new ArrayList[7];  // the seven piles to be played from
		for (int i = 0; i < 7; i++) {
			board[i] = new ArrayList<Card>();
		}
		top = new Stack[4];  // the four finished stacks
		for (int i = 0; i < 4; i++) {
			top[i] = new Stack<Card>();
		}
		for (int i = 0; i < 7; i++) {  // deal the seven piles of cards
			for (int j = 0; j < 7 - i; j++) {
				Card add = deck.top();
				add.flip();
				board[j].add(add);
			}
		}
		for (int i = 0; i < 7; i++) {  // flip over the top of each pile
			board[i].get(board[i].size()-1).flip();
		}
		flip = Deck.empty();
		height = 9;
		error = new String[0];
	}

	public String toString() {
		int height = 0; // count the height every time
		StringBuilder sb = new StringBuilder();
		sb.append(" ");
		for (Stack<Card> stack : top) {
			if (stack.isEmpty()) {
				sb.append("--  ");
			} else {
				sb.append(stack.get(stack.size()-1).toString() + " ");
			}
		}
		sb.append("    ");  // space between the four finished stacks and the flip stack
		if (flip.isEmpty()) {  // the flipped portion of the flip stack
			sb.append("--  ");
		} else {
			sb.append(flip.peek().toString() + " ");
		}
		if (deck.isEmpty()) {  // the upside down portion of the flip stack
			sb.append("--\n\n ");
		} else {
			sb.append("***\n\n ");
		}
		height += 2;
		int i = 0;  // testing height of the piles
		boolean done = false;  // while there are still piles this tall on the board
		while (! done) {
			done = true;  // this will be reset to false if a pile is this tall
			for (ArrayList<Card> stack : board) {  // loop over the piles
				if (i < stack.size()) {  // if there are still cards here
					done = false;  // keep going
					sb.append(stack.get(i).toString() + " ");  // add it to the print
				} else {
					if (i == 0) {  // use a placeholder for empty piles in the first row
						sb.append("--  ");
					} else {
						sb.append("    ");  // blank space to keep spacing for the other piles
					}
				}
			}
			if (i < error.length) {
				sb.append(" " + error[i]);
			}
			i++;
			sb.append("\n ");
			height++;
		}
		while (i < 6) { // make every print at least 8 lines tall
			sb.append("\n");
			height++;
			i++;
		}
		this.height = height + 1; // since toString() already has to count the height this is the easiest way to figure it out
		this.error = new String[0]; // get rid of the old error once we've printed it once
		return sb.toString();
	}

	public int getHeight() {
		return height;
	}
	
	public void setError(String e) {
		error = e.split("\\n");
	}

	/*
	 * put a card in its respective finished stack from the main board
	 */
	public void up(int stackNum) throws IOException {
		if (board[stackNum].isEmpty()) {
			throw new IOException(" Can't move from empty pile.");
		}
		Card card = board[stackNum].get(board[stackNum].size()-1);  // get the card to be put up
		if (card.number() == 1) {  // an ace can always go up
			top[card.suit()].push(card);  // put it up
			board[stackNum].remove(board[stackNum].size()-1);  // take the card off the stack it was on
			if (! board[stackNum].isEmpty() && ! board[stackNum].get(board[stackNum].size()-1).faceUp()) {
				board[stackNum].get(board[stackNum].size()-1).flip();  // flip over the card underneath it
			}
		} else if (top[card.suit()].isEmpty()) {
			throw new IOException(" Top stacks must start with aces.");
		} else if (top[card.suit()].peek().number() == card.number()-1) {  // if the stack this card needs to go on has the correct previous card already on it
			top[card.suit()].push(card);  // put it up
			board[stackNum].remove(board[stackNum].size()-1);  // take the card off the stack it was on
			if (! board[stackNum].isEmpty() && ! board[stackNum].get(board[stackNum].size()-1).faceUp()) {
				board[stackNum].get(board[stackNum].size()-1).flip();  // flip over the card underneath it
			}
		} else {
			throw new IOException(" Cards in top stacks must be in sequence.");
		}
	}

	/*
	 * put a card up from the flip stack
	 */
	public void up() throws IOException {
		if (flip.isEmpty()) {
			throw new IOException(" Can't move from empty stack.");
		}
		Card card = flip.peek();
		if (card.number() == 1) {
			top[card.suit()].push(card);  // put it up
			card.flip();  // it was flipping over for some reason
			flip.top();  // take the card off the flip stack
		} else if (top[card.suit()].isEmpty()) {
			throw new IOException(" Top stacks must start with aces.");
		} else if (top[card.suit()].peek().number() == card.number()-1) {  // if the stack this card needs to go on has the correct previous card already on it
			top[card.suit()].push(card);  // put it up
			card.flip();  // it was flipping over for some reason
			flip.top();  // take the card off the flip stack
		} else {
			throw new IOException(" Cards in top stacks must be in sequence.");
		}
	}

	/*
	 * move a card from one pile to another
	 */
	public void move(int from, int to) throws IOException {
		if (board[from].isEmpty()) {  // can't move from an empty pile
			throw new IOException(" Can't move from empty pile.");
		}
		int i = board[from].size()-1;  // get the index of the card to be moved - start with the top card
		while (i > 0 && board[from].get(i-1).faceUp()) {  // go down through all the face-up cards until you hit the bottom one
			i--;
		}
		Card move = board[from].get(i);  // the card on the bottom of the pile to be moved
		// check to make sure the move is legal
		if (board[to].isEmpty()) {
			if (move.number() != 13) {  // only a king can go into an empty pile
				throw new IOException(" Only kings can be placed into empty spaces.");
			}
		} else {
			Card moveTo = board[to].get(board[to].size()-1);
			if (move.number() != moveTo.number()-1) {  // check if the numbers are in sequence
				throw new IOException(" Cards in piles must be in sequence.");
			}
			if (move.black() == moveTo.black()) {  // check if the colors alternate
				throw new IOException(" Colors must alternate in piles.");
			}
		}
		// at this point we know the move is legal
		while (i < board[from].size()) {  // reuse the old i we used to determine the index of the bottom card to be moved
			board[to].add(board[from].remove(i));  // remove from the same place every time so things fall into that index when everything gets moved down
		}
		if (! board[from].isEmpty()) {  // flip over the card we just uncovered but only if it's there
			board[from].get(board[from].size()-1).flip();
		}
	}

	/*
	 * move from the flip stack to a pile
	 */
	public void move(int to) throws IOException {
		if (flip.isEmpty()) {
			throw new IOException(" Can't move from empty stack.");
		}
		Card move = flip.peek();  // get the card to be moved without removing it yet
		// check to make sure the move is legal
		if (board[to].isEmpty()) {
			if (move.number() != 13) {  // only a king can go into an empty pile
				throw new IOException(" Only kings can be placed into empty spaces.");
			}
		} else {
			Card moveTo = board[to].get(board[to].size()-1);
			if (move.number() != moveTo.number()-1) {  // check if the numbers are in sequence
				throw new IOException(" Cards in piles must be in sequence.");
			}
			if (move.black() == moveTo.black()) {  // check if the colors alternate
				throw new IOException(" Colors must alternate in piles.");
			}
		} // at this point the move is legal
		board[to].add(flip.top());
		board[to].get(board[to].size()-1).flip();
	}

	/*
	 * flip the top three cards off the flip stack
	 * or the remainder of the stack if there aren't three cards left
	 * or turn the stack over first if it's completely exhausted
	 */
	public void flip() {
		if (deck.isEmpty()) {
			while (! flip.isEmpty()) {
				deck.add(flip.top());
			}
		} else if (deck.size() < 3) {  // get the whole rest of the deck if there's fewer than 3 cards
			while (! deck.isEmpty()) {   // don't care if it's 1 or 2
				flip.add(deck.top());
			}
		} else {
			for (int i = 0; i < 3; i++) {
				flip.add(deck.top());
			}
		}
	}

	/*
	 * tell whether the player has won or not
	 */
	public boolean won() {
		if (flip.isEmpty() && deck.isEmpty()) {
			for (ArrayList<Card> stack : board) {
				if (! (stack.isEmpty() || stack.get(0).faceUp())) {  // if there is any stack that has face-down cards in it
					return false;
				}
			}
			return true;  // if no stack has face-down cards in it
		}
		return false;  // if either the flip stack or the deck has cards in it
	}

	/*
	 * auto-finish the game once it's won
	 */
	public void finish() throws InterruptedException {
		boolean done = false;
		while (! done) {
			done = true;
			for (int i = 0; i < 7; i++) {
				if (! board[i].isEmpty()) {
					done = false;  // done only if there are no more finished stacks
					try {
						up(i);
						// TODO make it delete the previous print first
						System.out.println(this);
						Thread.sleep(500);
					} catch (IOException e) {
						continue;
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		Game game = new Game();
		Scanner input = new Scanner(System.in);
		if (args.length > 0 && args[0].equals("-h")) {
			System.out.println(" This is a command line solitaire game. It lets you play solitaire on the command line.");
			System.out.println(" Enter a stack number 1-7 or \"s\" for the flip stack for your move's origin, then enter a stack number 1-7 for your move's destination or \"u\" to put the card up. Enter \"f\" to flip through the stack. Enter \"quit\" at any time to give up.");
			System.out.println(" Enter \"help\" at any time for a list of valid commands.");
		} else {
			System.out.println(game);
			System.out.println("Enter command:");
			while (! game.won()) {
				System.out.print("\033[2K"); // delete current line
				for (int i = 0; i <= game.getHeight(); i++) {
					System.out.print("\033[1A"); // move up one line
					System.out.print("\033[2K"); // delete current line
				}
				System.out.println(game);
				System.out.print(" Enter command: ");
				String command = input.nextLine().toLowerCase();
				Scanner c = new Scanner(command);
				if (! c.hasNext()) {
					game.setError("Enter a command.");
					continue; // don't crash if the user enters a blank command
				}
				String word = c.next(); // get the first portion of the command
				String orig = null;  // will be initialized later
				String dest = null;  // ^^
				if (! c.hasNext()) {
					// deal with the one-word and invalid commands
					if (word.equals("f")) {
						game.flip();
						continue;  // skip the two-word commands
					} else if (word.equals("quit")) {
						System.out.println(" Goodbye");
						input.close();
						System.exit(0); // lost/give up
					} else if (word.equals("help")) {
						game.setError("Enter a stack number 1-7 or \"s\" for the flip stack\n"
									   + "for your move's origin, then enter a stack number\n"
									   + "1-7 for your move's destination or \"u\" to put the\n"
									   + "card up. Enter \"f\" to flip through the stack.\n"
									   + "Enter \"quit\" at any time to give up.");
						continue;  // skip the two-word commands
					} else if (word.length() == 2) {  // if the user enters two valid characters without a space
						orig = word.substring(0, 1);
						dest = word.substring(1, 2);
					} else {
						game.setError("Enter a valid command.\nEnter \"help\" for a list of valid commands.");
						continue;  // skip the two-word commands
					}
				}  // deal with all the valid two-word commands
				if (orig == null && dest == null) {
					orig = word;
					dest = c.next();
				}
				// the only command without a number portion
				if (orig.equals("s") && dest.equals("u")) {
					try {
						game.up();
					} catch (IOException e) {
						game.setError(e.getMessage());
					}
				} else {
					int to = 0;
					int from = 0;
					// get the pile numbers if they aren't stack or up
					if (! orig.equals("s")) {
						try {
							from = Integer.parseInt(orig);
							if (from < 1 || from > 7) {
								game.setError("Enter pile numbers between 1 and 7.");
								continue;
							}
						} catch (NumberFormatException e) {
							game.setError(" Enter a valid origin pile.");
							continue;
						}
					}
					if (! dest.equals("u")) {
						try {
							to = Integer.parseInt(dest);
							if (to < 1 || to > 7) {
								game.setError(" Enter pile numbers between 1 and 7.");
								continue;
							}
						} catch (NumberFormatException e) {
							game.setError(" Enter a valid destination pile.");
							continue;
						}
					}
					to--; // convert from pile number 1-7 to array index 0-6
					from--; // convert from pile number 1-7 to array index 0-6
					// moves involving either the top stacks or the flip stack
					if (orig.equals("s")) {
						try {
							game.move(to);
						} catch (IOException e) {
							game.setError(e.getMessage());
						}
					} else if (dest.equals("u")) {
						try {
							game.up(from);
						} catch (IOException e) {
							game.setError(e.getMessage());
						}
					} else { // moves from one pile to another
						try {
							game.move(from, to);
						} catch (IOException e) {
							game.setError(e.getMessage());
						}
					}
				}
				c.close();  // close the scanner on this turn's command
			}  // won
			System.out.println(game);
			//	System.out.println(" KS  KH  KD  KC      --  --");
			try {
				game.finish();
			} catch (InterruptedException e) {
			}
			System.out.println("You win! Congratulations!\n\n");
			input.close();
			System.exit(0);
		}
	}
}
