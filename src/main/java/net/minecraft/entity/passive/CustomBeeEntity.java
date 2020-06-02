package net.minecraft.entity.passive;

import com.dungeonderps.resourcefulbees.ResourcefulBees;
import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.data.BeeData;
import com.dungeonderps.resourcefulbees.entity.ICustomBee;
import com.dungeonderps.resourcefulbees.lib.BeeConst;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import com.dungeonderps.resourcefulbees.utils.MathUtils;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("EntityConstructor")
public class CustomBeeEntity extends BeeEntity implements ICustomBee {

    private static final DataParameter<String> BEE_COLOR = EntityDataManager.createKey(CustomBeeEntity.class, DataSerializers.STRING);
    private static final DataParameter<String> BEE_TYPE = EntityDataManager.createKey(CustomBeeEntity.class, DataSerializers.STRING);
    private static final DataParameter<Float> SIZE_MODIFIER = EntityDataManager.createKey(CustomBeeEntity.class, DataSerializers.FLOAT);

    private boolean remove;
    private int ticksCounted;

    public CustomBeeEntity(EntityType<? extends BeeEntity> type, World world) {
        super(type, world);
    }

    //**************************** BEE INFO RELATED METHODS BELOW *******************************************

    public void setBeeType(boolean fromBiome){
        Biome curBiome = this.world.getBiome(this.getPosition());
        String bee = fromBiome ? BeeInfo.getRandomBee(curBiome) : BeeInfo.getRandomBee();
        this.dataManager.set(BEE_TYPE, bee);
    }

    public void setBeeType(String beeType){
        this.dataManager.set(BEE_TYPE, getNameFromInfo(beeType));
    }

    public String getBeeType() {
        BeeData info = getBeeInfo(this.dataManager.get(BEE_TYPE));
        if (info.getName().equals(BeeConst.DEFAULT_BEE_TYPE)) {
            markRemove();
        }
        return this.dataManager.get(BEE_TYPE);
    }

    @Override
    public Float getSizeModifierFromInfo(String beeType) {
        return MathUtils.clamp(getBeeInfo(getBeeType()).getSizeModifier(), 0.5f, 2f);
    }

    public String getColorFromInfo(String beeType) {
        return getBeeInfo(beeType).getHoneycombColor();
    }

    public String getNameFromInfo(String beeType) {
        return getBeeInfo(beeType).getName();
    }

    public BeeData getBeeInfo() {
        return BeeInfo.getInfo(this.getBeeType());
    }

    public BeeData getBeeInfo(String beeType) {
        return BeeInfo.getInfo(beeType);
    }

    //***************************** CUSTOM BEE RELATED METHODS BELOW *************************************************

    protected ITextComponent getProfessionName() {
        return new TranslationTextComponent("entity" + '.' + ResourcefulBees.MOD_ID + '.' + this.getBeeType() + "_bee");
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.isFireDamage() && (getBeeInfo().isNetherBee() || getBeeInfo().isBlazeBee()))
            return false;
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        if (getBeeInfo().isWitherBee() && potioneffectIn.getPotion().equals(Effects.WITHER))
            return false;
        return super.isPotionApplicable(potioneffectIn);
    }

    @Override
    public void livingTick() {
        if (remove) {
            if (!world.isRemote()) {
                LOGGER.info("Remove Bee");
                remove = false;
                this.dead = true;
                this.remove();
            }
        }

        if (this.world.isRemote && getBeeInfo().isEnderBee()){
            for(int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.PORTAL, this.getPosXRandom(0.5D),
                        this.getPosYRandom() - 0.25D, this.getPosZRandom(0.5D),
                        (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(),
                        (this.rand.nextDouble() - 0.5D) * 2.0D);
            }
        }
        super.livingTick();
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        setBeeType(reason.equals(SpawnReason.CHUNK_GENERATION) || reason.equals(SpawnReason.NATURAL));
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public static boolean canBeeSpawn(EntityType<? extends AnimalEntity> typeIn, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        return worldIn.getDimension().getType().equals(DimensionType.THE_NETHER)
                || worldIn.getDimension().getType().equals(DimensionType.THE_END)
                || worldIn.getLight(pos) > 8;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(BEE_TYPE, BeeConst.DEFAULT_BEE_TYPE);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.dataManager.set(BEE_TYPE, compound.getString(BeeConst.NBT_BEE_TYPE));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString(BeeConst.NBT_BEE_TYPE, this.getBeeType());
    }

    public CustomBeeEntity createSelectedChild(String beeType) {
        CustomBeeEntity childBee = new CustomBeeEntity(RegistryHandler.CUSTOM_BEE.get(), this.world);
        childBee.setBeeType(beeType);
        return childBee;
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        Item item = itemstack.getItem();
        if (item instanceof NameTagItem){
            super.processInteract(player,hand);
        }
        if (this.isBreedingItem(itemstack)) {
            if (!this.world.isRemote && this.getGrowingAge() == 0 && this.canBreed()) {
                this.consumeItemFromStack(player, itemstack);
                this.setInLove(player);
                player.swing(hand, true);
                return true;
            }

            if (this.isChild()) {
                this.consumeItemFromStack(player, itemstack);
                this.ageUp((int)((float)(-this.getGrowingAge() / 20) * 0.1F), true);
                return true;
            }
        }
        if (item instanceof SpawnEggItem && ((SpawnEggItem)item).hasType(itemstack.getTag(), this.getType())) {
            if (!this.world.isRemote) {
                AgeableEntity ageableentity = this.createSelectedChild(this.getBeeType());
                ageableentity.setGrowingAge(-24000);
                ageableentity.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), 0.0F, 0.0F);
                this.world.addEntity(ageableentity);
                if (itemstack.hasDisplayName()) {
                    ageableentity.setCustomName(itemstack.getDisplayName());
                }

                this.onChildSpawnFromEgg(player, ageableentity);
                if (!player.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                }
            }
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public EntitySize getSize(Pose poseIn) {
        float scale = getSizeModifierFromInfo(getBeeType());
        return super.getSize(poseIn).scale(scale);
    }

    @Override
    public void notifyDataManagerChange(@Nonnull DataParameter<?> parameter) {
        if (parameter.equals(BEE_TYPE))
            recalculateSize();
        super.notifyDataManagerChange(parameter);
    }

    private void markRemove() {
        if (!remove) {
            this.remove = true;
        }
    }
}