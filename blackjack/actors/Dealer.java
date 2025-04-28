package blackjack.actors;

import blackjack.deck.*;

public class Dealer extends Actor {
    // Constructor
    public Dealer() {
        super();
    }

    // Cài đặt hành vi cho nhà cái: nhà cái rút bài cho đến khi có ít nhất 17 điểm
    @Override
    public void takeTurn(Deck deck) {
        // Nhà cái rút bài cho đến khi có ít nhất 17 điểm
        while (sum < 17) {
        	Card card = deck.getCard(); 
            addCard(card);
        }
    }
}
