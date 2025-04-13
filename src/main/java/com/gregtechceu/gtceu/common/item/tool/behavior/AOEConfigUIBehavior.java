package com.gregtechceu.gtceu.common.item.tool.behavior;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.item.datacomponents.AoESymmetrical;
import com.gregtechceu.gtceu.api.item.tool.behavior.IToolUIBehavior;

import com.gregtechceu.gtceu.api.item.tool.behavior.ToolBehaviorType;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;

import static com.gregtechceu.gtceu.api.item.tool.ToolHelper.*;

public class AOEConfigUIBehavior implements IToolUIBehavior<AOEConfigUIBehavior> {

    public static final AOEConfigUIBehavior INSTANCE = new AOEConfigUIBehavior();

    @Override
    public boolean openUI(@NotNull Player player, @NotNull InteractionHand hand) {
        return player.isShiftKeyDown() && getMaxAoEDefinition(player.getItemInHand(hand)) != AoESymmetrical.none();
    }

    @Override
    public ModularUI createUI(Player player, HeldItemUIFactory.HeldItemHolder holder) {
        ItemStack held = holder.getHeld();
        final MutableObject<AoESymmetrical> definition = new MutableObject<>(getAoEDefinition(held));
        return new ModularUI(120, 80, holder, player).background(GuiTextures.BACKGROUND)
                .widget(new LabelWidget(6, 10, "item.gtceu.tool.aoe.columns"))
                .widget(new LabelWidget(49, 10, "item.gtceu.tool.aoe.rows"))
                .widget(new LabelWidget(79, 10, "item.gtceu.tool.aoe.layers"))
                .widget(new ButtonWidget(15, 24, 20, 20, new TextTexture("+"), (data) -> {
                    definition.setValue(AoESymmetrical.increaseColumn(definition.getValue()));
                    held.set(GTDataComponents.AOE, definition.getValue());
                    holder.markAsDirty();
                }))
                .widget(new ButtonWidget(15, 44, 20, 20, new TextTexture("-"), (data) -> {
                    definition.setValue(AoESymmetrical.decreaseColumn(definition.getValue()));
                    held.set(GTDataComponents.AOE, definition.getValue());
                    holder.markAsDirty();
                }))
                .widget(new ButtonWidget(50, 24, 20, 20, new TextTexture("+"), (data) -> {
                    definition.setValue(AoESymmetrical.increaseRow(definition.getValue()));
                    held.set(GTDataComponents.AOE, definition.getValue());
                    holder.markAsDirty();
                }))
                .widget(new ButtonWidget(50, 44, 20, 20, new TextTexture("-"), (data) -> {
                    definition.setValue(AoESymmetrical.decreaseRow(definition.getValue()));
                    held.set(GTDataComponents.AOE, definition.getValue());
                    holder.markAsDirty();
                }))
                .widget(new ButtonWidget(85, 24, 20, 20, new TextTexture("+"), (data) -> {
                    definition.setValue(AoESymmetrical.increaseLayer(definition.getValue()));
                    held.set(GTDataComponents.AOE, definition.getValue());
                    holder.markAsDirty();
                }))
                .widget(new ButtonWidget(85, 44, 20, 20, new TextTexture("-"), (data) -> {
                    definition.setValue(AoESymmetrical.decreaseLayer(definition.getValue()));
                    held.set(GTDataComponents.AOE, definition.getValue());
                    holder.markAsDirty();
                }))
                .widget(new LabelWidget(23, 65,
                        () -> Integer.toString(
                                1 + 2 * held.getOrDefault(GTDataComponents.AOE, AoESymmetrical.none()).getColumn())))
                .widget(new LabelWidget(58, 65,
                        () -> Integer.toString(
                                1 + 2 * held.getOrDefault(GTDataComponents.AOE, AoESymmetrical.none()).getRow())))
                .widget(new LabelWidget(93, 65, () -> Integer
                        .toString(1 + held.getOrDefault(GTDataComponents.AOE, AoESymmetrical.none()).getLayer())));
    }

    @Override
    public ToolBehaviorType<AOEConfigUIBehavior> getType() {
        return null;
    }

}
