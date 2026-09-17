package com.sirbilby.fastplacesync.client;

import com.sirbilby.fastplacesync.client.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;

public class FastplaceSyncClient implements ClientModInitializer {
    public static final SoundLogger soundLogger = new SoundLogger();

    // INLINE INITIALIZATION: Guarantees 'config' is never null,
    // even if Mod Menu accesses the config screen before/during init.
    public static ModConfig config = ModConfig.load();

    @Override
    public void onInitializeClient() {
        // Client initialization
    }
}