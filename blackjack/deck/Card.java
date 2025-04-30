package blackjack.deck;

public class Card {
	// Name of each cards in folder "cards"
	private final Rank value; // Số
	private final Suit type;  // Chất
	
	public Card(Rank value, Suit type){
		this.value = value;
		this.type = type;
	}
	
    @Override
	public String toString() {
		return value + " - " + type;
	}
	
	//get value of each cards: 2-10 are equal to themselves; J, Q, K are considered as 10; Ace is 1 or 11 for some case
	public int getValue() {
		return value.getBlackjackValue();
	}
	
	public Rank getRank() {
		return value;
	}
	
	public Suit getSuit() {
		return type;
	}
	
	public String getColor() {
		return type.getColor();
	}
	
	//check that a cards is an Ace or not
	public boolean isAce() {
		return value == Rank.A;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Card o) {
			return o.getRank() == this.value && o.getSuit() == this.type;
		}
		return false;
	}

	// getting image of each cards
	public String getImagePath() {
		return "./cards/" + toString() + ".png";
	}
}
