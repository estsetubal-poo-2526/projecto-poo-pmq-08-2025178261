package batalhanaval.controller;

import batalhanaval.exception.ColocacaoNavioException;
import batalhanaval.exception.PosicaoInvalidaException;
import batalhanaval.model.*;
import batalhanaval.view.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Controlador principal — liga o modelo às views.
 * Gere navegação entre ecrãs e processa todas as ações do utilizador.
 */
public class GameController {

    private final Stage stage;
    private final String nomePlayer;

    private Game game;
    private ColocacaoView colocacaoView;
    private GameView gameView;

    public GameController(Stage stage, String nomePlayer) {
        this.stage = stage;
        this.nomePlayer = nomePlayer;
    }

    // ── NAVEGAÇÃO ─────────────────────────────────────────────────────

    public void iniciarFaseColocacao() {
        try {
            game = new Game(nomePlayer);
            game.iniciar();

            colocacaoView = new ColocacaoView(stage, this,
                    game.getPlayer().getTabuleiro(), nomePlayer);
            stage.setScene(colocacaoView.getScene());
            stage.setWidth(960);
            stage.setHeight(590);
            stage.centerOnScreen();

        } catch (Exception e) {
            mostrarErro("Erro ao iniciar", e.getMessage());
        }
    }

    public void colocarNaviosAleatorio() {
        try {
            game = new Game(nomePlayer);
            game.iniciar();
            ColocadorNavios.colocarNaviosAleatorio(
                    game.getPlayer().getTabuleiro(), new java.util.Random());

            colocacaoView.resetNaviosColocados(game.getPlayer().getTabuleiro());

        } catch (Exception e) {
            mostrarErro("Erro na colocação aleatória", e.getMessage());
        }
    }

    public void colocarNavioManual(TipoNavio tipo, int linha, int coluna, boolean horizontal) {
        if (tipo == null) return;

        if (colocacaoView.navioJaColocado(tipo)) {
            mostrarAviso("Navio já colocado",
                    "Já colocaste o " + tipo.getNome() + ".\nEscolhe outro tipo de navio.");
            return;
        }

        try {
            Navio navio = new Navio(tipo);
            game.getPlayer().getTabuleiro().colocarNavio(navio, linha, coluna, horizontal);
            colocacaoView.marcarNavioColocado(tipo, game.getPlayer().getTabuleiro());

        } catch (ColocacaoNavioException e) {
            mostrarAviso("Posição inválida", e.getMessage());
        } catch (PosicaoInvalidaException e) {
            mostrarAviso("Fora do tabuleiro", e.getMessage());
        }
    }

    public void iniciarBatalha() {
        if (game.getPlayer().getTabuleiro().getNumNavios() == 0) {
            mostrarAviso("Navios em falta",
                    "Tens de colocar os navios antes de iniciar!\n" +
                            "Usa 'Colocação Aleatória' ou clica no tabuleiro.");
            return;
        }

        game.iniciarBatalha();

        gameView = new GameView(this,
                game.getPlayer().getTabuleiro(),
                game.getCpu().getTabuleiro(),
                nomePlayer);

        stage.setScene(gameView.getScene());
        stage.setWidth(1000);
        stage.setHeight(650);
        stage.centerOnScreen();
    }

    // ── LÓGICA DE JOGO ───────────────────────────────────────────────

    public void processarAtaquePlayer(int linha, int coluna) {
        try {
            Posicao p = game.getCpu().getTabuleiro().getPosicao(linha, coluna);
            if (p.isAtacada()) {
                mostrarAviso("Já atacado", "Escolhe uma posição ainda não atacada.");
                return;
            }
        } catch (PosicaoInvalidaException e) { return; }

        gameView.bloquearAtaques();

        try {
            // Ataque do player
            ResultadoAtaque resultadoPlayer = game.atacarCPU(linha, coluna);
            gameView.atualizarTabuleiros(
                    game.getPlayer().getTabuleiro(),
                    game.getCpu().getTabuleiro());
            gameView.mostrarFlash(resultadoPlayer, true);
            gameView.setMensagemTurno("Jogada de " + nomePlayer
                    + " → " + resultadoPlayer.getMensagem());

            if (game.estaTerminado()) {
                new Thread(() -> {
                    try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
                    Platform.runLater(this::mostrarResultadoFinal);
                }).start();
                return;
            }

            // Turno da CPU com delay
            gameView.setMensagemTurno("Jogada de CPU...");
            new Thread(() -> {
                try { Thread.sleep(1400); } catch (InterruptedException ignored) {}
                Platform.runLater(() -> {
                    try {
                        int[] ataqueCPU = game.turnosCPU();
                        ResultadoAtaque resultadoCPU = ResultadoAtaque.values()[ataqueCPU[2]];

                        gameView.atualizarTabuleiros(
                                game.getPlayer().getTabuleiro(),
                                game.getCpu().getTabuleiro());
                        gameView.mostrarFlash(resultadoCPU, false);
                        gameView.setMensagemTurno("CPU → " + resultadoCPU.getMensagem());

                        if (game.estaTerminado()) {
                            new Thread(() -> {
                                try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
                                Platform.runLater(this::mostrarResultadoFinal);
                            }).start();
                            return;
                        }

                        // Recriar GameView com cliques ativos
                        gameView = new GameView(this,
                                game.getPlayer().getTabuleiro(),
                                game.getCpu().getTabuleiro(),
                                nomePlayer);
                        gameView.setMensagemTurno("Jogada de " + nomePlayer);
                        stage.setScene(gameView.getScene());

                    } catch (PosicaoInvalidaException e) {
                        mostrarErro("Erro CPU", e.getMessage());
                    }
                });
            }).start();

        } catch (PosicaoInvalidaException e) {
            mostrarErro("Posição inválida", e.getMessage());
        } catch (IllegalStateException e) {
            mostrarAviso("Ação inválida", e.getMessage());
        }
    }

    private void mostrarResultadoFinal() {
        boolean playerVenceu = game.getVencedor() == game.getPlayer();
        ResultadoView rv = new ResultadoView(this, playerVenceu);
        stage.setScene(rv.getScene());
        stage.setWidth(1100);
        stage.setHeight(760);
        stage.centerOnScreen();
    }

    // ── AÇÕES DE NAVEGAÇÃO ───────────────────────────────────────────

    public void reiniciarJogo() {
        iniciarFaseColocacao();
    }

    public void voltarAoMenu() {
        MenuView menuView = new MenuView(stage);
        stage.setScene(menuView.getScene());
        stage.setWidth(968);
        stage.setHeight(680);
        stage.centerOnScreen();
    }

    // ── UTILITÁRIOS UI ───────────────────────────────────────────────

    private void mostrarErro(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarAviso(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}