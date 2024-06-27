package LogicaNegocios;

import LogicaNegocios.EventoRepetible;

import java.util.ArrayList;

public interface ObserverBorradoHijo {

    void eliminarPadre(ArrayList<EventoRepetible> listaEventosHijos);

}