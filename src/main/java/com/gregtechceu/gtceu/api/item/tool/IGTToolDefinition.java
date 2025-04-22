package com.gregtechceu.gtceu.api.item.tool;

import com.gregtechceu.gtceu.api.item.datacomponents.AoESymmetrical;
import com.gregtechceu.gtceu.api.item.tool.behavior.IToolBehavior;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.List;

/**
 * GT Tool Definition
 */
public interface IGTToolDefinition {

    /**
     * Tool Component/Behaviours
     */
    List<IToolBehavior<?>> getBehaviors();

    Tool getTool();

    boolean isToolEffective(BlockState state);

    /**
     * Durability Spec
     */
    int getDamagePerAction(ItemStack stack);

    int getDamagePerCraftingAction(ItemStack stack);

    boolean isSuitableForBlockBreak(ItemStack stack);

    boolean isSuitableForAttacking(ItemStack stack);

    boolean isSuitableForCrafting(ItemStack stack);

    default int getToolDamagePerBlockBreak(ItemStack stack) {
        int action = getDamagePerAction(stack);
        return isSuitableForBlockBreak(stack) ? action : action * 2;
    }

    default int getToolDamagePerAttack(ItemStack stack) {
        int action = getDamagePerAction(stack);
        return isSuitableForAttacking(stack) ? action : action * 2;
    }

    default int getToolDamagePerCraft(ItemStack stack) {
        int action = getDamagePerCraftingAction(stack);
        return isSuitableForCrafting(stack) ? action : action * 2;
    }

    /**
     * Tool Stat
     */
    default int getBaseDurability() {
        return 0;
    }

    default float getDurabilityMultiplier() {
        return 1f;
    }

    default int getBaseQuality() {
        return 0;
    }

    default float getBaseDamage() {
        return 1.0F;
    }

    default float getBaseEfficiency() {
        return 1.0F;
    }

    default float getEfficiencyMultiplier() {
        return 1.0F;
    }

    default float getAttackSpeed() {
        return 0.0F;
    }

    default AoESymmetrical getAoEDefinition() {
        return AoESymmetrical.none();
    }

    /**
     * Enchantments
     */
    default boolean isEnchantable(ItemStack stack) {
        return true;
    }

    List<TagKey<Item>> getValidEnchantmentTags();

    Object2IntMap<ResourceKey<Enchantment>> getDefaultEnchantments();

    /**
     * Misc
     */
    boolean doesSneakBypassUse();

    default ItemStack getBrokenStack() {
        return ItemStack.EMPTY;
    }
}
