package com.gregtechceu.gtceu.integration.kjs.recipe.components;

import com.gregtechceu.gtceu.GTCEu;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.component.*;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.HashMap;
import java.util.Map;

public record JavaMapRecipeComponent<K, V>(RecipeComponent<K> key, RecipeComponent<V> value)
        implements RecipeComponent<Map<K, V>> {

    public static final RecipeComponentType<Map<?, ?>> TYPE = RecipeComponentType.dynamic(GTCEu.id("java_map"), (RecipeComponentCodecFactory<JavaMapRecipeComponent<?, ?>>) ctx -> RecordCodecBuilder.mapCodec(instance -> instance.group(
            ctx.codec().fieldOf("key").forGetter(JavaMapRecipeComponent::key),
            ctx.codec().fieldOf("value").forGetter(JavaMapRecipeComponent::value)
    ).apply(instance, JavaMapRecipeComponent::new)));

    @Override
    public RecipeComponentType<?> type() {
        return TYPE;
    }

    @Override
    public Map<K, V> replace(Context cx, KubeRecipe recipe, Map<K, V> original, ReplacementMatchInfo match,
                             Object with) {
        var map = original;

        for (Map.Entry<K, V> entry : original.entrySet()) {
            var r = value.replace(cx, recipe, entry.getValue(), match, with);
            if (r != entry.getValue()) {
                if (map == original) {
                    map = new HashMap<>(original);
                }
                map.put(entry.getKey(), r);
            }
        }

        return map;
    }

    @Override
    public String toString() {
        return "java_map{" + key + ":" + value + "}";
    }

    @Override
    public Codec<Map<K, V>> codec() {
        return Codec.unboundedMap(key.codec(), value.codec());
    }

    @Override
    public TypeInfo typeInfo() {
        return TypeInfo.RAW_MAP.withParams(key.typeInfo(), value.typeInfo());
    }
}
