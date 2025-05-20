package com.gregtechceu.gtceu.api.multiblock;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.multiblock.error.PatternError;
import com.gregtechceu.gtceu.api.multiblock.error.PatternStringError;
import com.gregtechceu.gtceu.api.multiblock.predicates.SimplePredicate;
import com.gregtechceu.gtceu.api.multiblock.util.PatternMatchContext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.stream.Collectors;

public class MultiblockState {

    public final static PatternError UNLOAD_ERROR = new PatternStringError("multiblocked.pattern.error.chunk");
    public final static PatternError UNINIT_ERROR = new PatternStringError("multiblocked.pattern.error.init");

    private BlockPos pos;
    private BlockState blockState;
    private BlockEntity blockEntity;
    private boolean blockEntityInitialized;
    @Getter
    private final PatternMatchContext matchContext;
    @Getter
    private Object2IntOpenHashMap<SimplePredicate> globalCount;
    @Getter
    private Object2IntOpenHashMap<SimplePredicate> layerCount;
    public TraceabilityPredicate predicate;
    public IO io;
    public PatternError error;
    @Getter
    @Setter
    private boolean neededFlip = false;
    public final Level world;
    public final BlockPos controllerPos;
    public IMultiController lastController;

    // persist
    public LongOpenHashSet cache;

    public MultiblockState(Level world, BlockPos controllerPos) {
        this.world = world;
        this.controllerPos = controllerPos;
        this.error = UNINIT_ERROR;
        this.matchContext = new PatternMatchContext();
    }

    protected void clean() {
        this.matchContext.reset();
        this.globalCount = new Object2IntOpenHashMap<>();
        this.layerCount = new Object2IntOpenHashMap<>();
        cache = new LongOpenHashSet();
    }

    protected boolean update(BlockPos posIn, TraceabilityPredicate predicate) {
        this.pos = posIn;
        this.blockState = null;
        this.blockEntity = null;
        this.blockEntityInitialized = false;
        this.predicate = predicate;
        this.error = null;
        if (!world.isLoaded(posIn)) {
            error = UNLOAD_ERROR;
            return false;
        }
        return true;
    }

    public IMultiController getController() {
        if (world.isLoaded(controllerPos)) {
            if (world.getBlockEntity(controllerPos) instanceof IMachineBlockEntity machineBlockEntity &&
                    machineBlockEntity.getMetaMachine() instanceof IMultiController controller) {
                return lastController = controller;
            }
        } else {
            error = UNLOAD_ERROR;
        }
        return null;
    }

    public boolean hasError() {
        return error != null;
    }

    public void setError(PatternError error) {
        this.error = error;
        if (error != null) {
            error.setWorldState(this);
        }
    }

    public BlockState getBlockState() {
        if (this.blockState == null) {
            this.blockState = this.world.getBlockState(this.pos);
        }
        if (this.blockState == null) {
            GTCEu.LOGGER.error("could not get BlockState at " + this.pos + " in MultiblockState");
        }
        return this.blockState;
    }

    @Nullable
    public BlockEntity getBlockEntity() {
        if (!getBlockState().hasBlockEntity()) {
            return null;
        }
        if (this.blockEntity == null && !this.blockEntityInitialized) {
            this.blockEntity = this.world.getBlockEntity(this.pos);
            this.blockEntityInitialized = true;
        }

        return this.blockEntity;
    }

    public BlockPos getPos() {
        return this.pos.immutable();
    }

    public BlockState getOffsetState(Direction face) {
        if (pos instanceof BlockPos.MutableBlockPos) {
            ((BlockPos.MutableBlockPos) pos).move(face);
            BlockState blockState = world.getBlockState(pos);
            ((BlockPos.MutableBlockPos) pos).move(face.getOpposite());
            return blockState;
        }
        return world.getBlockState(this.pos.relative(face));
    }

    public Level getWorld() {
        return world;
    }

    public void addPosCache(BlockPos pos) {
        cache.add(pos.asLong());
    }

    public boolean isPosInCache(BlockPos pos) {
        return cache.contains(pos.asLong());
    }

    public Collection<BlockPos> getCache() {
        return cache.stream().map(BlockPos::of).collect(Collectors.toList());
    }

    public void onBlockStateChanged(BlockPos pos, BlockState state) {
        if (world instanceof ServerLevel serverLevel) {
            if (pos.equals(controllerPos)) {
                if (lastController != null) {
                    if (!state.is(lastController.self().getBlockState().getBlock())) {
                        lastController.onStructureInvalid();
                        var mwsd = MultiblockWorldSavedData.getOrCreate(serverLevel);
                        mwsd.removeMapping(this);
                    }
                }
            } else {
                IMultiController controller = getController();
                if (controller != null) {
                    if (controller.isFormed() && state.getBlock() instanceof ActiveBlock) {
                        LongSet activeBlocks = getMatchContext().getOrDefault("vaBlocks", LongSets.emptySet());
                        if (activeBlocks.contains(pos.asLong())) {
                            // fine! it's caused by active blocks.
                            // speed up here!
                            return;
                        }
                    }
                    if (controller.checkPatternWithLock()) {
                        // refresh structure
                        controller.self().setFlipped(this.neededFlip);
                        controller.onStructureFormed();
                    } else {
                        // invalid structure
                        controller.self().setFlipped(false);
                        controller.onStructureInvalid();
                        var mwsd = MultiblockWorldSavedData.getOrCreate(serverLevel);
                        mwsd.removeMapping(this);
                        mwsd.addAsyncLogic(controller);
                    }
                }
            }
        }
    }
}
