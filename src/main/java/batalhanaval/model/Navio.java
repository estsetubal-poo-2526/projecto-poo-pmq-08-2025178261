package batalhanaval.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa um navio no jogo Batalha Naval.
 * Um navio ocupa várias posições consecutivas no tabuleiro
 * e é afundado quando todas as suas posições são atingidas.
 */
public class Navio {

    private final TipoNavio tipo;
    private final List<Posicao> posicoes;
    private int acertos;

    public Navio(TipoNavio tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo de navio não pode ser nulo.");
        }
        this.tipo = tipo;
        this.posicoes = new ArrayList<>();
        this.acertos = 0;
    }

    public TipoNavio getTipo() {
        return tipo;
    }

    public String getNome() {
        return tipo.getNome();
    }

    public int getTamanho() {
        return tipo.getTamanho();
    }

    public List<Posicao> getPosicoes() {
        return Collections.unmodifiableList(posicoes);
    }

    public void adicionarPosicao(Posicao posicao) {
        if (posicao == null) {
            throw new IllegalArgumentException("A posição não pode ser nula.");
        }
        if (posicoes.size() >= tipo.getTamanho()) {
            throw new IllegalStateException("O navio " + tipo.getNome() + " já atingiu o seu tamanho máximo.");
        }
        posicoes.add(posicao);
        posicao.setNavio(this);
    }

    /**
     * Regista um acerto no navio.
     */
    public void registarAcerto() {
        acertos++;
    }

    /**
     * Verifica se o navio foi completamente afundado.
     * @return true se todos os segmentos foram atingidos
     */
    public boolean estaAfundado() {
        return acertos >= tipo.getTamanho();
    }

    public int getAcertos() {
        return acertos;
    }

    @Override
    public String toString() {
        return tipo.getNome() + " [" + acertos + "/" + tipo.getTamanho() + "]"
                + (estaAfundado() ? " AFUNDADO" : "");
    }
}