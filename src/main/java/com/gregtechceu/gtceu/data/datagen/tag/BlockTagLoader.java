package com.gregtechceu.gtceu.data.datagen.tag;

import com.gregtechceu.gtceu.data.machine.GTMachines;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.gregtechceu.gtceu.data.tag.CustomTags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import com.tterrag.registrate.providers.RegistrateTagsProvider;

public class BlockTagLoader {

    public static void init(RegistrateTagsProvider.IntrinsicImpl<Block> provider) {
        provider.addTag(CustomTags.ENDSTONE_ORE_REPLACEABLES)
                .addTag(Tags.Blocks.END_STONES);

        provider.addTag(CustomTags.NEEDS_NEUTRONIUM_TOOL);
        provider.addTag(CustomTags.NEEDS_DURANIUM_TOOL);

        provider.addTag(CustomTags.INCORRECT_FOR_DURANIUM_TOOL)
                .addTag(CustomTags.NEEDS_NEUTRONIUM_TOOL);
        provider.addTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)
                .addTag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
                .addTag(CustomTags.NEEDS_DURANIUM_TOOL)
                .addTag(CustomTags.NEEDS_NEUTRONIUM_TOOL);
        provider.addTag(BlockTags.INCORRECT_FOR_NETHERITE_TOOL)
                .addTag(CustomTags.NEEDS_DURANIUM_TOOL)
                .addTag(CustomTags.NEEDS_NEUTRONIUM_TOOL);

        // this is awful. I don't care, though.
        // spotless:off
        provider.addTag(BlockTags.REPLACEABLE)
                .add(GTMaterials.Oil.getFluid().defaultFluidState().createLegacyBlock().getBlock())
                .add(GTMaterials.LightOil.getFluid().defaultFluidState().createLegacyBlock().getBlock())
                .add(GTMaterials.HeavyOil.getFluid().defaultFluidState().createLegacyBlock().getBlock())
                .add(GTMaterials.RawOil.getFluid().defaultFluidState().createLegacyBlock().getBlock())
                .add(GTMaterials.NaturalGas.getFluid().defaultFluidState().createLegacyBlock().getBlock());
        // spotless:on

        provider.addTag(BlockTags.MINEABLE_WITH_AXE)
                .add(GTMachines.WOODEN_DRUM.getBlock())
                .add(GTMachines.WOODEN_CRATE.getBlock());

        // always add the wrench/pickaxe tag as a valid tag to mineable/wrench etc.
        provider.addTag(CustomTags.MINEABLE_WITH_WRENCH)
                .addTag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH);
        provider.addTag(CustomTags.MINEABLE_WITH_WIRE_CUTTER)
                .addTag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WIRE_CUTTER);

        provider.addTag(CustomTags.CLEANROOM_FLOORS)
                .addOptionalTag(ResourceLocation.fromNamespaceAndPath("elevatorid", "elevators"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("enderio", "travel_anchor"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("rftoolsutility", "matter_transmitter"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("rftoolsutility", "matter_receiver"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("rftoolsutility", "dialing_device"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("travelanchors", "travel_anchor"));

        provider.addTag(CustomTags.CHARCOAL_PILE_IGNITER_WALLS)
                .addTag(BlockTags.DIRT) // any dirt blocks
                .remove(Blocks.MOSS_BLOCK, Blocks.MUD, Blocks.MUDDY_MANGROVE_ROOTS) // except moss and mud
                .add(Blocks.DIRT_PATH) // path blocks
                .addTag(Tags.Blocks.SANDS).addTag(BlockTags.SAND) // any sand blocks
                .addTag(BlockTags.TERRACOTTA); // any terracotta
    }
}
