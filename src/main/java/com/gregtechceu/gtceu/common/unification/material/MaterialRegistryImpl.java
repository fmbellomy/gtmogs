package com.gregtechceu.gtceu.common.unification.material;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.material.material.registry.MaterialRegistry;
import com.gregtechceu.gtceu.data.material.GTMaterials;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

public class MaterialRegistryImpl extends MaterialRegistry {

    private static int networkIdCounter;

    private final int networkId = networkIdCounter++;
    private final String modid;
    private final ResourceLocation templateId;

    private boolean isRegistryClosed = false;
    @NotNull
    private Material fallbackMaterial = GTMaterials.NULL;

    protected MaterialRegistryImpl(@NotNull String modId) {
        super(modId);
        this.modid = modId;
        this.templateId = ResourceLocation.fromNamespaceAndPath(modId, "");
    }

    @Override
    public void register(Material material) {
        this.register(material.getName(), material);
    }

    @Override
    public @NotNull Material get(String key) {
        return getOrDefault(templateId.withPath(key), getFallbackMaterial());
    }

    public <T extends Material> T register(@NotNull String key, @NotNull T value) {
        if (isRegistryClosed) {
            GTCEu.LOGGER.error(
                    "Materials cannot be registered in the PostMaterialEvent (or after)! Must be added in the MaterialEvent. Skipping material {}...",
                    key);
            return null;
        }
        super.register(templateId.withPath(key), value);
        return value;
    }

    @NotNull
    @UnmodifiableView
    @Override
    public Collection<Material> getAllMaterials() {
        return this.registry().values();
    }

    @Override
    public void setFallbackMaterial(@NotNull Material material) {
        this.fallbackMaterial = material;
    }

    @NotNull
    @Override
    public Material getFallbackMaterial() {
        if (this.fallbackMaterial.isNull()) {
            this.fallbackMaterial = MaterialRegistryManager.getInstance().getDefaultFallback();
        }
        return this.fallbackMaterial;
    }

    @Override
    public int getNetworkId() {
        return this.networkId;
    }

    @NotNull
    @Override
    public String getModid() {
        return this.modid;
    }

    public void closeRegistry() {
        this.isRegistryClosed = true;
    }
}
