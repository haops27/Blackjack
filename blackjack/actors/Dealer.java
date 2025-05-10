package blackjack.actors;

import blackjack.deck.*;

public class Dealer extends Actor {
    public Dealer() {
        super();
    }

    @Override
    public void takeTurn(Deck deck) {
        while (hand.getSum() < 17) {
            addCard(deck.getCard());
        }
    }
}
