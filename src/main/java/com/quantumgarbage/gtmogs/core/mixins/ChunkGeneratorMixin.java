package com.quantumgarbage.gtmogs.core.mixins;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;

import com.quantumgarbage.gtmogs.api.worldgen.ores.OrePlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {

    @Unique
    private final OrePlacer gtceu$orePlacer = new OrePlacer();

    @Inject(method = "applyBiomeDecoration", at = @At("TAIL"))
    private void gtceu$applyBiomeDecoration(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager,
                                            CallbackInfo ci) {
        gtceu$orePlacer.placeOres(level, (ChunkGenerator) ((Object) this), chunk);
    }
}
