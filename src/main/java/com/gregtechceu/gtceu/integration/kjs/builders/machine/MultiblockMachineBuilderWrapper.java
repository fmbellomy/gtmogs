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
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.multiblock.BlockPattern;
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
import net.minecraft.core.Direction;
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
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.function.*;

@SuppressWarnings("unused")
public class MultiblockMachineBuilderWrapper extends BuilderBase<MultiblockMachineDefinition> {

    private final MultiblockMachineBuilder internal;

    public MultiblockMachineBuilderWrapper(ResourceLocation id, MultiblockMachineBuilder internal) {
        super(id);
        this.internal = internal;
    }

    public MultiblockMachineBuilderWrapper generator(boolean generator) {
        internal.generator(generator);
        return this;
    }

    public MultiblockMachineBuilderWrapper pattern(Function<MultiblockMachineDefinition, BlockPattern> pattern) {
        internal.pattern(pattern);
        return this;
    }

    public MultiblockMachineBuilderWrapper allowFlip(boolean allowFlip) {
        internal.allowFlip(allowFlip);
        return this;
    }

    public MultiblockMachineBuilderWrapper partSorter(Comparator<IMultiPart> partSorter) {
        internal.partSorter(partSorter);
        return this;
    }

    public MultiblockMachineBuilderWrapper partAppearance(@Nullable TriFunction<IMultiController, IMultiPart, Direction, BlockState> partAppearance) {
        internal.partAppearance(partAppearance);
        return this;
    }

    public MultiblockMachineBuilderWrapper additionalDisplay(BiConsumer<IMultiController, List<Component>> additionalDisplay) {
        internal.additionalDisplay(additionalDisplay);
        return this;
    }

    public MultiblockMachineBuilderWrapper shapeInfo(Function<MultiblockMachineDefinition, MultiblockShapeInfo> shape) {
        internal.shapeInfo(shape);
        return this;
    }

    public MultiblockMachineBuilderWrapper shapeInfos(Function<MultiblockMachineDefinition, List<MultiblockShapeInfo>> shapes) {
        internal.shapeInfos(shapes);
        return this;
    }

    public MultiblockMachineBuilderWrapper recoveryItems(Supplier<ItemLike[]> items) {
        internal.recoveryItems(items);
        return this;
    }

    public MultiblockMachineBuilderWrapper recoveryStacks(Supplier<ItemStack[]> stacks) {
        internal.recoveryStacks(stacks);
        return this;
    }

    public MultiblockMachineBuilderWrapper definition(Function<ResourceLocation, MultiblockMachineDefinition> definition) {
        internal.definition(definition);
        return this;
    }

    public MultiblockMachineBuilderWrapper machine(Function<IMachineBlockEntity, MetaMachine> machine) {
        internal.machine(machine);
        return this;
    }

    public MultiblockMachineBuilderWrapper renderer(@Nullable Supplier<IRenderer> renderer) {
        internal.renderer(renderer);
        return this;
    }

    public MultiblockMachineBuilderWrapper shape(VoxelShape shape) {
        internal.shape(shape);
        return this;
    }

    public MultiblockMachineBuilderWrapper multiblockPreviewRenderer(boolean multiBlockWorldPreview,
                                                                     boolean multiBlockXEIPreview) {
        internal.multiblockPreviewRenderer(multiBlockWorldPreview, multiBlockXEIPreview);
        return this;
    }

    public MultiblockMachineBuilderWrapper rotationState(RotationState rotationState) {
        internal.rotationState(rotationState);
        return this;
    }

    public MultiblockMachineBuilderWrapper hasTESR(boolean hasTESR) {
        internal.hasTESR(hasTESR);
        return this;
    }

    public MultiblockMachineBuilderWrapper blockProp(NonNullUnaryOperator<BlockBehaviour.Properties> blockProp) {
        internal.blockProp(blockProp);
        return this;
    }

    public MultiblockMachineBuilderWrapper itemProp(NonNullUnaryOperator<Item.Properties> itemProp) {
        internal.itemProp(itemProp);
        return this;
    }

    public MultiblockMachineBuilderWrapper blockBuilder(@Nullable Consumer<BlockBuilder<? extends Block, ?>> blockBuilder) {
        internal.blockBuilder(blockBuilder);
        return this;
    }

