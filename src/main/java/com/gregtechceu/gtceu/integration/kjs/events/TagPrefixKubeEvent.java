package com.gregtechceu.gtceu.integration.kjs.events;

import com.gregtechceu.gtceu.api.tag.TagPrefix;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class TagPrefixKubeEvent implements KubeEvent {

    @Info("Create a tag prefix set with no defaults set.")
    public TagPrefix create(String name, Consumer<TagPrefix> consumer) {
        TagPrefix builder = new TagPrefix(name);
        consumer.accept(builder);
        return builder;
    }

    @Info("Create a tag prefix set with the defaults for ores set.")
    public TagPrefix createOre(String name, TagKey<Block> miningToolTag, Consumer<TagPrefix> consumer) {
        TagPrefix builder = TagPrefix.oreTagPrefix(name, miningToolTag);
        consumer.accept(builder);
        return builder;
    }
}
