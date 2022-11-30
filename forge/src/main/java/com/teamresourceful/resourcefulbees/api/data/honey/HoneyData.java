package com.teamresourceful.resourcefulbees.api.data.honey;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public record HoneyData(String name, HoneyBottleData bottleData, HoneyBlockData blockData, HoneyFluidData fluidData) {

    public static Codec<HoneyData> codec(String name) {
        return RecordCodecBuilder.create(instance -> instance.group(
                MapCodec.of(Encoder.empty(), Decoder.unit(() -> name)).forGetter(HoneyData::name),
                HoneyBottleData.codec(name).fieldOf("bottleData").orElseGet((Consumer<String>) s -> ResourcefulBees.LOGGER.error("bottleData is REQUIRED!"), null).forGetter(HoneyData::bottleData),
                HoneyBlockData.codec(name).fieldOf("blockData").orElse(HoneyBlockData.getDefault(name)).forGetter(HoneyData::blockData),
                HoneyFluidData.codec(name).fieldOf("fluidData").orElse(HoneyFluidData.getDefault(name)).forGetter(HoneyData::fluidData)
        ).apply(instance, HoneyData::new));
    }

    public Component getDisplayName() {
        return Component.translatable("honey_type.resourcefulbees." + name());
    }
}