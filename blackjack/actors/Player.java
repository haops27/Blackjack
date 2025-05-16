package blackjack.actors;

import blackjack.deck.*;
import java.util.*;

public class Player extends Actor implements Iterable<Hand> {
    private float bet;
    private float sidebets;
    private float tokens = 2500f;
    private String name;
    private final List<Hand> hands;
    private int handIndex = 0;

    public Player(String name) {
        super();
        this.name = name;
        this.hands = new ArrayList<>();
        hands.add(hand);
    }
    
    public Player(String name, float tokens) {
        super();
        this.name = name;
        this.hands = new ArrayList<>();
        hands.add(hand);
        this.tokens = tokens;
    }

    @Override
	public void addCard(Card card) {
		hands.get(handIndex).addCard(card);
	}
    
    @Override
    public void hit(Deck deck) {
        addCard(deck.getCard());
    }

	public boolean canSplit() {
        return hands.get(handIndex).numCards() == 2 &&
               hands.get(handIndex).getCard(0).getRank() == hands.get(handIndex).getCard(1).getRank() &&
               tokens >= bet*(hands.size() + 1);
    }

    public boolean split(Deck deck) {
        if (!canSplit()) return false;
        Hand splitHand = new Hand();

        Card card0 = hands.get(handIndex).getCard(0);
        Card card1 = hands.get(handIndex).getCard(1);

        hands.get(handIndex).reset();
        hands.get(handIndex).addCard(card0);
        splitHand.addCard(card1);

        addCard(deck.getCard());
        splitHand.addCard(deck.getCard());
        hands.add(splitHand);

        System.out.println("Player has split the hand.");
        return true;
    }

    public boolean canDouble() {
        return handIndex == 0 && hand.numCards() == 2 && tokens >= bet*2;
    }

    public boolean doubleDown(Deck deck) {
        if (!canDouble()) return false;

        bet *= 2;
        addCard(deck.getCard());
        return true;
    }

    public float getTokens() {
        return tokens;
    }

    public void setTokens(float tokens) {
        this.tokens = tokens;
    }

    public void setBet(float bet) {
        this.bet = bet;
    }

    public float getBet() {
        return bet;
    }

    public float getSidebets() {
        return sidebets;
    }

    public void setSidebets(float sidebets) {
        this.sidebets = sidebets;
    }
    
    public boolean hasNext() {
    	return handIndex < hands.size();
    }
    
    public void nextHand() {
    	if (hasNext()) handIndex++;
    }
    
	public Hand getHand() {
		return hands.get(handIndex);
	}
    
    public int numHands() {
    	return hands.size();
    }

	@Override
	public boolean isBust() {
		return hands.get(handIndex).isBust();
	}

	@Override
    public boolean isBlackjack() {
        return hands.get(handIndex).isBlackjack();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player p) {
            return name.equals(p.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public void reset() {
        super.reset();
        hands.clear();
        hands.add(hand);
        handIndex = 0;
        this.bet = 0;
        this.sidebets = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Player ");
        sb.append(name);
        sb.append(": \n\t");
        int i = 0;
        for (Hand h : hands) {
        	sb.append("Hand ");
        	sb.append(++i);
        	sb.append(": ");
        	sb.append(h);
        	sb.append("\n\t");
        }
        sb.append("Tokens: $");
        sb.append(tokens);
        return sb.toString();
    }

	@Override
	public Iterator<Hand> iterator() {
		return hands.iterator();
	}

}
