package com.sirbilby.fastplacesync.client.mixin;

import com.sirbilby.fastplacesync.client.FastplaceSyncClient;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (FastplaceSyncClient.soundLogger != null) {
            ((SoundEngine) (Object) this).addEventListener(FastplaceSyncClient.soundLogger);
        }
    }
}
