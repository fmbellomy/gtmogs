package com.gregtechceu.gtceu.integration.kjs.builders.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.steam.SimpleSteamMachine;
import com.gregtechceu.gtceu.api.multiblock.util.RelativeDirection;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.common.registry.GTRegistration;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.resources.ResourceLocation;

import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true, chain = true)
public class KJSSteamMachineBuilder extends BuilderBase<MachineDefinition> {

    @Setter
    public volatile boolean hasHighPressure = true;
    @Setter
    public volatile SteamCreationFunction machine = SimpleSteamMachine::new;
    @Setter
    public volatile SteamDefinitionFunction definition = (isHP, def) -> def.tier(isHP ? 1 : 0);
    @Nullable
    private volatile MachineDefinition hp = null;

    public KJSSteamMachineBuilder(ResourceLocation id) {
        super(id);
    }

    @Override
    public MachineDefinition createObject() {
        MachineBuilder<?> lowPressureBuilder = GTRegistration.REGISTRATE.machine(
                String.format("lp_%s", this.id.getPath()),
                holder -> machine.create(holder, false));
        lowPressureBuilder.langValue("Low Pressure " + FormattingUtil.toEnglishName(this.id.getPath()))
                .tier(0)
                .recipeModifier(SimpleSteamMachine::recipeModifier)
                .modelProperty(SimpleSteamMachine.VENT_DIRECTION_PROPERTY, RelativeDirection.BACK)
                .workableSteamHullModel(false, id.withPrefix("block/machines/"));
        definition.apply(false, lowPressureBuilder);
        var lowPressure = lowPressureBuilder.register();

        if (hasHighPressure) {
            MachineBuilder<?> highPressureBuilder = GTRegistration.REGISTRATE.machine(
                    String.format("hp_%s", this.id.getPath()),
                    holder -> machine.create(holder, true));
            highPressureBuilder.langValue("High Pressure " + FormattingUtil.toEnglishName(this.id.getPath()))
                    .tier(1)
                    .recipeModifier(SimpleSteamMachine::recipeModifier)
                    .modelProperty(SimpleSteamMachine.VENT_DIRECTION_PROPERTY, RelativeDirection.BACK)
                    .workableSteamHullModel(true, id.withPrefix("block/machines/"));
            definition.apply(true, highPressureBuilder);
            hp = highPressureBuilder.register();
        }

        return lowPressure;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void generateLang(LangKubeEvent lang) {
        super.generateLang(lang);
        lang.add(GTCEu.MOD_ID, get().getDescriptionId(), get().getLangValue());
        if (hp != null) {
            lang.add(GTCEu.MOD_ID, hp.getDescriptionId(), hp.getLangValue());
        }
    }

    @FunctionalInterface
    public interface SteamCreationFunction {

        MetaMachine create(IMachineBlockEntity holder, boolean isHighPressure);
    }

    @FunctionalInterface
    public interface SteamDefinitionFunction {

        void apply(boolean isHighPressure, MachineBuilder<?> builder);
    }
}
