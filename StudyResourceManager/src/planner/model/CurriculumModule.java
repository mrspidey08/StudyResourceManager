package planner.model;

import java.util.Vector;

public class CurriculumModule {
    private int id;
    private int subjectId;
    private int moduleNo;
    private String name;
    private Vector<Topic> topics;

    public CurriculumModule(int id, int subjectId, int moduleNo, String name) {
        this.id = id;
        this.subjectId = subjectId;
        this.moduleNo = moduleNo;
        this.name = name;
        this.topics = new Vector<Topic>();
    }

    public int getId() {
        return id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public int getModuleNo() {
        return moduleNo;
    }

    public String getName() {
        return name;
    }

    public Vector<Topic> getTopics() {
        return topics;
    }

    public void addTopic(Topic t) {
        this.topics.add(t);
    }

    @Override
    public String toString() {
        return "Module " + moduleNo + ": " + name;
    }
}