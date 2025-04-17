package com.gregtechceu.gtceu.api.item.tool;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IElectricItem;
import com.gregtechceu.gtceu.api.capability.recipe.*;
import com.gregtechceu.gtceu.api.item.datacomponents.AoESymmetrical;
import com.gregtechceu.gtceu.api.item.datacomponents.GTTool;
import com.gregtechceu.gtceu.api.item.datacomponents.ToolBehaviors;
import com.gregtechceu.gtceu.api.material.ChemicalHelper;
import com.gregtechceu.gtceu.api.material.material.Material;
import com.gregtechceu.gtceu.api.material.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredientExtensions;
import com.gregtechceu.gtceu.api.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.IGTTool;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.recipe.kind.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.data.enchantment.GTEnchantmentProviders;
import com.gregtechceu.gtceu.data.item.GTItemAbilities;
import com.gregtechceu.gtceu.data.item.GTItems;
import com.gregtechceu.gtceu.data.item.GTMaterialItems;
import com.gregtechceu.gtceu.data.material.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.GTRecipeTypes;
import com.gregtechceu.gtceu.data.machine.GTMachineUtils;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.data.item.GTDataComponents;
import com.gregtechceu.gtceu.utils.DummyMachineBlockEntity;
import com.gregtechceu.gtceu.utils.InfiniteEnergyContainer;

import lombok.experimental.ExtensionMethod;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.event.EventHooks;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.function.Supplier;

@ExtensionMethod(SizedIngredientExtensions.class)
public class ToolHelper {

    // Crafting Symbols
    private static final BiMap<Character, GTToolType> symbols = HashBiMap.create();

    private ToolHelper() {/**/}

    /**
     * @return finds the registered crafting symbol with the tool
     */
    public static Character getSymbolFromTool(GTToolType tool) {
        return symbols.inverse().get(tool);
    }

    /**
     * @return finds the registered tool with the crafting symbol
     */
    public static GTToolType getToolFromSymbol(Character symbol) {
        return symbols.get(symbol);
    }

    public static Set<Character> getToolSymbols() {
        return symbols.keySet();
    }

    /**
     * Registers the tool against a crafting symbol, this is used in
     * {@link com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper}
     */
    public static void registerToolSymbol(Character symbol, GTToolType tool) {
        symbols.put(symbol, tool);
    }

    public static ToolBehaviors getBehaviorsComponent(ItemStack stack) {
        return stack.getOrDefault(GTDataComponents.TOOL_BEHAVIORS, ToolBehaviors.EMPTY);
    }

    public static boolean hasBehaviorsComponent(ItemStack stack) {
        return stack.has(GTDataComponents.TOOL_BEHAVIORS);
    }

