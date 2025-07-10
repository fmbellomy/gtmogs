package com.gregtechceu.gtceu.integration.kjs.builders.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import com.gregtechceu.gtceu.common.registry.GTRegistration;
import com.gregtechceu.gtceu.data.machine.GTMachineUtils;
import com.gregtechceu.gtceu.data.model.GTMachineModels;
import com.gregtechceu.gtceu.integration.kjs.GTKubeJSPlugin;

import net.minecraft.resources.ResourceLocation;

import com.google.common.base.Preconditions;
import com.tterrag.registrate.providers.DataGenContext;
import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.integration.kjs.GTKubeJSPlugin.RUNTIME_BLOCKSTATE_PROVIDER;

@Accessors(fluent = true, chain = true)
public class KJSTieredMultiblockBuilder extends BuilderBase<@Nullable MultiblockMachineDefinition @NotNull []> {

    private final MultiblockMachineBuilder[] builders = new MultiblockMachineBuilder[TIER_COUNT];

    @Setter
    public volatile int[] tiers = GTMachineUtils.ELECTRIC_TIERS;
    @Setter
    public volatile TieredCreationFunction machine;
    @Setter
    public volatile DefinitionFunction definition = (tier, def) -> def.tier(tier);

    public KJSTieredMultiblockBuilder(ResourceLocation id) {
        super(id);
    }

    public KJSTieredMultiblockBuilder(ResourceLocation id, TieredCreationFunction machine) {
        super(id);
        this.machine = machine;
    }

    @Override
    public void generateAssets(KubeAssetGenerator generator) {
        GTKubeJSPlugin.initRuntimeProvider(generator);

        super.generateAssets(generator);
        for (int tier : this.tiers) {
            MachineBuilder<?> builder = this.builders[tier];
            MachineDefinition definition = this.object[tier];
            if (builder == null || definition == null) {
                continue;
            }
            if (builder.model() == null && builder.blockModel() == null) return;

            final ResourceLocation id = definition.getId();
            // Fake a data provider for the GT model builders
            var context = new DataGenContext<>(definition::getBlock, definition.getName(), id);
            if (builder.blockModel() != null) {
                builder.blockModel().accept(context, RUNTIME_BLOCKSTATE_PROVIDER);
            } else {
                GTMachineModels.createMachineModel(builder.model()).accept(context, RUNTIME_BLOCKSTATE_PROVIDER);
            }

            generator.itemModel(id, gen -> gen.parent(id.withPrefix("block/machine/")));
        }
    }

    @Override
    public void generateLang(LangKubeEvent lang) {
        super.generateLang(lang);
        for (int tier : tiers) {
            MachineDefinition def = object[tier];
            if (def != null && def.getLangValue() != null) {
                lang.add(def.getId().getNamespace(), def.getDescriptionId(), def.getLangValue());
            }
        }
    }

    @Override
    public @Nullable MultiblockMachineDefinition @NotNull [] createObject() {
        Preconditions.checkNotNull(tiers, "Tiers can't be null!");
        Preconditions.checkArgument(tiers.length > 0, "tiers must have at least one tier!");
        Preconditions.checkNotNull(machine, "You must set a machine creation function! " +
                "example: `builder.machine((holder, tier) => new SimpleTieredMachine(holder, tier, t => t * 3200)`");
        Preconditions.checkNotNull(definition, "You must set a definition function! " +
                "See GTMachines for examples");
        MultiblockMachineDefinition[] definitions = new MultiblockMachineDefinition[TIER_COUNT];
        for (final int tier : tiers) {
            String tierName = VN[tier].toLowerCase(Locale.ROOT);
            MultiblockMachineBuilder builder = GTRegistration.REGISTRATE.multiblock(
                    String.format("%s_%s", tierName, this.id.getPath()),
                    holder -> machine.create(holder, tier));

            builder.workableTieredHullModel(id.withPrefix("block/machines/"))
                    .tier(tier);
            this.definition.apply(tier, builder);
            this.builders[tier] = builder;
            definitions[tier] = builder.register();
        }
        return definitions;
    }

    @FunctionalInterface
    public interface TieredCreationFunction {

        MultiblockControllerMachine create(IMachineBlockEntity holder, int tier);
    }

    @FunctionalInterface
    public interface DefinitionFunction {

        void apply(int tier, MachineBuilder<?> builder);
    }
}
