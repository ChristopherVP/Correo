package uce.edu.ec.correo.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class EstiloUI {

    public static final Color AZUL = new Color(0, 102, 204);
    public static final Color FONDO = new Color(245, 248, 250);
    public static final Color BORDE = new Color(200, 200, 200);
    public static final Color PELIGRO = new Color(220, 53, 69);

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private EstiloUI() {
    }

    public static JButton boton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 13));
        boton.setForeground(Color.BLACK);
        boton.setBackground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        return boton;
    }

    public static JButton botonPeligro(String texto) {
        JButton boton = boton(texto);
        boton.setForeground(PELIGRO);
        return boton;
    }

    public static JLabel titulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Arial", Font.BOLD, 20));
        etiqueta.setForeground(AZUL);
        return etiqueta;
    }

    public static JLabel nota(String texto) {
        JLabel etiqueta = new JLabel("<html>" + texto + "</html>");
        etiqueta.setFont(new Font("Arial", Font.ITALIC, 12));
        etiqueta.setForeground(new Color(100, 100, 100));
        return etiqueta;
    }

    public static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    public static JTable tabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(28);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tabla.getTableHeader().setReorderingAllowed(false);
        return tabla;
    }

    public static JScrollPane conDesplazamiento(JTable tabla) {
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE, 1));
        return scroll;
    }

    public static Object valorSeleccionado(JTable tabla, int columna) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        return tabla.getModel().getValueAt(tabla.convertRowIndexToModel(fila), columna);
    }

    public static String fecha(LocalDateTime fecha) {
        return fecha == null ? "—" : fecha.format(FECHA_HORA);
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}
