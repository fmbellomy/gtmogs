package com.gregtechceu.gtceu.integration.kjs.builders.machine;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.editor.EditableMachineUI;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.RotationState;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.multiblock.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import com.lowdragmc.lowdraglib.client.renderer.IRenderer;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.*;

public class MultiblockMachineBuilderWrapper extends BuilderBase<MultiblockMachineDefinition> {

    private final MultiblockMachineBuilder builder;

    public MultiblockMachineBuilderWrapper(ResourceLocation id) {
        super(id);
        builder = new MultiblockMachineBuilder(GTRegistrate.createIgnoringListenerErrors(id.getNamespace()), id.getPath(),
                WorkableElectricMultiblockMachine::new,
                MetaMachineBlock::new,
                MetaMachineItem::new,
                MetaMachineBlockEntity::createBlockEntity);
    }

    public MultiblockMachineBuilder shapeInfo(Function<MultiblockMachineDefinition, MultiblockShapeInfo> shape) {
        this.shapeInfos.add(d -> List.of(shape.apply(d)));
        return this;
    }

    public MultiblockMachineBuilder shapeInfos(Function<MultiblockMachineDefinition, List<MultiblockShapeInfo>> shapes) {
        this.shapeInfos.add(shapes);
        return this;
    }

    public MultiblockMachineBuilder recoveryItems(Supplier<ItemLike[]> items) {
        this.recoveryItems.add(() -> Arrays.stream(items.get()).map(ItemLike::asItem).map(Item::getDefaultInstance)
                .toArray(ItemStack[]::new));
        return this;
    }

    public MultiblockMachineBuilder recoveryStacks(Supplier<ItemStack[]> stacks) {
        this.recoveryItems.add(stacks);
        return this;
    }

    public MultiblockMachineBuilderWrapper definition(Function<ResourceLocation, MultiblockMachineDefinition> definition) {
        return (MultiblockMachineBuilder) super.definition(definition);
    }

    public MultiblockMachineBuilderWrapper machine(Function<IMachineBlockEntity, MetaMachine> machine) {
        return (MultiblockMachineBuilder) super.machine(machine);
    }

    public MultiblockMachineBuilderWrapper renderer(@Nullable Supplier<IRenderer> renderer) {
        return (MultiblockMachineBuilder) super.renderer(renderer);
    }

    public MultiblockMachineBuilderWrapper shape(VoxelShape shape) {
        return (MultiblockMachineBuilder) super.shape(shape);
    }

    public MultiblockMachineBuilderWrapper multiblockPreviewRenderer(boolean multiBlockWorldPreview,
                                                                     boolean multiBlockXEIPreview) {
        return (MultiblockMachineBuilder) super.multiblockPreviewRenderer(multiBlockWorldPreview, multiBlockXEIPreview);
    }

    public MultiblockMachineBuilderWrapper rotationState(RotationState rotationState) {
        return (MultiblockMachineBuilder) super.rotationState(rotationState);
    }

    public MultiblockMachineBuilderWrapper hasTESR(boolean hasTESR) {
        return (MultiblockMachineBuilder) super.hasTESR(hasTESR);
    }

    public MultiblockMachineBuilderWrapper blockProp(NonNullUnaryOperator<BlockBehaviour.Properties> blockProp) {
        return (MultiblockMachineBuilder) super.blockProp(blockProp);
    }

    public MultiblockMachineBuilderWrapper itemProp(NonNullUnaryOperator<Item.Properties> itemProp) {
        return (MultiblockMachineBuilder) super.itemProp(itemProp);
    }

    public MultiblockMachineBuilderWrapper blockBuilder(@Nullable Consumer<BlockBuilder<? extends Block, ?>> blockBuilder) {
        return (MultiblockMachineBuilder) super.blockBuilder(blockBuilder);
    }

    public MultiblockMachineBuilderWrapper itemBuilder(@Nullable Consumer<ItemBuilder<? extends MetaMachineItem, ?>> itemBuilder) {
        return (MultiblockMachineBuilder) super.itemBuilder(itemBuilder);
    }

