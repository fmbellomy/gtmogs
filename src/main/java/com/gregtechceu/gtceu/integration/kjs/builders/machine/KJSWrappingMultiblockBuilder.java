package com.gregtechceu.gtceu.integration.kjs.builders.machine;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.integration.kjs.GTKubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import com.gregtechceu.gtceu.common.registry.GTRegistration;

import dev.latvian.mods.kubejs.client.LangKubeEvent;
import net.minecraft.resources.ResourceLocation;

import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public class KJSWrappingMultiblockBuilder extends BuilderBase<MultiblockMachineDefinition> {

    @HideFromJS
    @Nullable
    @Getter
    private final KJSTieredMultiblockBuilder tieredBuilder;
    @HideFromJS
    @Nullable
    @Getter
    private final MultiblockMachineBuilder machineBuilder;

    public KJSWrappingMultiblockBuilder(ResourceLocation id, KJSTieredMultiblockBuilder tieredBuilder) {
        super(id);
        this.tieredBuilder = tieredBuilder;
        this.machineBuilder = null;
    }

    public KJSWrappingMultiblockBuilder(ResourceLocation id, MultiblockMachineBuilder machineBuilder) {
        super(id);
        this.tieredBuilder = null;
        this.machineBuilder = machineBuilder;
    }

    public KJSWrappingMultiblockBuilder tiers(int... tiers) {
        if (tieredBuilder != null) {
            tieredBuilder.tiers(tiers);
        }
        if (machineBuilder != null && tiers.length > 0) {
            machineBuilder.tier(tiers[0]);
        }
        return this;
    }

    public KJSWrappingMultiblockBuilder machine(KJSTieredMultiblockBuilder.TieredCreationFunction machine) {
        if (tieredBuilder != null) {
            tieredBuilder.machine(machine);
        }
        if (machineBuilder != null) {
            machineBuilder.machine((holder) -> machine.create(holder, machineBuilder.tier()));
        }
        return this;
    }

    public KJSWrappingMultiblockBuilder definition(KJSTieredMultiblockBuilder.DefinitionFunction definition) {
        if (tieredBuilder != null) {
            tieredBuilder.definition(definition);
        }
        if (machineBuilder != null) {
            definition.apply(machineBuilder.tier(), machineBuilder);
        }
        return this;
    }

    @Override
    public void generateLang(LangKubeEvent lang) {
        if (tieredBuilder != null) {
            tieredBuilder.generateLang(lang);
        }
        if (machineBuilder != null) {
            MultiblockMachineDefinition def = get();
            if (def.getLangValue() != null) {
                lang.add(def.getId().getNamespace(), def.getDescriptionId(), def.getLangValue());
            }
        }
    }

    @Override
    public MultiblockMachineDefinition createObject() {
        if (tieredBuilder != null) {
            tieredBuilder.createObject();
            for (var def : tieredBuilder.get()) {
                if (def != null) {
                    return def;
                }
            }
        }
        if (machineBuilder != null) {
            return machineBuilder.register();
        }
        // should never happen.
        throw new IllegalStateException("Empty multiblock builder [" + id + "]");
    }

    public static KJSWrappingMultiblockBuilder createKJSMulti(ResourceLocation id) {
        var baseBuilder = new MultiblockMachineBuilder(GTRegistrate.create(id.getNamespace()), id.getPath(),
                WorkableElectricMultiblockMachine::new,
                MetaMachineBlock::new,
                MetaMachineItem::new,
                MetaMachineBlockEntity::createBlockEntity);
        return new KJSWrappingMultiblockBuilder(id, baseBuilder);
    }

    public static KJSWrappingMultiblockBuilder createKJSMulti(ResourceLocation id,
                                                              KJSTieredMachineBuilder.CreationFunction<? extends MultiblockControllerMachine> machine) {
        var baseBuilder = new MultiblockMachineBuilder(GTRegistrate.create(id.getNamespace()), id.getPath(),
                machine::create,
                MetaMachineBlock::new,
                MetaMachineItem::new,
                MetaMachineBlockEntity::createBlockEntity);
        return new KJSWrappingMultiblockBuilder(id, baseBuilder);
    }
}
