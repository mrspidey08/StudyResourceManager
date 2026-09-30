package planner.model;

import planner.exception.CurriculumException;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebUrlResource extends Resource {

    public WebUrlResource(int id, int topicId, String title, String uriPointer) {
        super(id, topicId, title, uriPointer);
    }

    @Override
    public String getResourceType() {
        return "WEB_URL";
    }

    @Override
    public String getDisplayLocation() {
        return "URL: " + uriPointer;
    }

    @Override
    public void launch() throws CurriculumException {
        try {
            String target = uriPointer != null ? uriPointer.trim() : "";
            if (!target.toLowerCase().startsWith("http://") && !target.toLowerCase().startsWith("https://")) {
                target = "https://" + target;
            }
            URI uri = new URI(target);
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
            } else {
                throw new CurriculumException("Desktop browsing not supported on this platform.");
            }
        } catch (CurriculumException e) {
            throw e;
        } catch (Exception e) {
            throw new CurriculumException("Invalid or unreachable web resource: " + e.getMessage(), e);
        }
    }
}