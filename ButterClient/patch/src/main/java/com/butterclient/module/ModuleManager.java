package com.butterclient.module;

import com.butterclient.config.ButterConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;

public final class ModuleManager {
    private static double previousGamma = 1.0;
    private static boolean fullbrightWasActive = false;
    private static int previousFov = 70;
    private static boolean zoomWasActive = false;

    private ModuleManager() {}

    public static void init() {
        fullbrightWasActive = false;
        zoomWasActive = false;
    }

    public static void onTick(MinecraftClient client) {
        if (client == null || client.options == null) return;

        boolean inWorld = client.player != null && client.world != null;

        if (inWorld && ButterConfig.data.fullbright) {
            if (!fullbrightWasActive) {
                previousGamma = client.options.getGamma().getValue();
                fullbrightWasActive = true;
            }
            client.options.getGamma().setValue(16.0);
        } else if (fullbrightWasActive) {
            client.options.getGamma().setValue(previousGamma);
            fullbrightWasActive = false;
        }

        if (!inWorld) {
            restoreZoom(client);
            return;
        }

        boolean zoomActive = ButterConfig.data.zoom && ButterClientKeyState.isZoomHeld(client);
        if (zoomActive) {
            if (!zoomWasActive) {
                previousFov = client.options.getFov().getValue();
                zoomWasActive = true;
            }
            int zoomFov = (int) Math.max(10, Math.min(70, previousFov * ButterConfig.data.zoomLevel));
            client.options.getFov().setValue(zoomFov);
        } else {
            restoreZoom(client);
        }

        if (ButterConfig.data.butterParticles && client.player.isSprinting()
                && client.player.age % 4 == 0) {
            client.world.addParticleClient(ParticleTypes.END_ROD,
                    client.player.getX(), client.player.getY() + 0.08, client.player.getZ(),
                    0.0, 0.015, 0.0);
        }
    }

    private static void restoreZoom(MinecraftClient client) {
        if (zoomWasActive) {
            client.options.getFov().setValue(previousFov);
            zoomWasActive = false;
        }
    }

    public static final class ButterClientKeyState {
        private ButterClientKeyState() {}
        public static boolean isZoomHeld(MinecraftClient client) {
            return com.butterclient.ButterClient.zoomKey != null
                    && com.butterclient.ButterClient.zoomKey.isPressed();
        }
    }
}
