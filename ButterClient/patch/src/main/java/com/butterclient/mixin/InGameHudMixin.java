package com.butterclient.mixin;

import com.butterclient.config.ButterConfig;
import com.butterclient.module.CpsCounter;
import com.butterclient.module.HitVisuals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    private static final int PURPLE = 0xFFB58CFF;

    @Inject(method = "render", at = @At("TAIL"))
    private void butter$renderHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        ButterConfig.Data cfg = ButterConfig.data;
        float hitFlash = HitVisuals.getFlashProgress(MinecraftClient.getInstance());
        if (hitFlash > 0.0f) {
            int alpha = Math.min(42, (int) (hitFlash * 42));
            context.fill(0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight(),
                    (alpha << 24) | 0x00A855FF);
            int cx = context.getScaledWindowWidth() / 2;
            int cy = context.getScaledWindowHeight() / 2;
            context.fill(cx - 7, cy, cx + 8, cy + 1, 0xFFDA9CFF);
            context.fill(cx, cy - 7, cx + 1, cy + 8, 0xFFDA9CFF);
        }
        if (!cfg.hudEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || client.getDebugHud().shouldShowDebugHud()) return;

        List<String> lines = new ArrayList<>();
        String playerName = client.getSession() != null ? client.getSession().getUsername() : "Player";

        if (cfg.showWatermark) lines.add("§d§lBUTTER CLIENT §8/ §7v1.1");
        if (cfg.showInGameWelcome) lines.add("§7Welcome, §f" + playerName);
        if (cfg.showFps) lines.add("§dFPS §f" + client.getCurrentFps());
        if (cfg.showCoordinates) {
            lines.add(String.format("§dXYZ §f%d §7/ §f%d §7/ §f%d",
                    (int) client.player.getX(), (int) client.player.getY(), (int) client.player.getZ()));
        }
        if (cfg.showCps) lines.add("§dCPS §f" + CpsCounter.getCps());
        if (cfg.showPing && client.getNetworkHandler() != null) {
            var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            if (entry != null) lines.add("§dPING §f" + entry.getLatency() + " ms");
        }
        if (cfg.showDirection) {
            lines.add("§dFACING §f" + client.player.getHorizontalFacing().asString().toUpperCase());
        }
        if (cfg.showSprintStatus) {
            lines.add("§dSPRINT §f" + (client.player.isSprinting() ? "ON" : "OFF"));
        }
        if (cfg.showArmor) {
            List<String> armor = new ArrayList<>();
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!slot.isArmorSlot()) continue;
                ItemStack stack = client.player.getEquippedStack(slot);
                if (stack.isEmpty()) armor.add("--");
                else if (stack.isDamageable()) {
                    int remaining = Math.max(0, stack.getMaxDamage() - stack.getDamage());
                    int percent = (int) Math.round(remaining * 100.0 / stack.getMaxDamage());
                    armor.add(percent + "%");
                } else armor.add("OK");
            }
            lines.add("§dARMOR §f" + String.join("  ", armor));
        }
        if (cfg.showKeystrokes) {
            lines.add("§dKEYS §f" + key(client.options.forwardKey.isPressed(), "W") + " "
                    + key(client.options.leftKey.isPressed(), "A") + " "
                    + key(client.options.backKey.isPressed(), "S") + " "
                    + key(client.options.rightKey.isPressed(), "D") + "  "
                    + key(client.options.jumpKey.isPressed(), "J") + "  "
                    + key(client.options.sprintKey.isPressed(), "SP"));
        }

        if (lines.isEmpty()) return;
        int x = Math.max(4, cfg.hudX);
        int y = Math.max(4, cfg.hudY);
        int maxWidth = 0;
        for (String line : lines) maxWidth = Math.max(maxWidth, client.textRenderer.getWidth(line));

        int lineHeight = cfg.compactHud ? 9 : 11;
        int padding = cfg.compactHud ? 4 : 6;
        int boxHeight = lines.size() * lineHeight + padding * 2;
        int boxWidth = maxWidth + padding * 2 + 3;
        context.fill(x - padding, y - padding, x + boxWidth - padding, y + boxHeight - padding, 0xC9100D18);
        context.fill(x - padding, y - padding, x - padding + 2, y + boxHeight - padding, PURPLE);
        if (cfg.roundedPanel) {
            context.fill(x - padding + 2, y - padding, x + boxWidth - padding - 2, y - padding + 1, 0x66D8BFFF);
        }

        int currentY = y;
        for (String line : lines) {
            context.drawTextWithShadow(client.textRenderer, Text.literal(line), x, currentY, 0xFFFFFFFF);
            currentY += lineHeight;
        }
    }

    private static String key(boolean pressed, String label) {
        return pressed ? "§d[" + label + "]" : "§8[" + label + "]";
    }
}
