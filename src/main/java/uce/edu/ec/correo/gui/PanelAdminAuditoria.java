package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.RegistroAuditoria;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelAdminAuditoria extends JPanel implements PanelAdmin.Refrescable {

    private final EmpresaCorreo empresa;
    private final DefaultTableModel modelo = EstiloUI.modelo("Fecha", "Administrador", "Acción", "Objetivo", "Detalle");

    public PanelAdminAuditoria(EmpresaCorreo empresa) {
        this.empresa = empresa;
        setLayout(new BorderLayout(10, 10));
        setBackground(EstiloUI.FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel superior = new JPanel(new BorderLayout());
        superior.setOpaque(false);
        superior.add(EstiloUI.titulo("Registro de auditoría"), BorderLayout.WEST);
        JButton btnActualizar = EstiloUI.boton("Actualizar");
        btnActualizar.addActionListener(e -> refrescar());
        superior.add(btnActualizar, BorderLayout.EAST);
        add(superior, BorderLayout.NORTH);

        JTable tabla = EstiloUI.tabla(modelo);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(320);
        add(EstiloUI.conDesplazamiento(tabla), BorderLayout.CENTER);
        add(EstiloUI.nota("Los correos de los usuarios afectados se guardan enmascarados para minimizar datos personales."),
                BorderLayout.SOUTH);
    }

    @Override
    public void refrescar() {
        try {
            List<RegistroAuditoria> registros = empresa.listarAuditoria();
            modelo.setRowCount(0);
            for (int i = registros.size() - 1; i >= 0; i--) {
                RegistroAuditoria r = registros.get(i);
                modelo.addRow(new Object[]{EstiloUI.fecha(r.getFecha()), r.getEmailAdmin(), r.getAccion(),
                        r.getObjetivo(), r.getDetalle()});
            }
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }
}
