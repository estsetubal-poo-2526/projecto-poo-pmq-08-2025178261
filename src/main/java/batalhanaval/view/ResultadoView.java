package batalhanaval.view;

import batalhanaval.controller.GameController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Ecrã de resultado final.
 *
 * Usa a imagem de vitória ou derrota a full screen.
 * As imagens já têm o texto e o botão desenhados.
 * Coloca um botão invisível e clicável por cima do botão da imagem.
 *
 * Vitória:  imagem 1536x1024, botão "Jogar Novamente" ~y=73%, x centro
 * Derrota:  imagem 1536x1024, botão "Tentar Novamente" ~y=73%, x centro
 */
public class ResultadoView {

    // Janela mostrada em 1100x733 (ratio 3:2 da imagem original)
    private static final double W = 1100;
    private static final double H = 733;

    private final Scene scene;

    public ResultadoView(GameController controller, boolean playerVenceu) {

        StackPane root = new StackPane();

        // ── Imagem a full screen ──────────────────────────────────────
        Image img = playerVenceu ? ImageLoader.vitoria() : ImageLoader.derrota();
        ImageView iv = new ImageView();
        if (img != null) {
            iv.setImage(img);
            iv.setFitWidth(W);
            iv.setFitHeight(H);
            iv.setPreserveRatio(false);
        } else {
            root.setStyle("-fx-background-color: " + (playerVenceu ? "#1a237e" : "#b71c1c") + ";");
        }
        root.getChildren().add(iv);

        // ── Botão invisível sobre o botão da imagem ───────────────────
        // Na imagem (1536x1024): botão ocupa ~y=720-790, x=450-1090
        // Escalado para 1100x733: y ≈ 515-565, largura ≈ 460, centro x = 550
        Button btn = new Button();
        btn.setOpacity(0);
        btn.setPrefWidth(460);
        btn.setPrefHeight(70);
        btn.setStyle("-fx-cursor: hand;");

        // Posicionar: 71% de altura
        StackPane.setAlignment(btn, Pos.TOP_CENTER);
        btn.setTranslateY(H * 0.705);

        btn.setOnAction(e -> controller.reiniciarJogo());

        root.getChildren().add(btn);

        scene = new Scene(root, W, H);
        MenuView.aplicarEstilos(scene);
    }

    public Scene getScene() { return scene; }
}