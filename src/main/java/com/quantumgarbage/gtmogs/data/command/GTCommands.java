package com.quantumgarbage.gtmogs.data.command;


import com.quantumgarbage.gtmogs.GTMOGS;
import com.quantumgarbage.gtmogs.api.registry.GTRegistries;
import com.quantumgarbage.gtmogs.api.worldgen.OreVeinDefinition;
import com.quantumgarbage.gtmogs.api.worldgen.ores.GeneratedVeinMetadata;
import com.quantumgarbage.gtmogs.api.worldgen.ores.OreGenerator;
import com.quantumgarbage.gtmogs.api.worldgen.ores.OrePlacer;
import com.quantumgarbage.gtmogs.core.mixins.ResourceKeyArgumentAccessor;

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
            (id, sourcePos) -> Component.translatable("command.gtmogs.place_vein.failure", id, sourcePos));
    private static final DynamicCommandExceptionType ERROR_INVALID_VEIN = new DynamicCommandExceptionType(
            id -> Component.translatableEscape("command.gtmogs.place_vein.invalid", id));

    // spotless:off
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(literal("gtmogs")
                .then(literal("place_vein")
                        .requires(ctx -> ctx.hasPermission(LEVEL_ADMINS))
                        .then(argument("vein", ResourceKeyArgument.key(GTRegistries.ORE_VEIN_REGISTRY))
                                .executes(context -> {
                                    return GTCommands.placeVein(context, BlockPos.containing(context.getSource().getPosition()));
                                })
                                .then(argument("position", BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            return GTCommands.placeVein(context, BlockPosArgument.getBlockPos(context, "position"));
                                        })))));
    }
    // spotless:on

    private static int placeVein(CommandContext<CommandSourceStack> context,
                                 BlockPos sourcePos) throws CommandSyntaxException {
        for( var registry : GTRegistries.getRegistries()){
            for(var item : registry){
                GTMOGS.LOGGER.debug(item);
            }
        }
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
            context.getSource().sendSuccess(() -> Component.translatable("command.gtmogs.place_vein.success",
                    id.toString(), sourcePos.toString()), true);
        }

        return 1;
    }
}
