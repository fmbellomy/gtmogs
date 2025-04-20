package com.gregtechceu.gtceu.api.item.datacomponents;

import com.google.common.collect.ImmutableMap;
import com.gregtechceu.gtceu.api.item.tool.behavior.IToolBehavior;
import com.gregtechceu.gtceu.api.item.tool.behavior.ToolBehaviorType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.utils.codec.StreamCodecUtils;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public record ToolBehaviors(@Unmodifiable Map<ToolBehaviorType<?>, IToolBehavior<?>> behaviors) {

    public static final ToolBehaviors EMPTY = new ToolBehaviors(Map.of());

    public static final Codec<ToolBehaviors> CODEC = Codec
            .dispatchedMap(GTRegistries.TOOL_BEHAVIORS.byNameCodec(), type -> (Codec<IToolBehavior<?>>) type.getCodec())
            .xmap(ToolBehaviors::new, ToolBehaviors::behaviors);
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolBehaviors> STREAM_CODEC = StreamCodecUtils
            .dispatchMap(
                    size -> (Map<ToolBehaviorType<?>, IToolBehavior<?>>) new HashMap<ToolBehaviorType<?>, IToolBehavior<?>>(
                            size),
                    GTRegistries.TOOL_BEHAVIORS.streamCodec(),
                    type -> (StreamCodec<? super RegistryFriendlyByteBuf, IToolBehavior<?>>) type.getStreamCodec())
            .map(ToolBehaviors::new, ToolBehaviors::behaviors);

    public ToolBehaviors(List<IToolBehavior<?>> behaviors) {
        this(behaviors.stream().collect(Collectors.toMap(IToolBehavior::getType, Function.identity())));
    }

    public boolean hasBehavior(ToolBehaviorType<?> type) {
        return behaviors.containsKey(type);
    }

    public <T extends IToolBehavior<T>> T getBehavior(ToolBehaviorType<T> type) {
        return (T) this.behaviors.get(type);
    }

    public ToolBehaviors withBehavior(IToolBehavior<?> behavior) {
        var behaviors = ImmutableMap.<ToolBehaviorType<?>, IToolBehavior<?>>builder()
                .putAll(this.behaviors)
                .put(behavior.getType(), behavior)
                .buildKeepingLast();
        return new ToolBehaviors(behaviors);
    }
}
