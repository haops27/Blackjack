package blackjack.bet;

import blackjack.actors.Player;
import blackjack.deck.Card;
import java.util.*;

public class BettingSystem {
	Map<Player, SideBetRule> sb = new HashMap<>();

	//Place main bets
	public boolean placeBet(float num, Player player) {
		if (num > 0 && num <= player.getTokens()) {
			player.setBet(num);
			System.out.println("Player placed bet successfully");
			return true;
		}
		System.out.println("Player does not have enough tokens");
		return false;
	}
	
	//Calculate main payouts at the end of the round
	public void calculatePayout(Player player, boolean playerWin, boolean push) {
		float payout;
		if (playerWin) {
			if (player.hasBlackjack()) {
				payout = player.getBet()*1.5f;
				System.out.println("PLAYER HAS BLACKJACK");
			} else payout = player.getBet();
			System.out.println("Player won $" + payout);
		} else if (push) {
			payout = 0;
			System.out.println("Player won nothing");
		} else {
			payout = -player.getBet();
			System.out.println("Player lost $" + -payout);
		}
		player.setTokens(player.getTokens() + payout);
	}
	
	//Place side bets
	public boolean placeSideBet(float num, Player player, SideBetRule sidebet) {
		if (num > 0 && num <= player.getTokens()) {
			sb.putIfAbsent(player, sidebet);
			player.setSidebets(num);
			System.out.println("Player placed sidebet successfully");
			return true;
		}
		System.out.println("Player does not have enough tokens");
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
			System.out.println("Player lost $" + -payout + " in sidebets");
		} else {
			payout = player.getSidebets()*multiplier;
			System.out.println("Player won $" + payout + " in sidebets");
		}
		player.setTokens(player.getTokens() + payout);
		
	}
	
	private float evalPerfectPair(List<Card> cards) {
		float multiplier = 0f;
		Card c1 = cards.get(0);
		Card c2 = cards.get(1);
		if (c1.equals(c2)) multiplier = 25f;
		else if (c1.getRank() == c2.getRank()) {
			if (c1.getColor().equals(c2.getColor())) multiplier = 12f;
			else multiplier = 6f;
		}
		return multiplier;
	}
	
	private float eval21Plus3(List<Card> cards, Card dealerFirstCard) {
		float multiplier = 0f;
		List<Card> c = new ArrayList<>();
		c.add(cards.get(0));
		c.add(cards.get(1));
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
		return c.get(0).getSuit() == c.get(1).getSuit() && c.get(0).getSuit() == c.get(2).getSuit();
	}
	
	private boolean evalStraight(List<Card> c) {
		c.sort(Comparator.comparingInt(cmp -> cmp.getRank().getIndexValue()));
		int c0 = c.get(0).getRank().getIndexValue();
		int c1 = c.get(1).getRank().getIndexValue();
		int c2 = c.get(2).getRank().getIndexValue();
		return c1 == c0 + 1 && c2 == c1 + 1;
	}
	
	private boolean eval3(List<Card> c) {
		return c.get(0).getRank() == c.get(1).getRank() && c.get(0).getRank() == c.get(2).getRank();
	}
	
}
