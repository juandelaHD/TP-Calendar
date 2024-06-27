import LogicaNegocios.Alarma;

public class AlarmaTupla {
    private Alarma alarma;
    private String tipo;

    public AlarmaTupla(Alarma alarma, String tipo) {
        this.alarma = alarma;
        this.tipo = tipo;
    }

    public Alarma getAlarma() {
        return alarma;
    }

    public String getTipo() {
        return tipo;
    }
}