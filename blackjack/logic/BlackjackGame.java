package blackjack.logic;

import java.util.*;

import blackjack.actors.*;
import blackjack.bet.*;
import blackjack.deck.*;

public class BlackjackGame {
    private Scanner scanner = new Scanner(System.in);
    private final Deck deck = new Deck(6);
    private Dealer dealer = new Dealer();
    private BettingSystem bettingSystem = new BettingSystem();
    private List<Player> players = new ArrayList<>();

    public void start() {
        initializePlayers();

        while (true) {
            resetRound();
            placeBets();
            dealInitialCards();
            showHands();
            playPlayerTurns();
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
                int sideType = scanner.nextInt();
                SideBetRule rule = (sideType == 1) ? SideBetRule.PERFECT_PAIR : SideBetRule.TWENTYONE_PLUS_THREE;
                bettingSystem.placeSideBet(sideBet, player, rule);
            }
        }
    }

    private void dealInitialCards() {
        for (Player player : players) {
            // override card example for testing split
            Card c1 = new Card(Rank.EIGHT, Suit.D);
            Card c2 = new Card(Rank.EIGHT, Suit.C);
            player.addCard(c1);
            player.addCard(c2);
        }

        for (int i = 0; i < 2; i++) {
            dealer.addCard(deck.getCard());
        }
    }

    private void showHands() {
        for (Player player : players) {
            System.out.println(player.getName() + "'s hand: " + player.getHand() + " (sum: " + player.getSum() + ")");
        }
        System.out.println("Dealer shows: " + dealer.getHand().getCards().get(0));
    }

    private void playPlayerTurns() {
        for (Player player : players) {
            System.out.println("\n" + player.getName() + "'s turn:");
            if (player.hasBlackjack()) {
                System.out.println("BLACKJACK!");
                continue;
            }

            boolean endTurn = false;
            int flag = 0;

            while (!player.isBust() && !endTurn) {
                System.out.print("Hit or Stay or Double or Split? (h/s/d/sp): ");
                String move = scanner.next();

                switch (move.toLowerCase()) {
                    case "s" -> endTurn = true;

                    case "h" -> {
                        player.addCard(deck.getCard());
                        System.out.println("Hand: " + player.getHand() + " (sum: " + player.getSum() + ")");
                    }

                    case "d" -> {
                        if (player.doubleDown(deck)) {
                            endTurn = true;
                        }
                    }

                    case "sp" -> {
                        if (player.split(deck)) {
                            // First hand
                            while (!player.isBust()) {
                                System.out.print("First hand - Hit or Stay? (h/s): ");
                                String move1 = scanner.next();
                                if (move1.equalsIgnoreCase("h")) {
                                    player.addCard(deck.getCard());
                                    System.out.println("First hand: " + player.getHand() + " (sum: " + player.getSum() + ")");
                                } else break;
                            }
                            if (player.isBust()) {
                                flag = 1;
                                System.out.println(player.getName() + " busted!");
                            }

                            // Second hand
                            while (!player.isSplitBust()) {
                                System.out.print("Second hand - Hit or Stay? (h/s): ");
                                String move2 = scanner.next();
                                if (move2.equalsIgnoreCase("h")) {
                                    player.addCardToSplitHand(deck.getCard());
                                    System.out.println("Second hand: " + player.getSplitHand() + " (sum: " + player.getSplitSum() + ")");
                                } else break;
                            }
                            if (player.isSplitBust()) {
                                System.out.println(player.getName() + " busted!");
                            }
                            endTurn = true;
                        }
                    }

                    default -> System.out.println("Invalid input.");
                }

                if (player.isBust() && flag == 0) {
                    System.out.println(player.getName() + " busted!");
                }
            }
        }
    }

    private void playDealerTurn() {
        System.out.println("\nDealer's turn...");
        System.out.println("Dealer's hand: " + dealer.getHand() + " (sum: " + dealer.getSum() + ")");
        dealer.takeTurn(deck);
        System.out.println("Dealer's final hand: " + dealer.getHand() + " (sum: " + dealer.getSum() + ")");
    }

    private void evaluateResults() {
        int dealerSum = dealer.getSum();
        boolean dealerBust = dealer.isBust();

        for (Player player : players) {
            int playerSum = player.getSum();
            System.out.println("\n======= Result for " + player.getName() + " =======");

            boolean playerWin = !player.isBust() && (dealerBust || playerSum > dealerSum);
            boolean push = !player.isBust() && playerSum == dealerSum;
            System.out.println("Result for main hand:");
            bettingSystem.calculatePayout(player, playerWin, push);

            if (!player.getSplitHand().getCards().isEmpty()) {
                int splitSum = player.getSplitSum();
                boolean splitWin = !player.isSplitBust() && (dealerBust || splitSum > dealerSum);
                boolean splitPush = !player.isSplitBust() && splitSum == dealerSum;
                System.out.println("Result for split hand:");
                bettingSystem.calculatePayout(player, splitWin, splitPush);
            }

            System.out.println(player.getName() + "'s tokens after round: $" + player.getTokens());
        }
    }
}
