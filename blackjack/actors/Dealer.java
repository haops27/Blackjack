package blackjack.actors;

import blackjack.deck.*;

public class Dealer extends Actor {
    public Dealer() {
        super();
    }

    public Card showFirstCard() {
        return hand.getCard(0);
    }

    @Override
    public void hit(Deck deck) {
        while (hand.getSum() < 17) {
            addCard(deck.getCard());
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dealer: ");
        sb.append(hand);
        return sb.toString();
    }
    
}
