package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.containers;

import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.blocks.CentrifugeTerminal;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.entities.CentrifugeTerminalEntity;
import com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states.CentrifugeState;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.roguelogix.phosphophyllite.gui.GuiSync;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CentrifugeTerminalContainer extends CentrifugeContainer<CentrifugeTerminalEntity> implements GuiSync.IGUIPacketProvider {

    public CentrifugeTerminalContainer(int id, PlayerInventory inv, PacketBuffer buffer) {
        this(id, inv, getTileFromBuf(buffer, CentrifugeTerminalEntity.class));
    }

    public CentrifugeTerminalContainer(int id, PlayerInventory inv, CentrifugeTerminalEntity entity) {
        super(ModContainers.CENTRIFUGE_TERMINAL_CONTAINER.get(), id, inv, entity);
        this.getGuiPacket();
    }

    @Override
    public boolean stillValid(@NotNull PlayerEntity player) {
        return entity != null && IWorldPosCallable.create(level, entity.getBlockPos()).evaluate((world, pos) ->
                world.getBlockState(pos).getBlock() instanceof CentrifugeTerminal && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    public int getContainerInputEnd() {
        return 0;
    }

    @Override
    public int getInventoryStart() {
        return 0;
    }

    @Override
    public int getContainerInputStart() {
        return 0;
    }

    @Override
    protected void setupSlots() {
        //addPlayerInvSlots();
    }

    @Nullable
    @Override
    public GuiSync.IGUIPacket getGuiPacket() {
        return this.entity == null ?  new CentrifugeState(null) : this.entity.getState();
    }

    @Override
    public void runRequest(@NotNull String requestName, @Nullable Object requestData) {
        GuiSync.IGUIPacketProvider.super.runRequest(requestName, requestData);
    }

    @Override
    public void executeRequest(String requestName, Object requestData) {
        GuiSync.IGUIPacketProvider.super.executeRequest(requestName, requestData);
    }
}