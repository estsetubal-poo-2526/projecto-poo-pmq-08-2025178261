package batalhanaval.model;

/**
 * Representa o resultado de um ataque a uma posição do tabuleiro.
 */
public enum ResultadoAtaque {
    AGUA("Água! Nenhum navio nesta posição."),
    ACERTO("Acerto! Atingiu um navio!"),
    AFUNDADO("Navio afundado!");

    private final String mensagem;

    ResultadoAtaque(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    @Override
    public String toString() {
        return mensagem;
    }
}