    public MultiblockMachineBuilderWrapper recipeTypes(GTRecipeType... recipeTypes) {
        return (MultiblockMachineBuilder) super.recipeTypes(recipeTypes);
    }

    public MultiblockMachineBuilderWrapper recipeType(GTRecipeType recipeTypes) {
        return (MultiblockMachineBuilder) super.recipeType(recipeTypes);
    }

    public MultiblockMachineBuilderWrapper tier(int tier) {
        return (MultiblockMachineBuilder) super.tier(tier);
    }

    public MultiblockMachineBuilder recipeOutputLimits(Object2IntMap<RecipeCapability<?>> map) {
        return (MultiblockMachineBuilder) super.recipeOutputLimits(map);
    }

    public MultiblockMachineBuilderWrapper addOutputLimit(RecipeCapability<?> capability, int limit) {
        return (MultiblockMachineBuilder) super.addOutputLimit(capability, limit);
    }

    public MultiblockMachineBuilderWrapper itemColor(BiFunction<ItemStack, Integer, Integer> itemColor) {
        return (MultiblockMachineBuilder) super.itemColor(itemColor);
    }

    public MultiblockMachineBuilderWrapper modelRenderer(Supplier<ResourceLocation> model) {
        return (MultiblockMachineBuilder) super.modelRenderer(model);
    }

    public MultiblockMachineBuilderWrapper defaultModelRenderer() {
        return (MultiblockMachineBuilder) super.defaultModelRenderer();
    }

    public MultiblockMachineBuilderWrapper tieredHullRenderer(ResourceLocation model) {
        return (MultiblockMachineBuilder) super.tieredHullRenderer(model);
    }

    public MultiblockMachineBuilderWrapper overlayTieredHullRenderer(String name) {
        return (MultiblockMachineBuilder) super.overlayTieredHullRenderer(name);
    }

    public MultiblockMachineBuilderWrapper workableTieredHullRenderer(ResourceLocation workableModel) {
        return (MultiblockMachineBuilder) super.workableTieredHullRenderer(workableModel);
    }

    public MultiblockMachineBuilderWrapper workableCasingRenderer(ResourceLocation baseCasing, ResourceLocation overlayModel) {
        return (MultiblockMachineBuilder) super.workableCasingRenderer(baseCasing, overlayModel);
    }

    public MultiblockMachineBuilderWrapper workableCasingRenderer(ResourceLocation baseCasing, ResourceLocation overlayModel,
                                                                  boolean tint) {
        return (MultiblockMachineBuilder) super.workableCasingRenderer(baseCasing, overlayModel, tint);
    }

    public MultiblockMachineBuilderWrapper sidedWorkableCasingRenderer(String basePath, ResourceLocation overlayModel,
                                                                       boolean tint) {
        return (MultiblockMachineBuilder) super.sidedWorkableCasingRenderer(basePath, overlayModel, tint);
    }

    public MultiblockMachineBuilderWrapper sidedWorkableCasingRenderer(String basePath, ResourceLocation overlayModel) {
        return (MultiblockMachineBuilder) super.sidedWorkableCasingRenderer(basePath, overlayModel);
    }

    public MultiblockMachineBuilderWrapper tooltipBuilder(@Nullable BiConsumer<ItemStack, List<Component>> tooltipBuilder) {
        return (MultiblockMachineBuilder) super.tooltipBuilder(tooltipBuilder);
    }

    public MultiblockMachineBuilderWrapper appearance(@Nullable Supplier<BlockState> state) {
        return (MultiblockMachineBuilder) super.appearance(state);
    }

    public MultiblockMachineBuilderWrapper appearanceBlock(Supplier<? extends Block> block) {
        return (MultiblockMachineBuilder) super.appearanceBlock(block);
    }

    public MultiblockMachineBuilderWrapper langValue(@Nullable String langValue) {
        return (MultiblockMachineBuilder) super.langValue(langValue);
    }

    public MultiblockMachineBuilderWrapper overlaySteamHullRenderer(String name) {
        return (MultiblockMachineBuilder) super.overlaySteamHullRenderer(name);
    }

