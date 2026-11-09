package com.corare.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class CorareClickGui extends Screen {
    private static final int WIDTH = 370;
    private static final int HEIGHT = 260;
    private static final int SIDEBAR = 112;
    private int left;
    private int top;
    private int selectedCategory = 0;

    private static final String[] CATEGORIES = {"Visual", "HUD", "Utility", "Client"};

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

        // Translucent, softly rounded client window.
        rounded(ctx, left + 2, top + 3, WIDTH - 4, HEIGHT - 4, 12, 0x99000000);
        rounded(ctx, left, top, WIDTH, HEIGHT, 12, 0xE9100C19);
        rounded(ctx, left + 1, top + 1, WIDTH - 2, HEIGHT - 2, 11, 0xE8171222);
        rounded(ctx, left + 10, top + 10, SIDEBAR - 8, HEIGHT - 20, 9, 0xA50B0812);

        ctx.drawTextWithShadow(textRenderer, Text.literal("CORARE"), left + 19, top + 17, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("CLIENT"), left + 68, top + 17, CorareClient.PURPLE);
        ctx.drawTextWithShadow(textRenderer, Text.literal("FABRIC  •  1.21.11"), left + 19, top + 31, CorareClient.MUTED);

        for (int i = 0; i < CATEGORIES.length; i++) {
            int x = left + 16;
            int y = top + 61 + i * 35;
            boolean hover = inside(mouseX, mouseY, x, y, SIDEBAR - 20, 28);
            if (i == selectedCategory) {
                rounded(ctx, x, y, SIDEBAR - 20, 28, 7, 0xAA7540B8);
                rounded(ctx, x, y, 3, 28, 2, CorareClient.PURPLE);
            } else if (hover) {
                rounded(ctx, x, y, SIDEBAR - 20, 28, 7, 0x663C2A50);
            }
            int color = i == selectedCategory ? 0xFFFFFFFF : CorareClient.MUTED;
            ctx.drawTextWithShadow(textRenderer, Text.literal(CATEGORIES[i]), x + 12, y + 10, color);
        }

        int contentX = left + SIDEBAR + 12;
        ctx.drawTextWithShadow(textRenderer, Text.literal(CATEGORIES[selectedCategory].toUpperCase()), contentX, top + 18, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Manage your client modules"), contentX, top + 32, CorareClient.MUTED);

        switch (selectedCategory) {
            case 0 -> {
                drawToggle(ctx, "Watermark", "Client branding on screen", CorareClient.watermarkEnabled, 0, mouseX, mouseY);
                drawToggle(ctx, "Purple Crosshair", "Custom center crosshair", CorareClient.crosshairEnabled, 1, mouseX, mouseY);
            }
            case 1 -> {
                drawToggle(ctx, "Client HUD", "Master HUD visibility", CorareClient.hudEnabled, 0, mouseX, mouseY);
                drawToggle(ctx, "FPS Counter", "Display current FPS", CorareClient.fpsEnabled, 1, mouseX, mouseY);
                drawToggle(ctx, "Coordinates", "Show XYZ position", CorareClient.coordsEnabled, 2, mouseX, mouseY);
                drawToggle(ctx, "Keystrokes", "Show movement keys", CorareClient.keystrokesEnabled, 3, mouseX, mouseY);
            }
            default -> {
                rounded(ctx, contentX, top + 67, WIDTH - SIDEBAR - 30, 76, 9, 0x661D1728);
                ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Nothing here yet"), contentX + (WIDTH - SIDEBAR - 30) / 2, top + 91, CorareClient.MUTED);
                ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("More modules coming soon"), contentX + (WIDTH - SIDEBAR - 30) / 2, top + 106, 0xFF806F91);
            }
        }

        rounded(ctx, contentX, top + HEIGHT - 31, WIDTH - SIDEBAR - 28, 1, 1, 0x664D3A61);
        ctx.drawTextWithShadow(textRenderer, Text.literal("RIGHT SHIFT  •  CLOSE"), contentX, top + HEIGHT - 20, 0xFF9A8BAA);
        ctx.drawTextWithShadow(textRenderer, Text.literal("v1.0.0"), left + WIDTH - 47, top + HEIGHT - 20, 0xFF806F91);
    }

    private void drawToggle(DrawContext ctx, String label, String description, boolean enabled, int index, int mx, int my) {
        int x = left + SIDEBAR + 12;
        int y = top + 57 + index * 39;
        int width = WIDTH - SIDEBAR - 30;
        boolean hover = inside(mx, my, x, y, width, 33);
        rounded(ctx, x, y, width, 33, 8, hover ? 0xAA332543 : 0x8850475A);
        ctx.drawTextWithShadow(textRenderer, Text.literal(label), x + 10, y + 6, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal(description), x + 10, y + 19, 0xFFAA9CB8);
        int switchColor = enabled ? 0xFF9B5CFF : 0xFF51465E;
        rounded(ctx, x + width - 43, y + 10, 32, 14, 7, switchColor);
        int knobX = enabled ? x + width - 23 : x + width - 41;
        rounded(ctx, knobX, y + 12, 10, 10, 5, 0xFFFFFFFF);
    }

    private static boolean inside(double mx, double my, int x, int y, int width, int height) {
        return mx >= x && mx <= x + width && my >= y && my <= y + height;
    }

    // Draw a rounded rectangle using horizontal scan lines; no external rendering library needed.
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
        double mouseX = click.x();
        double mouseY = click.y();
        if (click.button() == 0) {
            for (int i = 0; i < CATEGORIES.length; i++) {
                int x = left + 16;
                int y = top + 61 + i * 35;
                if (inside(mouseX, mouseY, x, y, SIDEBAR - 20, 28)) {
                    selectedCategory = i;
                    return true;
                }
            }

            int x = left + SIDEBAR + 12;
            int width = WIDTH - SIDEBAR - 30;
            if (selectedCategory == 0) {
                if (inside(mouseX, mouseY, x, top + 57, width, 33)) {
                    CorareClient.watermarkEnabled = !CorareClient.watermarkEnabled;
                    return true;
                }
                if (inside(mouseX, mouseY, x, top + 96, width, 33)) {
                    CorareClient.crosshairEnabled = !CorareClient.crosshairEnabled;
                    return true;
                }
            } else if (selectedCategory == 1) {
                if (inside(mouseX, mouseY, x, top + 57, width, 33)) {
                    CorareClient.hudEnabled = !CorareClient.hudEnabled;
                    return true;
                }
                if (inside(mouseX, mouseY, x, top + 96, width, 33)) {
                    CorareClient.fpsEnabled = !CorareClient.fpsEnabled;
                    return true;
                }
                if (inside(mouseX, mouseY, x, top + 135, width, 33)) {
                    CorareClient.coordsEnabled = !CorareClient.coordsEnabled;
                    return true;
                }
                if (inside(mouseX, mouseY, x, top + 174, width, 33)) {
                    CorareClient.keystrokesEnabled = !CorareClient.keystrokesEnabled;
                    return true;
                }
            }
        }
        return super.mouseClicked(click, doubleClick);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
