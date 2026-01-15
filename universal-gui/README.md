# universal-gui (Java)

Мини-библиотека для быстрого создания нестандартных GUI в Bukkit/Paper.
Не требует plugin.yml: подключайте как зависимость и используйте
`GuiService` в своём плагине.

## Возможности
- Registry `GuiService` с обработкой кликов/закрытий
- `GuiItem` с обработчиком нажатия
- `GuiView` как контракт для вьюхи
- `ThemedView` с палитрами (`Theme.dark/light/neon/retroTerminal/glass`) и готовыми виджетами (Button, Checkbox, Number Input, Dropdown, Slider, Progress, Field, Stack/Grid)
- `PagedView<T>` для простых пагинированных меню

## Подключение (Gradle)
```kotlin
dependencies {
    implementation(project(":universal-gui"))
}
```

## Быстрый старт
```java
GuiService gui = new GuiService(plugin);
gui.registerEvents();

Inventory inv = GuiView.createInventory(3, "Custom Menu");
Map<Integer, GuiItem> items = new HashMap<>();
items.put(13, new GuiItem(new ItemStack(Material.DIAMOND), ctx -> {
    ctx.player().sendMessage("Clicked!");
}));

GuiView view = new GuiView() {
    public Inventory inventory() { return inv; }
    public Map<Integer, GuiItem> items() { return items; }
};

gui.open(player, view);
```

## ThemedView (темы + виджеты)
```java
Theme theme = Theme.neon(); // dark/light/neon/retroTerminal/glass
ThemedView view = new ThemedView("Settings", 5, theme);

view.addButton(Slots.at(0,4), "Apply", ctx -> ctx.player().sendMessage("Applied"));
view.addCheckbox(Slots.at(1,2), "Enable FX", false, enabled -> {});
view.addNumberInput(Slots.at(1,6), "Volume", 50, 5, 10, 0, 100, v -> {});
view.addDropdown(Slots.at(2,2), "Profile", List.of("Default", "Builder", "PvP"), 0, choice -> {});
view.addSlider(Slots.at(2,6), "Opacity", 0.5, 0.0, 1.0, 0.05, val -> {});
view.addProgress(Slots.at(3,4), "Sync", 0.42, "Syncing shop.json");
view.addField(Slots.at(4,2), "Build", "1.2.0");
gui.open(player, view);
```

## Пагинация
```java
List<String> data = List.of("A","B","C","D","E");
PagedView<String> view = new PagedView<>("List", 4, data, value -> {
    ItemStack stack = new ItemStack(Material.PAPER);
    stack.editMeta(meta -> meta.displayName(Component.text(value)));
    return stack;
});

gui.open(player, view);
```

## Заметки
- Библиотека компилируется под Java 17, Paper API 1.20.4.
- Обработчики кликов уже cancel-ят событие; если нужно разрешить перенос
  предметов, расширьте `GuiService`.
- Данные GUI хранятся в памяти; добавьте свои проверки прав/состояния в
  колбеки `GuiItem`.
