package com.gregtechceu.gtceu.common.recipe.condition;

import com.gregtechceu.gtceu.api.medicalcondition.MedicalCondition;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.gregtechceu.gtceu.common.capability.EnvironmentalHazardSavedData;
import com.gregtechceu.gtceu.data.medicalcondition.GTMedicalConditions;
import com.gregtechceu.gtceu.data.recipe.GTRecipeConditions;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import com.google.gson.JsonObject;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentalHazardCondition extends RecipeCondition<EnvironmentalHazardCondition> {

    public static final MapCodec<EnvironmentalHazardCondition> CODEC = RecordCodecBuilder
            .mapCodec(instance -> RecipeCondition.isReverse(instance)
                    .and(
                            MedicalCondition.CODEC.fieldOf("condition").forGetter(val -> val.condition))
                    .apply(instance, EnvironmentalHazardCondition::new));

    @Getter
    private MedicalCondition condition = GTMedicalConditions.CARBON_MONOXIDE_POISONING;

    public EnvironmentalHazardCondition(boolean isReverse, MedicalCondition condition) {
        super(isReverse);
        this.condition = condition;
    }

    @Override
    public RecipeConditionType<EnvironmentalHazardCondition> getType() {
        return GTRecipeConditions.ENVIRONMENTAL_HAZARD;
    }

    @Override
    public Component getTooltips() {
        return isReverse ?
                Component.translatable("gtceu.recipe.environmental_hazard.reverse",
                        Component.translatable("gtceu.medical_condition." + condition.name)) :
                Component.translatable("gtceu.recipe.environmental_hazard",
                        Component.translatable("gtceu.medical_condition." + condition.name));
    }

    @Override
    public boolean testCondition(@NotNull GTRecipe recipe, @NotNull RecipeLogic recipeLogic) {
        if (!ConfigHolder.INSTANCE.gameplay.hazardsEnabled) return true;
        if (!(recipeLogic.getMachine().getLevel() instanceof ServerLevel serverLevel)) {
            return false;
        }
        EnvironmentalHazardSavedData savedData = EnvironmentalHazardSavedData.getOrCreate(serverLevel);
        var zone = savedData.getZoneByContainedPos(recipeLogic.getMachine().getPos());
        return zone != null && zone.strength() > 0;
    }

    @NotNull
    @Override
    public JsonObject serialize() {
        JsonObject value = super.serialize();
        value.addProperty("condition", condition.name);
        return value;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        super.toNetwork(buf);
        buf.writeUtf(this.condition.name);
    }

    @Override
    public EnvironmentalHazardCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        super.fromNetwork(buf);
        this.condition = MedicalCondition.CONDITIONS.get(buf.readUtf());
        return this;
    }

    @Override
    public EnvironmentalHazardCondition createTemplate() {
        return new EnvironmentalHazardCondition();
    }
}
