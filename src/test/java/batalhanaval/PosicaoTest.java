package batalhanaval;

import batalhanaval.model.Navio;
import batalhanaval.model.Posicao;
import batalhanaval.model.TipoNavio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe Posicao")
class PosicaoTest {

    private Posicao posicao;

    @BeforeEach
    void setUp() {
        posicao = new Posicao(3, 5);
    }

    @Test
    @DisplayName("Posição é criada com os valores corretos")
    void testCriacaoPositiva() {
        assertEquals(3, posicao.getLinha());
        assertEquals(5, posicao.getColuna());
        assertFalse(posicao.isAtacada());
        assertFalse(posicao.temNavio());
    }

    @Test
    @DisplayName("Ataque a posição sem navio retorna false (água)")
    void testAtaqueSemNavio() {
        boolean resultado = posicao.atacar();
        assertFalse(resultado);
        assertTrue(posicao.isAtacada());
    }

    @Test
    @DisplayName("Ataque a posição com navio retorna true")
    void testAtaqueComNavio() {
        Navio navio = new Navio(TipoNavio.LANCHA);
        posicao.setNavio(navio);
        boolean resultado = posicao.atacar();
        assertTrue(resultado);
        assertTrue(posicao.isAtacada());
    }

    @Test
    @DisplayName("Atacar posição já atacada lança exceção")
    void testAtaqueRepetidoLancaExcecao() {
        posicao.atacar();
        assertThrows(IllegalStateException.class, () -> posicao.atacar());
    }

    @Test
    @DisplayName("Posição com coordenadas negativas lança exceção")
    void testCoordenadasNegativas() {
        assertThrows(IllegalArgumentException.class, () -> new Posicao(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Posicao(0, -1));
    }
}