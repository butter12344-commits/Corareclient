package com.corare.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class CorareClient implements ClientModInitializer {
    public static final int PURPLE = 0xFFB36BFF;
    public static final int MUTED = 0xFFD6C6E8;

    public static boolean hudEnabled = true;
    public static boolean fpsEnabled = true;
    public static boolean coordsEnabled = true;
    public static boolean keystrokesEnabled = true;
    public static boolean watermarkEnabled = true;
    public static boolean crosshairEnabled = false;
    public static boolean hitEffectEnabled = true;
    public static boolean walkTrailEnabled = false;
    public static boolean skyEnabled = false;

    // HUD opacity is intentionally adjustable independently from the text opacity.
    public static float hudOpacity = 0.58f;
    public static int keySize = 22;
    public static int keyColor = 0xFF9B5CFF;

    private static KeyBinding openGuiKey;
    private static int trailTicks;

    @Override
    public void onInitializeClient() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.corareclient.open_gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.create(Identifier.of("corareclient", "main"))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new CorareClickGui());
            }
            if (client.player != null && client.world != null && walkTrailEnabled) {
                trailTicks++;
                double speed = client.player.getVelocity().horizontalLength();
                if (speed > 0.08 && trailTicks % 3 == 0 && !client.player.isOnGround()) {
                    client.world.addParticle(ParticleTypes.PORTAL, false, false,
                            client.player.getX(), client.player.getY() + 0.08, client.player.getZ(),
                            0.0, 0.015, 0.0);
                }
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == world && hitEffectEnabled) {
                for (int i = 0; i < 8; i++) {
                    double ox = (client.world.random.nextDouble() - 0.5) * 0.65;
                    double oy = client.world.random.nextDouble() * 0.75;
                    double oz = (client.world.random.nextDouble() - 0.5) * 0.65;
                    client.world.addParticle(ParticleTypes.CRIT, false, false,
                            entity.getX() + ox, entity.getY() + oy, entity.getZ() + oz,
                            ox * 0.25, 0.08, oz * 0.25);
                }
            }
            return ActionResult.PASS;
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> renderHud(drawContext));
    }

    private static int panelColor() {
        int alpha = Math.max(0, Math.min(255, Math.round(hudOpacity * 255)));
        return (alpha << 24) | 0x170D22;
    }

    private static void glassPanel(DrawContext ctx, int x, int y, int width, int height) {
        ctx.fill(x, y, x + width, y + height, panelColor());
        ctx.fill(x, y, x + 2, y + height, 0xAAAD70FF);
    }

    private void renderHud(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!hudEnabled || mc.options.hudHidden || mc.player == null) return;

        int x = 8;
        int y = 8;
        if (watermarkEnabled) {
            glassPanel(ctx, x - 4, y - 3, 132, 20);
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal("CORARECLIENT"), x + 4, y + 3, 0xFFFFFFFF);
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal("1.0"), x + 105, y + 3, PURPLE);
            y += 24;
        }
        if (fpsEnabled) {
            String fps = "FPS  " + mc.getCurrentFps();
            glassPanel(ctx, x - 4, y - 3, mc.textRenderer.getWidth(fps) + 14, 18);
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal(fps), x + 4, y + 2, 0xFFFFFFFF);
            y += 21;
        }
        if (coordsEnabled) {
            String coords = String.format("XYZ  %.1f / %.1f / %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
            glassPanel(ctx, x - 4, y - 3, mc.textRenderer.getWidth(coords) + 14, 18);
            ctx.drawTextWithShadow(mc.textRenderer, Text.literal(coords), x + 4, y + 2, MUTED);
        }
        if (keystrokesEnabled) {
            int size = keySize;
            int bx = 8, by = mc.getWindow().getScaledHeight() - (size * 3 + 12);
            String[] keys = {"W", "A", "S", "D"};
            int[] codes = {GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_D};
            for (int i = 0; i < 4; i++) {
                int px = bx + (i == 0 ? size : (i == 1 ? 0 : i == 2 ? size : size * 2));
                int py = by + (i == 0 ? 0 : size);
                boolean down = GLFW.glfwGetKey(mc.getWindow().getHandle(), codes[i]) == GLFW.GLFW_PRESS;
                int bg = down ? keyColor : panelColor();
                ctx.fill(px, py, px + size - 2, py + size - 2, bg);
                ctx.drawCenteredTextWithShadow(mc.textRenderer, keys[i], px + (size - 2) / 2, py + (size - 2 - 8) / 2, 0xFFFFFFFF);
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
