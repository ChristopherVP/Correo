package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.EstadoSolicitud;
import uce.edu.ec.correo.dominio.Solicitud;
import uce.edu.ec.correo.dominio.TipoSolicitud;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

public class PanelAdminSolicitudes extends JPanel implements PanelAdmin.Refrescable {

    private static final String TODOS = "Todos";

    private final EmpresaCorreo empresa;
    private final DefaultTableModel modelo = EstiloUI.modelo("#", "Fecha", "Tipo", "Asunto", "Usuario",
            "Estado", "Atendida por");
    private final JTable tabla = EstiloUI.tabla(modelo);
    private final JComboBox<Object> cmbEstado = new JComboBox<>();
    private final JComboBox<Object> cmbTipo = new JComboBox<>();
    private List<Solicitud> solicitudes = List.of();

    public PanelAdminSolicitudes(EmpresaCorreo empresa) {
        this.empresa = empresa;
        setLayout(new BorderLayout(10, 10));
        setBackground(EstiloUI.FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel superior = new JPanel(new BorderLayout(10, 0));
        superior.setOpaque(false);
        superior.add(EstiloUI.titulo("Centro de Solicitudes y Mejoras"), BorderLayout.WEST);

        cmbEstado.addItem(TODOS);
        for (EstadoSolicitud estado : EstadoSolicitud.values()) {
            cmbEstado.addItem(estado);
        }
        cmbTipo.addItem(TODOS);
        for (TipoSolicitud tipo : TipoSolicitud.values()) {
            cmbTipo.addItem(tipo);
        }
        cmbEstado.addActionListener(e -> llenarTabla());
        cmbTipo.addActionListener(e -> llenarTabla());

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filtros.setOpaque(false);
        filtros.add(new JLabel("Estado:"));
        filtros.add(cmbEstado);
        filtros.add(new JLabel("Tipo:"));
        filtros.add(cmbTipo);
        superior.add(filtros, BorderLayout.EAST);
        add(superior, BorderLayout.NORTH);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    atenderSeleccionada();
                }
            }
        });
        add(EstiloUI.conDesplazamiento(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        JButton btnActualizar = EstiloUI.boton("Actualizar");
        btnActualizar.addActionListener(e -> refrescar());
        JButton btnAtender = EstiloUI.boton("Ver y atender");
        btnAtender.addActionListener(e -> atenderSeleccionada());
        botones.add(btnActualizar);
        botones.add(btnAtender);
        add(botones, BorderLayout.SOUTH);
    }

    @Override
    public void refrescar() {
        try {
            solicitudes = empresa.listarSolicitudesAdmin().stream()
                    .sorted(Comparator.comparingInt(Solicitud::getId).reversed())
                    .toList();
            llenarTabla();
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }

    private void llenarTabla() {
        Object filtroEstado = cmbEstado.getSelectedItem();
        Object filtroTipo = cmbTipo.getSelectedItem();
        modelo.setRowCount(0);
        for (Solicitud s : solicitudes) {
            if (filtroEstado instanceof EstadoSolicitud estado && s.getEstado() != estado) {
                continue;
            }
            if (filtroTipo instanceof TipoSolicitud tipo && s.getTipo() != tipo) {
                continue;
            }
            modelo.addRow(new Object[]{
                    s.getId(), EstiloUI.fecha(s.getFechaCreacion()), s.getTipo().getDescripcion(), s.getAsunto(),
                    s.getEmailUsuario(), s.getEstado().getDescripcion(),
                    s.getAtendidaPor() == null ? "—" : s.getAtendidaPor()});
        }
    }

    private void atenderSeleccionada() {
        Object id = EstiloUI.valorSeleccionado(tabla, 0);
        if (id == null) {
            EstiloUI.info(this, "Seleccione una solicitud de la tabla.");
            return;
        }
        solicitudes.stream()
                .filter(s -> s.getId() == (Integer) id)
                .findFirst()
                .ifPresent(solicitud -> {
                    DialogoAtenderSolicitud dialogo = new DialogoAtenderSolicitud(
                            SwingUtilities.getWindowAncestor(this), empresa, solicitud);
                    dialogo.setVisible(true);
                    if (dialogo.fueGuardado()) {
                        refrescar();
                    }
                });
    }
}
