package com.gregtechceu.gtceu.common.item;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.material.material.properties.FluidPipeProperties;
import com.gregtechceu.gtceu.api.material.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.misc.forge.ThermalFluidHandlerItemStack;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.gregtechceu.gtceu.data.machine.GTMachineUtils;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

/**
 * @author KilaBash
 * @date 2023/3/28
 * @implNote DrumMachineItem
 */
public class DrumMachineItem extends MetaMachineItem {

    @NotNull
    private Material mat = GTMaterials.NULL;

    protected DrumMachineItem(IMachineBlock block, Properties properties, @NotNull Material mat) {
        super(block, properties);
        this.mat = mat;
    }

    public static DrumMachineItem create(IMachineBlock block, Properties properties, @NotNull Material mat) {
        return new DrumMachineItem(block, properties, mat);
    }

    public @NotNull <T> LazyOptional<T> getCapability(ItemStack itemStack, @NotNull Capability<T> cap) {
        FluidPipeProperties property;
        if (mat.hasProperty(PropertyKey.FLUID_PIPE)) {
            property = mat.getProperty(PropertyKey.FLUID_PIPE);
        } else {
            property = null;
        }

        if (cap == Capabilities.FLUID_HANDLER_ITEM && property != null) {
            return Capabilities.FLUID_HANDLER_ITEM.orEmpty(cap, LazyOptional.of(
                    () -> new ThermalFluidHandlerItemStack(
                            itemStack,
                            Math.toIntExact(GTMachineUtils.DRUM_CAPACITY.get(getDefinition())),
                            property.getMaxFluidTemperature(), property.isGasProof(), property.isAcidProof(),
                            property.isCryoProof(), property.isPlasmaProof())));
        }
        return LazyOptional.empty();
    }
}
