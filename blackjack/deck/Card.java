package blackjack.deck;

public class Card {
	// Name of each cards in folder "cards"
	private final Rank rank; // Số
	private final Suit suit;  // Chất
	
	public Card(Rank rank, Suit suit){
		this.rank = rank;
		this.suit = suit;
	}
	
    @Override
	public String toString() {
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
	
	public String getColor() {
		return suit.getColor();
	}
	
	//check that a cards is an Ace or not
	public boolean isAce() {
		return rank == Rank.A;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Card o) {
			return o.getRank() == this.rank && o.getSuit() == this.suit;
		}
		return false;
	}

	// getting image of each cards
	public String getImagePath() {
		return "./cards/" + toString() + ".png";
	}
}
