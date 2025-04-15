package com.gregtechceu.gtceu.api.capability.recipe;

import com.gregtechceu.gtceu.api.gui.widget.TankWidget;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredientExtensions;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.content.SerializerFluidIngredient;
import com.gregtechceu.gtceu.api.recipe.lookup.AbstractMapIngredient;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.fluid.MapFluidIntersectionIngredient;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.fluid.MapFluidStackDataComponentIngredient;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.fluid.MapFluidStackIngredient;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.fluid.MapFluidStackWeakDataComponentIngredient;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.fluid.MapFluidTagIngredient;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.ui.GTRecipeTypeUI;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.transfer.fluid.IFluidHandlerModifiable;
import com.gregtechceu.gtceu.client.TooltipsHandler;
import com.gregtechceu.gtceu.integration.xei.entry.fluid.FluidEntryList;
import com.gregtechceu.gtceu.integration.xei.entry.fluid.FluidStackList;
import com.gregtechceu.gtceu.integration.xei.entry.fluid.FluidTagList;
import com.gregtechceu.gtceu.integration.xei.handlers.fluid.CycleFluidEntryHandler;
import com.gregtechceu.gtceu.integration.xei.widgets.GTRecipeWidget;
import com.gregtechceu.gtceu.utils.FluidKey;
import com.gregtechceu.gtceu.utils.GTHashMaps;
import com.gregtechceu.gtceu.utils.OverlayedTankHandler;
import com.gregtechceu.gtceu.utils.OverlayingFluidStorage;

import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.jei.IngredientIO;

import lombok.experimental.ExtensionMethod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.neoforged.neoforge.fluids.crafting.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;
import java.util.stream.Collectors;

@ExtensionMethod(SizedIngredientExtensions.class)
public class FluidRecipeCapability extends RecipeCapability<SizedFluidIngredient> {

    public final static FluidRecipeCapability CAP = new FluidRecipeCapability();

    protected FluidRecipeCapability() {
        super("fluid", 0xFF3C70EE, true, 1, SerializerFluidIngredient.INSTANCE);
    }

    @Override
    public SizedFluidIngredient copyInner(SizedFluidIngredient content) {
        return content.copy();
    }

    @Override
    public SizedFluidIngredient copyWithModifier(SizedFluidIngredient content, ContentModifier modifier) {
        if (content.ingredient().isEmpty()) return content.copy();
        return content.copyWithAmount(modifier.apply(content.amount()));
    }

    @Override
    public List<AbstractMapIngredient> convertToMapIngredient(Object obj) {
        List<AbstractMapIngredient> ingredients = new ObjectArrayList<>(1);
        if (obj instanceof SizedFluidIngredient ingredient) {
            switch (ingredient.ingredient()) {
                case TagFluidIngredient tag -> ingredients.add(new MapFluidTagIngredient(tag.tag()));
                case SingleFluidIngredient single -> ingredients
                        .add(new MapFluidStackIngredient(single.getStacks()[0]));
                case DataComponentFluidIngredient component when component.isStrict() -> ingredients
                        .addAll(MapFluidStackDataComponentIngredient.from(ingredient.ingredient()));
                case DataComponentFluidIngredient component when !component.isStrict() -> ingredients
                        .addAll(MapFluidStackWeakDataComponentIngredient.from(ingredient.ingredient()));
                case IntersectionFluidIngredient intersection -> ingredients
                        .add(new MapFluidIntersectionIngredient(intersection));
                case CompoundFluidIngredient compound -> {
                    for (FluidIngredient inner : compound.children()) {
                        ingredients.addAll(convertToMapIngredient(inner));
                    }
                }
                default -> {
                    for (FluidStack fluid : ingredient.getFluids()) {
                        ingredients.add(new MapFluidStackIngredient(fluid));
                    }
                }
            }
        } else if (obj instanceof FluidStack stack) {
            ingredients.add(new MapFluidStackIngredient(stack));
            stack.getFluidHolder().tags()
                    .forEach(tag -> ingredients.add(new MapFluidTagIngredient(tag)));
        }

        return ingredients;
    }