    public MultiblockMachineBuilderWrapper workableSteamHullRenderer(boolean isHighPressure, ResourceLocation workableModel) {
        return (MultiblockMachineBuilder) super.workableSteamHullRenderer(isHighPressure, workableModel);
    }

    public MultiblockMachineBuilderWrapper tooltips(Component... components) {
        return (MultiblockMachineBuilder) super.tooltips(components);
    }

    public MultiblockMachineBuilderWrapper conditionalTooltip(Component component, Supplier<Boolean> condition) {
        return conditionalTooltip(component, condition.get());
    }

    public MultiblockMachineBuilderWrapper conditionalTooltip(Component component, boolean condition) {
        if (condition)
            tooltips(component);
        return this;
    }

    public MultiblockMachineBuilderWrapper abilities(PartAbility... abilities) {
        return (MultiblockMachineBuilder) super.abilities(abilities);
    }

    public MultiblockMachineBuilderWrapper paintingColor(int paintingColor) {
        return (MultiblockMachineBuilder) super.paintingColor(paintingColor);
    }

    public MultiblockMachineBuilderWrapper recipeModifier(RecipeModifier recipeModifier) {
        return (MultiblockMachineBuilder) super.recipeModifier(recipeModifier);
    }

    public MultiblockMachineBuilderWrapper recipeModifier(RecipeModifier recipeModifier, boolean alwaysTryModifyRecipe) {
        return (MultiblockMachineBuilder) super.recipeModifier(recipeModifier, alwaysTryModifyRecipe);
    }

    public MultiblockMachineBuilderWrapper recipeModifiers(RecipeModifier... recipeModifiers) {
        return (MultiblockMachineBuilder) super.recipeModifiers(recipeModifiers);
    }

    public MultiblockMachineBuilderWrapper recipeModifiers(boolean alwaysTryModifyRecipe, RecipeModifier... recipeModifiers) {
        return (MultiblockMachineBuilder) super.recipeModifiers(alwaysTryModifyRecipe, recipeModifiers);
    }

    public MultiblockMachineBuilder noRecipeModifier() {
        return (MultiblockMachineBuilder) super.noRecipeModifier();
    }

    public MultiblockMachineBuilderWrapper alwaysTryModifyRecipe(boolean alwaysTryModifyRecipe) {
        return (MultiblockMachineBuilder) super.alwaysTryModifyRecipe(alwaysTryModifyRecipe);
    }

    public MultiblockMachineBuilderWrapper beforeWorking(BiPredicate<IRecipeLogicMachine, GTRecipe> beforeWorking) {
        return (MultiblockMachineBuilder) super.beforeWorking(beforeWorking);
    }

    public MultiblockMachineBuilderWrapper onWorking(Predicate<IRecipeLogicMachine> onWorking) {
        return (MultiblockMachineBuilder) super.onWorking(onWorking);
    }

    public MultiblockMachineBuilderWrapper onWaiting(Consumer<IRecipeLogicMachine> onWaiting) {
        return (MultiblockMachineBuilder) super.onWaiting(onWaiting);
    }

    public MultiblockMachineBuilderWrapper afterWorking(Consumer<IRecipeLogicMachine> afterWorking) {
        return (MultiblockMachineBuilder) super.afterWorking(afterWorking);
    }

    public MultiblockMachineBuilderWrapper regressWhenWaiting(boolean regressWhenWaiting) {
        return (MultiblockMachineBuilder) super.regressWhenWaiting(regressWhenWaiting);
    }

    public MultiblockMachineBuilderWrapper editableUI(@Nullable EditableMachineUI editableUI) {
        return (MultiblockMachineBuilder) super.editableUI(editableUI);
    }

    public MultiblockMachineBuilderWrapper onBlockEntityRegister(NonNullConsumer<BlockEntityType<BlockEntity>> onBlockEntityRegister) {
        return (MultiblockMachineBuilder) super.onBlockEntityRegister(onBlockEntityRegister);
    }

    public MultiblockMachineBuilderWrapper allowExtendedFacing(boolean allowExtendedFacing) {
        return (MultiblockMachineBuilder) super.allowExtendedFacing(allowExtendedFacing);
    }

    @Override
    public MultiblockMachineDefinition createObject() {
        return null;
    }

}
