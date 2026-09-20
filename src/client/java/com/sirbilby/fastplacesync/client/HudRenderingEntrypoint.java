package com.sirbilby.fastplacesync.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class HudRenderingEntrypoint implements ClientModInitializer {

    private static final int FLASH_SIZE = 100;
    private static final long FLASH_DURATION_MS = 100;
    private static final int FLASH_COLOR = 0xFFFF0000;

    private static volatile long flashStartMillis = -1;

    // Boolean flag for recording state
    private static boolean recording = false;

    private KeyMapping keyBinding;
    private final SoundLogger soundLogger = new SoundLogger();


    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("fastplacesync", "key_category")
        );

        keyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.fastplacesync.action",
                        InputConstants.Type.KEYSYM,
                        InputConstants.KEY_F12,
                        category
                )
        );


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }
            while (keyBinding.consumeClick()) {
                // Check if this should start or stop recording
                if (!recording) {
                    onCalibrationKeyPressed();
                    recording = true;
                    SoundLogger.recording = true;
                    client.player.sendSystemMessage(Component.literal("Recording started."));
                } else {
                    client.player.sendSystemMessage(Component.literal("Recording finished."));
                    recording = false;
                    SoundLogger.recording = false;
                }
            }
        });

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("fastplacesync", "before_chat"),
                HudRenderingEntrypoint::render
        );
    }

    private void onCalibrationKeyPressed() {
        flashStartMillis = System.currentTimeMillis();
        soundLogger.setCalibrationPoint();
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        if (flashStartMillis < 0) {
            return;
        }
        long elapsed = System.currentTimeMillis() - flashStartMillis;
        if (elapsed > FLASH_DURATION_MS) {
            return;
        }
        graphics.fill(0, 0, FLASH_SIZE, FLASH_SIZE, FLASH_COLOR);
    }
}