package lxxv.shared.javascript.bridge;

import com.caoccao.javet.values.V8Value;
import lxxv.shared.javascript.runtime.JsValueConverter;

/**
 * Maps V8 values to Java objects.
 */
public class V8ValueMapper {
    public Object map(V8Value value) {
        return JsValueConverter.toJava(value);
    }
}
