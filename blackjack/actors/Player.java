package blackjack.actors;

import blackjack.deck.*;

public class Player extends Actor {
    private int bet;  // Số tiền cược của người chơi

    // Constructor
    public Player() {
        super();
    }

    // Thiết lập số tiền cược
    public void setBet(int bet) {
        this.bet = bet;
    }

    // Lấy số tiền cược
    public int getBet() {
        return bet;
    }

    // Cài đặt hành vi cho người chơi: người chơi có thể "Hit" hoặc "Stay"
    @Override
    public void takeTurn(Deck deck) {
        // Người chơi sẽ tự quyết định hành vi của mình, ví dụ: chỉ đơn giản rút bài
        // Thực hiện hành động "Hit" cho đến khi quyết định "Stay"
        // Đây là ví dụ, bạn có thể thêm logic để người chơi quyết định
        Card card = deck.getCard();  // Lấy lá bài cuối cùng
        addCard(card);
    }
}
