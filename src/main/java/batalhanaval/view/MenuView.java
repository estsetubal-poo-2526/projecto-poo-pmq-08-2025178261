package batalhanaval.view;

import batalhanaval.controller.GameController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Ecrã inicial — imagem a full screen com campo de nome
 * posicionado por cima dos botões "Novo Jogo" e "Sair" da imagem.
 */
public class MenuView {

    private final Scene scene;

    public MenuView(Stage stage) {
        StackPane root = new StackPane();

        // ── Imagem de fundo a preencher toda a janela ──
        Image bg = ImageLoader.menuInicial();
        ImageView bgView = new ImageView();
        if (bg != null) {
            bgView.setImage(bg);
            bgView.setPreserveRatio(false);
            bgView.setFitWidth(968);
            bgView.setFitHeight(646);
        }
        root.getChildren().add(bgView);

        // ── Campo de nome por cima dos botões da imagem ──
        // Na imagem: "Novo Jogo" está ~55% de altura, "Sair" ~65%
        // O campo de nome fica mesmo por cima, ~48% de altura
        VBox painelNome = new VBox(10);
        painelNome.setAlignment(Pos.CENTER);
        painelNome.setMaxWidth(340);

        Label lblNome = new Label("O teu nome:");
        lblNome.setStyle(
                "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;" +
                        "-fx-effect: dropshadow(gaussian, black, 6, 0.8, 0, 1);"
        );

        TextField campoNome = new TextField("");
        campoNome.setPromptText("Escreve o teu nome...");
        campoNome.setMaxWidth(260);
        campoNome.setStyle(
                "-fx-background-color: rgba(10,40,80,0.85);" +
                        "-fx-text-fill: white; -fx-prompt-text-fill: #90caf9;" +
                        "-fx-border-color: #1565c0; -fx-border-radius: 6;" +
                        "-fx-background-radius: 6; -fx-padding: 7 12 7 12;" +
                        "-fx-font-size: 14px;"
        );

        Label lblErro = new Label();
        lblErro.setStyle("-fx-text-fill: #ff5252; -fx-font-size: 12px; -fx-font-weight: bold;");

        painelNome.getChildren().addAll(lblNome, campoNome, lblErro);

        // ── Botões invisíveis sobrepostos nos botões da imagem ──
        // "Novo Jogo" na imagem ocupa ~y=295-355 (de 646), x centro ~484
        Button btnNovoJogo = new Button();
        btnNovoJogo.setOpacity(0); // transparente — clickável mas invisível
        btnNovoJogo.setPrefWidth(280);
        btnNovoJogo.setPrefHeight(58);
        btnNovoJogo.setStyle("-fx-cursor: hand;");

        // "Sair" na imagem ocupa ~y=370-420
        Button btnSair = new Button();
        btnSair.setOpacity(0);
        btnSair.setPrefWidth(280);
        btnSair.setPrefHeight(50);
        btnSair.setStyle("-fx-cursor: hand;");

        btnNovoJogo.setOnAction(e -> {
            String nome = campoNome.getText().trim();
            if (nome.isEmpty()) {
                lblErro.setText("Escreve o teu nome primeiro!");
                return;
            }
            lblErro.setText("");
            new GameController(stage, nome).iniciarFaseColocacao();
        });

        campoNome.setOnAction(e -> btnNovoJogo.fire());

        btnSair.setOnAction(e -> stage.close());

        // Layout absoluto para posicionar sobre a imagem
        // Painel do nome: centrado horizontalmente, a ~44% de altura
        StackPane.setAlignment(painelNome, Pos.TOP_CENTER);
        painelNome.setTranslateY(258); // posicionado por cima dos botões da imagem

        // Botão Novo Jogo: centrado, ~46% altura
        StackPane.setAlignment(btnNovoJogo, Pos.TOP_CENTER);
        btnNovoJogo.setTranslateY(310);

        // Botão Sair: centrado, ~58% altura
        StackPane.setAlignment(btnSair, Pos.TOP_CENTER);
        btnSair.setTranslateY(385);

        root.getChildren().addAll(painelNome, btnNovoJogo, btnSair);

        scene = new Scene(root, 968, 646);
        aplicarEstilos(scene);
    }

    public Scene getScene() { return scene; }

    public static void aplicarEstilos(Scene scene) {
        var css = MenuView.class.getResource("/batalhanaval/styles.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
    }
}