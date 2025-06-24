package com.gregtechceu.gtceu.data.datagen.tag;

import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.MarkerMaterials.Color;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.data.item.GTItems;
import com.gregtechceu.gtceu.data.item.GTMaterialItems;
import com.gregtechceu.gtceu.data.tag.CustomTags;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import com.tterrag.registrate.providers.RegistrateItemTagsProvider;

import java.util.Objects;

import static com.gregtechceu.gtceu.api.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.data.material.GTMaterials.*;

@SuppressWarnings({ "SameParameterValue", "DataFlowIssue" })
public class ItemTagLoader {

    public static void init(RegistrateItemTagsProvider provider) {
        // spotless:off
        // the coral blocks: alive, dead, both
        provider.addTag(CustomTags.ALIVE_CORAL_BLOCK_ITEMS)
                .add(Items.BRAIN_CORAL_BLOCK, Items.BUBBLE_CORAL_BLOCK, Items.FIRE_CORAL_BLOCK, Items.TUBE_CORAL_BLOCK, Items.HORN_CORAL_BLOCK);
        provider.addTag(CustomTags.DEAD_CORAL_BLOCK_ITEMS)
                .add(Items.DEAD_BRAIN_CORAL_BLOCK, Items.DEAD_BUBBLE_CORAL_BLOCK, Items.DEAD_FIRE_CORAL_BLOCK, Items.DEAD_TUBE_CORAL_BLOCK, Items.DEAD_HORN_CORAL_BLOCK);
        provider.addTag(CustomTags.CORAL_BLOCK_ITEMS)
                .addTag(CustomTags.ALIVE_CORAL_BLOCK_ITEMS)
                .addTag(CustomTags.DEAD_CORAL_BLOCK_ITEMS);

        // the coral plants (the V-shaped pointy ones)
        provider.addTag(CustomTags.ALIVE_CORAL_PLANT_ITEMS)
                .add(Items.BRAIN_CORAL, Items.BUBBLE_CORAL, Items.FIRE_CORAL, Items.TUBE_CORAL, Items.HORN_CORAL);
        provider.addTag(CustomTags.DEAD_CORAL_PLANT_ITEMS)
                .add(Items.DEAD_BRAIN_CORAL, Items.DEAD_BUBBLE_CORAL, Items.DEAD_FIRE_CORAL, Items.DEAD_TUBE_CORAL, Items.DEAD_HORN_CORAL);
        provider.addTag(CustomTags.CORAL_PLANT_ITEMS)
                .addTag(CustomTags.ALIVE_CORAL_PLANT_ITEMS)
                .addTag(CustomTags.DEAD_CORAL_PLANT_ITEMS);

        // the coral fans (the flat circular ones)
        provider.addTag(CustomTags.ALIVE_CORAL_FAN_ITEMS)
                .add(Items.BRAIN_CORAL_FAN, Items.BUBBLE_CORAL_FAN, Items.FIRE_CORAL_FAN, Items.TUBE_CORAL_FAN, Items.HORN_CORAL_FAN);
        provider.addTag(CustomTags.DEAD_CORAL_FAN_ITEMS)
                .add(Items.DEAD_BRAIN_CORAL_FAN, Items.DEAD_BUBBLE_CORAL_FAN, Items.DEAD_FIRE_CORAL_FAN, Items.DEAD_TUBE_CORAL_FAN, Items.DEAD_HORN_CORAL_FAN);
        provider.addTag(CustomTags.CORAL_FAN_ITEMS)
                .addTag(CustomTags.ALIVE_CORAL_FAN_ITEMS)
                .addTag(CustomTags.DEAD_CORAL_FAN_ITEMS);

        // all the coral items (except the blocks)
        provider.addTag(CustomTags.ALIVE_CORAL_ITEMS)
                .addTag(CustomTags.ALIVE_CORAL_PLANT_ITEMS)
                .addTag(CustomTags.ALIVE_CORAL_FAN_ITEMS);
        provider.addTag(CustomTags.DEAD_CORAL_ITEMS)
                .addTag(CustomTags.DEAD_CORAL_PLANT_ITEMS)
                .addTag(CustomTags.DEAD_CORAL_FAN_ITEMS);
        provider.addTag(CustomTags.CORAL_ITEMS)
                .addTag(CustomTags.ALIVE_CORAL_ITEMS)
                .addTag(CustomTags.DEAD_CORAL_ITEMS);
        // spotless:on

        addTag(provider, lens, Color.White)
                .add(GTMaterialItems.MATERIAL_ITEMS.get(lens, Glass).get())
                .add(GTMaterialItems.MATERIAL_ITEMS.get(lens, NetherStar).get());
        addTag(provider, lens, Color.LightBlue)
                .add(GTMaterialItems.MATERIAL_ITEMS.get(lens, Diamond).get());
        addTag(provider, lens, Color.Red)
                .add(GTMaterialItems.MATERIAL_ITEMS.get(lens, Ruby).get());
        addTag(provider, lens, Color.Green)
                .add(GTMaterialItems.MATERIAL_ITEMS.get(lens, Emerald).get());
        addTag(provider, lens, Color.Blue)
                .add(GTMaterialItems.MATERIAL_ITEMS.get(lens, Sapphire).get());
        addTag(provider, lens, Color.Purple)
                .add(GTMaterialItems.MATERIAL_ITEMS.get(lens, Amethyst).get());

        provider.addTag(CustomTags.PISTONS).add(Items.PISTON, Items.STICKY_PISTON);

        // add treated wood stick to vanilla sticks tag
        // noinspection DataFlowIssue ChemicalHelper#getTag can't return null with treated wood rod
        provider.addTag(Tags.Items.RODS_WOODEN)
                .add(GTMaterialItems.MATERIAL_ITEMS.get(TagPrefix.rod, TreatedWood).get());

        // add treated and untreated wood plates to vanilla planks tag
        provider.addTag(ItemTags.PLANKS)
                .add(GTMaterialItems.MATERIAL_ITEMS.get(plate, TreatedWood).get())
                .add(GTMaterialItems.MATERIAL_ITEMS.get(plate, Wood).get());

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

    private static IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> addTag(RegistrateItemTagsProvider provider,
                                                                                 TagPrefix prefix, Material material) {
        return provider.addTag(Objects.requireNonNull(ChemicalHelper.getTag(prefix, material),
                "%s/%s doesn't have any tags!".formatted(prefix, material)));
    }
}
