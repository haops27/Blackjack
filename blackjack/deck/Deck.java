package blackjack.deck;
import java.util.*;

public class Deck {
	private List<Card> deck;
	private static final Rank[] ranks = {Rank.A, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX, Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.J, Rank.Q, Rank.K};
    private static final Suit[] suits = {Suit.C, Suit.D, Suit.H, Suit.S};
	
	//Building and shuffling deck
	public Deck() {
		this.buildDeck();
	}
	
	//Building and shuffling n 52-card decks into 1 single deck
	public Deck(int n) {
		this.buildDeck(n);
	}
	
	private void buildDeck() {
		deck = new ArrayList<>(); // new deck variable to store all of the cards
        
        // adding each cards into deck
	    for (Suit s : suits) {
	    	for (Rank r : ranks) {
	    		Card card = new Card(r, s);
	    		deck.add(card);
	    	}
	    }
	    
		this.shuffleDeck();
	}
	
	private void buildDeck(int n) {
		deck = new ArrayList<>(); // new deck variable to store all of the cards
        
        // adding each cards into deck
        for (int k = 0; k < n; k++) {
        	for (Suit s : suits) {
				for (Rank r : ranks) {
					Card card = new Card(r, s);
					deck.add(card);
				}
			}
        }
	    
		this.shuffleDeck();
	}
	
	//Shuffle deck
	public void shuffleDeck() {
		Collections.shuffle(deck);
	}

	public Card getCard() {
		return deck.removeLast();
	}
	
}
