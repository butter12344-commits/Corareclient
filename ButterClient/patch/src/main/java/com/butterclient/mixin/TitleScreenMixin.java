package com.butterclient.mixin;

import com.butterclient.config.ButterConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    private static final int VIOLET = 0xFFD6A8FF;
    private static final int GLASS = 0x55301848;
    protected TitleScreenMixin(Text title) { super(title); }

    @Inject(method = "render", at = @At("TAIL"))
    private void butter$purpleMenu(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ButterConfig.data.customMainMenu) return;
        int cx = width / 2;
        int top = Math.max(8, height / 4 + 34);
        // Subtle glass rails frame the vanilla controls without replacing their actions.
        context.fill(cx - 104, top, cx - 102, top + 112, VIOLET);
        context.fill(cx + 102, top, cx + 104, top + 112, 0x889B6BD0);
        context.fill(cx - 102, top, cx + 102, top + 1, 0x99E3C4FF);
        context.fill(cx - 102, top + 111, cx + 102, top + 112, 0x668C69C5);
        context.fill(cx - 101, top + 2, cx + 101, top + 110, GLASS);
        String brand = "BUTTER CLIENT";
        String subtitle = "PURPLE EDITION  •  VISUAL CLIENT";
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(brand), cx, 13, VIOLET);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(subtitle), cx, 25, 0xFFE7D8FF);
        if (ButterConfig.data.showWelcomeText) {
            MinecraftClient client = MinecraftClient.getInstance();
            String name = client.getSession() == null ? "Player" : client.getSession().getUsername();
            context.drawCenteredTextWithShadow(textRenderer, Text.literal("Welcome, " + name), cx, top + 118, 0xFFE6D5FF);
        }
    }
}
