package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.HojaReporte;
import uce.edu.ec.correo.dominio.PeriodoReporte;
import uce.edu.ec.correo.dominio.TipoReporte;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ExcelWriter;
import uce.edu.ec.correo.util.PdfWriter;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DialogoExportarReporte extends JDialog {

    private static final String[] QUINCENAS = {"Primera quincena (del 1 al 15)", "Segunda quincena (del 16 al fin de mes)"};

    private final EmpresaCorreo empresa;
    private final JComboBox<TipoReporte> cmbTipo = new JComboBox<>();
    private final JComboBox<PeriodoReporte> cmbPeriodo = new JComboBox<>(PeriodoReporte.values());
    private final JComboBox<String> cmbQuincena = new JComboBox<>(QUINCENAS);
    private final SelectorFecha selectorFecha;
    private final JLabel lblFecha = new JLabel(" ");
    private final JLabel lblQuincena = new JLabel("Quincena:");
    private final JLabel lblResumenPeriodo = new JLabel(" ");
    private final JRadioButton rbExcel = new JRadioButton("Excel (.xlsx)", true);
    private final JRadioButton rbPdf = new JRadioButton("PDF (.pdf)");

    public DialogoExportarReporte(Window propietario, EmpresaCorreo empresa) {
        super(propietario, "Exportar reportes", Dialog.ModalityType.APPLICATION_MODAL);
        this.empresa = empresa;
        int anioActual = LocalDate.now().getYear();
        this.selectorFecha = new SelectorFecha(anioActual - 10, anioActual);
        construirInterfaz();
        ajustarSegunPeriodo();
        pack();
        setLocationRelativeTo(propietario);
    }

    private void construirInterfaz() {
        try {
            for (TipoReporte tipo : empresa.tiposDeReportePermitidos()) {
                cmbTipo.addItem(tipo);
            }
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
        cmbPeriodo.setSelectedItem(PeriodoReporte.MENSUAL);
        cmbPeriodo.addActionListener(e -> ajustarSegunPeriodo());
        cmbQuincena.setSelectedIndex(LocalDate.now().getDayOfMonth() <= 15 ? 0 : 1);
        cmbQuincena.addActionListener(e -> actualizarResumen());
        selectorFecha.setFecha(LocalDate.now());
        selectorFecha.alCambiar(this::actualizarResumen);

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        raiz.add(EstiloUI.titulo("Exportar reportes"), BorderLayout.NORTH);

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        agregar(formulario, new JLabel("Reporte:"));
        agregar(formulario, cmbTipo);
        agregar(formulario, new JLabel("Periodo:"));
        agregar(formulario, cmbPeriodo);
        agregar(formulario, lblFecha);
        agregar(formulario, selectorFecha);
        agregar(formulario, lblQuincena);
        agregar(formulario, cmbQuincena);
        lblResumenPeriodo.setFont(new Font("Arial", Font.BOLD, 13));
        lblResumenPeriodo.setForeground(EstiloUI.AZUL);
        agregar(formulario, lblResumenPeriodo);
        agregar(formulario, new JLabel("Formato:"));
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbExcel);
        grupo.add(rbPdf);
        JPanel formatos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        formatos.add(rbExcel);
        formatos.add(rbPdf);
        agregar(formulario, formatos);
        formulario.add(EstiloUI.nota("Cada reporte exportado queda registrado en la auditoría. Los correos de "
                + "usuarios aparecen enmascarados."));
        raiz.add(formulario, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnExportar = EstiloUI.boton("Generar y guardar");
        btnExportar.addActionListener(e -> exportar());
        JButton btnCancelar = EstiloUI.boton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        botones.add(btnExportar);
        botones.add(btnCancelar);
        raiz.add(botones, BorderLayout.SOUTH);

        setContentPane(raiz);
    }

    private static void agregar(JPanel contenedor, JComponent componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, componente.getPreferredSize().height));
        contenedor.add(componente);
        contenedor.add(Box.createVerticalStrut(5));
    }

    private void ajustarSegunPeriodo() {
        PeriodoReporte periodo = (PeriodoReporte) cmbPeriodo.getSelectedItem();
        switch (periodo) {
            case DIARIO -> {
                selectorFecha.configurar(true, true);
                lblFecha.setText("Día del reporte:");
            }
            case QUINCENAL -> {
                selectorFecha.configurar(false, true);
                lblFecha.setText("Mes y año de la quincena:");
            }
            case MENSUAL -> {
                selectorFecha.configurar(false, true);
                lblFecha.setText("Mes y año del reporte:");
            }
            case ANUAL -> {
                selectorFecha.configurar(false, false);
                lblFecha.setText("Año del reporte:");
            }
        }
        lblQuincena.setVisible(periodo == PeriodoReporte.QUINCENAL);
        cmbQuincena.setVisible(periodo == PeriodoReporte.QUINCENAL);
        actualizarResumen();
        if (isDisplayable()) {
            pack();
        }
    }

    private LocalDate fechaReferencia() {
        LocalDate fecha = selectorFecha.getFecha();
        if (fecha != null && cmbPeriodo.getSelectedItem() == PeriodoReporte.QUINCENAL) {
            return fecha.withDayOfMonth(cmbQuincena.getSelectedIndex() == 0 ? 1 : 16);
        }
        return fecha;
    }

    private void actualizarResumen() {
        PeriodoReporte periodo = (PeriodoReporte) cmbPeriodo.getSelectedItem();
        LocalDate referencia = fechaReferencia();
        lblResumenPeriodo.setText(referencia == null || periodo == null
                ? " " : "Periodo a exportar: " + periodo.rango(referencia).etiqueta());
    }

    private String mensajeFechaIncompleta(PeriodoReporte periodo) {
        return switch (periodo) {
            case DIARIO -> "Seleccione el día, el mes y el año.";
            case QUINCENAL, MENSUAL -> "Seleccione el mes y el año.";
            case ANUAL -> "Seleccione el año.";
        };
    }

    private void exportar() {
        TipoReporte tipo = (TipoReporte) cmbTipo.getSelectedItem();
        PeriodoReporte periodo = (PeriodoReporte) cmbPeriodo.getSelectedItem();
        LocalDate referencia = fechaReferencia();
        if (tipo == null || periodo == null) {
            EstiloUI.error(this, "Seleccione el tipo de reporte y el periodo.");
            return;
        }
        if (referencia == null) {
            EstiloUI.error(this, mensajeFechaIncompleta(periodo));
            return;
        }
        if (periodo.rango(referencia).desde().isAfter(LocalDateTime.now())) {
            EstiloUI.error(this, "No se pueden generar reportes de un periodo que aún no comienza.");
            return;
        }
        boolean excel = rbExcel.isSelected();
        String extension = excel ? "xlsx" : "pdf";

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte");
        selector.setFileFilter(new FileNameExtensionFilter(excel ? "Libro de Excel (*.xlsx)" : "Documento PDF (*.pdf)",
                extension));
        selector.setSelectedFile(new File("reporte_" + tipo.name().toLowerCase() + "_" + periodo.name().toLowerCase()
                + "_" + referencia + "." + extension));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith("." + extension)) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + "." + extension);
        }
        if (archivo.exists()) {
            int opcion = JOptionPane.showConfirmDialog(this, "El archivo ya existe. ¿Desea reemplazarlo?",
                    "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            List<HojaReporte> hojas = empresa.generarReporte(tipo, periodo, referencia);
            Path destino = archivo.toPath();
            if (excel) {
                ExcelWriter.escribir(hojas, destino);
            } else {
                PdfWriter.escribir(hojas, destino);
            }
            EstiloUI.info(this, "Reporte guardado en:\n" + archivo.getAbsolutePath());
            dispose();
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        } catch (IOException e) {
            EstiloUI.error(this, "No se pudo guardar el archivo: " + e.getMessage());
        }
    }
}
