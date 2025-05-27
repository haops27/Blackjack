package blackjack.userinterface;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

public class MainMenuController {

    @FXML
    private void playBlackjack(MouseEvent event) {
    	Stage primaryStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
    	//Parent root = FXMLLoader.load(getClass().getResource("/blackjack/resources/fxml/PlayerSetupView.fxml"));
    	URL loc = getClass().getResource("/resources/fxml/PlayerSetupView.fxml");
        if (loc == null) {
            throw new IllegalStateException(
                "Cannot find PlayerSetupView.fxml at /resources/fxml/PlayerSetupView.fxml"
            );
        }
        Parent root = null;
		try {
			root = new FXMLLoader(loc).load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // make the initial window exactly 400×400:
        Scene scene = new Scene(root, 700, 700);
        primaryStage.setTitle("Blackjack Game — Setup");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    @FXML
    private void playTexasHoldem(MouseEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/resources/fxml/HoldemView.fxml"));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setTitle("Texas Hold'em");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Có thể thêm thông báo lỗi cho người dùng nếu cần
        }
    }
}
