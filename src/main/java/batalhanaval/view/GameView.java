package batalhanaval.view;

import batalhanaval.controller.GameController;
import batalhanaval.model.ResultadoAtaque;
import batalhanaval.model.Tabuleiro;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * Ecrã principal da batalha.
 *
 * Layout:
 *   [barra topo: turno + nome]
 *   [tabuleiro player]  [tabuleiro CPU]
 *   [barra baixo: mensagens + botão desistir]
 *
 * Resultado do ataque: mensagem em grande no centro, aparece e desaparece em 1 segundo.
 */
public class GameView {

    private final Scene scene;
    private final TabuleiroPainel painelPlayer;
    private final TabuleiroPainel painelCPU;
    private final Label lblTurno;
    private final Label lblInfoPlayer;
    private final Label lblInfoCPU;
    private final Label lblFlash; // mensagem em grande que aparece/desaparece

    public GameView(GameController controller,
                    Tabuleiro tabuleiroPlayer,
                    Tabuleiro tabuleiroCPU,
                    String nomePlayer) {

        // ── BARRA TOPO ────────────────────────────────────────────────
        lblTurno = new Label("Jogada de " + nomePlayer);
        lblTurno.setStyle(
                "-fx-font-size:20px; -fx-font-weight:bold; -fx-text-fill:#ffd600;" +
                        "-fx-effect: dropshadow(gaussian, black, 8, 0.6, 0, 1);"
        );

        HBox barraTopo = new HBox(lblTurno);
        barraTopo.setAlignment(Pos.CENTER);
        barraTopo.setPadding(new Insets(10, 0, 6, 0));
        barraTopo.setStyle("-fx-background-color: rgba(5,20,40,0.75);");

        // ── TABULEIROS ────────────────────────────────────────────────
        // Player (com navios visíveis, sem cliques)
        painelPlayer = new TabuleiroPainel(true, null);
        painelPlayer.atualizar(tabuleiroPlayer);

        Label lblNomePlayer = new Label("Tabuleiro de " + nomePlayer);
        lblNomePlayer.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#b3e5fc;");

        lblInfoPlayer = new Label();
        lblInfoPlayer.setStyle("-fx-font-size:12px; -fx-text-fill:#90caf9;");

        VBox boxPlayer = new VBox(6, lblNomePlayer, lblInfoPlayer, painelPlayer);
        boxPlayer.setAlignment(Pos.TOP_CENTER);

        // CPU (sem navios visíveis, com cliques)
        painelCPU = new TabuleiroPainel(false,
                (linha, coluna) -> controller.processarAtaquePlayer(linha, coluna));
        painelCPU.atualizar(tabuleiroCPU);

        Label lblNomeCPU = new Label("Tabuleiro da CPU");
        lblNomeCPU.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#b3e5fc;");

        lblInfoCPU = new Label();
        lblInfoCPU.setStyle("-fx-font-size:12px; -fx-text-fill:#90caf9;");

        VBox boxCPU = new VBox(6, lblNomeCPU, lblInfoCPU, painelCPU);
        boxCPU.setAlignment(Pos.TOP_CENTER);

        HBox zonaTabuleiros = new HBox(40, boxPlayer, boxCPU);
        zonaTabuleiros.setAlignment(Pos.CENTER);
        zonaTabuleiros.setPadding(new Insets(10, 20, 10, 20));

        // ── BARRA BAIXO ───────────────────────────────────────────────
        Button btnDesistir = new Button("Desistir");
        btnDesistir.setStyle(
                "-fx-background-color:#c62828; -fx-text-fill:white;" +
                        "-fx-font-size:13px; -fx-background-radius:8; -fx-cursor:hand; -fx-padding:7 18 7 18;"
        );
        btnDesistir.setOnAction(e -> controller.voltarAoMenu());

        HBox barraBaixo = new HBox(btnDesistir);
        barraBaixo.setAlignment(Pos.CENTER);
        barraBaixo.setPadding(new Insets(8, 0, 10, 0));
        barraBaixo.setStyle("-fx-background-color: rgba(5,20,40,0.75);");

        // ── MENSAGEM FLASH (por cima de tudo, no centro) ─────────────
        lblFlash = new Label();
        lblFlash.setStyle(
                "-fx-font-size:52px; -fx-font-weight:bold; -fx-text-fill:white;" +
                        "-fx-effect: dropshadow(gaussian, black, 18, 0.9, 0, 2);" +
                        "-fx-padding: 14 40 14 40;" +
                        "-fx-background-color: rgba(0,0,0,0.55);" +
                        "-fx-background-radius: 18;"
        );
        lblFlash.setTextAlignment(TextAlignment.CENTER);
        lblFlash.setOpacity(0);
        lblFlash.setMouseTransparent(true);

        // ── LAYOUT PRINCIPAL ──────────────────────────────────────────
        VBox coluna = new VBox(barraTopo, zonaTabuleiros, barraBaixo);
        coluna.setStyle("-fx-background-color: linear-gradient(to bottom, #0a2942, #0d3d5c);");

        StackPane root = new StackPane(coluna, lblFlash);
        StackPane.setAlignment(lblFlash, Pos.CENTER);

        atualizarInfo(tabuleiroPlayer, tabuleiroCPU);

        scene = new Scene(root, 1000, 620);
        MenuView.aplicarEstilos(scene);
    }

