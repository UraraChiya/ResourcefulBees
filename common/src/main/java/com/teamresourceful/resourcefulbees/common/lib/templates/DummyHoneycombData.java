package com.teamresourceful.resourcefulbees.common.lib.templates;

import com.teamresourceful.resourcefulbees.api.data.honeycomb.OutputVariation;
import com.teamresourceful.resourcefulbees.api.tiers.ApiaryTier;
import com.teamresourceful.resourcefulbees.api.tiers.BeehiveTier;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.Map;
import java.util.Optional;

public final class DummyHoneycombData {

    private DummyHoneycombData() {
        throw new UtilityClassError();
    }

    public static final OutputVariation DUMMY_OUTPUT_VARIATION = new OutputVariation("template",
        Map.of(
            BeehiveTier.getOrThrow(new ResourceLocation(ModConstants.MOD_ID, "t1")), Items.HONEYCOMB.getDefaultInstance(),
            BeehiveTier.getOrThrow(new ResourceLocation(ModConstants.MOD_ID, "t2")), Items.HONEYCOMB.getDefaultInstance(),
            BeehiveTier.getOrThrow(new ResourceLocation(ModConstants.MOD_ID, "t3")), Items.HONEYCOMB.getDefaultInstance(),
            BeehiveTier.getOrThrow(new ResourceLocation(ModConstants.MOD_ID, "t4")), Items.HONEYCOMB.getDefaultInstance()
        ),
        Map.of(
            ApiaryTier.getOrThrow(new ResourceLocation(ModConstants.MOD_ID, "t1")), Items.HONEYCOMB_BLOCK.getDefaultInstance(),
            ApiaryTier.getOrThrow(new ResourceLocation(ModConstants.MOD_ID, "t2")), Items.HONEYCOMB_BLOCK.getDefaultInstance(),
            ApiaryTier.getOrThrow(new ResourceLocation(ModConstants.MOD_ID, "t3")), Items.HONEYCOMB_BLOCK.getDefaultInstance(),
            ApiaryTier.getOrThrow(new ResourceLocation(ModConstants.MOD_ID, "t4")), Items.HONEYCOMB_BLOCK.getDefaultInstance()
        ),
        Optional.of(Items.HONEYCOMB.getDefaultInstance()),
        Optional.of(Items.HONEYCOMB_BLOCK.getDefaultInstance()));
}
