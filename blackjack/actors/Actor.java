package blackjack.actors;

import blackjack.deck.Card;
import blackjack.deck.Deck;

public abstract class Actor {
    protected Hand hand;

    public Actor() {
        this.hand = new Hand();
    }

    public void addCard(Card card) {
        hand.addCard(card);
    }

    public int getSum() {
        return hand.getSum();
    }

    public Hand getHand() {
        return hand;
    }

    public boolean isBust() {
        return hand.isBust();
    }

    public abstract void takeTurn(Deck deck);

    public void reset() {
        hand.reset();
    }
}
