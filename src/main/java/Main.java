import LogicaNegocios.Calendario;
import LogicaNegocios.Frecuencia;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.File;
import java.time.LocalDateTime;


public class Main extends Application {

    @Override
    public void start(Stage escenario) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Escena_principal.fxml"));
        Calendario modelo = new Calendario();
        try {
            modelo.restaurar();
        } catch (Exception e) {
            File tareas = new File(System.getProperty("user.dir")+File.separator+"tareas.xml");
            tareas.createNewFile();
            File eventos = new File(System.getProperty("user.dir")+File.separator+"eventos.xml");
            eventos.createNewFile();
            File eventosRepetibles = new File(System.getProperty("user.dir")+File.separator+"eventosRepetibles.xml");
            eventosRepetibles.createNewFile();
        }
        LocalDateTime fechaDefault = LocalDateTime.now().toLocalDate().atStartOfDay();
        Controlador controlador = new Controlador(modelo, fechaDefault, Frecuencia.MENSUAL);
        loader.setController(controlador);
        BorderPane root = loader.load();
        controlador.inicializar();
        escenario.setScene(new Scene(root));
        escenario.show();
    }
}
