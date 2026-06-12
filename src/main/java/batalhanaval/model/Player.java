package batalhanaval.model;

import batalhanaval.exception.PosicaoInvalidaException;
import batalhanaval.exception.ColocacaoNavioException;

/**
 * Representa o jogador humano.
 * A escolha de ataque e a colocação de navios são feitas através da
 * interface gráfica (controlada pelo GameController).
 *
 * Herda de Jogador e especializa o comportamento onde necessário.
 */
public class Player extends Jogador {

    public Player(String nome) {
        super(nome);
    }

    /**
     * O Player humano não usa lógica automática para o ataque —
     * a posição é passada diretamente pelo controlador (GameController).
     * Este método lança UnsupportedOperationException para sinalizar
     * que o ataque deve ser despoletado pela interface gráfica.
     */
    @Override
    public int[] escolherAtaque(Tabuleiro tabuleiroAdversario) throws PosicaoInvalidaException {
        throw new UnsupportedOperationException(
                "O Player humano escolhe o ataque através da interface gráfica.");
    }

    /**
     * Coloca todos os navios do player — chamado pelo controlador
     * após o utilizador definir as posições manualmente ou via colocação automática.
     */
    @Override
    public void colocarNavios() throws PosicaoInvalidaException, ColocacaoNavioException {
        // Delegado ao GameController para colocação manual.
        // Para colocação automática rápida, usa-se o utilitário ColocadorNavios.
    }

    @Override
    public String toString() {
        return "Player: " + super.toString();
    }
}