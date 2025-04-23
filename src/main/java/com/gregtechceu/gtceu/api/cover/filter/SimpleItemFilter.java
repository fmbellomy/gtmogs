package com.gregtechceu.gtceu.api.cover.filter;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.PhantomSlotWidget;
import com.gregtechceu.gtceu.api.gui.widget.ToggleButtonWidget;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.data.item.GTDataComponents;

import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.world.item.ItemStack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class SimpleItemFilter implements ItemFilter {

    public static final Codec<SimpleItemFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("is_blacklist").forGetter(val -> val.isBlackList),
            Codec.BOOL.fieldOf("ignore_components").forGetter(val -> val.ignoreNbt),
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("matches").forGetter(val -> Arrays.stream(val.matches).toList()))
            .apply(instance, SimpleItemFilter::new));

    @Getter
    protected boolean isBlackList;
    @Getter
    protected boolean ignoreNbt;
    @Getter
    protected ItemStack[] matches = new ItemStack[9];

    protected Consumer<SimpleItemFilter> itemWriter = filter -> {};
    protected Consumer<SimpleItemFilter> onUpdated = filter -> itemWriter.accept(filter);

    @Getter
    protected int maxStackSize;

    protected SimpleItemFilter() {
        Arrays.fill(matches, ItemStack.EMPTY);
        maxStackSize = 1;
    }

    public SimpleItemFilter(boolean isBlackList, boolean ignoreNbt, List<ItemStack> matches) {
        this.isBlackList = isBlackList;
        this.ignoreNbt = ignoreNbt;
        this.matches = matches.toArray(ItemStack[]::new);
    }

    public static SimpleItemFilter loadFilter(ItemStack itemStack) {
        SimpleItemFilter handler = itemStack.getOrDefault(GTDataComponents.SIMPLE_ITEM_FILTER, new SimpleItemFilter());
        handler.itemWriter = filter -> itemStack.set(GTDataComponents.SIMPLE_ITEM_FILTER, filter);
        return handler;
    }

    @Override
    public void setOnUpdated(Consumer<ItemFilter> onUpdated) {
        this.onUpdated = filter -> {
            this.itemWriter.accept(filter);
            onUpdated.accept(filter);
        };
    }

    @Override
    public boolean isBlank() {
        return !isBlackList && !ignoreNbt && Arrays.stream(matches).allMatch(ItemStack::isEmpty);
    }

    public void setBlackList(boolean blackList) {
        isBlackList = blackList;
        onUpdated.accept(this);
    }

    public void setIgnoreNbt(boolean ingoreNbt) {
        this.ignoreNbt = ingoreNbt;
        onUpdated.accept(this);
    }

    public WidgetGroup openConfigurator(int x, int y) {
        WidgetGroup group = new WidgetGroup(x, y, 18 * 3 + 25, 18 * 3); // 80 55
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final int index = i * 3 + j;

                var handler = new CustomItemStackHandler(matches[index]);

                var slot = new PhantomSlotWidget(handler, 0, i * 18, j * 18) {

                    @Override
                    public void updateScreen() {
                        super.updateScreen();
                        setMaxStackSize(maxStackSize);
                    }

                    @Override
                    public void detectAndSendChanges() {
                        super.detectAndSendChanges();
                        setMaxStackSize(maxStackSize);
                    }
                };

                slot.setChangeListener(() -> {
                    matches[index] = handler.getStackInSlot(0);
                    onUpdated.accept(this);
                }).setBackground(GuiTextures.SLOT);

                group.addWidget(slot);
            }
        }
        group.addWidget(new ToggleButtonWidget(18 * 3 + 5, 0, 20, 20,
                GuiTextures.BUTTON_BLACKLIST, this::isBlackList, this::setBlackList));
        group.addWidget(new ToggleButtonWidget(18 * 3 + 5, 20, 20, 20,
                GuiTextures.BUTTON_FILTER_NBT, this::isIgnoreNbt, this::setIgnoreNbt));
        return group;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return testItemCount(itemStack) > 0;
    }

    @Override
    public int testItemCount(ItemStack itemStack) {
        int totalItemCount = getTotalConfiguredItemCount(itemStack);

        if (isBlackList) {
            return (totalItemCount > 0) ? 0 : Integer.MAX_VALUE;
        }

        return totalItemCount;
    }

    public int getTotalConfiguredItemCount(ItemStack itemStack) {
        int totalCount = 0;

        for (var candidate : matches) {
            if (ignoreNbt && ItemStack.isSameItemSameComponents(candidate, itemStack)) {
                totalCount += candidate.getCount();
            } else if (ItemStack.isSameItemSameComponents(candidate, itemStack)) {
                totalCount += candidate.getCount();
            }
        }

        return totalCount;
    }

    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;

        for (ItemStack match : matches) {
            match.setCount(Math.min(match.getCount(), maxStackSize));
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleItemFilter that)) return false;

        return isBlackList == that.isBlackList && ignoreNbt == that.ignoreNbt && maxStackSize == that.maxStackSize &&
                Arrays.equals(matches, that.matches);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(isBlackList);
        result = 31 * result + Boolean.hashCode(ignoreNbt);
        result = 31 * result + Arrays.hashCode(matches);
        result = 31 * result + maxStackSize;
        return result;
    }
}
