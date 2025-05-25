package blackjack.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> deck;
    private final List<Card> discardPile;
	private static final Card WILDCARD = new Card(null, null);
	private boolean wildCardReached = false;
	private int position;
    private static final Rank[] ranks = {Rank.A, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX, Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.J, Rank.Q, Rank.K};
    private static final Suit[] suits = {Suit.C, Suit.D, Suit.H, Suit.S};

    public Deck() {
        deck = new ArrayList<>();
        for (Suit s : suits) {
            for (Rank r : ranks) {
                deck.add(new Card(r, s));
            }
        }
        discardPile = new ArrayList<>();
        shuffleDeck();
    }

    public Deck(int n) {
        deck = new ArrayList<>();
        for (int k = 0; k < n; k++) {
            for (Suit s : suits) {
                for (Rank r : ranks) {
                    deck.add(new Card(r, s));
                }
            }
        }
        discardPile = new ArrayList<>();
        shuffleDeck();
    }

    public void discard(Card card) {
  	    discardPile.add(card);
  	}

    private void placeRedCard() {
    	position = (int)(deck.size() * 0.75);
    	deck.add(position, WILDCARD);
    }

    //Reshufflle
  	public boolean reshuffle() {
  	    if (wildCardReached) {
            deck.addAll(discardPile);
            discardPile.clear();
            shuffleDeck();
            wildCardReached = false;
            return true;
        }
        return false;
  	}

    public void shuffleDeck() {
        Collections.shuffle(deck);
        placeRedCard();
    }

    public Card getCard() {
  		if (deck.isEmpty()) {
  			throw new IllegalStateException("Deck is empty!");
  		}
  		Card card = deck.removeLast();
  		if (card.equals(WILDCARD)) {
  			wildCardReached = true;
  			return getCard();
  		}
  		return card;
  	}

}
