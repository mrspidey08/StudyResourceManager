package planner.model;

public class Topic {
    private int id;
    private int moduleId;
    private String title;
    private double examWeightage;
    private boolean hasNotes;
    private boolean hasPyq;

    public Topic(int id, int moduleId, String title, double examWeightage, boolean hasNotes, boolean hasPyq) {
        this.id = id;
        this.moduleId = moduleId;
        this.title = title;
        this.examWeightage = examWeightage;
        this.hasNotes = hasNotes;
        this.hasPyq = hasPyq;
    }

    public int getId() {
        return id;
    }

    public int getModuleId() {
        return moduleId;
    }

    public String getTitle() {
        return title;
    }

    public double getExamWeightage() {
        return examWeightage;
    }

    public boolean hasNotes() {
        return hasNotes;
    }

    public boolean hasPyq() {
        return hasPyq;
    }

    public boolean isFullyCovered() {
        return hasNotes && hasPyq;
    }
}