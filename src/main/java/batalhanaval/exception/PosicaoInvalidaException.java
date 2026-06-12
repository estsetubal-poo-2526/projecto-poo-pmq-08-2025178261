package batalhanaval.exception;

/**
 * Exceção lançada quando se tenta aceder a uma posição
 * fora dos limites do tabuleiro.
 */
public class PosicaoInvalidaException extends Exception {

    private final int linha;
    private final int coluna;

    public PosicaoInvalidaException(int linha, int coluna, int tamanhoMax) {
        super("Posição inválida: [" + linha + ", " + coluna + "]. "
                + "O tabuleiro tem dimensão " + tamanhoMax + "x" + tamanhoMax + ".");
        this.linha = linha;
        this.coluna = coluna;
    }

    public PosicaoInvalidaException(String mensagem) {
        super(mensagem);
        this.linha = -1;
        this.coluna = -1;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }
}