    @Override
    public List<Object> compressIngredients(Collection<Object> ingredients) {
        List<Object> list = new ObjectArrayList<>(ingredients.size());
        for (Object item : ingredients) {
            if (item instanceof SizedFluidIngredient fluid) {
                boolean isEqual = false;
                for (Object obj : list) {
                    if (obj instanceof SizedFluidIngredient SizedFluidIngredient) {
                        if (fluid.equals(SizedFluidIngredient)) {
                            isEqual = true;
                            break;
                        }
                    } else if (obj instanceof FluidStack fluidStack) {
                        if (fluid.test(fluidStack)) {
                            isEqual = true;
                            break;
                        }
                    }
                }
                if (isEqual) continue;
                list.add(fluid);
            } else if (item instanceof FluidStack fluidStack) {
                boolean isEqual = false;
                for (Object obj : list) {
                    if (obj instanceof SizedFluidIngredient fluidIngredient) {
                        if (fluidIngredient.test(fluidStack)) {
                            isEqual = true;
                            break;
                        }
                    } else if (obj instanceof FluidStack stack) {
                        if (FluidStack.isSameFluidSameComponents(fluidStack, stack)) {
                            isEqual = true;
                            break;
                        }
                    }
                }
                if (isEqual) continue;
                list.add(fluidStack);
            }
        }
        return list;
    }

    @Override
    public boolean isRecipeSearchFilter() {
        return true;
    }

    @Override
    public int limitParallel(GTRecipe recipe, IRecipeCapabilityHolder holder, int multiplier) {
        if (holder instanceof ICustomParallel p) return p.limitParallel(recipe, multiplier);

        int minMultiplier = 0;
        int maxMultiplier = multiplier;

        OverlayedTankHandler overlayedFluidHandler = new OverlayedTankHandler(
                holder.getCapabilitiesFlat(IO.OUT, FluidRecipeCapability.CAP).stream()
                        .filter(NotifiableFluidTank.class::isInstance)
                        .map(NotifiableFluidTank.class::cast)
                        .toList());

        List<FluidStack> recipeOutputs = recipe.getOutputContents(FluidRecipeCapability.CAP)
                .stream()
                .map(content -> FluidRecipeCapability.CAP.of(content.getContent()))
                .filter(ingredient -> !ingredient.ingredient().hasNoFluids())
                .map(ingredient -> ingredient.getFluids()[0])
                .toList();

        while (minMultiplier != maxMultiplier) {
            overlayedFluidHandler.reset();

            int returnedAmount = 0;
            int amountToInsert = 0;

            for (FluidStack fluidStack : recipeOutputs) {
                if (fluidStack.getAmount() <= 0) continue;
                if (fluidStack.isEmpty()) continue;
                // Since multiplier starts at Int.MAX, check here for integer overflow
                if (multiplier > Integer.MAX_VALUE / fluidStack.getAmount()) {
                    amountToInsert = Integer.MAX_VALUE;
                } else {
                    amountToInsert = fluidStack.getAmount() * multiplier;
                }
                returnedAmount = amountToInsert - overlayedFluidHandler.tryFill(fluidStack, amountToInsert);
                if (returnedAmount > 0) {
                    break;
                }
            }

            int[] bin = ParallelLogic.adjustMultiplier(returnedAmount == 0, minMultiplier, multiplier, maxMultiplier);
            minMultiplier = bin[0];
            multiplier = bin[1];
            maxMultiplier = bin[2];

        }
        return multiplier;
    }

