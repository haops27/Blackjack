package black_jack;
import java.util.ArrayList;
import java.util.Random;

class Card {
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
	
	//check that a cards is an Ace or not
	public boolean isAce() {
		return value.equals("A");
	}
	
	// getting image of each cards
	public String getImagePath() {
		return "./cards/" + toString() + ".png";
	}
}

public class Deck {
	private ArrayList<Card> deck;
	
	//Building deck
	public Deck() {
		buildDeck();
	}
	
	private void buildDeck() {
		deck = new ArrayList<Card>(); // new deck variable to store all of the cards
		String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        	String[] types = {"C", "D", "H", "S"};
        
        	// adding each cards into deck
	        for (int i = 0; i < types.length; i++) {
	            for (int j = 0; j < values.length; j++) {
	                Card card = new Card(values[j], types[i]);
	                deck.add(card);
	            }
	        }
	        
	        //Test if the code run ok
	        System.out.println("BUILD DECK:");
	        System.out.println(deck);
	}
	
	//Shuffle deck
	public void shuffleDeck() {
		Random random = new Random();
		for (int i = deck.size()-1; i > 0; i--) {
		        int j = random.nextInt(i+1);
			Card currCard = deck.get(i);
			Card randomCard = deck.get(j);
			deck.set(i, randomCard);
			deck.set(j, currCard);
        	}
		
		//Test if the code run ok
		System.out.println("AFTER SHUFFLE");
        	System.out.println(deck);
	}
}
