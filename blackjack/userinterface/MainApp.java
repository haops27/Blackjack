package blackjack.userinterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainApp extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL loc = getClass().getResource("/blackjack/resources/fxml/PlayerSetupView.fxml");
        if (loc == null) {
            throw new IllegalStateException(
                "Cannot find PlayerSetupView.fxml at /blackjack/resources/fxml/PlayerSetupView.fxml"
            );
        }
        Parent root = new FXMLLoader(loc).load();

        // make the initial window exactly 400×400:
        Scene scene = new Scene(root, 700, 700);
        primaryStage.setTitle("Blackjack Game — Setup");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
