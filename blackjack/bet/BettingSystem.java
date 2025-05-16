package blackjack.bet;

import blackjack.actors.*;
import blackjack.deck.Card;
import java.util.*;

public class BettingSystem {
	Map<Player, SideBetRule> sb = new HashMap<>();

	//Place main bets
	public boolean placeBet(float num, Player player) {
		if (num > 0 && num <= player.getTokens()) {
			player.setBet(num);
			System.out.println("Player " + player.getName() + " placed bet successfully");
			return true;
		}
		System.out.println("Player " + player.getName() + " does not have enough tokens");
		return false;
	}
	
	//Calculate main payouts at the end of the round
	public void calculatePayout(Player player, Dealer dealer) {
		int dealerSum = dealer.getSum();
		float payout;
		
		for (Hand hand : player) {
			int handSum = hand.getSum();

            boolean win = handSum <= 21 && (dealerSum > 21 || handSum > dealerSum);
            boolean push = handSum <= 21 && handSum == dealerSum;
            if (win) {
    			if (hand.isBlackjack()) {
    				payout = player.getBet()*1.5f;
    				System.out.println("PLAYER " + player.getName() + " HAS BLACKJACK");
    			} else payout = player.getBet();
    			System.out.println("Player " + player.getName() + " won $" + payout);
    		} else if (push) {
    			payout = 0;
    			System.out.println("Player " + player.getName() + " won nothing");
    		} else {
    			payout = -player.getBet();
    			System.out.println("Player " + player.getName() + " lost $" + -payout);
    		}
    		player.setTokens(player.getTokens() + payout);
		}
	}
	
	//Place side bets
	public boolean placeSideBet(float num, Player player, SideBetRule sidebet) {
		if (num > 0 && num <= player.getTokens()) {
			sb.putIfAbsent(player, sidebet);
			player.setSidebets(num);
			System.out.println("Player " + player.getName() + " placed sidebet successfully");
			return true;
		}
		System.out.println("Player " + player.getName() + " does not have enough tokens");
		return false;
	}
	
	public void calculateSidebetPayout(Player player, Card dealerFirstCard) {
		float multiplier = 0;
		switch (sb.get(player)) {
		case PERFECT_PAIR -> multiplier = evalPerfectPair(player.getHand());
		case TWENTYONE_PLUS_THREE -> multiplier = eval21Plus3(player.getHand(), dealerFirstCard);
		}
		
		float payout;
		
		if (multiplier == 0) {
			payout = -player.getSidebets();
			System.out.println("Player " + player.getName() + " lost $" + -payout + " in sidebets");
		} else {
			payout = player.getSidebets()*multiplier;
			System.out.println("Player " + player.getName() + " won $" + payout + " in sidebets");
		}
		player.setTokens(player.getTokens() + payout);
		
	}
	
	private float evalPerfectPair(Hand hand) {
		float multiplier = 0f;
		Card c1 = hand.getCard(0);
		Card c2 = hand.getCard(1);
		if (c1.equals(c2)) multiplier = 25f;
		else if (c1.equalRank(c2)) {
			if (c1.equalColor(c2)) multiplier = 12f;
			else multiplier = 6f;
		}
		return multiplier;
	}
	
	private float eval21Plus3(Hand hand, Card dealerFirstCard) {
		float multiplier = 0f;
		List<Card> c = new ArrayList<>();
		c.add(hand.getCard(0));
		c.add(hand.getCard(1));
		c.add(dealerFirstCard);
		if (evalFlush(c)) {
			if (evalStraight(c)) multiplier = 40f;
			else multiplier = 5f;
		}
		if (evalStraight(c)) {
			if (evalFlush(c)) multiplier = 40f;
			else multiplier = 10f;
		}
		if (eval3(c)) {
			if (evalFlush(c)) multiplier = 100f;
			else multiplier = 30f;
		}
		return multiplier;
	}
	
	private boolean evalFlush(List<Card> c) {
		return c.get(0).equalSuit(c.get(1)) && c.get(0).equalSuit(c.get(2));
	}
	
	private boolean evalStraight(List<Card> c) {
		c.sort(Comparator.comparingInt(cmp -> cmp.getRank().getIndexValue()));
		int c0 = c.get(0).getRank().getIndexValue();
		int c1 = c.get(1).getRank().getIndexValue();
		int c2 = c.get(2).getRank().getIndexValue();
		return c1 == c0 + 1 && c2 == c1 + 1;
	}
	
	private boolean eval3(List<Card> c) {
		return c.get(0).equalRank(c.get(1)) && c.get(0).equalRank(c.get(2));
	}
	
}
