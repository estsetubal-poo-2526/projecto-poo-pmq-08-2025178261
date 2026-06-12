package batalhanaval.model;

import batalhanaval.exception.ColocacaoNavioException;
import batalhanaval.exception.PosicaoInvalidaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Representa o adversário automático (Computador).
 * Herda de Jogador e implementa uma estratégia de ataque inteligente:
 *   1. Se houver um acerto anterior sem afundar, ataca posições adjacentes.
 *   2. Caso contrário, escolhe uma posição aleatória ainda não atacada.
 *
 * Polimorfismo: este comportamento é transparente para o Game,
 * que trata Player e CPU uniformemente como Jogador.
 */
public class CPU extends Jogador {

    private final Random random;
    // Posição do último acerto ainda não afundado (estratégia hunt)
    private int[] ultimoAcerto;

    public CPU(String nome) {
        super(nome);
        this.random = new Random();
        this.ultimoAcerto = null;
    }

    /**
     * Estratégia de ataque: hunt (aleatório) + target (acerto consecutivo).
     */
    @Override
    public int[] escolherAtaque(Tabuleiro tabuleiroAdversario) throws PosicaoInvalidaException {
        if (ultimoAcerto != null) {
            int[] ataque = escolherAdjacenteNaoAtacado(tabuleiroAdversario, ultimoAcerto);
            if (ataque != null) {
                return ataque;
            }
            // Não há adjacentes disponíveis, reinicia hunt
            ultimoAcerto = null;
        }
        return escolherPosicaoAleatoria(tabuleiroAdversario);
    }

    /**
     * Chamado pelo Game para notificar o resultado do ataque da CPU.
     */
    public void notificarResultado(int[] posicao, ResultadoAtaque resultado) {
        if (resultado == ResultadoAtaque.ACERTO) {
            ultimoAcerto = posicao;
        } else if (resultado == ResultadoAtaque.AFUNDADO) {
            ultimoAcerto = null;
        }
    }

    private int[] escolherAdjacenteNaoAtacado(Tabuleiro tabuleiro, int[] centro) {
        int[][] direcoes = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : direcoes) {
            int l = centro[0] + dir[0];
            int c = centro[1] + dir[1];
            try {
                Posicao p = tabuleiro.getPosicao(l, c);
                if (!p.isAtacada()) {
                    return new int[]{l, c};
                }
            } catch (PosicaoInvalidaException e) {
                // Fora dos limites, ignorar
            }
        }
        return null;
    }

    private int[] escolherPosicaoAleatoria(Tabuleiro tabuleiro) {
        List<int[]> disponiveis = new ArrayList<>();
        for (int i = 0; i < Tabuleiro.TAMANHO; i++) {
            for (int j = 0; j < Tabuleiro.TAMANHO; j++) {
                try {
                    if (!tabuleiro.getPosicao(i, j).isAtacada()) {
                        disponiveis.add(new int[]{i, j});
                    }
                } catch (PosicaoInvalidaException e) {
                    // não deve acontecer
                }
            }
        }
        if (disponiveis.isEmpty()) {
            throw new IllegalStateException("Não existem posições disponíveis para atacar.");
        }
        return disponiveis.get(random.nextInt(disponiveis.size()));
    }

    /**
     * Coloca os navios da CPU automaticamente de forma aleatória.
     */
    @Override
    public void colocarNavios() throws PosicaoInvalidaException, ColocacaoNavioException {
        ColocadorNavios.colocarNaviosAleatorio(tabuleiro, random);
    }

    @Override
    public String toString() {
        return "CPU: " + super.toString();
    }
}