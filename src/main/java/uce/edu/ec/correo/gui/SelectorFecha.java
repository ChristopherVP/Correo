package uce.edu.ec.correo.gui;

import javax.swing.*;
import java.awt.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class SelectorFecha extends JPanel {

    private static final String[] MESES = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio",
            "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JComboBox<String> cmbDia = new JComboBox<>();
    private final JComboBox<String> cmbMes = new JComboBox<>();
    private final JComboBox<String> cmbAnio = new JComboBox<>();
    private boolean actualizando = false;

    public SelectorFecha(int anioMinimo, int anioMaximo) {
        super(new GridLayout(1, 3, 6, 0));
        setOpaque(false);

        cmbMes.addItem("Mes");
        for (int i = 0; i < MESES.length; i++) {
            cmbMes.addItem(String.format("%02d - %s", i + 1, MESES[i]));
        }
        cmbAnio.addItem("Año");
        for (int anio = anioMaximo; anio >= anioMinimo; anio--) {
            cmbAnio.addItem(String.valueOf(anio));
        }
        llenarDias(31, 0);

        for (JComboBox<String> combo : java.util.List.of(cmbDia, cmbMes, cmbAnio)) {
            combo.setFont(new Font("Arial", Font.PLAIN, 14));
            combo.setBackground(Color.WHITE);
            combo.setMaximumRowCount(10);
            add(combo);
        }
        cmbMes.addActionListener(e -> ajustarDias());
        cmbAnio.addActionListener(e -> ajustarDias());
    }

    private void llenarDias(int cantidad, int seleccionado) {
        actualizando = true;
        cmbDia.removeAllItems();
        cmbDia.addItem("Día");
        for (int dia = 1; dia <= cantidad; dia++) {
            cmbDia.addItem(String.format("%02d", dia));
        }
        cmbDia.setSelectedIndex(Math.min(seleccionado, cantidad));
        actualizando = false;
    }

    private void ajustarDias() {
        if (actualizando) {
            return;
        }
        int mes = cmbMes.getSelectedIndex();
        int anio = cmbAnio.getSelectedIndex() <= 0 ? 2000 : Integer.parseInt((String) cmbAnio.getSelectedItem());
        int cantidad = mes <= 0 ? 31 : YearMonth.of(anio, mes).lengthOfMonth();
        if (cmbDia.getItemCount() - 1 != cantidad) {
            llenarDias(cantidad, cmbDia.getSelectedIndex());
        }
    }

    public LocalDate getFecha() {
        if (cmbDia.getSelectedIndex() <= 0 || cmbMes.getSelectedIndex() <= 0 || cmbAnio.getSelectedIndex() <= 0) {
            return null;
        }
        try {
            return LocalDate.of(Integer.parseInt((String) cmbAnio.getSelectedItem()),
                    cmbMes.getSelectedIndex(), Integer.parseInt((String) cmbDia.getSelectedItem()));
        } catch (DateTimeException e) {
            return null;
        }
    }

    public String getFechaTexto() {
        LocalDate fecha = getFecha();
        return fecha == null ? "" : fecha.format(FORMATO);
    }

    public void setFecha(LocalDate fecha) {
        if (fecha == null) {
            limpiar();
            return;
        }
        cmbAnio.setSelectedItem(String.valueOf(fecha.getYear()));
        cmbMes.setSelectedIndex(fecha.getMonthValue());
        ajustarDias();
        cmbDia.setSelectedIndex(fecha.getDayOfMonth());
    }

    public void limpiar() {
        cmbAnio.setSelectedIndex(0);
        cmbMes.setSelectedIndex(0);
        llenarDias(31, 0);
    }

    @Override
    public void setEnabled(boolean habilitado) {
        super.setEnabled(habilitado);
        cmbDia.setEnabled(habilitado);
        cmbMes.setEnabled(habilitado);
        cmbAnio.setEnabled(habilitado);
    }
}
