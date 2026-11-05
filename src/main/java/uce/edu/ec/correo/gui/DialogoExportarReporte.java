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
import java.util.List;

public class DialogoExportarReporte extends JDialog {

    private final EmpresaCorreo empresa;
    private final JComboBox<TipoReporte> cmbTipo = new JComboBox<>();
    private final JComboBox<PeriodoReporte> cmbPeriodo = new JComboBox<>(PeriodoReporte.values());
    private final SelectorFecha selectorFecha;
    private final JRadioButton rbExcel = new JRadioButton("Excel (.xlsx)", true);
    private final JRadioButton rbPdf = new JRadioButton("PDF (.pdf)");

    public DialogoExportarReporte(Window propietario, EmpresaCorreo empresa) {
        super(propietario, "Exportar reportes", Dialog.ModalityType.APPLICATION_MODAL);
        this.empresa = empresa;
        int anioActual = LocalDate.now().getYear();
        this.selectorFecha = new SelectorFecha(anioActual - 10, anioActual + 1);
        construirInterfaz();
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
        selectorFecha.setFecha(LocalDate.now());

        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        raiz.add(EstiloUI.titulo("Exportar reportes"), BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(0, 1, 4, 4));
        formulario.add(new JLabel("Reporte:"));
        formulario.add(cmbTipo);
        formulario.add(new JLabel("Periodo:"));
        formulario.add(cmbPeriodo);
        formulario.add(new JLabel("Fecha de referencia (el periodo se calcula a partir de ella):"));
        formulario.add(selectorFecha);
        formulario.add(new JLabel("Formato:"));
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbExcel);
        grupo.add(rbPdf);
        JPanel formatos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        formatos.add(rbExcel);
        formatos.add(rbPdf);
        formulario.add(formatos);
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

    private void exportar() {
        TipoReporte tipo = (TipoReporte) cmbTipo.getSelectedItem();
        PeriodoReporte periodo = (PeriodoReporte) cmbPeriodo.getSelectedItem();
        LocalDate fecha = selectorFecha.getFecha();
        if (fecha == null) {
            EstiloUI.error(this, "Seleccione la fecha de referencia completa (día, mes y año).");
            return;
        }
        boolean excel = rbExcel.isSelected();
        String extension = excel ? "xlsx" : "pdf";

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte");
        selector.setFileFilter(new FileNameExtensionFilter(excel ? "Libro de Excel (*.xlsx)" : "Documento PDF (*.pdf)",
                extension));
        selector.setSelectedFile(new File("reporte_" + tipo.name().toLowerCase() + "_" + periodo.name().toLowerCase()
                + "_" + fecha + "." + extension));
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
            List<HojaReporte> hojas = empresa.generarReporte(tipo, periodo, fecha);
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
