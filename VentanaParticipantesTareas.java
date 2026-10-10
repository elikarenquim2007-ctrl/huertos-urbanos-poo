import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Pantalla del módulo Participantes y Tareas (dos pestañas).
 * Es un JPanel, así que Jhoan puede meterlo en su ventana principal
 * pasándole el MISMO GestorTareas que usen los demás módulos.
 */
public class VentanaParticipantesTareas extends JPanel {
    private GestorTareas gestor;
    private String idSeleccionado = null;

    // Pestaña Participantes
    private JTextField txtNombre = new JTextField(20);
    private JTextField txtCedula = new JTextField(20);
    private JTextField txtTelefono = new JTextField(20);
    private DefaultTableModel modeloParticipantes;
    private JTable tablaParticipantes;

    // Pestaña Tareas
    private JComboBox<String> comboParticipante = new JComboBox<>();
    private JComboBox<String> comboTipo =
            new JComboBox<>(new String[]{"Riego", "Poda", "Fertilización"});
    private JTextField txtParcela = new JTextField(20);
    private JTextField txtFecha = new JTextField(LocalDate.now().toString(), 20);
    private JTextField txtDescripcion = new JTextField(20);
    private DefaultTableModel modeloTareas;
    private JTable tablaTareas;
    private JLabel lblAlerta = new JLabel(" ");

    public VentanaParticipantesTareas() {
        this(new GestorTareas());
    }

    public VentanaParticipantesTareas(GestorTareas gestor) {
        this.gestor = gestor;
        setLayout(new BorderLayout());
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Participantes", crearPanelParticipantes());
        pestanas.addTab("Tareas", crearPanelTareas());
        add(pestanas, BorderLayout.CENTER);
        refrescarParticipantes();
        refrescarTareas();
    }

