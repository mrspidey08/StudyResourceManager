package planner.model;

import planner.exception.CurriculumException;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class Resource implements Launchable {
    protected int id;
    protected int topicId;
    protected String title;
    protected String uriPointer;

    public Resource(int id, int topicId, String title, String uriPointer) {
        this.id = id;
        this.topicId = topicId;
        this.title = title;
        this.uriPointer = uriPointer;
    }

    public int getId() {
        return id;
    }

    public int getTopicId() {
        return topicId;
    }

    public String getTitle() {
        return title;
    }

    public String getUriPointer() {
        return uriPointer;
    }

    public abstract String getResourceType();
}