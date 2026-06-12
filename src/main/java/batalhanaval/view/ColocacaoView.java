package batalhanaval.view;

import batalhanaval.controller.GameController;
import batalhanaval.model.Tabuleiro;
import batalhanaval.model.TipoNavio;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Map;

/**
 * Ecrã de colocação de navios.
 *
 * Sistema de seleção de células:
 *  1. O jogador passa o rato sobre o tabuleiro → preview mostra onde ficará o navio
 *  2. Clica na primeira célula → a célula fica destacada, aguarda mais cliques
 *  3. Com orientação horizontal/vertical, o preview mostra o navio inteiro
 *  4. Apenas 1 navio de cada tipo permitido
 */
public class ColocacaoView {

    private final Scene scene;
    private final GameController controller;
    private final TabuleiroPainel tabuleiroPainel;

    private TipoNavio navioSelecionado = TipoNavio.PORTA_AVIOES;
    private boolean horizontal = true;

    private final Map<TipoNavio, Boolean>    naviosColocados = new EnumMap<>(TipoNavio.class);
    private final Map<TipoNavio, RadioButton> radioButtons   = new EnumMap<>(TipoNavio.class);
    private final Map<TipoNavio, Label>       checkLabels    = new EnumMap<>(TipoNavio.class);

    private Label lblDica; // instrução dinâmica

    public ColocacaoView(Stage stage, GameController controller,
                         Tabuleiro tabuleiro, String nomePlayer) {
        this.controller = controller;
        for (TipoNavio t : TipoNavio.values()) naviosColocados.put(t, false);

        // ── Painel lateral ────────────────────────────────────────────
        VBox painelControlo = new VBox(10);
        painelControlo.setPadding(new Insets(18));
        painelControlo.setPrefWidth(245);
        painelControlo.setAlignment(Pos.TOP_CENTER);
        painelControlo.setStyle(
                "-fx-background-color: rgba(5,20,40,0.88);" +
                        "-fx-background-radius: 12;"
        );

        Label lblTitulo = new Label("Colocar Navios");
        lblTitulo.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#b3e5fc;");

        Label lblJogador = new Label("Jogador: " + nomePlayer);
        lblJogador.setStyle("-fx-font-size:13px; -fx-text-fill:#ffd600; -fx-font-weight:bold;");

        // Lista de navios com radio buttons
        Label lblNavio = new Label("Selecionar navio:");
        lblNavio.setStyle("-fx-font-size:13px; -fx-text-fill:#b3e5fc;");

        ToggleGroup grupoNavios = new ToggleGroup();
        VBox listaNavios = new VBox(8);

        for (TipoNavio tipo : TipoNavio.values()) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);

            Image imgNavio = getImagemNavio(tipo);
            if (imgNavio != null) {
                ImageView iv = new ImageView(imgNavio);
                iv.setFitWidth(36);
                iv.setFitHeight(14);
                iv.setPreserveRatio(false);
                row.getChildren().add(iv);
            }

            RadioButton rb = new RadioButton(tipo.getNome() + " (" + tipo.getTamanho() + ")");
            rb.setStyle("-fx-text-fill:#e0f7fa; -fx-font-size:12px;");
            rb.setToggleGroup(grupoNavios);
            rb.setUserData(tipo);
            if (tipo == TipoNavio.PORTA_AVIOES) rb.setSelected(true);
            rb.setOnAction(e -> {
                if (!naviosColocados.get(tipo)) {
                    navioSelecionado = tipo;
                    atualizarDica();
                }
            });
            radioButtons.put(tipo, rb);

            Label check = new Label();
            check.setStyle("-fx-text-fill:#69f0ae; -fx-font-size:14px;");
            checkLabels.put(tipo, check);

