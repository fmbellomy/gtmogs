package com.gregtechceu.gtceu.api.material.material.properties;

import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.utils.memoization.GTMemoizer;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;
import java.util.function.Supplier;

// TODO document
public class ArmorProperty implements IMaterialProperty {

    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(
            new EnumMap<>(ArmorItem.Type.class), (map) -> {
                map.put(ArmorItem.Type.BOOTS, 13);
                map.put(ArmorItem.Type.LEGGINGS, 15);
                map.put(ArmorItem.Type.CHESTPLATE, 16);
                map.put(ArmorItem.Type.HELMET, 11);
            });

    @Getter
    @Setter
    @Range(from = 0, to = Integer.MAX_VALUE)
    private int durabilityMultiplier;
    @Setter
    private Map<ArmorItem.Type, Integer> protectionValues;
    @Setter
    private int enchantability;
    @Setter
    private Holder<SoundEvent> sound;
    @Setter
    private float toughness;
    @Setter
    private float knockbackResistance;

    @Setter
    private Supplier<@Nullable Ingredient> repairIngredient;
    private boolean noRepair;

    @Getter
    @Setter
    private CustomTextureGetter customTextureGetter = (stack, entity, slot, overlay) -> null;

    @Getter
    @Setter
    private boolean dyeable;

    @Getter
    @Setter
    private List<ArmorMaterial.Layer> layers = null;

    @Getter
    private Holder<ArmorMaterial> armorMaterial;
    private Material material;

    public ArmorProperty(int durabilityMultiplier, int[] protectionValues) {
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionValues = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            for (int i = 0; i < 4; i++) {
                map.put(ArmorItem.Type.values()[i], protectionValues[i]);
            }
        });
        this.sound = SoundEvents.ARMOR_EQUIP_IRON;
        this.toughness = 0;
        this.knockbackResistance = 0;
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {
        if (this.material == null) {
            this.material = properties.getMaterial();
        }

        if (this.repairIngredient == null && noRepair) {
            this.repairIngredient = () -> null;
        } else if (this.repairIngredient == null) {
            this.repairIngredient = GTMemoizer
                    .memoize(() -> Ingredient.of(ChemicalHelper.getTag(TagPrefix.plate, material)));
        }

        if (this.layers == null) {
            this.layers = List.of(new ArmorMaterial.Layer(this.material.getResourceLocation(), "", this.dyeable));
        }
        if (this.armorMaterial == null) {
            GTRegistrate registrate = GTRegistrate.createIgnoringListenerErrors(this.material.getModid());
            this.armorMaterial = registrate.generic(this.material.getName(), Registries.ARMOR_MATERIAL,
                    () -> new ArmorMaterial(protectionValues, enchantability, sound, repairIngredient,
                            layers, toughness, knockbackResistance))
                    .register();
        }
    }

    @SuppressWarnings("unused")
    public static class Builder {

        private final ArmorProperty armorProperty;

        public static ArmorProperty.Builder of(int durabilityMultiplier, int[] protectionValues) {
            Preconditions.checkArgument(protectionValues != null && protectionValues.length == 4,
                    "protectionValues must have 4 entries!");
            return new ArmorProperty.Builder(durabilityMultiplier, protectionValues);
        }

        private Builder(int durabilityMultiplier, int[] protectionValues) {
            armorProperty = new ArmorProperty(durabilityMultiplier, protectionValues);
        }

        public ArmorProperty.Builder unbreakable() {
            armorProperty.durabilityMultiplier = 0;
            return this;
        }

        public ArmorProperty.Builder enchantability(int enchantability) {
            armorProperty.enchantability = enchantability;
            return this;
        }

        public ArmorProperty.Builder protectionValue(ArmorItem.Type type, int value) {
            armorProperty.protectionValues.put(type, value);
            return this;
        }

        public ArmorProperty.Builder protectionValues(Map<ArmorItem.Type, Integer> protectionValues) {
            armorProperty.protectionValues = protectionValues;
            return this;
        }

        public ArmorProperty.Builder repairIngredient(@Nullable Supplier<@Nullable Ingredient> repairIngredient) {
            if (repairIngredient == null) {
                armorProperty.repairIngredient = () -> null;
                armorProperty.noRepair = true;
            } else {
                armorProperty.repairIngredient = GTMemoizer.memoize(repairIngredient);
            }
            return this;
        }

        public ArmorProperty.Builder toughness(float toughness) {
            armorProperty.toughness = toughness;
            return this;
        }

        public ArmorProperty.Builder knockbackResistance(float knockbackResistance) {
            armorProperty.knockbackResistance = knockbackResistance;
            return this;
        }

        public ArmorProperty.Builder dyeable(boolean dyeable) {
            armorProperty.dyeable = dyeable;
            return this;
        }

        public ArmorProperty.Builder layers(ArmorMaterial.Layer... layers) {
            return layers(Arrays.asList(layers));
        }

        public ArmorProperty.Builder layers(List<ArmorMaterial.Layer> layers) {
            armorProperty.layers = layers;
            return this;
        }

        public ArmorProperty.Builder customTexture(ArmorProperty.@NotNull CustomTextureGetter textureGetter) {
            armorProperty.customTextureGetter = textureGetter;
            return this;
        }

        public ArmorProperty build() {
            return armorProperty;
        }
    }

    @FunctionalInterface
    public interface CustomTextureGetter {

        ResourceLocation getCustomTexture(ItemStack stack, Entity entity, EquipmentSlot slot,
                                          ArmorMaterial.Layer layer);
    }
}
