package studentexamination;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    public void insert(Student s) throws SQLException {
        String sql = "INSERT INTO students " +
            "(roll_no, name, student_class, java_marks, python_marks, " +
            "dbms_marks, maths_marks, networks_marks, total, percentage, grade, result) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            fillStatement(ps, s);
            ps.executeUpdate();
        }
    }

    public void update(Student s) throws SQLException {
        String sql = "UPDATE students SET name=?, student_class=?, java_marks=?, " +
            "python_marks=?, dbms_marks=?, maths_marks=?, networks_marks=?, " +
            "total=?, percentage=?, grade=?, result=? WHERE roll_no=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getStudentClass());
            ps.setInt(3, s.getJavaMarks());
            ps.setInt(4, s.getPythonMarks());
            ps.setInt(5, s.getDbmsMarks());
            ps.setInt(6, s.getMathsMarks());
            ps.setInt(7, s.getNetworksMarks());
            ps.setInt(8, s.getTotal());
            ps.setDouble(9, s.getPercentage());
            ps.setString(10, s.getGrade());
            ps.setString(11, s.getResult());
            ps.setInt(12, s.getRollNo());
            int changed = ps.executeUpdate();
            if (changed == 0) {
                throw new SQLException("No student found with that roll number.");
            }
        }
    }

    private void fillStatement(PreparedStatement ps, Student s) throws SQLException {
        ps.setInt(1, s.getRollNo());
        ps.setString(2, s.getName());
        ps.setString(3, s.getStudentClass());
        ps.setInt(4, s.getJavaMarks());
        ps.setInt(5, s.getPythonMarks());
        ps.setInt(6, s.getDbmsMarks());
        ps.setInt(7, s.getMathsMarks());
        ps.setInt(8, s.getNetworksMarks());
        ps.setInt(9, s.getTotal());
        ps.setDouble(10, s.getPercentage());
        ps.setString(11, s.getGrade());
        ps.setString(12, s.getResult());
    }

    public void delete(int rollNo) throws SQLException {
        String sql = "DELETE FROM students WHERE roll_no=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rollNo);
            int changed = ps.executeUpdate();
            if (changed == 0) {
                throw new SQLException("No student found with that roll number.");
            }
        }
    }

    public Student findByRollNo(int rollNo) throws SQLException {
        String sql = "SELECT * FROM students WHERE roll_no=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return readStudent(rs);
            }
        }
        return null;
    }

    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY roll_no";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(readStudent(rs));
        }
        return list;
    }

    private Student readStudent(ResultSet rs) throws SQLException {
        Student s = new Student(
            rs.getInt("roll_no"),
            rs.getString("name"),
            rs.getString("student_class"),
            rs.getInt("java_marks"),
            rs.getInt("python_marks"),
            rs.getInt("dbms_marks"),
            rs.getInt("maths_marks"),
            rs.getInt("networks_marks")
        );
        s.setCalculatedResult(
            rs.getInt("total"),
            rs.getDouble("percentage"),
            rs.getString("grade"),
            rs.getString("result")
        );
        return s;
    }
}
