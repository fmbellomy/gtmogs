package com.gregtechceu.gtceu.data.datagen.datamap;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import com.tterrag.registrate.providers.RegistrateDataMapProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import org.jetbrains.annotations.NotNull;

public class DataMapsHandler {

    public static void init(RegistrateDataMapProvider provider) {}

    private static ResourceKey<Item> getItemKey(@NotNull RegistryEntry<Block, ? extends Block> entry) {
        return entry.getSibling(Registries.ITEM).getKey();
    }
}
