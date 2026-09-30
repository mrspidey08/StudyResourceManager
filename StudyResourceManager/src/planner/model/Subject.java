package planner.model;

import java.util.Vector;

public class Subject {
    private int id;
    private String code;
    private String name;
    private Vector<CurriculumModule> modules;

    public Subject(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.modules = new Vector<CurriculumModule>();
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Vector<CurriculumModule> getModules() {
        return modules;
    }

    public void addModule(CurriculumModule m) {
        this.modules.add(m);
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }
}