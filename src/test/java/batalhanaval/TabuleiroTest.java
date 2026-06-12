package batalhanaval;

import batalhanaval.exception.ColocacaoNavioException;
import batalhanaval.exception.PosicaoInvalidaException;
import batalhanaval.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe Tabuleiro")
class TabuleiroTest {

    private Tabuleiro tabuleiro;

    @BeforeEach
    void setUp() {
        tabuleiro = new Tabuleiro();
    }

    @Test
    @DisplayName("Tabuleiro é inicializado vazio e com tamanho correto")
    void testInicializacao() {
        assertEquals(0, tabuleiro.getNumNavios());
        assertEquals(Tabuleiro.TAMANHO, 10);
        assertFalse(tabuleiro.todosNaviosAfundados()); // sem navios = false
    }

    @Test
    @DisplayName("Colocar navio horizontal sem sobreposição")
    void testColocarNavioHorizontal() throws Exception {
        Navio navio = new Navio(TipoNavio.LANCHA);
        tabuleiro.colocarNavio(navio, 0, 0, true);
        assertEquals(1, tabuleiro.getNumNavios());
        assertTrue(tabuleiro.getGrelha()[0][0].temNavio());
        assertTrue(tabuleiro.getGrelha()[0][1].temNavio());
    }

    @Test
    @DisplayName("Colocar navio vertical sem sobreposição")
    void testColocarNavioVertical() throws Exception {
        Navio navio = new Navio(TipoNavio.SUBMARINO);
        tabuleiro.colocarNavio(navio, 2, 3, false);
        assertTrue(tabuleiro.getGrelha()[2][3].temNavio());
        assertTrue(tabuleiro.getGrelha()[3][3].temNavio());
        assertTrue(tabuleiro.getGrelha()[4][3].temNavio());
    }

    @Test
    @DisplayName("Colocar navio fora dos limites lança exceção")
    void testColocarNavioForaLimites() {
        Navio navio = new Navio(TipoNavio.PORTA_AVIOES); // tamanho 5
        assertThrows(ColocacaoNavioException.class,
                () -> tabuleiro.colocarNavio(navio, 0, 8, true)); // 8+5 > 10
    }

    @Test
    @DisplayName("Colocar navio com sobreposição lança exceção")
    void testColocarNavioSobreposicao() throws Exception {
        Navio n1 = new Navio(TipoNavio.LANCHA);
        tabuleiro.colocarNavio(n1, 0, 0, true);
        Navio n2 = new Navio(TipoNavio.LANCHA);
        assertThrows(ColocacaoNavioException.class,
                () -> tabuleiro.colocarNavio(n2, 0, 0, true));
    }

    @Test
    @DisplayName("Ataque a água retorna AGUA")
    void testAtaqueAgua() throws Exception {
        ResultadoAtaque resultado = tabuleiro.atacar(5, 5);
        assertEquals(ResultadoAtaque.AGUA, resultado);
    }

    @Test
    @DisplayName("Ataque a navio retorna ACERTO")
    void testAtaqueAcerto() throws Exception {
        Navio navio = new Navio(TipoNavio.PORTA_AVIOES); // tamanho 5
        tabuleiro.colocarNavio(navio, 0, 0, true);
        ResultadoAtaque resultado = tabuleiro.atacar(0, 0);
        assertEquals(ResultadoAtaque.ACERTO, resultado);
    }

    @Test
    @DisplayName("Afundar navio completamente retorna AFUNDADO")
    void testNavioAfundado() throws Exception {
        Navio navio = new Navio(TipoNavio.LANCHA); // tamanho 2
        tabuleiro.colocarNavio(navio, 1, 1, true);
        tabuleiro.atacar(1, 1);
        ResultadoAtaque resultado = tabuleiro.atacar(1, 2);
        assertEquals(ResultadoAtaque.AFUNDADO, resultado);
    }

    @Test
    @DisplayName("Atacar posição já atacada lança exceção")
    void testAtaqueRepetido() throws Exception {
        tabuleiro.atacar(0, 0);
        assertThrows(IllegalStateException.class, () -> tabuleiro.atacar(0, 0));
    }

    @Test
    @DisplayName("Atacar posição inválida lança PosicaoInvalidaException")
    void testAtaquePosicaoInvalida() {
        assertThrows(PosicaoInvalidaException.class, () -> tabuleiro.atacar(-1, 0));
        assertThrows(PosicaoInvalidaException.class, () -> tabuleiro.atacar(0, 10));
    }

    @Test
    @DisplayName("todosNaviosAfundados retorna true quando todos afundados")
    void testTodosNaviosAfundados() throws Exception {
        Navio navio = new Navio(TipoNavio.LANCHA);
        tabuleiro.colocarNavio(navio, 0, 0, true);
        tabuleiro.atacar(0, 0);
        tabuleiro.atacar(0, 1);
        assertTrue(tabuleiro.todosNaviosAfundados());
    }
}