package studentexamination;

public class Student {
    private int rollNo;
    private String name;
    private String studentClass;
    private int javaMarks;
    private int pythonMarks;
    private int dbmsMarks;
    private int mathsMarks;
    private int networksMarks;
    private int total;
    private double percentage;
    private String grade;
    private String result;

    public Student(int rollNo, String name, String studentClass,
                   int javaMarks, int pythonMarks, int dbmsMarks,
                   int mathsMarks, int networksMarks) {
        this.rollNo = rollNo;
        this.name = name;
        this.studentClass = studentClass;
        this.javaMarks = javaMarks;
        this.pythonMarks = pythonMarks;
        this.dbmsMarks = dbmsMarks;
        this.mathsMarks = mathsMarks;
        this.networksMarks = networksMarks;
    }

    public int getRollNo() { return rollNo; }
    public String getName() { return name; }
    public String getStudentClass() { return studentClass; }
    public int getJavaMarks() { return javaMarks; }
    public int getPythonMarks() { return pythonMarks; }
    public int getDbmsMarks() { return dbmsMarks; }
    public int getMathsMarks() { return mathsMarks; }
    public int getNetworksMarks() { return networksMarks; }
    public int getTotal() { return total; }
    public double getPercentage() { return percentage; }
    public String getGrade() { return grade; }
    public String getResult() { return result; }

    public void setCalculatedResult(int total, double percentage,
                                    String grade, String result) {
        this.total = total;
        this.percentage = percentage;
        this.grade = grade;
        this.result = result;
    }
}
