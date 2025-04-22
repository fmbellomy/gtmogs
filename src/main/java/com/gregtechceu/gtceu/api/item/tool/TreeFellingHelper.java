package com.gregtechceu.gtceu.api.item.tool;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.gregtechceu.gtceu.data.tools.GTToolBehaviors;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.*;
import java.util.stream.Collectors;

import static com.gregtechceu.gtceu.api.item.tool.ToolHelper.*;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = GTCEu.MOD_ID)
public class TreeFellingHelper {

    private final ServerPlayer player;
    private final ItemStack tool;
    private final Deque<BlockPos> orderedBlocks;
    private int tick;

    public static final List<TreeFellingHelper> helpers = ObjectArrayList.of();

    private TreeFellingHelper(ServerPlayer player, ItemStack tool, Deque<BlockPos> orderedBlocks) {
        this.player = player;
        this.tool = tool;
        this.orderedBlocks = orderedBlocks;
        tick = 0;
        helpers.add(this);
    }

    public static void fellTree(ItemStack stack, Level level, BlockState origin, BlockPos originPos,
                                LivingEntity miner) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        Queue<BlockPos> checking = new ArrayDeque<>();
        Set<BlockPos> visited = new ObjectOpenHashSet<>();

        checking.add(originPos);

        while (!checking.isEmpty()) {
            BlockPos check = checking.remove();
            if (check != originPos) {
                visited.add(check);
            }
            for (int y = 0; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x != 0 || y != 0 || z != 0) {
                            mutablePos.set(check.getX() + x, check.getY() + y, check.getZ() + z);
                            if (!visited.contains(mutablePos)) {
                                // Check that the found block matches the original block state, which is wood.
                                if (origin.getBlock() == level.getBlockState(mutablePos).getBlock()) {
                                    if (!checking.contains(mutablePos)) {
                                        BlockPos immutablePos = mutablePos.immutable();
                                        checking.add(immutablePos);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!visited.isEmpty() && miner instanceof ServerPlayer serverPlayer) {
            Deque<BlockPos> orderedBlocks = visited.stream()
                    .sorted(Comparator.comparingInt(pos -> pos.getY() - originPos.getY()))
                    .collect(Collectors.toCollection(LinkedList::new));
            new TreeFellingHelper(serverPlayer, stack, orderedBlocks);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Pre event) {
        if (!event.getLevel().isClientSide && !helpers.isEmpty()) {
            var iterator = helpers.iterator();
            while (iterator.hasNext()) {
                var helper = iterator.next();
                if (event.getLevel() == helper.player.level()) {
                    ItemStack held = helper.player.getMainHandItem();
                    if (helper.orderedBlocks.isEmpty() || helper.tool.isEmpty() ||
                            !getBehaviorsComponent(held).hasBehavior(GTToolBehaviors.TREE_FELLING)) {
                        iterator.remove();
                        continue;
                    }
                    if (helper.tick % ConfigHolder.INSTANCE.tools.treeFellingDelay == 0) {
                        ToolHelper.destroyBlock(helper.player, helper.tool, helper.orderedBlocks.removeLast(), true);
                    }
                    helper.tick++;
                }
            }
        }
    }
}
