package blackjack.actor;

import blackjack.deck.*;

public interface Playable {
    void addCard(Card card);
    int getSum();
    boolean isBust();
    boolean isBlackjack();
    void hit(Deck deck);
    void reset();
}
