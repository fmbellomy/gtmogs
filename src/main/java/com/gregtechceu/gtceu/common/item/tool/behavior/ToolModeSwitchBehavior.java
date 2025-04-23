package com.gregtechceu.gtceu.common.item.tool.behavior;

import com.gregtechceu.gtceu.api.item.tool.behavior.IToolBehavior;
import com.gregtechceu.gtceu.api.item.tool.behavior.ToolBehaviorType;
import com.gregtechceu.gtceu.data.item.GTDataComponents;
import com.gregtechceu.gtceu.data.item.GTItemAbilities;
import com.gregtechceu.gtceu.data.tools.GTToolBehaviors;

import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbility;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ToolModeSwitchBehavior implements IToolBehavior<ToolModeSwitchBehavior> {

    public static final ToolModeSwitchBehavior INSTANCE = new ToolModeSwitchBehavior();

    public static final Codec<ToolModeSwitchBehavior> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, ToolModeSwitchBehavior> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    protected ToolModeSwitchBehavior() {}

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility action) {
        var mode = stack.getOrDefault(GTDataComponents.TOOL_MODE, ModeType.BOTH);
        boolean canWrenchConfigureAll = action == GTItemAbilities.WRENCH_CONFIGURE_ALL;
        return action == GTItemAbilities.WRENCH_CONFIGURE || switch (mode) {
            case ITEM -> canWrenchConfigureAll || action == GTItemAbilities.WRENCH_CONFIGURE_ITEMS;
            case FLUID -> canWrenchConfigureAll || action == GTItemAbilities.WRENCH_CONFIGURE_FLUIDS;
            case BOTH -> GTItemAbilities.WRENCH_CONFIGURE_ACTIONS.contains(action);
        };
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> onItemRightClick(@NotNull Level level, @NotNull Player player,
                                                                        @NotNull InteractionHand hand) {
        var held = player.getItemInHand(hand);
        if (level.isClientSide || !player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(held);
        }
        var newMode = held.getOrDefault(GTDataComponents.TOOL_MODE, ModeType.BOTH).nextMode();
        held.set(GTDataComponents.TOOL_MODE, newMode);

        player.displayClientMessage(Component.translatable("metaitem.machine_configuration.mode", newMode.getName()),
                true);
        return InteractionResultHolder.success(held);
    }

    @Override
    public ToolBehaviorType<ToolModeSwitchBehavior> getType() {
        return GTToolBehaviors.MODE_SWITCH;
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, Item.TooltipContext context, @NotNull List<Component> tooltip,
                               @NotNull TooltipFlag flag) {
        ModeType behavior = stack.getOrDefault(GTDataComponents.TOOL_MODE, ModeType.BOTH);
        tooltip.add(Component.translatable("metaitem.machine_configuration.mode", behavior.getName()));
    }

    public enum ModeType implements StringRepresentable {

        ITEM("item", Component.translatable("gtceu.mode.item")),
        FLUID("fluid", Component.translatable("gtceu.mode.fluid")),
        BOTH("both", Component.translatable("gtceu.mode.both"));

        public static final Codec<ModeType> CODEC = StringRepresentable.fromEnum(ModeType::values);
        public static final StreamCodec<ByteBuf, ModeType> STREAM_CODEC = ByteBufCodecs.BYTE
                .map(aByte -> ModeType.values()[aByte], val -> (byte) val.ordinal());

        @Getter
        private final String id;
        @Getter
        private final Component name;

        ModeType(String id, Component name) {
            this.id = id;
            this.name = name;
        }

        public ModeType nextMode() {
            return switch (this) {
                case ITEM -> FLUID;
                case FLUID -> BOTH;
                case BOTH -> ITEM;
            };
        }

        @Override
        public @NotNull String getSerializedName() {
            return id;
        }
    }
}
