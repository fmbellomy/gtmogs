package com.gregtechceu.gtceu.data.datagen.tag;

import com.gregtechceu.gtceu.data.tag.CustomTags;

import net.minecraft.world.entity.EntityType;

import com.tterrag.registrate.providers.RegistrateTagsProvider;

public class EntityTypeTagLoader {

    public static void init(RegistrateTagsProvider.IntrinsicImpl<EntityType<?>> provider) {
        provider.addTag(CustomTags.HEAT_IMMUNE)
                .add(EntityType.BLAZE, EntityType.MAGMA_CUBE)
                .add(EntityType.WITHER_SKELETON, EntityType.WITHER);
        provider.addTag(CustomTags.CHEMICAL_IMMUNE)
                .add(EntityType.SKELETON, EntityType.STRAY, EntityType.BOGGED);
        provider.addTag(CustomTags.IRON_GOLEMS).add(EntityType.IRON_GOLEM);
        provider.addTag(CustomTags.SPIDERS)
                .add(EntityType.SPIDER, EntityType.CAVE_SPIDER);
    }
}
