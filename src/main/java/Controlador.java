import LogicaNegocios.*;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import static javafx.collections.FXCollections.observableArrayList;

public class Controlador {

    private LocalDateTime fechaInicial;
    private Frecuencia tipoVista;
    private Calendario calendario;
    boolean modoEliminarActivado;
    HashMap<Alarma, Suceso> AlarmasTotales;

    @FXML
    private Button eliminar;
    @FXML
    private Button agregar;
    @FXML
    private Button diaria;
    @FXML
    private Button semanal;
    @FXML
    private Button mensual;
    @FXML
    private ListView<Evento> eventosList;
    @FXML
    private DatePicker fechaPicker;
    @FXML
    private Button botonDiaSiguiente;
    @FXML
    private Label FechaActual;
    @FXML
    private Button botonDiaAnterior;
    @FXML
    private ListView<Tarea> tareasNoCompletadasList;
    @FXML
    private ListView<Tarea> tareasCompletadasList;

    ObservableList<Evento> eventosListaObservable = observableArrayList();
    ObservableList<Tarea> tareasCompletadasListaObservable = observableArrayList();
    ObservableList<Tarea> tareasNoCompletadasListaObservable = observableArrayList();

    public Controlador(Calendario modelo, LocalDateTime fechaInicial, Frecuencia tipoVista) {
        this.fechaInicial = fechaInicial;
        this.tipoVista = tipoVista;
        this.calendario = modelo;
        this.modoEliminarActivado = false;
    }

    private ArrayList<EventoHijo> getEventoRepetiblesFrecuencia(){
        ArrayList<EventoHijo> res = new ArrayList<>();
        switch (this.tipoVista) {
            case DIARIA -> res = calendario.eventosRepetiblesEnFechas(fechaInicial, fechaInicial.plusDays(1));
            case SEMANAL -> res = calendario.eventosRepetiblesEnFechas(fechaInicial, fechaInicial.plusWeeks(1));
            case MENSUAL -> res = calendario.eventosRepetiblesEnFechas(fechaInicial, fechaInicial.plusMonths(1));
        }

        return res;
    }


    public ArrayList<Evento> getEventosFrecuencia() {
        ArrayList<Evento> res = new ArrayList<>();
        switch (this.tipoVista) {
            case DIARIA -> res = calendario.eventosEnFechas(fechaInicial, fechaInicial.plusDays(1)) ;
            case SEMANAL -> res = calendario.eventosEnFechas(fechaInicial, fechaInicial.plusWeeks(1));
            case MENSUAL -> res = calendario.eventosEnFechas(fechaInicial, fechaInicial.plusMonths(1));
        }
        return res;
    }

    public ArrayList<Tarea> getTareasFrecuencia() {
        ArrayList<Tarea> res = new ArrayList<>();
        switch (this.tipoVista) {
            case DIARIA -> res = calendario.tareasEnFechas(fechaInicial, fechaInicial.plusDays(1));
            case SEMANAL -> res = calendario.tareasEnFechas(fechaInicial, fechaInicial.plusWeeks(1));
            case MENSUAL -> res = calendario.tareasEnFechas(fechaInicial, fechaInicial.plusMonths(1));
        }
        return res;
    }

    public void inicializar() throws Exception {
        this.AlarmasTotales = this.calendario.getTodasLasAlarmasOrdenadas();
        mostrarEventos();
        mostrarTareas();
        mostrarFecha();
        inicializarListas();
        configurarEliminarButton();
        configurarAgregarButton();
        configurarDiariaButton();
        configurarSemanalButton();
        configurarMensualButton();
        configurarDetalles();
        configurarFechaPIcker();
        configurarBotonSiguiente();
        configurarBotonAnterior();
        iniciarTimer();
        configurarCompletarTarea();
    }



    public void iniciarTimer() {
        for (Alarma alarma : AlarmasTotales.keySet()) {
            if (alarma.getFecha().isBefore(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
                AlarmasTotales.get(alarma).eliminarAlarma(alarma);
                AlarmasTotales.remove(alarma);
            }
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long tiempo) {
                    if (alarma.getFecha().equals(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
                        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                        alerta.setTitle("Alarma");
                        alerta.setHeaderText("Alarma");
                        alerta.setContentText("Se disparó una alarma por el suceso: " + AlarmasTotales.get(alarma).getTitulo());
                        alerta.show();
                        AlarmasTotales.get(alarma).eliminarAlarma(alarma);
                        AlarmasTotales.remove(alarma);
                        this.stop();
                    }
                    if (!AlarmasTotales.containsKey(alarma)) {
                        this.stop();
                    }
                }
            };
            timer.start();
        }
    }

