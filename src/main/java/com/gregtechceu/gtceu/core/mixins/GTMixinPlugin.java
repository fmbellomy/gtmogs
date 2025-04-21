package com.gregtechceu.gtceu.core.mixins;

import net.neoforged.fml.common.asm.enumextension.ExtensionInfo;
import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class GTMixinPlugin implements IMixinConfigPlugin {

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

    // not a fan, will have to do.
    // spotless:off
    private static final String DATA_FIX_TYPES_NAME = "net.minecraft.util.datafix.DataFixTypes";
    private static final Type   DATA_FIX_TYPES_DESC = Type.getObjectType(DATA_FIX_TYPES_NAME);

    private static final Type   EXT_INFO             = Type.getType(ExtensionInfo.class);
    private static final int    EXT_INFO_GETTER_ACC  = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
    private static final String EXT_INFO_GETTER_DESC = Type.getMethodDescriptor(EXT_INFO);
    private static final String EXT_INFO_GETTER_NAME = "getExtensionInfo";

    private static final String EXT_INFO_NONEXTENDED_NAME = "nonExtended";
    private static final String EXT_INFO_NONEXTENDED_DESC = Type.getMethodDescriptor(EXT_INFO, Type.getType(Class.class));

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // The class *should* implement IExtensibleEnum by now because this is after the mixin is applied
        if (DATA_FIX_TYPES_NAME.equals(targetClassName)) {
            for (MethodNode mn : targetClass.methods) {
                if (EXT_INFO_GETTER_NAME.equals(mn.name) && EXT_INFO_GETTER_DESC.equals(mn.desc)) {
                    if (mn.access != EXT_INFO_GETTER_ACC) {
                        mn.access = EXT_INFO_GETTER_ACC;
                    }
                    return;
                }
            }

            // add the getExtensionInfo() method
            MethodVisitor mv = targetClass.visitMethod(EXT_INFO_GETTER_ACC,
                    EXT_INFO_GETTER_NAME, EXT_INFO_GETTER_DESC, null, null);
            mv.visitCode();
            mv.visitLdcInsn(DATA_FIX_TYPES_DESC);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    EXT_INFO.getInternalName(), EXT_INFO_NONEXTENDED_NAME, EXT_INFO_NONEXTENDED_DESC, false);
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitMaxs(1, 0);
            mv.visitEnd();
        }
    }
    // spotless:on
}
