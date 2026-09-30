package planner.model;

import planner.exception.CurriculumException;

public interface Launchable {
    void launch() throws CurriculumException;

    String getDisplayLocation();
}