package com.gregtechceu.gtceu.integration.kjs.recipe.components;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.content.Content;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentType;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.apache.commons.lang3.mutable.MutableObject;

public record ContentJS<T>(RecipeComponentType<?> type, RecipeComponentType<T> baseComponent, RecipeCapability<?> capability)
        implements RecipeComponent<Content> {

    public static <T> ContentJS<T> create(RecipeComponentType<T> baseComponent, RecipeCapability<?> capability) {
        MutableObject<ContentJS<T>> value = new MutableObject<>();
        RecipeComponentType.<Content>unit(GTCEu.id(capability.name + "_content"),
                t -> {
                    var content = new ContentJS<>(t, baseComponent, capability);
                    value.setValue(content);
                    return content;
                });
        return value.getValue();
    }

    @Override
    public Codec<Content> codec() {
        return Content.codec(capability);
    }

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.of(Content.class);
    }

    @Override
    public Content replace(Context cx, KubeRecipe recipe, Content original, ReplacementMatchInfo match, Object with) {
        return new Content(
                baseComponent.instance().replace(cx, recipe,
                        baseComponent.instance().wrap(cx, recipe, original.content),match, with),
                original.chance, original.maxChance, original.tierChanceBoost);
    }

    @Override
    public String toString() {
        return "content[" + baseComponent + "]";
    }
}
