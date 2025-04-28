package blackjack.deck;

public class Card {
	// Name of each cards in folder "cards"
	private String value; // Số
	private String type;  // Chất
	
	Card(String value, String type){
		this.value = value;
		this.type = type;
	}
	
	public String toString() {
		return value + " - " + type;
	}
	
	//get value of each cards: 2-10 are equal to themselves; J, Q, K are considered as 10; Ace is 1 or 11 for some case
	public int getValue() {
		if ("JQK".contains(value)) {
			return 10;
		}
		if (this.isAce()) {
			return 11;
		}
		return Integer.parseInt(value);
	}
	
	public String getType() {
		return type;
	}
	
	//check that a cards is an Ace or not
	public boolean isAce() {
		return value.equals("A");
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Card) {
			Card o = (Card) obj;
			return o.getValue() == this.getValue() && o.getType().equals(this.type);
		}
		return false;
	}

	// getting image of each cards
	public String getImagePath() {
		return "./cards/" + toString() + ".png";
	}
}
