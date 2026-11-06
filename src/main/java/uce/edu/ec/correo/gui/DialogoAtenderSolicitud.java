package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.EstadoSolicitud;
import uce.edu.ec.correo.dominio.InfoSuspension;
import uce.edu.ec.correo.dominio.Solicitud;
import uce.edu.ec.correo.dominio.TipoSolicitud;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import java.awt.*;

public class DialogoAtenderSolicitud extends JDialog {

    private final EmpresaCorreo empresa;
    private final Solicitud solicitud;
    private final boolean esApelacion;
    private final JComboBox<EstadoSolicitud> cmbEstado = new JComboBox<>(new EstadoSolicitud[]{
            EstadoSolicitud.EN_REVISION, EstadoSolicitud.RESUELTA, EstadoSolicitud.RECHAZADA});
    private final JTextArea txtRespuesta = new JTextArea(6, 40);
    private final JButton btnGuardar = EstiloUI.boton("Guardar");
    private final String respuestaInicial;
    private boolean guardado = false;

    public DialogoAtenderSolicitud(Window propietario, EmpresaCorreo empresa, Solicitud solicitud) {
        super(propietario, solicitud.getTipo() == TipoSolicitud.APELACION
                        ? "Apelación de cuenta suspendida" : "Solicitud #" + solicitud.getId(),
                Dialog.ModalityType.APPLICATION_MODAL);
        this.empresa = empresa;
        this.solicitud = solicitud;
        this.esApelacion = solicitud.getTipo() == TipoSolicitud.APELACION;
        this.respuestaInicial = solicitud.getRespuesta() == null ? "" : solicitud.getRespuesta();
        construirInterfaz();
        pack();
        setMinimumSize(new Dimension(600, 600));
        setLocationRelativeTo(propietario);
    }

    private String textoCabecera() {
        StringBuilder texto = new StringBuilder("<html><b>" + solicitud.getTipo().getDescripcion() + "</b> — "
                + EstiloUI.fecha(solicitud.getFechaCreacion()) + "<br>Usuario: " + solicitud.getEmailUsuario()
                + "<br>Asunto: " + solicitud.getAsunto());
        if (esApelacion) {
            texto.append("<br>Estado de la cuenta: ");
            try {
                InfoSuspension info = empresa.consultarSuspensionComoAdmin(solicitud.getEmailUsuario());
                if (info == null) {
                    texto.append("<b>no está suspendida</b>");
                } else {
                    texto.append("<b>suspendida</b> desde ").append(EstiloUI.fecha(info.fecha()))
                            .append("<br>Motivo de la suspensión: ")
                            .append(info.motivo() == null ? "No especificado" : info.motivo());
                }
            } catch (ValidationException e) {
                texto.append("no disponible");
            }
        }
        return texto.append("</html>").toString();
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblCabecera = new JLabel(textoCabecera());
        lblCabecera.setFont(new Font("Arial", Font.PLAIN, 13));
        raiz.add(lblCabecera, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 10));
        JTextArea txtDescripcion = new JTextArea(solicitud.getDescripcion(), 6, 40);
        txtDescripcion.setEditable(false);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setBorder(BorderFactory.createTitledBorder(
                esApelacion ? "Apelación del usuario" : "Descripción del usuario"));
        centro.add(scrollDescripcion);

        JPanel respuesta = new JPanel(new BorderLayout(5, 5));
        respuesta.setBorder(BorderFactory.createTitledBorder("Respuesta para el usuario"));
        JPanel filaEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaEstado.add(new JLabel("Decisión:"));
        cmbEstado.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean seleccionado, boolean enfoque) {
                Component componente = super.getListCellRendererComponent(lista, valor, indice, seleccionado, enfoque);
                if (valor instanceof EstadoSolicitud estado) {
                    setText(etiquetaEstado(estado));
                }
                return componente;
            }
        });
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            cmbEstado.setSelectedItem(solicitud.getEstado());
        }
        filaEstado.add(cmbEstado);
        respuesta.add(filaEstado, BorderLayout.NORTH);
        txtRespuesta.setLineWrap(true);
        txtRespuesta.setWrapStyleWord(true);
        txtRespuesta.setText(respuestaInicial);
        respuesta.add(new JScrollPane(txtRespuesta), BorderLayout.CENTER);
        centro.add(respuesta);
        raiz.add(centro, BorderLayout.CENTER);

        boolean apelacionCerrada = esApelacion && !solicitud.getEstado().estaAbierta();
        JPanel inferior = new JPanel(new BorderLayout(0, 8));
        if (apelacionCerrada) {
            cmbEstado.setEnabled(false);
            txtRespuesta.setEditable(false);
            btnGuardar.setEnabled(false);
            inferior.add(EstiloUI.nota("Esta apelación ya fue atendida y no puede modificarse."), BorderLayout.NORTH);
        }
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGuardar.addActionListener(e -> guardar());
        JButton btnCancelar = EstiloUI.boton("Cancelar");
        btnCancelar.addActionListener(e -> cancelar());
        botones.add(btnGuardar);
        botones.add(btnCancelar);
        inferior.add(botones, BorderLayout.SOUTH);
        raiz.add(inferior, BorderLayout.SOUTH);

        setContentPane(raiz);
    }

    private String etiquetaEstado(EstadoSolicitud estado) {
        if (!esApelacion) {
            return estado.getDescripcion();
        }
        return switch (estado) {
            case RESUELTA -> "Aceptar apelación y reactivar la cuenta";
            case RECHAZADA -> "Rechazar apelación (la cuenta sigue suspendida)";
            default -> estado.getDescripcion();
        };
    }

    private boolean confirmarDecision(EstadoSolicitud estado) {
        String mensaje;
        if (esApelacion && estado == EstadoSolicitud.RESUELTA) {
            mensaje = "<html><body style='width: 340px;'><b>Se reactivará la cuenta " + solicitud.getEmailUsuario()
                    + "</b> y el usuario podrá volver a ingresar.<br><br>¿Está seguro de aceptar la apelación?</body></html>";
        } else if (esApelacion && estado == EstadoSolicitud.RECHAZADA) {
            mensaje = "<html><body style='width: 340px;'>La cuenta <b>" + solicitud.getEmailUsuario()
                    + "</b> seguirá suspendida y el usuario verá su respuesta.<br><br>¿Está seguro de rechazar la apelación?</body></html>";
        } else {
            return true;
        }
        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar decisión",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return opcion == JOptionPane.YES_OPTION;
    }

    private void guardar() {
        EstadoSolicitud estado = (EstadoSolicitud) cmbEstado.getSelectedItem();
        if (estado != EstadoSolicitud.EN_REVISION && txtRespuesta.getText().trim().length() < 10) {
            EstiloUI.error(this, "Escriba una respuesta de al menos 10 caracteres para el usuario.");
            txtRespuesta.requestFocusInWindow();
            return;
        }
        if (!confirmarDecision(estado)) {
            return;
        }
        try {
            empresa.responderSolicitud(solicitud.getId(), estado, txtRespuesta.getText());
            guardado = true;
            if (esApelacion && estado == EstadoSolicitud.RESUELTA) {
                EstiloUI.info(this, "Apelación aceptada. La cuenta fue reactivada.");
            }
            dispose();
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }

    private void cancelar() {
        if (!txtRespuesta.getText().equals(respuestaInicial)) {
            int opcion = JOptionPane.showConfirmDialog(this, "¿Desea descartar la respuesta escrita?",
                    "Cancelar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }
        dispose();
    }

    public boolean fueGuardado() {
        return guardado;
    }
}
