package players;

import cards.Card;
import cards.Deck;

public interface Playable {
    void addCard(Card card);
    int getSum();
    boolean isBust();
    boolean isBlackjack();
    void reset(Deck deck);
}
