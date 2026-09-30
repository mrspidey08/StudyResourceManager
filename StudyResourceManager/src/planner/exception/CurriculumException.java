package planner.exception;

public class CurriculumException extends Exception {
    public CurriculumException(String message) {
        super(message);
    }

    public CurriculumException(String message, Throwable cause) {
        super(message, cause);
    }
}