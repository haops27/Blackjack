package blackjack.userinterface;

import blackjack.deck.Card;
import blackjack.actor.Dealer;
import blackjack.actor.Player;
import blackjack.bet.BettingSystem.SideBetRule;
import blackjack.logic.Game;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainController {

    @FXML private ImageView tableImageView;
    @FXML private ImageView chipImageView;

    @FXML private HBox dealerCardsHBox;
    @FXML private HBox playerCardsHBox;

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

    @FXML
    public void initialize() {
        // Load images
        try (InputStream t = getClass().getResourceAsStream("/images/table.png")) {
            tableImageView.setImage(new Image(t));
        } catch (Exception ex) {
            appendStatus("Cannot load table image.");
        }

        try (InputStream c = getClass().getResourceAsStream("/images/chips.png")) {
            chipImageView.setImage(new Image(c));
            chipImageView.setVisible(false);
        } catch (Exception ex) {
            appendStatus("Cannot load chip image.");
        }

        // Initialize players
        game.initializePlayers(List.of("Player1"));
        resetForNewRound();
    }

    private void setActionButtonsDisabled(boolean disable) {
        hitButton.setDisable(disable);
        standButton.setDisable(disable);
        doubleButton.setDisable(disable);
        splitButton.setDisable(disable);
        insuranceButton.setDisable(disable);
    }

    private void updateUI() {
        Player p = game.getCurrentPlayer();
        Dealer d = game.getDealer();

        // Dealer cards display
        if (!dealerRevealed) {
            showDealerInitialCards();
        } else {
            showDealerAllCards();
        }

        // Show sums and tokens
        dealerSumLabel.setText("Dealer Sum: " + (dealerRevealed ? d.getSum() : "?"));
        playerSumLabel.setText("Player Sum: " + p.getCurrentHand().getSum());
        tokensLabel.setText(String.format("Tokens: %.2f", p.getTokens()));

        // Side bet label style and text
        sideBetLabel.setText(String.format("Side Bet: %.2f", p.getSidebets()));
        sideBetLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        // Show player's cards
        showCards(playerCardsHBox, p.getCurrentHand().getCards());

        // Update status message (only append when new action)
        appendStatus("Playing hand " + (p.getCurrentHandIndex() + 1) + " of " + p.getHands().size());
    }

    private void showDealerInitialCards() {
        dealerCardsHBox.getChildren().clear();
        List<Card> cards = game.getDealer().getHand().getCards();
        if (cards.size() >= 1) addCardToHBox(dealerCardsHBox, cards.get(0).getImagePath());
        addCardToHBox(dealerCardsHBox, "/images/back.png"); // Face down card
    }

    private void showDealerAllCards() {
        dealerCardsHBox.getChildren().clear();
        showCards(dealerCardsHBox, game.getDealer().getHand().getCards());
    }

    private void showCards(HBox box, List<Card> cards) {
        box.getChildren().clear();
        for (Card c : cards) addCardToHBox(box, c.getImagePath());
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
            if (perfectPairCheckBox.isSelected()) rules.add(SideBetRule.PERFECT_PAIR);
            if (twentyOnePlusThreeCheckBox.isSelected()) rules.add(SideBetRule.TWENTYONE_PLUS_THREE);

            game.resetRound();
            dealerRevealed = false;
            chipImageView.setVisible(true);

            game.placeBets(p, main, side, rules);
            appendStatus(String.format("Bet placed: Main %.1f, Side %.1f", main, side));

            game.dealInitialCards();

            updateUI();

            setActionButtonsDisabled(false);
            placeBetButton.setDisable(true);
            mainBetField.setDisable(true);
            sideBetField.setDisable(true);
            perfectPairCheckBox.setDisable(true);
            twentyOnePlusThreeCheckBox.setDisable(true);

            // Enable insurance button only if dealer shows Ace
            insuranceButton.setDisable(!game.dealerHasAce());

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

        if (p.isBust()) {
            appendStatus("Player busted!");
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

    @FXML
    private void onSplit() {
        Player p = game.getCurrentPlayer();
        if (p.canSplit()) {
            p.split(game.getDeck());
            appendStatus("Player splits hand.");
            updateUI();
        } else {
            appendStatus("Cannot split.");
        }
    }

    @FXML
    private void onInsurance() {
        Player p = game.getCurrentPlayer();
        game.placeInsurance(p);
        appendStatus("Insurance taken.");
        insuranceButton.setDisable(true);

        // Immediately deduct insurance cost from player's tokens
        float insuranceCost = p.getBet() / 2f;
        p.setTokens(-insuranceCost);
        appendStatus(String.format("Insurance cost $%.2f deducted.", insuranceCost));
    }

    private void endPlayerTurn() {
        if (game.nextPlayer()) {
            appendStatus("Next player's turn.");
            updateUI();
        } else {
            // Dealer turn
            dealerRevealed = true;
            appendStatus("Dealer's turn.");
            updateUI();

            game.dealerPlay();
            updateUI();

            // Evaluate side bets for all players
            for (Player player : game.getPlayers()) {
                game.evaluateSidebetPayouts(player);
            }

            // Evaluate main bets results
            game.evaluateResults();

            // Show result messages for all players
            for (Player player : game.getPlayers()) {
                float tokens = player.getTokens();
                String result = tokens > 2500 ? "won!" : tokens < 2500 ? "lost!" : "broke even.";
                appendStatus(player.getName() + " " + result);
            }

            setActionButtonsDisabled(true);
            newRoundButton.setDisable(false);
        }
    }

    @FXML
    private void onNewRound() {
        resetForNewRound();
    }

    private void resetForNewRound() {
        dealerCardsHBox.getChildren().clear();
        playerCardsHBox.getChildren().clear();

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

        setActionButtonsDisabled(true);
        newRoundButton.setDisable(true);

        insuranceButton.setDisable(true);

        statusLabel.setText("New round — place your bets.");
    }

    private void appendStatus(String line) {
        String currentText = statusLabel.getText();
        if (!currentText.isEmpty()) {
            statusLabel.setText(currentText + "\n" + line);
        } else {
            statusLabel.setText(line);
        }
    }
}
