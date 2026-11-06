package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.PreguntaSeguridad;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ReglasPassword;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class DialogoRecuperarPassword extends JDialog {

    private static final Pattern PATRON_EMAIL = Pattern.compile("^[a-z0-9._+-]+@[a-z0-9-]+(\\.[a-z0-9-]+)+$");
    private static final int LARGO_DIGITOS = 10;

    private record Entrada(Supplier<String> valor, Supplier<String> error, JLabel lblError) {
    }

    private final EmpresaCorreo empresa;
    private final JTextField txtEmail = new JTextField(26);
    private final JLabel lblErrorEmail = crearEtiquetaError();
    private final Map<PreguntaSeguridad, Entrada> entradas = new LinkedHashMap<>();
    private final CampoPassword campoNueva = new CampoPassword(20);
    private final CampoPassword campoConfirmar = new CampoPassword(20);
    private final JLabel lblErrorNueva = crearEtiquetaError();
    private final JLabel lblErrorConfirmar = crearEtiquetaError();
    private final JLabel lblErrorGeneral = crearEtiquetaError();
    private String emailVerificando = "";
    private boolean enPasoPreguntas = false;

    public DialogoRecuperarPassword(Window propietario, EmpresaCorreo empresa) {
        super(propietario, "Recuperar contraseña", Dialog.ModalityType.APPLICATION_MODAL);
        this.empresa = empresa;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelar();
            }
        });
        mostrarPasoEmail();
        setResizable(false);
        setLocationRelativeTo(propietario);
    }

    private static JLabel crearEtiquetaError() {
        JLabel etiqueta = new JLabel(" ");
        etiqueta.setForeground(EstiloUI.PELIGRO);
        etiqueta.setFont(new Font("Arial", Font.PLAIN, 12));
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    private static void alinear(JComponent componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, componente.getPreferredSize().height));
    }

    private void mostrarPasoEmail() {
        enPasoPreguntas = false;
        lblErrorEmail.setText(" ");
        txtEmail.setText(emailVerificando);

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        JLabel titulo = EstiloUI.titulo("¿Olvidó su contraseña?");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        centro.add(titulo);
        centro.add(Box.createVerticalStrut(8));
        JLabel nota = EstiloUI.nota("Escriba el email de su cuenta. Luego le pediremos algunos datos personales "
                + "registrados para verificar que es usted.");
        nota.setAlignmentX(Component.LEFT_ALIGNMENT);
        centro.add(nota);
        centro.add(Box.createVerticalStrut(12));
        JLabel etiqueta = new JLabel("Email de su cuenta:");
        etiqueta.setFont(new Font("Arial", Font.BOLD, 13));
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        centro.add(etiqueta);
        centro.add(Box.createVerticalStrut(4));
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        alinear(txtEmail);
        centro.add(txtEmail);
        centro.add(lblErrorEmail);
        raiz.add(centro, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnContinuar = EstiloUI.boton("Continuar");
        btnContinuar.addActionListener(e -> continuar());
        JButton btnCancelar = EstiloUI.boton("Cancelar");
        btnCancelar.addActionListener(e -> cancelar());
        botones.add(btnContinuar);
        botones.add(btnCancelar);
        raiz.add(botones, BorderLayout.SOUTH);

        setContentPane(raiz);
        getRootPane().setDefaultButton(btnContinuar);
        pack();
        txtEmail.requestFocusInWindow();
    }

    private void continuar() {
        String email = txtEmail.getText().trim().toLowerCase();
        if (email.isEmpty()) {
            lblErrorEmail.setText("Ingrese el email de su cuenta.");
            return;
        }
        if (!PATRON_EMAIL.matcher(email).matches()) {
            lblErrorEmail.setText("Ingrese un email válido, por ejemplo usuario@gmail.com.");
            return;
        }
        try {
            List<PreguntaSeguridad> preguntas = empresa.obtenerPreguntasRecuperacion(email);
            emailVerificando = email;
            mostrarPasoPreguntas(preguntas);
        } catch (ValidationException e) {
            lblErrorEmail.setText("<html>" + e.getMensaje() + "</html>");
        }
    }

    private Entrada crearEntrada(PreguntaSeguridad pregunta, JPanel destino) {
        JLabel lblError = crearEtiquetaError();
        JLabel etiqueta = new JLabel(pregunta.getEnunciado() + ":");
        etiqueta.setFont(new Font("Arial", Font.BOLD, 13));
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        destino.add(etiqueta);
        destino.add(Box.createVerticalStrut(4));

        Entrada entrada;
        if (pregunta == PreguntaSeguridad.FECHA_NACIMIENTO) {
            int anioActual = LocalDate.now().getYear();
            SelectorFecha selector = new SelectorFecha(anioActual - 100, anioActual);
            alinear(selector);
            destino.add(selector);
            entrada = new Entrada(selector::getFechaTexto,
                    () -> selector.getFecha() == null ? "Seleccione el día, el mes y el año completos." : null,
                    lblError);
        } else {
            JTextField campo = new JTextField(20);
            campo.setFont(new Font("Arial", Font.PLAIN, 14));
            ((AbstractDocument) campo.getDocument()).setDocumentFilter(new FiltroDigitos());
            alinear(campo);
            destino.add(campo);
            String nombre = pregunta == PreguntaSeguridad.CEDULA ? "La cédula" : "El teléfono";
            entrada = new Entrada(campo::getText,
                    () -> campo.getText().length() == LARGO_DIGITOS
                            ? null : nombre + " debe tener " + LARGO_DIGITOS + " dígitos numéricos.",
                    lblError);
        }
        destino.add(lblError);
        return entrada;
    }

    private void mostrarPasoPreguntas(List<PreguntaSeguridad> preguntas) {
        enPasoPreguntas = true;
        entradas.clear();
        campoNueva.limpiar();
        campoConfirmar.limpiar();
        lblErrorNueva.setText(" ");
        lblErrorConfirmar.setText(" ");
        lblErrorGeneral.setText(" ");

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        JLabel titulo = EstiloUI.titulo("Verifique su identidad");
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulario.add(titulo);
        formulario.add(Box.createVerticalStrut(4));
        JLabel nota = EstiloUI.nota("Cuenta: " + emailVerificando
                + ". Responda con los datos personales registrados en ella.");
        nota.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulario.add(nota);
        formulario.add(Box.createVerticalStrut(10));

        for (PreguntaSeguridad pregunta : preguntas) {
            entradas.put(pregunta, crearEntrada(pregunta, formulario));
        }

        JLabel etiquetaNueva = new JLabel("Nueva contraseña (" + ReglasPassword.DESCRIPCION.toLowerCase() + "):");
        etiquetaNueva.setFont(new Font("Arial", Font.BOLD, 13));
        etiquetaNueva.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulario.add(etiquetaNueva);
        formulario.add(Box.createVerticalStrut(4));
        alinear(campoNueva);
        formulario.add(campoNueva);
        formulario.add(lblErrorNueva);

        JLabel etiquetaConfirmar = new JLabel("Confirmar nueva contraseña:");
        etiquetaConfirmar.setFont(new Font("Arial", Font.BOLD, 13));
        etiquetaConfirmar.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulario.add(etiquetaConfirmar);
        formulario.add(Box.createVerticalStrut(4));
        alinear(campoConfirmar);
        formulario.add(campoConfirmar);
        formulario.add(lblErrorConfirmar);
        lblErrorGeneral.setFont(new Font("Arial", Font.BOLD, 12));
        formulario.add(lblErrorGeneral);
        raiz.add(formulario, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnRestablecer = EstiloUI.boton("Restablecer contraseña");
        btnRestablecer.addActionListener(e -> restablecer());
        JButton btnVolver = EstiloUI.boton("Volver");
        btnVolver.addActionListener(e -> volver());
        JButton btnCancelar = EstiloUI.boton("Cancelar");
        btnCancelar.addActionListener(e -> cancelar());
        botones.add(btnRestablecer);
        botones.add(btnVolver);
        botones.add(btnCancelar);
        raiz.add(botones, BorderLayout.SOUTH);

        setContentPane(raiz);
        getRootPane().setDefaultButton(btnRestablecer);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private boolean validarCampos() {
        boolean correcto = true;
        for (Entrada entrada : entradas.values()) {
            String error = entrada.error().get();
            entrada.lblError().setText(error == null ? " " : error);
            correcto &= error == null;
        }
        String nueva = campoNueva.getPassword();
        boolean passwordValida = ReglasPassword.cumple(nueva);
        lblErrorNueva.setText(passwordValida ? " " : "Debe tener " + ReglasPassword.DESCRIPCION.toLowerCase() + ".");
        campoNueva.marcarError(!passwordValida);
        boolean coincide = !nueva.isEmpty() && nueva.equals(campoConfirmar.getPassword());
        lblErrorConfirmar.setText(coincide ? " " : "Las contraseñas no coinciden.");
        campoConfirmar.marcarError(!coincide);
        lblErrorGeneral.setText(" ");
        return correcto && passwordValida && coincide;
    }

    private void restablecer() {
        if (!validarCampos()) {
            return;
        }
        Map<PreguntaSeguridad, String> respuestas = new EnumMap<>(PreguntaSeguridad.class);
        entradas.forEach((pregunta, entrada) -> respuestas.put(pregunta, entrada.valor().get()));
        try {
            empresa.restablecerPassword(emailVerificando, respuestas, campoNueva.getPassword());
            EstiloUI.info(this, "Su contraseña fue restablecida. Ya puede iniciar sesión.");
            dispose();
        } catch (ValidationException e) {
            lblErrorGeneral.setText("<html>" + e.getMensaje() + "</html>");
            if (empresa.segundosBloqueoRecuperacion(emailVerificando) > 0) {
                EstiloUI.error(this, e.getMensaje());
                dispose();
            }
        }
    }

    private boolean hayDatosIngresados() {
        boolean hayRespuestas = entradas.values().stream()
                .anyMatch(entrada -> !entrada.valor().get().isEmpty());
        return hayRespuestas || !campoNueva.estaVacio() || !campoConfirmar.estaVacio();
    }

    private void volver() {
        if (hayDatosIngresados()) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "Si vuelve, se perderán los datos ingresados. ¿Desea continuar?",
                    "Volver", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }
        mostrarPasoEmail();
    }

    private void cancelar() {
        boolean hayDatos = enPasoPreguntas ? hayDatosIngresados() : !txtEmail.getText().trim().isEmpty();
        if (hayDatos) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "¿Desea cancelar la recuperación de contraseña? Se perderán los datos ingresados.",
                    "Cancelar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }
        dispose();
    }

    private static class FiltroDigitos extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int posicion, String texto, AttributeSet atributos)
                throws BadLocationException {
            replace(fb, posicion, 0, texto, atributos);
        }

        @Override
        public void replace(FilterBypass fb, int posicion, int largo, String texto, AttributeSet atributos)
                throws BadLocationException {
            String soloDigitos = texto == null ? "" : texto.replaceAll("\\D", "");
            int disponible = LARGO_DIGITOS - (fb.getDocument().getLength() - largo);
            if (disponible <= 0 && !soloDigitos.isEmpty()) {
                return;
            }
            if (soloDigitos.length() > disponible) {
                soloDigitos = soloDigitos.substring(0, Math.max(disponible, 0));
            }
            super.replace(fb, posicion, largo, soloDigitos, atributos);
        }
    }
}
