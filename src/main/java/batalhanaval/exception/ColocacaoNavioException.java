package batalhanaval.exception;

/**
 * Exceção lançada quando um navio não pode ser colocado no tabuleiro
 * por sobreposição com outro navio ou por ultrapassar os limites.
 */
public class ColocacaoNavioException extends Exception {

    public ColocacaoNavioException(String mensagem) {
        super(mensagem);
    }

    public ColocacaoNavioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}