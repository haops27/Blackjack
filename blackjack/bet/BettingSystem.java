package blackjack.bet;

import blackjack.actor.*;
import blackjack.deck.*;
import java.util.*;

public class BettingSystem {
    public enum SideBetRule {
        PERFECT_PAIR, TWENTYONE_PLUS_THREE;
    }
    
    private final Map<Player, SideBetRule> sb = new HashMap<>();
    private final List<Player> insuredPlayer = new ArrayList<>();
    
    public boolean placeBet(float num, Player player) {
        if (num > 0 && num <= player.getAvailableTokens()) {
            player.setBet(num);
            System.out.println("Player " + player.getName() + " placed bet successfully");
            return true;
        }
        System.out.println("Player " + player.getName() + " does not have enough tokens");
        return false;
    }
    
    public void calculatePayout(Player player, Dealer dealer) {
        int dealerSum = dealer.getSum();
        float payout;
        
        for (Hand hand : player) {
            int handSum = hand.getSum();

            boolean win = handSum <= 21 && (dealerSum > 21 || handSum > dealerSum);
            boolean push = handSum <= 21 && handSum == dealerSum;

            if (win) {
                if (hand.isBlackjack()) {
                    payout = player.getBet() * 1.5f;
                    System.out.println("PLAYER " + player.getName() + " HAS BLACKJACK");
                } else payout = player.getBet();
                System.out.println("Player " + player.getName() + " won $" + payout);
            } else if (push) {
                payout = 0;
                System.out.println("Player " + player.getName() + " won nothing");
            } else {
                payout = -player.getBet();
                System.out.println("Player " + player.getName() + " lost $" + (-payout));
            }
            player.setTokens(payout);
            player.setBet(0);
        }
    }

    public void placeInsurance(Player player) {
        insuredPlayer.add(player);
    }

    public boolean insurancePayout(Dealer dealer) {
        if (dealer.isBlackjack()) {
            System.out.println("DEALER HAS BLACKJACK!");

            for (Player player : insuredPlayer) {
                player.setTokens(player.getBet());
                System.out.println("Player " + player.getName() + " insured successfully");
            }
            return true;
        } else {
            System.out.println("Nobody's home");
            for (Player player : insuredPlayer) {
                player.setTokens(-player.getBet()/2);
                System.out.println("Player " + player.getName() + " lost $" + player.getBet()/2);
            }
            return false;
        }
    }

    public boolean placeSideBet(float num, Player player, SideBetRule sidebet) {
        if (num > 0 && num <= player.getAvailableTokens()) {
            sb.put(player, sidebet);
            player.setSidebets(num);
            System.out.println("Player " + player.getName() + " placed sidebet successfully");
            return true;
        }
        System.out.println("Player " + player.getName() + " does not have enough tokens");
        return false;
    }
    
    public void calculateSidebetPayout(Player player, Dealer dealer) {
        float multiplier = switch (sb.get(player)) {
            case PERFECT_PAIR -> evalPerfectPair(player.getCurrentHand());
            case TWENTYONE_PLUS_THREE -> eval21Plus3(player.getCurrentHand(), dealer.showFirstCard());
            default -> 0;
        };

        float payout;

        if (multiplier == 0) {
            payout = -player.getSidebets();
            System.out.println("Player " + player.getName() + " lost $" + (-payout) + " in sidebets");
        } else {
            payout = player.getSidebets() * multiplier;
            System.out.println("Player " + player.getName() + " won $" + payout + " in sidebets");
        }
        player.setTokens(payout);
        player.setSidebets(0);
    }
    
    private float evalPerfectPair(Hand hand) {
        if (hand.numCards() < 2) return 0f;

        Card c1 = hand.getCard(0);
        Card c2 = hand.getCard(1);

        if (c1.equals(c2)) return 25f;
        else if (c1.equalRank(c2)) {
            if (c1.equalColor(c2)) return 12f;
            else return 6f;
        }
        return 0f;
    }
    
    private float eval21Plus3(Hand hand, Card dealerFirstCard) {
        if (hand.numCards() < 2) return 0f;

        List<Card> c = new ArrayList<>();
        c.add(hand.getCard(0));
        c.add(hand.getCard(1));
        c.add(dealerFirstCard);

        boolean flush = evalFlush(c);
        boolean straight = evalStraight(c);
        boolean threeOfKind = eval3(c);

        if (threeOfKind) return flush ? 100f : 30f;
        if (flush && straight) return 40f;
        if (flush) return 5f;
        if (straight) return 10f;

        return 0f;
    }
    
    private boolean evalFlush(List<Card> c) {
        return c.get(0).equalSuit(c.get(1)) && c.get(0).equalSuit(c.get(2));
    }
    
    private boolean evalStraight(List<Card> c) {
        c.sort(Comparator.comparingInt(card -> card.getRank().getIndexValue()));
        int c0 = c.get(0).getRank().getIndexValue();
        int c1 = c.get(1).getRank().getIndexValue();
        int c2 = c.get(2).getRank().getIndexValue();
        return c1 == c0 + 1 && c2 == c1 + 1;
    }
    
    private boolean eval3(List<Card> c) {
        return c.get(0).equalRank(c.get(1)) && c.get(0).equalRank(c.get(2));
    }
}
