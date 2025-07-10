package com.gregtechceu.gtceu.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import static com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties.ACTIVE;

public class ActiveBlock extends AppearanceBlock {

    public ActiveBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    /**
     * @deprecated Use {@link BlockState#setValue(Property, Comparable)}
     */
    @ApiStatus.Obsolete(since = "7.0.0")
    public BlockState changeActive(BlockState state, boolean active) {
        if (state.is(this)) {
            return state.setValue(ACTIVE, active);
        }
        return state;
    }

    /**
     * @deprecated Use {@link BlockState#getValue(Property)}
     */
    @ApiStatus.Obsolete(since = "7.0.0")
    public boolean isActive(BlockState state) {
        return state.getValue(ACTIVE);
    }

    @Override
    public BlockState getBlockAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side,
                                         @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return defaultBlockState();
    }
}
