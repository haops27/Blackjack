package blackjack.userinterface;

import blackjack.deck.Card;
import blackjack.actor.Dealer;
import blackjack.actor.Hand;
import blackjack.actor.Player;
import blackjack.bet.BettingSystem.SideBetRule;
import blackjack.logic.Game;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainController {

	@FXML private AnchorPane discardPilePane;
	@FXML private Pane deckPilePane;
	@FXML private AnchorPane overlayPane;
	
	@FXML private ImageView tableImageView;
    @FXML private ImageView chipImageView;
    @FXML private ImageView statusImage;
   

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
    
    @FXML private TextArea statusTextArea;

    private final Game game = new Game();
    private boolean dealerRevealed = false;
    private boolean insuranceTaken = true;

    private final String TABLE_IMAGE_PATH = "/blackjack/resources/images/table.png";
    private final String CHIP_IMAGE_PATH = "/blackjack/resources/images/chips.png";
    private final String BACK_IMAGE_PATH = "/blackjack/resources/images/back.png";
    private final String PUSH_IMAGE_PATH = "/blackjack/resources/images/push.png";
    private final String BUSTED_IMAGE_PATH = "/blackjack/resources/images/busted.png";
    private final String BLACKJACK_IMAGE_PATH = "/blackjack/resources/images/blackjack.png";
    

    @FXML
    public void initialize() {
        loadImage(tableImageView, TABLE_IMAGE_PATH);
        loadImage(chipImageView, CHIP_IMAGE_PATH);
        chipImageView.setVisible(false);
        createDeckPile();
        game.initializePlayers(List.of("Player1"));
        resetForNewRound();
    }
    
    private void loadImage(ImageView view, String path) {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            view.setImage(new Image(in));
        } catch (Exception ex) {
            appendStatus("Cannot load image: " + path);
        }
    }
    
    private void createDeckPile() {
        deckPilePane.getChildren().clear();
        for (int i = 0; i < 52; i++) {
            ImageView cardBack = createCardImage(BACK_IMAGE_PATH);
            cardBack.setTranslateX(i * -0.3); // Slight offset for realism
            deckPilePane.getChildren().add(cardBack);
        }
    }
    
    private ImageView createCardImage(String imagePath) {
        ImageView imageView = new ImageView();
        loadImage(imageView, imagePath);
        imageView.setFitWidth(70);
        imageView.setFitHeight(100);
        return imageView;
    }
    
    private void collectCardsWithAnimation(Runnable onFinish) {
        List<ImageView> allCards = new java.util.ArrayList<>();
        // Collect player cards
        for (Node node : hand1HBox.getChildren()) {
            if (node instanceof ImageView iv) allCards.add(iv);
        }
        if (hand2HBox.isVisible()) {
            for (Node node : hand2HBox.getChildren()) {
                if (node instanceof ImageView iv) allCards.add(iv);
            }
        }
        // Collect dealer cards
        for (Node node : dealerCardsHBox.getChildren()) {
            if (node instanceof ImageView iv) allCards.add(iv);
        }

        SequentialTransition seq = new SequentialTransition();
        for (int i = 0; i < allCards.size(); i++) {
            ImageView iv = allCards.get(i);
            Bounds bounds = iv.localToScene(iv.getBoundsInLocal());
            String imagePath = (String) iv.getUserData();
            PauseTransition delay = new PauseTransition(Duration.millis(200 * i));
            delay.setOnFinished(e -> {
                if (imagePath != null) {
                    animateCardToDiscard(imagePath, bounds);
                } else {
                    appendStatus("Missing image path for discard animation.");
                }
                // Remove original from UI
                ((Pane) iv.getParent()).getChildren().remove(iv);
            });
            seq.getChildren().add(delay);
        }

        // Run this after all animations finish
        seq.setOnFinished(e -> {
            if (onFinish != null) onFinish.run();
        });

        seq.play();
    }
    
    private void animateCardToDiscard(String imagePath, Bounds originalBounds) {
        try (InputStream in = getClass().getResourceAsStream(imagePath)) {
            ImageView card = new ImageView(new Image(in));
            card.setFitWidth(70);
            card.setFitHeight(100);
            overlayPane.getChildren().add(card);

            Bounds overlayBounds = overlayPane.localToScene(overlayPane.getBoundsInLocal());

            double startX = originalBounds.getMinX() - overlayBounds.getMinX() + originalBounds.getWidth() / 2;
            double startY = originalBounds.getMinY() - overlayBounds.getMinY() + originalBounds.getHeight() / 2;

            card.setLayoutX(startX);
            card.setLayoutY(startY);

            Bounds discardBounds = discardPilePane.localToScene(discardPilePane.getBoundsInLocal());
            double offset = discardPilePane.getChildren().size() * 0.3;  // smaller stacking offset

            double endX = discardBounds.getMinX() - overlayBounds.getMinX() + offset;
            double endY = discardBounds.getMinY() - overlayBounds.getMinY() + offset;

            // Rotate and flip to back mid-air
            RotateTransition flip = new RotateTransition(Duration.millis(400), card);
            flip.setFromAngle(0);
            flip.setToAngle(180);
            flip.setOnFinished(e -> {
                // When halfway flipped, change to back
                try (InputStream backIn = getClass().getResourceAsStream(BACK_IMAGE_PATH)) {
                    card.setImage(new Image(backIn));
                } catch (Exception ex) {
                    appendStatus("Error flipping card to back.");
                }
            });

            TranslateTransition move = new TranslateTransition(Duration.millis(400), card);
            move.setToX(endX - startX);
            move.setToY(endY - startY);

            ParallelTransition animation = new ParallelTransition(flip, move);
            animation.setOnFinished(e -> {
                overlayPane.getChildren().remove(card);
                discardPilePane.getChildren().add(card); // now face-down in discard pile
            });

            animation.play();

        } catch (Exception ex) {
            appendStatus("Error collecting card: " + imagePath);
        }
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
         int sum = p.getCurrentHand().getSum();
         boolean isBust = sum > 21;
         boolean isBlackjack = sum == 21;

         boolean playerTurnOver = isBust || isBlackjack;

         hitButton.setDisable(playerTurnOver);
         standButton.setDisable(playerTurnOver);
         doubleButton.setDisable(playerTurnOver || !p.canDouble());
         splitButton.setDisable(playerTurnOver || !p.canSplit());
         insuranceButton.setDisable(insuranceTaken);
    }

    private void updateUI() {
        Player p = game.getCurrentPlayer();
        Dealer d = game.getDealer();

        showDealerCards();

        dealerSumLabel.setText("Dealer Sum: " + (dealerRevealed ? d.getSum() : "?"));
        playerSumLabel.setText("Player " + p.getName() + "'s Hand " + (p.getCurrentHandIndex()+1) + "'s Sum: " + p.getCurrentHand().getSum());
        tokensLabel.setText(String.format("Tokens: %.2f", p.getTokens()));
        sideBetLabel.setText(String.format("Side Bet: %.2f", p.getSidebets()));
        sideBetLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        List<Hand> hands = p.getHands();
        showCards(hand1HBox, hands.get(0).getCards(), false);
        if (hands.size() == 1) {
            hand2HBox.setVisible(false);
        } else {
            showCards(hand2HBox, hands.get(1).getCards(), false);
            hand2HBox.setVisible(true);
        }

        setActionButtons();
    }

    private void showDealerCards() {
        dealerCardsHBox.getChildren().clear();

        List<Card> dealerCards = game.getDealer().getHand().getCards();

        if (dealerRevealed) {
            // Show all dealer cards normally
        	for (Card card : dealerCards) {
            	addCardToHBox(dealerCardsHBox, card.getImagePath());
            }
        } else {
            if (!dealerCards.isEmpty()) {
                
                // Show the back of the second card (if exists)
                if (dealerCards.size() > 1) {
                	if (dealerCards.size() > 1) {
            	    	addCardToHBox(dealerCardsHBox, dealerCards.get(0).getImagePath());
            	        // show hidden second card first (on left)
            	    }
            	    addCardToHBox(dealerCardsHBox, BACK_IMAGE_PATH);
                }
            }
        }
    }
    
    private void dealInitialCardsWithAnimation() {
        Player player = game.getCurrentPlayer();
        Dealer dealer = game.getDealer();

        // Make sure we clear previous cards
        hand1HBox.getChildren().clear();
        dealerCardsHBox.getChildren().clear();

        // Actually deal the cards first
        game.dealInitialCards(); // This should populate the hands

        PauseTransition pause = new PauseTransition(Duration.millis(400));
        SequentialTransition seq = new SequentialTransition();

        List<Card> playerCards = player.getCurrentHand().getCards();
        List<Card> dealerCards = dealer.getHand().getCards();

        for (int i = 0; i < playerCards.size(); i++) {
            int finalI = i;
            PauseTransition delay = new PauseTransition(Duration.millis(500 * finalI));
            delay.setOnFinished(e -> animateCardDeal(playerCards.get(finalI).getImagePath(), hand1HBox));
            seq.getChildren().add(delay);
        }

        // Show one dealer card face-up, one face-down
        seq.getChildren().add(new PauseTransition(Duration.millis(500 + (playerCards.size() - 1) * 500)));
        seq.setOnFinished(e -> {
            animateCardDeal(dealerCards.get(0).getImagePath(), dealerCardsHBox);
            animateCardDeal(BACK_IMAGE_PATH, dealerCardsHBox); // face down
            updateLabelsAndButtons();
        });

        seq.play();
    }
    
    private void updateDeckVisual() {
        if (!deckPilePane.getChildren().isEmpty()) {
            deckPilePane.getChildren().remove(deckPilePane.getChildren().size() - 1);
        }
    }
    
    private void animateCardDeal(String imagePath, HBox destinationBox) {
        try (InputStream in = getClass().getResourceAsStream(imagePath)) {
            ImageView card = new ImageView(new Image(in));
            card.setFitWidth(70);
            card.setFitHeight(100);
            overlayPane.getChildren().add(card);

            Bounds deckBounds = deckPilePane.localToScene(deckPilePane.getBoundsInLocal());
            Bounds destBounds = destinationBox.localToScene(destinationBox.getBoundsInLocal());
            Bounds overlayBounds = overlayPane.localToScene(overlayPane.getBoundsInLocal());

            double startX = deckBounds.getMinX() - overlayBounds.getMinX();
            double startY = deckBounds.getMinY() - overlayBounds.getMinY();
            double endX = destBounds.getMinX() - overlayBounds.getMinX() + destinationBox.getChildren().size() * (card.getFitWidth() + 10);
            double endY = destBounds.getMinY() - overlayBounds.getMinY();

            card.setLayoutX(startX);
            card.setLayoutY(startY);

            TranslateTransition transition = new TranslateTransition(Duration.millis(400), card);
            transition.setToX(endX - startX);
            transition.setToY(endY - startY);
            transition.setOnFinished(e -> {
                overlayPane.getChildren().remove(card);
                addCardToHBox(destinationBox, imagePath);
                updateDeckVisual(); // Simulate drawing a card from the deck visually
            });
            transition.play();
        } catch (Exception ex) {
            appendStatus("Error animating card: " + imagePath);
        }
    }

    private void showCards(HBox box, List<Card> cards, boolean animate) {
        box.getChildren().clear();
        for (Card c : cards) {
            if (animate) animateCardDeal(c.getImagePath(), box);
            else addCardToHBox(box, c.getImagePath());
        }
    }

    private void addCardToHBox(HBox box, String resource) {
        try (InputStream in = getClass().getResourceAsStream(resource)) {
            ImageView iv = new ImageView(new Image(in));
            iv.setFitWidth(70);
            iv.setFitHeight(100);
            iv.setUserData(resource);
            box.getChildren().add(iv);
        } catch (Exception ex) {
            appendStatus("Error loading card image: " + resource);
        	System.err.print("Error loading card image: " + resource);
        }
    }

    @FXML
    private void onPlaceBet() {
        try {
            float main = Float.parseFloat(mainBetField.getText());
            float side = sideBetField.getText().isBlank() ? 0f : Float.parseFloat(sideBetField.getText());
            
            boolean hasSelectedSideBets = perfectPairCheckBox.isSelected() || twentyOnePlusThreeCheckBox.isSelected();
            if (side > 0 && !hasSelectedSideBets) {
                appendStatus("Error: You must select at least one side bet option to place a side bet.");
                return;
            }

            Player p = game.getCurrentPlayer();
            if (p.getAvailableTokens() < main + side) {
                appendStatus("Insufficient tokens for bet!");
            	System.err.print("Insufficient tokens for bet!");
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
            dealInitialCardsWithAnimation(); 
            insuranceTaken = !game.dealerHasAce();

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
        List<Card> cards = p.getCurrentHand().getCards();
        Card newCard = cards.get(cards.size() - 1);

        // Determine the right hand HBox
        HBox targetHBox = p.getCurrentHandIndex() == 0 ? hand1HBox : hand2HBox;
        animateCardDeal(newCard.getImagePath(), targetHBox);

        // Update labels without re-rendering cards
        dealerSumLabel.setText("Dealer Sum: " + (dealerRevealed ? game.getDealer().getSum() : "?"));
        playerSumLabel.setText("Player Sum: " + p.getCurrentHand().getSum());
        tokensLabel.setText(String.format("Tokens: %.2f", p.getTokens()));
        sideBetLabel.setText(String.format("Side Bet: %.2f", p.getSidebets()));
        setActionButtons(); // Enable/disable buttons based on state

        if (p.getSum() >= 21) {
            if (p.isBust()) {
            	appendStatus("Player busted!");
            }

            // Delay slightly to allow animation to finish
            Platform.runLater(() -> {
                PauseTransition pause = new PauseTransition(Duration.millis(400));
                pause.setOnFinished(e -> endPlayerTurn());
                pause.play();
            });
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
            p.doubleDown(game.getDeck()); // this adds the third card to the current hand
            appendStatus("Player doubles down.");

            // Get the new card (the last one added)
            List<Card> cards = p.getCurrentHand().getCards();
            Card newCard = cards.get(cards.size() - 1);

            // Choose the right HBox (hand1 or hand2)
            HBox targetHBox = p.getCurrentHandIndex() == 0 ? hand1HBox : hand2HBox;
            animateCardDeal(newCard.getImagePath(), targetHBox);

            // Update just labels, not cards
            dealerSumLabel.setText("Dealer Sum: " + (dealerRevealed ? game.getDealer().getSum() : "?"));
            playerSumLabel.setText("Player Sum: " + p.getCurrentHand().getSum());
            tokensLabel.setText(String.format("Tokens: %.2f", p.getTokens()));
            sideBetLabel.setText(String.format("Side Bet: %.2f", p.getSidebets()));
            setActionButtons(); // disable hit/double/etc

            // Wait for animation to finish before ending turn
            Platform.runLater(() -> {
                PauseTransition pause = new PauseTransition(Duration.millis(400));
                pause.setOnFinished(e -> endPlayerTurn());
                pause.play();
            });

        } else {
            appendStatus("Cannot double down.");
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
        if (p.isBlackjack()) endPlayerTurn();
    }
    
    private void animateSplit() {
    	Card originalLeft = game.getCurrentPlayer().getHands().get(0).getCards().get(0);
        Card originalRight = game.getCurrentPlayer().getHands().get(1).getCards().get(0);

        // Get the new drawn cards after split
        Card newCard1 = game.getCurrentPlayer().getHands().get(0).getCards().get(1);
        Card newCard2 = game.getCurrentPlayer().getHands().get(1).getCards().get(1);

        // Clear both HBoxes before rebuilding
        hand1HBox.getChildren().clear();
        hand2HBox.getChildren().clear();

        // Add the original split cards manually (no animation)
        addCardToHBox(hand1HBox, originalLeft.getImagePath());
        addCardToHBox(hand2HBox, originalRight.getImagePath());

        // Make second hand visible now
        hand2HBox.setVisible(true);

        // Animate new cards being dealt to each hand
        animateCardDeal(newCard1.getImagePath(), hand1HBox);
        animateCardDeal(newCard2.getImagePath(), hand2HBox);

        appendStatus("Cards split into two hands.");
        updateLabelsAndButtons();
    }
    
    private void updateLabelsAndButtons() {
        Player p = game.getCurrentPlayer();
        dealerSumLabel.setText("Dealer Sum: " + (dealerRevealed ? game.getDealer().getSum() : "?"));
        playerSumLabel.setText("Player Sum: " + p.getCurrentHand().getSum());
        tokensLabel.setText(String.format("Tokens: %.2f", p.getTokens()));
        sideBetLabel.setText(String.format("Side Bet: %.2f", p.getSidebets()));
        setActionButtons();
    }

    @FXML
    private void onInsurance() {
        Player p = game.getCurrentPlayer();
        game.placeInsurance(p);
        appendStatus("Insurance taken.");
        if (!game.nextPlayer()) {
        	insuranceTaken = true;
        	if (game.insurancePayout()) {
        		appendStatus("DEALER HAS BLACKJACK!");
        		dealerRevealed = true;
        		showDealerCards();
        		updateUI();
        		endRound();
        		return;
        	}
        	appendStatus("Nobody's home");
        }
        updateUI();

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

            endRound();
        }
    }
    

    
    private void endRound() {
        Player currentPlayer = game.getCurrentPlayer();

        // 1. Evaluate sidebet payouts
        for (Player player : game.getPlayers()) {
            game.evaluateSidebetPayouts(player);
        }

        // 2. Evaluate main bet results
        game.evaluateResults();

        // 3. Update UI to reflect final state
        updateUI();

        // 4. Disable action buttons until next round
        setActionButtons(true);

        // 5. Enable New Round button
        newRoundButton.setDisable(false);
    }

       
      

    @FXML
    private void onNewRound() {
        collectCardsWithAnimation(() -> {
            resetForNewRound();
        });
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
    }

    private void appendStatus(String line) {
        statusTextArea.setText(statusTextArea.getText() + "\n" + line);
        statusTextArea.positionCaret(statusTextArea.getText().length());
    }
}