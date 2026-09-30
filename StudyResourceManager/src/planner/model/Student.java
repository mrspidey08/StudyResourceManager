package planner.model;

public class Student extends User {
    public Student(int id, String username, String fullName) {
        super(id, username, fullName);
    }

    @Override
    public String getRole() {
        return "STUDENT";
    }

    @Override
    public boolean canModifyCurriculum() {
        return false;
    }
}