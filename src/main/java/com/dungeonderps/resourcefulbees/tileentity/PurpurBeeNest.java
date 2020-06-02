package com.dungeonderps.resourcefulbees.tileentity;

import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

public class PurpurBeeNest extends BeeNestEntity{
    @Nonnull
    @Override
    public TileEntityType<?> getType() {
        return RegistryHandler.PURPUR_BEE_NEST_ENTITY.get();
    }

    @Override
    public boolean isAllowedBee() {
        Block hive = getBlockState().getBlock();
        return hive == RegistryHandler.PURPUR_BEE_NEST.get();
    }
}
