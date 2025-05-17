package blackjack.logic;

import blackjack.actors.*;
import blackjack.bet.*;
import blackjack.deck.*;
import java.util.*;

public class BlackjackGame {
    private final Scanner scanner = new Scanner(System.in);
    private final Deck deck = new Deck(6);
    private final Dealer dealer = new Dealer();
    private final BettingSystem bettingSystem = new BettingSystem();
    private final List<Player> players = new ArrayList<>();

    public void start() {
        initializePlayers();

        while (true) {
            resetRound();
            placeBets();
            dealInitialCards();
            showHands();
            for (Player player : players) playPlayerTurn(player);
            playDealerTurn();
            evaluateResults();

            System.out.print("\nPlay another round? (y/n): ");
            if (!scanner.next().equalsIgnoreCase("y")) break;
        }

        System.out.println("Thanks for playing!");
    }

    private void initializePlayers() {
        System.out.println("==== WELCOME TO BLACKJACK ====");
        System.out.print("Enter the number of players: ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine();
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter name for Player " + i + ": ");
            players.add(new Player(scanner.nextLine()));
        }
    }

    private void resetRound() {
        dealer.reset();
        for (Player player : players) {
            player.reset();
        }
        System.out.println("\n==== NEW GAME ====");
    }

    private void placeBets() {
        for (Player player : players) {
            System.out.println(player.getName() + "'s tokens: $" + player.getTokens());
            float mainBet;
            do {
                System.out.print(player.getName() + ", enter main bet: ");
                mainBet = scanner.nextFloat();
            } while (!bettingSystem.placeBet(mainBet, player));

            System.out.print(player.getName() + ", enter side bet (0 to skip): ");
            float sideBet = scanner.nextFloat();
            if (sideBet > 0) {
                System.out.print("Choose side bet type (1 = PERFECT_PAIR, 2 = TWENTYONE_PLUS_THREE): ");
                BettingSystem.SideBetRule rule = (scanner.nextInt() == 1) ? BettingSystem.SideBetRule.PERFECT_PAIR : BettingSystem.SideBetRule.TWENTYONE_PLUS_THREE;
                bettingSystem.placeSideBet(sideBet, player, rule);
            }
        }
    }

    private void dealInitialCards() {
        for (Player player : players) {
            // override card example for testing split
            player.addCard(deck.getCard());
            player.addCard(deck.getCard());
        }

        for (int i = 0; i < 2; i++) {
            dealer.addCard(deck.getCard());
        }
        
    }

    private void showHands() {
        for (Player player : players) {
            System.out.println(player);
        }
        System.out.println("Dealer: " + dealer.showFirstCard() + ", HIDDEN");
    }

    private void playPlayerTurn(Player player) {
    	System.out.println("\n" + player.getName() + "'s turn:");

        //Calculate sidebet payouts immediately after dealing initial cards
        if (player.getSidebets() > 0){
            bettingSystem.calculateSidebetPayout(player, dealer);
        }
        
        boolean endTurn = false;
        
        while (player.hasNext()) {
        	System.out.println("Hand: " + player.getHand());
        	if (player.isBlackjack()) {
                System.out.println("BLACKJACK!");
            }
        	else while (!player.isBust() && !endTurn && player.getSum() < 21) {
                System.out.print("Hit or Stand or Double or Split? (h/s/d/sp): ");
                String move = scanner.next();

                switch (move.toLowerCase()) {
                    case "s" -> endTurn = true;

                    case "h" -> {
                        player.hit(deck);
                        System.out.println("Hand: " + player.getHand());
                    }

                    case "d" -> {
                        if (player.doubleDown(deck)) {
                            endTurn = true;
                            System.out.println("Hand: " + player.getHand());
                        } else System.out.println("You can't double down anymore");
                    }

                    case "sp" -> {
                        if (!player.split(deck)) System.out.println("You can't split here");
                        else System.out.println(player);
                    }

                    default -> System.out.println("Invalid input.");
                }

                if (player.isBust()) {
                	System.out.println("Busted!");
                }
        	}
        	player.nextHand();
        	endTurn = false;
        }

    }

    private void playDealerTurn() {
        System.out.println("\nDealer's turn...");
        System.out.println(dealer);
        dealer.hit(deck);
        System.out.println(dealer);
    }

    private void evaluateResults() {
        for (Player player : players) {
        	System.out.println("\n======= Result for " + player.getName() + " =======");
        	bettingSystem.calculatePayout(player, dealer);

            System.out.println(player.getName() + "'s tokens after round: $" + player.getTokens());
        }
    }
}
