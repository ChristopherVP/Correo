package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.CentroAdministracion;
import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.InfoSuspension;
import uce.edu.ec.correo.dominio.Solicitud;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import java.awt.*;

public class DialogoApelacionSuspension extends JDialog {

    private final EmpresaCorreo empresa;
    private final String email;
    private final String password;
    private final JTextArea txtEstado = new JTextArea(9, 46);
    private final JTextArea txtApelacion = new JTextArea(6, 46);
    private final JButton btnEnviar = EstiloUI.boton("Enviar apelación");
    private final JLabel lblContador = new JLabel(" ");
    private boolean listo = false;

    public DialogoApelacionSuspension(Window propietario, EmpresaCorreo empresa, String email, String password) {
        super(propietario, "Cuenta suspendida", Dialog.ModalityType.APPLICATION_MODAL);
        this.empresa = empresa;
        this.email = email;
        this.password = password;
        construirInterfaz();
        if (!cargarEstado()) {
            dispose();
            return;
        }
        pack();
        setLocationRelativeTo(propietario);
        listo = true;
    }

    public void mostrar() {
        if (listo) {
            setVisible(true);
        }
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel superior = new JPanel(new BorderLayout(0, 6));
        superior.add(EstiloUI.titulo("Su cuenta está suspendida"), BorderLayout.NORTH);
        superior.add(EstiloUI.nota("Esto no cuenta como un intento fallido de inicio de sesión. "
                + "Puede enviar una apelación al soporte de EcuMail."), BorderLayout.SOUTH);
        raiz.add(superior, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        txtEstado.setEditable(false);
        txtEstado.setLineWrap(true);
        txtEstado.setWrapStyleWord(true);
        JScrollPane scrollEstado = new JScrollPane(txtEstado);
        scrollEstado.setBorder(BorderFactory.createTitledBorder("Estado de su cuenta y apelaciones"));
        centro.add(scrollEstado, BorderLayout.CENTER);

        JPanel formulario = new JPanel(new BorderLayout(0, 4));
        formulario.setBorder(BorderFactory.createTitledBorder("Nueva apelación (mínimo "
                + CentroAdministracion.DESCRIPCION_MIN + " caracteres)"));
        txtApelacion.setLineWrap(true);
        txtApelacion.setWrapStyleWord(true);
        formulario.add(new JScrollPane(txtApelacion), BorderLayout.CENTER);
        lblContador.setFont(new Font("Arial", Font.PLAIN, 11));
        lblContador.setForeground(Color.GRAY);
        formulario.add(lblContador, BorderLayout.SOUTH);
        centro.add(formulario, BorderLayout.SOUTH);
        raiz.add(centro, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnEnviar.addActionListener(e -> enviarApelacion());
        JButton btnCerrar = EstiloUI.boton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        botones.add(btnEnviar);
        botones.add(btnCerrar);
        raiz.add(botones, BorderLayout.SOUTH);

        setContentPane(raiz);
    }

    private boolean cargarEstado() {
        try {
            InfoSuspension info = empresa.consultarSuspension(email, password);
            StringBuilder texto = new StringBuilder();
            texto.append("Fecha de suspensión: ").append(EstiloUI.fecha(info.fecha())).append('\n');
            texto.append("Motivo: ").append(info.motivo() == null ? "No especificado" : info.motivo()).append("\n\n");
            if (info.apelaciones().isEmpty()) {
                texto.append("Aún no ha enviado ninguna apelación.");
            }
            for (Solicitud apelacion : info.apelaciones()) {
                texto.append("Apelación #").append(apelacion.getId()).append(" — ")
                        .append(EstiloUI.fecha(apelacion.getFechaCreacion())).append(" — ")
                        .append(apelacion.getEstado().getDescripcion()).append('\n')
                        .append(apelacion.getDescripcion()).append('\n');
                if (apelacion.getRespuesta() != null) {
                    texto.append("Respuesta del soporte: ").append(apelacion.getRespuesta()).append('\n');
                }
                texto.append('\n');
            }
            txtEstado.setText(texto.toString());
            txtEstado.setCaretPosition(0);

            boolean puedeApelar = !info.tieneApelacionAbierta();
            txtApelacion.setEnabled(puedeApelar);
            btnEnviar.setEnabled(puedeApelar);
            lblContador.setText(puedeApelar ? " " : "Ya tiene una apelación en curso. Espere la respuesta del soporte.");
            return true;
        } catch (ValidationException e) {
            EstiloUI.error(getOwner(), e.getMensaje());
            return false;
        }
    }

    private void enviarApelacion() {
        try {
            Solicitud apelacion = empresa.enviarApelacion(email, password, txtApelacion.getText());
            EstiloUI.info(this, "Su apelación #" + apelacion.getId() + " fue enviada. "
                    + "Vuelva a intentar iniciar sesión más tarde para ver la respuesta.");
            txtApelacion.setText("");
            cargarEstado();
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }
}
