package studentexamination;

public class StudentService {
    public Student prepareStudent(Student student) {
        if (student.getRollNo() <= 0) {
            throw new IllegalArgumentException("Roll number must be greater than zero.");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }
        if (student.getStudentClass() == null || student.getStudentClass().trim().isEmpty()) {
            throw new IllegalArgumentException("Class cannot be empty.");
        }

        int[] marks = {
            student.getJavaMarks(), student.getPythonMarks(),
            student.getDbmsMarks(), student.getMathsMarks(),
            student.getNetworksMarks()
        };

        int total = 0;
        boolean passedEverySubject = true;

        for (int mark : marks) {
            if (mark < 0 || mark > 100) {
                throw new IllegalArgumentException("Every subject mark must be between 0 and 100.");
            }
            total += mark;
            if (mark < 35) {
                passedEverySubject = false;
            }
        }

        double percentage = total / 5.0;
        String result = passedEverySubject ? "PASS" : "FAIL";
        String grade;

        if (!passedEverySubject) {
            grade = "F";
        } else if (percentage >= 90) {
            grade = "A+";
        } else if (percentage >= 80) {
            grade = "A";
        } else if (percentage >= 70) {
            grade = "B+";
        } else if (percentage >= 60) {
            grade = "B";
        } else if (percentage >= 50) {
            grade = "C";
        } else {
            grade = "D";
        }

        student.setCalculatedResult(total, percentage, grade, result);
        return student;
    }
}
