package com.sirbilby.fastplacesync.client.config;

import com.sirbilby.fastplacesync.client.FastplaceSyncClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (Screen parent) -> {
            if (FastplaceSyncClient.config == null) {
                FastplaceSyncClient.config = ModConfig.load();
            }

            File clicksFile = Paths.get("clicks.txt").toAbsolutePath().toFile();

            // --- PAGE 1: PRIMARY CATEGORIES ---
            ConfigCategory primaryCategory = ConfigCategory.createBuilder()
                    .name(Component.literal("Primary Sound Filters"))

                    // BUTTON: Open clicks.txt or its folder in OS File Explorer (highlighted)
                    .option(ButtonOption.createBuilder()
                            .name(Component.literal("Show Output File"))
                            .description(OptionDescription.of(Component.literal("Opens and highlights clicks.txt in your system file explorer.")))
                            .text(Component.literal("Show File"))
                            .action((yaclScreen, buttonOption) -> openFolderNative(clicksFile))
                            .build())

                    .option(createBooleanOption("Log Block Placement", "Log when blocks are placed.",
                            () -> FastplaceSyncClient.config.logBlockPlace, val -> FastplaceSyncClient.config.logBlockPlace = val))
                    .option(createBooleanOption("Log Block Breaking", "Log when blocks are broken.",
                            () -> FastplaceSyncClient.config.logBlockBreak, val -> FastplaceSyncClient.config.logBlockBreak = val))
                    .option(createBooleanOption("Log Firework Use", "Log when fireworks are launched.",
                            () -> FastplaceSyncClient.config.logFireworks, val -> FastplaceSyncClient.config.logFireworks = val))
                    .option(createBooleanOption("Log Bucket Use", "Log bucket emptying and filling (water, lava, snow).",
                            () -> FastplaceSyncClient.config.logBuckets, val -> FastplaceSyncClient.config.logBuckets = val))
                    .option(createBooleanOption("Log Player Damage", "Log when taking damage or dying.",
                            () -> FastplaceSyncClient.config.logPlayerDamage, val -> FastplaceSyncClient.config.logPlayerDamage = val))
                    .option(createBooleanOption("Log Player Attacks", "Log melee attack sounds on entities.",
                            () -> FastplaceSyncClient.config.logPlayerAttacks, val -> FastplaceSyncClient.config.logPlayerAttacks = val))
                    .option(createBooleanOption("Log Chests & Barrels", "Log chest/barrel opening and closing.",
                            () -> FastplaceSyncClient.config.logChestsAndBarrels, val -> FastplaceSyncClient.config.logChestsAndBarrels = val))
                    .option(createBooleanOption("Log Ringing Bells", "Log bell ringing sounds (block.bell.use).",
                            () -> FastplaceSyncClient.config.logBells, val -> FastplaceSyncClient.config.logBells = val))
                    .option(createBooleanOption("Log Doors & Trapdoors", "Log door and trapdoor interactions.",
                            () -> FastplaceSyncClient.config.logDoorsAndTrapdoors, val -> FastplaceSyncClient.config.logDoorsAndTrapdoors = val))
                    .option(createBooleanOption("Log Pick Up Item", "Log picking up dropped items.",
                            () -> FastplaceSyncClient.config.logItemPickups, val -> FastplaceSyncClient.config.logItemPickups = val))
                    .option(createBooleanOption("Log Stonecutter", "Log selecting recipes and taking output from stonecutters.",
                            () -> FastplaceSyncClient.config.logStonecutter, val -> FastplaceSyncClient.config.logStonecutter = val))
                    .option(createBooleanOption("Log UI Clicks", "Log interface button clicks.",
                            () -> FastplaceSyncClient.config.logUiClicks, val -> FastplaceSyncClient.config.logUiClicks = val))
                    .option(createBooleanOption("Log Levers & Buttons", "Log pressing levers and stone/wooden buttons.",
                            () -> FastplaceSyncClient.config.logLeversAndButtons, val -> FastplaceSyncClient.config.logLeversAndButtons = val))
                    .option(createBooleanOption("Log Armor & Elytra Equip", "Log equipping armor pieces or elytra.",
                            () -> FastplaceSyncClient.config.logArmorAndElytraEquip, val -> FastplaceSyncClient.config.logArmorAndElytraEquip = val))
                    .option(createBooleanOption("Log Workstations & Tables", "Log anvil, smithing, grindstone, loom, and other table uses.",
                            () -> FastplaceSyncClient.config.logWorkstations, val -> FastplaceSyncClient.config.logWorkstations = val))
                    .option(createBooleanOption("Log Shears", "Log shearing sheep, mooshrooms, hives, or snip actions.",
                            () -> FastplaceSyncClient.config.logShears, val -> FastplaceSyncClient.config.logShears = val))
                    .build();

            // --- PAGE 2: DYNAMIC ALL SOUNDS REGISTRY ---
            ConfigCategory.Builder rawSoundsCategory = ConfigCategory.createBuilder()
                    .name(Component.literal("All Game Sounds"));

            rawSoundsCategory.option(LabelOption.create(
                    Component.literal("Select individual additional sounds to log. Use the Search bar to find sounds.")
            ));

            List<String> soundIds = new ArrayList<>();
            for (Identifier id : BuiltInRegistries.SOUND_EVENT.keySet()) {
                if (id != null) {
                    soundIds.add(id.toString());
                }
            }
            soundIds.sort(String::compareTo);

            for (String soundIdStr : soundIds) {
                rawSoundsCategory.option(Option.<Boolean>createBuilder()
                        .name(Component.literal(soundIdStr))
                        .description(OptionDescription.of(Component.literal("Explicitly log: " + soundIdStr)))
                        .binding(false,
                                () -> FastplaceSyncClient.config.enabledCustomSoundIds.contains(soundIdStr),
                                val -> {
                                    if (val) FastplaceSyncClient.config.enabledCustomSoundIds.add(soundIdStr);
                                    else FastplaceSyncClient.config.enabledCustomSoundIds.remove(soundIdStr);
                                })
                        .controller(opt -> BooleanControllerBuilder.create(opt))
                        .build());
            }

            return YetAnotherConfigLib.createBuilder()
                    .title(Component.literal("FastplaceSync Configuration"))
                    .category(primaryCategory)
                    .category(rawSoundsCategory.build())
                    .save(FastplaceSyncClient.config::save)
                    .build()
                    .generateScreen(parent);
        };
    }

    private Option<Boolean> createBooleanOption(String name, String desc, java.util.function.Supplier<Boolean> getter, java.util.function.Consumer<Boolean> setter) {
        return Option.<Boolean>createBuilder()
                .name(Component.literal(name))
                .description(OptionDescription.of(Component.literal(desc)))
                .binding(true, getter, setter)
                .controller(opt -> BooleanControllerBuilder.create(opt))
                .build();
    }

    private static void openFolderNative(File targetFile) {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (targetFile.exists()) {
                if (os.contains("win")) {
                    new ProcessBuilder("explorer.exe", "/select,", targetFile.getAbsolutePath()).start();
                } else if (os.contains("mac")) {
                    new ProcessBuilder("open", "-R", targetFile.getAbsolutePath()).start();
                } else {
                    new ProcessBuilder("xdg-open", targetFile.getParentFile().getAbsolutePath()).start();
                }
            } else {
                File parentDir = targetFile.getParentFile() != null ? targetFile.getParentFile() : new File(".");
                if (os.contains("win")) {
                    new ProcessBuilder("explorer.exe", parentDir.getAbsolutePath()).start();
                } else if (os.contains("mac")) {
                    new ProcessBuilder("open", parentDir.getAbsolutePath()).start();
                } else {
                    new ProcessBuilder("xdg-open", parentDir.getAbsolutePath()).start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}