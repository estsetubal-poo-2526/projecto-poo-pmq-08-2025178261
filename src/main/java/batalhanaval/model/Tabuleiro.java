package batalhanaval.model;

import batalhanaval.exception.PosicaoInvalidaException;
import batalhanaval.exception.ColocacaoNavioException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa o tabuleiro do jogo Batalha Naval.
 * É composto por uma grelha de posições (composição) e gere a colocação
 * e o estado dos navios.
 */
public class Tabuleiro {

    public static final int TAMANHO = 10;

    private final Posicao[][] grelha;
    private final List<Navio> navios;

    public Tabuleiro() {
        this.grelha = new Posicao[TAMANHO][TAMANHO];
        this.navios = new ArrayList<>();
        inicializarGrelha();
    }

    private void inicializarGrelha() {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                grelha[i][j] = new Posicao(i, j);
            }
        }
    }

    /**
     * Obtém a posição na grelha dadas linha e coluna.
     */
    public Posicao getPosicao(int linha, int coluna) throws PosicaoInvalidaException {
        if (linha < 0 || linha >= TAMANHO || coluna < 0 || coluna >= TAMANHO) {
            throw new PosicaoInvalidaException(linha, coluna, TAMANHO);
        }
        return grelha[linha][coluna];
    }

    /**
     * Coloca um navio no tabuleiro.
     * @param navio   o navio a colocar
     * @param linha   linha inicial
     * @param coluna  coluna inicial
     * @param horizontal true se horizontal, false se vertical
     */
    public void colocarNavio(Navio navio, int linha, int coluna, boolean horizontal)
            throws PosicaoInvalidaException, ColocacaoNavioException {

        int tamanho = navio.getTamanho();

        // Validar limites
        if (horizontal && coluna + tamanho > TAMANHO) {
            throw new ColocacaoNavioException("Navio ultrapassa os limites do tabuleiro na horizontal.");
        }
        if (!horizontal && linha + tamanho > TAMANHO) {
            throw new ColocacaoNavioException("Navio ultrapassa os limites do tabuleiro na vertical.");
        }

        // Validar sobreposição
        for (int i = 0; i < tamanho; i++) {
            int l = horizontal ? linha : linha + i;
            int c = horizontal ? coluna + i : coluna;
            Posicao p = getPosicao(l, c);
            if (p.temNavio()) {
                throw new ColocacaoNavioException(
                        "Posição [" + l + "," + c + "] já está ocupada por outro navio.");
            }
        }

        // Colocar navio
        for (int i = 0; i < tamanho; i++) {
            int l = horizontal ? linha : linha + i;
            int c = horizontal ? coluna + i : coluna;
            navio.adicionarPosicao(getPosicao(l, c));
        }
        navios.add(navio);
    }

    /**
     * Ataca uma posição do tabuleiro.
     * @return o resultado do ataque
     */
    public ResultadoAtaque atacar(int linha, int coluna) throws PosicaoInvalidaException {
        Posicao posicao = getPosicao(linha, coluna);

        if (posicao.isAtacada()) {
            throw new IllegalStateException("A posição [" + linha + "," + coluna + "] já foi atacada.");
        }

        boolean acertou = posicao.atacar();

        if (!acertou) {
            return ResultadoAtaque.AGUA;
        }

        Navio navio = posicao.getNavio();
        if (navio.estaAfundado()) {
            return ResultadoAtaque.AFUNDADO;
        }
        return ResultadoAtaque.ACERTO;
    }

    /**
     * Verifica se todos os navios foram afundados (condição de vitória).
     */
    public boolean todosNaviosAfundados() {
        return navios.stream().allMatch(Navio::estaAfundado);
    }

    public List<Navio> getNavios() {
        return Collections.unmodifiableList(navios);
    }

    public int getNumNavios() {
        return navios.size();
    }

    public int getNumNaviosAfundados() {
        return (int) navios.stream().filter(Navio::estaAfundado).count();
    }

    public Posicao[][] getGrelha() {
        return grelha;
    }

    @Override
    public String toString() {
        return "Tabuleiro [navios=" + navios.size()
                + ", afundados=" + getNumNaviosAfundados() + "]";
    }
}