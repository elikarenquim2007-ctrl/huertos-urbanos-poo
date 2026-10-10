/**
 * Una persona que cuida el huerto.
 * Los atributos son privados (encapsulamiento) y solo se cambian con los setters,
 * que revisan que los datos sean correctos.
 */
public class Participante {
    private String id;
    private String nombre;
    private String cedula;
    private String telefono;

    public Participante(String id, String nombre, String cedula, String telefono) {
        this.id = id;
        setNombre(nombre);
        setCedula(cedula);
        setTelefono(telefono);
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        if (cedula == null || !cedula.trim().matches("\\d{10}")) {
            throw new IllegalArgumentException("La cédula debe tener 10 dígitos numéricos.");
        }
        this.cedula = cedula.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono == null || !telefono.trim().matches("\\d{7,10}")) {
            throw new IllegalArgumentException("El teléfono debe tener entre 7 y 10 dígitos.");
        }
        this.telefono = telefono.trim();
    }
}
