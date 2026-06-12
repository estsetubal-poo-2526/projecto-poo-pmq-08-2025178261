package batalhanaval;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import batalhanaval.view.MenuView;

/**
 * Ponto de entrada da aplicação JavaFX Batalha Naval.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Batalha Naval");
        primaryStage.setResizable(false);

        MenuView menuView = new MenuView(primaryStage);
        primaryStage.setScene(menuView.getScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}