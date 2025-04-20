package com.gregtechceu.gtceu.integration.kjs.events;

import com.gregtechceu.gtceu.api.material.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.recipe.component.CraftingComponent;

import dev.latvian.mods.kubejs.event.KubeStartupEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@SuppressWarnings({ "unused" })
@NoArgsConstructor
public class CraftingComponentsKubeEvent implements KubeStartupEvent {

    public void modify(CraftingComponent craftingComponent, int tier, Object value) {
        craftingComponent.add(tier, value);
    }

    public void modify(CraftingComponent craftingComponent, Map<Number, Object> map) {
        for (var val : map.entrySet()) {
            craftingComponent.add(val.getKey().intValue(), val.getValue());
        }
    }

    public void modifyItem(CraftingComponent craftingComponent, int tier, ItemStack item) {
        craftingComponent.add(tier, item);
    }

    public void modifyItem(CraftingComponent craftingComponent, Map<Number, ItemStack> map) {
        for (var val : map.entrySet()) {
            craftingComponent.add(val.getKey().intValue(), val.getValue());
        }
    }

    public void modifyTag(CraftingComponent craftingComponent, int tier, ResourceLocation tag) {
        craftingComponent.add(tier, TagKey.create(Registries.ITEM, tag));
    }

    public void modifyTag(CraftingComponent craftingComponent, Map<Number, ResourceLocation> map) {
        for (var val : map.entrySet()) {
            craftingComponent.add(val.getKey().intValue(), TagKey.create(Registries.ITEM, val.getValue()));
        }
    }

    public void modifyMaterialEntry(CraftingComponent craftingComponent, int tier, MaterialEntry item) {
        craftingComponent.add(tier, item);
    }

    public void modifyMaterialEntry(CraftingComponent craftingComponent, Map<Number, MaterialEntry> map) {
        for (var val : map.entrySet()) {
            craftingComponent.add(val.getKey().intValue(), val.getValue());
        }
    }

    public void setFallbackItem(CraftingComponent craftingComponent, ItemStack stack) {
        craftingComponent.setFallback(stack);
    }

    public void setFallbackTag(CraftingComponent craftingComponent, ResourceLocation tag) {
        craftingComponent.setFallback(TagKey.create(Registries.ITEM, tag));
    }

    public void setFallbackMaterialEntry(CraftingComponent craftingComponent, MaterialEntry materialEntry) {
        craftingComponent.setFallback(materialEntry);
    }

    public void removeTier(CraftingComponent craftingComponent, int tier) {
        craftingComponent.remove(tier);
    }

    public void removeTiers(CraftingComponent craftingComponent, List<Number> tiers) {
        for (var tier : tiers) {
            craftingComponent.remove(tier.intValue());
        }
    }

    public CraftingComponent create(String id, Object fallback) {
        return CraftingComponent.of(id, fallback);
    }

    public CraftingComponent create(String id, Object fallback, Map<Number, Object> map) {
        var m = CraftingComponent.of(id, fallback);
        for (var val : map.entrySet()) {
            m.add(val.getKey().intValue(), val.getValue());
        }
        return m;
    }

    public CraftingComponent createItem(String id, Object fallback, Map<Number, ItemStack> map) {
        var m = CraftingComponent.of(id, fallback);
        for (var val : map.entrySet()) {
            m.add(val.getKey().intValue(), val.getValue());
        }
        return m;
    }

    public CraftingComponent createTag(String id, Object fallback, Map<Number, ResourceLocation> map) {
        var m = CraftingComponent.of(id, fallback);
        for (var val : map.entrySet()) {
            m.add(val.getKey().intValue(), TagKey.create(Registries.ITEM, val.getValue()));
        }
        return m;
    }

    public CraftingComponent createMaterialEntry(String id, Object fallback, Map<Number, MaterialEntry> map) {
        var m = CraftingComponent.of(id, fallback);
        for (var val : map.entrySet()) {
            m.add(val.getKey().intValue(), val.getValue());
        }
        return m;
    }
}
