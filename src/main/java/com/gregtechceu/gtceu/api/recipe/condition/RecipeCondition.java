package com.gregtechceu.gtceu.api.recipe.condition;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;

import com.google.gson.JsonObject;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Accessors(chain = true)
public abstract class RecipeCondition<T extends RecipeCondition<T>> {

    public static final Codec<RecipeCondition<?>> CODEC = GTRegistries.RECIPE_CONDITIONS.byNameCodec()
            .dispatch(RecipeCondition::getType, RecipeConditionType::getCodec);
    // spotless:off
    public static <RC extends RecipeCondition<?>> Products.P1<RecordCodecBuilder.Mu<RC>, Boolean> isReverse(RecordCodecBuilder.Instance<RC> instance) {
        return instance.group(Codec.BOOL.optionalFieldOf("reverse", false).forGetter(val -> val.isReverse));
    }
    // spotless:on
    @Getter
    @Setter
    protected boolean isReverse;

    public RecipeCondition() {
        this(false);
    }

    public RecipeCondition(boolean isReverse) {
        this.isReverse = isReverse;
    }

    public abstract RecipeConditionType<T> getType();

    public String getTranslationKey() {
        return "gtceu.recipe.condition." + getType();
    }

    public IGuiTexture getInValidTexture() {
        return new ResourceTexture("gtceu:textures/gui/condition/" + getType() + ".png").getSubTexture(0, 0, 1, 0.5f);
    }

    public IGuiTexture getValidTexture() {
        return new ResourceTexture("gtceu:textures/gui/condition/" + getType() + ".png").getSubTexture(0, 0.5f, 1,
                0.5f);
    }

    public boolean isOr() {
        return false;
    }

    public abstract Component getTooltips();

    public boolean check(@NotNull GTRecipe recipe, @NotNull RecipeLogic recipeLogic) {
        boolean test = testCondition(recipe, recipeLogic);
        return test != isReverse;
    }

    protected abstract boolean testCondition(@NotNull GTRecipe recipe, @NotNull RecipeLogic recipeLogic);

    public abstract RecipeCondition<T> createTemplate();

    @NotNull
    public JsonObject serialize() {
        JsonObject jsonObject = new JsonObject();
        if (isReverse) {
            jsonObject.addProperty("reverse", true);
        }
        return jsonObject;
    }

    public void toNetwork(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(isReverse);
    }

    public RecipeCondition<T> fromNetwork(RegistryFriendlyByteBuf buf) {
        isReverse = buf.readBoolean();
        return this;
    }
}
