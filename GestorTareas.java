import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Guarda los participantes y las tareas, y aplica las reglas de negocio:
 *  - R2.1 registrar / actualizar / eliminar participantes y tareas
 *  - R2.2 máximo de tareas por semana para cada participante
 *  - R2.3 estados de las tareas (Pendiente, Realizada, Atrasada)
 */
public class GestorTareas {
    public static final int MAX_TAREAS_SEMANA = 3;

    private Map<String, Participante> participantes = new LinkedHashMap<>();
    private Map<String, Tarea> tareas = new LinkedHashMap<>();
    private int siguienteParticipante = 1;
    private int siguienteTarea = 1;

    // ---------- Participantes (R2.1) ----------
    public Participante registrarParticipante(String nombre, String cedula, String telefono) {
        Participante nuevo = new Participante(
                String.format("P%03d", siguienteParticipante), nombre, cedula, telefono);
        for (Participante p : participantes.values()) {
            if (p.getCedula().equals(nuevo.getCedula())) {
                throw new IllegalArgumentException("Ya existe un participante con esa cédula.");
            }
        }
        participantes.put(nuevo.getId(), nuevo);
        siguienteParticipante++;
        return nuevo;
    }

    public void actualizarParticipante(String id, String nombre, String cedula, String telefono) {
        Participante p = obtenerParticipante(id);
        // Se crea uno de prueba para validar todo antes de cambiar el real
        Participante prueba = new Participante(id, nombre, cedula, telefono);
        for (Participante otro : participantes.values()) {
            if (!otro.getId().equals(id) && otro.getCedula().equals(prueba.getCedula())) {
                throw new IllegalArgumentException("Ya existe otro participante con esa cédula.");
            }
        }
        p.setNombre(prueba.getNombre());
        p.setCedula(prueba.getCedula());
        p.setTelefono(prueba.getTelefono());
    }

    public void eliminarParticipante(String id) {
        obtenerParticipante(id);
        Iterator<Tarea> it = tareas.values().iterator();
        while (it.hasNext()) {            // también se borran sus tareas
            if (it.next().getIdParticipante().equals(id)) {
                it.remove();
            }
        }
        participantes.remove(id);
    }

    public Participante obtenerParticipante(String id) {
        Participante p = participantes.get(id);
        if (p == null) {
            throw new IllegalArgumentException("El participante no existe.");
        }
        return p;
    }

    public List<Participante> listarParticipantes() {
        return new ArrayList<>(participantes.values());
    }

    // ---------- Tareas (R2.1 y R2.2) ----------
    public Tarea asignarTarea(String idParticipante, String tipo, String descripcion,
                              String parcela, LocalDate fecha) {
        obtenerParticipante(idParticipante);
        if (parcela == null || parcela.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar la parcela.");
        }
        // R2.2: contar las tareas del participante en la misma semana
        int enLaSemana = 0;
        for (Tarea t : tareasDe(idParticipante)) {
            if (mismaSemana(t.getFecha(), fecha)) {
                enLaSemana++;
            }
        }
        if (enLaSemana >= MAX_TAREAS_SEMANA) {
            throw new IllegalArgumentException("El participante ya tiene "
                    + MAX_TAREAS_SEMANA + " tareas esa semana (límite alcanzado).");
        }
        String id = String.format("T%03d", siguienteTarea);
        Tarea tarea;
        switch (tipo) {
            case "Riego":
                tarea = new Riego(id, descripcion, parcela, fecha, idParticipante);
                break;
            case "Poda":
                tarea = new Poda(id, descripcion, parcela, fecha, idParticipante);
                break;
            case "Fertilización":
                tarea = new Fertilizacion(id, descripcion, parcela, fecha, idParticipante);
                break;
            default:
                throw new IllegalArgumentException("Tipo de tarea no válido.");
        }
        tareas.put(id, tarea);
        siguienteTarea++;
        return tarea;
    }

    private boolean mismaSemana(LocalDate a, LocalDate b) {
        WeekFields wf = WeekFields.ISO;
        return a.get(wf.weekBasedYear()) == b.get(wf.weekBasedYear())
                && a.get(wf.weekOfWeekBasedYear()) == b.get(wf.weekOfWeekBasedYear());
    }

    public void eliminarTarea(String idTarea) {
        if (tareas.remove(idTarea) == null) {
            throw new IllegalArgumentException("La tarea no existe.");
        }
    }

    public void marcarRealizada(String idTarea) {
        Tarea t = tareas.get(idTarea);
        if (t == null) {
            throw new IllegalArgumentException("La tarea no existe.");
        }
        t.marcarRealizada();
    }

    public List<Tarea> listarTareas() {
        return new ArrayList<>(tareas.values());
    }

    public List<Tarea> tareasDe(String idParticipante) {
        List<Tarea> resultado = new ArrayList<>();
        for (Tarea t : tareas.values()) {
            if (t.getIdParticipante().equals(idParticipante)) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    // ---------- Estados y alertas (R2.3) ----------
    public void actualizarEstados(LocalDate hoy) {
        for (Tarea t : tareas.values()) {
            t.actualizarEstado(hoy);
        }
    }

    public List<Tarea> tareasAtrasadas() {
        List<Tarea> resultado = new ArrayList<>();
        for (Tarea t : tareas.values()) {
            if (t.getEstado().equals("Atrasada")) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    // ---------- Datos que usa el módulo de Cosechas y Reparto (Toño) ----------
    public int tareasRealizadasPor(String idParticipante) {
        int total = 0;
        for (Tarea t : tareasDe(idParticipante)) {
            if (t.getEstado().equals("Realizada")) {
                total++;
            }
        }
        return total;
    }

    public int totalTareasRealizadas() {
        int total = 0;
        for (Tarea t : tareas.values()) {
            if (t.getEstado().equals("Realizada")) {
                total++;
            }
        }
        return total;
    }
}
