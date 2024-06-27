import java.util.Objects;

public class Verificador {

    public boolean verificarNoVacio(String texto) {
        return !Objects.equals(texto, "");
    }

    public boolean verificarTiempo(String texto) {
        try {
            Integer.parseInt(texto);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
