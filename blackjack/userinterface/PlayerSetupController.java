package blackjack.userinterface;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class PlayerSetupController {

    @FXML private void startGame1(ActionEvent e) { startGame(1, e); }
    @FXML private void startGame2(ActionEvent e) { startGame(2, e); }
    @FXML private void startGame3(ActionEvent e) { startGame(3, e); }
    @FXML private void startGame4(ActionEvent e) { startGame(4, e); }

    private void startGame(int count, ActionEvent event) {
        try {
            URL loc = getClass()
                       .getResource("/blackjack/resources/fxml/MainView.fxml");
            if (loc == null) {
                throw new IllegalStateException(
                  "MainView.fxml not found at /blackjack/resources/fxml/MainView.fxml"
                );
            }

            FXMLLoader loader = new FXMLLoader(loc);
            Parent root = loader.load();

            // Hand off the player count
            MainController controller = loader.getController();
            controller.initializePlayers(count);

            // **Pull the Stage from the event’s source node**
            Stage stage = (Stage)((Node)event.getSource())
                                 .getScene()
                                 .getWindow();

            // Swap in your game view (resize as needed)
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
