public class Card {

    private int num;  // 1-13
    private int suit;  // 1-4
    private boolean faceUp;  // whether the card is ret up or not
    private static final String WHITE_BKG = "\u001B[48;5;253m";
    private static final String BLACK_TXT = "\u001B[30;1m";
    private static final String RED_TXT = "\u001B[31;1m";
    private static final String RESET_COLOR = "\u001B[0m";

    /*
     * takes a number 1-13 for A-K and a number 0, 1, 2, 3 for spade, heart, diamond, club
     */
    public Card(int num, int suit) {
	this.num = num;
	this.suit = suit;
	faceUp = false;
    }

    public String toString() {
	if (! faceUp) {
	    return "***";
	}
	String ret = "";
	switch (suit) {
	case 0:
	    ret = "S";
	    break;
	case 1:
	    ret = "H";
	    break;
	case 2:
	    ret = "D";
	    break;
	case 3:
	    ret = "C";
	    break;
	}
	switch (num) {
	// non-number cases: face cards & ace
	case 13:
	    ret = "K" + ret + " ";
	    break;
	case 12:
	    ret = "Q" + ret + " ";
	    break;
	case 11:
	    ret = "J" + ret + " ";
	    break;
	case 1:
	    ret = "A" + ret + " ";
	    break;
	    // no spacing because it's too wide
	case 10:
	    ret = "10" + ret;
	    break;
	    // everything else should be the same
	default:
	    ret = num + ret + " ";
	}
	switch (suit) {
	case 0:
	case 3:
		ret = WHITE_BKG + BLACK_TXT + ret;
		break;
	case 1:
	case 2:
		ret = WHITE_BKG + RED_TXT + ret;
		break;
	}
	ret += RESET_COLOR;
	return ret;
    }

    public void flip() {
	faceUp = ! faceUp;
    }

    public boolean faceUp() {
	return faceUp;
    }

    public int number() {
	if (faceUp) {
	    return num;
	}
	return -1;
    }

    public int suit() {
	if (faceUp) {
	    return suit;
	}
	return -1;
    }

    /*
     * is this card black
     */
    public boolean black() {
	return (suit == 0 || suit == 3);
    }
}

