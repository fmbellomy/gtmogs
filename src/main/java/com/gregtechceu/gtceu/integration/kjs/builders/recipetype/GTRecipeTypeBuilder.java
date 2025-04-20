package com.gregtechceu.gtceu.integration.kjs.builders.recipetype;

import com.gregtechceu.gtceu.api.capability.recipe.*;
import com.gregtechceu.gtceu.api.gui.SteamTexture;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.sound.SoundEntry;
import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Accessors(chain = true)
public class GTRecipeTypeBuilder extends BuilderBase<GTRecipeType> {

    public transient String name, category;
    public transient final Object2IntMap<RecipeCapability<?>> maxInputs;
    public transient final Object2IntMap<RecipeCapability<?>> maxOutputs;
    private ProgressTexture progressBarTexture;
    @Nullable
    private SteamTexture steamProgressBarTexture;
    private ProgressTexture.FillDirection steamMoveType;
    private transient final Byte2ObjectMap<IGuiTexture> slotOverlays;
    @Setter
    @Nullable
    protected transient SoundEntry sound;
    @Setter
    protected transient boolean hasResearchSlot;
    @Setter
    protected transient int maxTooltips;

    @Setter
    @Nullable
    private transient GTRecipeType smallRecipeMap;
    @Setter
    @Nullable
    private transient Supplier<ItemStack> iconSupplier;
    @Nullable
    @Setter
    protected transient BiConsumer<GTRecipe, WidgetGroup> uiBuilder;

    public GTRecipeTypeBuilder(ResourceLocation i) {
        super(i);
        name = i.getPath();
        category = "custom";
        maxInputs = new Object2IntOpenHashMap<>();
        maxOutputs = new Object2IntOpenHashMap<>();
        progressBarTexture = new ProgressTexture();
        steamProgressBarTexture = null;
        steamMoveType = ProgressTexture.FillDirection.LEFT_TO_RIGHT;
        slotOverlays = new Byte2ObjectArrayMap<>();
        this.sound = null;
        this.hasResearchSlot = false;
        this.maxTooltips = 3;
        this.smallRecipeMap = null;
        this.iconSupplier = null;
        this.uiBuilder = null;
    }
    public GTRecipeTypeBuilder category(String category) {
        this.category = category;
        return this;
    }

    public GTRecipeTypeBuilder setMaxIOSize(int maxInputs, int maxOutputs, int maxFluidInputs, int maxFluidOutputs) {
        return setMaxSize(IO.IN, ItemRecipeCapability.CAP, maxInputs)
                .setMaxSize(IO.IN, FluidRecipeCapability.CAP, maxFluidInputs)
                .setMaxSize(IO.OUT, ItemRecipeCapability.CAP, maxOutputs)
                .setMaxSize(IO.OUT, FluidRecipeCapability.CAP, maxFluidOutputs);
    }

    public GTRecipeTypeBuilder setEUIO(IO io) {
        if (io.support(IO.IN)) {
            setMaxSize(IO.IN, EURecipeCapability.CAP, 1);
        }
        if (io.support(IO.OUT)) {
            setMaxSize(IO.OUT, EURecipeCapability.CAP, 1);
        }
        return this;
    }

    public GTRecipeTypeBuilder setMaxSize(IO io, RecipeCapability<?> cap, int max) {
        if (io == IO.IN || io == IO.BOTH) {
            maxInputs.put(cap, max);
        }
        if (io == IO.OUT || io == IO.BOTH) {
            maxOutputs.put(cap, max);
        }
        return this;
    }

    public GTRecipeTypeBuilder setSlotOverlay(boolean isOutput, boolean isFluid, IGuiTexture slotOverlay) {
        return this.setSlotOverlay(isOutput, isFluid, false, slotOverlay).setSlotOverlay(isOutput, isFluid, true,
                slotOverlay);
    }

    public GTRecipeTypeBuilder setSlotOverlay(boolean isOutput, boolean isFluid, boolean isLast,
                                              IGuiTexture slotOverlay) {
        this.slotOverlays.put((byte) ((isOutput ? 2 : 0) + (isFluid ? 1 : 0) + (isLast ? 4 : 0)), slotOverlay);
        return this;
    }

    public GTRecipeTypeBuilder setProgressBar(ResourceTexture progressBar, ProgressTexture.FillDirection moveType) {
        this.progressBarTexture = new ProgressTexture(progressBar.getSubTexture(0, 0, 1, 0.5),
                progressBar.getSubTexture(0, 0.5, 1, 0.5)).setFillDirection(moveType);
        return this;
    }

    public GTRecipeTypeBuilder setSteamProgressBar(SteamTexture progressBar, ProgressTexture.FillDirection moveType) {
        this.steamProgressBarTexture = progressBar;
        this.steamMoveType = moveType;
        return this;
    }

    @Override
    public String getTranslationKeyGroup() {
        return GTRecipeType.LANGUAGE_KEY_PATH;
    }

    @Override
    public GTRecipeType createObject() {
        var type = GTRecipeTypes.register(name, category);
        type.maxInputs.putAll(maxInputs);
        type.maxOutputs.putAll(maxOutputs);
        type.getSlotOverlays().putAll(slotOverlays);
        type.getRecipeUI().setProgressBarTexture(progressBarTexture);
        type.getRecipeUI().setSteamProgressBarTexture(steamProgressBarTexture);
        type.getRecipeUI().setSteamMoveType(steamMoveType);
        type.setSound(sound);
        type.setHasResearchSlot(hasResearchSlot);
        type.setMaxTooltips(maxTooltips);
        type.setSmallRecipeMap(smallRecipeMap);
        type.setIconSupplier(iconSupplier);
        type.setUiBuilder(uiBuilder);
        return type;
    }
}
