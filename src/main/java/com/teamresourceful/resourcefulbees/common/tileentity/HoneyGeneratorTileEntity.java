package com.teamresourceful.resourcefulbees.common.tileentity;

import com.teamresourceful.resourcefulbees.common.block.HoneyGenerator;
import com.teamresourceful.resourcefulbees.common.capabilities.CustomEnergyStorage;
import com.teamresourceful.resourcefulbees.common.capabilities.HoneyFluidTank;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import com.teamresourceful.resourcefulbees.common.inventory.AutomationSensitiveItemStackHandler;
import com.teamresourceful.resourcefulbees.common.inventory.containers.HoneyGeneratorContainer;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModTags;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.TranslationConstants;
import com.teamresourceful.resourcefulbees.common.network.NetPacketHandler;
import com.teamresourceful.resourcefulbees.common.network.packets.SyncGUIMessage;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class HoneyGeneratorTileEntity extends TileEntity implements ITickableTileEntity, ISyncableGUI {

    public static final int HONEY_BOTTLE_INPUT = 0;
    public static final int BOTTLE_OUTPUT = 1;
    public static final int HONEY_DRAIN_AMOUNT = CommonConfig.HONEY_DRAIN_AMOUNT.get();
    public static final int ENERGY_FILL_AMOUNT = CommonConfig.ENERGY_FILL_AMOUNT.get();
    public static final int ENERGY_TRANSFER_AMOUNT = CommonConfig.ENERGY_TRANSFER_AMOUNT.get();
    public static final int MAX_ENERGY_CAPACITY = CommonConfig.MAX_ENERGY_CAPACITY.get();
    public static final int MAX_TANK_STORAGE = CommonConfig.MAX_TANK_STORAGE.get();


    private final HoneyGeneratorTileEntity.TileStackHandler tileStackHandler = new HoneyGeneratorTileEntity.TileStackHandler(5, (slot, stack, automation) -> !automation || slot == 0, (slot, automation) -> !automation || slot == 1);
    private final CustomEnergyStorage energyStorage = createEnergy();
    private final HoneyFluidTank tank = new HoneyFluidTank(MAX_TANK_STORAGE);

    private final LazyOptional<IItemHandler> inventoryOptional = LazyOptional.of(this::getTileStackHandler);
    private final LazyOptional<IEnergyStorage> energyOptional = LazyOptional.of(() -> energyStorage);
    private final LazyOptional<FluidTank> tankOptional = LazyOptional.of(() -> tank);

    public static final ITag<Fluid> HONEY_FLUID_TAG = ModTags.Fluids.HONEY;
    public static final ITag<Item> HONEY_BOTTLE_TAG = ModTags.Items.HONEY_BOTTLES;

    private int fluidFilled;
    private int energyFilled;
    private boolean isProcessing;

    public HoneyGeneratorTileEntity() {
        super(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY.get());
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide) {
            if (!tank.isEmpty() && this.canProcessEnergy()) {
                this.processEnergy();
            }
            if (!isProcessing && !this.canProcessEnergy()) {
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(HoneyGenerator.PROPERTY_ON, false));
            }
        }
        sendOutPower();
    }

    private void sendOutPower() {
        if (getStoredEnergy() > 0 && level != null) {
            Arrays.stream(Direction.values())
                    .map(direction -> Pair.of(level.getBlockEntity(worldPosition.relative(direction)), direction))
                    .filter(pair -> pair.getLeft() != null && pair.getRight() != null)
                    .forEach(this::transferEnergy);
        }
    }

    private void transferEnergy(Pair<TileEntity, Direction> tileEntityDirectionPair) {
        tileEntityDirectionPair.getLeft().getCapability(CapabilityEnergy.ENERGY, tileEntityDirectionPair.getRight().getOpposite())
                .filter(IEnergyStorage::canReceive)
                .ifPresent(this::transferEnergy);
    }

    private void transferEnergy(IEnergyStorage handler) {
        if (getStoredEnergy() > 0) {
            int received = handler.receiveEnergy(Math.min(getStoredEnergy(), ENERGY_TRANSFER_AMOUNT), false);
            energyStorage.consumeEnergy(received);
        }
    }

    private int getStoredEnergy() {
        return energyStorage.getEnergyStored();
    }

    public boolean canProcessEnergy() {
        return getStoredEnergy() + ENERGY_FILL_AMOUNT <= energyStorage.getMaxEnergyStored() && tank.getFluidAmount() >= HONEY_DRAIN_AMOUNT;
    }

    private void processEnergy() {
        if (this.canProcessEnergy()) {
            tank.drain(HONEY_DRAIN_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
            energyStorage.addEnergy(ENERGY_FILL_AMOUNT);
            setEnergyFilled(getEnergyFilled() + ENERGY_FILL_AMOUNT);
            if (getEnergyFilled() >= ENERGY_FILL_AMOUNT) setEnergyFilled(0);
            assert level != null : "World is null?";
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(HoneyGenerator.PROPERTY_ON, true));
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("tank", tank.writeToNBT(new CompoundNBT()));
        nbt.put("power", energyStorage.serializeNBT());
        return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        tank.readFromNBT(nbt.getCompound("tank"));
        energyStorage.deserializeNBT(nbt.getCompound("power"));
    }

    @NotNull
    @Override
    public CompoundNBT save(@NotNull CompoundNBT tag) {
        CompoundNBT inv = this.getTileStackHandler().serializeNBT();
        tag.put(NBTConstants.NBT_INVENTORY, inv);
        tag.put(NBTConstants.NBT_ENERGY, energyStorage.serializeNBT());
        tag.put(NBTConstants.NBT_TANK, tank.writeToNBT(new CompoundNBT()));
        tag.putInt(NBTConstants.NBT_ENERGY_FILLED, getEnergyFilled());
        tag.putInt(NBTConstants.NBT_FLUID_FILLED, getFluidFilled());
        tag.putBoolean(NBTConstants.NBT_IS_PROCESSING, isProcessing);
        return super.save(tag);
    }

    @NotNull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        save(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound(NBTConstants.NBT_INVENTORY);
        getTileStackHandler().deserializeNBT(invTag);
        energyStorage.deserializeNBT(tag.getCompound(NBTConstants.NBT_ENERGY));
        tank.readFromNBT(tag.getCompound(NBTConstants.NBT_TANK));
        if (tag.contains(NBTConstants.NBT_ENERGY_FILLED)) setEnergyFilled(tag.getInt(NBTConstants.NBT_ENERGY_FILLED));
        if (tag.contains(NBTConstants.NBT_FLUID_FILLED)) setFluidFilled(tag.getInt(NBTConstants.NBT_FLUID_FILLED));
        if (tag.contains(NBTConstants.NBT_IS_PROCESSING)) isProcessing = tag.getBoolean(NBTConstants.NBT_IS_PROCESSING);
        super.load(state, tag);
    }

    @Override
    public void handleUpdateTag(@NotNull BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) return inventoryOptional.cast();
        if (cap.equals(CapabilityEnergy.ENERGY)) return energyOptional.cast();
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) return tankOptional.cast();
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        this.inventoryOptional.invalidate();
        this.energyOptional.invalidate();
        this.tankOptional.invalidate();
    }

    @Nullable
    @Override
    public Container createMenu(int id, @NotNull PlayerInventory playerInventory, @NotNull PlayerEntity playerEntity) {
        //noinspection ConstantConditions
        return new HoneyGeneratorContainer(id, level, worldPosition, playerInventory);
    }

    @NotNull
    @Override
    public ITextComponent getDisplayName() {
        return TranslationConstants.Guis.GENERATOR;
    }

    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(MAX_ENERGY_CAPACITY, 0, ENERGY_TRANSFER_AMOUNT) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    public void sendGUINetworkPacket(IContainerListener player) {
        if (player instanceof ServerPlayerEntity && (!(player instanceof FakePlayer))) {
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeFluidStack(tank.getFluid());
            buffer.writeInt(energyStorage.getEnergyStored());
            NetPacketHandler.sendToPlayer(new SyncGUIMessage(this.worldPosition, buffer), (ServerPlayerEntity) player);
        }
    }

    public void handleGUINetworkPacket(PacketBuffer buffer) {
        tank.setFluid(buffer.readFluidStack());
        energyStorage.setEnergy(buffer.readInt());
    }


    public @NotNull TileStackHandler getTileStackHandler() {
        return tileStackHandler;
    }

    public int getFluidFilled() {
        return fluidFilled;
    }

    public void setFluidFilled(int fluidFilled) {
        this.fluidFilled = fluidFilled;
    }

    public int getEnergyFilled() {
        return energyFilled;
    }

    public void setEnergyFilled(int energyFilled) {
        this.energyFilled = energyFilled;
    }

    public HoneyFluidTank getTank() {
        return tank;
    }

    public CustomEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public class TileStackHandler extends AutomationSensitiveItemStackHandler {
        protected TileStackHandler(int slots, IAcceptor acceptor, IRemover remover) {
            super(slots, acceptor, remover);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (stack.getItem() instanceof BucketItem) {
                BucketItem bucket = (BucketItem) stack.getItem();
                return bucket.getFluid().is(HONEY_FLUID_TAG);
            } else {
                return stack.getItem().is(HONEY_BOTTLE_TAG);
            }
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    }
}
