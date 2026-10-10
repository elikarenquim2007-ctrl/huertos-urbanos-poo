import java.time.LocalDate;

public class Fertilizacion extends Tarea {

    public Fertilizacion(String id, String descripcion, String parcela, LocalDate fecha, String idParticipante) {
        super(id, descripcion, parcela, fecha, idParticipante);
    }

    @Override
    public String getTipo() {
        return "Fertilización";
    }
}
