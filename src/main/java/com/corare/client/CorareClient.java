package com.corare.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class CorareClient implements ClientModInitializer {
    public static final int PURPLE = 0xFF9B5CFF;
    public static final int DARK = 0xD9100B1B;
    public static final int MUTED = 0xFFB8A7CC;

    public static boolean hudEnabled = true;
    public static boolean fpsEnabled = true;
    public static boolean coordsEnabled = true;
    public static boolean keystrokesEnabled = true;
    public static boolean watermarkEnabled = true;
    public static boolean crosshairEnabled = false;

    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.corareclient.open_gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.create(Identifier.of("corareclient", "main"))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new CorareClickGui());
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> renderHud(drawContext));
    }

    private void renderHud(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!hudEnabled || mc.options.hudHidden || mc.player == null) return;

        int x = 8;
        int y = 8;
        if (watermarkEnabled) {
            ctx.fill(x - 4, y - 3, x + 126, y + 16, DARK);
            ctx.fill(x - 4, y - 3, x - 1, y + 16, PURPLE);
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal("CORARECLIENT"), x + 3, y + 2, 0xFFFFFFFF);
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal("1.0"), x + 102, y + 2, PURPLE);
            y += 23;
        }
        if (fpsEnabled) {
            ctx.fill(x - 4, y - 3, x + 76, y + 13, DARK);
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal("FPS  " + mc.getCurrentFps()), x + 3, y, 0xFFFFFFFF);
            y += 19;
        }
        if (coordsEnabled) {
            String coords = String.format("XYZ  %.1f / %.1f / %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
            ctx.fill(x - 4, y - 3, x + mc.textRenderer.getWidth(coords) + 10, y + 13, DARK);
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal(coords), x + 3, y, MUTED);
        }
        if (keystrokesEnabled) {
            int bx = 8, by = mc.getWindow().getScaledHeight() - 57;
            String[] keys = {"W", "A", "S", "D"};
            int[] codes = {GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_D};
            for (int i = 0; i < 4; i++) {
                int px = bx + (i == 0 ? 22 : (i == 1 ? 0 : i == 2 ? 22 : 44));
                int py = by + (i == 0 ? 0 : 22);
                boolean down = GLFW.glfwGetKey(mc.getWindow().getHandle(), codes[i]) == GLFW.GLFW_PRESS;
                ctx.fill(px, py, px + 20, py + 19, down ? 0xFF7040B8 : DARK);
                ctx.drawCenteredTextWithShadow(mc.textRenderer, keys[i], px + 10, py + 5, 0xFFFFFFFF);
            }
        }
        if (crosshairEnabled) {
            int cx = mc.getWindow().getScaledWidth() / 2;
            int cy = mc.getWindow().getScaledHeight() / 2;
            ctx.fill(cx - 5, cy, cx + 6, cy + 1, PURPLE);
            ctx.fill(cx, cy - 5, cx + 1, cy + 6, PURPLE);
        }
    }
}
