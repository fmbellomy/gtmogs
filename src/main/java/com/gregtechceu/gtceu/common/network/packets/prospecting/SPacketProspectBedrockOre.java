package com.gregtechceu.gtceu.common.network.packets.prospecting;

import com.google.common.collect.Table;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.gui.misc.ProspectorMode;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class SPacketProspectBedrockOre extends SPacketProspect<ProspectorMode.OreInfo> {

    public static final ResourceLocation ID = GTCEu.id("prospect_bedrock_ore");
    public static final Type<SPacketProspectBedrockOre> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SPacketProspectBedrockOre> CODEC = StreamCodec
            .ofMember(SPacketProspectBedrockOre::encode, SPacketProspectBedrockOre::decode);

    public SPacketProspectBedrockOre(Table<ResourceKey<Level>, BlockPos, ProspectorMode.OreInfo> data) {
        super(data);
    }

    @Override
    public void encodeData(RegistryFriendlyByteBuf buf, ProspectorMode.OreInfo data) {
        ProspectorMode.BEDROCK_ORE.serialize(data, buf);
    }

    public static SPacketProspectBedrockOre decode(RegistryFriendlyByteBuf buf) {
        return SPacketProspect.decode(buf, ProspectorMode.BEDROCK_ORE::deserialize, SPacketProspectBedrockOre::new);
    }

    @Override
    public void execute(IPayloadContext handler) {
        // todo: add cache for bedrock ore veins
    }

    @Override
    public @NotNull Type<SPacketProspectBedrockOre> type() {
        return TYPE;
    }

}
