package com.gregtechceu.gtceu.integration.kjs;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.fluid.FluidBuilder;
import com.gregtechceu.gtceu.api.fluid.FluidState;
import com.gregtechceu.gtceu.api.fluid.attribute.FluidAttributes;
import com.gregtechceu.gtceu.api.fluid.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.RotationState;
import com.gregtechceu.gtceu.api.machine.SimpleGeneratorMachine;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.Element;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.material.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.material.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.material.material.info.MaterialIconType;
import com.gregtechceu.gtceu.api.material.material.properties.HazardProperty;
import com.gregtechceu.gtceu.api.material.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.material.material.properties.ToolProperty;
import com.gregtechceu.gtceu.api.material.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.material.material.stack.MaterialStack;
import com.gregtechceu.gtceu.api.medicalcondition.MedicalCondition;
import com.gregtechceu.gtceu.api.medicalcondition.Symptom;
import com.gregtechceu.gtceu.api.multiblock.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.multiblock.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.multiblock.Predicates;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.api.recipe.component.CraftingComponent;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.api.worldgen.*;
import com.gregtechceu.gtceu.api.worldgen.bedrockfluid.BedrockFluidDefinition;
import com.gregtechceu.gtceu.api.worldgen.bedrockore.BedrockOreDefinition;
import com.gregtechceu.gtceu.api.worldgen.generator.IndicatorGenerator;
import com.gregtechceu.gtceu.api.worldgen.generator.VeinGenerator;
import com.gregtechceu.gtceu.api.worldgen.generator.indicators.SurfaceIndicatorGenerator.IndicatorPlacement;
import com.gregtechceu.gtceu.api.worldgen.generator.veins.DikeVeinGenerator;
import com.gregtechceu.gtceu.common.machine.multiblock.primitive.PrimitiveFancyUIWorkableMachine;
import com.gregtechceu.gtceu.data.block.GCYMBlocks;
import com.gregtechceu.gtceu.data.block.GTBlocks;
import com.gregtechceu.gtceu.data.block.GTMaterialBlocks;
import com.gregtechceu.gtceu.data.item.GTItems;
import com.gregtechceu.gtceu.data.item.GTMaterialItems;
import com.gregtechceu.gtceu.data.machine.GCYMMachines;
import com.gregtechceu.gtceu.data.machine.GTMachineUtils;
import com.gregtechceu.gtceu.data.machine.GTMachines;
import com.gregtechceu.gtceu.data.machine.GTMultiMachines;
import com.gregtechceu.gtceu.data.material.GTElements;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.gregtechceu.gtceu.data.medicalcondition.GTMedicalConditions;
import com.gregtechceu.gtceu.data.recipe.GTCraftingComponents;
import com.gregtechceu.gtceu.data.recipe.GTRecipeCategories;
import com.gregtechceu.gtceu.data.recipe.GTRecipeModifiers;
import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import com.gregtechceu.gtceu.data.sound.GTSoundEntries;
import com.gregtechceu.gtceu.data.worldgen.GTOreVeins;
import com.gregtechceu.gtceu.integration.kjs.builders.block.ActiveBlockBuilder;
import com.gregtechceu.gtceu.integration.kjs.builders.block.CoilBlockBuilder;
import com.gregtechceu.gtceu.integration.kjs.builders.machine.*;
import com.gregtechceu.gtceu.integration.kjs.builders.material.ElementBuilder;
import com.gregtechceu.gtceu.integration.kjs.builders.material.MaterialBuilderWrapper;
import com.gregtechceu.gtceu.integration.kjs.builders.prefix.OreTagPrefixBuilder;
import com.gregtechceu.gtceu.integration.kjs.builders.prefix.TagPrefixBuilder;
import com.gregtechceu.gtceu.integration.kjs.builders.recipetype.GTRecipeCategoryBuilder;
import com.gregtechceu.gtceu.integration.kjs.builders.recipetype.GTRecipeTypeBuilder;
import com.gregtechceu.gtceu.integration.kjs.builders.worldgen.BedrockFluidBuilder;
import com.gregtechceu.gtceu.integration.kjs.builders.worldgen.BedrockOreBuilder;
import com.gregtechceu.gtceu.integration.kjs.builders.worldgen.DimensionMarkerBuilder;
import com.gregtechceu.gtceu.integration.kjs.builders.worldgen.OreVeinDefinitionBuilder;
import com.gregtechceu.gtceu.integration.kjs.helpers.GTResourceLocation;
import com.gregtechceu.gtceu.integration.kjs.helpers.MachineConstructors;
import com.gregtechceu.gtceu.integration.kjs.helpers.MachineModifiers;
import com.gregtechceu.gtceu.integration.kjs.helpers.MaterialStackWrapper;
import com.gregtechceu.gtceu.integration.kjs.recipe.GTRecipeSchema;
import com.gregtechceu.gtceu.integration.kjs.recipe.GTShapedRecipeSchema;
import com.gregtechceu.gtceu.integration.kjs.recipe.components.CapabilityMapComponent;
import com.gregtechceu.gtceu.integration.kjs.recipe.components.GTRecipeComponents;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;

