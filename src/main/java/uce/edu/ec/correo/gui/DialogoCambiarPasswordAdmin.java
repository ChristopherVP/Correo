package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ReglasPassword;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DialogoCambiarPasswordAdmin extends JDialog {

    private static final Color VERDE = new Color(40, 167, 69);
    private static final Color GRIS = new Color(150, 150, 150);

    private final EmpresaCorreo empresa;
    private final boolean obligatorio;
    private final CampoPassword campoActual = new CampoPassword(22);
    private final CampoPassword campoNueva = new CampoPassword(22);
    private final CampoPassword campoConfirmar = new CampoPassword(22);
    private final JLabel[] requisitos = {new JLabel(), new JLabel(), new JLabel(), new JLabel()};
    private final JLabel lblError = new JLabel(" ");
    private final JButton btnGuardar = EstiloUI.boton("Guardar contraseña");
    private boolean cambiada = false;

    public DialogoCambiarPasswordAdmin(Window propietario, EmpresaCorreo empresa, boolean obligatorio) {
        super(propietario, "Cambiar contraseña", Dialog.ModalityType.APPLICATION_MODAL);
        this.empresa = empresa;
        this.obligatorio = obligatorio;
        construirInterfaz();
        actualizarRequisitos();
        pack();
        setResizable(false);
        setLocationRelativeTo(propietario);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelar();
            }
        });
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout(10, 12));
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel encabezado = new JPanel(new GridLayout(0, 1, 0, 4));
        encabezado.add(EstiloUI.titulo(obligatorio ? "Debe cambiar su contraseña" : "Cambiar contraseña"));
        encabezado.add(EstiloUI.nota(obligatorio
                ? "Por seguridad, debe reemplazar la contraseña temporal para continuar."
                : "Ingrese su contraseña actual y elija una nueva."));
        raiz.add(encabezado, BorderLayout.NORTH);

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        agregarCampo(formulario, "Contraseña actual:", campoActual);
        agregarCampo(formulario, "Nueva contraseña:", campoNueva);
        agregarCampo(formulario, "Confirmar nueva contraseña:", campoConfirmar);

        JPanel panelRequisitos = new JPanel(new GridLayout(0, 1, 0, 2));
        panelRequisitos.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelRequisitos.setBorder(BorderFactory.createTitledBorder("Requisitos"));
        for (JLabel requisito : requisitos) {
            requisito.setFont(new Font("Arial", Font.PLAIN, 12));
            panelRequisitos.add(requisito);
        }
        formulario.add(panelRequisitos);
        formulario.add(Box.createVerticalStrut(8));

        lblError.setForeground(EstiloUI.PELIGRO);
        lblError.setFont(new Font("Arial", Font.BOLD, 12));
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulario.add(lblError);
        raiz.add(formulario, BorderLayout.CENTER);

        DocumentListener oyente = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                alEscribir();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                alEscribir();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                alEscribir();
            }
        };
        campoActual.alCambiar(oyente);
        campoNueva.alCambiar(oyente);
        campoConfirmar.alCambiar(oyente);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGuardar.addActionListener(e -> guardar());
        JButton btnCancelar = EstiloUI.boton(obligatorio ? "Cerrar sesión" : "Cancelar");
        btnCancelar.addActionListener(e -> cancelar());
        botones.add(btnGuardar);
        botones.add(btnCancelar);
        raiz.add(botones, BorderLayout.SOUTH);

        setContentPane(raiz);
        getRootPane().setDefaultButton(btnGuardar);
    }

    private void agregarCampo(JPanel formulario, String titulo, CampoPassword campo) {
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(new Font("Arial", Font.BOLD, 13));
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulario.add(etiqueta);
        formulario.add(Box.createVerticalStrut(4));
        formulario.add(campo);
        formulario.add(Box.createVerticalStrut(10));
    }

    private void alEscribir() {
        lblError.setText(" ");
        campoActual.marcarError(false);
        actualizarRequisitos();
    }

    private void actualizarRequisitos() {
        String actual = campoActual.getPassword();
        String nueva = campoNueva.getPassword();
        String confirmar = campoConfirmar.getPassword();
        boolean[] cumplidos = {
                ReglasPassword.longitudValida(nueva),
                ReglasPassword.tieneMayuscula(nueva),
                !nueva.isEmpty() && !nueva.equals(actual),
                !nueva.isEmpty() && nueva.equals(confirmar)
        };
        String[] textos = {
                "Entre " + ReglasPassword.MINIMO + " y " + ReglasPassword.MAXIMO + " caracteres",
                "Al menos una letra mayúscula",
                "Diferente a la contraseña actual",
                "Coincide con la confirmación"
        };
        boolean todos = !actual.isEmpty();
        for (int i = 0; i < requisitos.length; i++) {
            requisitos[i].setText("● " + textos[i]);
            requisitos[i].setForeground(cumplidos[i] ? VERDE : GRIS);
            todos &= cumplidos[i];
        }
        btnGuardar.setEnabled(todos);
    }

    private void guardar() {
        try {
            empresa.cambiarPasswordAdministrador(campoActual.getPassword(), campoNueva.getPassword());
            cambiada = true;
            EstiloUI.info(this, "Su contraseña fue actualizada correctamente.");
            dispose();
        } catch (ValidationException e) {
            lblError.setText("<html>" + e.getMensaje() + "</html>");
            if ("Contraseña actual".equals(e.getCampo())) {
                campoActual.marcarError(true);
                campoActual.getCampo().requestFocusInWindow();
            }
        }
    }

    private void cancelar() {
        boolean hayDatos = !campoActual.estaVacio() || !campoNueva.estaVacio() || !campoConfirmar.estaVacio();
        if (obligatorio) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "Debe cambiar su contraseña para continuar.\nSi cancela, se cerrará su sesión.\n\n¿Desea cancelar?",
                    "Cambio de contraseña obligatorio", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opcion == JOptionPane.YES_OPTION) {
                dispose();
            }
            return;
        }
        if (hayDatos) {
            int opcion = JOptionPane.showConfirmDialog(this, "¿Desea descartar los cambios?",
                    "Cancelar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }
        dispose();
    }

    public boolean fueCambiada() {
        return cambiada;
    }
}
