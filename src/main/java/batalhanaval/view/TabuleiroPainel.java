package batalhanaval.view;

import batalhanaval.model.Navio;
import batalhanaval.model.Posicao;
import batalhanaval.model.TipoNavio;
import batalhanaval.model.Tabuleiro;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Tabuleiro visual usando tabuleiros.png como fundo.
 * Coordenadas calibradas por análise pixel-a-pixel da imagem.
 *
 * Imagem: 418x392px
 * Cabeçalho colunas (A-J): x=42..411, y=11..43
 * Cabeçalho linhas (1-10): x=10..42, y=43..380
 * Grelha células: OFFSET_X=42, OFFSET_Y=43, CELL_W=37, CELL_H=33.7
 */
public class TabuleiroPainel extends StackPane {

    // Dimensões calibradas da imagem
    public static final double IMG_W    = 418;
    public static final double IMG_H    = 392;
    public static final double OFFSET_X = 42;
    public static final double OFFSET_Y = 43;
    public static final double CELL_W   = (411.0 - 42.0) / 10.0; // 36.9
    public static final double CELL_H   = (380.0 - 43.0) / 10.0; // 33.7

    private final boolean mostrarNavios;
    private final Pane overlay;
    private final Rectangle[][] hitAreas;

    // Estado de preview para colocação
    private int[] previewStart  = null; // [linha, coluna] início do hover
    private int   previewTamanho = 0;
    private boolean previewHorizontal = true;

    @FunctionalInterface
    public interface CellClickHandler {
        void onClick(int linha, int coluna);
    }

    @FunctionalInterface
    public interface CellHoverHandler {
        void onHover(int linha, int coluna);
    }

    public TabuleiroPainel(boolean mostrarNavios, CellClickHandler onCellClick) {
        this(mostrarNavios, onCellClick, null, null);
    }

    public TabuleiroPainel(boolean mostrarNavios,
                           CellClickHandler onCellClick,
                           CellHoverHandler onHover,
                           CellHoverHandler onExit) {
        this.mostrarNavios = mostrarNavios;
        this.hitAreas = new Rectangle[Tabuleiro.TAMANHO][Tabuleiro.TAMANHO];

        setPrefSize(IMG_W, IMG_H);
        setMaxSize(IMG_W, IMG_H);
        setMinSize(IMG_W, IMG_H);

        // Imagem de fundo
        Image bg = ImageLoader.tabuleiros();
        ImageView bgView = new ImageView();
        if (bg != null) {
            bgView.setImage(bg);
            bgView.setFitWidth(IMG_W);
            bgView.setFitHeight(IMG_H);
            bgView.setPreserveRatio(false);
        }

        // Overlay para navios, ataques e cliques
        overlay = new Pane();
        overlay.setPrefSize(IMG_W, IMG_H);
        overlay.setPickOnBounds(false);

        // Áreas de clique sobre cada célula
        for (int l = 0; l < Tabuleiro.TAMANHO; l++) {
            for (int c = 0; c < Tabuleiro.TAMANHO; c++) {
                double x = OFFSET_X + c * CELL_W;
                double y = OFFSET_Y + l * CELL_H;

                Rectangle r = new Rectangle(x, y, CELL_W - 1, CELL_H - 1);
                r.setFill(Color.TRANSPARENT);
                r.setStroke(Color.TRANSPARENT);
                hitAreas[l][c] = r;

                if (onCellClick != null) {
                    final int linha  = l;
                    final int coluna = c;
                    final Rectangle rect = r;

                    rect.setOnMouseEntered(e -> {
                        if (onHover != null) onHover.onHover(linha, coluna);
                        else rect.setFill(Color.web("#4fc3f7", 0.35));
                    });
                    rect.setOnMouseExited(e -> {
                        if (onExit != null) onExit.onHover(linha, coluna);
                        else rect.setFill(Color.TRANSPARENT);
                    });
                    rect.setOnMouseClicked(e -> onCellClick.onClick(linha, coluna));
                    rect.setStyle("-fx-cursor: hand;");
                }
                overlay.getChildren().add(r);
            }
        }

        getChildren().addAll(bgView, overlay);
    }

    // ── PREVIEW DE COLOCAÇÃO ─────────────────────────────────────────

    /** Mostra preview do navio em hover durante a colocação. */
    public void mostrarPreview(int linha, int coluna, int tamanho, boolean horizontal) {
        limparPreview();
        for (int i = 0; i < tamanho; i++) {
            int l = horizontal ? linha : linha + i;
            int c = horizontal ? coluna + i : coluna;
            if (l >= 0 && l < Tabuleiro.TAMANHO && c >= 0 && c < Tabuleiro.TAMANHO) {
                hitAreas[l][c].setFill(Color.web("#4fc3f7", 0.50));
                hitAreas[l][c].setStroke(Color.web("#00bcd4", 0.8));
            }
        }
    }

    /** Remove o preview. */
    public void limparPreview() {
        for (int l = 0; l < Tabuleiro.TAMANHO; l++)
            for (int c = 0; c < Tabuleiro.TAMANHO; c++) {
                hitAreas[l][c].setFill(Color.TRANSPARENT);
                hitAreas[l][c].setStroke(Color.TRANSPARENT);
            }
    }

    // ── ATUALIZAÇÃO ──────────────────────────────────────────────────

