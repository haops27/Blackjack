package blackjack.actors;

import java.util.ArrayList;
import java.util.List;
import blackjack.deck.Card;

public class Hand {
    protected List<Card> cards;
    protected int sum;
    protected int aceCount;

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

    protected void adjustForAce() {
        while (sum > 21 && aceCount > 0) {
            sum -= 10;
            aceCount--;
        }
    }

    public int getSum() {
        return sum;
    }

    public List<Card> getCards() {
        return cards;
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
