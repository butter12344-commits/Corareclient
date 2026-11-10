package com.butterclient.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;

public final class HitVisuals {
    private static int hitUntilTick = -1;
    private static long burstSeed;
    private static boolean criticalBurst;

    private HitVisuals() {}

    public static void onHit(MinecraftClient client, Entity target, boolean critical) {
        if (client == null || client.player == null || target == null) return;
        hitUntilTick = client.player.age + 12;
        burstSeed = System.nanoTime();
        criticalBurst = critical;
    }

    public static float getFlashProgress(MinecraftClient client) {
        if (client == null || client.player == null) return 0.0f;
        return Math.max(0.0f, Math.min(1.0f, (hitUntilTick - client.player.age) / 12.0f));
    }

    /** Screen-space cosmetic burst: it never changes damage, aim, reach or target selection. */
    public static void render(MinecraftClient client, DrawContext draw) {
        float progress = getFlashProgress(client);
        if (progress <= 0.0f) return;
        float elapsed = 1.0f - progress;
        int cx = draw.getScaledWindowWidth() / 2;
        int cy = draw.getScaledWindowHeight() / 2;
        int count = criticalBurst ? 22 : 16;
        double phase = (burstSeed & 1023L) * 0.0061;
        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2.0 * i / count) + phase;
            double radius = 10.0 + elapsed * (criticalBurst ? 43.0 : 34.0) + (i % 3) * 2.0;
            int px = cx + (int) Math.round(Math.cos(angle) * radius);
            int py = cy + (int) Math.round(Math.sin(angle) * radius);
            int size = Math.max(1, 3 - (int)(elapsed * 2.0));
            float hue = (float)((i / (double)count + phase * 0.08 + elapsed * 0.22) % 1.0);
            int rgb = java.awt.Color.HSBtoRGB(hue, 0.48f, 1.0f);
            int alpha = Math.max(0, Math.min(255, (int)(220 * progress)));
            int color = (alpha << 24) | (rgb & 0x00FFFFFF);
            draw.fill(px - size, py - size, px + size + 1, py + size + 1, color);
            if (i % 2 == 0) draw.fill(px - 1, py - 4, px + 2, py + 5, (alpha / 2 << 24) | (rgb & 0x00FFFFFF));
        }
        if (criticalBurst) {
            int alpha = (int)(170 * progress);
            int c = (alpha << 24) | 0x00F5C8FF;
            draw.fill(cx - 13, cy - 1, cx - 8, cy + 1, c);
            draw.fill(cx + 8, cy - 1, cx + 13, cy + 1, c);
        }
    }
}
