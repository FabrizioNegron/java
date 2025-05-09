package pe.edu.vallegrande;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GestionEstudiantes extends JFrame implements ActionListener {

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtCorreo;
    private JButton btnInsertar;
    private JButton btnModificar;
    private JButton btnEliminarLogico;
    private JButton btnListar;
    private JTextArea txtAreaLista;

    private static final String DB_URL = "jdbc:mysql://localhost:33060/dbExamen"; // Reemplaza con tu URL
    private static final String DB_USER = "root"; // Reemplaza con tu usuario
    private static final String DB_PASSWORD = "entrecodigosycafe"; // Reemplaza con tu contraseña

    public GestionEstudiantes() {
        setTitle("Gestión de Estudiantes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Panel para los campos de texto y botones
        JPanel panelCampos = new JPanel(new GridLayout(4, 2, 5, 5));
        JLabel lblNombre = new JLabel("Nombre:");
        txtNombre = new JTextField(20);
        JLabel lblApellido = new JLabel("Apellido:");
        txtApellido = new JTextField(20);
        JLabel lblCorreo = new JLabel("Correo:");
        txtCorreo = new JTextField(20);

        panelCampos.add(lblNombre);
        panelCampos.add(txtNombre);
        panelCampos.add(lblApellido);
        panelCampos.add(txtApellido);
        panelCampos.add(lblCorreo);
        panelCampos.add(txtCorreo);
        panelCampos.add(new JLabel()); // Espacio en blanco

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnInsertar = new JButton("Insertar");
        btnModificar = new JButton("Modificar");
        btnEliminarLogico = new JButton("Eliminar Lógico");
        btnListar = new JButton("Listar");

        btnInsertar.addActionListener(this);
        btnModificar.addActionListener(this);
        btnEliminarLogico.addActionListener(this);
        btnListar.addActionListener(this);

        panelBotones.add(btnInsertar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminarLogico);
        panelBotones.add(btnListar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelCampos, BorderLayout.NORTH);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        // Área de texto para listar los estudiantes
        txtAreaLista = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(txtAreaLista);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestionEstudiantes());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnInsertar) {
            insertarEstudiante();
        } else if (e.getSource() == btnModificar) {
            modificarEstudiante();
        } else if (e.getSource() == btnEliminarLogico) {
            eliminarLogicoEstudiante();
        } else if (e.getSource() == btnListar) {
            listarEstudiantes();
        }
    }

    private void insertarEstudiante() {
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String correo = txtCorreo.getText();

        String sql = "INSERT INTO estudiantes (nombre, apellido, correo, estado) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            pstmt.setString(3, correo);
            pstmt.setBoolean(4, true); // Establecemos el estado como activo por defecto
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Estudiante insertado correctamente.");
            limpiarCampos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al insertar estudiante: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modificarEstudiante() {
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String correo = txtCorreo.getText();
        String idStr = JOptionPane.showInputDialog(this, "Ingrese el ID del estudiante a modificar:");

        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                String sql = "UPDATE estudiantes SET nombre = ?, apellido = ?, correo = ? WHERE id = ?";
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, nombre);
                    pstmt.setString(2, apellido);
                    pstmt.setString(3, correo);
                    pstmt.setInt(4, id);
                    int filasActualizadas = pstmt.executeUpdate();
                    if (filasActualizadas > 0) {
                        JOptionPane.showMessageDialog(this, "Estudiante modificado correctamente.");
                        limpiarCampos();
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró ningún estudiante con ese ID.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al modificar estudiante: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El ID ingresado no es un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarLogicoEstudiante() {
        String idStr = JOptionPane.showInputDialog(this, "Ingrese el ID del estudiante a eliminar (lógico):");

        if (idStr != null && !idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                String sql = "UPDATE estudiantes SET estado = ? WHERE id = ?";
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setBoolean(1, false); // Establecemos el estado a inactivo (eliminación lógica)
                    pstmt.setInt(2, id);
                    int filasActualizadas = pstmt.executeUpdate();
                    if (filasActualizadas > 0) {
                        JOptionPane.showMessageDialog(this, "Estudiante eliminado (lógicamente) correctamente.");
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró ningún estudiante con ese ID.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar estudiante: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El ID ingresado no es un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listarEstudiantes() {
        txtAreaLista.setText(""); // Limpiamos el área de texto
        String sql = "SELECT id, nombre, apellido, correo, estado FROM estudiantes";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            StringBuilder sb = new StringBuilder("Lista de Estudiantes:\n");
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id")).append("\n");
                sb.append("Nombre: ").append(rs.getString("nombre")).append("\n");
                sb.append("Apellido: ").append(rs.getString("apellido")).append("\n");
                sb.append("Correo: ").append(rs.getString("correo")).append("\n");
                sb.append("Estado: ").append(rs.getBoolean("estado") ? "Activo" : "Inactivo").append("\n");
                sb.append("--------------------\n");
            }
            txtAreaLista.setText(sb.toString());

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al listar estudiantes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtCorreo.setText("");
    }
}