package blackjack.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> deck;
    private static final Rank[] ranks = {Rank.A, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX, Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.J, Rank.Q, Rank.K};
    private static final Suit[] suits = {Suit.C, Suit.D, Suit.H, Suit.S};

    public Deck() {
        this.buildDeck();
    }

    public Deck(int n) {
        this.buildDeck(n);
    }

    private void buildDeck() {
        deck = new ArrayList<>();
        for (Suit s : suits) {
            for (Rank r : ranks) {
                deck.add(new Card(r, s));
            }
        }
        shuffleDeck();
    }

    private void buildDeck(int n) {
        deck = new ArrayList<>();
        for (int k = 0; k < n; k++) {
            for (Suit s : suits) {
                for (Rank r : ranks) {
                    deck.add(new Card(r, s));
                }
            }
        }
        shuffleDeck();
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public Card getCard() {
        if (deck.isEmpty()) {
            throw new IllegalStateException("Deck is empty!");
        }
        return deck.remove(deck.size() - 1);
    }
}
