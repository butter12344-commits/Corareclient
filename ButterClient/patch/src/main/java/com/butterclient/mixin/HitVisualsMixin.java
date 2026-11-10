package com.butterclient.mixin;

import com.butterclient.module.HitVisuals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class HitVisualsMixin {
    @Inject(method = "attackEntity", at = @At("TAIL"))
    private void butter$hitVisuals(PlayerEntity player, Entity target, CallbackInfo ci) {
        boolean critical = player.fallDistance > 0.0f && !player.isOnGround()
                && !player.isClimbing() && !player.isTouchingWater();
        HitVisuals.onHit(MinecraftClient.getInstance(), target, critical);
    }
}
