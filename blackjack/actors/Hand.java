package blackjack.actors;

import blackjack.deck.Card;
import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> cards;
    private int sum;
    private int aceCount;

    public Hand() {
        this.cards = new ArrayList<>();
        this.sum = 0;
        this.aceCount = 0;
    }

    public void addCard(Card card) {
        cards.add(card);
        sum += card.getValue();
        if (card.isAce()) aceCount++;
        adjustForAce();
    }

    private void adjustForAce() {
        while (sum > 21 && aceCount > 0) {
            sum -= 10;
            aceCount--;
        }
    }

    public int getSum() {
        return sum;
    }

    public int numCards() {
        return cards.size();
    }

    public Card getCard(int index) {
        return cards.get(index);
    }

    public boolean isBust() {
        return sum > 21;
    }

    public boolean isBlackjack() {
        return cards.size() == 2 && sum == 21;
    }

    public void reset() {
        cards.clear();
        sum = 0;
        aceCount = 0;
    }

    @Override
    public String toString() {
        return cards + " (sum: " + sum + ")";
    }
}
