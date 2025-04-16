package com.gregtechceu.gtceu.common.network.packets.prospecting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Collection;
import java.util.function.Function;

public abstract class SPacketProspect<T> implements CustomPacketPayload {

    protected final Table<ResourceKey<Level>, BlockPos, T> data;

    protected SPacketProspect() {
        data = HashBasedTable.create();
    }

    protected SPacketProspect(Table<ResourceKey<Level>, BlockPos, T> data) {
        this.data = data;
    }

    protected SPacketProspect(Collection<ResourceKey<Level>> keys, Collection<BlockPos> positions,
                              Collection<T> prospected) {
        this();
        var keyIterator = keys.iterator();
        var posIterator = positions.iterator();
        var prospectedIterator = prospected.iterator();
        while (keyIterator.hasNext()) {
            data.put(keyIterator.next(), posIterator.next(), prospectedIterator.next());
        }
    }

    protected SPacketProspect(ResourceKey<Level> key, Collection<BlockPos> positions, Collection<T> prospected) {
        data = HashBasedTable.create(1, prospected.size());
        var posIterator = positions.iterator();
        var prospectedIterator = prospected.iterator();
        while (posIterator.hasNext()) {
            data.put(key, posIterator.next(), prospectedIterator.next());
        }
    }

    protected SPacketProspect(ResourceKey<Level> key, BlockPos position, T prospected) {
        data = HashBasedTable.create(1, 1);
        data.put(key, position, prospected);
    }

    public abstract void encodeData(FriendlyByteBuf buf, T data);

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(data.rowKeySet().size());
        data.rowMap().forEach((key, entry) -> {
            buf.writeResourceKey(key);
            buf.writeInt(entry.size());
            entry.forEach(((blockPos, t) -> {
                buf.writeBlockPos(blockPos);
                encodeData(buf, t);
            }));
        });
    }

    public static <T, V extends SPacketProspect<T>> V decode(FriendlyByteBuf buf,
                                                             Function<FriendlyByteBuf, T> decodeFunction,
                                                             Function<Table<ResourceKey<Level>, BlockPos, T>, V> createFunction) {
        Table<ResourceKey<Level>, BlockPos, T> data = HashBasedTable.create();

        var rowCount = buf.readInt();
        for (int i = 0; i < rowCount; i++) {
            var rowKey = buf.readResourceKey(Registries.DIMENSION);
            var entryCount = buf.readInt();
            for (int j = 0; j < entryCount; j++) {
                var blockPos = buf.readBlockPos();
                var t = decodeFunction.apply(buf);
                data.put(rowKey, blockPos, t);
            }
        }
        return createFunction.apply(data);
    }

    public abstract void execute(IPayloadContext handler);
}