    public static ItemStack get(GTToolType toolType, Material material) {
        if (material.hasProperty(PropertyKey.TOOL)) {
            var entry = GTMaterialItems.TOOL_ITEMS.get(material, toolType);
            if (entry != null) {
                return entry.get().get();
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean is(ItemStack stack, GTToolType toolType) {
        return getToolTypes(stack).contains(toolType);
    }

    public static boolean canUse(ItemStack stack) {
        return stack.getDamageValue() <= stack.getMaxDamage();
    }

    public static void damageItem(@NotNull ItemStack stack, @Nullable LivingEntity user, int damage) {
        if (!(stack.getItem() instanceof IGTTool tool)) {
            if (user != null) stack.hurtAndBreak(damage, user, EquipmentSlot.MAINHAND);
        } else {
            if (stack.has(DataComponents.UNBREAKABLE)) {
                return;
            }
            if (!(user instanceof Player player) || !player.isCreative()) {
                RandomSource random = user == null ? GTValues.RNG : user.getRandom();
                if (tool.isElectric()) {
                    int electricDamage = damage * ConfigHolder.INSTANCE.machines.energyUsageMultiplier;
                    IElectricItem electricItem = GTCapabilityHelper.getElectricItem(stack);
                    if (electricItem != null) {
                        electricItem.discharge(electricDamage, tool.getElectricTier(), true, false, false);
                        if (electricItem.getCharge() > 0 &&
                                random.nextInt(100) >= ConfigHolder.INSTANCE.tools.rngDamageElectricTools) {
                            return;
                        }
                    } else {
                        throw new IllegalStateException(
                                "Electric tool does not have an attached electric item capability.");
                    }
                }
                // don't process unbreaking if the entity is null as we get the level from there
                if (user != null && user.level() instanceof ServerLevel serverLevel) {
                    damage = EnchantmentHelper.processDurabilityChange(serverLevel, stack, damage);
                }
                if (damage <= 0) {
                    return;
                }
                int newDurability = stack.getDamageValue() + damage;
                if (user instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(serverPlayer, stack, newDurability);
                }
                stack.setDamageValue(newDurability);
                if (newDurability > stack.getMaxDamage()) {
                    if (user instanceof Player player) {
                        Stat<?> stat = Stats.ITEM_BROKEN.get(stack.getItem());
                        player.awardStat(stat);
                    }
                    if (user != null) {
                        user.breakItem(stack);
                    }
                    stack.shrink(1);
                }
            }
        }
    }

    public static void playToolSound(GTToolType toolType, ServerPlayer player) {
        if (toolType != null && toolType.soundEntry != null) {
            toolType.soundEntry.playOnServer(player.level(), player.blockPosition());
        }
    }

    public static ItemStack getAndSetToolData(GTToolType toolType, Material material, int maxDurability,
                                              int harvestLevel,
                                              float toolSpeed, float attackDamage) {
        var entry = GTMaterialItems.TOOL_ITEMS.get(material, toolType);
        if (entry == null) return ItemStack.EMPTY;
        ItemStack stack = entry.get().getRaw();
        stack.update(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY, val -> val.withTooltip(false));
        stack.set(DataComponents.MAX_DAMAGE, maxDurability);
        GTTool toolComponent = new GTTool(Optional.of(toolSpeed), Optional.of(attackDamage), Optional.empty(),
                Optional.of(harvestLevel), Optional.empty());
        stack.set(GTDataComponents.GT_TOOL, toolComponent);
        return stack;
    }

    /**
     * AoE Block Breaking Routine.
     */
    public static boolean areaOfEffectBlockBreakRoutine(ItemStack stack, ServerPlayer player) {
        int currentDurability = stack.getDamageValue();
        int maximumDurability = stack.getMaxDamage();
        int remainingUses = maximumDurability - currentDurability;
        Set<BlockPos> harvestableBlocks = getHarvestableBlocks(stack, player);
        if (!harvestableBlocks.isEmpty()) {
            int blocksBroken = 0;
            for (BlockPos pos : harvestableBlocks) {
                if (!destroyBlock(player, stack, pos, blocksBroken++ == 0)) {
                    return true;
                }

                remainingUses--;
                if (stack.getItem() instanceof IGTTool gtTool && !gtTool.isElectric() && remainingUses == 0) {
                    return true;
                }
                // If the tool is an electric tool, catch the tool breaking and cancel the remaining AOE
                else if (!ItemStack.isSameItem(player.getMainHandItem(), stack)) {
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    @FunctionalInterface
    public interface AOEFunction {

        boolean apply(ItemStack stack, Level level, Player player, BlockPos start, UseOnContext context);
    }

    public static AoESymmetrical getAoEDefinition(ItemStack stack) {
        return stack.getOrDefault(GTDataComponents.AOE, AoESymmetrical.none());
    }

    public static Set<BlockPos> iterateAoE(ItemStack stack, AoESymmetrical aoeDefinition, Level world,
                                           Player player, HitResult hit, AOEFunction function) {
        if (aoeDefinition != null && !aoeDefinition.isNone() && hit instanceof BlockHitResult blockHit) {
            int column = aoeDefinition.column();
            int row = aoeDefinition.row();
            int layer = aoeDefinition.layer();
            Direction playerFacing = player.getDirection();
            Direction.Axis playerAxis = playerFacing.getAxis();
            Direction.Axis sideHitAxis = blockHit.getDirection().getAxis();
            Direction.AxisDirection sideHitAxisDir = blockHit.getDirection().getAxisDirection();
            Set<BlockPos> validPositions = new ObjectOpenHashSet<>();
            if (sideHitAxis.isVertical()) {
                boolean isX = playerAxis == Direction.Axis.X;
                boolean isDown = sideHitAxisDir == Direction.AxisDirection.NEGATIVE;
                for (int y = 0; y <= layer; y++) {
                    for (int x = isX ? -row : -column; x <= (isX ? row : column); x++) {
                        for (int z = isX ? -column : -row; z <= (isX ? column : row); z++) {
                            if (!(x == 0 && y == 0 && z == 0)) {
                                BlockPos pos = blockHit.getBlockPos().offset(x, isDown ? y : -y, z);
                                if (player.mayUseItemAt(pos.relative(blockHit.getDirection()), blockHit.getDirection(),
                                        stack)) {
                                    if (function.apply(stack, world, player, pos, new UseOnContext(player.level(),
                                            player, player.getUsedItemHand(), stack, blockHit))) {
                                        validPositions.add(pos);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                boolean isX = sideHitAxis == Direction.Axis.X;
                boolean isNegative = sideHitAxisDir == Direction.AxisDirection.NEGATIVE;
                for (int x = 0; x <= layer; x++) {
                    // Special case for any additional column > 1: https://i.imgur.com/Dvcx7Vg.png
                    // Same behaviour as the Flux Bore
                    for (int y = (row == 0 ? 0 : -1); y <= (row == 0 ? 0 : row * 2 - 1); y++) {
                        for (int z = -column; z <= column; z++) {
                            if (!(x == 0 && y == 0 && z == 0)) {
                                BlockPos pos = blockHit.getBlockPos().offset(
                                        isX ? (isNegative ? x : -x) : (isNegative ? z : -z), y,
                                        isX ? (isNegative ? z : -z) : (isNegative ? x : -x));
                                if (function.apply(stack, world, player, pos, new UseOnContext(player.level(), player,
                                        player.getUsedItemHand(), stack, blockHit))) {
                                    validPositions.add(pos);
                                }
                            }
                        }
                    }
                }
            }
            return validPositions;
        }
        return Collections.emptySet();
    }

    public static Set<BlockPos> getHarvestableBlocks(ItemStack stack, AoESymmetrical aoeDefinition, Level world,
                                                     Player player, HitResult rayTraceResult) {
        return iterateAoE(stack, aoeDefinition, world, player, rayTraceResult, ToolHelper::isBlockAoEHarvestable);
    }

    private static boolean isBlockAoEHarvestable(ItemStack stack, Level world, Player player, BlockPos pos,
                                                 UseOnContext context) {
        if (world.getBlockState(pos).isAir()) return false;

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof LiquidBlock) return false;

        BlockPos hitBlockPos = context.getClickedPos();
        BlockState hitBlockState = world.getBlockState(hitBlockPos);
        if (state.getDestroySpeed(world, pos) < 0 ||
                state.getDestroySpeed(world, pos) - hitBlockState.getDestroySpeed(world, hitBlockPos) > 8) {
            // If mining a block takes significantly longer than the center block, do not mine it.
            // Originally this was just a check for if it is at all harder of a block, however that
            // would cause some annoyances, like Grass Block not being broken if a Dirt Block was the
            // hit block for AoE. This value is somewhat arbitrary, but should cause things to feel
            // natural to mine, but avoid exploits like mining Obsidian quickly by instead targeting Stone.
            return false;
        }
        return stack.getItem().isCorrectToolForDrops(stack, state);
    }

    /**
     * Applies Forge Hammer recipes to block broken, used for hammers or tools with hard hammer enchant applied.
     */
    public static void applyHammerDropConversion(ServerLevel world, BlockPos pos, ItemStack tool, BlockState state,
                                                 List<ItemStack> drops, int fortune, float dropChance,
                                                 RandomSource random) {
        // || EnchantmentHelper.getEnchantmentLevel(EnchantmentHardHammer.INSTANCE, tool) > 0
        if (is(tool, GTToolType.HARD_HAMMER)) {
            List<ItemStack> silkTouchDrops = getSilkTouchDrop(world, pos, state);
            for (ItemStack silkTouchDrop : silkTouchDrops) {
                if (silkTouchDrop.isEmpty()) continue;
                // Stack lists can be immutable going into Recipe#matches barring no rewrites
                // Search for forge hammer recipes from all drops individually (only LV or under)

                DummyMachineBlockEntity be = new DummyMachineBlockEntity(GTValues.LV,
                        GTRecipeTypes.FORGE_HAMMER_RECIPES, GTMachineUtils.defaultTankSizeFunction,
                        Collections.emptyList());
                RecipeHandlerList dummyInputs = RecipeHandlerList.of(IO.IN,
                        new InfiniteEnergyContainer(be.getMetaMachine(), GTValues.V[GTValues.LV],
                                GTValues.V[GTValues.LV], 1, GTValues.V[GTValues.LV], 1),
                        new NotifiableItemStackHandler(be.getMetaMachine(), 1, IO.IN, IO.IN,
                                (slots) -> new CustomItemStackHandler(silkTouchDrop)));

                RecipeHandlerList dummyOutputs = RecipeHandlerList.of(IO.OUT,
                        new NotifiableItemStackHandler(be.getMetaMachine(), 2, IO.OUT));
                be.getMetaMachine().reinitializeHandlers(List.of(dummyInputs, dummyOutputs));

                Iterator<GTRecipe> hammerRecipes = GTRecipeTypes.FORGE_HAMMER_RECIPES.searchRecipe(be.metaMachine,
                        r -> RecipeHelper.matchContents(be.metaMachine, r).isSuccess());
                GTRecipe hammerRecipe = !hammerRecipes.hasNext() ? null : hammerRecipes.next();
                if (hammerRecipe != null && RecipeHelper.handleRecipeIO(be.metaMachine, hammerRecipe, IO.IN,
                        be.getMetaMachine().recipeLogic.getChanceCaches()).isSuccess()) {
                    drops.clear();
                    TagPrefix prefix = ChemicalHelper.getPrefix(silkTouchDrop.getItem());
                    if (prefix.isEmpty()) {
                        for (Content output : hammerRecipe.getOutputContents(ItemRecipeCapability.CAP)) {
                            if (dropChance >= 1.0F || random.nextFloat() <= dropChance) {
                                drops.add(ItemRecipeCapability.CAP.of(output.content).copy()
                                        .getItems()[0]);
                            }
                        }
                    } else if (TagPrefix.ORES.containsKey(prefix)) {
                        for (Content content : hammerRecipe.getOutputContents(ItemRecipeCapability.CAP)) {
                            if (dropChance >= 1.0F || random.nextFloat() <= dropChance) {
                                ItemStack output = ItemRecipeCapability.CAP.of(content.content).getItems()[0];
                                // Only apply fortune on ore -> crushed forge hammer recipes
                                if (ChemicalHelper.getPrefix(output.getItem()) == TagPrefix.crushed) {
                                    output = output.copy();
                                    if (fortune > 0) output.grow(random.nextInt(fortune));
                                    drops.add(output);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static final ThreadLocal<Boolean> DO_BLOCK_BREAK_SOUND_PARTICLES = ThreadLocal.withInitial(() -> true);
    public static final ThreadLocal<Boolean> IS_AOE_BREAKING_BLOCKS = ThreadLocal.withInitial(() -> false);

    public static boolean destroyBlock(ServerPlayer player, ItemStack tool, BlockPos pos, boolean playSound) {
        DO_BLOCK_BREAK_SOUND_PARTICLES.set(playSound);
        // This is *not* a vanilla/forge convention, Forge never added "shears" to ItemShear's tool classes.
        if (isTool(tool, GTToolType.SHEARS) && shearBlockRoutine(player, tool, pos) == 0) {
            return false;
        }
        Level level = player.level();

        // we set this flag when firing the event so the event listener that starts this whole thing doesn't cascade
        IS_AOE_BREAKING_BLOCKS.set(true);
        BlockState state = level.getBlockState(pos);
        var event = CommonHooks.fireBlockBreak(level, player.gameMode.getGameModeForPlayer(), player, pos, state);
        // ...and the easiest way to make sure it's false after is to set it before we return at all.
        IS_AOE_BREAKING_BLOCKS.set(false);
        if (event.isCanceled()) {
            return false;
        } else {
            Block block = state.getBlock();
            if (block instanceof GameMasterBlock && !player.canUseGameMasterBlocks()) {
                level.sendBlockUpdated(pos, state, state, 3);
                return false;
            } else if (player.blockActionRestricted(level, pos, player.gameMode.getGameModeForPlayer())) {
                return false;
            } else {
                BlockState newState = block.playerWillDestroy(level, pos, state, player);

                if (player.isCreative()) {
                    removeBlock(level, player, pos, newState, false);
                } else {
                    ItemStack toolCopy = tool.copy();
                    // previously player.hasCorrectToolForDrops(newState)
                    boolean canDropLoot = newState.canHarvestBlock(level, pos, player);
                    tool.mineBlock(level, newState, pos, player);
                    boolean wasActuallyBroken = removeBlock(level, player, pos, newState, canDropLoot);

                    if (canDropLoot && wasActuallyBroken) {
                        block.playerDestroy(level, player, pos, newState, level.getBlockEntity(pos), toolCopy);
                    }

                    // Neo: Fire the PlayerDestroyItemEvent if the tool was broken at any point during the break process
                    if (tool.isEmpty() && !toolCopy.isEmpty()) {
                        EventHooks.onPlayerDestroyItem(player, toolCopy, InteractionHand.MAIN_HAND);
                    }
                }
                return true;
            }
        }
    }

    public static boolean removeBlock(Level level, ServerPlayer player, BlockPos pos, BlockState state, boolean canDropLoot) {
        boolean removed = state.onDestroyedByPlayer(level, pos, player, canDropLoot, level.getFluidState(pos));
        if (removed) {
            state.getBlock().destroy(level, pos, state);
        }
        return removed;
    }

    public static Set<BlockPos> getHarvestableBlocks(ItemStack stack, Level world, Player player,
                                                     HitResult rayTraceResult) {
        return getHarvestableBlocks(stack, getAoEDefinition(stack), world, player, rayTraceResult);
    }

    public static Set<BlockPos> getHarvestableBlocks(ItemStack stack, Player player) {
        if (!hasBehaviorsComponent(stack)) return Collections.emptySet();

        AoESymmetrical aoeDefinition = getAoEDefinition(stack);
        if (aoeDefinition.isNone()) {
            return Collections.emptySet();
        }

        HitResult hit = getPlayerDefaultRaytrace(player);
        return getHarvestableBlocks(stack, aoeDefinition, player.level(), player, hit);
    }

    public static HitResult getPlayerDefaultRaytrace(@NotNull Player player) {
        return player.pick(player.blockInteractionRange(), 0.0f, false);
    }

    /**
     * Can be called to do a default set of "successful use" actions.
     * Damages the item, plays the tool sound (if available), and swings the player's arm.
     *
     * @param player the player clicking the item
     * @param world  the world in which the click happened
     * @param hand   the hand holding the item
     */
    public static void onActionDone(@NotNull Player player, @NotNull Level world, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        IGTTool tool = (IGTTool) stack.getItem();
        ToolHelper.damageItem(stack, player);
        if (tool.getSound() != null) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), tool.getSound().getMainEvent(),
                    SoundSource.PLAYERS, 1.0F,
                    1.0F);
        }
        player.swing(hand);
    }

    @NotNull
    public static Set<GTToolType> getToolTypes(final ItemStack tool) {
        Set<GTToolType> types = new HashSet<>();
        if (tool.getItem() instanceof IGTTool gtTool) {
            return gtTool.getToolClasses(tool);
        }
        for (GTToolType toolType : GTToolType.getTypes().values()) {
            if (toolType.itemTags.stream().anyMatch(tool::is)) types.add(toolType);
            if (toolType.defaultAbilities.stream().anyMatch(tool::canPerformAction)) types.add(toolType);
        }
        GTItemAbilities.DEFAULT_TYPE_ASSOCIATIONS.forEach((action, type) -> {
            if (tool.canPerformAction(action)) {
                types.add(type);
            }
        });
        return types;
    }

    /**
     * @return if any of the specified tool classes exists in the tool
     */
    public static boolean isTool(ItemStack tool, GTToolType... toolClasses) {
        for (GTToolType toolType : toolClasses) {
            if (toolType.itemTags.stream().anyMatch(tool::is)) return true;
        }

        if (tool.getItem() instanceof IGTTool igtTool) {
            if (toolClasses.length == 1) {
                return igtTool.getToolClasses(tool).contains(toolClasses[0]);
            }
            for (GTToolType toolClass : igtTool.getToolClasses(tool)) {
                for (GTToolType specified : toolClasses) {
                    if (toolClass.equals(specified)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // encompasses all vanilla special case tool checks for harvesting
    public static boolean isToolEffective(ItemStack stack, BlockState state, Set<GTToolType> toolClasses, int harvestLevel) {
        Tool tool = stack.get(DataComponents.TOOL);
        return tool != null && tool.isCorrectForDrops(state);
    }

    /**
     * Damages tools in a context where the tool had been used to craft something.
     * This supports both vanilla-esque and GT tools in case it does get called on a vanilla-esque tool
     *
     * @param stack  stack to be damaged
     * @param entity entity that has damaged this stack
     */
    public static void damageItemWhenCrafting(@NotNull ItemStack stack, @Nullable LivingEntity entity) {
        int damage = 2;
        if (stack.getItem() instanceof IGTTool) {
            damage = ((IGTTool) stack.getItem()).getToolStats().getToolDamagePerCraft(stack);
        } else {
            if (stack.getTags().anyMatch(s -> s.location().getPath().startsWith("tool") ||
                    s.location().getPath().startsWith("crafting_tool"))) {
                damage = 1;
            }
        }
        damageItem(stack, entity, damage);
    }

    /**
     * Damages tools appropriately.
     * This supports both vanilla-esque and GT tools in case it does get called on a vanilla-esque tool.
     * <p>
     * This method only takes 1 durability off, it ignores the tool's effectiveness because of the lack of context.
     *
     * @param stack  stack to be damaged
     * @param entity entity that has damaged this stack
     */
    public static void damageItem(@NotNull ItemStack stack, @Nullable LivingEntity entity) {
        damageItem(stack, entity, 1);
    }

    /**
     * Special cases for vanilla destroy speed changes.
     * If return -1, no special case was found, and some other method
     * should be used to determine the destroy speed.
     */
    public static float getDestroySpeed(BlockState state, Set<GTToolType> toolClasses) {
        if (toolClasses.contains(GTToolType.SWORD)) {
            Block block = state.getBlock();
            if (block instanceof WebBlock) {
                return 15.0F;
            }
        }
        return -1;
    }

    /**
     * Shearing a Block.
     *
     * @return -1 if not shearable, otherwise return 0 or 1, 0 if tool is now broken.
     */
    public static int shearBlockRoutine(ServerPlayer player, ItemStack tool, BlockPos pos) {
        if (!player.isCreative()) {
            Level world = player.serverLevel();
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof IShearable shearable) {
                if (shearable.isShearable(player, tool, world, pos)) {
                    List<ItemStack> shearedDrops = shearable.onSheared(player, tool, world, pos);
                    boolean relocateMinedBlocks = tool.has(GTDataComponents.RELOCATE_MINED_BLOCKS);
                    Iterator<ItemStack> iter = shearedDrops.iterator();
                    while (iter.hasNext()) {
                        ItemStack stack = iter.next();
                        if (relocateMinedBlocks && player.addItem(stack)) {
                            iter.remove();
                        } else {
                            float f = 0.7F;
                            double xo = world.random.nextFloat() * f + 0.15D;
                            double yo = world.random.nextFloat() * f + 0.15D;
                            double zo = world.random.nextFloat() * f + 0.15D;
                            ItemEntity entityItem = new ItemEntity(world, pos.getX() + xo, pos.getY() + yo,
                                    pos.getZ() + zo, stack);
                            entityItem.setDefaultPickUpDelay();
                            world.addFreshEntity(entityItem);
                        }
                    }
                    ToolHelper.damageItem(tool, player, 1);
                    player.awardStat(Stats.BLOCK_MINED.get((Block) shearable));
                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
                    return tool.isEmpty() ? 0 : 1;
                }
            }
        }
        return -1;
    }

    // Suppliers for broken tool stacks
    public static final Supplier<ItemStack> SUPPLY_POWER_UNIT_LV = () -> GTItems.POWER_UNIT_LV.get()
            .getDefaultInstance();
    public static final Supplier<ItemStack> SUPPLY_POWER_UNIT_MV = () -> GTItems.POWER_UNIT_MV.get()
            .getDefaultInstance();
    public static final Supplier<ItemStack> SUPPLY_POWER_UNIT_HV = () -> GTItems.POWER_UNIT_HV.get()
            .getDefaultInstance();
    public static final Supplier<ItemStack> SUPPLY_POWER_UNIT_EV = () -> GTItems.POWER_UNIT_EV.get()
            .getDefaultInstance();
    public static final Supplier<ItemStack> SUPPLY_POWER_UNIT_IV = () -> GTItems.POWER_UNIT_IV.get()
            .getDefaultInstance();

    /**
     * @param state the BlockState of the block
     * @return the silk touch drop
     */
    @NotNull
    public static List<ItemStack> getSilkTouchDrop(ServerLevel level, BlockPos origin, @NotNull BlockState state) {
        ItemStack tool = GTMaterialItems.TOOL_ITEMS.get(GTMaterials.Neutronium, GTToolType.PICKAXE).get().get();
        // oh wow, this exists now. cool!
        EnchantmentHelper.enchantItemFromProvider(
                tool,
                level.registryAccess(),
                GTEnchantmentProviders.SILK_TOUCH,
                level.getCurrentDifficultyAt(origin),
                level.getRandom()
        );

        return state.getDrops(new LootParams.Builder(level).withParameter(LootContextParams.BLOCK_STATE, state)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(origin))
                .withParameter(LootContextParams.TOOL, tool));
    }
}
