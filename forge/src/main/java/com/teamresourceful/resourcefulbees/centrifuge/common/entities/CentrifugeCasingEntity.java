package com.teamresourceful.resourcefulbees.centrifuge.common.entities;

import com.teamresourceful.resourcefulbees.centrifuge.common.entities.base.AbstractCentrifugeEntity;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CentrifugeCasingEntity extends AbstractCentrifugeEntity {
    public CentrifugeCasingEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CENTRIFUGE_CASING_ENTITY.get(), pos, state);
    }
}