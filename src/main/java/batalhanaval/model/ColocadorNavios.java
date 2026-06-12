package batalhanaval.model;

import batalhanaval.exception.ColocacaoNavioException;
import batalhanaval.exception.PosicaoInvalidaException;

import java.util.Random;

/**
 * Utilitário responsável pela colocação aleatória de navios no tabuleiro.
 * Separado do modelo para manter responsabilidades únicas (SRP).
 */
public class ColocadorNavios {

    private ColocadorNavios() {
        // Classe utilitária, não instanciável
    }

    /**
     * Coloca todos os navios padrão do jogo de forma aleatória no tabuleiro.
     */
    public static void colocarNaviosAleatorio(Tabuleiro tabuleiro, Random random)
            throws PosicaoInvalidaException, ColocacaoNavioException {

        TipoNavio[] tipos = TipoNavio.values();

        for (TipoNavio tipo : tipos) {
            Navio navio = new Navio(tipo);
            boolean colocado = false;
            int tentativas = 0;

            while (!colocado && tentativas < 200) {
                int linha = random.nextInt(Tabuleiro.TAMANHO);
                int coluna = random.nextInt(Tabuleiro.TAMANHO);
                boolean horizontal = random.nextBoolean();

                try {
                    tabuleiro.colocarNavio(navio, linha, coluna, horizontal);
                    colocado = true;
                } catch (ColocacaoNavioException | PosicaoInvalidaException e) {
                    // Tentar outra posição
                    navio = new Navio(tipo); // reset navio
                }
                tentativas++;
            }

            if (!colocado) {
                throw new ColocacaoNavioException(
                        "Não foi possível colocar o navio " + tipo.getNome() + " após 200 tentativas.");
            }
        }
    }
}