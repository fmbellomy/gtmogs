package com.gregtechceu.gtceu.api.registry.registrate;

import dev.latvian.mods.kubejs.client.LangKubeEvent;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Supplier;

public abstract class BuilderBase<T> implements Supplier<T> {

    public ResourceLocation id;
    protected T value = null;

    public BuilderBase(ResourceLocation id) {
        this.id = id;
    }

    public void generateDataJsons(KubeDataGenerator generator) {}

    public void generateAssetJsons(KubeAssetGenerator generator) {}

    public void generateLang(LangKubeEvent lang) {}

    public abstract T register();

    @Override
    public T get() {
        return value;
    }
}
