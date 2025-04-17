package com.gregtechceu.gtceu.data.datagen.datamap;

import com.gregtechceu.gtceu.data.block.GTBlocks;
import com.gregtechceu.gtceu.data.tag.CustomTags;
import com.tterrag.registrate.providers.RegistrateDataMapProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.jetbrains.annotations.NotNull;

public class DataMapsHandler {

    public static void init(RegistrateDataMapProvider provider) {
        provider.builder(NeoForgeDataMaps.COMPOSTABLES)
                        .add(getItemKey(GTBlocks.RUBBER_LEAVES), new Compostable(0.3f), false)
                        .add(getItemKey(GTBlocks.RUBBER_SAPLING), new Compostable(0.3f), false);
    }

    private static ResourceKey<Item> getItemKey(@NotNull RegistryEntry<Block, ? extends Block> entry) {
        return entry.getSibling(Registries.ITEM).getKey();
    }
}
