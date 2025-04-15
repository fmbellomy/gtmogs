package com.gregtechceu.gtceu.common.item.tool.behavior;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.item.datacomponents.AoESymmetrical;
import com.gregtechceu.gtceu.api.item.tool.behavior.IToolUIBehavior;

import com.gregtechceu.gtceu.api.item.tool.behavior.ToolBehaviorType;
import com.gregtechceu.gtceu.data.tag.GTDataComponents;
import com.gregtechceu.gtceu.data.tools.GTToolBehaviors;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.gregtechceu.gtceu.api.item.tool.ToolHelper.*;

public class AOEConfigUIBehavior implements IToolUIBehavior<AOEConfigUIBehavior> {

    public static final AOEConfigUIBehavior INSTANCE = new AOEConfigUIBehavior();
    public static final Codec<AOEConfigUIBehavior> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, AOEConfigUIBehavior> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public boolean openUI(@NotNull Player player, @NotNull InteractionHand hand) {
        return player.isShiftKeyDown() && player.getItemInHand(hand)
                .getOrDefault(GTDataComponents.AOE, AoESymmetrical.none()) != AoESymmetrical.none();
    }

    @Override
    public ModularUI createUI(Player player, HeldItemUIFactory.HeldItemHolder holder) {
        ItemStack held = holder.getHeld();
        final AoESymmetrical.Mutable definition = getAoEDefinition(held).toMutable();
        return new ModularUI(120, 80, holder, player).background(GuiTextures.BACKGROUND)
                .widget(new LabelWidget(6, 10, "item.gtceu.tool.aoe.columns"))
                .widget(new LabelWidget(49, 10, "item.gtceu.tool.aoe.rows"))
                .widget(new LabelWidget(79, 10, "item.gtceu.tool.aoe.layers"))
                .widget(new ButtonWidget(15, 24, 20, 20, new TextTexture("+"), (data) -> {
                    definition.increaseColumn();
                    held.set(GTDataComponents.AOE, definition.toImmutable());
                    holder.markAsDirty();
                }))
                .widget(new ButtonWidget(15, 44, 20, 20, new TextTexture("-"), (data) -> {
                    definition.decreaseColumn();
                    held.set(GTDataComponents.AOE, definition.toImmutable());
                    holder.markAsDirty();
                }))
                .widget(new ButtonWidget(50, 24, 20, 20, new TextTexture("+"), (data) -> {
                    definition.increaseRow();
                    held.set(GTDataComponents.AOE, definition.toImmutable());
                    holder.markAsDirty();
                }))
                .widget(new ButtonWidget(50, 44, 20, 20, new TextTexture("-"), (data) -> {
                    definition.decreaseRow();
                    held.set(GTDataComponents.AOE, definition.toImmutable());
                    holder.markAsDirty();
                }))
                .widget(new ButtonWidget(85, 24, 20, 20, new TextTexture("+"), (data) -> {
                    definition.increaseLayer();
                    held.set(GTDataComponents.AOE, definition.toImmutable());
                    holder.markAsDirty();
                }))
                .widget(new ButtonWidget(85, 44, 20, 20, new TextTexture("-"), (data) -> {
                    definition.decreaseLayer();
                    held.set(GTDataComponents.AOE, definition.toImmutable());
                    holder.markAsDirty();
                }))
                .widget(new LabelWidget(23, 65,
                        () -> Integer.toString(
                                1 + 2 * held.getOrDefault(GTDataComponents.AOE, AoESymmetrical.none()).column())))
                .widget(new LabelWidget(58, 65,
                        () -> Integer.toString(
                                1 + 2 * held.getOrDefault(GTDataComponents.AOE, AoESymmetrical.none()).row())))
                .widget(new LabelWidget(93, 65, () -> Integer
                        .toString(1 + held.getOrDefault(GTDataComponents.AOE, AoESymmetrical.none()).layer())));
    }

    @Override
    public ToolBehaviorType<AOEConfigUIBehavior> getType() {
        return GTToolBehaviors.AOE_CONFIG_UI;
    }

}
