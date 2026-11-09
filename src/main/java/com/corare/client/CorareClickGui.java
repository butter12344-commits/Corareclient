package com.corare.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class CorareClickGui extends Screen {
    private static final int WIDTH = 390;
    private static final int HEIGHT = 320;
    private static final int SIDEBAR = 116;
    private int left;
    private int top;
    private int selectedCategory = 0;

    private static final String[] CATEGORIES = {"Visual", "HUD", "Cosmetics", "Client"};

    public CorareClickGui() {
        super(Text.literal("CorareClient"));
    }

    @Override
    protected void init() {
        left = (this.width - WIDTH) / 2;
        top = (this.height - HEIGHT) / 2;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        rounded(ctx, left + 2, top + 3, WIDTH - 4, HEIGHT - 4, 13, 0x99000000);
        rounded(ctx, left, top, WIDTH, HEIGHT, 13, 0xE9100C19);
        rounded(ctx, left + 1, top + 1, WIDTH - 2, HEIGHT - 2, 12, 0xE8171222);
        rounded(ctx, left + 10, top + 10, SIDEBAR - 8, HEIGHT - 20, 9, 0xA50B0812);

        ctx.drawTextWithShadow(textRenderer, Text.literal("CORARE"), left + 19, top + 17, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("CLIENT"), left + 68, top + 17, CorareClient.PURPLE);
        ctx.drawTextWithShadow(textRenderer, Text.literal("FABRIC  •  1.21.11"), left + 19, top + 31, CorareClient.MUTED);

        for (int i = 0; i < CATEGORIES.length; i++) {
            int x = left + 16;
            int y = top + 61 + i * 36;
            boolean hover = inside(mouseX, mouseY, x, y, SIDEBAR - 20, 29);
            if (i == selectedCategory) {
                rounded(ctx, x, y, SIDEBAR - 20, 29, 7, 0xAA7540B8);
                rounded(ctx, x, y, 3, 29, 2, CorareClient.PURPLE);
            } else if (hover) {
                rounded(ctx, x, y, SIDEBAR - 20, 29, 7, 0x663C2A50);
            }
            ctx.drawTextWithShadow(textRenderer, Text.literal(CATEGORIES[i]), x + 12, y + 10,
                    i == selectedCategory ? 0xFFFFFFFF : CorareClient.MUTED);
        }

        int contentX = left + SIDEBAR + 12;
        int contentWidth = WIDTH - SIDEBAR - 30;
        ctx.drawTextWithShadow(textRenderer, Text.literal(CATEGORIES[selectedCategory].toUpperCase()), contentX, top + 18, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Purple glass interface  /  v1.0.0"), contentX, top + 32, CorareClient.MUTED);

        switch (selectedCategory) {
            case 0 -> {
                drawToggle(ctx, "Watermark", "CorareClient branding", CorareClient.watermarkEnabled, 0, mouseX, mouseY);
                drawToggle(ctx, "Custom Crosshair", "Purple center crosshair", CorareClient.crosshairEnabled, 1, mouseX, mouseY);
            }
            case 1 -> {
                drawToggle(ctx, "HUD Master", "Show or hide all HUD widgets", CorareClient.hudEnabled, 0, mouseX, mouseY);
                drawToggle(ctx, "FPS Counter", "Display current FPS", CorareClient.fpsEnabled, 1, mouseX, mouseY);
                drawToggle(ctx, "Coordinates", "Show XYZ position", CorareClient.coordsEnabled, 2, mouseX, mouseY);
                drawToggle(ctx, "Keystrokes", "Show WASD display", CorareClient.keystrokesEnabled, 3, mouseX, mouseY);
                drawValue(ctx, "Glass opacity", Math.round(CorareClient.hudOpacity * 100) + "%", 4, mouseX, mouseY);
                drawValue(ctx, "Keystroke size", CorareClient.keySize + " px", 5, mouseX, mouseY);
            }
            case 2 -> {
                drawToggle(ctx, "Hit Effect", "Purple critical hit particles", CorareClient.hitEffectEnabled, 0, mouseX, mouseY);
                drawToggle(ctx, "Walk Trail", "Particles follow your movement", CorareClient.walkTrailEnabled, 1, mouseX, mouseY);
                rounded(ctx, contentX, top + 148, contentWidth, 52, 8, 0x553D2A50);
                ctx.drawTextWithShadow(textRenderer, Text.literal("More cosmetics are coming"), contentX + 10, top + 160, 0xFFFFFFFF);
                ctx.drawTextWithShadow(textRenderer, Text.literal("Original Corare effects, purple style"), contentX + 10, top + 176, CorareClient.MUTED);
            }
            default -> {
                rounded(ctx, contentX, top + 65, contentWidth, 78, 9, 0x661D1728);
                ctx.drawTextWithShadow(textRenderer, Text.literal("Client settings"), contentX + 10, top + 78, 0xFFFFFFFF);
                ctx.drawTextWithShadow(textRenderer, Text.literal("Right Shift  •  Open / close GUI"), contentX + 10, top + 96, CorareClient.MUTED);
                ctx.drawTextWithShadow(textRenderer, Text.literal("Sky themes and main menu styling"), contentX + 10, top + 114, 0xFFB88BEA);
                ctx.drawTextWithShadow(textRenderer, Text.literal("are planned for the next visual update."), contentX + 10, top + 128, 0xFFB88BEA);
            }
        }

        rounded(ctx, contentX, top + HEIGHT - 31, contentWidth, 1, 1, 0x664D3A61);
        ctx.drawTextWithShadow(textRenderer, Text.literal("RIGHT SHIFT  •  CLOSE"), contentX, top + HEIGHT - 20, 0xFF9A8BAA);
    }

    private void drawToggle(DrawContext ctx, String label, String description, boolean enabled, int index, int mx, int my) {
        int x = left + SIDEBAR + 12;
        int y = top + 55 + index * 39;
        int width = WIDTH - SIDEBAR - 30;
        rounded(ctx, x, y, width, 33, 8, inside(mx, my, x, y, width, 33) ? 0xAA332543 : 0x8850475A);
        ctx.drawTextWithShadow(textRenderer, Text.literal(label), x + 10, y + 6, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal(description), x + 10, y + 19, 0xFFAA9CB8);
        rounded(ctx, x + width - 43, y + 10, 32, 14, 7, enabled ? CorareClient.PURPLE : 0xFF51465E);
        rounded(ctx, enabled ? x + width - 23 : x + width - 41, y + 12, 10, 10, 5, 0xFFFFFFFF);
    }

    private void drawValue(DrawContext ctx, String label, String value, int index, int mx, int my) {
        int x = left + SIDEBAR + 12;
        int y = top + 55 + index * 39;
        int width = WIDTH - SIDEBAR - 30;
        rounded(ctx, x, y, width, 33, 8, inside(mx, my, x, y, width, 33) ? 0xAA332543 : 0x8850475A);
        ctx.drawTextWithShadow(textRenderer, Text.literal(label), x + 10, y + 11, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("−"), x + width - 65, y + 10, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal(value), x + width - 49, y + 11, CorareClient.PURPLE);
        ctx.drawTextWithShadow(textRenderer, Text.literal("+"), x + width - 13, y + 10, 0xFFFFFFFF);
    }

    private static boolean inside(double mx, double my, int x, int y, int width, int height) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    private static void rounded(DrawContext ctx, int x, int y, int width, int height, int radius, int color) {
        int r = Math.min(radius, Math.min(width / 2, height / 2));
        for (int row = 0; row < height; row++) {
            int inset = 0;
            if (row < r) {
                double dy = r - row - 0.5;
                inset = r - (int) Math.sqrt(Math.max(0, r * r - dy * dy));
            } else if (row >= height - r) {
                double dy = row - (height - r) + 0.5;
                inset = r - (int) Math.sqrt(Math.max(0, r * r - dy * dy));
            }
            ctx.fill(x + inset, y + row, x + width - inset, y + row + 1, color);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubleClick) {
        double mx = click.x();
        double my = click.y();
        if (click.button() == 0) {
            for (int i = 0; i < CATEGORIES.length; i++) {
                int x = left + 16;
                int y = top + 61 + i * 36;
                if (inside(mx, my, x, y, SIDEBAR - 20, 29)) {
                    selectedCategory = i;
                    return true;
                }
            }

            int x = left + SIDEBAR + 12;
            int width = WIDTH - SIDEBAR - 30;
            if (selectedCategory == 0) {
                if (inside(mx, my, x, top + 55, width, 33)) CorareClient.watermarkEnabled = !CorareClient.watermarkEnabled;
                else if (inside(mx, my, x, top + 94, width, 33)) CorareClient.crosshairEnabled = !CorareClient.crosshairEnabled;
                else return super.mouseClicked(click, doubleClick);
                return true;
            }
            if (selectedCategory == 1) {
                if (inside(mx, my, x, top + 55, width, 33)) CorareClient.hudEnabled = !CorareClient.hudEnabled;
                else if (inside(mx, my, x, top + 94, width, 33)) CorareClient.fpsEnabled = !CorareClient.fpsEnabled;
                else if (inside(mx, my, x, top + 133, width, 33)) CorareClient.coordsEnabled = !CorareClient.coordsEnabled;
                else if (inside(mx, my, x, top + 172, width, 33)) CorareClient.keystrokesEnabled = !CorareClient.keystrokesEnabled;
                else if (inside(mx, my, x, top + 211, width, 33)) {
                    if (mx < x + width - 48) CorareClient.hudOpacity = Math.max(0.10f, CorareClient.hudOpacity - 0.05f);
                    else CorareClient.hudOpacity = Math.min(0.95f, CorareClient.hudOpacity + 0.05f);
                } else if (inside(mx, my, x, top + 250, width, 33)) {
                    if (mx < x + width - 48) CorareClient.keySize = Math.max(16, CorareClient.keySize - 2);
                    else CorareClient.keySize = Math.min(40, CorareClient.keySize + 2);
                } else return super.mouseClicked(click, doubleClick);
                return true;
            }
            if (selectedCategory == 2) {
                if (inside(mx, my, x, top + 55, width, 33)) CorareClient.hitEffectEnabled = !CorareClient.hitEffectEnabled;
                else if (inside(mx, my, x, top + 94, width, 33)) CorareClient.walkTrailEnabled = !CorareClient.walkTrailEnabled;
                else return super.mouseClicked(click, doubleClick);
                return true;
            }
        }
        return super.mouseClicked(click, doubleClick);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
