import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

//import javax.imageio.ImageIO;

//import javafx.application.Application;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.StackPane;
//import javafx.stage.Stage;
//import javafx.stage.WindowEvent;

public class Game {

	ArrayList<Card>[] board;  // hold the seven piles of cards that make up the game
	Stack<Card>[] top;    // the four finished stacks that go at the top
	Deck deck;
	Deck flip;  // the deck that cards are flipped to from the flip stack
	//    Stage stage;
	//    Pane pane;

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
	}

	public String toString() {
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
			i++;
			sb.append("\n ");
		}
		while (i <= 6) {
			sb.append("\n");
			i++;
		}
		return sb.toString();
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
			if (move.black() == moveTo.black()) {  // check if the colors alternate
				throw new IOException(" Colors must alternate in piles.");
			}
			if (move.number() != moveTo.number()-1) {  // check if the numbers are in sequence
				throw new IOException(" Cards in piles must be in sequence.");
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
			if (move.black() == moveTo.black()) {  // check if the colors alternate
				throw new IOException(" Colors must alternate in piles.");
			}
			if (move.number() != moveTo.number()-1) {  // check if the numbers are in sequence
				throw new IOException(" Cards in piles must be in sequence.");
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
						System.out.println(this);
						Thread.sleep(500);
					} catch (IOException e) {
						continue;
					}
				}
			}
		}
	}

	/*
	 * animate some celebratory ASCII fireworks
	 */
	public void celebrate() throws InterruptedException {
		String[] frames = new String[21];
		frames[0] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n          *";
		frames[1] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n          *\n          *\n          *\n          *";
		frames[2] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n          *\n          *\n          *\n          *\n          *\n          *\n          *\n          *\n          *";
		frames[3] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n          *\n          *\n          *\n          *\n          *\n          *\n          *\n          *\n          *\n          *\n          *\n          *\n          *\n          *\n          *\n          *\n          *";
		frames[4] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n        _\\ /_"
				+   "\n         / \\"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *";
		frames[5] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n        *   *"
				+   "\n         \\ /"
				+   "\n      * - x - *"
				+   "\n         / \\"
				+   "\n        *   *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *                *"
				+   "\n          *                *";
		frames[6] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n       *.   .*"
				+   "\n     *   \\ /   *"
				+   "\n      . - x - ."
				+   "\n     *   / \\   *"
				+   "\n       *     *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *                 *"
				+   "\n          *                *"
				+   "\n          *                *"
				+   "\n          *                *"
				+   "\n          *                *"
				+   "\n          *                *"
				+   "\n          *                *";
		frames[7] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n       * . . *"
				+   "\n    *.  \\   /  .*"
				+   "\n   *  \\  *    /  *"
				+   "\n   *  . - x - .  *"
				+   "\n   * : /  .  \\ : *"
				+   "\n    *   /   \\   *"
				+   "\n       *     *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *"
				+   "\n          *                   *"
				+   "\n          *                  *"
				+   "\n          *                  *"
				+   "\n          *                 *"
				+   "\n          *                 *"
				+   "\n          *                 *"
				+   "\n          *                 *"
				+   "\n          *                 *"
				+   "\n          *                *"
				+   "\n          *                *"
				+   "\n          *                *"
				+   "\n          *                *"
				+   "\n          *                *"
				+   "\n          *                *"
				+   "\n          *                *";
		frames[8] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n       * . . *"
				+   "\n   *.  \\     /  .*"
				+   "\n  *  \\    *     /  *"
				+   "\n  *  . -     - .   *"
				+   "\n  *   :   .    :   *"
				+   "\n   *   .     .    *"
				+   "\n   *    /   \\    *"
				+   "\n       *     *                  \\ /"
				+   "\n          *                    - * -"
				+   "\n                                / \\"
				+   "\n          *                    *"
				+   "\n                              *"
				+   "\n          *                   *"
				+   "\n                             *"
				+   "\n          *                  *"
				+   "\n                            *"
				+   "\n          *                 *"
				+   "\n                            *"
				+   "\n          *                 *"
				+   "\n                            *"
				+   "\n          *                *"
				+   "\n                           *"
				+   "\n          *                *"
				+   "\n                           *"
				+   "\n          *                *"
				+   "\n                           *"
				+   "\n          *                *";
		frames[9] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n        .   ."
				+   "\n    * .       . *"
				+   "\n  *  \\    *    /  *"
				+   "\n *  .  -     -  .  *"
				+   "\n*    :    .    :    *"
				+   "\n*     .       .     *"
				+   "\n *     /     \\     *         *   *"
				+   "\n   *      *      *          *  \\ /  *"
				+   "\n     *         *           <  - * -  >"
				+   "\n                            *  / \\  *"
				+   "\n          *                   *   *"
				+   "\n                              *"
				+   "\n          *                   *"
				+   "\n                             *"
				+   "\n          *                  *"
				+   "\n                            *"
				+   "\n          *                 *"
				+   "\n                            *"
				+   "\n          *                 *"
				+   "\n                            *"
				+   "\n          *                *"
				+   "\n                           *"
				+   "\n          *                *"
				+   "\n                           *"
				+   "\n          *                *"
				+   "\n                           *"
				+   "\n          *                *";
		frames[10] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n        .   ."
				+   "\n    * .       . *"
				+   "\n  *  '    .    '  *"
				+   "\n*     .       .     *"
				+   "\n*   :  -  .  -  :   *"
				+   "\n*     .       .     *         .   ."
				+   "\n *        *        *        *       *"
				+   "\n   *  '       '  *        *   \\   /   *"
				+   "\n     *         *         <  : - * - :  >"
				+   "\n                          *   /   \\   *"
				+   "\n          *                *    .    *"
				+   "\n                              '   '"
				+   "\n                              *"
				+   "\n                              "
				+   "\n          *                  *"
				+   "\n                             "
				+   "\n                            *"
				+   "\n                             "
				+   "\n          *                 *"
				+   "\n                             "
				+   "\n                           *"
				+   "\n                            "
				+   "\n          *                *"
				+   "\n                            "
				+   "\n                  *        *"
				+   "\n                  *              *"
				+   "\n          *       *        *     *";
		frames[11] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n      * .   . *"
				+   "\n  *  '    .    '  *"
				+   "\n *    .       .    *"
				+   "\n    :     .     :    "
				+   "\n'     .       .     '        *     *"
				+   "\n*         *         *     * '   '   ' *"
				+   "\n  *   '       '   *      *    \\   /    *"
				+   "\n     *         *        *   : - * - :   *"
				+   "\n                        *   /  / \\  \\   *"
				+   "\n          *              *    .   .    *"
				+   "\n                            *   '   *"
				+   "\n                              *   *"
				+   "\n                              "
				+   "\n          *                  *"
				+   "\n                 *           "
				+   "\n                 *          *"
				+   "\n                 *           "
				+   "\n          *       *         *"
				+   "\n                  *             *"
				+   "\n                  *        *    *"
				+   "\n                  *              *"
				+   "\n          *       *        *     *"
				+   "\n                  *              *"
				+   "\n                  *        *     *"
				+   "\n                  *              *"
				+   "\n          *       *        *     *";
		frames[12] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n        .   ."
				+   "\n     '    .    '"
				+   "\n    .           ."
				+   "\n   .   :  .  :   ."
				+   "\n    .           .            *  '  *"
				+   "\n        .   .* *         *  '  ' '  '  *"
				+   "\n           * *** *      * '  \\  '  /  ' *"
				+   "\n           * *** *     *   :  - * -  :   *"
				+   "\n             * *       * '  /  / \\  \\  ' *"
				+   "\n               *        *  '  .   .  '  *"
				+   "\n                *         * ' / '  '  *"
				+   "\n                *           X  ' '  *"
				+   "\n                *         /  *\\"
				+   "\n                 *           **"
				+   "\n                 *            *"
				+   "\n                 *             *"
				+   "\n                 *             *"
				+   "\n                  *         *   *"
				+   "\n                  *             *"
				+   "\n                  *             *"
				+   "\n                  *              *"
				+   "\n                  *        *     *"
				+   "\n                  *              *"
				+   "\n                  *              *"
				+   "\n                  *              *"
				+   "\n                  *        *     *";
		frames[13] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n        .    "
				+   "\n          ."
				+   "\n    .           ."
				+   "\n          .   ."
				+   "\n    .      * * * *            *  '  *"
				+   "\n         *  * * *  *      *  '  ' '  '  *"
				+   "\n        * *  ***  * *    * '  \\  '  /  ' *"
				+   "\n        * *  ***  * *   *   :  - * -  :   *"
				+   "\n         *  * * *  *    * '  /  / \\  \\  ' *"
				+   "\n           * * * *      * *'  *   .  '  *"
				+   "\n                *       * \\ ' / *  '  *"
				+   "\n                *      * >  X  < *  *"
				+   "\n                *       * /  *\\ *"
				+   "\n                 *        x  **"
				+   "\n                 *            *"
				+   "\n                 *             *"
				+   "\n                 *             *"
				+   "\n                  *         *   *"
				+   "\n                  *             *"
				+   "\n                  *             *"
				+   "\n                  *              *"
				+   "\n                  *        *     *"
				+   "\n                  *              *"
				+   "\n                  *              *"
				+   "\n        *         *              *"
				+   "\n        *         *        *     *";
		frames[14] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n        .    "
				+   "\n          ."
				+   "\n    .           ."
				+   "\n          .  * *"
				+   "\n    .    *    *    *          *    *"
				+   "\n        *   *   *   *     *  '      '  *"
				+   "\n       *  *  ***  *  *   * '  \\  '  /  ' *"
				+   "\n       *  *  ***  *  *  *   :    -    :   *"
				+   "\n        *   *   *   *   * '   /  .  \\   ' *"
				+   "\n         *    *    *    * *\\ /*          *"
				+   "\n            *   *       \\ \\ ' / /   '  *"
				+   "\n                *      * >  X  < *  *"
				+   "\n                        / / . \\ \\"
				+   "\n                 *         / \\*"
				+   "\n                *             *"
				+   "\n               * *             *"
				+   "\n              *                *"
				+   "\n             *    *         *   *"
				+   "\n            *                   *"
				+   "\n           *      *             *"
				+   "\n          *                      *"
				+   "\n          *       *        *     *"
				+   "\n         *                       *"
				+   "\n         *        *              *"
				+   "\n        *                        *"
				+   "\n        *         *        *     *";
		frames[15] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n            *   *"
				+   "\n        *     *     *         '    '"
				+   "\n       *   *  *  *   *    '  '      '  '"
				+   "\n      *  *   * *   *  *  __/\\__'   '   '   '"
				+   "\n      *  *   * *   *  * ' >##<    -    :   '"
				+   "\n       *   *  *  *   *  '*|/\\|   .   '    '"
				+   "\n        *     *     *  ** *\\ /* /*       ."
				+   "\n            *   *     *\\  * ' *  /* '  ."
				+   "\n                *   ** * >  X  < * *"
				+   "\n                   * * /  * . *  \\ *"
				+   "\n                 *    *  / / \\ \\  *"
				+   "\n                *       * * * * *"
				+   "\n               * *             *"
				+   "\n              *                *"
				+   "\n             *    *             *"
				+   "\n            *                   *"
				+   "\n           *      *             *"
				+   "\n          *                      *"
				+   "\n          *       *              *"
				+   "\n         *                       *"
				+   "\n         *        *              *"
				+   "\n        *                        *"
				+   "\n        *         *              *";
		frames[16] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n            *   *          /\\"
				+   "\n       *      *      *    /  \\'    '"
				+   "\n      *    *  *  *    *  /    \\     '  '"
				+   "\n     *   *   * *--------/      \\--------'   '"
				+   "\n     *   *  *   *\\                    /   :   '"
				+   "\n      *   *  * *  *\\                /  '    '"
				+   "\n       *   *     *   >            <       ."
				+   "\n           *  *  *  /     .''.     \\ /* '  ."
				+   "\n                *  /   .-'  X '-.   \\*"
				+   "\n                  / .-'/  * . *  '-. \\"
				+   "\n                 *-'  *  / / \\ \\  * '-"
				+   "\n                *       *       *"
				+   "\n               * *        * * *"
				+   "\n              *                 "
				+   "\n             *    *             *"
				+   "\n            *                    "
				+   "\n           *      *             *"
				+   "\n          *                       "
				+   "\n          *       *              *"
				+   "\n         *                        "
				+   "\n         *        *              *"
				+   "\n        *                         "
				+   "\n        *         *              *";
		frames[17] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n                           /\\"
				+   "\n                          /  \\"
				+   "\n                         /    \\"
				+   "\n                         /    \\"
				+   "\n            *   *       /      \\"
				+   "\n       *      *      * /        \\"
				+   "\n      *    *  *  *    /          \\  '"
				+   "\n  -------------------/            \\-------------------"
				+   "\n   \\                                                /"
				+   "\n     \\                                            /"
				+   "\n       \\                                        /"
				+   "\n         \\                                    /"
				+   "\n           \\                                /"
				+   "\n             \\                            /"
				+   "\n              >                          <"
				+   "\n             /                          \\"
				+   "\n            /                            \\"
				+   "\n           /             .--.             \\"
				+   "\n           /          .-'    '-.          \\"
				+   "\n          /        .-'          '-.        \\"
				+   "\n         /      .-'             *  '-.      \\"
				+   "\n         /   .-'                      '-.   \\"
				+   "\n        / .-'     *              *       '-. \\"
				+   "\n        ''                                   ''"
				+   "\n         *                       *"
				+   "\n                                  "
				+   "\n        *         *              *";
		frames[18] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n                       /        \\"
				+   "\n                      /          \\"
				+   "\n                      /          \\"
				+   "\n                     /            \\"
				+   "\n                    /              \\"
				+   "\n                   /                \\"
				+   "\n                   /                \\"
				+   "\n            *     /                  \\"
				+   "\n       *      *  /                    \\"
				+   "\n      *    *    /                      \\  '"
				+   "\n---------------/                        \\---------------------"
				+   "\n\n\n\n\n\n\n"
				+   "\n                                                             /"
				+   "\n                                                           /"
				+   "\n                                                         /"
				+   "\n\\                                                      /"
				+   "\n  \\                                                  /"
				+   "\n   >                                                <"
				+   "\n  /                                                  \\"
				+   "\n /                                                    \\"
				+   "\n                                                       \\"
				+   "\n                                                       \\"
				+   "\n                                                        \\"
				+   "\n                                                         \\";
		frames[19] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+   "\n            /                              \\"
				+   "\n           /                                \\"
				+   "\n          /                                  \\"
				+   "\n         /                                    \\"
				+   "\n        /                                      \\"
				+   "\n        /                                      \\"
				+   "\n       /                                        \\"
				+   "\n      /                                          \\"
				+   "\n     /                                            \\"
				+   "\n    /                                              \\"
				+   "\n---/                                                \\---------"
				+   "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		frames[20] = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
				+    "\n                      You win!"
				+    "\n                  Congratulations!"
				+    "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		for (int i = 0; i < frames.length; i++) {
			System.out.println(frames[i]);
			Thread.sleep(300);
		}
	}

	//    public void start(Stage primaryStage) throws IOException {
	//	StackPane root = new StackPane();
	//	Scene scene = new Scene(root);
	//	stage = primaryStage;
	//	Card card = new Card(1, 0);
	//	pane = new Pane();
	//	root.getChildren().add(pane);
	//	primaryStage.setScene(scene);
	//	primaryStage.setOnCloseRequest((WindowEvent we) -> {
	//	    System.exit(0);
	//	});
	//	primaryStage.show();
	//  }

	public static void main(String[] args) {
		Game game = new Game();
		Scanner input = new Scanner(System.in);
		System.out.println(" Welcome to my amazing solitaire player!");
		System.out.println(" Enter a stack number 1-7 or \"s\" for the flip stack for your move's origin, then");
		System.out.println(" enter a stack number 1-7 or \"u\" for a top stack for your move's destination.");
		System.out.println(" Enter \"f\" to flip through the stack.");
		System.out.println(" Enter \"quit\" at any time to give up.");
		System.out.println(" Enter \"help\" at any time for a list of valid commands.");
		while (! game.won()) {
			System.out.println(game);
			System.out.print(" Enter command: ");
			String command = input.nextLine().toLowerCase();
			Scanner c = new Scanner(command);
			if (! c.hasNext()) {
				continue; // don't crash if the user enters a blank command
			}
			String word = c.next(); // get the first portion of the command
			if (! c.hasNext()) {
				// deal with the one-word and invalid commands
				if (word.equals("f")) {
					game.flip();
				} else if (word.equals("quit")) {
					System.out.println(" Goodbye");
					input.close();
					System.exit(0); // lost/give up
				} else if (word.equals("help")) {
					System.out.println(" Enter a stack number 1-7 or \"s\" for the flip stack for your move's origin.");
					System.out.println(" Then enter a stack number 1-7 or \"u\" for a top stack for your move's destination.");
					System.out.println(" Enter \"f\" to flip through the stack.");
					System.out.println(" Enter \"quit\" at any time to give up.");
				} else {
					System.out.println(" Enter a valid command.\n Enter \"help\" for a list of valid commands.");
				}
			} else {
				// deal with all the valid two-word commands
				String orig = word;
				String dest = c.next();
				// the only command without a number portion
				if (orig.equals("s") && dest.equals("u")) {
					try {
						game.up();
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
				} else {
					int to = 0;
					int from = 0;
					// get the pile numbers if they aren't stack or up
					if (! dest.equals("u")) {
						try {
    							to = Integer.parseInt(dest);
    							if (to < 1 || to > 7) {
    								System.out.println(" Enter pile numbers between 1 and 7.");
    								continue;
    							}
    						} catch (NumberFormatException e) {
    							System.out.println(" Enter a valid destination pile.");
    							continue;
    						}
    					}
    					if (! orig.equals("s")) {
    						try {
    							from = Integer.parseInt(orig);
    							if (from < 1 || from > 7) {
    								System.out.println(" Enter pile numbers between 1 and 7.");
    								continue;
    							}
    						} catch (NumberFormatException e) {
    							System.out.println(" Enter a valid origin pile.");
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
    							System.out.println(e.getMessage());
    						}
    					} else if (dest.equals("u")) {
    						try {
    							game.up(from);
    						} catch (IOException e) {
    							System.out.println(e.getMessage());
    						}
    					} else { // moves from one pile to another
    						try {
    							game.move(from, to);
    						} catch (IOException e) {
    							System.out.println(e.getMessage());
    						}
    					}
    				}
    			}
    			c.close();  // close the resource leak
		}  // won
		System.out.println(game);
		//	System.out.println(" KS  KH  KD  KC      --  --");
		try {
			game.finish();
		} catch (InterruptedException e) {
		}
		try {
			game.celebrate();
		} catch (InterruptedException e) {
		}
		input.close();
		System.exit(0);
	}
}
