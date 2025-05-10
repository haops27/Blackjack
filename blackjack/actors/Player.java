package blackjack.actors;

import java.util.*;
import blackjack.deck.*;

public class Player extends Actor {
    private float bet;
    private float sidebets;
    private float tokens = 2500f;
    private String name;
    private Hand splitHand = new Hand();

    public Player(String name) {
        super();
        this.name = name;
    }

    public boolean canSplit() {
        return hand.getCards().size() == 2 &&
               hand.getCards().get(0).getRank() == hand.getCards().get(1).getRank() &&
               tokens >= bet;
    }

    public boolean split(Deck deck) {
        if (!canSplit()) return false;

        Card cardToMove = hand.getCards().remove(1);
        hand.reset();
        hand.addCard(cardToMove);
        splitHand.reset();
        splitHand.addCard(cardToMove);

        addCard(deck.getCard());
        splitHand.addCard(deck.getCard());

        System.out.println("Player has split the hand.");
        System.out.println("First hand: " + this.hand);
        System.out.println("Second hand: " + this.splitHand);
        return true;
    }

    public Hand getSplitHand() {
        return splitHand;
    }

    public int getSplitSum() {
        return splitHand.getSum();
    }

    public boolean isSplitBust() {
        return splitHand.isBust();
    }

    public boolean canDouble() {
        return hand.getCards().size() == 2 && tokens >= bet;
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

    public boolean hasBlackjack() {
        return hand.isBlackjack();
    }

    public String getName() {
        return name;
    }

    @Override
    public void takeTurn(Deck deck) {
        addCard(deck.getCard());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player p) {
            return Objects.equals(p.name, name);
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
        splitHand.reset();
        this.bet = 0;
        this.sidebets = 0;
    }

    @Override
    public String toString() {
        return "Player " + name + ": " + hand;
    }
    public void addCardToSplitHand(Card card) {
        splitHand.addCard(card);
    }

}
