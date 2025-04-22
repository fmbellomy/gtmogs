package com.gregtechceu.gtceu.common.recipe.condition;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.gregtechceu.gtceu.data.recipe.GTRecipeConditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class EUToStartCondition extends RecipeCondition<EUToStartCondition> {

    public static final MapCodec<EUToStartCondition> CODEC = RecordCodecBuilder
            .mapCodec(instance -> RecipeCondition.isReverse(instance)
                    .and(Codec.LONG.fieldOf("eu_to_start").forGetter(val -> val.euToStart))
                    .apply(instance, EUToStartCondition::new));
    public static final EUToStartCondition INSTANCE = new EUToStartCondition();

    private long euToStart;

    public EUToStartCondition(long euToStart) {
        this.euToStart = euToStart;
    }

    public EUToStartCondition(boolean isReverse, long euToStart) {
        super(isReverse);
        this.euToStart = euToStart;
    }

    @Override
    public RecipeConditionType<EUToStartCondition> getType() {
        return GTRecipeConditions.EU_TO_START;
    }

    @Override
    public Component getTooltips() {
        return Component.translatable("recipe.condition.eu_to_start.tooltip");
    }

    @Override
    public boolean testCondition(@NotNull GTRecipe recipe, @NotNull RecipeLogic recipeLogic) {
        return recipeLogic.getMachine().getTraits().stream().filter(IEnergyContainer.class::isInstance)
                .anyMatch(energyContainer -> ((IEnergyContainer) energyContainer).getEnergyCapacity() > euToStart);
    }

    @Override
    public RecipeCondition createTemplate() {
        return new EUToStartCondition();
    }

    @NotNull
    @Override
    public JsonObject serialize() {
        JsonObject config = super.serialize();
        config.addProperty("euToStart", euToStart);
        return config;
    }

    @Override
    public RecipeCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        super.fromNetwork(buf);
        euToStart = buf.readLong();
        return this;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        super.toNetwork(buf);
        buf.writeLong(euToStart);
    }
}
