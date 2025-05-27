package texas.userinterface;

import blackjack.actor.Player;
import blackjack.deck.Card;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import texas.logic.TexasHoldemGame;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class HoldemController {

    @FXML private HBox communityCardsHBox;
    @FXML private HBox playerCardsHBox;

    @FXML private Label playerNameLabel;
    @FXML private Label tokensLabel;
    @FXML private Label potLabel;
    @FXML private Label currentBetLabel;

    @FXML private ImageView chipImageView; // ImageView hiển thị chip

    @FXML private TextArea statusLabel;

    @FXML private Button checkCallButton;
    @FXML private Button raiseButton;
    @FXML private Button foldButton;
    @FXML private Button nextStageButton;

    @FXML private TextField raiseAmountField;
    @FXML private Button resetButton;
    private final TexasHoldemGame game = new TexasHoldemGame();

    @FXML
    public void initialize() {
        // Load ảnh chip (chip.png trong thư mục /resources/images/)
        try (InputStream in = getClass().getResourceAsStream("/images/chips.png")) {
            if (in != null) {
                Image chipImage = new Image(in);
                chipImageView.setImage(chipImage);
                chipImageView.setFitWidth(24);
                chipImageView.setFitHeight(24);
                chipImageView.setVisible(false); // ẩn chip lúc đầu
            }
        } catch (Exception e) {
            System.err.println("Không load được ảnh chip: " + e.getMessage());
        }

        game.initializePlayers(List.of("Kien", "Hao", "Phuoc", "Dien", "Dat"));
        game.dealPreFlop();
        updateUI();

        resetButton.setVisible(false);
        resetButton.setDisable(true);
    }

    private void updateUI() {
        Player p = game.getCurrentPlayer();

        playerNameLabel.setText("Current: " + p.getName());
        tokensLabel.setText(String.format("Tokens: %.2f", p.getTokens()));
        potLabel.setText("Pot: " + game.getPot());
        currentBetLabel.setText("Current Bet: " + game.getCurrentBet());
        showCards(playerCardsHBox, p.getHoleCards());
        showCards(communityCardsHBox, game.getCommunityCards());

        checkCallButton.setText(game.getCurrentBet() == 0 ? "Check" : "Call");
        checkCallButton.setDisable(game.getStage() >= 4);
        raiseButton.setDisable(p.getTokens() < game.getCurrentBet() || game.getStage() >= 4);
        foldButton.setDisable(false);
        nextStageButton.setDisable(game.getStage() < 4);
        checkCallButton.setVisible(game.getStage() < 4);

        // Ẩn hiện chip theo raise hiện tại (nếu currentBet > 0 thì hiển thị chip)
        chipImageView.setVisible(game.getCurrentBet() > 0);

        if (game.getStage() == 4) {
            var winners = game.evaluateShowdown();
            if (winners.isEmpty()) {
                statusLabel.appendText("No winners (all folded).\n");
            } else {
                String winnerNames = winners.stream()
                    .map(Player::getName)
                    .collect(Collectors.joining(", "));
                statusLabel.appendText("Winner(s): " + winnerNames + "\n");
            }

            checkCallButton.setDisable(true);
            raiseButton.setDisable(true);
            foldButton.setDisable(true);
            nextStageButton.setDisable(true);

            resetButton.setVisible(true);
            resetButton.setDisable(false);
        } else {
            resetButton.setVisible(false);
            resetButton.setDisable(true);
        }

        statusLabel.appendText("Stage: " + game.getStage() + "\n");
    }

    private void showCards(HBox box, List<Card> cards) {
        box.getChildren().clear();
        for (Card c : cards) {
            try (InputStream in = getClass().getResourceAsStream(c.getImagePath())) {
                ImageView iv = new ImageView(new Image(in));
                iv.setFitWidth(70);
                iv.setFitHeight(100);
                box.getChildren().add(iv);
            } catch (Exception e) {
                // Có thể log lỗi hoặc bỏ qua
            }
        }
    }

    @FXML
    private void onCheckCall() {
        game.playerAction(game.getCurrentBet() == 0 ? "check" : "call", 0);
        statusLabel.appendText(checkCallButton.getText() + "\n");
        updateUI();
    }

    @FXML
    private void onRaise() {
        try {
            float amt = Float.parseFloat(raiseAmountField.getText());
            if (amt <= 0) {
                statusLabel.appendText("Raise amount must be positive\n");
                return;
            }
            game.playerAction("raise", amt);
            statusLabel.appendText("Raise " + amt + "\n");
        } catch (NumberFormatException e) {
            statusLabel.appendText("Invalid raise amount\n");
        }
        updateUI();
    }

    @FXML
    private void onFold() {
        game.playerAction("fold", 0);
        statusLabel.appendText("Fold\n");
        updateUI();
    }

    @FXML
    private void onNextStage() {
        if (game.getStage() < 4) {
            switch (game.getStage()) {
                case 0 -> game.dealFlop();
                case 1 -> game.dealTurn();
                case 2 -> game.dealRiver();
                case 3 -> game.showdown();
            }
            updateUI();
        } else {
            statusLabel.appendText("Round ended. Click 'Reset' to start new round.\n");
            checkCallButton.setDisable(true);
            raiseButton.setDisable(true);
            foldButton.setDisable(true);
            nextStageButton.setText("Reset");
            nextStageButton.setDisable(false);
        }
    }

    @FXML
    private void onResetRound() {
        game.resetRound();
        game.dealPreFlop();
        statusLabel.clear();
        statusLabel.appendText("New round started.\n");
        updateUI();

        checkCallButton.setDisable(false);
        raiseButton.setDisable(false);
        foldButton.setDisable(false);
        nextStageButton.setText("Next Stage");
    }

    @FXML
    private void onReset() {
        game.resetRound();
        game.dealPreFlop();
        statusLabel.clear();
        statusLabel.appendText("New round started.\n");
        updateUI();
    }
}
