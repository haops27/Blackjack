package blackjack.logic;

import blackjack.actor.Dealer;
import blackjack.actor.Player;
import blackjack.bet.BettingSystem;
import blackjack.deck.Deck;
import blackjack.deck.Rank;
import java.util.ArrayList;
import java.util.List;

public class Game {
	private final Deck deck = new Deck(6);
	private final Dealer dealer = new Dealer();
    private final BettingSystem bettingSystem = new BettingSystem();
    private final List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;
    
    public void initializePlayers(List<String> names) {
        players.clear();
        for (String s : names) players.add(new Player(s));
    }
    
    public Dealer getDealer() {
		return dealer;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void resetRound() {
        deck.reshuffle();
        dealer.reset(deck);
        players.forEach(player -> player.reset(deck));
        currentPlayerIndex = 0;
    }
    
    public void dealInitialCards() {
        players.forEach(player -> {
        	player.addCard(deck.getCard());
            player.addCard(deck.getCard());
        });
        dealer.addCard(deck.getCard());
        dealer.addCard(deck.getCard());
    }
    
    public void placeBets(Player player, float mainBet, float sideBet, BettingSystem.SideBetRule rule) {
    	bettingSystem.placeBet(mainBet, player);
    	
    	if (sideBet > 0) {
    		bettingSystem.placeSideBet(sideBet, player, rule);
    	}
    }
    
    public boolean dealerHasAce() {
    	return dealer.showFirstCard().getRank() == Rank.A;
    }
    
    public void placeInsurance(Player player) {
    	if (dealerHasAce()) {
    		bettingSystem.placeInsurance(player);
    	}
    }
    
    public boolean insurancePayout() {
    	return bettingSystem.insurancePayout(dealer);
    }
    
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public boolean nextPlayer() {
        if (currentPlayerIndex < players.size()-1) {
            currentPlayerIndex++;
            return true;
        }
        return false;
    }
    
    public void split(Player player) {
        player.split(deck);
    }

    public void doubleDown(Player player) {
        player.doubleDown(deck);
    }
    
    public void dealerPlay() {
        dealer.hit(deck);
    }
    
    public void evaluateSidebetPayouts(Player player) {
        bettingSystem.calculateSidebetPayout(player, dealer);
    }
    
    public void evaluateResults() {
        players.forEach(player -> {
        	bettingSystem.calculatePayout(player, dealer);
        });
    }
    
}
