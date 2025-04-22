package com.gregtechceu.gtceu.utils;

import net.minecraft.world.item.ItemStack;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Collections;
import java.util.List;

public class GTMath {

    public static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }

    public static List<ItemStack> splitStacks(ItemStack stack, long amount) {
        int count = saturatedCast(amount);
        int fullStacks = count / 64;
        int rem = count % 64;
        List<ItemStack> stacks = new ObjectArrayList<>(fullStacks + 1);
        if (fullStacks > 0) stacks.addAll(Collections.nCopies(fullStacks, stack.copyWithCount(64)));
        if (rem > 0) stacks.add(stack.copyWithCount(rem));
        return stacks;
    }

    public static int[] split(long value) {
        IntArrayList result = new IntArrayList();
        while (value > 0) {
            int intValue = (int) Math.min(value, Integer.MAX_VALUE);
            result.add(intValue);
            value -= intValue;
        }
        return result.toIntArray();
    }

    public static int saturatedCast(long value) {
        if (value > 2147483647L) {
            return Integer.MAX_VALUE;
        } else {
            return value < -2147483648L ? Integer.MIN_VALUE : (int) value;
        }
    }
}
