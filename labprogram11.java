import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/student";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void insertStudent(int regno, String name, String program, int semester, String subject, int marks) {
        String query = "INSERT INTO students (regno, name, program, semester, subject, marks) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, regno);
            stmt.setString(2, name);
            stmt.setString(3, program);
            stmt.setInt(4, semester);
            stmt.setString(5, subject);
            stmt.setInt(6, marks);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Student record added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateMarks(int regno, int semester, String subject, int newMarks) {
        String query = "UPDATE students SET marks = ? WHERE regno = ? AND semester = ? AND subject = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newMarks);
            stmt.setInt(2, regno);
            stmt.setInt(3, semester);
            stmt.setString(4, subject);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Marks updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "No matching record found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Vector<Vector<String>> fetchStudentRecords() {
        Vector<Vector<String>> data = new Vector<>();
        String query = "SELECT * FROM students";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(String.valueOf(rs.getInt("regno")));
                row.add(rs.getString("name"));
                row.add(rs.getString("program"));
                row.add(String.valueOf(rs.getInt("semester")));
                row.add(rs.getString("subject"));
                row.add(String.valueOf(rs.getInt("marks")));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}

public class labprogram11 extends JFrame {
    private JTextField txtRegNo, txtName, txtProgram, txtSemester, txtSubject, txtMarks;
    private JTable table;
    private DefaultTableModel tableModel;

    public labprogram11() {
        setTitle("Student Management System");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // UI Styling
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));

        inputPanel.add(new JLabel("Register No:"));
        txtRegNo = new JTextField();
        inputPanel.add(txtRegNo);

        inputPanel.add(new JLabel("Name:"));
        txtName = new JTextField();
        inputPanel.add(txtName);

        inputPanel.add(new JLabel("Program:"));
        txtProgram = new JTextField();
        inputPanel.add(txtProgram);

        inputPanel.add(new JLabel("Semester:"));
        txtSemester = new JTextField();
        inputPanel.add(txtSemester);

        inputPanel.add(new JLabel("Subject:"));
        txtSubject = new JTextField();
        inputPanel.add(txtSubject);

        inputPanel.add(new JLabel("Marks:"));
        txtMarks = new JTextField();
        inputPanel.add(txtMarks);

        JButton btnAdd = new JButton("Add Student");
        JButton btnUpdate = new JButton("Update Marks");
        inputPanel.add(btnAdd);
        inputPanel.add(btnUpdate);

        add(inputPanel, BorderLayout.NORTH);

        // Table Panel
        tableModel = new DefaultTableModel(new String[]{"RegNo", "Name", "Program", "Semester", "Subject", "Marks"}, 0);
        table = new JTable(tableModel);
        refreshTable();
        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER);

        // Button Actions
        btnAdd.addActionListener(this::addStudent);
        btnUpdate.addActionListener(this::updateMarks);
    }

    private void addStudent(ActionEvent e) {
        try {
            int regno = Integer.parseInt(txtRegNo.getText());
            String name = txtName.getText();
            String program = txtProgram.getText();
            int semester = Integer.parseInt(txtSemester.getText());
            String subject = txtSubject.getText();
            int marks = Integer.parseInt(txtMarks.getText());

            DatabaseManager.insertStudent(regno, name, program, semester, subject, marks);
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMarks(ActionEvent e) {
        try {
            int regno = Integer.parseInt(txtRegNo.getText());
            int semester = Integer.parseInt(txtSemester.getText());
            String subject = txtSubject.getText();
            int newMarks = Integer.parseInt(txtMarks.getText());

            DatabaseManager.updateMarks(regno, semester, subject, newMarks);
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Vector<String> row : DatabaseManager.fetchStudentRecords()) {
            tableModel.addRow(row);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new labprogram11().setVisible(true));
    }
}
