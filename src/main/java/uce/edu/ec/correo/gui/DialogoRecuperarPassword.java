package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.PreguntaSeguridad;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DialogoRecuperarPassword extends JDialog {

    private final EmpresaCorreo empresa;
    private final JTextField txtEmail = new JTextField(26);
    private final Map<PreguntaSeguridad, JTextField> camposRespuesta = new LinkedHashMap<>();
    private final JPasswordField txtNueva = new JPasswordField(20);
    private final JPasswordField txtConfirmar = new JPasswordField(20);
    private String emailVerificando = "";

    public DialogoRecuperarPassword(Window propietario, EmpresaCorreo empresa) {
        super(propietario, "Recuperar contraseña", Dialog.ModalityType.APPLICATION_MODAL);
        this.empresa = empresa;
        mostrarPasoEmail();
        setResizable(false);
        setLocationRelativeTo(propietario);
    }

    private void mostrarPasoEmail() {
        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel centro = new JPanel(new GridLayout(0, 1, 5, 5));
        centro.add(EstiloUI.titulo("¿Olvidó su contraseña?"));
        centro.add(new JLabel("Ingrese el email de su cuenta:"));
        txtEmail.setText("");
        centro.add(txtEmail);
        raiz.add(centro, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnContinuar = EstiloUI.boton("Continuar");
        btnContinuar.addActionListener(e -> continuar());
        JButton btnCancelar = EstiloUI.boton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        botones.add(btnContinuar);
        botones.add(btnCancelar);
        raiz.add(botones, BorderLayout.SOUTH);

        setContentPane(raiz);
        getRootPane().setDefaultButton(btnContinuar);
        pack();
    }

    private void continuar() {
        try {
            emailVerificando = txtEmail.getText().trim().toLowerCase();
            List<PreguntaSeguridad> preguntas = empresa.obtenerPreguntasRecuperacion(emailVerificando);
            mostrarPasoPreguntas(preguntas);
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }

    private void mostrarPasoPreguntas(List<PreguntaSeguridad> preguntas) {
        camposRespuesta.clear();
        txtNueva.setText("");
        txtConfirmar.setText("");

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel formulario = new JPanel(new GridLayout(0, 1, 4, 4));
        formulario.add(EstiloUI.titulo("Verifique su identidad"));
        formulario.add(EstiloUI.nota("Responda con los datos personales registrados en su cuenta."));
        for (PreguntaSeguridad pregunta : preguntas) {
            JTextField campo = new JTextField(26);
            camposRespuesta.put(pregunta, campo);
            formulario.add(new JLabel(pregunta.getEnunciado() + ":"));
            formulario.add(campo);
        }
        formulario.add(new JLabel("Nueva contraseña (8-14 caracteres, al menos una mayúscula):"));
        formulario.add(txtNueva);
        formulario.add(new JLabel("Confirmar nueva contraseña:"));
        formulario.add(txtConfirmar);

        JCheckBox chkMostrar = new JCheckBox("Mostrar contraseñas");
        chkMostrar.addActionListener(e -> {
            char eco = chkMostrar.isSelected() ? (char) 0 : '•';
            txtNueva.setEchoChar(eco);
            txtConfirmar.setEchoChar(eco);
        });
        formulario.add(chkMostrar);
        raiz.add(formulario, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnRestablecer = EstiloUI.boton("Restablecer contraseña");
        btnRestablecer.addActionListener(e -> restablecer());
        JButton btnVolver = EstiloUI.boton("Volver");
        btnVolver.addActionListener(e -> mostrarPasoEmail());
        botones.add(btnRestablecer);
        botones.add(btnVolver);
        raiz.add(botones, BorderLayout.SOUTH);

        setContentPane(raiz);
        getRootPane().setDefaultButton(btnRestablecer);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void restablecer() {
        String nueva = new String(txtNueva.getPassword());
        if (!nueva.equals(new String(txtConfirmar.getPassword()))) {
            EstiloUI.error(this, "Las contraseñas no coinciden.");
            return;
        }
        Map<PreguntaSeguridad, String> respuestas = new EnumMap<>(PreguntaSeguridad.class);
        camposRespuesta.forEach((pregunta, campo) -> respuestas.put(pregunta, campo.getText()));
        try {
            empresa.restablecerPassword(emailVerificando, respuestas, nueva);
            EstiloUI.info(this, "Su contraseña fue restablecida. Ya puede iniciar sesión.");
            dispose();
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }
}
