package blackjack.actors;

import blackjack.deck.*;

public class Player extends Actor {
    private float bet;  // Số tiền cược của người chơi
    private float tokens;

    // Constructor
    public Player() {
        super();
        tokens = 5000;
    }

    public float getTokens() {
		return tokens;
	}

	public void setTokens(float tokens) {
		this.tokens = tokens;
	}

	// Thiết lập số tiền cược
    public void setBet(int bet) {
        this.bet = bet;
    }

    // Lấy số tiền cược
    public float getBet() {
        return bet;
    }
    
    public boolean hasBlackjack() {
    	return hand.size() == 2 && sum == 21;
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
