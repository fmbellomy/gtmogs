package com.gregtechceu.gtceu.integration.kjs.builders.machine;

import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;

import net.minecraft.resources.ResourceLocation;

import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.Getter;

import java.util.Arrays;

public class KJSWrappingMultiblockBuilder extends BuilderBase<MultiblockMachineDefinition> {

    @HideFromJS
    @Getter
    private final KJSTieredMultiblockBuilder tieredBuilder;

    public KJSWrappingMultiblockBuilder(ResourceLocation id) {
        super(id);
        this.tieredBuilder = new KJSTieredMultiblockBuilder(id);
    }

    public KJSWrappingMultiblockBuilder tiers(int... tiers) {
        tieredBuilder.tiers(tiers);
        return this;
    }

    public KJSWrappingMultiblockBuilder machine(KJSTieredMultiblockBuilder.TieredCreationFunction machine) {
        tieredBuilder.machine(machine);
        return this;
    }

    public KJSWrappingMultiblockBuilder definition(KJSTieredMultiblockBuilder.DefinitionFunction definition) {
        tieredBuilder.definition(definition);
        return this;
    }

    @Override
    public void generateLang(LangKubeEvent lang) {
        tieredBuilder.generateLang(lang);
    }

    @Override
    public MultiblockMachineDefinition createObject() {
        tieredBuilder.createObject();
        for (var def : tieredBuilder.get()) {
            if (def != null) {
                return def;
            }
        }
        // should never happen.
        throw new IllegalStateException("Empty tiered multiblock builder " + Arrays.toString(tieredBuilder.get()) +
                " With id " + tieredBuilder.id);
    }
}
