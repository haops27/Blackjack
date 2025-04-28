package blackjack.gamerule;

import blackjack.actors.*;

public class BettingSystem {
	
	//Place main bets
	public boolean placeBet(int num, Player player) {
		if (num > 0 && num < player.getTokens()) {
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
		}
		if (push) {
			payout = 0;
			System.out.println("Player won nothing");
		} else {
			payout = -player.getBet();
			System.out.println("Player lost $" + -payout);
		}
		player.setTokens(player.getTokens() + payout);
	}
	
	
}
