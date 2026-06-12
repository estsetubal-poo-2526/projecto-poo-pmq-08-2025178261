package batalhanaval;

import batalhanaval.exception.ColocacaoNavioException;
import batalhanaval.exception.PosicaoInvalidaException;
import batalhanaval.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe Game")
class GameTest {

    private Game game;

    @BeforeEach
    void setUp() throws Exception {
        game = new Game("Rodrigo");
        game.iniciar(); // coloca navios da CPU
    }

    @Test
    @DisplayName("Game é criado no estado NAO_INICIADO")
    void testEstadoInicial() {
        Game g = new Game("Teste");
        assertEquals(Game.Estado.NAO_INICIADO, g.getEstado());
        assertNull(g.getVencedor());
    }

    @Test
    @DisplayName("Após iniciar, estado passa a COLOCACAO_NAVIOS")
    void testEstadoAposIniciar() {
        assertEquals(Game.Estado.COLOCACAO_NAVIOS, game.getEstado());
    }

    @Test
    @DisplayName("CPU tem navios colocados após iniciar")
    void testCPUTemNavios() {
        assertTrue(game.getCpu().getTabuleiro().getNumNavios() > 0);
    }

    @Test
    @DisplayName("Iniciar batalha sem navios do player lança exceção")
    void testIniciarBatalhaSemNaviosPlayer() {
        assertThrows(IllegalStateException.class, () -> game.iniciarBatalha());
    }

    @Test
    @DisplayName("Atacar CPU antes de iniciar batalha lança exceção")
    void testAtacarCPUAntesDeIniciar() {
        assertThrows(IllegalStateException.class, () -> game.atacarCPU(0, 0));
    }

    @Test
    @DisplayName("Jogo funciona corretamente após setup completo")
    void testFluxoCompletoSetup() throws Exception {
        // Colocar navios do player
        ColocadorNavios.colocarNaviosAleatorio(game.getPlayer().getTabuleiro(), new java.util.Random());
        game.iniciarBatalha();

        assertEquals(Game.Estado.EM_JOGO, game.getEstado());
        assertFalse(game.estaTerminado());
    }

    @Test
    @DisplayName("Ataque ao player pela CPU processa corretamente")
    void testTurnoCPU() throws Exception {
        ColocadorNavios.colocarNaviosAleatorio(game.getPlayer().getTabuleiro(), new java.util.Random());
        game.iniciarBatalha();

        int[] resultado = game.turnosCPU();
        // resultado = [linha, coluna, ordinal do ResultadoAtaque]
        assertNotNull(resultado);
        assertEquals(3, resultado.length);
        assertTrue(resultado[0] >= 0 && resultado[0] < Tabuleiro.TAMANHO);
        assertTrue(resultado[1] >= 0 && resultado[1] < Tabuleiro.TAMANHO);
    }

    @Test
    @DisplayName("Iniciar jogo duas vezes lança exceção")
    void testIniciarDuasVezes() {
        assertThrows(IllegalStateException.class, () -> game.iniciar());
    }

    @Test
    @DisplayName("Player e CPU têm nomes corretos")
    void testNomesJogadores() {
        assertEquals("Rodrigo", game.getPlayer().getNome());
        assertEquals("CPU", game.getCpu().getNome());
    }
}