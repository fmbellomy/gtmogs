package com.gregtechceu.gtceu.common.recipe.condition;

import com.gregtechceu.gtceu.api.worldgen.DimensionMarker;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.data.recipe.GTRecipeConditions;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.jei.IngredientIO;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class DimensionCondition extends RecipeCondition {

    public static final MapCodec<DimensionCondition> CODEC = RecordCodecBuilder
            .mapCodec(instance -> RecipeCondition.isReverse(instance)
                    .and(ResourceLocation.CODEC.fieldOf("dimension").forGetter(val -> val.dimension))
                    .apply(instance, DimensionCondition::new));

    public final static DimensionCondition INSTANCE = new DimensionCondition();
    private ResourceLocation dimension = ResourceLocation.withDefaultNamespace("dummy");

    public DimensionCondition(ResourceLocation dimension) {
        this.dimension = dimension;
    }

    public DimensionCondition(boolean isReverse, ResourceLocation dimension) {
        super(isReverse);
        this.dimension = dimension;
    }

    @Override
    public RecipeConditionType<?> getType() {
        return GTRecipeConditions.DIMENSION;
    }

    @Override
    public boolean isOr() {
        return true;
    }

    @Override
    public Component getTooltips() {
        return Component.translatable("recipe.condition.dimension.tooltip", dimension);
    }

    public SlotWidget setupDimensionMarkers(int xOffset, int yOffset) {
        DimensionMarker dimMarker = GTRegistries.DIMENSION_MARKERS.getOrDefault(this.dimension,
                new DimensionMarker(DimensionMarker.MAX_TIER, () -> Blocks.BARRIER, this.dimension.toString()));
        ItemStack icon = dimMarker.getIcon();
        CustomItemStackHandler handler = new CustomItemStackHandler(1);
        SlotWidget dimSlot = new SlotWidget(handler, 0, xOffset, yOffset, false, false)
                .setIngredientIO(IngredientIO.INPUT);
        handler.setStackInSlot(0, icon);
        if (ConfigHolder.INSTANCE.compat.showDimensionTier) {
            dimSlot.setOverlay(
                    new TextTexture("T" + (dimMarker.tier >= DimensionMarker.MAX_TIER ? "?" : dimMarker.tier))
                            .scale(0.75f).transform(-3.0f, 5.0f));
        }
        return dimSlot;
    }

    public ResourceLocation getDimension() {
        return dimension;
    }

    @Override
    public boolean testCondition(@NotNull GTRecipe recipe, @NotNull RecipeLogic recipeLogic) {
        Level level = recipeLogic.machine.self().getLevel();
        return level != null && dimension.equals(level.dimension().location());
    }

    @Override
    public RecipeCondition createTemplate() {
        return new DimensionCondition();
    }

    @NotNull
    @Override
    public JsonObject serialize() {
        JsonObject config = super.serialize();
        config.addProperty("dimension", dimension.toString());
        return config;
    }

    @Override
    public RecipeCondition deserialize(@NotNull JsonObject config) {
        super.deserialize(config);
        dimension = ResourceLocation.parse(
                GsonHelper.getAsString(config, "dimension", "dummy"));
        return this;
    }

    @Override
    public RecipeCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        super.fromNetwork(buf);
        dimension = buf.readResourceLocation();
        return this;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        super.toNetwork(buf);
        buf.writeResourceLocation(dimension);
    }
}
