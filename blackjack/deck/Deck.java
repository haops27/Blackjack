package blackjack.deck;
import java.util.*;

public class Deck {
	private List<Card> deck;
	private Rank[] values = {Rank.A, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX, Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.J, Rank.Q, Rank.K};
    private Suit[] types = {Suit.C, Suit.D, Suit.H, Suit.S};
	
	//Building and shuffling deck
	public Deck() {
		this.buildDeck();
		this.shuffleDeck();
	}
	
	//Building and shuffling n 52-card decks into 1 single deck
	public Deck(int n) {
		this.buildDeck(n);
		this.shuffleDeck();
	}
	
	private void buildDeck() {
		deck = new ArrayList<Card>(); // new deck variable to store all of the cards
        
        // adding each cards into deck
	    for (int i = 0; i < types.length; i++) {
	    	for (int j = 0; j < values.length; j++) {
	    		Card card = new Card(values[j], types[i]);
	    		deck.add(card);
	    	}
	    }
	    
	    System.out.print("DECK BUILT: ");
	    System.out.println(deck);
	}
	
	private void buildDeck(int n) {
		deck = new ArrayList<Card>(); // new deck variable to store all of the cards
        
        // adding each cards into deck
        for (int k = 0; k < n; k++) {
        	for (int i = 0; i < types.length; i++) {
    	    	for (int j = 0; j < values.length; j++) {
    	    		Card card = new Card(values[j], types[i]);
    	    		deck.add(card);
    	    	}
    	    }
        }
	    
	    System.out.print("DECK BUILT: ");
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
		
		System.out.print("DECK SHUFFLED: ");
	    System.out.println(deck);
	}

	public Card getCard() {
		return deck.removeLast();
	}
	
}
