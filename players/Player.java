package players;

import cards.Card;
import cards.Deck;

import java.util.ArrayList;
import java.util.List;

public class Player implements Playable {
    private final List<Hand> hands;
    private int currentHandIndex = 0;
    private static final int MAX_HANDS = 2;
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
    public void reset(Deck deck) {
        for (Hand hand : hands) {
            for (int i = 0; i < hand.numCards(); i++) {
                deck.discard(hand.getCard(i));
            }
            hand.reset();
        }
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
        currentHandIndex = 0;
        return false;
    }

    public Hand getCurrentHand() {
        return hands.get(currentHandIndex);
    }

    public boolean canSplit() {
        Hand hand = getCurrentHand();
        return hands.size() < MAX_HANDS && hand.numCards() == 2 &&
                hand.getCard(0).equalValue(hand.getCard(1)) &&
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

    public float getTokens() {
        return tokens;
    }

    public float getAvailableTokens() {
        return tokens - hands.size() * bet - sidebets;
    }

    public void setTokens(float payout) {
        this.tokens += payout;
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

    public int getCurrentHandIndex() {
        return currentHandIndex;
    }

    public List<Hand> getHands() {
        return hands;
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

    public List<Card> getHoleCards() {
        return hands.get(0).getCards();
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
