package com.butterclient;

import com.butterclient.config.ButterConfig;
import com.butterclient.gui.ButterClickGuiScreen;
import com.butterclient.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ButterClient implements ClientModInitializer {
    public static final String MOD_ID = "butterclient";
    public static final String MOD_NAME = "Butter Client";
    public static KeyBinding openGuiKey;
    public static KeyBinding zoomKey;
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of(MOD_ID, "general"));

    @Override
    public void onInitializeClient() {
        ButterConfig.load();
        ModuleManager.init();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.butterclient.open_gui", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY));
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.butterclient.zoom", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new ButterClickGuiScreen());
                else if (client.currentScreen instanceof ButterClickGuiScreen) client.setScreen(null);
            }
            ModuleManager.onTick(client);
        });
    }
}