    /** Atualiza toda a grelha com base no tabuleiro. */
    public void atualizar(Tabuleiro tabuleiro) {
        // Remove tudo exceto hit areas
        overlay.getChildren().removeIf(n -> !(n instanceof Rectangle));

        // Re-adiciona hit areas se foram removidas
        for (int l = 0; l < Tabuleiro.TAMANHO; l++)
            for (int c = 0; c < Tabuleiro.TAMANHO; c++)
                if (!overlay.getChildren().contains(hitAreas[l][c]))
                    overlay.getChildren().add(hitAreas[l][c]);

        // Desenha navios como imagem única por navio
        if (mostrarNavios) {
            for (Navio navio : tabuleiro.getNavios()) {
                desenharNavioImagem(navio);
            }
        }

        // Marcadores de ataque (círculos)
        for (int l = 0; l < Tabuleiro.TAMANHO; l++)
            for (int c = 0; c < Tabuleiro.TAMANHO; c++) {
                Posicao p = tabuleiro.getGrelha()[l][c];
                if (p.isAtacada()) desenharMarcador(l, c, p);
            }
    }

    // ── DESENHO DE NAVIO ─────────────────────────────────────────────

    private void desenharNavioImagem(Navio navio) {
        if (navio.getPosicoes().isEmpty()) return;

        var posicoes = navio.getPosicoes();
        int minL = posicoes.stream().mapToInt(Posicao::getLinha).min().orElse(0);
        int minC = posicoes.stream().mapToInt(Posicao::getColuna).min().orElse(0);
        int maxL = posicoes.stream().mapToInt(Posicao::getLinha).max().orElse(0);
        int maxC = posicoes.stream().mapToInt(Posicao::getColuna).max().orElse(0);

        boolean isHorizontal = (maxL == minL);
        int tamanho = navio.getTamanho();

        double x = OFFSET_X + minC * CELL_W + 1;
        double y = OFFSET_Y + minL * CELL_H + 1;
        double largura = isHorizontal ? CELL_W * tamanho - 2 : CELL_W - 2;
        double altura  = isHorizontal ? CELL_H - 2 : CELL_H * tamanho - 2;

        // Cor de overlay quando afundado
        if (navio.estaAfundado()) {
            Rectangle overlay_afundado = new Rectangle(x, y, largura, altura);
            overlay_afundado.setFill(Color.web("#b71c1c", 0.6));
            overlay_afundado.setMouseTransparent(true);
            overlay.getChildren().add(overlay_afundado);
            return;
        }

        Image img = getImagemNavio(navio.getTipo());
        if (img == null) {
            Rectangle rect = new Rectangle(x, y, largura, altura);
            rect.setFill(Color.web("#26a69a", 0.75));
            rect.setMouseTransparent(true);
            overlay.getChildren().add(rect);
            return;
        }

        ImageView iv = new ImageView(img);
        iv.setMouseTransparent(true);

        if (isHorizontal) {
            iv.setLayoutX(x);
            iv.setLayoutY(y);
            iv.setFitWidth(largura);
            iv.setFitHeight(altura);
            iv.setPreserveRatio(false);
        } else {
            // Vertical: rodar 90° e reposicionar
            iv.setFitWidth(altura);   // imagem "deitada" → largura = altura futura
            iv.setFitHeight(largura); // imagem "deitada" → altura = largura futura
            iv.setPreserveRatio(false);
            iv.setRotate(90);
            // Após rotação, o pivot está no centro da ImageView
            iv.setLayoutX(x + largura / 2.0 - altura / 2.0);
            iv.setLayoutY(y + altura / 2.0 - largura / 2.0);
        }

        overlay.getChildren().add(iv);
    }

    // ── MARCADOR DE ATAQUE ───────────────────────────────────────────

    private void desenharMarcador(int l, int c, Posicao p) {
        double cx = OFFSET_X + c * CELL_W + CELL_W / 2.0;
        double cy = OFFSET_Y + l * CELL_H + CELL_H / 2.0;
        double r  = Math.min(CELL_W, CELL_H) * 0.32;

        Circle circulo = new Circle(cx, cy, r);
        circulo.setMouseTransparent(true);

        if (p.temNavio()) {
            circulo.setFill(p.getNavio().estaAfundado()
                    ? Color.web("#b71c1c", 0.95)
                    : Color.web("#e53935", 0.90));
            circulo.setStroke(Color.web("#ff5252"));
        } else {
            circulo.setFill(Color.web("#e3f2fd", 0.80));
            circulo.setStroke(Color.web("#90caf9"));
        }
        circulo.setStrokeWidth(1.5);
        overlay.getChildren().add(circulo);
    }

    // ── DESATIVAR CLIQUES ────────────────────────────────────────────

    public void desativarCliques() {
        for (int l = 0; l < Tabuleiro.TAMANHO; l++) {
            for (int c = 0; c < Tabuleiro.TAMANHO; c++) {
                Rectangle r = hitAreas[l][c];
                r.setOnMouseClicked(null);
                r.setOnMouseEntered(null);
                r.setOnMouseExited(null);
                r.setFill(Color.TRANSPARENT);
                r.setStyle("-fx-cursor: default;");
            }
        }
    }

    // ── UTILITÁRIO ───────────────────────────────────────────────────

    private Image getImagemNavio(TipoNavio tipo) {
        return switch (tipo) {
            case PORTA_AVIOES     -> ImageLoader.portaAvioes();
            case NAVIO_GUERRA     -> ImageLoader.navioGuerra();
            case SUBMARINO        -> ImageLoader.submarino();
            case CONTRATORPEDEIRO -> ImageLoader.contratorpedeiro();
            case LANCHA           -> ImageLoader.lancha();
        };
    }
}