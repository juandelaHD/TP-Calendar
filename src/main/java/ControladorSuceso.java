import LogicaNegocios.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ControladorSuceso {

    private Controlador controladorPrincipal;

    Verificador verificador;

    // BOTONES PARA AGREGAR TAREAS
    @FXML
    private Button GuardarTarea;
    @FXML
    private Button CancelarTarea;
    @FXML
    private TextField TituloTarea;
    @FXML
    private TextArea DescripcionTarea;
    @FXML
    private DatePicker FechaPickerTarea;
    @FXML
    private CheckBox DiaCompletoTarea;
    @FXML
    private TextField TextFieldTareaHora;
    @FXML
    private TextField TextFieldTareaMinutos;
    @FXML
    private Button AgregarAlarmasTarea;

    // BOTONES PARA AGREGAR EVENTOS
    @FXML
    private Button GuardarEvento;
    @FXML
    private Button CancelarEvento;
    @FXML
    private TextField TituloEvento;
    @FXML
    private TextArea DescripcionEvento;
    @FXML
    private DatePicker FechaInicialPicker;
    @FXML
    private TextField TextFieldEventoHoraIni;
    @FXML
    private TextField TextFieldEventoMinutosIni;
    @FXML
    private DatePicker FechaFinalPicker;
    @FXML
    private TextField TextFieldEventoHoraFin;
    @FXML
    private TextField TextFieldEventoMinutosFin;
    @FXML
    private CheckBox DiaCompletoEvento;
    @FXML
    private Button AgregarAlarmasEvento;
    @FXML
    private CheckBox EsRepetible;
    @FXML
    private TextField Intervalo;

    private List<AlarmaTupla> alarmasParaAgregar;

    public ControladorSuceso(Controlador controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
        this.verificador = new Verificador();
        this.alarmasParaAgregar = new ArrayList<>();
    }

    public void inicializar() throws Exception  {
        Intervalo.disableProperty().bind(EsRepetible.selectedProperty().not());
        TextFieldTareaHora.disableProperty().bind(DiaCompletoTarea.selectedProperty());
        TextFieldTareaMinutos.disableProperty().bind(DiaCompletoTarea.selectedProperty());
        TextFieldEventoHoraIni.disableProperty().bind(DiaCompletoEvento.selectedProperty());
        TextFieldEventoMinutosIni.disableProperty().bind(DiaCompletoEvento.selectedProperty());
        TextFieldEventoHoraFin.disableProperty().bind(DiaCompletoEvento.selectedProperty());
        TextFieldEventoMinutosFin.disableProperty().bind(DiaCompletoEvento.selectedProperty());

        configuararGuardarTarea();
        configurarCancelarTarea();
        configurarGuardarEvento();
        configurarCancelarEvento();
        configurarAgregarAlarmaTarea();
        configurarAgregarAlarmaEvento();
    }

    public void configuararGuardarTarea() {
        this.GuardarTarea.setOnAction(event -> {
            Tarea tarea;
            try {
                tarea = definirCamposTarea();
            } catch (Exception e) {
                catchExcepcionesTarea();
                return;
            }
            for (AlarmaTupla alarmaTupla : alarmasParaAgregar) {
                Alarma alarma = alarmaTupla.getAlarma();
                if (Objects.equals(alarmaTupla.getTipo(), "Definida")) {
                    tarea.agregarAlarmaDefinida(alarma.getTipo(), alarma.getFecha());
                } else {
                    tarea.agregarAlarmaMinsAntes(alarma.getTipo(),  alarma.getMinsAntes());
                }
            }
            this.controladorPrincipal.getCalendario().agregarTarea(tarea);
            this.controladorPrincipal.updatePrincipal();
            Stage stage = (Stage) GuardarTarea.getScene().getWindow();
            stage.close();
        });
    }

    public void configurarCancelarTarea() {
        this.CancelarTarea.setOnAction(event -> {
            Stage stage = (Stage) CancelarTarea.getScene().getWindow();
            stage.close();
        });
    }

    public void configurarGuardarEvento() {
        this.GuardarEvento.setOnAction(event -> {
            if (this.EsRepetible.isSelected()) {
                EventoRepetible eventoRepetible;
                try {
                    eventoRepetible = definirCamposEventoRepetible();
                } catch (Exception e) {
                    catchExcepcionesEventos();
                    return;
                }
                for (AlarmaTupla alarmaTupla : alarmasParaAgregar) {
                    Alarma alarma = alarmaTupla.getAlarma();
                    if (Objects.equals(alarmaTupla.getTipo(), "Definida")) {
                        eventoRepetible.agregarAlarmaDefinida(alarma.getTipo(), alarma.getFecha());
                    } else {
                        eventoRepetible.agregarAlarmaMinsAntes(alarma.getTipo(),  alarma.getMinsAntes());
                    }
                }
                this.controladorPrincipal.getCalendario().agregarEventoRepetible(eventoRepetible);
            } else {
                Evento evento;
                try {
                    evento = definirCamposEvento();
                } catch (Exception e) {
                    catchExcepcionesEventos();
                    return;
                }
                for (AlarmaTupla alarmaTupla : alarmasParaAgregar) {
                    Alarma alarma = alarmaTupla.getAlarma();
                    if (Objects.equals(alarmaTupla.getTipo(), "Definida")) {
                        evento.agregarAlarmaDefinida(alarma.getTipo(), alarma.getFecha());
                    } else {
                        evento.agregarAlarmaMinsAntes(alarma.getTipo(),  alarma.getMinsAntes());
                    }
                }
                this.controladorPrincipal.getCalendario().agregarEvento(evento);
            }
            controladorPrincipal.updatePrincipal();
            Stage stage = (Stage) GuardarEvento.getScene().getWindow();
            stage.close();
        });
    }

    public void configurarCancelarEvento() {
        this.CancelarEvento.setOnAction(event -> {
            Stage stage = (Stage) CancelarEvento.getScene().getWindow();
            stage.close();
        });
    }

    public void configurarAgregarAlarmaTarea() {
        List<AlarmaTupla> alarmas = this.alarmasParaAgregar;
        ControladorAlarma controladorAlarma = new ControladorAlarma(alarmas);
        this.AgregarAlarmasTarea.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Agregar_alarma.fxml"));
                    loader.setController(controladorAlarma);
                    TitledPane root = loader.load();
                    if (DiaCompletoTarea.isSelected()){
                        controladorAlarma.inicializar(FechaPickerTarea.getValue().atTime(0, 0));
                    } else {
                        controladorAlarma.inicializar(FechaPickerTarea.getValue().atTime(Integer.parseInt(TextFieldTareaHora.getText()), Integer.parseInt(TextFieldTareaMinutos.getText())));
                    }
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    catchExcepcionesTarea();
                }
            }
        });
    }

    public void configurarAgregarAlarmaEvento() {
        List<AlarmaTupla> alarmas = this.alarmasParaAgregar;
        ControladorAlarma controladorAlarma = new ControladorAlarma(alarmas);
        this.AgregarAlarmasEvento.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Agregar_alarma.fxml"));
                    loader.setController(controladorAlarma);
                    TitledPane root = loader.load();
                    if (DiaCompletoEvento.isSelected()) {
                        controladorAlarma.inicializar(FechaInicialPicker.getValue().atTime(0, 0));
                    } else {
                        controladorAlarma.inicializar(FechaInicialPicker.getValue().atTime(Integer.parseInt(TextFieldEventoHoraIni.getText()), Integer.parseInt(TextFieldEventoMinutosIni.getText())));
                    }
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    catchExcepcionesEventos();
                }
            }
        });
    }

    public void dispararAlerta(String contexto) {
        Alert alerta = new Alert (Alert.AlertType.ERROR);
        alerta.setTitle("ERROR");
        alerta.setHeaderText("ERROR DE PARAMETROS");
        alerta.setContentText(contexto);
        alerta.show();
    }

    public Tarea definirCamposTarea() {
        String titulo = this.TituloTarea.getText();
        String descripcion = this.DescripcionTarea.getText();
        boolean diaCompleto = this.DiaCompletoTarea.isSelected();
        LocalDateTime fechaTarea;
        if (diaCompleto) {
            fechaTarea = this.FechaPickerTarea.getValue().atTime(0, 0, 0);
        } else {
            int TareaMinutos = Integer.parseInt(this.TextFieldTareaMinutos.getText());
            int TareaHoras = Integer.parseInt(this.TextFieldTareaHora.getText());
            fechaTarea = this.FechaPickerTarea.getValue().atTime(TareaHoras, TareaMinutos);
        }
        return new Tarea(titulo, descripcion, fechaTarea, diaCompleto);
    }

    public EventoRepetible definirCamposEventoRepetible() {
        String titulo = this.TituloEvento.getText();
        String descripcion = this.DescripcionEvento.getText();
        boolean diaCompleto = this.DiaCompletoEvento.isSelected();
        LocalDateTime fechaIni;
        LocalDateTime fechaFin;
        int intervalo = Integer.parseInt(this.Intervalo.getText());
        if (diaCompleto) {
            fechaIni = this.FechaInicialPicker.getValue().atTime(0, 0, 0);
            fechaFin = this.FechaFinalPicker.getValue().atTime(23, 59, 59);
        } else {
            int EventoMinutosIni = Integer.parseInt(this.TextFieldEventoMinutosIni.getText());
            int EventoHorasIni = Integer.parseInt(this.TextFieldEventoHoraIni.getText());
            int EventoMinutosFin = Integer.parseInt(this.TextFieldEventoMinutosFin.getText());
            int EventoHorasFin = Integer.parseInt(this.TextFieldEventoHoraFin.getText());
            fechaIni = this.FechaInicialPicker.getValue().atTime(EventoHorasIni,EventoMinutosIni);
            fechaFin = this.FechaFinalPicker.getValue().atTime(EventoHorasFin,EventoMinutosFin );
        }
        return new EventoRepetible(titulo, descripcion, fechaIni, fechaFin, diaCompleto, Frecuencia.DIARIA, null, intervalo, 0, null, true);
    }

    public Evento definirCamposEvento() {
        String titulo = this.TituloEvento.getText();
        String descripcion = this.DescripcionEvento.getText();
        boolean diaCompleto = this.DiaCompletoEvento.isSelected();
        LocalDateTime fechaIni;
        LocalDateTime fechaFin;
        if (diaCompleto) {
            fechaIni = this.FechaInicialPicker.getValue().atTime(0, 0, 0);
            fechaFin = this.FechaFinalPicker.getValue().atTime(23, 59, 59);
        } else {
            int EventoMinutosIni = Integer.parseInt(this.TextFieldEventoMinutosIni.getText());
            int EventoHorasIni = Integer.parseInt(this.TextFieldEventoHoraIni.getText());
            int EventoMinutosFin = Integer.parseInt(this.TextFieldEventoMinutosFin.getText());
            int EventoHorasFin = Integer.parseInt(this.TextFieldEventoHoraFin.getText());
            fechaIni = this.FechaInicialPicker.getValue().atTime(EventoHorasIni, EventoMinutosIni);
            fechaFin = this.FechaFinalPicker.getValue().atTime(EventoHorasFin, EventoMinutosFin);
        }
        return new Evento(titulo, descripcion, fechaIni, fechaFin, diaCompleto);
    }

    public void catchExcepcionesTarea() {
        if (!DiaCompletoTarea.isSelected()) {
            if (FechaPickerTarea.getValue() == null) {
                dispararAlerta("Debes elegir una fecha particular para tu tarea!");
            } else {
                dispararAlerta("Debes ingresar una hora y minutos válidos para tu tarea.");
            }
        }
    }

    public void catchExcepcionesEventos() {
        if (!DiaCompletoEvento.isSelected()) {
            if (FechaInicialPicker.getValue() == null || FechaFinalPicker.getValue() == null) {
                dispararAlerta("Debes elegir una fecha Inicial/Final particular para tu evento!");
            } else {
                dispararAlerta("Debes ingresar una hora y minutos válidos para tu evento.");
            }
        } else {
            if (FechaInicialPicker.getValue() == null) {
                dispararAlerta("Debes elegir la fecha Inicial/Final para tu evento de dia completo!");
            }
        }
    }

}
