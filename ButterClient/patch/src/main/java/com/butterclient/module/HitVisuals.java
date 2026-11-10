package com.butterclient.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;

public final class HitVisuals {
    private static int hitUntilTick = -1;

    private HitVisuals() {}

    public static void onHit(MinecraftClient client, Entity target, boolean critical) {
        if (client == null || client.world == null || client.player == null || target == null) return;
        hitUntilTick = client.player.age + 8;
        double x = target.getX(), y = target.getBodyY(0.55), z = target.getZ();
        for (int i = 0; i < 12; i++) {
            double ox = (client.world.random.nextDouble() - 0.5) * 0.9;
            double oy = client.world.random.nextDouble() * 1.2;
            double oz = (client.world.random.nextDouble() - 0.5) * 0.9;
            client.world.addParticleClient(ParticleTypes.WITCH, x + ox, y + oy, z + oz,
                    ox * 0.08, 0.04 + client.world.random.nextDouble() * 0.06, oz * 0.08);
        }
        client.world.addParticleClient(ParticleTypes.END_ROD, x, y, z, 0, 0.12, 0);
        if (critical) for (int i = 0; i < 8; i++) {
            double ox = (client.world.random.nextDouble() - 0.5) * 0.7;
            double oz = (client.world.random.nextDouble() - 0.5) * 0.7;
            client.world.addParticleClient(ParticleTypes.CRIT, x + ox, y + 0.4, z + oz,
                    ox * 0.12, 0.1, oz * 0.12);
        }
    }

    public static float getFlashProgress(MinecraftClient client) {
        if (client == null || client.player == null) return 0.0f;
        return Math.max(0.0f, Math.min(1.0f, (hitUntilTick - client.player.age) / 8.0f));
    }
}