import com.mojang.serialization.DataResult;
import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.NBTWrapper;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentTypeRegistry;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.registry.ServerRegistryRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.Wrapper;

import java.util.*;

public class GTKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void initStartup() {
        KubeJSPlugin.super.initStartup();
    }

    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        registry.addDefault(GTRegistries.ELEMENT_REGISTRY, ElementBuilder.class, ElementBuilder::new);
        registry.addDefault(GTRegistries.DIMENSION_MARKER_REGISTRY, DimensionMarkerBuilder.class,
                DimensionMarkerBuilder::new);
        registry.addDefault(GTRegistries.MATERIAL_REGISTRY, MaterialBuilderWrapper.class, MaterialBuilderWrapper::new);
        registry.of(GTRegistries.TAG_PREFIX_REGISTRY, reg -> {
            reg.addDefault(TagPrefixBuilder.class, TagPrefixBuilder::new);
            reg.add(ID.kjs("ore"), OreTagPrefixBuilder.class, OreTagPrefixBuilder::new);
        });

        registry.addDefault(GTRegistries.RECIPE_TYPE_REGISTRY, GTRecipeTypeBuilder.class, GTRecipeTypeBuilder::new);
        registry.addDefault(GTRegistries.RECIPE_CATEGORY_REGISTRY, GTRecipeCategoryBuilder.class,
                GTRecipeCategoryBuilder::new);

        registry.of(GTRegistries.MACHINE_REGISTRY, reg -> {
            reg.addDefault(KJSWrappingMachineBuilder.class,
                    (id) -> new KJSWrappingMachineBuilder(id,
                            new KJSTieredMachineBuilder(id, SimpleTieredMachine::new,
                                    SimpleTieredMachine.EDITABLE_UI_CREATOR)));

            reg.add(ID.kjs("custom"), KJSWrappingMachineBuilder.class,
                    (id) -> new KJSWrappingMachineBuilder(id, new KJSTieredMachineBuilder(id)));
            reg.add(ID.kjs("steam"), KJSSteamMachineBuilder.class, KJSSteamMachineBuilder::new);
            reg.add(ID.kjs("generator"), KJSWrappingMachineBuilder.class,
                    (id) -> new KJSWrappingMachineBuilder(id,
                            new KJSTieredMachineBuilder(id, SimpleGeneratorMachine::new,
                                    SimpleGeneratorMachine.EDITABLE_UI_CREATOR)));

            reg.add(ID.kjs("multiblock"), MultiblockMachineBuilderWrapper.class,
                    MultiblockMachineBuilderWrapper::createKJSMulti);
            reg.add(ID.kjs("tiered_multiblock"), KJSWrappingMultiblockBuilder.class, KJSWrappingMultiblockBuilder::new);
            reg.add(ID.kjs("primitive"), MultiblockMachineBuilderWrapper.class,
                    (id) -> MultiblockMachineBuilderWrapper.createKJSMulti(id, PrimitiveFancyUIWorkableMachine::new));
        });

        registry.of(Registries.BLOCK, reg -> {
            reg.add(GTCEu.id("active"), ActiveBlockBuilder.class, ActiveBlockBuilder::new);
            reg.add(GTCEu.id("coil"), CoilBlockBuilder.class, CoilBlockBuilder::new);
        });

        registry.addDefault(GTRegistries.ORE_VEIN_REGISTRY, OreVeinDefinitionBuilder.class,
                OreVeinDefinitionBuilder::new);
        registry.addDefault(GTRegistries.BEDROCK_FLUID_REGISTRY, BedrockFluidBuilder.class, BedrockFluidBuilder::new);
        registry.addDefault(GTRegistries.BEDROCK_ORE_REGISTRY, BedrockOreBuilder.class, BedrockOreBuilder::new);
    }

    @Override
    public void registerServerRegistries(ServerRegistryRegistry registry) {
        registry.register(GTRegistries.ORE_VEIN_REGISTRY, OreVeinDefinition.DIRECT_CODEC, OreVeinDefinition.class);
        registry.register(GTRegistries.BEDROCK_FLUID_REGISTRY,
                BedrockFluidDefinition.DIRECT_CODEC, BedrockFluidDefinition.class);
        registry.register(GTRegistries.BEDROCK_ORE_REGISTRY,
                BedrockOreDefinition.DIRECT_CODEC, BedrockOreDefinition.class);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(GTCEuStartupEvents.GROUP);
        registry.register(GTCEuServerEvents.GROUP);
    }

    @Override
    public void registerClasses(ClassFilter filter) {
        // allow user to access all gtceu classes by importing them.
        filter.allow("com.gregtechceu.gtceu");
    }

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry event) {
        for (var id : BuiltInRegistries.RECIPE_TYPE.keySet()) {
            RecipeType<?> type = BuiltInRegistries.RECIPE_TYPE.get(id);
            if (!(type instanceof GTRecipeType)) continue;
            event.register(id, GTRecipeSchema.SCHEMA);
        }
        event.namespace(GTCEu.MOD_ID).register("shaped", GTShapedRecipeSchema.SCHEMA);
    }

    @Override
    public void registerRecipeComponents(RecipeComponentTypeRegistry event) {
        event.register(GTRecipeComponents.TAG_TYPE);
        event.register(GTRecipeComponents.RECIPE_CONDITION_TYPE);
        event.register(GTRecipeComponents.RESOURCE_LOCATION_TYPE);
        event.register(GTRecipeComponents.RECIPE_CAPABILITY_TYPE);
        event.register(GTRecipeComponents.CHANCE_LOGIC_TYPE);
        event.register(CapabilityMapComponent.TYPE);

        event.register(GTRecipeComponents.ITEM.type());
        event.register(GTRecipeComponents.FLUID.type());
        event.register(GTRecipeComponents.EU.type());
    }

    @Override
    public void registerBindings(BindingRegistry event) {
        // Mod related
        event.add("GTCEu", GTCEu.class);
        event.add("GTCEuAPI", GTCEuAPI.class);
        event.add("GTRegistries", GTRegistries.class);
        event.add("GTValues", GTValues.class);
        // Material related
        event.add("GTElements", GTElements.class);
        event.add("GTMaterials", GTMaterials.class);
        event.add("GTMaterialRegistry", GTCEuAPI.materialManager);
        event.add("TagPrefix", TagPrefix.class);
        event.add("ItemGenerationCondition", TagPrefix.Conditions.class);
        event.add("MaterialEntry", MaterialEntry.class);
        event.add("GTMaterialFlags", MaterialFlags.class);
        event.add("GTFluidAttributes", FluidAttributes.class);
        event.add("GTFluidBuilder", FluidBuilder.class);
        event.add("GTFluidStorageKeys", FluidStorageKeys.class);
        event.add("GTFluidState", FluidState.class);
        event.add("GTMaterialIconSet", MaterialIconSet.class);
        event.add("GTMaterialIconType", MaterialIconType.class);
        event.add("ChemicalHelper", ChemicalHelper.class);
        event.add("PropertyKey", PropertyKey.class);
        event.add("ToolProperty", ToolProperty.class);
        event.add("GTToolType", GTToolType.class);
        // Block/Item related
        event.add("GTBlocks", GTBlocks.class);
        event.add("GTMaterialBlocks", GTMaterialBlocks.class);
        event.add("GCYMBlocks", GCYMBlocks.class);
        event.add("GTMachines", GTMachines.class);
        event.add("GTMultiMachines", GTMultiMachines.class);
        event.add("GTMachineUtils", GTMachineUtils.class);
        event.add("GCYMMachines", GCYMMachines.class);
        event.add("GTItems", GTItems.class);
        event.add("GTMaterialItems", GTMaterialItems.class);
        // Recipe related
        event.add("GTRecipeTypes", GTRecipeTypes.class);
        event.add("GTRecipeCategories", GTRecipeCategories.class);
        event.add("GTMedicalConditions", GTMedicalConditions.class);
        event.add("GTRecipeModifiers", GTRecipeModifiers.class);
        event.add("OverclockingLogic", OverclockingLogic.class);
        event.add("MachineConstructors", MachineConstructors.class);
        event.add("MachineModifiers", MachineModifiers.class);
        event.add("ModifierFunction", ModifierFunction.class);
        event.add("RecipeCapability", RecipeCapability.class);
        event.add("ChanceLogic", ChanceLogic.class);
        event.add("CleanroomType", CleanroomType.class);
        event.add("CraftingComponent", CraftingComponent.class);
        event.add("GTCraftingComponents", GTCraftingComponents.class);
        // Sound related
        event.add("GTSoundEntries", GTSoundEntries.class);
        event.add("SoundType", SoundType.class);
        // GUI related
        event.add("GuiTextures", GuiTextures.class);
        // Multiblock related
        event.add("RotationState", RotationState.class);
        event.add("FactoryBlockPattern", FactoryBlockPattern.class);
        event.add("MultiblockShapeInfo", MultiblockShapeInfo.class);
        event.add("Predicates", Predicates.class);
        event.add("PartAbility", PartAbility.class);

        // Hazard Related
        event.add("HazardProperty", HazardProperty.class);
        event.add("MedicalCondition", MedicalCondition.class);
        event.add("Symptom", Symptom.class);
        // World Gen Related
        event.add("GTOreVein", OreVeinDefinition.class);
        event.add("GTLayerPattern", GTLayerPattern.class);
        event.add("GTDikeBlockDefinition", DikeVeinGenerator.DikeBlockDefinition.class);
        event.add("GTOres", GTOreVeins.class);
        event.add("GTWorldGenLayers", WorldGenLayers.class);
        // MaterialColor stuff, for TagPrefix
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry registry) {
        registry.register(GTResourceLocation.class, GTResourceLocation::wrap);
        registry.register(GTRecipeType.class, o -> {
            if (o instanceof Wrapper w) {
                o = w.unwrap();
            }
            if (o instanceof GTRecipeType recipeType) return recipeType;
            if (o instanceof CharSequence chars) return GTRecipeTypes.get(chars.toString());
            return null;
        });
        registry.register(GTRecipeCategory.class, o -> {
            if (o instanceof Wrapper w) {
                o = w.unwrap();
            }
            if (o instanceof GTRecipeCategory recipeCategory) return recipeCategory;
            if (o instanceof CharSequence chars) return GTRecipeCategories.get(chars.toString());
            return null;
        });

        registry.register(Element.class, o -> {
            if (o instanceof Element element) return element;
            if (o instanceof CharSequence chars) return GTElements.get(chars.toString());
            return null;
        });
        registry.register(Material.class, o -> {
            if (o instanceof Material material) return material;
            if (o instanceof CharSequence chars) return GTMaterials.get(chars.toString());
            return null;
        });
        registry.register(MachineDefinition.class, o -> {
            if (o instanceof MachineDefinition definition) return definition;
            if (o instanceof CharSequence chars) return GTMachines.get(chars.toString());
            return null;
        });

        registry.register(TagPrefix.class, o -> {
            if (o instanceof TagPrefix tagPrefix) return tagPrefix;
            if (o instanceof ResourceLocation resLoc) return GTRegistries.TAG_PREFIXES.get(resLoc);
            return GTRegistries.TAG_PREFIXES.get(GTResourceLocation.wrap(o).wrapped());
        });
        registry.register(MaterialEntry.class, MaterialEntry::of);

        registry.register(RecipeCapability.class, o -> {
            if (o instanceof RecipeCapability<?> capability) return capability;
            if (o instanceof ResourceLocation id) return GTRegistries.RECIPE_CAPABILITIES.get(id);
            GTResourceLocation wrapper = GTResourceLocation.wrap(o);
            if (wrapper == null) return null;
            return GTRegistries.RECIPE_CAPABILITIES.get(wrapper.wrapped());
        });
        registry.register(ChanceLogic.class, o -> {
            if (o instanceof ChanceLogic capability) return capability;
            if (o instanceof ResourceLocation id) return GTRegistries.CHANCE_LOGICS.get(id);
            GTResourceLocation wrapper = GTResourceLocation.wrap(o);
            if (wrapper == null) return null;
            return GTRegistries.CHANCE_LOGICS.get(wrapper.wrapped());
        });

        registry.register(MaterialIconSet.class, o -> {
            if (o instanceof MaterialIconSet iconSet) return iconSet;
            if (o instanceof CharSequence chars) return MaterialIconSet.getByName(chars.toString());
            return null;
        });
        registry.register(MaterialStack.class, o -> {
            if (o instanceof MaterialStack stack) return stack;
            if (o instanceof Material material) return new MaterialStack(material, 1);
            if (o instanceof CharSequence chars) return MaterialStack.fromString(chars);
            return null;
        });
        registry.register(MaterialStackWrapper.class, o -> {
            if (o instanceof MaterialStackWrapper wrapper) return wrapper;
            if (o instanceof MaterialStack stack) return new MaterialStackWrapper(stack::material, stack.amount());
            if (o instanceof Material material) return new MaterialStackWrapper(() -> material, 1);
            if (o instanceof CharSequence chars) return MaterialStackWrapper.fromString(chars);
            return null;
        });

        registry.register(IWorldGenLayer.class, o -> {
            if (o instanceof IWorldGenLayer layer) return layer;
            if (o instanceof CharSequence chars) return WorldGenLayers.getByName(chars.toString());
            return null;
        });
        registry.register(HeightRangePlacement.class, (cx, o, t) -> {
            if (o instanceof HeightRangePlacement placement) return placement;
            return Optional.ofNullable(NBTWrapper.wrapCompound(cx, o))
                    .map(tag -> HeightRangePlacement.CODEC.codec().parse(NbtOps.INSTANCE, tag))
                    .flatMap(DataResult::result)
                    .orElse(null);
        });
        registry.register(BiomeWeightModifier.class, (cx, o, t) -> {
            if (o instanceof BiomeWeightModifier modifier) return modifier;
            return Optional.ofNullable(NBTWrapper.wrapCompound(cx, o))
                    .map(tag -> BiomeWeightModifier.CODEC.parse(NbtOps.INSTANCE, tag))
                    .flatMap(DataResult::result)
                    .orElse(null);
        });
        registry.register(VeinGenerator.class, (cx, o, t) -> {
            if (o instanceof VeinGenerator generator) return generator;
            return Optional.ofNullable(NBTWrapper.wrapCompound(cx, o))
                    .map(tag -> VeinGenerator.DIRECT_CODEC.parse(NbtOps.INSTANCE, tag))
                    .flatMap(DataResult::result)
                    .orElse(null);
        });
        registry.register(IndicatorGenerator.class, (cx, o, t) -> {
            if (o instanceof IndicatorGenerator generator) return generator;
            return Optional.ofNullable(NBTWrapper.wrapCompound(cx, o))
                    .map(tag -> IndicatorGenerator.DIRECT_CODEC.parse(NbtOps.INSTANCE, tag))
                    .flatMap(DataResult::result)
                    .orElse(null);
        });
        registry.register(IndicatorPlacement.class, o -> {
            if (o instanceof IndicatorPlacement placement) return placement;
            if (o instanceof CharSequence str) return IndicatorPlacement.getByName(str.toString());
            return null;
        });
        registry.register(MedicalCondition.class, o -> {
            if (o instanceof MedicalCondition condition) return condition;
            if (o instanceof CharSequence str) return MedicalCondition.CONDITIONS.get(str.toString());
            return null;
        });
        // jank because Rhino doesn't agree that it's an interface
        registry.register(IWorldGenLayer.RuleTestSupplier.class, (cx, o, t) -> {
            if (o instanceof IWorldGenLayer.RuleTestSupplier supplier) return supplier;
            return () -> BlockStatePredicate.wrapRuleTest(cx, o);
        });
        registry.register(CraftingComponent.class, o -> {
            if (o instanceof CraftingComponent comp) return comp;
            if (o instanceof CharSequence str) return CraftingComponent.ALL_COMPONENTS.get(str.toString());
            return null;
        });
    }
}
