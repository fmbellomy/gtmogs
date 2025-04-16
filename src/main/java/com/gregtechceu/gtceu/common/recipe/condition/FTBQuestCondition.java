package com.gregtechceu.gtceu.common.recipe.condition;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.gregtechceu.gtceu.data.recipe.GTRecipeConditions;
import com.gregtechceu.gtceu.common.machine.owner.FTBOwner;
import com.gregtechceu.gtceu.common.machine.owner.MachineOwner;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbquests.api.FTBQuestsAPI;
import dev.ftb.mods.ftbquests.quest.BaseQuestFile;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class FTBQuestCondition extends RecipeCondition {

    private static final Long2ObjectMap<QuestObject> QUEST_CACHE = new Long2ObjectOpenHashMap<>();
    public static final MapCodec<FTBQuestCondition> CODEC = RecordCodecBuilder
            .mapCodec(instance -> RecipeCondition.isReverse(instance)
                    .and(Codec.LONG.fieldOf("questId").forGetter(val -> val.parsedQuestId))
                    .apply(instance, FTBQuestCondition::new));

    public final static FTBQuestCondition INSTANCE = new FTBQuestCondition();

    private long parsedQuestId;

    public FTBQuestCondition(long questId) {
        this.parsedQuestId = questId;
    };

    public FTBQuestCondition(boolean isReverse, long questId) {
        super(isReverse);
        this.parsedQuestId = questId;
    }

    private QuestObject getQuest() {
        return QUEST_CACHE.computeIfAbsent(parsedQuestId, id -> FTBQuestsAPI.api().getQuestFile(false).get(id));
    }

    @Override
    public RecipeConditionType<?> getType() {
        return GTRecipeConditions.FTB_QUEST;
    }

    @Override
    public Component getTooltips() {
        Component questTitle = getQuest().getTitle();

        if (isReverse) {
            return Component.translatable("recipe.condition.quest.not_completed.tooltip", questTitle);
        } else {
            return Component.translatable("recipe.condition.quest.completed.tooltip", questTitle);
        }
    }

    @Override
    public boolean testCondition(@NotNull GTRecipe recipe, @NotNull RecipeLogic recipeLogic) {
        MachineOwner owner = recipeLogic.machine.self().getOwner();
        if (!(owner instanceof FTBOwner ftbOwner)) return false;
        if (ftbOwner.getTeam() == null) return false;
        BaseQuestFile questFile = FTBQuestsAPI.api().getQuestFile(false);

        return questFile.getOrCreateTeamData(ftbOwner.getTeam()).isCompleted(getQuest());
    }

    @Override
    public RecipeCondition createTemplate() {
        return new FTBQuestCondition();
    }

    @Override
    public @NotNull JsonObject serialize() {
        var obj = super.serialize();
        obj.addProperty("questId", parsedQuestId);
        return obj;
    }

    @Override
    public RecipeCondition fromNetwork(RegistryFriendlyByteBuf buf) {
        super.fromNetwork(buf);
        parsedQuestId = buf.readLong();
        return this;
    }

    @Override
    public void toNetwork(RegistryFriendlyByteBuf buf) {
        super.toNetwork(buf);
        buf.writeLong(parsedQuestId);
    }
}
