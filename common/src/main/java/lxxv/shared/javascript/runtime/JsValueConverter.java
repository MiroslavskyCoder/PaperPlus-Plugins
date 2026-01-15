package lxxv.shared.javascript.runtime;

import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueDouble;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;

/**
 * Utility to convert V8 values into plain Java objects.
 */
public final class JsValueConverter {
    private JsValueConverter() {}

    public static Object toJava(V8Value value) {
        if (value == null || value.isNullOrUndefined()) {
            return null;
        }
        try {
            if (value instanceof V8ValueString) {
                return value.toString();
            }
            if (value instanceof V8ValueInteger) {
                return ((V8ValueInteger) value).getValue();
            }
            if (value instanceof V8ValueDouble) {
                return ((V8ValueDouble) value).getValue();
            }
            if (value instanceof V8ValueBoolean) {
                return ((V8ValueBoolean) value).getValue();
            }
            return value.toString();
        } catch (Exception ex) {
            return value.toString();
        }
    }
}
