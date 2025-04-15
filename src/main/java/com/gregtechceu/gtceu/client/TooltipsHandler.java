package com.gregtechceu.gtceu.client;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.material.material.properties.HazardProperty;
import com.gregtechceu.gtceu.api.material.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.fluid.FluidConstants;
import com.gregtechceu.gtceu.api.fluid.FluidState;
import com.gregtechceu.gtceu.api.fluid.GTFluid;
import com.gregtechceu.gtceu.data.fluid.GTFluids;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.gregtechceu.gtceu.common.fluid.potion.PotionFluidHelper;
import com.gregtechceu.gtceu.data.lang.LangHandler;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.*;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import java.util.List;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class TooltipsHandler {

    private static final String ITEM_PREFIX = "item." + GTCEu.MOD_ID;
    private static final String BLOCK_PREFIX = "block." + GTCEu.MOD_ID;

    public static void appendTooltips(ItemStack stack, TooltipFlag flag, List<Component> tooltips, Item.TooltipContext context) {
        // Formula
        var materialEntry = ChemicalHelper.getMaterialEntry(stack.getItem());
        if (!materialEntry.isEmpty()) {
            var formula = materialEntry.material().getChemicalFormula();
            if (formula != null && !formula.isEmpty()) {
                tooltips.add(1, Component.literal(formula).withStyle(ChatFormatting.YELLOW));
            }
        }
        if (stack.getItem() instanceof BucketItem bucket) {
            var fluid = bucket.content;
            if (!(fluid instanceof EmptyFluid)) {
                appendFluidTooltips(new FluidStack(fluid, FluidType.BUCKET_VOLUME), tooltips::add, flag, context);
            }
        } else if (stack.getItem() instanceof MilkBucketItem) {
            appendFluidTooltips(GTMaterials.Milk.getFluid(FluidType.BUCKET_VOLUME), tooltips::add, flag, context);
        }

        // Block/Item custom tooltips
        String translationKey = stack.getDescriptionId();
        if (translationKey.startsWith(ITEM_PREFIX) || translationKey.startsWith(BLOCK_PREFIX)) {
            String tooltipKey = translationKey + ".tooltip";
            if (I18n.exists(tooltipKey)) {
                tooltips.add(1, Component.translatable(tooltipKey));
            } else {
                List<MutableComponent> multiLang = LangHandler.getMultiLang(tooltipKey);
                if (multiLang != null && !multiLang.isEmpty()) {
                    tooltips.addAll(1, multiLang);
                }
            }
        }

        Material material = HazardProperty.getValidHazardMaterial(stack);
        if (material.isNull()) {
            return;
        }
        GTUtil.appendHazardTooltips(material, tooltips);
    }

    public static void appendFluidTooltips(FluidStack fluidStack, Consumer<Component> tooltips, TooltipFlag flag, Item.TooltipContext context) {
        Fluid fluid = fluidStack.getFluid();
        int amount = fluidStack.getAmount();
        FluidType fluidType = fluid.getFluidType();

        if (fluidType == GTFluids.POTION.getType()) {
            if (fluidStack.is(FluidTags.WATER)) {
                return;
            }
            PotionFluidHelper.addPotionTooltip(fluidStack, tooltips, context);
            return;
        }

        var material = ChemicalHelper.getMaterial(fluid);
        if (!material.isNull()) {
            var formula = material.getChemicalFormula();
            if (formula != null && !formula.isEmpty()) {
                tooltips.accept(Component.literal(formula).withStyle(ChatFormatting.YELLOW));
            }

            if (material.hasProperty(PropertyKey.INGOT)) {
                if (GTUtil.isShiftDown() && amount >= GTValues.L) {
                    long ingots = amount / GTValues.L;
                    long remainder = amount % GTValues.L;
                    String fluidAmount = String.format(" %,d mB = %,d * %d mB", amount, ingots, GTValues.L);
                    if (remainder != 0) {
                        fluidAmount += String.format(" + %d mB", remainder);
                    }
                    tooltips.accept(Component.translatable("gtceu.gui.fluid_amount").withStyle(ChatFormatting.GRAY)
                            .append(Component.literal(fluidAmount)));
                }
            }
        }

        if (fluid instanceof GTFluid attributedFluid) {
            FluidState state = attributedFluid.getState();
            switch (state) {
                case LIQUID -> tooltips.accept(Component.translatable("gtceu.fluid.state_liquid"));
                case GAS -> tooltips.accept(Component.translatable("gtceu.fluid.state_gas"));
                case PLASMA -> tooltips.accept(Component.translatable("gtceu.fluid.state_plasma"));
            }
            attributedFluid.getAttributes().forEach(a -> a.appendFluidTooltips(tooltips));
        } else {
            String key = "gtceu.fluid.state_" + (fluidType.isLighterThanAir() ? "gas" : "liquid");
            tooltips.accept(Component.translatable(key));
        }

        tooltips.accept(Component.translatable("gtceu.fluid.temperature", fluidType.getTemperature()));
        if (fluidType.getTemperature() < FluidConstants.CRYOGENIC_FLUID_THRESHOLD) {
            tooltips.accept(Component.translatable("gtceu.fluid.temperature.cryogenic"));
        }
    }
}
