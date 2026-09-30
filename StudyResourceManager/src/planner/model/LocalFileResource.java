package planner.model;

import planner.exception.CurriculumException;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class LocalFileResource extends Resource {

    public LocalFileResource(int id, int topicId, String title, String uriPointer) {
        super(id, topicId, title, uriPointer);
    }

    @Override
    public String getResourceType() {
        return "LOCAL_FILE";
    }

    @Override
    public String getDisplayLocation() {
        return "File: " + uriPointer;
    }

    @Override
    public void launch() throws CurriculumException {
        File file = new File(uriPointer);
        if (!file.exists()) {
            throw new CurriculumException("Target local file not found: " + uriPointer);
        }
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            } else {
                throw new CurriculumException("Desktop file opening is not supported on this platform.");
            }
        } catch (IOException e) {
            throw new CurriculumException("Failed to open file: " + e.getMessage(), e);
        }
    }
}