    @Override
    public int getMaxParallelRatio(IRecipeCapabilityHolder holder, GTRecipe recipe, int parallelAmount) {
        // Find all the fluids in the combined Fluid Input inventories and create oversized FluidStacks
        Map<FluidKey, Integer> fluidStacks = holder.getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP).stream()
                .map(container -> container.getContents().stream().filter(FluidStack.class::isInstance)
                        .map(FluidStack.class::cast).toList())
                .flatMap(container -> GTHashMaps.fromFluidCollection(container).entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum,
                        Object2IntLinkedOpenHashMap::new));

        int minMultiplier = Integer.MAX_VALUE;
        // map the recipe input fluids to account for duplicated fluids,
        // so their sum is counted against the total of fluids available in the input
        Map<SizedFluidIngredient, Integer> fluidCountMap = new HashMap<>();
        Map<SizedFluidIngredient, Integer> notConsumableMap = new HashMap<>();
        for (Content content : recipe.getInputContents(FluidRecipeCapability.CAP)) {
            SizedFluidIngredient fluidInput = FluidRecipeCapability.CAP.of(content.content);
            final int fluidAmount = fluidInput.amount();
            if (content.chance == 0.0f) {
                notConsumableMap.computeIfPresent(fluidInput,
                        (k, v) -> v + fluidAmount);
                notConsumableMap.putIfAbsent(fluidInput, fluidAmount);
            } else {
                fluidCountMap.computeIfPresent(fluidInput,
                        (k, v) -> v + fluidAmount);
                fluidCountMap.putIfAbsent(fluidInput, fluidAmount);
            }
        }

        // Iterate through the recipe inputs, excluding the not consumable fluids from the fluid inventory map
        for (Map.Entry<SizedFluidIngredient, Integer> notConsumableFluid : notConsumableMap.entrySet()) {
            int needed = notConsumableFluid.getValue();
            int available = 0;
            // For every fluid gathered from the fluid inputs.
            for (Map.Entry<FluidKey, Integer> inputFluid : fluidStacks.entrySet()) {
                // Strip the Non-consumable tags here, as FluidKey compares the tags, which causes finding matching
                // fluids
                // in the input tanks to fail, because there is nothing in those hatches with a non-consumable tag
                FluidStack stack = new FluidStack(inputFluid.getKey().fluid, inputFluid.getValue(),
                        inputFluid.getKey().component);
                if (notConsumableFluid.getKey().equals(stack)) {
                    available = inputFluid.getValue();
                    if (available > needed) {
                        inputFluid.setValue(available - needed);
                        needed -= available;
                        break;
                    } else {
                        inputFluid.setValue(0);
                        notConsumableFluid.setValue(needed - available);
                        needed -= available;
                    }
                }
            }
            // We need to check >= available here because of Non-Consumable inputs with stack size. If there is a NC
            // input
            // with size 1000, and only 500 in the input, needed will be equal to available, but this situation should
            // still fail
            // as not all inputs are present
            if (needed >= available) {
                return 0;
            }
        }

        // Return the maximum parallel limit here if there are only non-consumed inputs, which are all found in the
        // input bus
        // At this point, we would have already returned 0 if we were missing any non-consumable inputs, so we can omit
        // that check
        if (fluidCountMap.isEmpty() && !notConsumableMap.isEmpty()) {
            return parallelAmount;
        }

        // Iterate through the fluid inputs in the recipe
        for (Map.Entry<SizedFluidIngredient, Integer> fs : fluidCountMap.entrySet()) {
            int needed = fs.getValue();
            int available = 0;
            // For every fluid gathered from the fluid inputs.
            for (Map.Entry<FluidKey, Integer> inputFluid : fluidStacks.entrySet()) {
                FluidStack stack = new FluidStack(inputFluid.getKey().fluid, inputFluid.getValue(),
                        inputFluid.getKey().component);
                if (fs.getKey().test(stack)) {
                    available += inputFluid.getValue();
                }
            }
            if (available >= needed) {
                int ratio = (int) Math.min(parallelAmount, (float) available / needed);
                if (ratio < minMultiplier) {
                    minMultiplier = ratio;
                }
            } else {
                return 0;
            }
        }
        return minMultiplier;
    }

    @Override
    public @NotNull List<Object> createXEIContainerContents(List<Content> contents, GTRecipe recipe, IO io) {
        return contents.stream().map(content -> content.content)
                .map(this::of)
                .map(FluidRecipeCapability::mapFluid)
                .collect(Collectors.toList());
    }

    public Object createXEIContainer(List<?> contents) {
        // cast is safe if you don't pass the wrong thing.
        // noinspection unchecked
        return new CycleFluidEntryHandler((List<FluidEntryList>) contents);
    }

    @NotNull
    @Override
    public Widget createWidget() {
        TankWidget tank = new TankWidget();
        tank.initTemplate();
        tank.setFillDirection(ProgressTexture.FillDirection.ALWAYS_FULL);
        return tank;
    }

    @NotNull
    @Override
    public Class<? extends Widget> getWidgetClass() {
        return TankWidget.class;
    }

    @Override
    public void applyWidgetInfo(@NotNull Widget widget,
                                int index,
                                boolean isXEI,
                                IO io,
                                GTRecipeTypeUI.@UnknownNullability("null when storage == null") RecipeHolder recipeHolder,
                                @NotNull GTRecipeType recipeType,
                                @UnknownNullability("null when content == null") GTRecipe recipe,
                                @Nullable Content content,
                                @Nullable Object storage, int recipeTier, int chanceTier) {
        if (widget instanceof TankWidget tank) {
            if (storage instanceof CycleFluidEntryHandler cycleHandler) {
                tank.setFluidTank(cycleHandler, index);
            } else if (storage instanceof IFluidHandlerModifiable fluidHandler) {
                tank.setFluidTank(new OverlayingFluidStorage(fluidHandler, index));
            }
            tank.setIngredientIO(io == IO.IN ? IngredientIO.INPUT : IngredientIO.OUTPUT);
            tank.setAllowClickFilled(!isXEI);
            tank.setAllowClickDrained(!isXEI && io.support(IO.IN));
            if (isXEI) tank.setShowAmount(false);
            if (content != null) {
                float chance = (float) recipeType.getChanceFunction()
                        .getBoostedChance(content, recipeTier, chanceTier) / content.maxChance;
                tank.setXEIChance(chance);
                tank.setOnAddedTooltips((w, tooltips) -> {
                    SizedFluidIngredient ingredient = FluidRecipeCapability.CAP.of(content.content);
                    if (!isXEI && ingredient.getFluids().length > 0) {
                        FluidStack stack = ingredient.getFluids()[0];
                        TooltipsHandler.appendFluidTooltips(stack, tooltips::add,
                                TooltipFlag.NORMAL, Item.TooltipContext.of(GTRegistries.builtinRegistry()));
                    }

                    GTRecipeWidget.setConsumedChance(content,
                            recipe.getChanceLogicForCapability(this, io, isTickSlot(index, io, recipe)),
                            tooltips, recipeTier, chanceTier, recipeType.getChanceFunction());
                    if (isTickSlot(index, io, recipe)) {
                        tooltips.add(Component.translatable("gtceu.gui.content.per_tick"));
                    }
                });
                if (io == IO.IN && (content.chance == 0)) {
                    tank.setIngredientIO(IngredientIO.CATALYST);
                }
            }
        }
    }

    // Maps ingredients to an FluidEntryList for XEI: either an FluidTagList or a FluidStackList
    private static FluidEntryList mapFluid(final SizedFluidIngredient ingredient) {
        if (ingredient.ingredient() instanceof IntersectionFluidIngredient intersection) {
            return mapIntersection(intersection, ingredient.amount());
        } else if (ingredient.ingredient() instanceof TagFluidIngredient tag) {
            return FluidTagList.of(tag.tag(), ingredient.amount(), null);
        }
        return FluidStackList.of(Arrays.asList(ingredient.getFluids()));
    }

    // Map intersection ingredients to the items inside, as recipe viewers don't support them.
    private static FluidEntryList mapIntersection(final IntersectionFluidIngredient intersection, int amount) {
        List<FluidIngredient> children = intersection.children();
        if (children.isEmpty()) return new FluidStackList();

        var childList = mapFluid(new SizedFluidIngredient(children.getFirst(), amount));
        FluidStackList stackList = new FluidStackList();
        for (var stack : childList.getStacks()) {
            if (children.stream().skip(1).allMatch(child -> child.test(stack))) {
                if (amount > 0) stackList.add(stack.copyWithAmount(amount));
                else stackList.add(stack.copy());
            }
        }
        return stackList;
    }

    public interface ICustomParallel {

        /**
         * Custom impl of the parallel limiter used by ParallelLogic to limit by outputs
         * 
         * @param recipe     Recipe
         * @param multiplier Initial multiplier
         * @return Limited multiplier
         */
        int limitParallel(GTRecipe recipe, int multiplier);
    }
}
