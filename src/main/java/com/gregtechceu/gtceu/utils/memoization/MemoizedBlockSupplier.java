package com.gregtechceu.gtceu.utils.memoization;

import com.gregtechceu.gtceu.api.material.material.stack.MaterialEntry;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * A variant of the memoized supplier that stores a block explicitly.
 * Use this to save blocks to
 * {@link com.gregtechceu.gtceu.api.material.material.ItemMaterialData#registerMaterialInfoItem(MaterialEntry, Supplier)}}
 */
public class MemoizedBlockSupplier<T extends Block> extends MemoizedSupplier<T> {

    protected MemoizedBlockSupplier(Supplier<T> delegate) {
        super(delegate);
    }
}
