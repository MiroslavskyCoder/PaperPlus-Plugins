package lxxv.shared.javascript.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects diagnostic messages during transpile/execute.
 */
public class Diagnostics {
    private final List<String> messages = new ArrayList<>();

    public void info(String message) {
        messages.add("INFO: " + message);
    }

    public void warn(String message) {
        messages.add("WARN: " + message);
    }

    public void error(String message) {
        messages.add("ERROR: " + message);
    }

    public List<String> messages() {
        return Collections.unmodifiableList(messages);
    }

    public boolean hasErrors() {
        return messages.stream().anyMatch(m -> m.startsWith("ERROR"));
    }
}
