package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeItemOutputEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.roguelogix.phosphophyllite.multiblock2.IAssemblyStateBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CentrifugeItemOutput extends AbstractGUICentrifuge implements IAssemblyStateBlock {

    private final Supplier<BlockEntityType<CentrifugeItemOutputEntity>> entityType;

    public CentrifugeItemOutput(@NotNull Properties properties, Supplier<BlockEntityType<CentrifugeItemOutputEntity>> entityType) {
        super(properties);
        this.entityType = entityType;
        this.registerDefaultState(defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void buildStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.buildStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return entityType.get().create(pos, state);
    }
}
