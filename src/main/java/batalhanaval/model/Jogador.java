package batalhanaval.model;

import batalhanaval.exception.PosicaoInvalidaException;
import batalhanaval.exception.ColocacaoNavioException;

/**
 * Classe abstrata que representa um participante no jogo Batalha Naval.
 *
 * Hierarquia de herança:
 *   Jogador (abstrato)
 *   ├── Player  (jogador humano)
 *   └── CPU     (jogador automático)
 *
 * Aplica os conceitos de:
 *   - Herança (extends)
 *   - Abstração (classe abstrata + método abstrato escolherAtaque)
 *   - Encapsulamento (atributos privados/protected)
 */
public abstract class Jogador {

    protected final String nome;
    protected final Tabuleiro tabuleiro;

    public Jogador(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do jogador não pode ser vazio.");
        }
        this.nome = nome;
        this.tabuleiro = new Tabuleiro();
    }

    public String getNome() {
        return nome;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    /**
     * Método abstrato: define a forma como o jogador escolhe a posição de ataque.
     * Cada subclasse implementa a sua própria estratégia.
     *
     * @param tabuleiroAdversario o tabuleiro do adversário a atacar
     * @return array [linha, coluna] da posição escolhida
     */
    public abstract int[] escolherAtaque(Tabuleiro tabuleiroAdversario)
            throws PosicaoInvalidaException;

    /**
     * Verifica se o jogador perdeu (todos os seus navios foram afundados).
     */
    public boolean perdeu() {
        return tabuleiro.todosNaviosAfundados();
    }

    /**
     * Coloca todos os navios do jogador no tabuleiro.
     * Cada subclasse decide como colocar (manual ou automático).
     */
    public abstract void colocarNavios()
            throws PosicaoInvalidaException, ColocacaoNavioException;

    @Override
    public String toString() {
        return nome + " [" + tabuleiro.getNumNaviosAfundados()
                + "/" + tabuleiro.getNumNavios() + " navios afundados]";
    }
}