package lxxv.shared.javascript.runtime;

/**
 * Holds transpile output data.
 */
public class TranspileResult {
    private final String code;
    private final String sourceMap;

    public TranspileResult(String code, String sourceMap) {
        this.code = code;
        this.sourceMap = sourceMap;
    }

    public String code() {
        return code;
    }

    public String sourceMap() {
        return sourceMap;
    }
}
