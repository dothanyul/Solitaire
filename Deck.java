import java.util.LinkedList;
import java.util.Collections;

public class Deck {

	private LinkedList<Card> deck;

	public Deck() {
		deck = new LinkedList<Card>();
		for (int i = 0; i < 52; i++) {
			deck.add(new Card(i % 13 + 1, i / 13));
		}
	}

	/*
	 * get a new empty deck
	 */
	public static Deck empty() {
		Deck ret = new Deck();
		ret.clear();
		return ret;
	}

	/*
	 * only for use in the above method to get a new empty deck
	 */
	private void clear() {
		deck.clear();
	}

	public void shuffle() {
		Collections.shuffle(deck);
	}

	/*
	 * take the top card off the deck
	 */
	public Card top() {
		Card ret = deck.remove(deck.size() - 1);
		ret.flip();
		return ret;
	}

	/*
	 * look at the top card from the deck
	 */
	public Card peek() {
		return deck.get(deck.size() - 1);
	}

	public boolean isEmpty() {
		return deck.isEmpty();
	}

	/*
	 * add the given card to the top of the deck
	 */
	public void add(Card card) {
		deck.add(card);
	}

	/*
	 * the number of cards in the deck
	 */
	public int size() {
		return deck.size();
	}
}