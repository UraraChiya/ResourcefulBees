/* MIT License
 *
 * Copyright (c) 2021 Team Resourceful
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Methods below are taken from Mekanism and modified to fit Resourceful Bees -> https://github.com/mekanism/Mekanism
 */

package com.teamresourceful.resourcefulbees.common.util;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class WorldUtils {

    private WorldUtils() {
        throw new UtilityClassError();
    }

    @Nullable
    @Contract("_, null, _, _ -> null")
    public static <A extends BlockEntity> A getTileEntity(@NotNull Class<A> clazz, @Nullable Level reader, @NotNull BlockPos pos, boolean logWrongType) {
        BlockEntity tile = getTileEntity(reader, pos);
        if (tile == null) {
            return null;
        } else if (clazz.isInstance(tile)) {
            return clazz.cast(tile);
        } else {
            if (logWrongType) {
                ModConstants.LOGGER.warn("Unexpected TileEntity class at {}, expected {}, but found: {}", pos, clazz, tile.getClass());
            }

            return null;
        }
    }

    @Nullable
    @Contract("null, _ -> null")
    public static BlockEntity getTileEntity(@Nullable Level world, @NotNull BlockPos pos) {
        return !isBlockLoaded(world, pos) ? null : world.getBlockEntity(pos);
    }

    @Contract("null, _ -> false")
    public static boolean isBlockLoaded(@Nullable Level world, @NotNull BlockPos pos) {
        return world != null && world.isInWorldBounds(pos) && world.isLoaded(pos);
    }

    @Nullable
    @Contract("_, null, _ -> null")
    public static <T extends BlockEntity> T getTileEntity(@NotNull Class<T> clazz, @Nullable Level world, @NotNull BlockPos pos) {
        return getTileEntity(clazz, world, pos, false);
    }

    @Nullable
    @Contract("_, _, _ -> null")
    public static <T extends BlockEntity> T getTileEntity(@NotNull Class<T> clazz, Supplier<@Nullable Level> input, @NotNull BlockPos pos) {
        return getTileEntity(clazz, input.get(), pos, false);
    }

    //This method was added by us, but we hereby license this method under MIT as well.
    @Contract("null, _, _ -> false")
    public static boolean checkBlock(@Nullable Level level, @NotNull BlockPos pos, Predicate<BlockState> statePredicate) {
        return isBlockLoaded(level, pos) && statePredicate.test(level.getBlockState(pos));
    }
}
