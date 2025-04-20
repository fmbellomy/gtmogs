package com.gregtechceu.gtceu.integration.kjs.builders.machine;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;

import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import net.minecraft.resources.ResourceLocation;

import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import lombok.Getter;

import java.util.Arrays;

public class KJSWrappingMachineBuilder extends BuilderBase<MachineDefinition> {

    @HideFromJS
    @Getter
    private final KJSTieredMachineBuilder tieredBuilder;

    public KJSWrappingMachineBuilder(ResourceLocation id, KJSTieredMachineBuilder tieredBuilder) {
        super(id);
        this.tieredBuilder = tieredBuilder;
    }

    public KJSWrappingMachineBuilder tiers(int... tiers) {
        tieredBuilder.tiers(tiers);
        return this;
    }

    public KJSWrappingMachineBuilder machine(KJSTieredMachineBuilder.TieredCreationFunction machine) {
        tieredBuilder.machine(machine);
        return this;
    }

    public KJSWrappingMachineBuilder definition(KJSTieredMachineBuilder.DefinitionFunction definition) {
        tieredBuilder.definition(definition);
        return this;
    }

    public KJSWrappingMachineBuilder tankScalingFunction(Int2IntFunction tankScalingFunction) {
        tieredBuilder.tankScalingFunction(tankScalingFunction);
        return this;
    }

    public KJSWrappingMachineBuilder addDefaultTooltips(boolean addDefaultTooltips) {
        tieredBuilder.addDefaultTooltips(addDefaultTooltips);
        return this;
    }

    @Override
    public void generateLang(LangKubeEvent lang) {
        super.generateLang(lang);
        tieredBuilder.generateLang(lang);
    }

    @Override
    public MachineDefinition createObject() {
        tieredBuilder.createObject();
        //noinspection DataFlowIssue
        for (var def : tieredBuilder.get()) {
            if (def != null) {
                return value = def;
            }
        }
        // should never happen.
        throw new IllegalStateException("Empty tiered machine builder " + Arrays.toString(tieredBuilder.get()) +
                " With id " + tieredBuilder.id);
    }
}
