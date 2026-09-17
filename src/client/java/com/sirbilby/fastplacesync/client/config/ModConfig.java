package com.sirbilby.fastplacesync.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class ModConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("FastplaceSync");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("fastplacesync.json");

    // Primary Filters
    public boolean logBlockPlace = true;
    public boolean logBlockBreak = true;
    public boolean logFireworks = true;
    public boolean logBuckets = true;
    public boolean logPlayerDamage = true;
    public boolean logPlayerAttacks = true;
    public boolean logChestsAndBarrels = true;
    public boolean logBells = true;
    public boolean logDoorsAndTrapdoors = true;
    public boolean logItemPickups = true;

    // New Filters
    public boolean logStonecutter = true;
    public boolean logUiClicks = true;
    public boolean logLeversAndButtons = true;
    public boolean logArmorAndElytraEquip = true;
    public boolean logWorkstations = true;
    public boolean logShears = true;

    // Set of individual raw sound IDs enabled from the dynamic registry page
    public Set<String> enabledCustomSoundIds = new HashSet<>();

    public static ModConfig load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                ModConfig config = GSON.fromJson(reader, ModConfig.class);
                if (config != null) return config;
            } catch (IOException e) {
                LOGGER.error("Failed to load FastplaceSync config, using defaults", e);
            }
        }
        ModConfig defaultConfig = new ModConfig();
        defaultConfig.save();
        return defaultConfig;
    }

    public void save() {
        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save FastplaceSync config", e);
        }
    }
}