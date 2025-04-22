package com.gregtechceu.gtceu.common.command.argument;

import com.gregtechceu.gtceu.api.material.material.Material;

import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.HolderLookup;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class MaterialArgument implements ArgumentType<Material> {

    private static final Collection<String> EXAMPLES = Arrays.asList("aluminium", "gtceu:steel", "foo:barinite");
    private final HolderLookup<Material> materials;

    public MaterialArgument(CommandBuildContext buildContext) {
        this.materials = buildContext.lookupOrThrow(GTRegistries.MATERIAL_REGISTRY);
    }

    public static MaterialArgument material(CommandBuildContext buildContext) {
        return new MaterialArgument(buildContext);
    }

    @Override
    public Material parse(StringReader reader) throws CommandSyntaxException {
        return MaterialParser.parseForMaterial(materials, reader);
    }

    public static <S> Material getMaterial(CommandContext<S> context, String name) {
        return context.getArgument(name, Material.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return MaterialParser.fillSuggestions(materials, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
