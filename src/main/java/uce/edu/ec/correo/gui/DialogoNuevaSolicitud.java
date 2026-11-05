package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.CentroAdministracion;
import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.Solicitud;
import uce.edu.ec.correo.dominio.TipoSolicitud;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class DialogoNuevaSolicitud extends JDialog {

    private final EmpresaCorreo empresa;
    private final JComboBox<TipoSolicitud> cmbTipo = new JComboBox<>(new TipoSolicitud[]{
            TipoSolicitud.QUEJA, TipoSolicitud.REPORTE_TECNICO, TipoSolicitud.RECOMENDACION});
    private final JTextField txtAsunto = new JTextField(30);
    private final JTextArea txtDescripcion = new JTextArea(8, 30);
    private final JLabel lblContador = new JLabel();
    private boolean enviada = false;

    public DialogoNuevaSolicitud(Window propietario, EmpresaCorreo empresa) {
        super(propietario, "Nueva solicitud", Dialog.ModalityType.APPLICATION_MODAL);
        this.empresa = empresa;
        construirInterfaz();
        pack();
        setLocationRelativeTo(propietario);
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formulario.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formulario.add(cmbTipo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formulario.add(new JLabel("Asunto:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formulario.add(txtAsunto, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formulario.add(new JLabel("Descripción (mínimo " + CentroAdministracion.DESCRIPCION_MIN + " caracteres):"), gbc);

        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarContador();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarContador();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarContador();
            }
        });
        gbc.gridy = 3;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formulario.add(new JScrollPane(txtDescripcion), gbc);

        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        lblContador.setFont(new Font("Arial", Font.PLAIN, 11));
        lblContador.setForeground(Color.GRAY);
        formulario.add(lblContador, gbc);

        gbc.gridy = 5;
        formulario.add(EstiloUI.nota("No incluya contraseñas ni datos sensibles. Solo el equipo de soporte "
                + "podrá ver esta solicitud."), gbc);
        actualizarContador();
        raiz.add(formulario, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnEnviar = EstiloUI.boton("Enviar");
        btnEnviar.addActionListener(e -> enviar());
        JButton btnCancelar = EstiloUI.boton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        botones.add(btnEnviar);
        botones.add(btnCancelar);
        raiz.add(botones, BorderLayout.SOUTH);

        setContentPane(raiz);
    }

    private void actualizarContador() {
        lblContador.setText(txtDescripcion.getText().length() + " / " + CentroAdministracion.DESCRIPCION_MAX);
    }

    private void enviar() {
        try {
            Solicitud solicitud = empresa.crearSolicitud((TipoSolicitud) cmbTipo.getSelectedItem(),
                    txtAsunto.getText(), txtDescripcion.getText());
            EstiloUI.info(this, "Su solicitud #" + solicitud.getId() + " fue enviada. "
                    + "Puede revisar su estado en el Centro de Solicitudes y Mejoras.");
            enviada = true;
            dispose();
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }

    public boolean fueEnviada() {
        return enviada;
    }
}