            row.getChildren().addAll(rb, check);
            listaNavios.getChildren().add(row);
        }

        // Orientação
        Label lblOrientacao = new Label("Orientação:");
        lblOrientacao.setStyle("-fx-font-size:13px; -fx-text-fill:#b3e5fc;");

        ToggleGroup grupoOrient = new ToggleGroup();
        RadioButton rbH = new RadioButton("Horizontal");
        RadioButton rbV = new RadioButton("Vertical");
        rbH.setStyle("-fx-text-fill:#e0f7fa; -fx-font-size:12px;");
        rbV.setStyle("-fx-text-fill:#e0f7fa; -fx-font-size:12px;");
        rbH.setToggleGroup(grupoOrient); rbH.setSelected(true);
        rbV.setToggleGroup(grupoOrient);
        rbH.setOnAction(e -> { horizontal = true;  atualizarDica(); });
        rbV.setOnAction(e -> { horizontal = false; atualizarDica(); });

        // Dica dinâmica
        lblDica = new Label();
        lblDica.setStyle(
                "-fx-font-size:12px; -fx-text-fill:#80cbc4; " +
                        "-fx-text-alignment:center; -fx-wrap-text:true;"
        );
        lblDica.setWrapText(true);
        lblDica.setMaxWidth(210);
        atualizarDica();

        // Botões
        Button btnAleatorio = new Button("⚡  Colocação Aleatória");
        btnAleatorio.setPrefWidth(210);
        btnAleatorio.setStyle(
                "-fx-background-color:#37474f; -fx-text-fill:#b3e5fc;" +
                        "-fx-font-size:13px; -fx-background-radius:8; -fx-cursor:hand; -fx-padding:8 14 8 14;"
        );
        btnAleatorio.setOnAction(e -> controller.colocarNaviosAleatorio());

        Button btnIniciar = new Button("⚔  Iniciar Batalha!");
        btnIniciar.setPrefWidth(210);
        btnIniciar.setStyle(
                "-fx-background-color:#0097a7; -fx-text-fill:white;" +
                        "-fx-font-size:14px; -fx-font-weight:bold; -fx-background-radius:8;" +
                        "-fx-cursor:hand; -fx-padding:10 14 10 14;"
        );
        btnIniciar.setOnAction(e -> controller.iniciarBatalha());

        painelControlo.getChildren().addAll(
                lblTitulo, lblJogador, new Separator(),
                lblNavio, listaNavios, new Separator(),
                lblOrientacao, rbH, rbV, new Separator(),
                lblDica, new Separator(),
                btnAleatorio, btnIniciar
        );

        // ── Tabuleiro com preview ao hover ────────────────────────────
        // variável auxiliar para usar dentro dos lambdas
        TabuleiroPainel[] ref = new TabuleiroPainel[1];

        ref[0] = new TabuleiroPainel(
                true,
                // clique → coloca navio
                (linha, coluna) -> controller.colocarNavioManual(navioSelecionado, linha, coluna, horizontal),
                // hover → preview
                (linha, coluna) -> {
                    if (navioSelecionado != null && !naviosColocados.get(navioSelecionado)) {
                        ref[0].mostrarPreview(linha, coluna, navioSelecionado.getTamanho(), horizontal);
                    }
                },
                // sair → limpa preview
                (linha, coluna) -> ref[0].limparPreview()
        );

        tabuleiroPainel = ref[0];
        tabuleiroPainel.atualizar(tabuleiro);

        // ── Layout principal ──────────────────────────────────────────
        HBox root = new HBox(24, painelControlo, tabuleiroPainel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0a2942, #0d3d5c);");

        scene = new Scene(root, 960, 560);
        MenuView.aplicarEstilos(scene);
    }

    private void atualizarDica() {
        if (navioSelecionado == null) return;
        String orient = horizontal ? "horizontal" : "vertical";
        lblDica.setText(
                "Passa o rato pelo tabuleiro para ver o preview.\n" +
                        "Clica para colocar o " + navioSelecionado.getNome() +
                        " (" + navioSelecionado.getTamanho() + " células, " + orient + ")."
        );
    }

    /** Chamado após colocar com sucesso. */
    public void marcarNavioColocado(TipoNavio tipo, Tabuleiro tabuleiro) {
        naviosColocados.put(tipo, true);
        RadioButton rb = radioButtons.get(tipo);
        if (rb != null) rb.setDisable(true);
        Label check = checkLabels.get(tipo);
        if (check != null) check.setText("✓");

        tabuleiroPainel.limparPreview();
        tabuleiroPainel.atualizar(tabuleiro);

        // Selecionar próximo navio automaticamente
        for (TipoNavio t : TipoNavio.values()) {
            if (!naviosColocados.get(t)) {
                navioSelecionado = t;
                RadioButton next = radioButtons.get(t);
                if (next != null) next.setSelected(true);
                atualizarDica();
                break;
            }
        }
    }

    /** Reset completo — colocação aleatória. */
    public void resetNaviosColocados(Tabuleiro tabuleiro) {
        for (TipoNavio t : TipoNavio.values()) {
            naviosColocados.put(t, true); // marca todos como colocados
            RadioButton rb = radioButtons.get(t);
            if (rb != null) rb.setDisable(true);
            Label check = checkLabels.get(t);
            if (check != null) check.setText("✓");
        }
        tabuleiroPainel.limparPreview();
        tabuleiroPainel.atualizar(tabuleiro);
        lblDica.setText("Navios colocados automaticamente!\nClica em 'Iniciar Batalha' para jogar.");
    }

    public boolean navioJaColocado(TipoNavio tipo) {
        return naviosColocados.getOrDefault(tipo, false);
    }

    private Image getImagemNavio(TipoNavio tipo) {
        return switch (tipo) {
            case PORTA_AVIOES     -> ImageLoader.portaAvioes();
            case NAVIO_GUERRA     -> ImageLoader.navioGuerra();
            case SUBMARINO        -> ImageLoader.submarino();
            case CONTRATORPEDEIRO -> ImageLoader.contratorpedeiro();
            case LANCHA           -> ImageLoader.lancha();
        };
    }

    public Scene getScene() { return scene; }
}