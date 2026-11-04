package uce.edu.ec.correo.gui;

import uce.edu.ec.correo.dominio.EmpresaCorreo;
import uce.edu.ec.correo.dominio.EstadisticasSistema;
import uce.edu.ec.correo.util.EstiloUI;
import uce.edu.ec.correo.util.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class PanelAdminResumen extends JPanel implements PanelAdmin.Refrescable {

    private final EmpresaCorreo empresa;
    private final JPanel contenido = new JPanel();

    public PanelAdminResumen(EmpresaCorreo empresa) {
        this.empresa = empresa;
        setLayout(new BorderLayout(10, 10));
        setBackground(EstiloUI.FONDO);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel superior = new JPanel(new BorderLayout());
        superior.setOpaque(false);
        superior.add(EstiloUI.titulo("Resumen del sistema"), BorderLayout.WEST);
        JButton btnActualizar = EstiloUI.boton("Actualizar");
        btnActualizar.addActionListener(e -> refrescar());
        superior.add(btnActualizar, BorderLayout.EAST);
        add(superior, BorderLayout.NORTH);

        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        add(EstiloUI.nota("Solo se muestran datos agregados. El contenido de correos, contactos y chats "
                + "no es accesible para los administradores."), BorderLayout.SOUTH);
    }

    @Override
    public void refrescar() {
        try {
            EstadisticasSistema datos = empresa.obtenerEstadisticas();
            contenido.removeAll();

            JPanel indicadores = new JPanel(new GridLayout(2, 4, 10, 10));
            indicadores.setOpaque(false);
            indicadores.setAlignmentX(Component.LEFT_ALIGNMENT);
            indicadores.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
            indicadores.add(tarjeta("Usuarios", datos.totalUsuarios()));
            indicadores.add(tarjeta("Cuentas suspendidas", datos.usuariosSuspendidos()));
            indicadores.add(tarjeta("Correos enviados", datos.correosEnviados()));
            indicadores.add(tarjeta("Grupos", datos.totalGrupos()));
            indicadores.add(tarjeta("Administradores", datos.totalAdministradores()));
            indicadores.add(tarjeta("Solicitudes totales", datos.solicitudesTotales()));
            indicadores.add(tarjeta("Solicitudes abiertas", datos.solicitudesAbiertas()));
            indicadores.add(tarjeta("Cerradas", datos.solicitudesTotales() - datos.solicitudesAbiertas()));
            contenido.add(indicadores);
            contenido.add(Box.createVerticalStrut(15));

            JPanel distribuciones = new JPanel(new GridLayout(0, 3, 10, 10));
            distribuciones.setOpaque(false);
            distribuciones.setAlignmentX(Component.LEFT_ALIGNMENT);
            distribuciones.add(distribucion("Usuarios por dominio", datos.usuariosPorDominio()));
            distribuciones.add(distribucion("Usuarios por provincia", datos.usuariosPorProvincia()));
            distribuciones.add(distribucion("Usuarios por género", datos.usuariosPorGenero()));
            distribuciones.add(distribucion("Solicitudes por tipo", datos.solicitudesPorTipo()));
            distribuciones.add(distribucion("Solicitudes por estado", datos.solicitudesPorEstado()));
            contenido.add(distribuciones);

            contenido.revalidate();
            contenido.repaint();
        } catch (ValidationException e) {
            EstiloUI.error(this, e.getMensaje());
        }
    }

    private JPanel tarjeta(String titulo, int valor) {
        JPanel tarjeta = new JPanel(new GridLayout(2, 1));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(EstiloUI.BORDE, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        JLabel lblValor = new JLabel(String.valueOf(valor));
        lblValor.setFont(new Font("Arial", Font.BOLD, 28));
        lblValor.setForeground(EstiloUI.AZUL);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        tarjeta.add(lblValor);
        tarjeta.add(lblTitulo);
        return tarjeta;
    }

    private JScrollPane distribucion(String titulo, Map<String, Long> datos) {
        DefaultTableModel modelo = EstiloUI.modelo("Categoría", "Total");
        datos.forEach((categoria, total) -> modelo.addRow(new Object[]{categoria, total}));
        JScrollPane scroll = EstiloUI.conDesplazamiento(EstiloUI.tabla(modelo));
        scroll.setBorder(BorderFactory.createTitledBorder(titulo));
        scroll.setPreferredSize(new Dimension(260, 190));
        return scroll;
    }
}
