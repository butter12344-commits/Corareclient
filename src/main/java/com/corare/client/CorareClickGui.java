package com.corare.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class CorareClickGui extends Screen {
    private static final int WIDTH = 310;
    private static final int HEIGHT = 238;
    private int left;
    private int top;

    public CorareClickGui() { super(Text.literal("CorareClient")); }

    @Override
    protected void init() {
        left = (this.width - WIDTH) / 2;
        top = (this.height - HEIGHT) / 2;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);
        ctx.fill(left, top, left + WIDTH, top + HEIGHT, 0xF20D0915);
        ctx.fill(left, top, left + WIDTH, top + 3, CorareClient.PURPLE);
        ctx.fill(left, top + 3, left + 86, top + HEIGHT, 0xE9161023);
        ctx.drawTextWithShadow(textRenderer, Text.literal("CORARE"), left + 13, top + 15, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("CLIENT"), left + 13, top + 27, CorareClient.PURPLE);
        ctx.drawTextWithShadow(textRenderer, Text.literal("FABRIC  /  1.21.11"), left + 13, top + 49, CorareClient.MUTED);
        ctx.drawTextWithShadow(textRenderer, Text.literal("VISUAL"), left + 13, top + 83, CorareClient.PURPLE);
        ctx.drawTextWithShadow(textRenderer, Text.literal("HUD"), left + 13, top + 100, CorareClient.MUTED);
        ctx.drawTextWithShadow(textRenderer, Text.literal("UTILITY"), left + 13, top + 134, CorareClient.PURPLE);
        ctx.drawTextWithShadow(textRenderer, Text.literal("CLIENT"), left + 13, top + 151, CorareClient.MUTED);
        ctx.drawTextWithShadow(textRenderer, Text.literal("MODULES"), left + 103, top + 15, CorareClient.MUTED);
        drawToggle(ctx, "Client HUD", CorareClient.hudEnabled, 0, mouseX, mouseY);
        drawToggle(ctx, "Watermark", CorareClient.watermarkEnabled, 1, mouseX, mouseY);
        drawToggle(ctx, "FPS Counter", CorareClient.fpsEnabled, 2, mouseX, mouseY);
        drawToggle(ctx, "Coordinates", CorareClient.coordsEnabled, 3, mouseX, mouseY);
        drawToggle(ctx, "Keystrokes", CorareClient.keystrokesEnabled, 4, mouseX, mouseY);
        drawToggle(ctx, "Purple Crosshair", CorareClient.crosshairEnabled, 5, mouseX, mouseY);
        ctx.fill(left + 99, top + 207, left + WIDTH - 12, top + 208, 0xFF3B2A4C);
        ctx.drawTextWithShadow(textRenderer, Text.literal("RIGHT SHIFT  •  CLOSE MENU"), left + 103, top + 218, CorareClient.MUTED);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawToggle(DrawContext ctx, String label, boolean enabled, int index, int mx, int my) {
        int x = left + 100;
        int y = top + 35 + index * 27;
        boolean hover = mx >= x && mx <= left + WIDTH - 12 && my >= y && my <= y + 21;
        ctx.fill(x, y, left + WIDTH - 12, y + 21, hover ? 0xFF2C203C : 0xCC191222);
        ctx.drawTextWithShadow(textRenderer, Text.literal(label), x + 8, y + 7, 0xFFFFFFFF);
        int color = enabled ? 0xFF9B5CFF : 0xFF51465E;
        ctx.fill(left + WIDTH - 43, y + 5, left + WIDTH - 20, y + 16, color);
        ctx.drawCenteredTextWithShadow(textRenderer, enabled ? "ON" : "OFF", left + WIDTH - 31, y + 7, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int x = left + 100;
            for (int i = 0; i < 6; i++) {
                int y = top + 35 + i * 27;
                if (mouseX >= x && mouseX <= left + WIDTH - 12 && mouseY >= y && mouseY <= y + 21) {
                    switch (i) {
                        case 0 -> CorareClient.hudEnabled = !CorareClient.hudEnabled;
                        case 1 -> CorareClient.watermarkEnabled = !CorareClient.watermarkEnabled;
                        case 2 -> CorareClient.fpsEnabled = !CorareClient.fpsEnabled;
                        case 3 -> CorareClient.coordsEnabled = !CorareClient.coordsEnabled;
                        case 4 -> CorareClient.keystrokesEnabled = !CorareClient.keystrokesEnabled;
                        case 5 -> CorareClient.crosshairEnabled = !CorareClient.crosshairEnabled;
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() { return false; }
}