    public MultiblockMachineBuilderWrapper itemBuilder(@Nullable Consumer<ItemBuilder<? extends MetaMachineItem, ?>> itemBuilder) {
        internal.itemBuilder(itemBuilder);
        return this;
    }

    public MultiblockMachineBuilderWrapper recipeTypes(GTRecipeType... recipeTypes) {
        internal.recipeTypes(recipeTypes);
        return this;
    }

    public MultiblockMachineBuilderWrapper recipeType(GTRecipeType recipeTypes) {
        internal.recipeType(recipeTypes);
        return this;
    }

    public MultiblockMachineBuilderWrapper tier(int tier) {
        internal.tier(tier);
        return this;
    }

    public MultiblockMachineBuilderWrapper recipeOutputLimits(Object2IntMap<RecipeCapability<?>> map) {
        internal.recipeOutputLimits(map);
        return this;
    }

    public MultiblockMachineBuilderWrapper addOutputLimit(RecipeCapability<?> capability, int limit) {
        internal.addOutputLimit(capability, limit);
        return this;
    }

    public MultiblockMachineBuilderWrapper itemColor(BiFunction<ItemStack, Integer, Integer> itemColor) {
        internal.itemColor(itemColor);
        return this;
    }

    public MultiblockMachineBuilderWrapper modelRenderer(Supplier<ResourceLocation> model) {
        internal.modelRenderer(model);
        return this;
    }

    public MultiblockMachineBuilderWrapper defaultModelRenderer() {
        internal.defaultModelRenderer();
        return this;
    }

    public MultiblockMachineBuilderWrapper tieredHullRenderer(ResourceLocation model) {
        internal.tieredHullRenderer(model);
        return this;
    }

    public MultiblockMachineBuilderWrapper overlayTieredHullRenderer(String name) {
        internal.overlayTieredHullRenderer(name);
        return this;
    }

    public MultiblockMachineBuilderWrapper workableTieredHullRenderer(ResourceLocation workableModel) {
        internal.workableTieredHullRenderer(workableModel);
        return this;
    }

    public MultiblockMachineBuilderWrapper workableCasingRenderer(ResourceLocation baseCasing, ResourceLocation overlayModel) {
        internal.workableCasingRenderer(baseCasing, overlayModel);
        return this;
    }

    public MultiblockMachineBuilderWrapper workableCasingRenderer(ResourceLocation baseCasing, ResourceLocation overlayModel,
                                                                  boolean tint) {
        internal.workableCasingRenderer(baseCasing, overlayModel, tint);
        return this;
    }

    public MultiblockMachineBuilderWrapper sidedWorkableCasingRenderer(String basePath, ResourceLocation overlayModel,
                                                                       boolean tint) {
        internal.sidedWorkableCasingRenderer(basePath, overlayModel, tint);
        return this;
    }

    public MultiblockMachineBuilderWrapper sidedWorkableCasingRenderer(String basePath, ResourceLocation overlayModel) {
        internal.sidedWorkableCasingRenderer(basePath, overlayModel);
        return this;
    }

    public MultiblockMachineBuilderWrapper tooltipBuilder(@Nullable BiConsumer<ItemStack, List<Component>> tooltipBuilder) {
        internal.tooltipBuilder(tooltipBuilder);
        return this;
    }

    public MultiblockMachineBuilderWrapper appearance(@Nullable Supplier<BlockState> state) {
        internal.appearance(state);
        return this;
    }

    public MultiblockMachineBuilderWrapper appearanceBlock(Supplier<? extends Block> block) {
        internal.appearanceBlock(block);
        return this;
    }

    public MultiblockMachineBuilderWrapper langValue(@Nullable String langValue) {
        internal.langValue(langValue);
        return this;
    }

    public MultiblockMachineBuilderWrapper overlaySteamHullRenderer(String name) {
        internal.overlaySteamHullRenderer(name);
        return this;
    }

    public MultiblockMachineBuilderWrapper workableSteamHullRenderer(boolean isHighPressure, ResourceLocation workableModel) {
        internal.workableSteamHullRenderer(isHighPressure, workableModel);
        return this;
    }

    public MultiblockMachineBuilderWrapper tooltips(Component... components) {
        internal.tooltips(components);
        return this;
    }

    public MultiblockMachineBuilderWrapper conditionalTooltip(Component component, Supplier<Boolean> condition) {
        internal.conditionalTooltip(component, condition.get());
        return this;
    }

