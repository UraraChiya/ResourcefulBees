package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistries;
import com.teamresourceful.resourcefulbees.platform.common.registry.api.ResourcefulRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import org.jetbrains.annotations.NotNull;

public final class ModEffects {

    private ModEffects() {
        throw new UtilityClassError();
    }

    public static final ResourcefulRegistry<MobEffect> EFFECTS = ResourcefulRegistries.create(Registry.MOB_EFFECT, BeeConstants.MOD_ID);

    public static final RegistryEntry<MobEffect> CALMING = EFFECTS.register("calming", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 16763783) {
        @Override
        public void applyEffectTick(@NotNull LivingEntity entity, int level) {
            if (entity instanceof NeutralMob neutralMob) neutralMob.stopBeingAngry();
            super.applyEffectTick(entity, level);
        }

        @Override
        public boolean isDurationEffectTick(int duration, int level) {
            return duration % 5 == 0;
        }
    });
}