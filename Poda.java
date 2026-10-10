import java.time.LocalDate;

public class Poda extends Tarea {

    public Poda(String id, String descripcion, String parcela, LocalDate fecha, String idParticipante) {
        super(id, descripcion, parcela, fecha, idParticipante);
    }

    @Override
    public String getTipo() {
        return "Poda";
    }
}
