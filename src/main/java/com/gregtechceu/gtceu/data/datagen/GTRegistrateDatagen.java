package com.gregtechceu.gtceu.data.datagen;

import com.gregtechceu.gtceu.common.registry.GTRegistration;
import com.gregtechceu.gtceu.data.datagen.datamap.DataMapsHandler;
import com.gregtechceu.gtceu.data.datagen.lang.LangHandler;
import com.gregtechceu.gtceu.data.datagen.tag.*;

import net.minecraft.data.DataProvider;

import com.tterrag.registrate.providers.ProviderType;

public class GTRegistrateDatagen {

    public static void init() {
        DataProvider.INDENT_WIDTH.set(4);

        GTRegistration.REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, ItemTagLoader::init);
        GTRegistration.REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, BlockTagLoader::init);
        GTRegistration.REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, FluidTagLoader::init);
        GTRegistration.REGISTRATE.addDataGenerator(ProviderType.ENTITY_TAGS, EntityTypeTagLoader::init);
        GTRegistration.REGISTRATE.addDataGenerator(ProviderType.LANG, LangHandler::init);
        GTRegistration.REGISTRATE.addDataGenerator(ProviderType.DATA_MAP, DataMapsHandler::init);
    }
}
