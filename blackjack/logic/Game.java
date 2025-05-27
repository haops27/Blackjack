package blackjack.logic;

import blackjack.actor.Dealer;
import blackjack.actor.Player;
import blackjack.bet.BettingSystem;
import blackjack.bet.BettingSystem.SideBetRule;
import blackjack.deck.Card;
import blackjack.deck.Deck;
import blackjack.deck.Rank;
import blackjack.deck.Suit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Game {
    private final Deck deck = new Deck(1);
    private final Dealer dealer = new Dealer();
    private final BettingSystem bettingSystem = new BettingSystem();
    private final List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;

    /**
     * Khởi tạo danh sách người chơi theo tên.<br>
     * Xóa danh sách cũ nếu có.
     */
    public void initializePlayers(List<String> names) {
        players.clear();
        names.forEach(name -> {
        	players.add(new Player(name));
        });
    }

    
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    
    public Dealer getDealer() {
        return dealer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Reset vòng chơi:<br>
     * - Trộn lại bộ bài nếu cần<br>
     * - Reset tay dealer và người chơi<br>
     * - Reset index người chơi hiện tại<br>
     */
    public void resetRound() {
        if (deck.reshuffle()) {
            System.out.println("Deck reshuffled due to red card.");
        }
        dealer.reset(deck);
        players.forEach(player -> {
        	player.reset(deck);
        });
        currentPlayerIndex = 0;
    }

    /**
     * Chia bài đầu cho tất cả người chơi và dealer<br>
     * Mỗi người chơi nhận 2 lá, dealer nhận 2 lá.
     */
    public void dealInitialCards() {
    	/*for (Player player : players) {
        	 Card c1 = deck.getCard();  // take top card
        	 Card c2 = deck.getCard();  // take next card

        	 // manually override to forced split cards
        	 c1 = new Card(Rank.A, Suit.H);
        	 c2 = new Card(Rank.TEN, Suit.D);

        	 // deal these to the first player
        	  player.addCard(c1);
        	  player.addCard(c2);
        	}*/

            for (Player player : players) {
                // override card example for testing split
                player.addCard(deck.getCard());
                player.addCard(deck.getCard());
            }

    	 /*Card c3 = new Card(Rank.A, Suit.H);
    	 Card c4 = new Card(Rank.EIGHT, Suit.D);
        dealer.addCard(c3);
        dealer.addCard(c4);*/
            dealer.addCard(deck.getCard());
            dealer.addCard(deck.getCard());
        
        System.out.println("Dealer cards: " + dealer.getHand().getCards());
    }

    /**
     * Người chơi đặt cược chính và cược phụ side bets<br>
     * Nếu side bet > 0 và sideBetRules không rỗng thì đặt cược phụ
     */
    public void placeBets(Player player, float mainBet, float sideBet, Set<SideBetRule> sideBetRules) {
        bettingSystem.placeBet(mainBet, player);

        if (sideBet > 0 && sideBetRules != null && !sideBetRules.isEmpty()) {
            bettingSystem.placeSideBet(sideBet, player, sideBetRules);
        }
    }
    
    public String getCurrentPlayerName() {
        return getCurrentPlayer().getName();
    }

    /** wraps your dealerHasAce() check + “only after the initial deal” if you want */
    public boolean canTakeInsurance() {
        // only if the dealer actually has an up-card
        return !dealer.getHand().getCards().isEmpty() 
            && dealerHasAce();
    }

    /**
     * Kiểm tra dealer có lá bài đầu là Ace không
     */
    public boolean dealerHasAce() {
        return dealer.showFirstCard().getRank() == Rank.A;
    }

    /**
     * Người chơi đặt bảo hiểm nếu dealer có lá bài đầu là Ace
     */
    public void placeInsurance(Player player) {
        if (dealerHasAce()) {
            bettingSystem.placeInsurance(player);
        }
    }

    /**
     * Thanh toán bảo hiểm sau khi dealer lật bài
     */
    public boolean insurancePayout() {
        return bettingSystem.insurancePayout(dealer);
    }

    /**
     * Lấy người chơi hiện tại (theo currentPlayerIndex)
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Chuyển sang người chơi kế tiếp. <br>
     * Trả về true nếu còn người chơi tiếp theo, <br>
     * false nếu đã hết lượt người chơi. <br>
     */
    public boolean nextPlayer() {
    	if (players.get(currentPlayerIndex).nextHand()) return true;
    	else {
    		if (currentPlayerIndex < players.size() - 1) {
    			currentPlayerIndex++;
                return true;
    		}
    		currentPlayerIndex = 0;
    	}
    	return false;
    }

    /**
     * Thực hiện split cho người chơi
     */
    public void playerSplit() {
        getCurrentPlayer().split(deck);
    }
    
    public void playerHit() {
        getCurrentPlayer().addCard(deck.getCard());
    }


    /**
     * Thực hiện double down cho người chơi
     */
    public void playerDoubleDown() {
        getCurrentPlayer().doubleDown(deck);
    }
    /**
     * Dealer thực hiện lượt chơi theo luật
     */
    public void dealerPlay() {
        dealer.hit(deck);
    }

    /**
     * Tính tiền thắng thua cho cược phụ của người chơi
     */
    public void evaluateSidebetPayouts(Player player) {
        bettingSystem.calculateSidebetPayout(player, dealer);
    }
    
    public int getRemainingDeckSize() {
        return deck.size();
    }

    /**
     * Lấy bộ bài
     */
    public Deck getDeck() {
        return deck;
    }
    
   
    /**
     * Tính tiền thắng thua cho cược chính của tất cả người chơi
     */
    public void evaluateResults() {
        for (Player player : players) {
            bettingSystem.calculatePayout(player, dealer);
        }
    }
}