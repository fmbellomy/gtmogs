package com.gregtechceu.gtceu.common.machine.storage;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.UITemplate;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.*;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.data.item.GTDataComponents;
import com.gregtechceu.gtceu.data.item.GTItems;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class CrateMachine extends MetaMachine implements IUIMachine, IMachineLife,
                          IDropSaveMachine, IInteractedMachine {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(CrateMachine.class,
            MetaMachine.MANAGED_FIELD_HOLDER);

    public static final BooleanProperty TAPED_PROPERTY = BooleanProperty.create("taped");

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Getter
    private final Material material;
    @Getter
    private final int inventorySize;
    @Getter
    @RequireRerender
    @Persisted
    @DescSynced
    private boolean isTaped;

    @Persisted
    public final NotifiableItemStackHandler inventory;

    public CrateMachine(IMachineBlockEntity holder, Material material, int inventorySize) {
        super(holder);
        this.material = material;
        this.inventorySize = inventorySize;
        this.inventory = new NotifiableItemStackHandler(this, inventorySize, IO.BOTH);
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        int xOffset = inventorySize >= 90 ? 162 : 0;
        int yOverflow = xOffset > 0 ? 18 : 9;
        int yOffset = inventorySize > 3 * yOverflow ?
                (inventorySize - 3 * yOverflow - (inventorySize - 3 * yOverflow) % yOverflow) / yOverflow * 18 : 0;
        var modularUI = new ModularUI(176 + xOffset, 166 + yOffset, this, entityPlayer)
                .background(GuiTextures.BACKGROUND)
                .widget(new LabelWidget(5, 5, getBlockState().getBlock().getDescriptionId()))
                .widget(UITemplate.bindPlayerInventory(entityPlayer.getInventory(), GuiTextures.SLOT, 7 + xOffset / 2,
                        82 + yOffset, true));
        int x = 0;
        int y = 0;
        for (int slot = 0; slot < inventorySize; slot++) {
            modularUI.widget(new SlotWidget(inventory, slot, x * 18 + 7, y * 18 + 17)
                    .setBackgroundTexture(GuiTextures.SLOT));
            x++;
            if (x == yOverflow) {
                x = 0;
                y++;
            }
        }
        return modularUI;
    }

    @Override
    public ItemInteractionResult onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isShiftKeyDown() && !isTaped) {
            if (stack.is(GTItems.DUCT_TAPE.asItem()) || stack.is(GTItems.BASIC_TAPE.asItem())) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                isTaped = true;
                setRenderState(getRenderState().setValue(TAPED_PROPERTY, isTaped));
                return ItemInteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return IInteractedMachine.super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public void applyImplicitComponents(MetaMachineBlockEntity.@NotNull ExDataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        if (componentInput.get(DataComponents.CONTAINER) != null) {
            var contents = componentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            contents.copyInto(inventory.storage.getStacks());
            setRenderState(getRenderState().setValue(TAPED_PROPERTY, false));
        }
    }

    @Override
    public void collectImplicitComponents(DataComponentMap.@NotNull Builder components) {
        super.collectImplicitComponents(components);
        if (isTaped) {
            components.set(GTDataComponents.TAPED, Unit.INSTANCE);
            components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(inventory.storage.getStacks()));
        }
    }

    @Override
    public void removeItemComponentsFromTag(@NotNull CompoundTag tag) {
        super.removeItemComponentsFromTag(tag);
        tag.remove("isTaped");
        tag.remove("inventory");
    }

    @Override
    public boolean saveBreak() {
        return isTaped;
    }

    @Override
    public void onMachineRemoved() {
        if (!isTaped) clearInventory(inventory.storage);
    }
}
