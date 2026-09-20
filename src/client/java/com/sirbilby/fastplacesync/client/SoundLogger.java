package com.sirbilby.fastplacesync.client;

import com.sirbilby.fastplacesync.client.config.ModConfig;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

public class SoundLogger implements SoundEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger("FastplaceSync");
    private static final Path CLICKSFILE = Paths.get("clicks.txt");

    private record CalibrationState(boolean calibrated, long calibrationNanos) {}
    private static final CalibrationState UNCALIBRATED = new CalibrationState(false, 0);

    // Shared globally across all instances and volatile for thread visibility
    private static volatile CalibrationState calibration = UNCALIBRATED;

    // Boolean flag for recording state
    public static boolean recording = false;

    @Override
    public void onPlaySound(SoundInstance instance, WeighedSoundEvents soundEvent, float range) {
        int instanceHash = System.identityHashCode(this);
        CalibrationState state = calibration; // Atomic read of static reference

        //LOGGER.info("[SoundLogger:{}] onPlaySound fired for: {} | calibrated={}", Integer.toHexString(instanceHash), instance.getIdentifier(), state.calibrated());

        if (!state.calibrated()) {
            //LOGGER.warn("[SoundLogger:{}] SKIPPED sound because calibrated=false!", Integer.toHexString(instanceHash));
            return;
        }

        Identifier soundId = instance.getIdentifier();
        if (shouldLogSound(soundId)) {
            long elapsedNanos = System.nanoTime() - state.calibrationNanos();
            double relativeSeconds = elapsedNanos / 1_000_000_000.0;

            //LOGGER.info("[SoundLogger:{}] MATCHED sound: {} | Relative time: {}s", Integer.toHexString(instanceHash), soundId, relativeSeconds);

            writeTimestamp(relativeSeconds);
        }
    }

    private boolean shouldLogSound(Identifier soundId) {
        ModConfig config = FastplaceSyncClient.config;
        if (config == null) return false;

        String path = soundId.getPath();
        String rawId = soundId.toString();

        // 1. Dynamic raw sound overrides from Page 2
        if (config.enabledCustomSoundIds.contains(rawId)) {
            return true;
        }

        // 2. Primary filter categories
        if (config.logBlockPlace && path.startsWith("block.") && path.endsWith("place")) return true;
        if (config.logBlockBreak && path.startsWith("block.") && path.endsWith("break")) return true;
        if (config.logFireworks && path.contains("firework")) return true;

        if (config.logBuckets && (path.contains("bucket.empty") || path.contains("bucket.fill"))) return true;
        if (config.logPlayerDamage && (path.equals("entity.player.hurt") || path.equals("entity.player.death"))) return true;
        if (config.logPlayerAttacks && path.startsWith("entity.player.attack.")) return true;

        if (config.logChestsAndBarrels && (path.contains("chest.open") || path.contains("chest.close")
                || path.contains("barrel.open") || path.contains("barrel.close"))) return true;

        if (config.logBells && path.equals("block.bell.use")) return true;

        if (config.logDoorsAndTrapdoors && (path.contains("door.open") || path.contains("door.close")
                || path.contains("trapdoor.open") || path.contains("trapdoor.close"))) return true;

        if (config.logItemPickups && path.equals("entity.item.pickup")) return true;

        // Stonecutter (explicit check so it doesn't accidentally trigger UI click logic)
        if (config.logStonecutter && path.startsWith("ui.stonecutter.")) return true;

        // Standard UI Button Clicks & Menu Clicks
        if (config.logUiClicks && (path.equals("ui.button.click") || path.startsWith("ui.hud."))) return true;

        // Levers & Buttons (in-world blocks)
        if (config.logLeversAndButtons && (path.contains("lever.click") || (path.contains("button.click") && !path.startsWith("ui.")))) return true;

        // Armor & Elytra Equip
        if (config.logArmorAndElytraEquip && (path.startsWith("item.armor.equip") || path.equals("item.elytra.equip"))) return true;

        // Workstations & Tables
        if (config.logWorkstations && (path.startsWith("block.anvil.")
                || path.startsWith("block.smithing_table.")
                || path.startsWith("block.grindstone.")
                || path.startsWith("ui.cartography_table.")
                || path.startsWith("ui.loom.")
                || path.startsWith("block.enchantment_table.")
                || path.startsWith("block.brewing_stand.")
                || path.startsWith("block.beacon."))) return true;

        // Shears
        if (config.logShears && (path.endsWith(".shear") || path.equals("item.shears.snip"))) return true;

        return false;
    }

    private void writeTimestamp(double relativeSeconds) {
        // Don't write sh*t unless recording is true
        if (recording) {
            String line = String.format(Locale.ROOT, "%.3f%n", relativeSeconds);
            try (Writer writer = Files.newBufferedWriter(
                    CLICKSFILE, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(line);
                //LOGGER.info("Wrote timestamp to {}: {}", CLICKSFILE.toAbsolutePath(), line.trim());
            } catch (IOException e) {
                LOGGER.error("Failed to write to clicks.txt", e);
            }
        }
    }

    public void setCalibrationPoint() {
        int instanceHash = System.identityHashCode(this);
        calibration = new CalibrationState(true, System.nanoTime());

        //LOGGER.info("[SoundLogger:{}] setCalibrationPoint called! New calibration nanos: {}", Integer.toHexString(instanceHash), calibration.calibrationNanos());

        // Only set calibration point if not recording.
        // setCalibrationPoint is called by onCalibrationKeyPressed, which is called before recording is set to true.
        if (!recording) {
            try {
                Files.newBufferedWriter(
                        CLICKSFILE, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).close();
                LOGGER.info("[SoundLogger:{}] clicks.txt reset/truncated successfully.",
                        Integer.toHexString(instanceHash));
            } catch (IOException e) {
                LOGGER.error("Failed to reset clicks.txt", e);
            }
        }
    }
}