import java.time.LocalDate;

/**
 * Una labor de cuidado del huerto (riego, poda, etc.).
 * Es abstracta: no se crea una "Tarea" a secas, sino Riego, Poda o Fertilizacion.
 */
public abstract class Tarea {
    private String id;
    private String descripcion;
    private String parcela;
    private LocalDate fecha;
    private String idParticipante;
    private String estado; // Pendiente, Realizada o Atrasada

    public Tarea(String id, String descripcion, String parcela, LocalDate fecha, String idParticipante) {
        this.id = id;
        this.descripcion = descripcion.trim();
        this.parcela = parcela.trim();
        this.fecha = fecha;
        this.idParticipante = idParticipante;
        this.estado = "Pendiente";
    }

    /** Cada subclase dice de qué tipo es. */
    public abstract String getTipo();

    public String getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getParcela() {
        return parcela;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getIdParticipante() {
        return idParticipante;
    }

    public String getEstado() {
        return estado;
    }

    public void marcarRealizada() {
        estado = "Realizada";
    }

    /** R2.3: si sigue pendiente y su fecha ya pasó, queda Atrasada. */
    public void actualizarEstado(LocalDate hoy) {
        if (estado.equals("Pendiente") && fecha.isBefore(hoy)) {
            estado = "Atrasada";
        }
    }
}
