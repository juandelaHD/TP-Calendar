import LogicaNegocios.Alarma;
import LogicaNegocios.TipoDeAlarma;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.util.List;


public class ControladorAlarma {
    Verificador verificador;

    List<AlarmaTupla> alarmasDelSuceso;

    LocalDateTime fechaSuceso;

    @FXML
    private BorderPane contenedor;
    @FXML
    private TextField TextFieldMinutosAntes;
    @FXML
    private TextField TextFieldHoras;
    @FXML
    private TextField TextFieldMinutos;
    @FXML
    private CheckBox CheckBoxEsDefinida;
    @FXML
    private ComboBox ComboBoxTipoDeAlarma;
    @FXML
    private DatePicker FechaPickerAlarma;
    @FXML
    private Button AgregarAlarma;
    @FXML
    private Button CancelarAlarma;

    public ControladorAlarma(List<AlarmaTupla> alarmas) {
        this.verificador = new Verificador();
        this.alarmasDelSuceso = alarmas;

    }

    public void inicializar(LocalDateTime fechaSuceso) {
        this.fechaSuceso = fechaSuceso;
        this.ComboBoxTipoDeAlarma.getItems().addAll(TipoDeAlarma.values());
        FechaPickerAlarma.disableProperty().bind(CheckBoxEsDefinida.selectedProperty().not());
        TextFieldHoras.disableProperty().bind(CheckBoxEsDefinida.selectedProperty().not());
        TextFieldMinutos.disableProperty().bind(CheckBoxEsDefinida.selectedProperty().not());
        TextFieldMinutosAntes.disableProperty().bind(CheckBoxEsDefinida.selectedProperty());
        configurarAgregarAlarma();
        configurarCancelarAlarma();
    }

    public void configurarAgregarAlarma() {
        this.AgregarAlarma.setOnAction(event -> {
            if (ComboBoxTipoDeAlarma.getSelectionModel().getSelectedItem() == null) {
                dispararAlerta("Debes elegir un tipo de notificacion para tu alarma.");
                return;
            }
            TipoDeAlarma tipoDeAlarma = (TipoDeAlarma) ComboBoxTipoDeAlarma.getSelectionModel().getSelectedItem();
            int minutosAntes = 0;
            if (CheckBoxEsDefinida.selectedProperty().getValue()) {
                try {
                    int horas = Integer.parseInt(TextFieldHoras.getText());
                    int minutos = Integer.parseInt(TextFieldMinutos.getText());
                    LocalDateTime fechaAlarma = FechaPickerAlarma.getValue().atTime(horas, minutos);
                } catch (Exception e) {
                    if (!verificador.verificarNoVacio(TextFieldHoras.getText()) || !verificador.verificarNoVacio(TextFieldMinutos.getText()) || !verificador.verificarTiempo(TextFieldHoras.getText()) || !verificador.verificarTiempo(TextFieldMinutos.getText()) || !verificador.verificarNoVacio(TextFieldHoras.getText()))  {
                        dispararAlerta("Debes elegir una hora y minutos para tu alarma.");
                    } else {
                        dispararAlerta("Debes elegir una fecha para tu alarma.");
                    }
                    return;
                }
            } else if (!CheckBoxEsDefinida.selectedProperty().getValue()) {
                try {
                    minutosAntes = Integer.parseInt(TextFieldMinutosAntes.getText());
                } catch (Exception e) {
                    dispararAlerta("Debes elegir una cantidad de minutos para tu alarma.");
                    return;
                }
            }
            AlarmaTupla alarmaParaAgregar;
            if (CheckBoxEsDefinida.selectedProperty().getValue()){
                alarmaParaAgregar = new AlarmaTupla(new Alarma((TipoDeAlarma) ComboBoxTipoDeAlarma.getSelectionModel().getSelectedItem(), FechaPickerAlarma.getValue().atTime(Integer.parseInt(TextFieldHoras.getText()), Integer.parseInt(TextFieldMinutos.getText()))), "Definida");
            } else {
                alarmaParaAgregar = new AlarmaTupla(new Alarma((TipoDeAlarma) ComboBoxTipoDeAlarma.getSelectionModel().getSelectedItem(),fechaSuceso , minutosAntes ), "MinsAntes");
            }

            Stage stage = (Stage) AgregarAlarma.getScene().getWindow();
            stage.close();
            this.alarmasDelSuceso.add(alarmaParaAgregar);
        });
    }

    public void configurarCancelarAlarma() {
        this.CancelarAlarma.setOnAction(event -> {
            Stage stage = (Stage) CancelarAlarma.getScene().getWindow();
            stage.close();
        });
    }

    public void dispararAlerta(String contexto) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("ERROR");
        alerta.setHeaderText("ERROR DE PARAMETROS");
        alerta.setContentText(contexto);
        alerta.show();
    }



}
