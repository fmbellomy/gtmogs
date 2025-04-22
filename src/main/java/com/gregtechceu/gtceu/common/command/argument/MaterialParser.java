package com.gregtechceu.gtceu.common.command.argument;

import com.gregtechceu.gtceu.api.material.material.Material;

import com.gregtechceu.gtceu.api.registry.GTRegistries;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class MaterialParser {

    private static final DynamicCommandExceptionType ERROR_UNKNOWN_MATERIAL = new DynamicCommandExceptionType(
            id -> Component.translatable("argument.material.id.invalid", id));
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    private final HolderLookup<Material> materials;
    private final StringReader reader;
    private Material result;
    /**
     * Builder to be used when creating a list of suggestions
     */
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

    private MaterialParser(HolderLookup<Material> materials, StringReader reader) {
        this.materials = materials;
        this.reader = reader;
    }

    public static Material parseForMaterial(HolderLookup<Material> registry, StringReader reader)
            throws CommandSyntaxException {
        int i = reader.getCursor();

        try {
            MaterialParser materialParser = new MaterialParser(registry, reader);
            materialParser.parse();
            return materialParser.result;
        } catch (CommandSyntaxException var5) {
            reader.setCursor(i);
            throw var5;
        }
    }

    public static CompletableFuture<Suggestions> fillSuggestions(HolderLookup<Material> lookup,
                                                                 SuggestionsBuilder builder) {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        MaterialParser materialParser = new MaterialParser(lookup, stringReader);

        try {
            materialParser.parse();
        } catch (CommandSyntaxException ignored) {}

        return materialParser.suggestions.apply(builder.createOffset(stringReader.getCursor()));
    }

    private void readMaterial() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        ResourceLocation id = ResourceLocation.read(this.reader);
        this.result = this.materials.get(ResourceKey.create(GTRegistries.MATERIAL_REGISTRY, id)).orElseThrow(() -> {
                    this.reader.setCursor(i);
                    return ERROR_UNKNOWN_MATERIAL.createWithContext(this.reader, id);
                }).value();
    }

    private void parse() throws CommandSyntaxException {
        this.suggestions = this::suggestMaterial;
        this.readMaterial();
    }

    private CompletableFuture<Suggestions> suggestMaterial(SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestResource(
                this.materials.listElementIds().map(ResourceKey::location), builder);
    }
}
