package com.quantumgarbage.gtmogs.data.command;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import com.mojang.brigadier.CommandDispatcher;
import com.quantumgarbage.gtmogs.common.network.packets.SCPacketShareProspection;
import com.quantumgarbage.gtmogs.integration.map.ClientCacheManager;

import java.util.List;
import java.util.UUID;

import static net.minecraft.commands.Commands.*;

public class GTClientCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(literal("gtmogs")
                .then(literal("share_prospection_data")
                        .then(argument("player", EntityArgument.player())
                                .executes(ctx -> {
                                    Player player = EntityArgument.getPlayer(ctx, "player");
                                    Thread sendThread = new Thread(new ProspectingShareTask(
                                            ctx.getSource().getPlayerOrException().getUUID(), player.getUUID()));
                                    sendThread.start();
                                    return 1;
                                }))));
    }

    private static class ProspectingShareTask implements Runnable {

        private final List<ClientCacheManager.ProspectionInfo> prospectionData;
        private final UUID sender;
        private final UUID reciever;

        public ProspectingShareTask(UUID sender, UUID reciever) {
            prospectionData = ClientCacheManager.getProspectionShareData();
            this.sender = sender;
            this.reciever = reciever;
        }

        @Override
        public void run() {
            boolean first = true;
            for (ClientCacheManager.ProspectionInfo info : prospectionData) {
                PacketDistributor.sendToServer(new SCPacketShareProspection(sender, reciever, info.cacheName, info.key,
                        info.isDimCache, info.dim, info.data, first));
                first = false;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
