package com.gregtechceu.gtceu.core.mixins;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.gregtechceu.gtceu.core.MixinHelpers;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.*;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerRegistries.class)
public class ReloadableServerRegistriesMixin {

    @Shadow
    private static @Final Gson GSON;

    @ModifyExpressionValue(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;toList()Ljava/util/List;"))
    private static List<CompletableFuture<WritableRegistry<?>>> gtceu$addReloadableRegistries(List<CompletableFuture<WritableRegistry<?>>> original,
                                                                                              LayeredRegistryAccess<RegistryLayer> registries,
                                                                                              ResourceManager resourceManager,
                                                                                              Executor backgroundExecutor,
                                                                                              @Local RegistryOps<JsonElement> registryops) {
        return MixinHelpers.injectGTReloadableRegistries(original, registries,
                resourceManager, backgroundExecutor, registryops);
    }
}
