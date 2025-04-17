package com.gregtechceu.gtceu.data.datagen.tag;

import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.MarkerMaterials.Color;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.data.item.GTItems;
import com.gregtechceu.gtceu.data.item.GTMaterialItems;
import com.gregtechceu.gtceu.data.tag.CustomTags;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import com.tterrag.registrate.providers.RegistrateTagsProvider;

import java.util.Objects;

import static com.gregtechceu.gtceu.api.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.data.material.GTMaterials.*;

public class ItemTagLoader {

    public static void init(RegistrateTagsProvider<Item> provider) {
        addTag(provider, lens, Color.White)
                .add(GTMaterialItems.MATERIAL_ITEMS.get(lens, Glass).getKey())
                .add(GTMaterialItems.MATERIAL_ITEMS.get(lens, NetherStar).getKey());
        addTag(provider, lens, Color.LightBlue).add(GTMaterialItems.MATERIAL_ITEMS.get(lens, Diamond).getKey());
        addTag(provider, lens, Color.Red).add(GTMaterialItems.MATERIAL_ITEMS.get(lens, Ruby).getKey());
        addTag(provider, lens, Color.Green).add(GTMaterialItems.MATERIAL_ITEMS.get(lens, Emerald).getKey());
        addTag(provider, lens, Color.Blue).add(GTMaterialItems.MATERIAL_ITEMS.get(lens, Sapphire).getKey());
        addTag(provider, lens, Color.Purple).add(GTMaterialItems.MATERIAL_ITEMS.get(lens, Amethyst).getKey());

        provider.addTag(CustomTags.PISTONS)
                        .add(Items.PISTON.builtInRegistryHolder().key())
                        .add(Items.STICKY_PISTON.builtInRegistryHolder().key());
        provider.addTag(CustomTags.BRICKS_FIREBRICK).add(GTItems.FIRECLAY_BRICK.getKey());
        provider.addTag(Tags.Items.BRICKS).addTag(CustomTags.BRICKS_FIREBRICK);

        create(provider, dye, Color.Brown, GTMaterialItems.MATERIAL_ITEMS.get(dust, MetalMixture).get());

        // add treated wood stick to vanilla sticks tag
        // noinspection DataFlowIssue ChemicalHelper#getTag can't return null with treated wood rod
        provider.addTag(Tags.Items.RODS_WOODEN)
                .add(GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.rod, TreatedWood).getKey());

        // add treated and untreated wood plates to vanilla planks tag
        provider.addTag(ItemTags.PLANKS)
                .add(GTMaterialItems.MATERIAL_ITEMS.get(plate, TreatedWood).getKey())
                .add(GTMaterialItems.MATERIAL_ITEMS.get(plate, Wood).getKey());

        provider.addTag(CustomTags.CIRCUITS)
                .addTag(CustomTags.ULV_CIRCUITS)
                .addTag(CustomTags.LV_CIRCUITS)
                .addTag(CustomTags.MV_CIRCUITS)
                .addTag(CustomTags.HV_CIRCUITS)
                .addTag(CustomTags.EV_CIRCUITS)
                .addTag(CustomTags.IV_CIRCUITS)
                .addTag(CustomTags.LuV_CIRCUITS)
                .addTag(CustomTags.ZPM_CIRCUITS)
                .addTag(CustomTags.UV_CIRCUITS)
                .addTag(CustomTags.UHV_CIRCUITS)
                .addOptionalTag(CustomTags.UEV_CIRCUITS.location())
                .addOptionalTag(CustomTags.UIV_CIRCUITS.location())
                .addOptionalTag(CustomTags.UXV_CIRCUITS.location())
                .addOptionalTag(CustomTags.OpV_CIRCUITS.location())
                .addOptionalTag(CustomTags.MAX_CIRCUITS.location());

        provider.addTag(CustomTags.BATTERIES)
                .addTag(CustomTags.ULV_BATTERIES)
                .addTag(CustomTags.LV_BATTERIES)
                .addTag(CustomTags.MV_BATTERIES)
                .addTag(CustomTags.HV_BATTERIES)
                .addTag(CustomTags.EV_BATTERIES)
                .addTag(CustomTags.IV_BATTERIES)
                .addTag(CustomTags.LuV_BATTERIES)
                .addTag(CustomTags.ZPM_BATTERIES)
                .addTag(CustomTags.UV_BATTERIES)
                .addTag(CustomTags.UHV_BATTERIES);

        // Add highTierContent items as optional entries so it doesn't error
        provider.addTag(CustomTags.ELECTRIC_MOTORS)
                .addOptional(GTItems.ELECTRIC_MOTOR_UHV.getId())
                .addOptional(GTItems.ELECTRIC_MOTOR_UEV.getId())
                .addOptional(GTItems.ELECTRIC_MOTOR_UIV.getId())
                .addOptional(GTItems.ELECTRIC_MOTOR_UXV.getId())
                .addOptional(GTItems.ELECTRIC_MOTOR_OpV.getId());

