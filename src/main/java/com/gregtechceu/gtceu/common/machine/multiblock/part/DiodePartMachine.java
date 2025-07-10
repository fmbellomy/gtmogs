package com.gregtechceu.gtceu.common.machine.multiblock.part;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.data.item.GTItemAbilities;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import lombok.Getter;

public class DiodePartMachine extends TieredIOPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(DiodePartMachine.class,
            TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    // spotless:off
    public enum AmpMode implements StringRepresentable {
        MODE_1A("1a", 1),
        MODE_2A("2a", 2),
        MODE_4A("4a", 4),
        MODE_8A("8a", 8),
        MODE_16A("16a", 16);

        public static final AmpMode[] VALUES = values();

        @Getter
        private final String serializedName;
        @Getter
        private final int ampValue;

        AmpMode(String serializedName, int ampValue) {
            this.serializedName = serializedName;
            this.ampValue = ampValue;
        }

        public AmpMode cycle() {
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }

        public static AmpMode getByValue(int amps) {
            return switch (amps) {
                case 2 -> MODE_2A;
                case 4 -> MODE_4A;
                case 8 -> MODE_8A;
                case 16 -> MODE_16A;
                default -> MODE_1A;
            };
        }
    }

    public static final EnumProperty<DiodePartMachine.AmpMode> AMP_MODE_PROPERTY = EnumProperty.create("amp_mode", AmpMode.class);
    // spotless:on

    public static int MAX_AMPS = 16;

    @Persisted
    protected NotifiableEnergyContainer energyContainer;

    @Getter
    @DescSynced
    @Persisted(key = "amp_mode")
    private int amps;

    public DiodePartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier, IO.BOTH);
        long tierVoltage = GTValues.V[getTier()];

        this.amps = 1;
        this.energyContainer = new NotifiableEnergyContainer(this, tierVoltage * MAX_AMPS * 2, tierVoltage, MAX_AMPS,
                tierVoltage, MAX_AMPS);

        reinitializeEnergyContainer();
    }

    private void cycleAmpMode() {
        amps = amps == getMaxAmperage() ? 1 : amps << 1;
        if (!getLevel().isClientSide) {
            reinitializeEnergyContainer();
            notifyBlockUpdate();
            markDirty();
        }
    }

    /** Change this value (or override) to make the Diode able to handle more amps. Must be a power of 2 */
    protected int getMaxAmperage() {
        return MAX_AMPS;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (!GTCEu.isClientThread())
            reinitializeEnergyContainer();
    }

    protected void reinitializeEnergyContainer() {
        long tierVoltage = GTValues.V[getTier()];

        this.energyContainer.resetBasicInfo(tierVoltage * MAX_AMPS * 2, tierVoltage, amps, tierVoltage, amps);
        this.energyContainer.setSideInputCondition(s -> s != getFrontFacing());
        this.energyContainer.setSideOutputCondition(s -> s == getFrontFacing() && isWorkingEnabled());
    }

    @Override
    public int tintColor(int index) {
        if (index == 2 || index == 3) {
            return GTValues.VC[getTier()];
        }
        return super.tintColor(index);
    }

    @Override
    public boolean isFacingValid(Direction facing) {
        return true;
    }

    @Override
    protected ItemInteractionResult onSoftMalletClick(Player playerIn, InteractionHand hand, ItemStack held,
                                                      Direction gridSide,
                                                      BlockHitResult hitResult) {
        if (!held.canPerformAction(GTItemAbilities.MALLET_CONFIGURE)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        cycleAmpMode();
        if (getLevel().isClientSide) {
            setRenderState(getRenderState()
                    .setValue(AMP_MODE_PROPERTY, AmpMode.getByValue(this.amps)));

            scheduleRenderUpdate();
            playerIn.sendSystemMessage(Component.translatable("gtceu.machine.diode.message", amps));
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.CONSUME;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
