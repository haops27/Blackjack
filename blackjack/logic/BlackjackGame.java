package blackjack.logic;

import blackjack.actor.*;
import blackjack.bet.BettingSystem;
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
            if (!placeInsurances()) {
                for (Player player : players) {
                    playPlayerTurn(player);
                }
                playDealerTurn();
            }
            evaluateResults();

            System.out.print("\nPlay another round? (y/n): ");
            if (!scanner.next().equalsIgnoreCase("y")) break;
        }

        System.out.println("Thanks for playing!");
    }
    //DONE
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
    //DONE
    private void resetRound() {
        dealer.reset(deck);
        for (Player player : players) {
            player.reset(deck);
        }
        if (deck.reshuffle()) {
            System.out.println("Red card reached — reshuffling before next round.");
        }
        System.out.println("\n==== NEW GAME ====");
    }
    //DONE
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
    //DONE
    private void dealInitialCards() {
        for (Player player : players) {
            player.addCard(deck.getCard());
            player.addCard(deck.getCard());
        }
        for (int i = 0; i < 2; i++) {
            dealer.addCard(deck.getCard());
        }

        for (Player player : players) {
            System.out.println(player);
        }
        System.out.println("Dealer: " + dealer.showFirstCard() + ", HIDDEN");
    }
    //DONE
    private boolean placeInsurances() {
        if (dealer.showFirstCard().getRank() == Rank.A) {
            System.out.println("Dealer has an Ace, press y to place insurance, otherwise press n");
            for (Player player : players) {
                System.out.print(player.getName() + ": ");
                String move = scanner.next();
                if (move.equals("y")) {
                    bettingSystem.placeInsurance(player);
                }
            }
            
            if (bettingSystem.insurancePayout(dealer)) {
                for (Player player : players) {
                    if (player.getSidebets() > 0) bettingSystem.calculateSidebetPayout(player, dealer);
                }
                return true;
            }
        }
        return false;
    }

    private void playPlayerTurn(Player player) {
        System.out.println("\n" + player.getName() + "'s turn:");

        if (player.getSidebets() > 0) {
            bettingSystem.calculateSidebetPayout(player, dealer);
        }

        do {
            System.out.println("Hand: " + player.getCurrentHand());
            if (player.isBlackjack()) {
                System.out.println("BLACKJACK!");
            } else {
                outer: while (player.getSum() < 21) {
                    System.out.println("Hit or Stand or Double or Split? (h/s/d/sp)");
                    String move = scanner.next();

                    switch (move.toLowerCase()) {
                        case "s" -> {
                            break outer;
                        }

                        case "h" -> {
                            player.addCard(deck.getCard());
                            System.out.println("Hand: " + player.getCurrentHand());
                            if (player.isBust()) {
                                System.out.println("Busted!");
                            }
                        }

                        case "d" -> {
                            if (player.doubleDown(deck)) {
                                System.out.println("Hand: " + player.getCurrentHand());
                                break outer;
                            } else System.out.println("You can't double down anymore");
                        }

                        case "sp" -> {
                            if (!player.split(deck)) System.out.println("You can't split here");
                            else System.out.println(player);
                            System.out.println("Hand: " + player.getCurrentHand());
                        }

                        default -> System.out.println("Invalid input.");
                    }
                    
                }
            }

        } while (player.nextHand());
    }
    
    //DONE
    private void playDealerTurn() {
        System.out.println("\nDealer's turn...");
        System.out.println(dealer);
        dealer.hit(deck);
        System.out.println(dealer);
    }
    //DONE
    private void evaluateResults() {
        for (Player player : players) {
            System.out.println("\n======= Result for " + player.getName() + " =======");
            bettingSystem.calculatePayout(player, dealer);
            System.out.println(player.getName() + "'s tokens after round: $" + player.getTokens());
        }
    }
}
