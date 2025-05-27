package blackjack.userinterface;

import blackjack.actor.Hand;
import blackjack.actor.Player;
import blackjack.bet.BettingSystem.SideBetRule;
import blackjack.deck.Card;
import blackjack.logic.Game;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainController {
    @FXML private Pane deckPilePane;
    @FXML private Pane discardPilePane;
    @FXML private ImageView tableImageView, chipImageView;
    @FXML private Label dealerSumLabel, tokensLabel, sideBetLabel, statusLabel;
    @FXML private TextField mainBetField, sideBetField;
    @FXML private CheckBox perfectPairCheckBox, twentyOnePlusThreeCheckBox;
    @FXML private Button placeBetButton, hitButton, standButton,
                     doubleButton, splitButton, newRoundButton, insuranceButton;

    @FXML private HBox playersContainer;
    @FXML private Pane dealerCards;

    private final Game game = new Game();
    private boolean dealerRevealed;
    private boolean bettingPhase;
    private boolean reshuffledLastRound = false;

    private static final String BACK_IMAGE_PATH = "/blackjack/resources/images/back.png";
    private static final double CARD_W  = 70;
    private static final double CARD_H  = 100;
    private static final double CARD_OFF = 20;

    @FXML
    public void initialize() {
        enableBettingControls(false);
        enableGameButtons(true);
        newRoundButton.setDisable(true);
        chipImageView.setVisible(false);
        statusLabel.setText("Welcome — set your bets when ready.");

        sideBetField.textProperty().addListener((obs, oldText, newText) -> {
            boolean allow = false;
            if (!newText.isBlank()) {
                try { allow = Float.parseFloat(newText) > 0; }
                catch (NumberFormatException ignored) {}
            }
            perfectPairCheckBox.setDisable(!allow);
            twentyOnePlusThreeCheckBox.setDisable(!allow);
        });
    }

    public void initializePlayers(int count) {
        List<String> names = IntStream.rangeClosed(1, count)
                .mapToObj(i -> "Player" + i)
                .collect(Collectors.toList());
        game.initializePlayers(names);
        resetForNewRound();
    }

    private void resetForNewRound() {
        // STEP 1: Discard every in-play card (including dealer hole)
        List<Card> allDiscarded = new ArrayList<>();
        for (Card c : game.getDealer().getHand().getCards()) {
            game.getDeck().discard(c);
            allDiscarded.add(c);
        }
        for (Player pl : game.getPlayers()) {
            for (Hand h : pl.getHands()) {
                for (Card c : h.getCards()) {
                    game.getDeck().discard(c);
                    allDiscarded.add(c);
                }
            }
        }

        // STEP 2: Reset logic (calls deck.reshuffle() if wildcard was drawn in last turn)
        game.resetRound();

        // STEP 3: Update discard-pile UI
        discardPilePane.getChildren().clear();
        if (!reshuffledLastRound && !allDiscarded.isEmpty()) {
            updateDiscardPile(allDiscarded);
        }
        reshuffledLastRound = false;

        // STEP 4: Clear table & enable betting
        bettingPhase   = true;
        dealerRevealed = false;
        dealerCards.getChildren().clear();
        playersContainer.getChildren().clear();

        mainBetField.clear();
        sideBetField.clear();
        perfectPairCheckBox.setSelected(false);
        twentyOnePlusThreeCheckBox.setSelected(false);
        perfectPairCheckBox.setDisable(true);
        twentyOnePlusThreeCheckBox.setDisable(true);

        enableBettingControls(true);
        enableGameButtons(true);
        newRoundButton.setDisable(true);
        chipImageView.setVisible(false);

        updateUI();
        appendStatus("New round — place your bets.");
    }

    @FXML private void onPlaceBet() {
        try {
            float main = parseFloat(mainBetField.getText());
            float side = sideBetField.getText().isBlank() ? 0f : parseFloat(sideBetField.getText());
            if (side > 0 && !perfectPairCheckBox.isSelected() && !twentyOnePlusThreeCheckBox.isSelected()) {
                appendStatus("Select a side-bet rule before placing a side-bet.");
                return;
            }
            Player p = game.getCurrentPlayer();
            if (p.getAvailableTokens() < main + side) {
                appendStatus("Insufficient tokens!");
                return;
            }
            Set<SideBetRule> rules = new HashSet<>();
            if (side > 0) {
                if (perfectPairCheckBox.isSelected()) rules.add(SideBetRule.PERFECT_PAIR);
                if (twentyOnePlusThreeCheckBox.isSelected()) rules.add(SideBetRule.TWENTYONE_PLUS_THREE);
            }
            game.placeBets(p, main, side, rules);
            appendStatus(p.getName() + " bet: Main=" + main + ", Side=" + side);

            mainBetField.clear();
            sideBetField.clear();
            perfectPairCheckBox.setSelected(false);
            twentyOnePlusThreeCheckBox.setSelected(false);

            if (bettingPhase && game.nextPlayer()) {
                appendStatus("Now " + game.getCurrentPlayerName() + ", place your bet.");
                updateUI();
            } else {
                bettingPhase = false;
                game.dealInitialCards();
                appendStatus("Dealing initial cards…");
                enableBettingControls(false);

                Player p1 = game.getCurrentPlayer();
                if (p1.getCurrentHand().getSum() == 21 && p1.getCurrentHand().numCards() == 2) {
                    appendStatus(p1.getName() + " has Blackjack!");
                    endTurn();
                } else {
                    enableGameButtons(false);
                    updateUI();
                }
            }
        } catch (NumberFormatException ex) {
            appendStatus("Invalid bet input.");
        }
    }

    @FXML private void onHit() {
        String name = game.getCurrentPlayerName();
        perform(() -> game.playerHit(), name + " hits.");
    }

    @FXML private void onStand() {
        String name = game.getCurrentPlayerName();
        perform(this::endTurn, name + " stands.");
    }

    @FXML private void onDouble() {
        String name = game.getCurrentPlayerName();
        performConditional(
            () -> game.playerDoubleDown(),
            name + " doubles down.",
            name + " cannot double down.",
            true
        );
    }

    @FXML private void onSplit() {
        String name = game.getCurrentPlayerName();
        performConditional(
            () -> game.playerSplit(),
            name + " splits.",
            name + " cannot split.",
            false
        );
    }

    @FXML private void onInsurance() {
        String name = game.getCurrentPlayerName();
        if (game.canTakeInsurance()) {
            game.placeInsurance(game.getCurrentPlayer());
            appendStatus(name + " takes insurance.");
        } else {
            appendStatus("Insurance not allowed — dealer’s up-card is not an Ace.");
        }
        insuranceButton.setDisable(true);
    }

    @FXML private void onNewRound() {
        resetForNewRound();
    }

    // ——————————————————————————————————————————
    // Core helpers
    // ——————————————————————————————————————————

    private void perform(Runnable action, String msg) {
        action.run();
        checkForWildAndReshuffle();
        appendStatus(msg);
        updateUI();
        if (game.getCurrentPlayer().getSum() >= 21) endTurn();
    }

    private void performConditional(Runnable action, String ok, String fail, boolean end) {
        try {
            action.run();
            checkForWildAndReshuffle();
            appendStatus(ok);
            updateUI();
            if (end) endTurn();
        } catch (Exception e) {
            appendStatus(fail);
            setButtonsState();
        }
    }

    /** Checks if the last draw was the wildcard, reshuffles immediately if so. */
    private void checkForWildAndReshuffle() {
        if (game.getDeck().reshuffle()) {
            reshuffledLastRound = true;
            appendStatus("🃏 Wild card reached! Deck reshuffled.");
            discardPilePane.getChildren().clear();
        }
    }

    private void endTurn() {
        while (game.nextPlayer()) {
            Player current = game.getCurrentPlayer();
            Hand hand = current.getCurrentHand();
            if (hand.getSum() == 21 && hand.numCards() == 2) {
                appendStatus(current.getName() + " has Blackjack!");
            } else {
                appendStatus("Next turn.");
                updateUI();
                return;
            }
        }

        dealerRevealed = true;
        appendStatus("Dealer's turn.");
        updateUI();

        game.dealerPlay();
        updateUI();

        game.getPlayers().forEach(p -> game.evaluateSidebetPayouts(p));
        game.evaluateResults();
        game.getPlayers().forEach(p ->
            appendStatus(p.getName() + " " + p.getCurrentHand().getStatus())
        );

        // final dealer-phase wildcard check
        reshuffledLastRound = game.getDeck().reshuffle();
        if (reshuffledLastRound) {
            appendStatus("🃏 Wild card reached! Deck reshuffled.");
        }

        enableGameButtons(false);
        newRoundButton.setDisable(false);
        updateUI();
    }

    // ——————————————————————————————————————————
    // UI update methods (only show one dealer card until reveal)
    // ——————————————————————————————————————————

    private void updateUI() {
        showDealer();
        showAllPlayers();
        updateLabels();
        setButtonsState();
        updateDeckPile();
    }

    private void showDealer() {
        dealerCards.getChildren().clear();
        List<Card> cards = game.getDealer().getHand().getCards();
        int count = dealerRevealed ? cards.size() : 1;
        double width = CARD_W + (count - 1) * CARD_OFF;
        dealerCards.setPrefWidth(width);

        if (!dealerRevealed) {
            if (!cards.isEmpty()) {
                dealerCards.getChildren().add(
                    makeCardView(cards.get(0).getImagePath(), 0)
                );
            }
            dealerSumLabel.setText("?");
        } else {
            for (int i = 0; i < cards.size(); i++) {
                dealerCards.getChildren().add(
                    makeCardView(cards.get(i).getImagePath(), i)
                );
            }
            dealerSumLabel.setText(String.valueOf(game.getDealer().getSum()));
        }
        dealerSumLabel.setVisible(!cards.isEmpty());
    }

    private void updateDeckPile() {
        deckPilePane.getChildren().clear();
        int remaining = game.getRemainingDeckSize();
        int toDraw   = Math.min(remaining, 10);
        for (int i = 0; i < toDraw; i++) {
            ImageView back = makeCardView(BACK_IMAGE_PATH, i);
            back.setLayoutX(i * 2);
            back.setLayoutY(i * 2);
            back.setFitWidth(60);
            back.setFitHeight(90);
            deckPilePane.getChildren().add(back);
        }
        Label count = new Label(String.valueOf(remaining));
        count.setStyle("-fx-text-fill:white; -fx-font-size:14px; -fx-font-weight:bold;");
        count.setLayoutX(deckPilePane.getPrefWidth()/2 - 10);
        count.setLayoutY(deckPilePane.getPrefHeight()/2 - 10);
        deckPilePane.getChildren().add(count);
    }

    private void updateDiscardPile(List<Card> allDiscarded) {
        discardPilePane.getChildren().clear();
        int size  = allDiscarded.size();
        int start = Math.max(0, size - 10);
        for (int i = start; i < size; i++) {
            ImageView back = makeCardView(BACK_IMAGE_PATH, i - start);
            back.setFitWidth(60);
            back.setFitHeight(90);
            back.setLayoutX((i - start) * 2);
            back.setLayoutY((i - start) * 2);
            discardPilePane.getChildren().add(back);
        }
        Label count = new Label(String.valueOf(size));
        count.setStyle("-fx-text-fill:white; -fx-font-size:14px; -fx-font-weight:bold;");
        count.setLayoutX(10);
        count.setLayoutY(10);
        discardPilePane.getChildren().add(count);
    }

    private void showAllPlayers() {
        playersContainer.getChildren().clear();
        List<Player> all = game.getPlayers();
        int current = game.getCurrentPlayerIndex();
        for (int pi = all.size() - 1; pi >= 0; pi--) {
            Player p = all.get(pi);
            VBox box = new VBox(5);
            box.setAlignment(Pos.CENTER);
            box.getChildren().add(
                new Label(p.getName() + "  —  Tokens: " + String.format("%.2f", p.getTokens()))
            );
            HBox hands = new HBox(10);
            hands.setAlignment(Pos.CENTER);

            for (int hi = 0; hi < p.getHands().size(); hi++) {
                Hand h = p.getHands().get(hi);
                int cardCount = h.getCards().size();
                double w = CARD_W + Math.max(0, cardCount - 1) * CARD_OFF;
                Pane pane = new Pane();
                pane.setPrefSize(w, CARD_H + 60);

                for (int ci = 0; ci < cardCount; ci++) {
                    pane.getChildren().add(
                        makeCardView(h.getCards().get(ci).getImagePath(), ci)
                    );
                }
                if (cardCount > 0) {
                    Label handSum = new Label(String.valueOf(h.getSum()));
                    handSum.setStyle(
                      "-fx-background-color: yellow;"
                    + "-fx-background-radius: 15px;"
                    + "-fx-text-fill: black;"
                    + "-fx-font-weight: bold;"
                    );
                    handSum.setAlignment(Pos.CENTER);
                    handSum.setPrefSize(30, 30);
                    handSum.setLayoutX((w - 30) / 2);
                    handSum.setLayoutY(CARD_H + 10);
                    pane.getChildren().add(handSum);
                }
                if (pi == current && hi == p.getCurrentHandIndex()) {
                    double underlineY = CARD_H + 50;
                    Line underline = new Line(0, underlineY, w, underlineY);
                    underline.setStrokeWidth(4);
                    underline.setStroke(Color.GOLD);
                    underline.setEffect(new Glow(0.8));
                    pane.getChildren().add(underline);
                }
                hands.getChildren().add(pane);
            }
            box.getChildren().add(hands);
            playersContainer.getChildren().add(box);
        }
    }

    private void updateLabels() {
        Player p = game.getCurrentPlayer();
        tokensLabel.setText(String.format("Tokens: %.2f", p.getTokens()));
        sideBetLabel.setText(String.format("Side Bet: %.2f", p.getSidebets()));
    }

    private void setButtonsState() {
        if (!newRoundButton.isDisable()) {
            hitButton.setDisable(true);
            standButton.setDisable(true);
            doubleButton.setDisable(true);
            splitButton.setDisable(true);
            insuranceButton.setDisable(true);
            return;
        }
        Player p = game.getCurrentPlayer();
        boolean dealt = !p.getCurrentHand().getCards().isEmpty();
        hitButton.setDisable(!dealt || p.getCurrentHand().getSum() >= 21);
        standButton.setDisable(!dealt);
        doubleButton.setDisable(!p.canDouble());
        splitButton.setDisable(!p.canSplit());
        boolean available = !bettingPhase
                         && !game.getDealer().getHand().getCards().isEmpty()
                         && game.dealerHasAce();
        insuranceButton.setDisable(!available);
    }

    private void enableBettingControls(boolean e) {
        placeBetButton.setDisable(!e);
        mainBetField.setDisable(!e);
        sideBetField.setDisable(!e);
    }

    private void enableGameButtons(boolean e) {
        hitButton.setDisable(e);
        standButton.setDisable(e);
        doubleButton.setDisable(e);
        splitButton.setDisable(e);
    }

    private void appendStatus(String m) {
        String cur = statusLabel.getText();
        statusLabel.setText(cur.length() > 100 ? m : cur + "\n" + m);
    }

    private float parseFloat(String t) {
        return Float.parseFloat(t.trim());
    }

    private ImageView makeCardView(String path, int idx) {
        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(path)));
        iv.setFitWidth(CARD_W);
        iv.setFitHeight(CARD_H);
        iv.setLayoutX(idx * CARD_OFF);
        return iv;
    }
}
