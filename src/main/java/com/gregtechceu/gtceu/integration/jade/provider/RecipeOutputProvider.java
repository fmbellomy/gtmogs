package com.gregtechceu.gtceu.integration.jade.provider;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.ingredient.IntProviderFluidIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.IntProviderIngredient;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredientExtensions;
import com.gregtechceu.gtceu.integration.jade.GTElementHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.util.FluidTextHelper;

import java.util.ArrayList;
import java.util.List;

@ExtensionMethod(SizedIngredientExtensions.class)
public class RecipeOutputProvider extends CapabilityBlockProvider<RecipeLogic> {

    public RecipeOutputProvider() {
        super(GTCEu.id("recipe_output_info"));
    }

    @Override
    protected @Nullable RecipeLogic getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        return GTCapabilityHelper.getRecipeLogic(level, pos, side);
    }

    @Override
    protected void write(CompoundTag data, RecipeLogic recipeLogic) {
        if (recipeLogic.isWorking()) {
            data.putBoolean("Working", recipeLogic.isWorking());
            var recipe = recipeLogic.getLastRecipe();
            if (recipe != null) {
                int recipeTier = RecipeHelper.getPreOCRecipeEuTier(recipe);
                int chanceTier = recipeTier + recipe.ocLevel;
                var function = recipe.getType().getChanceFunction();
                var itemContents = recipe.getOutputContents(ItemRecipeCapability.CAP);
                var fluidContents = recipe.getOutputContents(FluidRecipeCapability.CAP);

                var ops = recipeLogic.getMachine().getLevel()
                        .registryAccess().createSerializationContext(NbtOps.INSTANCE);

                ListTag itemTags = new ListTag();
                for (var item : itemContents) {
                    var itemTag = new CompoundTag();
                    if (item.content instanceof IntProviderIngredient provider) {
                        // don't bother rolling output
                        itemTag = IntProviderIngredient.CODEC.codec().encodeStart(ops, provider)
                                .map(tag -> (CompoundTag) tag)
                                .getOrThrow();
                    } else {
                        var stacks = ItemRecipeCapability.CAP.of(item.content).getItems();
                        if (stacks.length == 0 || stacks[0].isEmpty()) continue;
                        var stack = stacks[0];

                        itemTag = ItemStack.CODEC.encodeStart(ops, stack)
                                .map(tag -> (CompoundTag) tag)
                                .getOrThrow();
                        if (item.chance < item.maxChance) {
                            int count = stack.getCount();
                            double countD = (double) count * recipe.parallels *
                                    function.getBoostedChance(item, recipeTier, chanceTier) / item.maxChance;
                            count = countD < 1 ? 1 : (int) Math.round(countD);
                            itemTag.putInt("Count", count);
                        }
                    }
                    itemTags.add(itemTag);
                }

                if (!itemTags.isEmpty()) {
                    data.put("OutputItems", itemTags);
                }

                ListTag fluidTags = new ListTag();
                for (var fluid : fluidContents) {
                    FluidIngredient ingredient = FluidRecipeCapability.CAP.of(fluid.getContent()).ingredient();
                    var fluidTag = new CompoundTag();
                    if (ingredient instanceof IntProviderFluidIngredient provider) {
                        // don't bother rolling output for nothing
                        fluidTag = IntProviderFluidIngredient.CODEC.codec().encodeStart(ops, provider)
                                .map(tag -> (CompoundTag) tag)
                                .getOrThrow();
                    } else {
                        FluidStack[] stacks = FluidRecipeCapability.CAP.of(fluid.content).getFluids();
                        if (stacks.length == 0) continue;
                        if (stacks[0].isEmpty()) continue;
                        var stack = stacks[0];

                        fluidTag = FluidStack.CODEC.encodeStart(ops, stack)
                                .map(tag -> (CompoundTag) tag)
                                .getOrThrow();
                        if (fluid.chance < fluid.maxChance) {
                            int amount = stack.getAmount();
                            double amountD = (double) amount * recipe.parallels *
                                    function.getBoostedChance(fluid, recipeTier, chanceTier) / fluid.maxChance;
                            amount = amountD < 1 ? 1 : (int) Math.round(amountD);
                            fluidTag.putInt("Amount", amount);
                        }
                    }
                    fluidTags.add(fluidTag);
                }

                if (!fluidTags.isEmpty()) {
                    data.put("OutputFluids", fluidTags);
                }
            }
        }
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
                              BlockEntity blockEntity, IPluginConfig config) {
        if (capData.getBoolean("Working")) {
            var ops = block.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE);

            List<SizedIngredient> outputItems = new ArrayList<>();
            if (capData.contains("OutputItems", Tag.TAG_LIST)) {
                ListTag itemTags = capData.getList("OutputItems", Tag.TAG_COMPOUND);
                if (!itemTags.isEmpty()) {
                    for (Tag tag : itemTags) {
                        if (tag instanceof CompoundTag tCompoundTag) {
                            if (tCompoundTag.contains("count_provider")) {
                                var ingredient = IntProviderIngredient.CODEC.codec()
                                        .parse(ops, tCompoundTag).getOrThrow();
                                outputItems.add(new SizedIngredient(ingredient.toVanilla(), 1));
                            } else {
                                ItemStack stack = ItemStack.CODEC.parse(ops, tag).getOrThrow();
                                if (!stack.isEmpty()) {
                                    outputItems.add(RecipeHelper.makeSizedIngredient(stack));
                                }
                            }
                        }
                    }
                }
            }
            List<SizedFluidIngredient> outputFluids = new ArrayList<>();
            if (capData.contains("OutputFluids", Tag.TAG_LIST)) {
                ListTag fluidTags = capData.getList("OutputFluids", Tag.TAG_COMPOUND);
                for (Tag tag : fluidTags) {
                    if (tag instanceof CompoundTag tCompoundTag) {
                        if (tCompoundTag.contains("count_provider")) {
                            var ingredient = IntProviderFluidIngredient.CODEC.codec()
                                    .parse(ops, tCompoundTag).getOrThrow();
                            outputFluids.add(new SizedFluidIngredient(ingredient, 1));
                        } else {
                            FluidStack stack = FluidStack.CODEC.parse(ops, tag).getOrThrow();
                            if (!stack.isEmpty()) {
                                outputFluids.add(RecipeHelper.makeSizedFluidIngredient(stack));
                            }
                        }
                    }
                }
            }
            if (!outputItems.isEmpty() || !outputFluids.isEmpty()) {
                tooltip.add(Component.translatable("gtceu.top.recipe_output"));
            }
            addItemTooltips(tooltip, outputItems);
            addFluidTooltips(tooltip, outputFluids);
        }
    }

    private void addItemTooltips(ITooltip iTooltip, List<SizedIngredient> outputItems) {
        IElementHelper helper = IElementHelper.get();
        for (SizedIngredient itemOutput : outputItems) {
            if (itemOutput != null && !itemOutput.ingredient().hasNoItems()) {
                ItemStack item = itemOutput.getItems()[0];
                int count = item.getCount();
                item.setCount(1);

                iTooltip.add(helper.smallItem(item));
                MutableComponent text = CommonComponents.space();
                if (itemOutput.getContainedCustom() instanceof IntProviderIngredient provider) {
                    text.append(String.valueOf(provider.getCountProvider().getMinValue()))
                            .append("-")
                            .append(String.valueOf(provider.getCountProvider().getMaxValue()));
                } else {
                    text.append(String.valueOf(count));
                }
                text.append("× ")
                        .append(getItemName(item))
                        .withStyle(ChatFormatting.WHITE);
                iTooltip.append(text);
            }
        }
    }

    private void addFluidTooltips(ITooltip iTooltip, List<SizedFluidIngredient> outputFluids) {
        for (SizedFluidIngredient fluidOutput : outputFluids) {
            if (fluidOutput != null && !fluidOutput.ingredient().hasNoFluids()) {
                FluidStack fluid = fluidOutput.getFluids()[0];

                iTooltip.add(GTElementHelper.smallFluid(getFluid(fluid)));
                MutableComponent text = CommonComponents.space();
                if (fluidOutput.ingredient() instanceof IntProviderFluidIngredient provider) {
                    text.append(FluidTextHelper.getUnicodeMillibuckets(
                            provider.getCountProvider().getMinValue(), true))
                            .append("-")
                            .append(FluidTextHelper.getUnicodeMillibuckets(
                                    provider.getCountProvider().getMaxValue(), true));
                } else {
                    text.append(FluidTextHelper.getUnicodeMillibuckets(fluidOutput.amount(), true));
                }
                text.append(CommonComponents.space())
                        .append(getFluidName(fluid))
                        .withStyle(ChatFormatting.WHITE);
                iTooltip.append(text);
            }
        }
    }

    private Component getItemName(ItemStack stack) {
        return ComponentUtils.wrapInSquareBrackets(stack.getHoverName()).withStyle(ChatFormatting.WHITE);
    }

    private Component getFluidName(FluidStack stack) {
        return ComponentUtils.wrapInSquareBrackets(stack.getHoverName()).withStyle(ChatFormatting.WHITE);
    }

    private JadeFluidObject getFluid(FluidStack stack) {
        return JadeFluidObject.of(stack.getFluid(), stack.getAmount());
    }
}
