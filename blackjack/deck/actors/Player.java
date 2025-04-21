package blackjack.deck;
import java.util.ArrayList;
import java.util.Random;

public class Deck {
	private ArrayList<Card> deck;
	
	//Building and shuffling deck
	public Deck() {
		this.buildDeck();
		this.shuffleDeck();
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
		System.out.println("AFTER SHUFFLE: ");
        	System.out.println(deck);
	}
	public ArrayList<Card> getDeck() {
        return deck;
    }
}
