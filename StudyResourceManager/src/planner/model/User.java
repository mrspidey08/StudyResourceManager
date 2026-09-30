package planner.model;

public abstract class User {
    protected int id;
    protected String username;
    protected String fullName;

    public User(int id, String username, String fullName) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public abstract String getRole();

    public abstract boolean canModifyCurriculum();
}