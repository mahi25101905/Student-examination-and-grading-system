package studentexamination;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class Main extends JFrame {
    private final StudentDAO dao = new StudentDAO();
    private final StudentService service = new StudentService();

    private final JTextField rollField = new JTextField(10);
    private final JTextField nameField = new JTextField(18);
    private final JTextField classField = new JTextField(18);
    private final JTextField javaField = new JTextField(6);
    private final JTextField pythonField = new JTextField(6);
    private final JTextField dbmsField = new JTextField(6);
    private final JTextField mathsField = new JTextField(6);
    private final JTextField networksField = new JTextField(6);

    private final JLabel statusLabel = new JLabel("Ready");
    private final JLabel totalStudentsLabel = new JLabel("0");
    private final JLabel passedStudentsLabel = new JLabel("0");
    private final JLabel failedStudentsLabel = new JLabel("0");
    private final JLabel averageLabel = new JLabel("0.00%");
    private final DefaultTableModel tableModel = new DefaultTableModel(
        new String[]{"Roll No", "Name", "Class", "Java", "Python", "DBMS",
                     "Maths", "Networks", "Total", "Percentage", "Grade", "Result"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public Main() {
        super("Student Examination and Grading System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("STUDENT EXAMINATION AND GRADING SYSTEM", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(heading, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));
        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.add(createDashboardPanel(), BorderLayout.NORTH);
        topPanel.add(createFormPanel(), BorderLayout.CENTER);

        center.add(topPanel, BorderLayout.NORTH);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 12, 8, 12));
        add(statusLabel, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                loadSelectedRowIntoForm();
            }
        });

        refreshTable();
    }

    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel(new GridLayout(1, 4, 12, 12));

        dashboard.add(createStatCard("TOTAL STUDENTS", totalStudentsLabel));
        dashboard.add(createStatCard("PASSED STUDENTS", passedStudentsLabel));
        dashboard.add(createStatCard("FAILED STUDENTS", failedStudentsLabel));
        dashboard.add(createStatCard("AVERAGE PERCENTAGE", averageLabel));

        return dashboard;
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(5, 8));

        card.setBackground(new Color(235, 243, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(190, 210, 235)),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(new Color(30, 80, 150));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
    private JPanel createFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(8, 8));
        outer.setBorder(BorderFactory.createTitledBorder("Student Details and Examination Marks"));

        JPanel fields = new JPanel(new GridLayout(0, 4, 8, 8));
        addField(fields, "Roll Number", rollField);
        addField(fields, "Student Name", nameField);
        addField(fields, "Class / Year", classField);
        addField(fields, "Java (0-100)", javaField);
        addField(fields, "Python (0-100)", pythonField);
        addField(fields, "DBMS (0-100)", dbmsField);
        addField(fields, "Mathematics (0-100)", mathsField);
        addField(fields, "Computer Networks (0-100)", networksField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton saveButton = new JButton("Save Student");
        JButton updateButton = new JButton("Update Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton searchButton = new JButton("Search by Roll No");
        JButton clearButton = new JButton("Clear Form");
        JButton refreshButton = new JButton("Refresh Table");

        saveButton.addActionListener(e -> saveStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        searchButton.addActionListener(e -> searchStudent());
        clearButton.addActionListener(e -> clearForm());
        refreshButton.addActionListener(e -> refreshTable());

        buttons.add(saveButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(searchButton);
        buttons.add(clearButton);
        buttons.add(refreshButton);

        outer.add(fields, BorderLayout.CENTER);
        outer.add(buttons, BorderLayout.SOUTH);
        return outer;
    }

    private void addField(JPanel panel, String label, JTextField field) {
        JPanel cell = new JPanel(new BorderLayout(3, 3));
        cell.add(new JLabel(label), BorderLayout.NORTH);
        cell.add(field, BorderLayout.CENTER);
        panel.add(cell);
    }

    private Student readForm() {
        try {
            int roll = Integer.parseInt(rollField.getText().trim());
            String name = nameField.getText().trim();
            String studentClass = classField.getText().trim();

            int java = Integer.parseInt(javaField.getText().trim());
            int python = Integer.parseInt(pythonField.getText().trim());
            int dbms = Integer.parseInt(dbmsField.getText().trim());
            int maths = Integer.parseInt(mathsField.getText().trim());
            int networks = Integer.parseInt(networksField.getText().trim());

            Student s = new Student(roll, name, studentClass,
                                    java, python, dbms, maths, networks);
            return service.prepareStudent(s);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                "Roll number and all five marks must be whole numbers."
            );
        }
    }

    private void saveStudent() {
        try {
            Student s = readForm();
            dao.insert(s);
            refreshTable();
            showResult(s, "Student saved successfully.");
            clearForm();
        } catch (IllegalArgumentException | SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private void updateStudent() {
        try {
            Student s = readForm();
            dao.update(s);
            refreshTable();
            showResult(s, "Student record updated successfully.");
        } catch (IllegalArgumentException | SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            int roll = Integer.parseInt(rollField.getText().trim());
            int answer = JOptionPane.showConfirmDialog(
                this, "Delete student with roll number " + roll + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION
            );
            if (answer != JOptionPane.YES_OPTION) return;
            dao.delete(roll);
            refreshTable();
            clearForm();
            statusLabel.setText("Student deleted successfully.");
        } catch (NumberFormatException ex) {
            showError("Enter a valid roll number to delete.");
        } catch (SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private void searchStudent() {
        try {
            int roll = Integer.parseInt(rollField.getText().trim());
            Student s = dao.findByRollNo(roll);
            if (s == null) {
                showError("No student found with roll number " + roll + ".");
                return;
            }
            putStudentInForm(s);
            showResult(s, "Student found.");
        } catch (NumberFormatException ex) {
            showError("Enter a valid roll number to search.");
        } catch (SQLException ex) {
            showError(ex.getMessage());
        }
    }


private void refreshTable() {
    try {
        tableModel.setRowCount(0);

        List<Student> students = dao.findAll();

        int totalStudents = students.size();
        int passedStudents = 0;
        int failedStudents = 0;
        double totalPercentage = 0;

        for (Student s : students) {

            tableModel.addRow(new Object[]{
                s.getRollNo(), s.getName(), s.getStudentClass(),
                s.getJavaMarks(), s.getPythonMarks(), s.getDbmsMarks(),
                s.getMathsMarks(), s.getNetworksMarks(), s.getTotal(),
                String.format("%.2f%%", s.getPercentage()),
                s.getGrade(), s.getResult()
            });

            if ("PASS".equalsIgnoreCase(s.getResult())) {
                passedStudents++;
            } else {
                failedStudents++;
            }

            totalPercentage += s.getPercentage();
        }

        double averagePercentage = totalStudents > 0
                ? totalPercentage / totalStudents : 0;

        totalStudentsLabel.setText(String.valueOf(totalStudents));
        passedStudentsLabel.setText(String.valueOf(passedStudents));
        failedStudentsLabel.setText(String.valueOf(failedStudents));
        averageLabel.setText(String.format("%.2f%%", averagePercentage));

        statusLabel.setText("Total student records: " + totalStudents);

    } catch (SQLException ex) {
        statusLabel.setText("Database connection failed. Check MySQL setup.");
        showError(ex.getMessage());
    }
}

    private void loadSelectedRowIntoForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        rollField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        classField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        javaField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        pythonField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        dbmsField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
        mathsField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
        networksField.setText(String.valueOf(tableModel.getValueAt(row, 7)));
    }

    private void putStudentInForm(Student s) {
        rollField.setText(String.valueOf(s.getRollNo()));
        nameField.setText(s.getName());
        classField.setText(s.getStudentClass());
        javaField.setText(String.valueOf(s.getJavaMarks()));
        pythonField.setText(String.valueOf(s.getPythonMarks()));
        dbmsField.setText(String.valueOf(s.getDbmsMarks()));
        mathsField.setText(String.valueOf(s.getMathsMarks()));
        networksField.setText(String.valueOf(s.getNetworksMarks()));
    }

    private void showResult(Student s, String message) {
        statusLabel.setText(message + " Total: " + s.getTotal()
            + "/500 | Percentage: " + String.format("%.2f", s.getPercentage())
            + "% | Grade: " + s.getGrade() + " | Result: " + s.getResult());

        JOptionPane.showMessageDialog(this,
            "Roll Number: " + s.getRollNo()
            + "\nName: " + s.getName()
            + "\nTotal: " + s.getTotal() + "/500"
            + "\nPercentage: " + String.format("%.2f%%", s.getPercentage())
            + "\nGrade: " + s.getGrade()
            + "\nResult: " + s.getResult(),
            "Examination Result", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Notice", JOptionPane.WARNING_MESSAGE);
    }

    private void clearForm() {
        rollField.setText("");
        nameField.setText("");
        classField.setText("");
        javaField.setText("");
        pythonField.setText("");
        dbmsField.setText("");
        mathsField.setText("");
        networksField.setText("");
        table.clearSelection();
        statusLabel.setText("Form cleared.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
