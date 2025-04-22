<<<<<<<< HEAD:src/main/java/com/gregtechceu/gtceu/data/tag/BiomeTagsLoader.java
package com.gregtechceu.gtceu.data.tag;
========
package com.gregtechceu.gtceu.data.datagen.tag;
>>>>>>>> sc/remake-1.21.1-branch:src/main/java/com/gregtechceu/gtceu/data/datagen/tag/BiomeTagsLoader.java

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.data.tag.CustomTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
<<<<<<<< HEAD:src/main/java/com/gregtechceu/gtceu/data/tag/BiomeTagsLoader.java
import net.minecraft.world.level.biome.Biomes;
========
>>>>>>>> sc/remake-1.21.1-branch:src/main/java/com/gregtechceu/gtceu/data/datagen/tag/BiomeTagsLoader.java
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BiomeTagsLoader extends BiomeTagsProvider {

    public BiomeTagsLoader(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture,
                           @Nullable ExistingFileHelper existingFileHelper) {
        super(arg, completableFuture, GTCEu.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(CustomTags.HAS_RUBBER_TREE).addTag(Tags.Biomes.IS_SWAMP).addTag(BiomeTags.IS_FOREST)
                .addTag(BiomeTags.IS_JUNGLE);
    }
}