    public MultiblockMachineBuilderWrapper conditionalTooltip(Component component, boolean condition) {
        internal.conditionalTooltip(component, condition);
        return this;
    }

    public MultiblockMachineBuilderWrapper abilities(PartAbility... abilities) {
        internal.abilities(abilities);
        return this;
    }

    public MultiblockMachineBuilderWrapper paintingColor(int paintingColor) {
        internal.paintingColor(paintingColor);
        return this;
    }

    public MultiblockMachineBuilderWrapper recipeModifier(RecipeModifier recipeModifier) {
        internal.recipeModifier(recipeModifier);
        return this;
    }

    public MultiblockMachineBuilderWrapper recipeModifier(RecipeModifier recipeModifier, boolean alwaysTryModifyRecipe) {
        internal.recipeModifier(recipeModifier, alwaysTryModifyRecipe);
        return this;
    }

    public MultiblockMachineBuilderWrapper recipeModifiers(RecipeModifier... recipeModifiers) {
        internal.recipeModifiers(recipeModifiers);
        return this;
    }

    public MultiblockMachineBuilderWrapper recipeModifiers(boolean alwaysTryModifyRecipe, RecipeModifier... recipeModifiers) {
        internal.recipeModifiers(alwaysTryModifyRecipe, recipeModifiers);
        return this;
    }

    public MultiblockMachineBuilderWrapper noRecipeModifier() {
        internal.noRecipeModifier();
        return this;
    }

    public MultiblockMachineBuilderWrapper alwaysTryModifyRecipe(boolean alwaysTryModifyRecipe) {
        internal.alwaysTryModifyRecipe(alwaysTryModifyRecipe);
        return this;
    }

    public MultiblockMachineBuilderWrapper beforeWorking(BiPredicate<IRecipeLogicMachine, GTRecipe> beforeWorking) {
        internal.beforeWorking(beforeWorking);
        return this;
    }

    public MultiblockMachineBuilderWrapper onWorking(Predicate<IRecipeLogicMachine> onWorking) {
        internal.onWorking(onWorking);
        return this;
    }

    public MultiblockMachineBuilderWrapper onWaiting(Consumer<IRecipeLogicMachine> onWaiting) {
        internal.onWaiting(onWaiting);
        return this;
    }

    public MultiblockMachineBuilderWrapper afterWorking(Consumer<IRecipeLogicMachine> afterWorking) {
        internal.afterWorking(afterWorking);
        return this;
    }

    public MultiblockMachineBuilderWrapper regressWhenWaiting(boolean regressWhenWaiting) {
        internal.regressWhenWaiting(regressWhenWaiting);
        return this;
    }

    public MultiblockMachineBuilderWrapper editableUI(@Nullable EditableMachineUI editableUI) {
        internal.editableUI(editableUI);
        return this;
    }

    public MultiblockMachineBuilderWrapper onBlockEntityRegister(NonNullConsumer<BlockEntityType<BlockEntity>> onBlockEntityRegister) {
        internal.onBlockEntityRegister(onBlockEntityRegister);
        return this;
    }

    public MultiblockMachineBuilderWrapper allowExtendedFacing(boolean allowExtendedFacing) {
        internal.allowExtendedFacing(allowExtendedFacing);
        return this;
    }

    @Override
    public MultiblockMachineDefinition createObject() {
        return internal.register();
    }

    public static MultiblockMachineBuilderWrapper createKJSMulti(ResourceLocation id) {
        var baseBuilder = new MultiblockMachineBuilder(GTRegistrate.createIgnoringListenerErrors(id.getNamespace()), id.getPath(),
                WorkableElectricMultiblockMachine::new,
                MetaMachineBlock::new,
                MetaMachineItem::new,
                MetaMachineBlockEntity::createBlockEntity);
        return new MultiblockMachineBuilderWrapper(id, baseBuilder);
    }

    public static MultiblockMachineBuilderWrapper createKJSMulti(ResourceLocation id,
                                                              KJSTieredMachineBuilder.CreationFunction<? extends MultiblockControllerMachine> machine) {
        var baseBuilder = new MultiblockMachineBuilder(GTRegistrate.createIgnoringListenerErrors(id.getNamespace()), id.getPath(),
                machine::create,
                MetaMachineBlock::new,
                MetaMachineItem::new,
                MetaMachineBlockEntity::createBlockEntity);
        return new MultiblockMachineBuilderWrapper(id, baseBuilder);
    }

}
