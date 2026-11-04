package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.Solicitud;
import uce.edu.ec.correo.util.EstiloUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;

public class DialogoCentroSolicitudes extends JDialog {

    private final EmpresaCorreo empresa;
    private final DefaultTableModel modelo = EstiloUI.modelo("#", "Fecha", "Tipo", "Asunto", "Estado");
    private final JTable tabla = EstiloUI.tabla(modelo);
    private List<Solicitud> solicitudes = List.of();

    public DialogoCentroSolicitudes(Window propietario, EmpresaCorreo empresa) {
        super(propietario, "Centro de Solicitudes y Mejoras", Dialog.ModalityType.APPLICATION_MODAL);
        this.empresa = empresa;
        construirInterfaz();
        refrescar();
        setSize(760, 480);
        setLocationRelativeTo(propietario);
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel superior = new JPanel(new BorderLayout(0, 5));
        superior.add(EstiloUI.titulo("Centro de Solicitudes y Mejoras"), BorderLayout.NORTH);
        superior.add(EstiloUI.nota("Envíe quejas, reportes técnicos o recomendaciones para mejorar EcuMail "
                + "y consulte la respuesta del equipo de soporte."), BorderLayout.SOUTH);
        raiz.add(superior, BorderLayout.NORTH);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verDetalle();
                }
            }
        });
        raiz.add(EstiloUI.conDesplazamiento(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnNueva = EstiloUI.boton("Nueva solicitud");
        btnNueva.addActionListener(e -> nuevaSolicitud());
        JButton btnDetalle = EstiloUI.boton("Ver detalle");
        btnDetalle.addActionListener(e -> verDetalle());
        JButton btnActualizar = EstiloUI.boton("Actualizar");
        btnActualizar.addActionListener(e -> refrescar());
        JButton btnCerrar = EstiloUI.boton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        botones.add(btnNueva);
        botones.add(btnDetalle);
        botones.add(btnActualizar);
        botones.add(btnCerrar);
        raiz.add(botones, BorderLayout.SOUTH);

        setContentPane(raiz);
    }

    private void refrescar() {
        solicitudes = empresa.listarMisSolicitudes().stream()
                .sorted(Comparator.comparingInt(Solicitud::getId).reversed())
                .toList();
        modelo.setRowCount(0);
        for (Solicitud s : solicitudes) {
            modelo.addRow(new Object[]{s.getId(), EstiloUI.fecha(s.getFechaCreacion()),
                    s.getTipo().getDescripcion(), s.getAsunto(), s.getEstado().getDescripcion()});
        }
    }

    private void nuevaSolicitud() {
        DialogoNuevaSolicitud dialogo = new DialogoNuevaSolicitud(this, empresa);
        dialogo.setVisible(true);
        if (dialogo.fueEnviada()) {
            refrescar();
        }
    }

    private void verDetalle() {
        Object id = EstiloUI.valorSeleccionado(tabla, 0);
        if (id == null) {
            EstiloUI.info(this, "Seleccione una solicitud de la tabla.");
            return;
        }
        solicitudes.stream()
                .filter(s -> s.getId() == (Integer) id)
                .findFirst()
                .ifPresent(this::mostrarDetalle);
    }

    private void mostrarDetalle(Solicitud s) {
        StringBuilder texto = new StringBuilder();
        texto.append("Solicitud #").append(s.getId()).append('\n');
        texto.append("Tipo: ").append(s.getTipo().getDescripcion()).append('\n');
        texto.append("Fecha: ").append(EstiloUI.fecha(s.getFechaCreacion())).append('\n');
        texto.append("Estado: ").append(s.getEstado().getDescripcion()).append("\n\n");
        texto.append("Asunto: ").append(s.getAsunto()).append("\n\n");
        texto.append("Descripción:\n").append(s.getDescripcion()).append("\n\n");
        if (s.getRespuesta() == null) {
            texto.append("Respuesta del equipo de soporte:\nAún no hay una respuesta.");
        } else {
            texto.append("Respuesta del equipo de soporte (").append(EstiloUI.fecha(s.getFechaRespuesta()))
                    .append("):\n").append(s.getRespuesta());
        }
        JTextArea area = new JTextArea(texto.toString(), 16, 50);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Detalle de la solicitud",
                JOptionPane.PLAIN_MESSAGE);
    }
}
