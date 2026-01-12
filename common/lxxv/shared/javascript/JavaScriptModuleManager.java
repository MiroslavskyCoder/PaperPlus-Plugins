package lxxv.shared.javascript.advanced;

import lxxv.shared.javascript.JavaScriptEngine;
import java.util.*;
import java.util.concurrent.*;

/**
 * Менеджер для управления JavaScript модулями и их зависимостями
 */
public class JavaScriptModuleManager {
    private final Map<String, JavaScriptModule> modules = new ConcurrentHashMap<>();
    private final Map<String, Object> exports = new ConcurrentHashMap<>();
    private final JavaScriptEngine engine;

    public JavaScriptModuleManager(JavaScriptEngine engine) {
        this.engine = engine;
    }

    /**
     * Регистрирует JS модуль
     */
    public void registerModule(String moduleName, String code, String... dependencies) {
        modules.put(moduleName, new JavaScriptModule(moduleName, code, dependencies));
    }

    /**
     * Загружает модуль и его зависимости
     */
    public Object loadModule(String moduleName) throws Exception {
        if (exports.containsKey(moduleName)) {
            return exports.get(moduleName);
        }

        JavaScriptModule module = modules.get(moduleName);
        if (module == null) {
            throw new Exception("Module not found: " + moduleName);
        }

        // Загрузить зависимости
        Map<String, Object> dependencies = new HashMap<>();
        for (String dep : module.getDependencies()) {
            dependencies.put(dep, loadModule(dep));
        }

        // Выполнить код модуля
        Map<String, Object> context = new HashMap<>(dependencies);
        context.put("exports", new HashMap<>());
        context.put("module", new HashMap<String, Object>() {{
            put("exports", new HashMap<>());
        }});

        engine.execute(module.getCode(), context);

        Object moduleExports = context.get("exports");
        if (moduleExports == null) {
            moduleExports = ((Map<?, ?>) context.get("module")).get("exports");
        }

        exports.put(moduleName, moduleExports);
        return moduleExports;
    }

    /**
     * Удаляет кэш модуля
     */
    public void unloadModule(String moduleName) {
        exports.remove(moduleName);
    }

    /**
     * Очищает кэш всех модулей
     */
    public void unloadAll() {
        exports.clear();
    }

    /**
     * Проверяет наличие модуля
     */
    public boolean hasModule(String moduleName) {
        return modules.containsKey(moduleName);
    }

    /**
     * Получает список загруженных модулей
     */
    public List<String> getLoadedModules() {
        return new ArrayList<>(exports.keySet());
    }

    /**
     * Получает список всех зарегистрированных модулей
     */
    public List<String> getRegisteredModules() {
        return new ArrayList<>(modules.keySet());
    }

    /**
     * Класс для хранения информации о модуле
     */
    public static class JavaScriptModule {
        private final String name;
        private final String code;
        private final String[] dependencies;

        public JavaScriptModule(String name, String code, String... dependencies) {
            this.name = name;
            this.code = code;
            this.dependencies = dependencies != null ? dependencies : new String[0];
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        public String[] getDependencies() {
            return dependencies;
        }
    }
}
