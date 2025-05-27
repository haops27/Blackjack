package blackjack.userinterface;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

public class MainMenuController {

    @FXML
    private void playBlackjack(MouseEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Blackjack");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Có thể thêm thông báo lỗi cho người dùng nếu cần
        }
    }

    @FXML
    private void playTexasHoldem(MouseEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/HoldemView.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Texas Hold'em");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Có thể thêm thông báo lỗi cho người dùng nếu cần
        }
    }
}
