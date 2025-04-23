package com.gregtechceu.gtceu.data.command;

import com.gregtechceu.gtceu.api.gui.factory.GTUIEditorFactory;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.worldgen.OreVeinDefinition;
import com.gregtechceu.gtceu.api.worldgen.ores.GeneratedVeinMetadata;
import com.gregtechceu.gtceu.api.worldgen.ores.OreGenerator;
import com.gregtechceu.gtceu.api.worldgen.ores.OrePlacer;
import com.gregtechceu.gtceu.core.mixins.ResourceKeyArgumentAccessor;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import static net.minecraft.commands.Commands.*;

public class GTCommands {

    private static final Dynamic2CommandExceptionType VEIN_PLACE_FAILURE = new Dynamic2CommandExceptionType(
            (id, sourcePos) -> Component.translatable("command.gtceu.place_vein.failure", id, sourcePos));
    private static final DynamicCommandExceptionType ERROR_INVALID_VEIN = new DynamicCommandExceptionType(
            id -> Component.translatableEscape("command.gtceu.place_vein.invalid", id));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(
                literal("gtceu")
                        .requires(source -> source.hasPermission(3))
                        .then(literal("ui_editor")
                                .executes(context -> {
                                    GTUIEditorFactory.INSTANCE.openUI(GTUIEditorFactory.INSTANCE,
                                            context.getSource().getPlayerOrException());
                                    return 1;
                                }))
                        // .then(literal("dump_data")
                        // .then(literal("bedrock_fluid_veins")
                        // .executes(context -> dumpDataRegistry(context,
                        // GTRegistries.BEDROCK_FLUID_DEFINITIONS,
                        // BedrockFluidDefinition.CODEC,
                        // BedrockFluidLoader.FOLDER)))
                        // .then(literal("bedrock_ore_veins")
                        // .executes(context -> dumpDataRegistry(context,
                        // GTRegistries.BEDROCK_ORE_DEFINITIONS,
                        // BedrockOreDefinition.CODEC,
                        // BedrockOreLoader.FOLDER)))
                        // .then(literal("ore_veins")
                        // .executes(context -> dumpDataRegistry(context,
                        // GTRegistries.ORE_VEINS,
                        // OreVeinDefinition.DIRECT_CODEC,
                        // GTOreLoader.FOLDER))))
                        .then(literal("place_vein")
                                .then(argument("vein", ResourceKeyArgument.key(GTRegistries.ORE_VEIN_REGISTRY))
                                        .executes(context -> GTCommands.placeVein(context,
                                                BlockPos.containing(context.getSource().getPosition())))
                                        .then(argument("position", BlockPosArgument.blockPos())
                                                .executes(context -> GTCommands.placeVein(context,
                                                        BlockPosArgument.getBlockPos(context, "position")))))));
    }

    // private static <T> int dumpDataRegistry(CommandContext<CommandSourceStack> context,
    // GTRegistry<ResourceLocation, T> registry, Codec<T> codec, String folder) {
    // Path parent = GTCEu.getGameDir().resolve("gtceu/dumped/data");
    // var ops = RegistryOps.create(JsonOps.INSTANCE, context.getSource().registryAccess());
    // int dumpedCount = 0;
    // for (ResourceLocation id : registry.keys()) {
    // T entry = registry.get(id);
    // JsonElement json = codec.encodeStart(ops, entry).getOrThrow();
    // GTDynamicDataPack.writeJson(id, folder, parent, json.toString().getBytes(StandardCharsets.UTF_8));
    // dumpedCount++;
    // }
    // final int result = dumpedCount;
    // context.getSource().sendSuccess(
    // () -> Component.translatable("command.gtceu.dump_data.success", result,
    // registry.getRegistryName().toString(), parent.toString()),
    // true);
    // return result;
    // }

    private static int placeVein(CommandContext<CommandSourceStack> context,
                                 BlockPos sourcePos) throws CommandSyntaxException {
        Holder.Reference<OreVeinDefinition> vein = ResourceKeyArgumentAccessor.callResolveKey(context, "vein",
                GTRegistries.ORE_VEIN_REGISTRY, ERROR_INVALID_VEIN);
        ResourceLocation id = vein.key().location();

        ChunkPos chunkPos = new ChunkPos(sourcePos);
        ServerLevel level = context.getSource().getLevel();

        GeneratedVeinMetadata metadata = new GeneratedVeinMetadata(chunkPos, sourcePos, vein);
        RandomSource random = level.random;

        OrePlacer placer = new OrePlacer();
        OreGenerator generator = placer.getOreGenCache().getOreGenerator();

        try (BulkSectionAccess access = new BulkSectionAccess(level)) {
            var generated = generator.generateOres(new OreGenerator.VeinConfiguration(metadata, random), level,
                    chunkPos);
            if (generated.isEmpty()) {
                throw VEIN_PLACE_FAILURE.create(id.toString(), sourcePos.toString());
            }
            for (ChunkPos pos : generated.get().getGeneratedChunks()) {
                placer.placeVein(pos, random, access, generated.get(), AlwaysTrueTest.INSTANCE);
                level.getChunk(pos.x, pos.z).setUnsaved(true);
            }
            context.getSource().sendSuccess(() -> Component.translatable("command.gtceu.place_vein.success",
                    id.toString(), sourcePos.toString()), true);
        }

        return 1;
    }
}
