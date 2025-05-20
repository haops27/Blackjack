package blackjack.actor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import blackjack.deck.*;

public class Player implements Playable, Iterable<Hand> {
    private final List<Hand> hands;
    private int currentHandIndex = 0;
    private static int MAX_HANDS = 4;
    private float bet;
    private float sidebets;
    private float tokens = 2500f;
    private final String name;

    public Player(String name) {
        this.name = name;
        this.hands = new ArrayList<>();
        hands.add(new Hand());
    }

    public Player(String name, float tokens) {
        this.name = name;
        this.tokens = tokens;
        this.hands = new ArrayList<>();
        hands.add(new Hand());
    }

    @Override
    public void addCard(Card card) {
        hands.get(currentHandIndex).addCard(card);
    }

    @Override
    public int getSum() {
        return hands.get(currentHandIndex).getSum();
    }

    @Override
    public boolean isBust() {
        return hands.get(currentHandIndex).isBust();
    }

    @Override
    public boolean isBlackjack() {
        return hands.get(currentHandIndex).isBlackjack();
    }

    @Override
    public void hit(Deck deck) {
        addCard(deck.getCard());
    }

    @Override
    public void reset() {
        hands.clear();
        hands.add(new Hand());
        currentHandIndex = 0;
        bet = 0;
        sidebets = 0;
    }

    public boolean nextHand() {
        if (currentHandIndex < hands.size() - 1) {
            currentHandIndex++;
            return true;
        }
        return false;
    }

    public Hand getCurrentHand() {
        return hands.get(currentHandIndex);
    }

    public boolean canSplit() {
        Hand hand = getCurrentHand();
        return hands.size() <= MAX_HANDS && hand.numCards() == 2 &&
               hand.getCard(0).getRank() == hand.getCard(1).getRank() &&
               tokens >= bet * (hands.size() + 1);
    }

    public boolean split(Deck deck) {
        if (!canSplit()) return false;

        Hand currentHand = getCurrentHand();
        Card card0 = currentHand.getCard(0);
        Card card1 = currentHand.getCard(1);

        currentHand.reset();
        currentHand.addCard(card0);

        Hand splitHand = new Hand();
        splitHand.addCard(card1);

        // Rút thêm bài cho từng tay
        currentHand.addCard(deck.getCard());
        splitHand.addCard(deck.getCard());

        hands.add(splitHand);

        System.out.println("Player " + name + " has split the hand.");
        return true;
    }

    public boolean canDouble() {
        return hands.size() == 1 && getCurrentHand().numCards() == 2 && tokens >= bet * 2;
    }

    public boolean doubleDown(Deck deck) {
        if (!canDouble()) return false;

        bet *= 2;
        addCard(deck.getCard());
        return true;
    }

    // Getters and setters
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
    public Iterator<Hand> iterator() {
        return hands.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Player ").append(name).append(":\n");
        int i = 1;
        for (Hand hand : hands) {
            sb.append("\tHand ").append(i++).append(": ").append(hand).append("\n");
        }
        sb.append("Tokens: $").append(tokens);
        return sb.toString();
    }
}