    /** Atualiza ambos os tabuleiros. */
    public void atualizarTabuleiros(Tabuleiro tabPlayer, Tabuleiro tabCPU) {
        painelPlayer.atualizar(tabPlayer);
        painelCPU.atualizar(tabCPU);
        atualizarInfo(tabPlayer, tabCPU);
    }

    /**
     * Mostra mensagem em grande no centro durante 1 segundo e desaparece.
     */
    public void mostrarFlash(ResultadoAtaque resultado, boolean foiPlayer) {
        String emoji;
        String texto;
        String cor;

        switch (resultado) {
            case AFUNDADO -> { emoji = "💥"; texto = "NAVIO AFUNDADO!"; cor = "#ff5722"; }
            case ACERTO   -> { emoji = "🎯"; texto = "ACERTO!";         cor = "#ff1744"; }
            default       -> { emoji = "💧"; texto = "ÁGUA!";           cor = "#64b5f6"; }
        }

        lblFlash.setText(emoji + "  " + texto);
        lblFlash.setStyle(
                "-fx-font-size:52px; -fx-font-weight:bold; -fx-text-fill:" + cor + ";" +
                        "-fx-effect: dropshadow(gaussian, black, 18, 0.9, 0, 2);" +
                        "-fx-padding: 14 40 14 40;" +
                        "-fx-background-color: rgba(0,0,0,0.60);" +
                        "-fx-background-radius: 18;"
        );

        // Animação: aparece rápido, fica, desaparece
        FadeTransition aparecer = new FadeTransition(Duration.millis(150), lblFlash);
        aparecer.setFromValue(0); aparecer.setToValue(1);

        PauseTransition pausa = new PauseTransition(Duration.millis(900));

        FadeTransition desaparecer = new FadeTransition(Duration.millis(400), lblFlash);
        desaparecer.setFromValue(1); desaparecer.setToValue(0);

        new SequentialTransition(aparecer, pausa, desaparecer).play();
    }

    public void setMensagemTurno(String msg) {
        lblTurno.setText(msg);
    }

    public void bloquearAtaques() {
        painelCPU.desativarCliques();
    }

    private void atualizarInfo(Tabuleiro tabPlayer, Tabuleiro tabCPU) {
        lblInfoPlayer.setText("Navios restantes: "
                + (tabPlayer.getNumNavios() - tabPlayer.getNumNaviosAfundados()));
        lblInfoCPU.setText("Afundados: "
                + tabCPU.getNumNaviosAfundados() + "/" + tabCPU.getNumNavios());
    }

    public Scene getScene() { return scene; }
}