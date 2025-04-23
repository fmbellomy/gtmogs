package com.gregtechceu.gtceu.api.material.material;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.stream.Stream;

public interface IMaterialRegistry extends Iterable<Material> {

    /**
     * Accessible when in phases:
     * <ul>
     * <li>{@link Phase#OPEN}</li>
     * <li>{@link Phase#CLOSED}</li>
     * <li>{@link Phase#FROZEN}</li>
     * </ul>
     *
     * @return all namespaces the registered materials use
     */
    @UnmodifiableView
    @NotNull
    Collection<String> getUsedNamespaces();

    /**
     * Register a material. Accessible when in phase {@link Phase#OPEN}.
     *
     * @param material the material to register
     * @return the same material.
     */
    Material register(Material material);

    /**
     * Get a material from a String in formats:
     * <ul>
     * <li>{@code "modid:registry_name"}</li>
     * <li>{@code "registry_name"} - where modid is inferred to be {@link com.gregtechceu.gtceu.GTCEu#MOD_ID}</li>
     * </ul>
     *
     * Intended for use in reading/writing materials from/to NBT tags.
     *
     * @param name the name of the material in the above format
     * @return the material associated with the name
     */
    Material getMaterial(ResourceLocation name);

    ResourceLocation getKey(Material material);

    /**
     * Set the fallback material for a namespace.
     * This is only for manual fallback usage.
     *
     * @param modId    the namespace to set the fallback for
     * @param material the fallback material
     */
    void setFallbackMaterial(@NotNull String modId, @NotNull Material material);

    /**
     * This is only for manual fallback usage.
     *
     * @param modId the namespace to get the fallback for
     * @return the fallback material, used for when another material does not exist
     */
    @NotNull
    Material getFallbackMaterial(@NotNull String modId);

    Stream<Material> stream();

    /**
     * @return the current phase in the material registration process
     * @see Phase
     */
    @NotNull
    Phase getPhase();

    default boolean canModifyMaterials() {
        return this.getPhase() != Phase.FROZEN && this.getPhase() != Phase.PRE;
    }

    enum Phase {
        /** Material Registration and Modification is not started */
        PRE,
        /** Material Registration and Modification is available */
        OPEN,
        /** Material Registration is unavailable and only Modification is available */
        CLOSED,
        /** Material Registration and Modification is unavailable */
        FROZEN
    }
}
