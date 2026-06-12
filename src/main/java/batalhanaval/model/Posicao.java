package batalhanaval.model;

/**
 * Representa uma posição (célula) no tabuleiro do jogo Batalha Naval.
 * Cada posição é identificada por uma linha e uma coluna,
 * e guarda o estado de ataque e a referência ao navio (se existir).
 */
public class Posicao {

    private final int linha;
    private final int coluna;
    private boolean atacada;
    private Navio navio; // null se a posição não tiver navio

    public Posicao(int linha, int coluna) {
        if (linha < 0 || coluna < 0) {
            throw new IllegalArgumentException("Linha e coluna devem ser valores não negativos.");
        }
        this.linha = linha;
        this.coluna = coluna;
        this.atacada = false;
        this.navio = null;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    public boolean isAtacada() {
        return atacada;
    }

    public void setAtacada(boolean atacada) {
        this.atacada = atacada;
    }

    public boolean temNavio() {
        return navio != null;
    }

    public Navio getNavio() {
        return navio;
    }

    public void setNavio(Navio navio) {
        this.navio = navio;
    }

    /**
     * Ataca esta posição.
     * @return true se existia um navio aqui, false caso contrário (água)
     */
    public boolean atacar() {
        if (atacada) {
            throw new IllegalStateException("Esta posição já foi atacada.");
        }
        this.atacada = true;
        if (navio != null) {
            navio.registarAcerto();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Posicao[" + linha + "," + coluna + "]"
                + (atacada ? " atacada" : "")
                + (navio != null ? " com navio" : "");
    }
}