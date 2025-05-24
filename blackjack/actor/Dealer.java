package blackjack.actor;

import blackjack.deck.*;

public class Dealer implements Playable {
    private final Hand hand;

    public Dealer() {
        hand = new Hand();
    }
    
    public Hand getHand() {
    	return hand;
    }

    @Override
    public void addCard(Card card) {
        hand.addCard(card);
    }

    @Override
    public int getSum() {
        return hand.getSum();
    }

    @Override
    public boolean isBust() {
        return hand.isBust();
    }

    @Override
    public boolean isBlackjack() {
        return hand.isBlackjack();
    }

    public void hit(Deck deck) {
        while (hand.getSum() < 17) {
            addCard(deck.getCard());
        }
    }

    @Override
    public void reset(Deck deck) {
        for (int i = 0; i < hand.numCards(); i++) {
            deck.discard(hand.getCard(i));
        }
        hand.reset();
    }

    public Card showFirstCard() {
        return hand.getCard(0);
    }

    @Override
    public String toString() {
        return "Dealer: " + hand.toString();
    }
}
