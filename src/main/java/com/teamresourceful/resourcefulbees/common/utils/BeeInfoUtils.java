package com.teamresourceful.resourcefulbees.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.api.ICustomBee;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyBlockData;
import com.teamresourceful.resourcefulbees.api.honeydata.HoneyFluidData;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import com.teamresourceful.resourcefulbees.common.item.BeeJar;
import com.teamresourceful.resourcefulbees.common.item.CustomHoneyBottleItem;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.HoneyRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants.VANILLA_BEE_COLOR;

public class BeeInfoUtils {

    private BeeInfoUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static Pair<String, String> sortParents(String parent1, String parent2) {
        return parent1.compareTo(parent2) > 0 ? Pair.of(parent1, parent2) : Pair.of(parent2, parent1);
    }

    public static MobEffect getEffect(String effectName) {
        return ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(effectName));
    }

    public static @Nullable EntityType<?> getEntityType(String entityName) {
        return ForgeRegistries.ENTITIES.getValue(ResourceLocation.tryParse(entityName));
    }

    public static @Nullable EntityType<?> getEntityType(ResourceLocation entityId) {
        return ForgeRegistries.ENTITIES.getValue(entityId);
    }

    public static Optional<ResourceLocation> getResourceLocation(String resource) {
        return Optional.ofNullable(ResourceLocation.tryParse(resource));
    }

    @Nullable
    public static Tag<Item> getItemTag(String itemTag) {
        return getResourceLocation(itemTag).map(ItemTags.getAllTags()::getTag).orElse(null);
    }

    @Nullable
    public static Tag<Fluid> getFluidTag(String fluidTag) {
        return getResourceLocation(fluidTag).map(FluidTags.getAllTags()::getTag).orElse(null);
    }

    @Nullable
    public static Tag<Block> getBlockTag(String blockTag) {
        return getResourceLocation(blockTag).map(BlockTags.getAllTags()::getTag).orElse(null);
    }

    public static void flagBeesInRange(BlockPos pos, Level world) {
        BoundingBox box = new BoundingBox(pos.getX() + 10, pos.getY() + 10, pos.getZ() + 10, pos.getX() - 10, pos.getY() - 10, pos.getZ() - 10);
        AABB aabb = AABB.of(box);
        if (world != null) {
            List<CustomBeeEntity> list = world.getEntitiesOfClass(CustomBeeEntity.class, aabb);
            list.forEach(customBeeEntity -> customBeeEntity.setHasHiveInRange(true));
        }
    }

    public static List<String> getLoreLines(CompoundTag outputNBT) {
        if (outputNBT.isEmpty()) return new LinkedList<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(outputNBT.toString());
        String nbtString = "NBT: " + gson.toJson(je);
        return Arrays.asList(nbtString.split("\n"));
    }

    public static void ageBee(int ticksInHive, Bee beeEntity) {
        int i = beeEntity.getAge();
        if (i < 0) {
            beeEntity.setAge(Math.min(0, i + ticksInHive));
        } else if (i > 0) {
            beeEntity.setAge(Math.max(0, i - ticksInHive));
        }

        if (beeEntity instanceof CustomBeeEntity beeEntity) {
            beeEntity.setLoveTime(Math.max(0, beeEntity.getInLoveTime() - ticksInHive));
        } else {
            beeEntity.setInLoveTime(Math.max(0, beeEntity.getInLoveTime() - ticksInHive));
        }
        beeEntity.resetTicksWithoutNectarSinceExitingHive();
    }

    public static void setEntityLocationAndAngle(BlockPos blockpos, Direction direction, Entity entity) {
        EntityDimensions size = entity.getDimensions(Pose.STANDING);
        double d0 = 0.65D + size.width / 2.0F;
        double d1 = blockpos.getX() + 0.5D + d0 * direction.getStepX();
        double d2 = blockpos.getY() + Math.max(0.5D - (size.height / 2.0F), 0);
        double d3 = blockpos.getZ() + 0.5D + d0 * direction.getStepZ();
        entity.moveTo(d1, d2, d3, entity.getYRot(), entity.getXRot());
    }

    public static @NotNull CompoundTag createJarBeeTag(Bee beeEntity, String nbtTagID) {
        String type = EntityType.getKey(beeEntity.getType()).toString();
        CompoundTag nbt = new CompoundTag();
        nbt.putString(nbtTagID, type);

        beeEntity.saveWithoutId(nbt);

        String beeColor = VANILLA_BEE_COLOR;

        if (beeEntity instanceof ICustomBee iCustomBee) {
            nbt.putString(NBTConstants.NBT_BEE_TYPE, iCustomBee.getBeeType());
            beeColor = iCustomBee.getRenderData().getColorData().getJarColor().toString();
        }

        nbt.putString(NBTConstants.NBT_COLOR, beeColor);

        return nbt;
    }

    public static boolean isBeeInJarOurs(@NotNull ItemStack stack) {
        //noinspection ConstantConditions
        return BeeJar.isFilled(stack) && stack.getTag().getString(NBTConstants.NBT_ENTITY).startsWith(ResourcefulBees.MOD_ID);
    }

    public static Fluid getHoneyFluidFromBottle(ItemStack bottleOutput) {
        Item item = bottleOutput.getItem();
        if (item == Items.HONEY_BOTTLE) {
            return ModFluids.HONEY_STILL.get().getSource();
        } else if (item instanceof CustomHoneyBottleItem honey) {
            HoneyFluidData fluidData = HoneyRegistry.getRegistry().getHoneyData(honey.getHoneyData().getName()).getFluidData();
            return fluidData.getStillFluid().get().getSource();
        }
        return Fluids.EMPTY;
    }

    public static Item getHoneyBottleFromFluid(Fluid fluid) {
        if (fluid instanceof CustomHoneyFluid customfluid) {
            return HoneyRegistry.getRegistry().getHoneyData(customfluid.getHoneyData().getName()).getBottleData().getHoneyBottle().get();
        } else {
            return Items.HONEY_BOTTLE;
        }
    }

    public static Item getHoneyBlockFromFluid(Fluid fluid) {
        if (fluid instanceof CustomHoneyFluid customfluid) {
            HoneyBlockData blockData = HoneyRegistry.getRegistry().getHoneyData(customfluid.getHoneyData().getName()).getBlockData();
            if (blockData.getBlockItem() == null) return Items.AIR;
            return blockData.getBlockItem().get();
        } else {
            return Items.HONEY_BLOCK;
        }
    }

}
