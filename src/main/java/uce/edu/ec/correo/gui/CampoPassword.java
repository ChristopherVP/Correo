package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.IconoUtils;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class CampoPassword extends JPanel {

    private final JPasswordField campo = new JPasswordField();
    private final JToggleButton btnVer = new JToggleButton(IconoUtils.iconoOjoVisible(20));
    private final char caracterOculto;

    public CampoPassword(int columnas) {
        super(new BorderLayout(5, 0));
        setOpaque(false);
        campo.setColumns(columnas);
        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        caracterOculto = campo.getEchoChar();
        marcarError(false);

        btnVer.setFocusPainted(false);
        btnVer.setBackground(Color.WHITE);
        btnVer.setPreferredSize(new Dimension(45, 35));
        btnVer.setToolTipText("Mostrar contraseña");
        btnVer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVer.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE, 1));
        btnVer.addActionListener(e -> {
            boolean visible = btnVer.isSelected();
            campo.setEchoChar(visible ? (char) 0 : caracterOculto);
            btnVer.setIcon(visible ? IconoUtils.iconoOjoOculto(20) : IconoUtils.iconoOjoVisible(20));
            btnVer.setToolTipText(visible ? "Ocultar contraseña" : "Mostrar contraseña");
        });

        add(campo, BorderLayout.CENTER);
        add(btnVer, BorderLayout.EAST);
    }

    public String getPassword() {
        return new String(campo.getPassword());
    }

    public boolean estaVacio() {
        return campo.getPassword().length == 0;
    }

    public void limpiar() {
        campo.setText("");
    }

    public void marcarError(boolean hayError) {
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(hayError ? EstiloUI.PELIGRO : EstiloUI.BORDE, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    public void alCambiar(DocumentListener oyente) {
        campo.getDocument().addDocumentListener(oyente);
    }

    public JPasswordField getCampo() {
        return campo;
    }
}
