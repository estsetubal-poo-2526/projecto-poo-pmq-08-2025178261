package batalhanaval.view;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilitário centralizado para carregamento de imagens.
 * Faz cache das imagens já carregadas para evitar recarregamentos.
 */
public class ImageLoader {

    private static final Map<String, Image> cache = new HashMap<>();
    private static final String BASE_PATH = "/batalhanaval/";

    private ImageLoader() {}

    /**
     * Carrega uma imagem pelo nome do ficheiro.
     * Retorna null se a imagem não existir (sem crash).
     */
    public static Image get(String filename) {
        if (cache.containsKey(filename)) {
            return cache.get(filename);
        }
        try {
            var stream = ImageLoader.class.getResourceAsStream(BASE_PATH + filename);
            if (stream == null) {
                System.err.println("Imagem não encontrada: " + BASE_PATH + filename);
                return null;
            }
            Image img = new Image(stream);
            cache.put(filename, img);
            return img;
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagem: " + filename + " — " + e.getMessage());
            return null;
        }
    }

    // Atalhos para cada imagem do projeto
    public static Image menuInicial()        { return get("menuinicial.png"); }
    public static Image tabuleiros()         { return get("tabuleiros.png"); }
    public static Image vitoria()            { return get("vitoria.png"); }
    public static Image derrota()            { return get("derrota.png"); }
    public static Image portaAvioes()        { return get("portaavioes.png"); }
    public static Image lancha()             { return get("lancha.png"); }
    public static Image submarino()          { return get("submarino.png"); }
    public static Image contratorpedeiro()   { return get("contratorpedeiro.png"); }
    public static Image navioGuerra()        { return get("navioguerra.png"); }
}
