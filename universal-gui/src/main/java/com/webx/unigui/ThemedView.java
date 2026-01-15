package com.webx.unigui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Ready-to-use themed view with dark palette and helpers for common widgets:
 * button, checkbox, numeric input, read-only field, grid/stack placement.
 */
public final class ThemedView implements GuiView {
    private final Inventory inventory;
    private final Map<Integer, GuiItem> items = new HashMap<>();
    private final Theme theme;

    public ThemedView(String title, int rows, Theme theme) {
        this.theme = theme;
        this.inventory = GuiView.createInventory(rows, theme.titlePrefix() + title);
        fillBackground();
    }

    @Override
    public Inventory inventory() {
        return inventory;
    }

    @Override
    public Map<Integer, GuiItem> items() {
        return items;
    }

    /** Fills all empty slots with a muted background pane. */
    public void fillBackground() {
        ItemStack bg = new ItemStack(theme.backgroundMaterial());
        bg.editMeta(meta -> meta.displayName(Component.text(" ", NamedTextColor.DARK_GRAY)));
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, bg);
        }
    }

    public void addButton(int slot, String label, Consumer<GuiContext> onClick) {
        ItemStack stack = styled(theme.buttonMaterial(), Component.text(label, theme.primary()));
        put(slot, stack, onClick);
    }

    public void addField(int slot, String label, String value) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(value, theme.secondary()));
        ItemStack stack = styled(theme.fieldMaterial(), Component.text(label, theme.primary()), lore);
        put(slot, stack, null);
    }

    public void addCheckbox(int slot, String label, boolean initial, Consumer<Boolean> onToggle) {
        boolean[] state = {initial};
        ItemStack stack = checkboxItem(label, state[0]);
        put(slot, stack, ctx -> {
            state[0] = !state[0];
            ItemStack updated = checkboxItem(label, state[0]);
            inventory.setItem(slot, updated);
            items.put(slot, new GuiItem(updated, this.items.get(slot)::handleClick));
            if (onToggle != null) onToggle.accept(state[0]);
        });
    }

    public void addNumberInput(int slot, String label, double initial, double step, double stepShift,
                               double min, double max, Consumer<Double> onChange) {
        double[] value = {clampValue(initial, min, max)};
        Consumer<Double> update = v -> {
            value[0] = clampValue(v, min, max);
            ItemStack updated = numberItem(label, value[0], step, stepShift);
            inventory.setItem(slot, updated);
        };
        update.accept(value[0]);
        put(slot, inventory.getItem(slot), ctx -> {
            double v = value[0];
            ClickType click = ctx.clickType();
            if (click.isLeftClick()) v += click.isShiftClick() ? stepShift : step;
            else if (click.isRightClick()) v -= click.isShiftClick() ? stepShift : step;
            else if (click == ClickType.MIDDLE) v = initial;
            update.accept(v);
            if (onChange != null) onChange.accept(value[0]);
        });
    }

    public void addDropdown(int slot, String label, List<String> options, int initialIndex, Consumer<String> onChange) {
        if (options == null || options.isEmpty()) return;
        int[] index = {clampIndex(initialIndex, options.size())};

        Consumer<Integer> update = idx -> {
            index[0] = clampIndex(idx, options.size());
            ItemStack updated = dropdownItem(label, options.get(index[0]), index[0] + 1, options.size());
            inventory.setItem(slot, updated);
        };

        update.accept(index[0]);
        put(slot, inventory.getItem(slot), ctx -> {
            int next = index[0];
            ClickType click = ctx.clickType();
            if (click.isLeftClick()) next++;
            else if (click.isRightClick()) next--;
            else if (click == ClickType.MIDDLE) next = initialIndex;
            update.accept(next);
            if (onChange != null) onChange.accept(options.get(index[0]));
        });
    }

    public void addSlider(int slot, String label, double initial, double min, double max, double step, Consumer<Double> onChange) {
        double[] value = {clampValue(initial, min, max)};

        Consumer<Double> update = v -> {
            value[0] = clampValue(v, min, max);
            ItemStack updated = sliderItem(label, value[0], min, max, step);
            inventory.setItem(slot, updated);
        };

        update.accept(value[0]);
        put(slot, inventory.getItem(slot), ctx -> {
            double v = value[0];
            ClickType click = ctx.clickType();
            if (click.isLeftClick()) v += step;
            else if (click.isRightClick()) v -= step;
            else if (click == ClickType.MIDDLE) v = initial;
            update.accept(v);
            if (onChange != null) onChange.accept(value[0]);
        });
    }

    public void addProgress(int slot, String label, double progress, String note) {
        double clamped = clampValue(progress, 0.0, 1.0);
        ItemStack stack = progressItem(label, clamped, note);
        put(slot, stack, null);
    }

    public void addStack(int startSlot, int verticalSpacing, List<GuiItem> stackItems) {
        int slot = startSlot;
        for (GuiItem item : stackItems) {
            put(slot, item.item(), item::handleClick);
            slot += verticalSpacing;
            if (slot >= inventory.getSize()) break;
        }
    }

    public void addGrid(int startRow, int startCol, int rows, int cols, List<GuiItem> gridItems) {
        int index = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (index >= gridItems.size()) return;
                int slot = Slots.at(startRow + r, startCol + c);
                if (slot < inventory.getSize()) {
                    GuiItem item = gridItems.get(index);
                    put(slot, item.item(), item::handleClick);
                }
                index++;
            }
        }
    }

    private void put(int slot, ItemStack stack, Consumer<GuiContext> handler) {
        int clamped = Slots.clamp(slot, inventory.getSize());
        inventory.setItem(clamped, stack);
        items.put(clamped, new GuiItem(stack, handler));
    }

    private ItemStack checkboxItem(String label, boolean state) {
        Material mat = state ? theme.checkboxOnMaterial() : theme.checkboxOffMaterial();
        String box = state ? "☑" : "☐";
        String title = box + " " + label;
        return styled(mat, Component.text(title, theme.primary()));
    }

    private ItemStack numberItem(String label, double value, double step, double stepShift) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Left/Right ±" + step, theme.secondary()));
        lore.add(Component.text("Shift ±" + stepShift + " | Middle reset", theme.secondary()));
        lore.add(Component.empty());
        lore.add(Component.text("Value: " + value, theme.accent()));
        return styled(theme.inputMaterial(), Component.text(label, theme.primary()), lore);
    }

    private ItemStack dropdownItem(String label, String current, int position, int total) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Left: next | Right: prev", theme.secondary()));
        lore.add(Component.text("Middle: reset", theme.secondary()));
        lore.add(Component.empty());
        lore.add(Component.text("Selected: " + current + " (" + position + "/" + total + ")", theme.accent()));
        return styled(theme.buttonMaterial(), Component.text(label, theme.primary()), lore);
    }

    private ItemStack sliderItem(String label, double value, double min, double max, double step) {
        double percent = clampValue((value - min) / (max - min + Double.MIN_VALUE), 0.0, 1.0);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Left/Right ±" + step + " | Middle reset", theme.secondary()));
        lore.add(Component.text(progressBar(percent, 14), theme.accent()));
        lore.add(Component.text(String.format("%.2f / %.2f", value, max), theme.primary()));
        return styled(theme.inputMaterial(), Component.text(label, theme.primary()), lore);
    }

    private ItemStack progressItem(String label, double progress, String note) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(progressBar(progress, 18), theme.accent()));
        lore.add(Component.text(String.format("%d%%", (int) Math.round(progress * 100)), theme.primary()));
        if (note != null && !note.isEmpty()) {
            lore.add(Component.text(note, theme.secondary()));
        }
        return styled(theme.fieldMaterial(), Component.text(label, theme.primary()), lore);
    }

    private ItemStack styled(Material material, Component name) {
        ItemStack stack = new ItemStack(material);
        stack.editMeta(meta -> meta.displayName(name));
        return stack;
    }

    private ItemStack styled(Material material, Component name, List<Component> lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(name);
        meta.lore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private double clampValue(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private int clampIndex(int index, int size) {
        if (size <= 0) return 0;
        return Math.max(0, Math.min(size - 1, index));
    }

    private String progressBar(double progress, int width) {
        double clamped = clampValue(progress, 0.0, 1.0);
        int filled = (int) Math.round(clamped * width);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width; i++) {
            sb.append(i < filled ? '█' : '░');
        }
        return sb.toString();
    }
}