        provider.addTag(CustomTags.ELECTRIC_PUMPS)
                .addOptional(GTItems.ELECTRIC_PUMP_UHV.getId())
                .addOptional(GTItems.ELECTRIC_PUMP_UEV.getId())
                .addOptional(GTItems.ELECTRIC_PUMP_UIV.getId())
                .addOptional(GTItems.ELECTRIC_PUMP_UXV.getId())
                .addOptional(GTItems.ELECTRIC_PUMP_OpV.getId());

        provider.addTag(CustomTags.FLUID_REGULATORS)
                .addOptional(GTItems.FLUID_REGULATOR_UHV.getId())
                .addOptional(GTItems.FLUID_REGULATOR_UEV.getId())
                .addOptional(GTItems.FLUID_REGULATOR_UIV.getId())
                .addOptional(GTItems.FLUID_REGULATOR_UXV.getId())
                .addOptional(GTItems.FLUID_REGULATOR_OpV.getId());

        provider.addTag(CustomTags.CONVEYOR_MODULES)
                .addOptional(GTItems.CONVEYOR_MODULE_UHV.getId())
                .addOptional(GTItems.CONVEYOR_MODULE_UEV.getId())
                .addOptional(GTItems.CONVEYOR_MODULE_UIV.getId())
                .addOptional(GTItems.CONVEYOR_MODULE_UXV.getId())
                .addOptional(GTItems.CONVEYOR_MODULE_OpV.getId());

        provider.addTag(CustomTags.ELECTRIC_PISTONS)
                .addOptional(GTItems.ELECTRIC_PISTON_UHV.getId())
                .addOptional(GTItems.ELECTRIC_PISTON_UEV.getId())
                .addOptional(GTItems.ELECTRIC_PISTON_UIV.getId())
                .addOptional(GTItems.ELECTRIC_PISTON_UXV.getId())
                .addOptional(GTItems.ELECTRIC_PISTON_OpV.getId());

        provider.addTag(CustomTags.ROBOT_ARMS)
                .addOptional(GTItems.ROBOT_ARM_UHV.getId())
                .addOptional(GTItems.ROBOT_ARM_UEV.getId())
                .addOptional(GTItems.ROBOT_ARM_UIV.getId())
                .addOptional(GTItems.ROBOT_ARM_UXV.getId())
                .addOptional(GTItems.ROBOT_ARM_OpV.getId());

        provider.addTag(CustomTags.FIELD_GENERATORS)
                .addOptional(GTItems.FIELD_GENERATOR_UHV.getId())
                .addOptional(GTItems.FIELD_GENERATOR_UEV.getId())
                .addOptional(GTItems.FIELD_GENERATOR_UIV.getId())
                .addOptional(GTItems.FIELD_GENERATOR_UXV.getId())
                .addOptional(GTItems.FIELD_GENERATOR_OpV.getId());

        provider.addTag(CustomTags.EMITTERS)
                .addOptional(GTItems.EMITTER_UHV.getId())
                .addOptional(GTItems.EMITTER_UEV.getId())
                .addOptional(GTItems.EMITTER_UIV.getId())
                .addOptional(GTItems.EMITTER_UXV.getId())
                .addOptional(GTItems.EMITTER_OpV.getId());

        provider.addTag(CustomTags.SENSORS)
                .addOptional(GTItems.SENSOR_UHV.getId())
                .addOptional(GTItems.SENSOR_UEV.getId())
                .addOptional(GTItems.SENSOR_UIV.getId())
                .addOptional(GTItems.SENSOR_UXV.getId())
                .addOptional(GTItems.SENSOR_OpV.getId());
    }

    private static TagsProvider.TagAppender<Item> addTag(RegistrateTagsProvider<Item> provider,
                                                         TagPrefix prefix, Material material) {
        return provider.addTag(Objects.requireNonNull(ChemicalHelper.getTag(prefix, material),
                "%s/%s doesn't have any tags!".formatted(prefix, material)));
    }

    private static void create(RegistrateTagsProvider<Item> provider, TagPrefix prefix, Material material,
                               Item... rls) {
        create(provider, ChemicalHelper.getTag(prefix, material), rls);
    }

    public static void create(RegistrateTagsProvider<Item> provider, TagKey<Item> tagKey, ResourceLocation... rls) {
        var builder = provider.addTag(tagKey);
        for (ResourceLocation rl : rls) {
            builder.addOptional(rl);
        }
    }

    @SafeVarargs
    public static void create(RegistrateTagsProvider<Item> provider, TagKey<Item> tagKey, TagKey<Item>... rls) {
        var builder = provider.addTag(tagKey);
        for (TagKey<Item> tag : rls) {
            builder.addTag(tag);
        }
    }

    public static void create(RegistrateTagsProvider<Item> provider, TagKey<Item> tagKey, Item... rls) {
        var builder = provider.addTag(tagKey);
        for (Item item : rls) {
            builder.add(BuiltInRegistries.ITEM.getResourceKey(item).get());
        }
    }

    private static ResourceLocation rl(String name) {
        return ResourceLocation.parse(name);
    }
}
