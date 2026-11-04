package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.EstadoSolicitud;
import uce.edu.ec.correo.dominio.Solicitud;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import java.awt.*;

public class DialogoAtenderSolicitud extends JDialog {

    private final EmpresaCorreo empresa;
    private final Solicitud solicitud;
    private final JComboBox<EstadoSolicitud> cmbEstado = new JComboBox<>(new EstadoSolicitud[]{
            EstadoSolicitud.EN_REVISION, EstadoSolicitud.RESUELTA, EstadoSolicitud.RECHAZADA});
    private final JTextArea txtRespuesta = new JTextArea(6, 40);
    private boolean guardado = false;

    public DialogoAtenderSolicitud(Window propietario, EmpresaCorreo empresa, Solicitud solicitud) {
        super(propietario, "Solicitud #" + solicitud.getId(), Dialog.ModalityType.APPLICATION_MODAL);
        this.empresa = empresa;
        this.solicitud = solicitud;
        construirInterfaz();
        pack();
        setMinimumSize(new Dimension(560, 560));
        setLocationRelativeTo(propietario);
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblCabecera = new JLabel("<html><b>" + solicitud.getTipo().getDescripcion() + "</b> — "
                + EstiloUI.fecha(solicitud.getFechaCreacion()) + "<br>Usuario: " + solicitud.getEmailUsuario()
                + "<br>Asunto: " + solicitud.getAsunto() + "</html>");
        lblCabecera.setFont(new Font("Arial", Font.PLAIN, 13));
        raiz.add(lblCabecera, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 10));
        JTextArea txtDescripcion = new JTextArea(solicitud.getDescripcion(), 6, 40);
        txtDescripcion.setEditable(false);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setBorder(BorderFactory.createTitledBorder("Descripción del usuario"));
        centro.add(scrollDescripcion);

        JPanel respuesta = new JPanel(new BorderLayout(5, 5));
        respuesta.setBorder(BorderFactory.createTitledBorder("Respuesta para el usuario"));
        JPanel filaEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaEstado.add(new JLabel("Nuevo estado:"));
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            cmbEstado.setSelectedItem(solicitud.getEstado());
        }
        filaEstado.add(cmbEstado);
        respuesta.add(filaEstado, BorderLayout.NORTH);
        txtRespuesta.setLineWrap(true);
        txtRespuesta.setWrapStyleWord(true);
        if (solicitud.getRespuesta() != null) {
            txtRespuesta.setText(solicitud.getRespuesta());
        }
        respuesta.add(new JScrollPane(txtRespuesta), BorderLayout.CENTER);
        centro.add(respuesta);
        raiz.add(centro, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnGuardar = EstiloUI.boton("Guardar");
        btnGuardar.addActionListener(e -> guardar());
        JButton btnCancelar = EstiloUI.boton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        botones.add(btnGuardar);
        botones.add(btnCancelar);
        raiz.add(botones, BorderLayout.SOUTH);

        setContentPane(raiz);
    }

    private void guardar() {
        try {
            empresa.responderSolicitud(solicitud.getId(), (EstadoSolicitud) cmbEstado.getSelectedItem(),
                    txtRespuesta.getText());
            guardado = true;
            dispose();
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }

    public boolean fueGuardado() {
        return guardado;
    }
}
