package com.gregtechceu.gtceu.common.network;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.common.network.packets.*;
import com.gregtechceu.gtceu.common.network.packets.prospecting.SPacketProspectOre;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class GTNetwork {

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(GTCEuAPI.NETWORK_VERSION);
        // spotless:off
        registrar.playToServer(CPacketKeysPressed.TYPE, CPacketKeysPressed.CODEC, CPacketKeysPressed::execute);
        registrar.playToClient(SPacketProspectOre.TYPE, SPacketProspectOre.CODEC, SPacketProspectOre::execute);

        registrar.playToClient(SPacketSendWorldID.TYPE, SPacketSendWorldID.CODEC, SPacketSendWorldID::execute);
        registrar.playBidirectional(SCPacketShareProspection.TYPE, SCPacketShareProspection.CODEC, SCPacketShareProspection::execute);
        // spotless:on
    }
}
