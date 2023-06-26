package com.teamresourceful.resourcefulbees.common.blocks.base;

import com.teamresourceful.resourcefulbees.platform.common.menu.ContentMenuProvider;
import com.teamresourceful.resourcefulbees.platform.common.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class BeeHouseTopBlock extends Block {

    public static final VoxelShape TOP_Z_SHAPE = Stream.of(
            Block.box(1, 0, 0, 15, 2, 16),
            Block.box(0, -3, 0, 16, 0, 16),
            Block.box(1, -16, 1, 15, -3, 15),
            Block.box(3, 2, 0, 13, 4, 16),
            Block.box(5, 4, 0, 11, 6, 16),
            Block.box(7, 6, -1, 9, 8, 17)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape TOP_X_SHAPE = Stream.of(
            Block.box(0, 0, 1, 16, 2, 15),
            Block.box(0, -3, 0, 16, 0, 16),
            Block.box(1, -16, 1, 15, -3, 15),
            Block.box(0, 2, 3, 16, 4, 13),
            Block.box(0, 4, 5, 16, 6, 11),
            Block.box(-1, 6, 7, 17, 8, 9)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public BeeHouseTopBlock() {
        super(Properties.of().sound(SoundType.WOOD).strength(5f, 6f).pushReaction(PushReaction.BLOCK));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        if (!player.isShiftKeyDown() && !world.isClientSide) {
            BlockPos posBelow = pos.below();
            BlockState stateBelow = world.getBlockState(posBelow);
            MenuProvider blockEntity = stateBelow.getMenuProvider(world,posBelow);
            if (blockEntity instanceof ContentMenuProvider<?> contentMenu) {
                contentMenu.openMenu((ServerPlayer) player);
            } else if (blockEntity != null) {
                ModUtils.openScreen(player, blockEntity, posBelow);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        if (level.getBlockState(pos.below()).getBlock() instanceof BeeHouseBlock) level.destroyBlock(pos.below(), true);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos neighborPos, boolean moving) {
        if (!(level.getBlockState(pos.below()).getBlock() instanceof BeeHouseBlock))
            level.removeBlock(pos, false);
        super.neighborChanged(state, level, pos, block, neighborPos, moving);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.hasProperty(FACING) && state.getValue(FACING).getAxis().equals(Direction.Axis.Z) ? TOP_Z_SHAPE : TOP_X_SHAPE;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public @NotNull ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, @NotNull BlockState state) {
        if (level.getBlockState(pos.below()).getBlock() instanceof BeeHouseBlock block) {
            return block.getCloneItemStack(level, pos, state);
        }
        return Items.AIR.getDefaultInstance();
    }
}
