package lxxv.shared.javascript.bridge;

/**
 * Represents a script registration handle.
 */
public class V8ScriptHandle {
    private final String id;
    private final String description;

    public V8ScriptHandle(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String id() {
        return id;
    }

    public String description() {
        return description;
    }
}