    public void inicializarListas(){
        eventosList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Evento evento, boolean empty) {
                super.updateItem(evento, empty);
                if (empty || evento == null) {
                    setText(null);
                } else {
                    if (evento.esDiaCompleto()) {
                        setText(evento.getTitulo() + " | " + evento.getFecha().toLocalDate().toString() + " " + evento.getFechaFin().toLocalDate().toString());
                    } else {
                        setText(evento.getTitulo() + " | " + evento.getFecha().toString() + " | " + evento.getFechaFin().toString());
                    }
                }
            }
        });
        setTareaCellFactory(tareasCompletadasList);
        setTareaCellFactory(tareasNoCompletadasList);
    }

    private void setTareaCellFactory(ListView<Tarea> listView) {
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);
                if (empty || tarea == null) {
                    setText(null);
                } else {
                    if (tarea.esDiaCompleto()) {
                        setText(tarea.getTitulo() + " | " + tarea.getFecha().toLocalDate().toString());
                    } else {
                        setText(tarea.getTitulo() + " | " + tarea.getFechaLimite().toString());
                    }
                }
            }
        });
    }

    public void mostrarEventos()  {
        this.eventosListaObservable.addAll(getEventosFrecuencia());
        this.eventosListaObservable.addAll(getEventoRepetiblesFrecuencia());
        this.eventosListaObservable.sort(Comparator.comparing(Suceso::getFecha));
        eventosList.setItems(eventosListaObservable);
    }

    public void mostrarTareas(){
        for (Tarea tarea : getTareasFrecuencia()) {
            if (tarea.estaCompletada()) {
                this.tareasCompletadasListaObservable.add(tarea);
            } else {
                this.tareasNoCompletadasListaObservable.add(tarea);
            }
        }
        tareasCompletadasList.setItems(tareasCompletadasListaObservable);
        tareasNoCompletadasList.setItems(tareasNoCompletadasListaObservable);
    }
    //
    public void configurarEliminarButton(){
        eliminar.setOnAction(event -> {
            modoEliminarActivado = !modoEliminarActivado;
            configurarBorrarCosas();
            modoEliminarActivado = !modoEliminarActivado;
        });
    }

    public void configurarBorrarCosas() {
        if (modoEliminarActivado) {
            Tarea selectedTareaCompletada = tareasCompletadasList.getSelectionModel().getSelectedItem();
            Tarea selectedTareaNoCompletada = tareasNoCompletadasList.getSelectionModel().getSelectedItem();
            Evento selectedEvento = eventosList.getSelectionModel().getSelectedItem();
            if (selectedTareaCompletada != null) {
                calendario.eliminarTarea(selectedTareaCompletada);
            } else if (selectedTareaNoCompletada != null) {
                calendario.eliminarTarea(selectedTareaNoCompletada);
            } else if (selectedEvento != null) {
                if (selectedEvento instanceof EventoHijo)
                    calendario.eliminarEventoRepetibleDesdeHijo( (EventoHijo) selectedEvento);
                else{
                    calendario.eliminarEvento(selectedEvento);
                }
            }
            try {
                updatePrincipal();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void configurarAgregarButton(){
        ControladorSuceso controladorSuceso = new ControladorSuceso(this);
        agregar.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Agregar_suceso.fxml"));
                Stage escenario = new Stage();
                loader.setController(controladorSuceso);
                TabPane root = null;
                try {
                    root = loader.load();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    controladorSuceso.inicializar();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                escenario.setScene(new Scene(root));
                escenario.show();
            }
        });
    }

    public void configurarDetalles(){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("Detalles");
        contextMenu.getItems().add(menuItem);
        tareasCompletadasList.setContextMenu(contextMenu);
        eventosList.setContextMenu(contextMenu);
        tareasNoCompletadasList.setContextMenu(contextMenu);
        menuItem.setOnAction(event -> {
            Tarea selectedTareaCompletada = tareasCompletadasList.getSelectionModel().getSelectedItem();
            Tarea selectedTareaNoCompletada = tareasNoCompletadasList.getSelectionModel().getSelectedItem();
            Evento selectedEvento = eventosList.getSelectionModel().getSelectedItem();
            if (selectedTareaCompletada != null) {
                mostrarDetallesTarea(selectedTareaCompletada);
                tareasCompletadasList.getSelectionModel().clearSelection();
            } else if (selectedTareaNoCompletada != null) {
                mostrarDetallesTarea(selectedTareaNoCompletada);
                tareasNoCompletadasList.getSelectionModel().clearSelection();
            } else if (selectedEvento != null) {
                if (selectedEvento instanceof EventoHijo){
                    mostrarDetallesEvento((EventoHijo) selectedEvento);
                } else {
                    mostrarDetallesEvento(selectedEvento);
                }
                eventosList.getSelectionModel().clearSelection();
            }
        });
    }

    public void mostrarDetallesTarea(Tarea tarea){
        //Muestra la informacion de la tarea en pantalla en una alerta de javaFX
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Detalles de la tarea");
        info.setHeaderText(tarea.getTitulo());
        info.setContentText("Fecha: " + tarea.getFecha().toString() + "\n" +
                "Fecha limite: " + tarea.getFechaLimite().toString() + "\n" +
                "Descripción: " + tarea.getDescripcion() + "\n" +
                "Completada: " + tarea.estaCompletada() + "\n" +
                "Alarmas: \n" + tarea.devolverAlarmas()) ;
        info.showAndWait();
    }

    public void mostrarDetallesEvento(Evento evento){
        //Muestra la informacion del evento en pantalla en una alerta de javaFX
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Detalles del evento");
        info.setHeaderText(evento.getTitulo());
        info.setContentText("Fecha: " + evento.getFecha().toString() + "\n" +
                "Hasta: " + evento.getFechaFin().toString() + "\n" +
                "Descripción: " + evento.getDescripcion() + "\n" +
                "Alarmas: \n" + evento.devolverAlarmas());
        info.showAndWait();
    }

    public void mostrarDetallesEvento(EventoHijo evento){
        //Muestra la informacion del evento en pantalla en una alerta de javaFX
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Detalles del evento");
        info.setHeaderText(evento.getTitulo());
        info.setContentText("Fecha: " + evento.getFecha().toString() + "\n" +
                "Hasta: " + evento.getFechaFin().toString() + "\n" +
                "Descripción: " + evento.getDescripcion() + "\n" +
                "Repeticion: " + evento.getIntervalo() + " dias por semana.\n" +
                "Alarmas: \n" + evento.devolverAlarmas());
        info.showAndWait();
    }
    public void configurarDiariaButton() {
        if (tipoVista == Frecuencia.DIARIA){
            diaria.setStyle("-fx-background-color: green;");
        } else{
            diaria.setStyle("-fx-background-color: red;");
        }
        diaria.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eventosList.getItems().clear();
                tareasCompletadasList.getItems().clear();
                tareasNoCompletadasList.getItems().clear();
                tipoVista = Frecuencia.DIARIA;
                try {
                    inicializar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void configurarSemanalButton(){
        if (tipoVista == Frecuencia.SEMANAL){
            semanal.setStyle("-fx-background-color: green;");
        } else{
            semanal.setStyle("-fx-background-color: red;");
        }
        semanal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eventosList.getItems().clear();
                tareasCompletadasList.getItems().clear();
                tareasNoCompletadasList.getItems().clear();
                tipoVista = Frecuencia.SEMANAL;
                try {
                    inicializar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void configurarMensualButton(){
        if (tipoVista == Frecuencia.MENSUAL){
            mensual.setStyle("-fx-background-color: green;");
        } else{
            mensual.setStyle("-fx-background-color: red;");
        }
        mensual.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eventosList.getItems().clear();
                tareasCompletadasList.getItems().clear();
                tareasNoCompletadasList.getItems().clear();
                tipoVista = Frecuencia.MENSUAL;
                try {
                    inicializar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void configurarFechaPIcker(){
        fechaPicker.setOnAction( event -> {
            this.fechaInicial = fechaPicker.getValue().atStartOfDay();
            try {
                updatePrincipal();
            } catch (Exception e) {
                e.printStackTrace();
            }});
    }

    public void configurarBotonSiguiente() {
        botonDiaSiguiente.setOnAction(event -> {
            if (this.tipoVista == Frecuencia.DIARIA) {
                this.fechaInicial = fechaInicial.plusDays(1);
            } else if (this.tipoVista == Frecuencia.SEMANAL) {
                this.fechaInicial = fechaInicial.plusWeeks(1);
            } else {
                this.fechaInicial = fechaInicial.plusMonths(1);
            }
            try {
                updatePrincipal();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void configurarBotonAnterior() {
        botonDiaAnterior.setOnAction( event -> {
            if (this.tipoVista == Frecuencia.DIARIA) {
                this.fechaInicial = fechaInicial.minusDays(1);
            } else if (this.tipoVista == Frecuencia.SEMANAL) {
                this.fechaInicial = fechaInicial.minusWeeks(1);
            } else {
                this.fechaInicial = fechaInicial.minusMonths(1);
            }
            try {
                updatePrincipal();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void configurarCompletarTarea(){
        tareasNoCompletadasList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2){
                Tarea tarea = tareasNoCompletadasList.getSelectionModel().getSelectedItem();
                tarea.actualizar();
                for(Alarma alarma : tarea.getAlarmas()){
                    tarea.eliminarAlarma(alarma);
                }
                try {
                    updatePrincipal();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updatePrincipal() {
        try {
            this.calendario.guardar();
        } catch (Exception e) {
            e.printStackTrace();
        }
        eventosList.getItems().clear();
        tareasCompletadasList.getItems().clear();
        tareasNoCompletadasList.getItems().clear();
        try {
            inicializar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mostrarFecha(){
        this.FechaActual.setText("Fecha actual: " + fechaInicial.toLocalDate().toString());
    }

    public Calendario getCalendario(){
        return this.calendario;
    }

}
