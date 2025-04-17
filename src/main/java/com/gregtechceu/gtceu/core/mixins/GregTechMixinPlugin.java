package com.gregtechceu.gtceu.core.mixins;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class GregTechMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("com.gregtechceu.gtceu.core.mixins.rei")) {
            return LoadingModList.get().getModFileById("roughlyenoughitems") != null;
        } else if (mixinClassName.startsWith("com.gregtechceu.gtceu.core.mixins.top")) {
            return LoadingModList.get().getModFileById("theoneprobe") != null;
        } else if (mixinClassName.startsWith("com.gregtechceu.gtceu.core.mixins.jei")) {
            return LoadingModList.get().getModFileById("jei") != null;
        } else if (mixinClassName.contains("com.gregtechceu.gtceu.core.mixins.emi")) {
            return LoadingModList.get().getModFileById("emi") != null;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
