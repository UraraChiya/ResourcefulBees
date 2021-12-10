package com.teamresourceful.resourcefulbees.common.inventory.containers;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class UnvalidatedApiaryContainer extends AbstractContainerMenu {

    private final ApiaryTileEntity apiaryTileEntity;
    private final BlockPos pos;

    public UnvalidatedApiaryContainer(int id, Level world, BlockPos pos, Inventory inv) {
        super(ModContainers.UNVALIDATED_APIARY_CONTAINER.get(), id);
        this.pos = pos;
        this.apiaryTileEntity = (ApiaryTileEntity)world.getBlockEntity(pos);
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return true;
    }


    public ApiaryTileEntity getApiaryTileEntity() {
        return apiaryTileEntity;
    }

    public BlockPos getPos() {
        return pos;
    }
}
