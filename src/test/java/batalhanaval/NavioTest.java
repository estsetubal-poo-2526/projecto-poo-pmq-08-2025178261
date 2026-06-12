package batalhanaval;

import batalhanaval.model.Navio;
import batalhanaval.model.Posicao;
import batalhanaval.model.TipoNavio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe Navio")
class NavioTest {

    @Test
    @DisplayName("Navio é criado com tipo e tamanho corretos")
    void testCriacaoNavio() {
        Navio navio = new Navio(TipoNavio.SUBMARINO);
        assertEquals(TipoNavio.SUBMARINO, navio.getTipo());
        assertEquals(3, navio.getTamanho());
        assertFalse(navio.estaAfundado());
        assertEquals(0, navio.getAcertos());
    }

    @Test
    @DisplayName("Navio afunda após todos os acertos")
    void testNavioAfundaCorretamente() {
        Navio navio = new Navio(TipoNavio.LANCHA); // tamanho 2
        assertFalse(navio.estaAfundado());
        navio.registarAcerto();
        assertFalse(navio.estaAfundado());
        navio.registarAcerto();
        assertTrue(navio.estaAfundado());
    }

    @Test
    @DisplayName("Adicionar posição ao navio funciona corretamente")
    void testAdicionarPosicao() {
        Navio navio = new Navio(TipoNavio.LANCHA);
        Posicao p1 = new Posicao(0, 0);
        Posicao p2 = new Posicao(0, 1);
        navio.adicionarPosicao(p1);
        navio.adicionarPosicao(p2);
        assertEquals(2, navio.getPosicoes().size());
    }

    @Test
    @DisplayName("Adicionar posição em excesso lança exceção")
    void testAdicionarPosicaoEmExcesso() {
        Navio navio = new Navio(TipoNavio.LANCHA); // tamanho 2
        navio.adicionarPosicao(new Posicao(0, 0));
        navio.adicionarPosicao(new Posicao(0, 1));
        assertThrows(IllegalStateException.class, () -> navio.adicionarPosicao(new Posicao(0, 2)));
    }

    @Test
    @DisplayName("Criar navio com tipo nulo lança exceção")
    void testTipoNulo() {
        assertThrows(IllegalArgumentException.class, () -> new Navio(null));
    }

    @Test
    @DisplayName("toString inclui estado afundado quando aplicável")
    void testToString() {
        Navio navio = new Navio(TipoNavio.LANCHA);
        navio.registarAcerto();
        navio.registarAcerto();
        assertTrue(navio.toString().contains("AFUNDADO"));
    }
}