package blackjack.actors;

import blackjack.deck.*;

public class Player extends Actor {
    private float bet;  // Số tiền cược của người chơi
    private float sidebets; // Số tiền cược phụ của người chơi
    private float tokens = 2500f; // Tổng số tiền người chơi đang có, bao gồm cả tiền cược
    private final String name; // Tên của người chơi

    // Constructor
    public Player(String name) {
        super();
        this.name = name;
    }

    public Player(String name, float tokens) {
        super();
        this.name = name;
        this.tokens = tokens;
    }

    public float getTokens() {
		return tokens;
	}

	public void setTokens(float tokens) {
		this.tokens = tokens;
	}

	// Thiết lập số tiền cược
    public void setBet(float bet) {
        this.bet = bet;
    }

    // Lấy số tiền cược
    public float getBet() {
        return bet;
    }

    public float getSidebets() {
        return sidebets;
    }

    public void setSidebets(float sidebets) {
        this.sidebets = sidebets;
    }
    
    public boolean hasBlackjack() {
    	return hand.size() == 2 && sum == 21;
    }

    public String getName() {
		return name;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Player ");
        sb.append(name);
        sb.append(": ");
        sb.append(hand);
        sb.append(", sum: ");
        sb.append(getSum());
        return sb.toString();
    }

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Player o) {
			return o.getName().equals(name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
