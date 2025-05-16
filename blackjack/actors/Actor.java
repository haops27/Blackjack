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

    public boolean isBust() {
        return hand.isBust();
    }
    
    public boolean isBlackjack() {
    	return hand.isBlackjack();
    }

    public abstract void hit(Deck deck);

    public void reset() {
        hand.reset();
    }
}
