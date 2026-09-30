package planner.model;

public class Contributor extends User {
    public Contributor(int id, String username, String fullName) {
        super(id, username, fullName);
    }

    @Override
    public String getRole() {
        return "CONTRIBUTOR";
    }

    @Override
    public boolean canModifyCurriculum() {
        return true;
    }
}