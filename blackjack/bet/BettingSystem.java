package blackjack.bet;

import cards.Card;
import players.Dealer;
import players.Hand;
import players.Player;

import java.util.*;

public class BettingSystem {
    public enum SideBetRule {
        PERFECT_PAIR, TWENTYONE_PLUS_THREE;
    }

    // Lưu side bets theo người chơi
    private final Map<Player, Set<SideBetRule>> sb = new HashMap<>();
    private final List<Player> insuredPlayers = new ArrayList<>();

    public boolean placeBet(float amount, Player player) {
        if (amount > 0 && amount <= player.getAvailableTokens()) {
            player.setBet(amount);
            System.out.println("Player " + player.getName() + " placed main bet successfully: " + amount);
            return true;
        }
        System.out.println("Player " + player.getName() + " does not have enough tokens for main bet");
        return false;
    }

    public boolean placeSideBet(float amount, Player player, Set<SideBetRule> sidebets) {
        if (amount > 0 && amount <= player.getAvailableTokens()) {
            sb.put(player, sidebets);
            player.setSidebets(amount);
            System.out.println("Player " + player.getName() + " placed side bets: " + sidebets + " with amount: " + amount);
            return true;
        }
        System.out.println("Player " + player.getName() + " does not have enough tokens for side bet");
        return false;
    }

    public void calculatePayout(Player player, Dealer dealer) {
        int dealerSum = dealer.getSum();
        float payout;
        
        do {
        	Hand hand = player.getCurrentHand();
        	int handSum = hand.getSum();

            boolean win = handSum <= 21 && (dealerSum > 21 || handSum > dealerSum);
            boolean push = handSum <= 21 && handSum == dealerSum;

            if (win) {
                if (hand.isBlackjack()) {
                    payout = player.getBet() * 1.5f;
                    System.out.println("PLAYER " + player.getName() + " HAS BLACKJACK");
                    hand.setStatus(Hand.Status.BLACKJACK);
                } else {
                    payout = player.getBet();
                    hand.setStatus(Hand.Status.WON);
                }
                System.out.println("Player " + player.getName() + " won $" + payout);
            } else if (push) {
                payout = 0;
                System.out.println("Player " + player.getName() + " pushes (tie)");
                hand.setStatus(Hand.Status.PUSH);
            } else {
                payout = -player.getBet();
                System.out.println("Player " + player.getName() + " lost $" + (-payout));
                hand.setStatus(Hand.Status.LOST);
            }
            player.setTokens(payout);
        } while (player.nextHand());
        player.setBet(0);
    }

    public void calculateSidebetPayout(Player player, Dealer dealer) {
        Set<SideBetRule> playerSideBets = sb.getOrDefault(player, Collections.emptySet());
        float payout = 0f;

        for (SideBetRule rule : playerSideBets) {
            float multiplier = 0f;
            switch (rule) {
                case PERFECT_PAIR -> multiplier = evalPerfectPair(player.getCurrentHand());
                case TWENTYONE_PLUS_THREE -> multiplier = eval21Plus3(player.getCurrentHand(), dealer.showFirstCard());
            }
            if (multiplier > 0) {
                float winAmount = player.getSidebets() * multiplier;
                payout += winAmount;
                System.out.println("Player " + player.getName() + " won $" + winAmount + " on side bet " + rule);
            } else {
                payout -= player.getSidebets();
                System.out.println("Player " + player.getName() + " lost $" + player.getSidebets() + " on side bet " + rule);
            }
        }

        player.setTokens(payout);
        player.setSidebets(0);
        sb.remove(player);
    }

    public void placeInsurance(Player player) {
        if (!insuredPlayers.contains(player)) {
            insuredPlayers.add(player);
            System.out.println("Player " + player.getName() + " placed insurance.");
        }
    }

    public boolean insurancePayout(Dealer dealer) {
        if (dealer.isBlackjack()) {
            System.out.println("DEALER HAS BLACKJACK!");

            for (Player player : insuredPlayers) {
                player.setTokens(player.getBet()); // trả lại tiền bảo hiểm (1:1)
                System.out.println("Player " + player.getName() + " won insurance.");
            }
            insuredPlayers.clear();
            return true;
        } else {
            System.out.println("DEALER DOES NOT HAVE BLACKJACK.");

            for (Player player : insuredPlayers) {
                float loss = player.getBet() / 2f;
                player.setTokens(-loss);  // mất nửa tiền bảo hiểm
                System.out.println("Player " + player.getName() + " lost insurance $" + loss);
            }
            insuredPlayers.clear();
            return false;
        }
    }

    private float evalPerfectPair(Hand hand) {
        if (hand.numCards() < 2) return 0f;

        Card c1 = hand.getCard(0);
        Card c2 = hand.getCard(1);

        if (c1.equals(c2)) return 25f; // perfect pair chính xác (giống hệt lá bài)
        else if (c1.equalRank(c2)) {
            if (c1.equalColor(c2)) return 12f; // cùng chất và cùng số
            else return 6f; // chỉ cùng số
        }
        return 0f;
    }

    private float eval21Plus3(Hand hand, Card dealerFirstCard) {
        if (hand.numCards() < 2) return 0f;

        List<Card> cards = new ArrayList<>();
        cards.add(hand.getCard(0));
        cards.add(hand.getCard(1));
        cards.add(dealerFirstCard);

        boolean flush = evalFlush(cards);
        boolean straight = evalStraight(cards);
        boolean threeOfKind = eval3(cards);

        if (threeOfKind) return flush ? 100f : 30f;
        if (flush && straight) return 40f;
        if (flush) return 5f;
        if (straight) return 10f;

        return 0f;
    }

    private boolean evalFlush(List<Card> cards) {
        return cards.get(0).equalSuit(cards.get(1)) && cards.get(0).equalSuit(cards.get(2));
    }

    private boolean evalStraight(List<Card> cards) {
        cards.sort(Comparator.comparingInt(card -> card.getRank().getIndexValue()));
        int c0 = cards.get(0).getRank().getIndexValue();
        int c1 = cards.get(1).getRank().getIndexValue();
        int c2 = cards.get(2).getRank().getIndexValue();
        return c1 == c0 + 1 && c2 == c1 + 1;
    }

    private boolean eval3(List<Card> cards) {
        return cards.get(0).equalRank(cards.get(1)) && cards.get(0).equalRank(cards.get(2));
    }
}
