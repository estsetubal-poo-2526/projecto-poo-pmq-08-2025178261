package batalhanaval.model;

import batalhanaval.exception.PosicaoInvalidaException;

/**
 * Classe central do jogo que coordena o fluxo da partida.
 * É responsável por gerir os dois jogadores, controlar os turnos
 * e verificar as condições de vitória/derrota.
 *
 * Relações:
 *   - Game associa-se a dois Jogadores (Player e CPU)
 *   - Não cria diretamente os tabuleiros (cada Jogador possui o seu)
 */
public class Game {

    public enum Estado {
        NAO_INICIADO,
        COLOCACAO_NAVIOS,
        EM_JOGO,
        TERMINADO
    }

    private final Player player;
    private final CPU cpu;
    private Estado estado;
    private Jogador vencedor;

    public Game(String nomePlayer) {
        this.player = new Player(nomePlayer);
        this.cpu = new CPU("CPU");
        this.estado = Estado.NAO_INICIADO;
        this.vencedor = null;
    }

    /**
     * Inicia o jogo: coloca os navios da CPU automaticamente
     * e prepara o estado para a colocação de navios do player.
     */
    public void iniciar() throws Exception {
        if (estado != Estado.NAO_INICIADO) {
            throw new IllegalStateException("O jogo já foi iniciado.");
        }
        cpu.colocarNavios();
        estado = Estado.COLOCACAO_NAVIOS;
    }

    /**
     * Chamado após o player terminar de colocar os navios.
     */
    public void iniciarBatalha() {
        if (estado != Estado.COLOCACAO_NAVIOS) {
            throw new IllegalStateException("Os navios ainda não foram colocados.");
        }
        if (player.getTabuleiro().getNumNavios() == 0) {
            throw new IllegalStateException("O player ainda não colocou os seus navios.");
        }
        estado = Estado.EM_JOGO;
    }

    /**
     * Processa o ataque do player a uma posição do tabuleiro da CPU.
     * @return resultado do ataque
     */
    public ResultadoAtaque atacarCPU(int linha, int coluna) throws PosicaoInvalidaException {
        validarEstadoJogo();
        ResultadoAtaque resultado = cpu.getTabuleiro().atacar(linha, coluna);
        verificarFimJogo();
        return resultado;
    }

    /**
     * Processa o ataque da CPU ao tabuleiro do player.
     * @return array: [linha, coluna, ordinal do ResultadoAtaque]
     */
    public int[] turnosCPU() throws PosicaoInvalidaException {
        validarEstadoJogo();
        int[] posicao = cpu.escolherAtaque(player.getTabuleiro());
        ResultadoAtaque resultado = player.getTabuleiro().atacar(posicao[0], posicao[1]);
        cpu.notificarResultado(posicao, resultado);
        verificarFimJogo();
        return new int[]{posicao[0], posicao[1], resultado.ordinal()};
    }

    private void validarEstadoJogo() {
        if (estado != Estado.EM_JOGO) {
            throw new IllegalStateException("O jogo não está em curso.");
        }
    }

    private void verificarFimJogo() {
        if (cpu.perdeu()) {
            vencedor = player;
            estado = Estado.TERMINADO;
        } else if (player.perdeu()) {
            vencedor = cpu;
            estado = Estado.TERMINADO;
        }
    }

    public boolean estaTerminado() {
        return estado == Estado.TERMINADO;
    }

    public Jogador getVencedor() {
        return vencedor;
    }

    public Player getPlayer() {
        return player;
    }

    public CPU getCpu() {
        return cpu;
    }

    public Estado getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return "Game [estado=" + estado + ", player=" + player + ", cpu=" + cpu + "]";
    }
}