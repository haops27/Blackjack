package blackjack.userinterface;

import blackjack.deck.Card;
import blackjack.actor.Dealer;
import blackjack.actor.Hand;
import blackjack.actor.Player;
import blackjack.bet.BettingSystem.SideBetRule;
import blackjack.logic.Game;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainController {

    @FXML private ImageView tableImageView;
    @FXML private ImageView chipImageView;

    @FXML private HBox dealerCardsHBox;
    @FXML private HBox hand1HBox;
    @FXML private HBox hand2HBox;

    @FXML private Label dealerSumLabel;
    @FXML private Label playerSumLabel;
    @FXML private Label tokensLabel;
    @FXML private Label sideBetLabel;

    @FXML private TextField mainBetField;
    @FXML private TextField sideBetField;
    @FXML private CheckBox perfectPairCheckBox;
    @FXML private CheckBox twentyOnePlusThreeCheckBox;

    @FXML private Button placeBetButton;
    @FXML private Button hitButton;
    @FXML private Button standButton;
    @FXML private Button doubleButton;
    @FXML private Button splitButton;
    @FXML private Button newRoundButton;
    @FXML private Button insuranceButton;

    @FXML private Label statusLabel;

    private final Game game = new Game();
    private boolean dealerRevealed = false;

    private final String TABLE_IMAGE_PATH = "/blackjack/resources/images/table.png";
    private final String CHIP_IMAGE_PATH = "/blackjack/resources/images/chips.png";
    private final String BACK_IMAGE_PATH = "/blackjack/resources/images/back.png";

    @FXML
    public void initialize() {
        try (InputStream t = getClass().getResourceAsStream(TABLE_IMAGE_PATH)) {
            tableImageView.setImage(new Image(t));
        } catch (Exception ex) {
            appendStatus("Cannot load table image.");
        }

        try (InputStream c = getClass().getResourceAsStream(CHIP_IMAGE_PATH)) {
            chipImageView.setImage(new Image(c));
            chipImageView.setVisible(false);
        } catch (Exception ex) {
            appendStatus("Cannot load chip image.");
        }

        game.initializePlayers(List.of("Player1"));
        resetForNewRound();
    }

    private void setActionButtons(boolean disable) {
        hitButton.setDisable(disable);
        standButton.setDisable(disable);
        doubleButton.setDisable(disable);
        splitButton.setDisable(disable);
        insuranceButton.setDisable(disable);
    }

    private void setActionButtons() {
        Player p = game.getCurrentPlayer();
        hitButton.setDisable(p.getSum() >= 21);
        standButton.setDisable(false);
        doubleButton.setDisable(!p.canDouble());
        splitButton.setDisable(!p.canSplit());
        insuranceButton.setDisable(!game.dealerHasAce());
    }

    private void updateUI() {
        Player p = game.getCurrentPlayer();
        Dealer d = game.getDealer();

        showDealerCards();

        dealerSumLabel.setText("Dealer Sum: " + (dealerRevealed ? d.getSum() : "?"));
        playerSumLabel.setText("Player Sum: " + p.getCurrentHand().getSum());
        tokensLabel.setText(String.format("Tokens: %.2f", p.getTokens()));
        sideBetLabel.setText(String.format("Side Bet: %.2f", p.getSidebets()));
        sideBetLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        List<Hand> hands = p.getHands();
        if (hands.size() == 1) {
            showCards(hand1HBox, hands.get(0).getCards());
            hand2HBox.setVisible(false);
        } else {
            showCards(hand1HBox, hands.get(0).getCards());
            showCards(hand2HBox, hands.get(1).getCards());
            hand2HBox.setVisible(true);
        }

        appendStatus("Playing hand " + (p.getCurrentHandIndex() + 1) + " of " + hands.size());
        setActionButtons();
    }

    private void showDealerCards() {
        dealerCardsHBox.getChildren().clear();

        List<Card> dealerCards = game.getDealer().getHand().getCards();

        if (dealerRevealed) {
            // Show all dealer cards normally
            showCards(dealerCardsHBox, dealerCards);
        } else {
            if (!dealerCards.isEmpty()) {
                // Show the first dealer card
                addCardToHBox(dealerCardsHBox, dealerCards.get(0).getImagePath());

                // Show the back of the second card (if exists)
                if (dealerCards.size() > 1) {
                    addCardToHBox(dealerCardsHBox, BACK_IMAGE_PATH);
                }
            }
        }
    }


    private void showCards(HBox box, List<Card> cards) {
        box.getChildren().clear();
        for (Card c : cards) {
            addCardToHBox(box, c.getImagePath());
        }
    }

    private void addCardToHBox(HBox box, String resource) {
        try (InputStream in = getClass().getResourceAsStream(resource)) {
            ImageView iv = new ImageView(new Image(in));
            iv.setFitWidth(70);
            iv.setFitHeight(100);
            box.getChildren().add(iv);
        } catch (Exception ex) {
            appendStatus("Error loading card image: " + resource);
        }
    }

    @FXML
    private void onPlaceBet() {
        try {
            float main = Float.parseFloat(mainBetField.getText());
            float side = sideBetField.getText().isBlank() ? 0f : Float.parseFloat(sideBetField.getText());

            Player p = game.getCurrentPlayer();
            if (p.getAvailableTokens() < main + side) {
                appendStatus("Insufficient tokens for bet!");
                return;
            }

            Set<SideBetRule> rules = new HashSet<>();
            if (side > 0) {
                if (perfectPairCheckBox.isSelected()) rules.add(SideBetRule.PERFECT_PAIR);
                if (twentyOnePlusThreeCheckBox.isSelected()) rules.add(SideBetRule.TWENTYONE_PLUS_THREE);
            }

            game.resetRound();
            dealerRevealed = false;
            chipImageView.setVisible(true);

            game.placeBets(p, main, side, rules);
            appendStatus(String.format("Bet placed: Main %.1f, Side %.1f", main, side));
            game.dealInitialCards();

            updateUI();

            placeBetButton.setDisable(true);
            mainBetField.setDisable(true);
            sideBetField.setDisable(true);
            perfectPairCheckBox.setDisable(true);
            twentyOnePlusThreeCheckBox.setDisable(true);

            if (game.getCurrentPlayer().isBlackjack()) {
                endPlayerTurn();
            }

        } catch (NumberFormatException ex) {
            appendStatus("Invalid bet input.");
        }
    }

    @FXML
    private void onHit() {
        Player p = game.getCurrentPlayer();
        p.addCard(game.getDeck().getCard());
        appendStatus("Player hits.");
        updateUI();

        if (p.getSum() >= 21) {
            if (p.isBust()) appendStatus("Player busted!");
            endPlayerTurn();
        }
    }

    @FXML
    private void onStand() {
        appendStatus("Player stands.");
        endPlayerTurn();
    }

    @FXML
    private void onDouble() {
        Player p = game.getCurrentPlayer();
        if (p.canDouble()) {
            p.doubleDown(game.getDeck());
            appendStatus("Player doubles down.");
            updateUI();
            endPlayerTurn();
        } else {
            appendStatus("Cannot double down.");
        }
    }
    
    private void animateSplit() {
        if (hand1HBox.getChildren().size() >= 2) {
            Node card1 = hand1HBox.getChildren().get(0);
            Node card2 = hand1HBox.getChildren().get(1);

            TranslateTransition tt1 = new TranslateTransition(Duration.millis(400), card1);
            tt1.setByX(-60);

            TranslateTransition tt2 = new TranslateTransition(Duration.millis(400), card2);
            tt2.setByX(60);

            ParallelTransition pt = new ParallelTransition(tt1, tt2);
            pt.setOnFinished(e -> {
                updateUI();
                appendStatus("Cards added to split hands.");
            });
            pt.play();
        } else {
            updateUI();
        }
    }

    @FXML
    private void onSplit() {
        Player p = game.getCurrentPlayer();
        if (!p.canSplit()) {
            appendStatus("Cannot split.");
            return;
        }

        p.split(game.getDeck());
        appendStatus("Player splits hand.");

        animateSplit();
    }

    @FXML
    private void onInsurance() {
        Player p = game.getCurrentPlayer();
        game.placeInsurance(p);
        appendStatus("Insurance taken.");
        insuranceButton.setDisable(true);

        float insuranceCost = p.getBet() / 2f;
        p.setTokens(-insuranceCost);
        appendStatus(String.format("Insurance cost $%.2f deducted.", insuranceCost));
    }

    private void endPlayerTurn() {
        if (game.nextPlayer()) {
            appendStatus("Next hand or player.");
            updateUI();
        } else {
            dealerRevealed = true;
            appendStatus("Dealer's turn.");
            updateUI();

            game.dealerPlay();
            updateUI();

            for (Player player : game.getPlayers()) {
                game.evaluateSidebetPayouts(player);
            }

            game.evaluateResults();

            for (Player player : game.getPlayers()) {
                for (Hand hand : player) {
                    appendStatus(player.getName() + " " + hand.getStatus());
                }
            }

            setActionButtons(true);
            newRoundButton.setDisable(false);
        }
    }

    @FXML
    private void onNewRound() {
        resetForNewRound();
    }

    private void resetForNewRound() {
        dealerCardsHBox.getChildren().clear();
        hand1HBox.getChildren().clear();
        hand2HBox.getChildren().clear();
        hand2HBox.setVisible(false);

        dealerSumLabel.setText("Dealer Sum: 0");
        playerSumLabel.setText("Player Sum: 0");
        sideBetLabel.setText("Side Bet: 0.00");

        mainBetField.clear();
        sideBetField.clear();
        tokensLabel.setText(String.format("Tokens: %.2f", game.getCurrentPlayer().getTokens()));

        placeBetButton.setDisable(false);
        mainBetField.setDisable(false);
        sideBetField.setDisable(false);
        perfectPairCheckBox.setDisable(false);
        twentyOnePlusThreeCheckBox.setDisable(false);

        chipImageView.setVisible(false);
        setActionButtons(true);
        newRoundButton.setDisable(true);

        statusLabel.setText("New round — place your bets.");
    }

    private void appendStatus(String line) {
        String currentText = statusLabel.getText();
        if (currentText.length() <= 100) {
            statusLabel.setText(currentText + "\n" + line);
        } else {
            statusLabel.setText(line);
        }
    }
}
