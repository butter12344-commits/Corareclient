package com.butterclient.gui;

import com.butterclient.config.ButterConfig;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ButterClickGuiScreen extends Screen {
    private static final String[] CATEGORIES = {
            "HUD", "Visuals", "World", "Cosmetics", "Combat", "Environment", "Extra", "Configs"
    };
    private static final int PURPLE = 0xFFB58CFF;
    private int selectedCategory = 0;
    private boolean editingHud = false;
    private boolean draggingHud = false;
    private int dragOffsetX;
    private int dragOffsetY;

    public ButterClickGuiScreen() {
        super(Text.literal("Butter Client"));
    }

    @Override
    protected void init() {
        clearChildren();
        int left = width / 2 - 245;
        int top = Math.max(18, height / 2 - 155);

        for (int i = 0; i < CATEGORIES.length; i++) {
            final int category = i;
            addDrawableChild(ButtonWidget.builder(Text.literal(CATEGORIES[i]), b -> {
                selectedCategory = category;
                editingHud = false;
                init();
            }).dimensions(left + 14, top + 48 + i * 27, 112, 22).build());
        }

        int contentX = left + 150;
        int y = top + 52;
        switch (selectedCategory) {
            case 0 -> {
                addToggle(contentX, y, "Master HUD", () -> ButterConfig.data.hudEnabled, v -> ButterConfig.data.hudEnabled = v);
                addToggle(contentX, y += 27, "Watermark", () -> ButterConfig.data.showWatermark, v -> ButterConfig.data.showWatermark = v);
                addToggle(contentX, y += 27, "Welcome message", () -> ButterConfig.data.showInGameWelcome, v -> ButterConfig.data.showInGameWelcome = v);
                addToggle(contentX, y += 27, "FPS counter", () -> ButterConfig.data.showFps, v -> ButterConfig.data.showFps = v);
                addToggle(contentX, y += 27, "Coordinates", () -> ButterConfig.data.showCoordinates, v -> ButterConfig.data.showCoordinates = v);
                addToggle(contentX, y += 27, "Sprint status", () -> ButterConfig.data.showSprintStatus, v -> ButterConfig.data.showSprintStatus = v);
                addToggle(contentX, y += 27, "Ping", () -> ButterConfig.data.showPing, v -> ButterConfig.data.showPing = v);
                addToggle(contentX, y += 27, "CPS counter", () -> ButterConfig.data.showCps, v -> ButterConfig.data.showCps = v);
                addToggle(contentX, y += 27, "Direction", () -> ButterConfig.data.showDirection, v -> ButterConfig.data.showDirection = v);
                addToggle(contentX, y += 27, "Keystrokes", () -> ButterConfig.data.showKeystrokes, v -> ButterConfig.data.showKeystrokes = v);
                addToggle(contentX, y += 27, "Armor status", () -> ButterConfig.data.showArmor, v -> ButterConfig.data.showArmor = v);
                addButton(contentX + 205, top + 52, 100, 22, "Edit HUD", () -> {
                    editingHud = !editingHud;
                    init();
                });
            }
            case 1 -> {
                addToggle(contentX, y, "Fullbright", () -> ButterConfig.data.fullbright, v -> ButterConfig.data.fullbright = v);
                addToggle(contentX, y += 27, "Butter trail particles", () -> ButterConfig.data.butterParticles, v -> ButterConfig.data.butterParticles = v);
                addToggle(contentX, y += 27, "Purple panel style", () -> ButterConfig.data.roundedPanel, v -> ButterConfig.data.roundedPanel = v);
                addToggle(contentX, y += 27, "Compact HUD", () -> ButterConfig.data.compactHud, v -> ButterConfig.data.compactHud = v);
            }
            case 3 -> addInfo(contentX, y, "Cosmetics", "Butter trail particles are available in Visuals.", "Animated capes and 3D cosmetics need dedicated assets/renderers.");
            case 4 -> addInfo(contentX, y, "Combat", "No combat cheats are included.", "Only fair-play visual indicators will be added here.");
            case 2 -> addInfo(contentX, y, "World", "World sky presets and weather visuals are not implemented yet.", "This page is reserved for client-side environment effects.");
            case 5 -> addInfo(contentX, y, "Environment", "Rain, snow, aurora and ambience effects are planned.", "No placeholder toggles are shown as working features.");
            case 6 -> {
                addToggle(contentX, y, "Zoom (hold C)", () -> ButterConfig.data.zoom, v -> ButterConfig.data.zoom = v);
                addToggle(contentX, y += 27, "Auto Sprint", () -> ButterConfig.data.autoSprint, v -> ButterConfig.data.autoSprint = v);
            }
            case 7 -> {
                addButton(contentX, y, 150, 22, "Save configuration", ButterConfig::save);
                addInfo(contentX, y + 45, "Config file", "config/butterclient.json", "Settings save automatically when changed.");
            }
        }

        addButton(left + 14, top + 278, 112, 22, "Save & Close", () -> {
            ButterConfig.save();
            close();
        });
    }

    private void addToggle(int x, int y, String label, java.util.function.BooleanSupplier getter,
                           java.util.function.Consumer<Boolean> setter) {
        boolean enabled = getter.getAsBoolean();
        String state = enabled ? "§aON" : "§cOFF";
        addDrawableChild(ButtonWidget.builder(Text.literal(label + "  " + state), button -> {
            setter.accept(!getter.getAsBoolean());
            ButterConfig.save();
            init();
        }).dimensions(x, y, 190, 22).build());
    }

    private void addButton(int x, int y, int w, int h, String label, Runnable action) {
        addDrawableChild(ButtonWidget.builder(Text.literal(label), button -> action.run())
                .dimensions(x, y, w, h).build());
    }

    private void addInfo(int x, int y, String heading, String line1, String line2) {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xB5080710);
        int left = width / 2 - 245;
        int top = Math.max(18, height / 2 - 155);
        int panelHeight = Math.min(330, height - 36);
        context.fill(left, top, left + 490, top + panelHeight, 0xF0141320);
        context.fill(left, top, left + 490, top + 34, 0xFF211A32);
        context.fill(left, top + 34, left + 136, top + panelHeight, 0xFF191525);
        context.fill(left, top, left + 3, top + panelHeight, PURPLE);
        context.drawTextWithShadow(textRenderer, Text.literal("BUTTER CLIENT"), left + 14, top + 12, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal("1.21.11  •  VISUAL CLIENT"), left + 355, top + 12, PURPLE);
        context.drawTextWithShadow(textRenderer, Text.literal("MODULES"), left + 18, top + 41, 0xFFB9A7D8);
        context.drawTextWithShadow(textRenderer, Text.literal(CATEGORIES[selectedCategory].toUpperCase()), left + 150, top + 40, PURPLE);

        if (selectedCategory == 2 || selectedCategory == 3 || selectedCategory == 4 || selectedCategory == 5) {
            String title = switch (selectedCategory) {
                case 2 -> "World";
                case 3 -> "Cosmetics";
                case 4 -> "Combat";
                default -> "Environment";
            };
            context.drawTextWithShadow(textRenderer, Text.literal(title), left + 150, top + 75, 0xFFFFFFFF);
            context.drawTextWithShadow(textRenderer, Text.literal(switch (selectedCategory) {
                case 2 -> "Sky presets and weather rendering are not implemented yet.";
                case 3 -> "Trail particles are in Visuals. Other cosmetics are planned.";
                case 4 -> "Cheat modules are intentionally excluded.";
                default -> "Atmosphere and weather effects are planned.";
            }), left + 150, top + 95, 0xFFBDB7C9);
            context.drawTextWithShadow(textRenderer, Text.literal("No fake toggles — unfinished features are clearly marked."), left + 150, top + 113, 0xFF8F879F);
        }
        if (selectedCategory == 7) {
            context.drawTextWithShadow(textRenderer, Text.literal("Settings file: config/butterclient.json"), left + 150, top + 105, 0xFFBDB7C9);
        }
        context.drawTextWithShadow(textRenderer, Text.literal("RIGHT SHIFT  •  Purple edition"), left + 150, top + panelHeight - 18, 0xFF9A8EAD);

        if (editingHud && selectedCategory == 0) renderHudPreview(context);
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderHudPreview(DrawContext context) {
        int x = ButterConfig.data.hudX;
        int y = ButterConfig.data.hudY;
        context.fill(x - 5, y - 5, x + 160, y + 45, 0xE51A1525);
        context.fill(x - 5, y - 5, x - 2, y + 45, PURPLE);
        context.drawTextWithShadow(textRenderer, Text.literal("HUD POSITION — DRAG"), x + 5, y + 4, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal("Butter Client  |  FPS  |  XYZ"), x + 5, y + 19, 0xFFD9C9FF);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (editingHud && selectedCategory == 0 && click.button() == 0) {
            int x = ButterConfig.data.hudX;
            int y = ButterConfig.data.hudY;
            if (mouseX >= x - 5 && mouseX <= x + 160 && mouseY >= y - 5 && mouseY <= y + 45) {
                draggingHud = true;
                dragOffsetX = (int) mouseX - x;
                dragOffsetY = (int) mouseY - y;
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        double mouseX = click.x();
        double mouseY = click.y();
        if (draggingHud) {
            ButterConfig.data.hudX = Math.max(0, Math.min(width - 170, (int) mouseX - dragOffsetX));
            ButterConfig.data.hudY = Math.max(0, Math.min(height - 55, (int) mouseY - dragOffsetY));
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (draggingHud) {
            draggingHud = false;
            ButterConfig.save();
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public void close() {
        ButterConfig.save();
        super.close();
    }
}
