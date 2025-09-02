package com.gregtechceu.gtceu.data.tag;


import com.gregtechceu.gtceu.api.tag.TagUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;

public class CustomTags {
    public static final TagKey<Block> ENDSTONE_ORE_REPLACEABLES = TagUtil.createBlockTag("end_stone_ore_replaceables");
}
