package lxxv.shared.javascript.mono;

import java.util.UUID;

/**
 * UUID helper for JS exposure.
 */
public final class MonoUuid {
    private MonoUuid() {}

    public static String random() {
        return UUID.randomUUID().toString();
    }
}
