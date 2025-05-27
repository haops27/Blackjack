package blackjack.userinterface;

import blackjack.deck.Card;
import blackjack.actor.Dealer;
import blackjack.actor.Hand;
import blackjack.actor.Player;
import blackjack.bet.BettingSystem.SideBetRule;
import blackjack.logic.Game;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
    
    private final String TABLE_IMAGE_PATH = "/images/table.png";
    private final String CHIP_IMAGE_PATH = "/images/chips.png";
    private final String BACK_IMAGE_PATH = "/images/back.png";


    @FXML
    public void initialize() {
        // Load images
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

        // Initialize players
        game.initializePlayers(List.of("Player1"));
        resetForNewRound();
    }
    
    /**
     * Phương thức này vô hiệu hóa các nút bấm
     */
    private void setActionButtons(boolean disable) {
        hitButton.setDisable(disable);
        standButton.setDisable(disable);
        doubleButton.setDisable(disable);
        splitButton.setDisable(disable);
        insuranceButton.setDisable(disable);
    }
    
    /**
     * Phương thức này khởi động các nút bấm dựa trên trạng thái của Player
     */
    private void setActionButtons() {
    	hitButton.setDisable(game.getCurrentPlayer().getSum() >= 21);
        standButton.setDisable(false);
        doubleButton.setDisable(!game.getCurrentPlayer().canDouble());
        splitButton.setDisable(!game.getCurrentPlayer().canSplit());
        insuranceButton.setDisable(!game.dealerHasAce());
    }
    
    /**
     * Cập nhật giao diện cho player/dealer
     */
    private void updateUI() {
        Player p = game.getCurrentPlayer();
        Dealer d = game.getDealer();

        // Dealer cards display
        showDealerCards();

        // Show sums and tokens
        dealerSumLabel.setText("Dealer Sum: " + (dealerRevealed ? d.getSum() : "?"));
        playerSumLabel.setText("Player Sum: " + p.getCurrentHand().getSum());
        tokensLabel.setText(String.format("Tokens: %.2f", p.getTokens()));

        // Side bet label style and text
        sideBetLabel.setText(String.format("Side Bet: %.2f", p.getSidebets()));
        sideBetLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        // Show player's cards
        playerCardsHBox.getChildren().clear();

        List<Hand> hands = p.getHands();
        for (int i = 0; i < hands.size(); i++) {
            HBox handBox = new HBox(10);
            handBox.setAlignment(Pos.CENTER);
            // draw each card in this hand
            for (Card c : hands.get(i).getCards()) {
                addCardToHBox(handBox, c.getImagePath());
            }
            // highlight the active hand
            if (i == p.getCurrentHandIndex()) {
                handBox.setStyle(
                   "-fx-border-color: gold; -fx-border-width: 3; -fx-padding: 5;"
                );
            }
            playerCardsHBox.getChildren().add(handBox);
        }

        // Update status message (only append when new action)
        appendStatus("Playing hand " + (p.getCurrentHandIndex() + 1) + " of " + p.getHands().size());
        setActionButtons();
    }
    /**
     * Hiện lá bài của Dealer
     */
    private void showDealerCards() {
        if (dealerRevealed) {
        	dealerCardsHBox.getChildren().clear();
            showCards(dealerCardsHBox, game.getDealer().getHand().getCards());
        } else {
        	dealerCardsHBox.getChildren().clear();
            if (game.getDealer().getNumCards() >= 1) {
            	addCardToHBox(dealerCardsHBox, game.getDealer().showFirstCard().getImagePath());
            }
            addCardToHBox(dealerCardsHBox, BACK_IMAGE_PATH); // Face down card
        }
    	
    }
    
    /**
     * Thêm toàn bộ lá bài của 1 người chơi vào HBox của người chơi đó
     */
    private void showCards(HBox box, List<Card> cards) {
        box.getChildren().clear();
        for (Card c : cards) addCardToHBox(box, c.getImagePath());
    }
    
    /**
     * Thêm lá bài vào mục hiện lá bài của player/dealer<br>
     * - box: HBox chứa lá bài của player/dealer<br>
     * - resource: đường dẫn đến ánh lá bài<br>
     */
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
            float side = sideBetField.getText().isBlank() 
                         ? 0f 
                         : Float.parseFloat(sideBetField.getText());

            // ❗ Reject an “orphan” side-bet before doing anything else
            if (side > 0 
                && !perfectPairCheckBox.isSelected() 
                && !twentyOnePlusThreeCheckBox.isSelected()) {
                appendStatus("You must select a side‐bet rule before placing a side‐bet!");
                return;
            }

            Player p = game.getCurrentPlayer();
            if (p.getAvailableTokens() < main + side) {
                appendStatus("Insufficient tokens for bet!");
                return;
            }

            // Only now collect the rules
            Set<SideBetRule> rules = new HashSet<>();
            if (side > 0) {
                if (perfectPairCheckBox.isSelected()) 
                    rules.add(SideBetRule.PERFECT_PAIR);
                if (twentyOnePlusThreeCheckBox.isSelected()) 
                    rules.add(SideBetRule.TWENTYONE_PLUS_THREE);
            }

            game.resetRound();
            dealerRevealed = false;
            chipImageView.setVisible(true);

            game.placeBets(p, main, side, rules);
            appendStatus(String.format("Bet placed: Main %.1f, Side %.1f", main, side));

            game.dealInitialCards();
            updateUI();

            // disable betting controls
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
        	return;
        }
        
        setActionButtons();
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
            return;
        } else {
            appendStatus("Cannot double down.");
        }
        setActionButtons();
    }

    @FXML
    private void onSplit() {
        Player p = game.getCurrentPlayer();
        if (p.canSplit()) {
            p.split(game.getDeck());
            appendStatus("Player splits hand.");
            updateUI();
            if (game.getCurrentPlayer().isBlackjack()) {
            	endPlayerTurn();
            }
            return;
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
    
    /**
     * Kiểm tra xem player đã hết lượt chơi chưa<br>
     * Nếu chưa, chuyển sang Hand tiếp theo của Player hoặc Player tiếp theo
     */
    private void endPlayerTurn() {
        if (game.nextPlayer()) {
            appendStatus("Next turn.");
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
    
    /**
     * Đặt lại card, bets để sang ván mới
     */
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

        setActionButtons(true);
        newRoundButton.setDisable(true);

        //insuranceButton.setDisable(true);

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