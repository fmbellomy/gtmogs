package com.quantumgarbage.gtmogs.common.network;

import com.quantumgarbage.gtmogs.api.GTCEuAPI;
import com.quantumgarbage.gtmogs.common.network.packets.*;
import com.quantumgarbage.gtmogs.common.network.packets.SCPacketShareProspection;
import com.quantumgarbage.gtmogs.common.network.packets.SPacketSendWorldID;
import com.quantumgarbage.gtmogs.common.network.packets.prospecting.SPacketProspectOre;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class GTNetwork {

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(GTCEuAPI.NETWORK_VERSION);
        // spotless:off
        registrar.playToClient(SPacketProspectOre.TYPE, SPacketProspectOre.CODEC, SPacketProspectOre::execute);

        registrar.playToClient(SPacketSendWorldID.TYPE, SPacketSendWorldID.CODEC, SPacketSendWorldID::execute);
        registrar.playBidirectional(SCPacketShareProspection.TYPE, SCPacketShareProspection.CODEC, SCPacketShareProspection::execute);
        // spotless:on
    }
}
