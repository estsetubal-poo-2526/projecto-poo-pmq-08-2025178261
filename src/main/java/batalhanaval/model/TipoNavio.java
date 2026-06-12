package batalhanaval.model;

/**
 * Tipos de navios disponíveis no jogo, com nome e tamanho associados.
 */
public enum TipoNavio {
    PORTA_AVIOES("Porta-Aviões", 5),
    NAVIO_GUERRA("Navio de Guerra", 4),
    SUBMARINO("Submarino", 3),
    CONTRATORPEDEIRO("Contratorpedeiro", 3),
    LANCHA("Lancha", 2);

    private final String nome;
    private final int tamanho;

    TipoNavio(String nome, int tamanho) {
        this.nome = nome;
        this.tamanho = tamanho;
    }

    public String getNome() {
        return nome;
    }

    public int getTamanho() {
        return tamanho;
    }

    @Override
    public String toString() {
        return nome + " (tamanho " + tamanho + ")";
    }
}