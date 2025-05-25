package blackjack.deck;

public class Card {
	// Name of each cards in folder "cards"
	private final Rank rank; // Số
	private final Suit suit; // Chất
	
	public Card(Rank rank, Suit suit) {
		this.rank = rank;
		this.suit = suit;
	}
	
    @Override
	public String toString() {
		if (rank == null && suit == null) {
			return "WILD CARD";
		}
		return rank + " - " + suit;
	}
	
	//get value of each cards: 2-10 are equal to themselves; J, Q, K are considered as 10; Ace is 1 or 11 for some case
	public int getValue() {
		return rank.getBlackjackValue();
	}
	
	public Rank getRank() {
		return rank;
	}
	
	public Suit getSuit() {
		return suit;
	}
	
	//check that a cards is an Ace or not
	public boolean isAce() {
		return rank == Rank.A;
	}

	public boolean equalRank(Card c) {
		return rank == c.rank;
	}

	public boolean equalSuit(Card c) {
		return suit == c.suit;
	}

	public boolean equalColor(Card c) {
		return suit.getColor() == c.suit.getColor();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Card c) {
			return equalRank(c) && equalSuit(c);
		}
		return false;
	}

	public String getImagePath() {
	    // Xử lý đặc biệt cho các lá bài A, J, Q, K
	    String rankStr;
	    switch (rank) {
	        case A: rankStr = "A"; break;
	        case J: rankStr = "J"; break;
	        case Q: rankStr = "Q"; break;
	        case K: rankStr = "K"; break;
	        default: rankStr = rank.toString(); // 2-10 trả về số chuỗi
	    }

	    // Suit sẽ trả về C, D, H, S đúng như tên file ảnh
	    String suitStr = suit.toString();

	    // Trả về đường dẫn file chuẩn theo thư mục resources/cards
	    return "/cards/" + rankStr + "-" + suitStr + ".png";
	}
}
