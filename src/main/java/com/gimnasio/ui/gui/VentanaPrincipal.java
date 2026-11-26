package com.gimnasio.ui.gui;

import com.gimnasio.Aplicacion;
import com.gimnasio.fachada.GimnasioFacade;
import com.gimnasio.facturacion.Plan;
import com.gimnasio.notificaciones.NotificadorGUI;
import com.gimnasio.reservas.ClaseGrupal;
import com.gimnasio.reservas.Reserva;
import com.gimnasio.usuarios.Rol;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class VentanaPrincipal extends JFrame {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final GimnasioFacade fachada;
    private final CardLayout cartas = new CardLayout();
    private final JPanel raiz = new JPanel(cartas);
    private final List<Runnable> refrescos = new ArrayList<>();
    private String token;

    public VentanaPrincipal(GimnasioFacade fachada) {
        super("Gimnasio - Membresías y reservas");
        this.fachada = fachada;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setContentPane(raiz);
        mostrarLogin();
    }

    private void mostrarLogin() {
        token = null;
        refrescos.clear();
        JTextField correo = new JTextField(20);
        JPasswordField clave = new JPasswordField(20);
        JButton entrar = new JButton("Iniciar sesión");
        JButton registro = new JButton("Registrarme como cliente");

        JPanel form = new JPanel(new GridBagLayout());
        fila(form, 0, "Correo", correo);
        fila(form, 1, "Contraseña", clave);
        JPanel botones = new JPanel();
        botones.add(entrar);
        botones.add(registro);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.insets = new Insets(12, 4, 4, 4);
        form.add(botones, c);

        JLabel titulo = new JLabel("Gimnasio: membresías y reservas", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        titulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        JPanel login = new JPanel(new BorderLayout());
        login.add(titulo, BorderLayout.NORTH);
        JPanel centro = new JPanel(new GridBagLayout());
        centro.add(form);
        login.add(centro, BorderLayout.CENTER);

        Runnable iniciar = () -> accion(() -> {
            token = fachada.iniciarSesion(correo.getText(), new String(clave.getPassword()));
            mostrarSesion();
        });
        entrar.addActionListener(e -> iniciar.run());
        clave.addActionListener(e -> iniciar.run());
        registro.addActionListener(e -> dialogoRegistro());

        mostrar(login, "login");
    }

    private void dialogoRegistro() {
        JTextField nombre = new JTextField(18);
        JTextField correo = new JTextField(18);
        JPasswordField clave = new JPasswordField(18);
        JPanel p = new JPanel(new GridBagLayout());
        fila(p, 0, "Nombre", nombre);
        fila(p, 1, "Correo", correo);
        fila(p, 2, "Contraseña (mín. 6)", clave);
        if (JOptionPane.showConfirmDialog(this, p, "Registro de cliente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            accion(() -> {
                fachada.registrarCliente(nombre.getText(), correo.getText(), new String(clave.getPassword()));
                info("Registro exitoso. Ahora inicia sesión.");
            });
        }
    }

    private void mostrarSesion() {
        refrescos.clear();
        Rol rol = fachada.rolDe(token);
        JTabbedPane pestanas = new JTabbedPane();
        switch (rol) {
            case ADMINISTRADOR -> {
                pestanas.addTab("Instructores", panelInstructores());
                pestanas.addTab("Clases", panelAdminClases());
                pestanas.addTab("Planes", panelPlanes());
            }
            case INSTRUCTOR -> pestanas.addTab("Asistencia", panelAsistencia());
            case CLIENTE -> {
                pestanas.addTab("Clases", panelClienteClases());
                pestanas.addTab("Mis reservas", panelMisReservas());
                pestanas.addTab("Mi cuenta", panelCuenta());
            }
        }

        JLabel quien = new JLabel("  " + fachada.nombreDe(token) + "  (" + rol + ")");
        JButton salir = new JButton("Cerrar sesión");
        salir.addActionListener(e -> {
            fachada.cerrarSesion(token);
            mostrarLogin();
        });
        JPanel barra = new JPanel(new BorderLayout());
        barra.add(quien, BorderLayout.WEST);
        barra.add(salir, BorderLayout.EAST);
        barra.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel sesion = new JPanel(new BorderLayout());
        sesion.add(barra, BorderLayout.NORTH);
        sesion.add(pestanas, BorderLayout.CENTER);
        mostrar(sesion, "sesion");
        refrescarTodo();
    }

    private JPanel panelInstructores() {
        JTextField nombre = new JTextField(20), correo = new JTextField(20);
        JPasswordField clave = new JPasswordField(20);
        JButton crear = new JButton("Crear instructor");
        JPanel form = new JPanel(new GridBagLayout());
        fila(form, 0, "Nombre", nombre);
        fila(form, 1, "Correo", correo);
        fila(form, 2, "Contraseña", clave);
        crear.addActionListener(e -> accion(() -> {
            fachada.registrarUsuario(token, Rol.INSTRUCTOR, nombre.getText(), correo.getText(),
                    new String(clave.getPassword()));
            info("Instructor creado.");
            nombre.setText(""); correo.setText(""); clave.setText("");
        }));
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel col = new JPanel(new BorderLayout());
        col.add(form, BorderLayout.CENTER);
        col.add(crear, BorderLayout.SOUTH);
        p.add(col);
        return p;
    }

    private JPanel panelAdminClases() {
        JTextField nombre = new JTextField(12), instructor = new JTextField(14);
        JTextField inicio = new JTextField(LocalDateTime.now().plusDays(1).withMinute(0).format(FORMATO), 12);
        JTextField fin = new JTextField(LocalDateTime.now().plusDays(1).withMinute(0).plusHours(1).format(FORMATO), 12);
        JSpinner aforo = new JSpinner(new SpinnerNumberModel(10, 1, 200, 1));
        JButton programar = new JButton("Programar clase");

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Nombre")); form.add(nombre);
        form.add(new JLabel("Correo instructor")); form.add(instructor);
        form.add(new JLabel("Inicio")); form.add(inicio);
        form.add(new JLabel("Fin")); form.add(fin);
        form.add(new JLabel("Aforo")); form.add(aforo);
        form.add(programar);

        TablaClases tabla = new TablaClases();
        programar.addActionListener(e -> accion(() -> {
            fachada.crearClase(token, nombre.getText(), instructor.getText(),
                    LocalDateTime.parse(inicio.getText().trim(), FORMATO),
                    LocalDateTime.parse(fin.getText().trim(), FORMATO),
                    (Integer) aforo.getValue());
            info("Clase programada.");
            refrescarTodo();
        }));

        JPanel p = new JPanel(new BorderLayout());
        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(tabla.tabla), BorderLayout.CENTER);
        refrescos.add(tabla::refrescar);
        return p;
    }

    private JPanel panelPlanes() {
        JTextField correo = new JTextField(20);
        JComboBox<String> plan = new JComboBox<>(new String[]{"BASICO", "PRO", "ESTUDIANTE", "GRUPAL"});
        JSpinner miembros = new JSpinner(new SpinnerNumberModel(2, 2, 20, 1));
        JSpinner meses = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
        JPanel checks = new JPanel(new FlowLayout(FlowLayout.LEFT));
        List<JCheckBox> beneficios = new ArrayList<>();
        for (String b : fachada.beneficiosDisponibles()) {
            JCheckBox cb = new JCheckBox(b);
            beneficios.add(cb);
            checks.add(cb);
        }
        JButton asignar = new JButton("Asignar plan y facturar");
        JTextArea factura = new JTextArea(8, 40);
        factura.setEditable(false);
        factura.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPanel form = new JPanel(new GridBagLayout());
        fila(form, 0, "Correo del cliente", correo);
        fila(form, 1, "Plan", plan);
        fila(form, 2, "Miembros (solo grupal)", miembros);
        fila(form, 3, "Beneficios", checks);
        fila(form, 4, "Meses a pagar", meses);

        asignar.addActionListener(e -> accion(() -> {
            List<String> elegidos = new ArrayList<>();
            for (JCheckBox cb : beneficios) if (cb.isSelected()) elegidos.add(cb.getText());
            boolean grupal = "GRUPAL".equals(plan.getSelectedItem());
            factura.setText(fachada.asignarPlan(token, correo.getText(), (String) plan.getSelectedItem(),
                    grupal ? (Integer) miembros.getValue() : 0, elegidos, (Integer) meses.getValue()).toString());
        }));

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.add(form, BorderLayout.CENTER);
        arriba.add(asignar, BorderLayout.SOUTH);
        JPanel p = new JPanel(new BorderLayout());
        p.add(arriba, BorderLayout.NORTH);
        p.add(new JScrollPane(factura), BorderLayout.CENTER);
        return p;
    }
    private JPanel panelAsistencia() {
        TablaClases clases = new TablaClases();
        DefaultTableModel modelo = modeloNoEditable("Cliente", "Estado");
        JTable tablaReservas = new JTable(modelo);
        List<Reserva> reservas = new ArrayList<>();

        Runnable cargar = () -> {
            modelo.setRowCount(0);
            reservas.clear();
            ClaseGrupal c = clases.seleccionada();
            if (c == null) return;
            reservas.addAll(fachada.reservasDeClase(token, c.getId()));
            for (Reserva r : reservas) {
                modelo.addRow(new Object[]{corto(r.getClienteId()), r.getEstado()});
            }
        };
        clases.tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) accion(cargar);
        });

        JButton asistio = new JButton("Asistió");
        JButton noAsistio = new JButton("No asistió");
        asistio.addActionListener(e -> accion(() -> {
            Reserva r = elegida(tablaReservas, reservas);
            fachada.registrarAsistencia(token, r.getClaseId(), r.getId());
            cargar.run();
        }));
        noAsistio.addActionListener(e -> accion(() -> {
            Reserva r = elegida(tablaReservas, reservas);
            fachada.registrarNoAsistencia(token, r.getClaseId(), r.getId());
            cargar.run();
        }));

        JPanel abajo = new JPanel(new BorderLayout());
        abajo.add(new JLabel("  Reservas de la clase seleccionada"), BorderLayout.NORTH);
        abajo.add(new JScrollPane(tablaReservas), BorderLayout.CENTER);
        JPanel botones = new JPanel();
        botones.add(asistio);
        botones.add(noAsistio);
        abajo.add(botones, BorderLayout.SOUTH);

        JSplitPane p = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(clases.tabla), abajo);
        p.setResizeWeight(0.5);
        refrescos.add(clases::refrescar);
        return wrap(p);
    }
    private JPanel panelClienteClases() {
        TablaClases clases = new TablaClases();
        JButton reservar = new JButton("Reservar");
        JButton espera = new JButton("Unirme a la lista de espera");
        reservar.addActionListener(e -> accion(() -> {
            fachada.reservarClase(token, requerida(clases).getId());
            info("Reserva confirmada.");
            refrescarTodo();
        }));
        espera.addActionListener(e -> accion(() -> {
            boolean nuevo = fachada.entrarListaEspera(token, requerida(clases).getId());
            info(nuevo ? "Quedaste en la lista de espera." : "Ya estabas en la lista de espera.");
            refrescarTodo();
        }));
        JPanel botones = new JPanel();
        botones.add(reservar);
        botones.add(espera);
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(clases.tabla), BorderLayout.CENTER);
        p.add(botones, BorderLayout.SOUTH);
        refrescos.add(clases::refrescar);
        return p;
    }

    private JPanel panelMisReservas() {
        DefaultTableModel modelo = modeloNoEditable("Clase", "Inicio", "Estado");
        JTable tabla = new JTable(modelo);
        List<Reserva> mias = new ArrayList<>();
        JButton cancelar = new JButton("Cancelar reserva seleccionada");
        cancelar.addActionListener(e -> accion(() -> {
            Reserva r = elegida(tabla, mias);
            fachada.cancelarReserva(token, r.getClaseId(), r.getId());
            info("Reserva cancelada.");
            refrescarTodo();
        }));
        refrescos.add(() -> {
            modelo.setRowCount(0);
            mias.clear();
            mias.addAll(fachada.misReservas(token));
            List<ClaseGrupal> clases = fachada.listarClases(token);
            for (Reserva r : mias) {
                ClaseGrupal c = clases.stream().filter(x -> x.getId().equals(r.getClaseId())).findFirst().orElse(null);
                modelo.addRow(new Object[]{c == null ? "?" : c.getNombre(),
                        c == null ? "" : c.getInicio().format(FORMATO), r.getEstado()});
            }
        });
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        p.add(cancelar, BorderLayout.SOUTH);
        return p;
    }

    private JPanel panelCuenta() {
        JTextArea texto = new JTextArea();
        texto.setEditable(false);
        texto.setFont(texto.getFont().deriveFont(15f));
        texto.setMargin(new Insets(16, 16, 16, 16));
        refrescos.add(() -> {
            String plan = fachada.miPlan(token)
                    .map((Plan p) -> p.getDescripcion() + String.format("%n($%,.0f por mes)", p.getCostoMensual()))
                    .orElse("Aún no tienes un plan activo.\nPide al administrador que te asigne uno.");
            texto.setText("Plan:\n" + plan + "\n\nInfracciones activas: " + fachada.consultarInfracciones(token)
                    + (fachada.puedeReservar(token) ? "" : "\nNo puedes reservar en este momento."));
        });
        return wrap(new JScrollPane(texto));
    }

    private void mostrar(JPanel pantalla, String nombre) {
        raiz.removeAll();
        raiz.add(pantalla, nombre);
        cartas.show(raiz, nombre);
        raiz.revalidate();
        raiz.repaint();
    }
    private class TablaClases {
        final DefaultTableModel modelo = modeloNoEditable("Clase", "Instructor", "Inicio", "Fin", "Cupos", "En espera");
        final JTable tabla = new JTable(modelo);
        private final List<ClaseGrupal> clases = new ArrayList<>();

        TablaClases() {
            tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        void refrescar() {
            String idSeleccionada = seleccionada() == null ? null : seleccionada().getId();
            modelo.setRowCount(0);
            clases.clear();
            clases.addAll(fachada.listarClases(token));
            for (ClaseGrupal c : clases) {
                modelo.addRow(new Object[]{c.getNombre(), c.getInstructor(), c.getInicio().format(FORMATO),
                        c.getFin().toLocalTime(), c.getCuposDisponibles() + "/" + c.getAforoMaximo(),
                        c.getListaEspera().cantidad()});
            }
            for (int i = 0; i < clases.size(); i++) {
                if (clases.get(i).getId().equals(idSeleccionada)) {
                    tabla.setRowSelectionInterval(i, i);
                }
            }
        }

        ClaseGrupal seleccionada() {
            int fila = tabla.getSelectedRow();
            return (fila < 0 || fila >= clases.size()) ? null : clases.get(fila);
        }
    }

    private ClaseGrupal requerida(TablaClases t) {
        ClaseGrupal c = t.seleccionada();
        if (c == null) throw new IllegalArgumentException("Selecciona una clase de la tabla");
        return c;
    }

    private Reserva elegida(JTable tabla, List<Reserva> lista) {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= lista.size()) throw new IllegalArgumentException("Selecciona una reserva de la tabla");
        return lista.get(fila);
    }

    private void refrescarTodo() {
        for (Runnable r : refrescos) r.run();
    }
    private void accion(Runnable tarea) {
        try {
            tarea.run();
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            if (e.getMessage() != null && e.getMessage().startsWith("Sesión")) mostrarLogin();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage() == null ? "Datos inválidos (revisa el formato de fecha)" : e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void info(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Listo", JOptionPane.INFORMATION_MESSAGE);
    }

    private static DefaultTableModel modeloNoEditable(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private static void fila(JPanel p, int y, String etiqueta, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = y; c.insets = new Insets(4, 4, 4, 4); c.anchor = GridBagConstraints.WEST;
        c.gridx = 0; p.add(new JLabel(etiqueta), c);
        c.gridx = 1; p.add(campo, c);
    }

    private static JPanel wrap(JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    private static String corto(String id) {
        return id.length() > 8 ? id.substring(0, 8) : id;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new VentanaPrincipal(Aplicacion.crearNucleo(new NotificadorGUI())).setVisible(true));
    }
}
