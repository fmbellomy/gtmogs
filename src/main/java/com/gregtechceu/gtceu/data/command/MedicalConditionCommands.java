package com.gregtechceu.gtceu.data.command;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IMedicalConditionTracker;
import com.gregtechceu.gtceu.api.medicalcondition.MedicalCondition;
import com.gregtechceu.gtceu.common.command.argument.MedicalConditionArgument;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

import static net.minecraft.commands.Commands.*;

public class MedicalConditionCommands {

    private static final SimpleCommandExceptionType ERROR_CLEAR_EVERYTHING_FAILED = new SimpleCommandExceptionType(
            Component.translatable("commands.effect.clear.everything.failed"));
    private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(
            Component.translatable("commands.effect.give.failed"));

    // spotless:off
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(
                literal("medical_condition")
                        .then(literal("query")
                                .executes(ctx -> {
                                    return queryMedicalConditions(ctx.getSource().getPlayerOrException());
                                })
                                .then(argument("target", EntityArgument.player())
                                        .requires(source -> source.hasPermission(LEVEL_GAMEMASTERS))
                                        .executes(context -> {
                                            return queryMedicalConditions(EntityArgument.getPlayer(context, "target"));
                                        })))
                        .then(literal("clear")
                                .requires(ctx -> ctx.hasPermission(LEVEL_ADMINS))
                                .executes(ctx -> {
                                    return clearMedicalConditions(
                                            Collections.singleton(ctx.getSource().getPlayerOrException()), null);
                                })
                                .then(argument("targets", EntityArgument.players())
                                        .executes(ctx -> {
                                            return clearMedicalConditions(EntityArgument.getPlayers(ctx, "targets"),
                                                    null);
                                        })
                                        .then(argument("condition", MedicalConditionArgument.medicalCondition())
                                                .executes(ctx -> {
                                                    Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx,
                                                            "targets");
                                                    MedicalCondition condition = MedicalConditionArgument
                                                            .getCondition(ctx, "condition");
                                                    return clearMedicalConditions(targets, condition);
                                                }))))
                        .then(literal("apply")
                                .requires(ctx -> ctx.hasPermission(LEVEL_GAMEMASTERS))
                                .then(argument("targets", EntityArgument.players())
                                        .then(argument("condition", MedicalConditionArgument.medicalCondition())
                                                .executes(ctx -> {
                                                    MedicalCondition condition = MedicalConditionArgument
                                                            .getCondition(ctx, "condition");
                                                    Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx,
                                                            "targets");
                                                    return applyMedicalConditions(players, condition, 1);
                                                })
                                                .then(argument("progression_multiplier", FloatArgumentType.floatArg(0))
                                                        .executes(ctx -> {
                                                            MedicalCondition condition = MedicalConditionArgument
                                                                    .getCondition(ctx, "condition");
                                                            Collection<ServerPlayer> players = EntityArgument
                                                                    .getPlayers(ctx, "targets");
                                                            float strength = FloatArgumentType.getFloat(ctx,
                                                                    "progression_multiplier");
                                                            return applyMedicalConditions(players, condition, strength);
                                                        }))))));
    }
    // spotless:off

    private static int queryMedicalConditions(ServerPlayer target) {
        IMedicalConditionTracker tracker = GTCapabilityHelper.getMedicalConditionTracker(target);
        int count = tracker.getMedicalConditions().size();
        if (count == 0) {
            target.sendSystemMessage(
                    Component.translatable("command.gtceu.medical_condition.get.empty", target.getName()));
        } else {
            target.sendSystemMessage(
                    Component.translatable("command.gtceu.medical_condition.get", target.getName()));
        }
        for (var entry : tracker.getMedicalConditions().object2FloatEntrySet()) {
            String langKey = "command.gtceu.medical_condition.get.element";
            if (entry.getKey().maxProgression * 2 <= entry.getFloatValue() &&
                    entry.getKey().canBePermanent) {
                langKey = "command.gtceu.medical_condition.get.element.permanent";
            }
            float time = entry.getFloatValue();
            target.sendSystemMessage(
                    Component.translatable(langKey,
                            Component.translatable("gtceu.medical_condition." + entry.getKey().name),
                            (int) (time / 60), (int) (time % 60)));
        }
        return count;
    }

    private static int clearMedicalConditions(Collection<ServerPlayer> targets,
                                              @Nullable MedicalCondition condition) throws CommandSyntaxException {
        int count = 0;
        for (ServerPlayer target : targets) {
            IMedicalConditionTracker tracker = GTCapabilityHelper.getMedicalConditionTracker(target);
            if (condition == null) {
                count += tracker.getMedicalConditions().keySet().size();
                for (MedicalCondition medicalCondition : tracker.getMedicalConditions().keySet()) {
                    tracker.removeMedicalCondition(medicalCondition);
                }
            } else {
                count++;
                tracker.removeMedicalCondition(condition);
            }
        }
        if (count == 0) {
            throw ERROR_CLEAR_EVERYTHING_FAILED.create();
        }
        return count;
    }

    private static int applyMedicalConditions(Collection<ServerPlayer> targets, MedicalCondition condition,
                                              float strength) throws CommandSyntaxException {
        int success = 0;
        for (ServerPlayer player : targets) {
            IMedicalConditionTracker tracker = GTCapabilityHelper.getMedicalConditionTracker(player);
            tracker.progressCondition(condition, strength);
            success++;
        }
        if (success == 0) {
            throw ERROR_GIVE_FAILED.create();
        }
        return success;
    }
}
