import java.time.LocalDate;

public class Riego extends Tarea {

    public Riego(String id, String descripcion, String parcela, LocalDate fecha, String idParticipante) {
        super(id, descripcion, parcela, fecha, idParticipante);
    }

    @Override
    public String getTipo() {
        return "Riego";
    }
}
