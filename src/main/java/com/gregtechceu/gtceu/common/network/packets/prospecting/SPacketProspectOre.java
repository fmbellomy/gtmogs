package com.gregtechceu.gtceu.common.network.packets.prospecting;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.worldgen.ores.GeneratedVeinMetadata;
import com.gregtechceu.gtceu.integration.map.cache.client.GTClientCache;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SPacketProspectOre extends SPacketProspect<GeneratedVeinMetadata> {

    public static final ResourceLocation ID = GTCEu.id("prospect_ore");
    public static final Type<SPacketProspectOre> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, SPacketProspectOre> CODEC = StreamCodec
            .ofMember(SPacketProspectOre::encode, SPacketProspectOre::decode);

    public SPacketProspectOre() {
        super();
    }

    public SPacketProspectOre(ResourceKey<Level> key, Collection<GeneratedVeinMetadata> veins) {
        super(key, veins.stream().map(GeneratedVeinMetadata::center).toList(), veins);
    }

    @Override
    public void encodeData(FriendlyByteBuf buf, GeneratedVeinMetadata data) {
        data.writeToPacket(buf);
    }

    public static SPacketProspectOre decode(FriendlyByteBuf buf) {
        return SPacketProspect.decode(buf, GeneratedVeinMetadata::readFromPacket, SPacketProspectOre::new);
    }

    @Override
    public void execute(IPayloadContext handler) {
        data.rowMap().forEach((level, ores) -> ores
                .forEach((blockPos, vein) -> GTClientCache.instance.addVein(level,
                        blockPos.getX() >> 4, blockPos.getZ() >> 4, vein)));
    }

    @Override
    public @NotNull Type<SPacketProspectOre> type() {
        return TYPE;
    }

}