    // ================= PESTAÑA PARTICIPANTES =================
    private JPanel crearPanelParticipantes() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(3, 2, 5, 5));
        formulario.add(new JLabel("Nombre"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Cédula (10 dígitos)"));
        formulario.add(txtCedula);
        formulario.add(new JLabel("Teléfono"));
        formulario.add(txtTelefono);

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        btnRegistrar.addActionListener(e -> registrarParticipante());
        btnActualizar.addActionListener(e -> actualizarParticipante());
        btnEliminar.addActionListener(e -> eliminarParticipante());
        btnLimpiar.addActionListener(e -> limpiarParticipante());
        JPanel botones = new JPanel(new FlowLayout());
        botones.add(btnRegistrar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.NORTH);
        norte.add(botones, BorderLayout.CENTER);

        modeloParticipantes = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Cédula", "Teléfono"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaParticipantes = new JTable(modeloParticipantes);
        tablaParticipantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarParticipante();
            }
        });

        panel.add(norte, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaParticipantes), BorderLayout.CENTER);
        return panel;
    }

    private void registrarParticipante() {
        try {
            gestor.registrarParticipante(txtNombre.getText(), txtCedula.getText(), txtTelefono.getText());
            limpiarParticipante();
            refrescarParticipantes();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void actualizarParticipante() {
        if (idSeleccionado == null) {
            mostrarAviso("Seleccione un participante de la lista.");
            return;
        }
        try {
            gestor.actualizarParticipante(idSeleccionado, txtNombre.getText(),
                    txtCedula.getText(), txtTelefono.getText());
            refrescarParticipantes();
            refrescarTareas();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarParticipante() {
        if (idSeleccionado == null) {
            mostrarAviso("Seleccione un participante de la lista.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Se eliminarán también sus tareas. ¿Continuar?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            gestor.eliminarParticipante(idSeleccionado);
            limpiarParticipante();
            refrescarParticipantes();
            refrescarTareas();
        }
    }

    private void limpiarParticipante() {
        idSeleccionado = null;
        txtNombre.setText("");
        txtCedula.setText("");
        txtTelefono.setText("");
        tablaParticipantes.clearSelection();
    }

    private void seleccionarParticipante() {
        int fila = tablaParticipantes.getSelectedRow();
        if (fila < 0) {
            return;
        }
        String id = (String) modeloParticipantes.getValueAt(fila, 0);
        Participante p = gestor.obtenerParticipante(id);
        idSeleccionado = p.getId();
        txtNombre.setText(p.getNombre());
        txtCedula.setText(p.getCedula());
        txtTelefono.setText(p.getTelefono());
    }

    private void refrescarParticipantes() {
        modeloParticipantes.setRowCount(0);
        comboParticipante.removeAllItems();
        for (Participante p : gestor.listarParticipantes()) {
            modeloParticipantes.addRow(new Object[]{
                    p.getId(), p.getNombre(), p.getCedula(), p.getTelefono()});
            comboParticipante.addItem(p.getId() + " - " + p.getNombre());
        }
    }

    // ================= PESTAÑA TAREAS =================
    private JPanel crearPanelTareas() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formulario = new JPanel(new GridLayout(5, 2, 5, 5));
        formulario.add(new JLabel("Participante"));
        formulario.add(comboParticipante);
        formulario.add(new JLabel("Tipo de tarea"));
        formulario.add(comboTipo);
        formulario.add(new JLabel("Parcela"));
        formulario.add(txtParcela);
        formulario.add(new JLabel("Fecha (AAAA-MM-DD)"));
        formulario.add(txtFecha);
        formulario.add(new JLabel("Descripción"));
        formulario.add(txtDescripcion);

        JButton btnAsignar = new JButton("Asignar tarea");
        JButton btnRealizada = new JButton("Marcar realizada");
        JButton btnEliminar = new JButton("Eliminar tarea");
        JButton btnEstados = new JButton("Actualizar estados");
        btnAsignar.addActionListener(e -> asignarTarea());
        btnRealizada.addActionListener(e -> marcarRealizada());
        btnEliminar.addActionListener(e -> eliminarTarea());
        btnEstados.addActionListener(e -> {
            gestor.actualizarEstados(LocalDate.now());
            refrescarTareas();
        });
        JPanel botones = new JPanel(new FlowLayout());
        botones.add(btnAsignar);
        botones.add(btnRealizada);
        botones.add(btnEliminar);
        botones.add(btnEstados);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(formulario, BorderLayout.NORTH);
        norte.add(botones, BorderLayout.CENTER);

        modeloTareas = new DefaultTableModel(
                new String[]{"ID", "Tipo", "Participante", "Parcela", "Fecha", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaTareas = new JTable(modeloTareas);
        // Colores: rojo si está atrasada, verde si está realizada
        tablaTareas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabla, Object valor,
                    boolean seleccionada, boolean foco, int fila, int columna) {
                Component c = super.getTableCellRendererComponent(
                        tabla, valor, seleccionada, foco, fila, columna);
                String estado = (String) tabla.getValueAt(fila, 5);
                if (!seleccionada) {
                    if ("Atrasada".equals(estado)) {
                        c.setBackground(new Color(248, 215, 218));
                    } else if ("Realizada".equals(estado)) {
                        c.setBackground(new Color(212, 237, 218));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        lblAlerta.setForeground(new Color(176, 42, 55));
        panel.add(norte, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaTareas), BorderLayout.CENTER);
        panel.add(lblAlerta, BorderLayout.SOUTH);
        return panel;
    }

    private void asignarTarea() {
        if (comboParticipante.getSelectedItem() == null) {
            mostrarAviso("Primero registre y seleccione un participante.");
            return;
        }
        try {
            String idParticipante = ((String) comboParticipante.getSelectedItem()).split(" - ")[0];
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            gestor.asignarTarea(idParticipante, (String) comboTipo.getSelectedItem(),
                    txtDescripcion.getText(), txtParcela.getText(), fecha);
            txtDescripcion.setText("");
            refrescarTareas();
        } catch (DateTimeParseException ex) {
            mostrarError("La fecha debe tener el formato AAAA-MM-DD (ejemplo: 2026-10-15).");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private String idTareaSeleccionada() {
        int fila = tablaTareas.getSelectedRow();
        if (fila < 0) {
            mostrarAviso("Seleccione una tarea de la lista.");
            return null;
        }
        return (String) modeloTareas.getValueAt(fila, 0);
    }

    private void marcarRealizada() {
        String id = idTareaSeleccionada();
        if (id != null) {
            gestor.marcarRealizada(id);
            refrescarTareas();
        }
    }

    private void eliminarTarea() {
        String id = idTareaSeleccionada();
        if (id != null) {
            gestor.eliminarTarea(id);
            refrescarTareas();
        }
    }

    private void refrescarTareas() {
        modeloTareas.setRowCount(0);
        for (Tarea t : gestor.listarTareas()) {
            String nombre;
            try {
                nombre = gestor.obtenerParticipante(t.getIdParticipante()).getNombre();
            } catch (IllegalArgumentException ex) {
                nombre = "?";
            }
            modeloTareas.addRow(new Object[]{t.getId(), t.getTipo(), nombre,
                    t.getParcela(), t.getFecha().toString(), t.getEstado()});
        }
        int atrasadas = gestor.tareasAtrasadas().size();
        lblAlerta.setText(atrasadas > 0 ? "⚠ Tareas atrasadas: " + atrasadas : " ");
    }

    // ================= MENSAJES =================
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
