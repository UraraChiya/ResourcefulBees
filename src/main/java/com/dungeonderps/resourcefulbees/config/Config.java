package com.dungeonderps.resourcefulbees.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    public static ForgeConfigSpec.BooleanValue GENERATE_DEFAULTS;
    public static ForgeConfigSpec.BooleanValue ENABLE_EASTER_EGG_BEES;

    public static ForgeConfigSpec.BooleanValue GENERATE_BEE_NESTS;

    public static ForgeConfigSpec.BooleanValue CENTRIFUGE_RECIPES;
    public static ForgeConfigSpec.BooleanValue HONEYCOMB_BLOCK_RECIPES;

    public static ForgeConfigSpec.IntValue CENTRIFUGE_RECIPE_TIME;

    public static ForgeConfigSpec.IntValue HIVE_MAX_BEES;
    public static ForgeConfigSpec.IntValue HIVE_MAX_COMBS;

    public static ForgeConfigSpec.IntValue SPAWN_WEIGHT;
    public static ForgeConfigSpec.IntValue SPAWN_MIN_GROUP;
    public static ForgeConfigSpec.IntValue SPAWN_MAX_GROUP;

    public static ForgeConfigSpec.DoubleValue BEE_NEST_GENERATION_WEIGHT;

    public static class CommonConfig {

        public static ForgeConfigSpec COMMON_CONFIG;

        static {
            ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

            COMMON_BUILDER.push("General Options");
                GENERATE_DEFAULTS = COMMON_BUILDER.comment("Set this to false when you want to overwrite the default bee files. [true/false]")
                        .define("generateDefaults",true);
                ENABLE_EASTER_EGG_BEES = COMMON_BUILDER.comment("Set to true if you want easter egg bees to generate (WIP) [true/false]")
                        .define("enableEasterEggBees", true);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Recipe Options");
                CENTRIFUGE_RECIPES = COMMON_BUILDER.comment("Set to false if you don't want the centrifuge recipes to be auto generated [true/false]")
                        .define("centrifugeRecipes", true);
                HONEYCOMB_BLOCK_RECIPES = COMMON_BUILDER.comment("Set to false if you don't want the honeycomb block recipes to be auto generated [true/false]")
                        .define("honeycombBlockRecipes", true);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Centrifuge Options");
                CENTRIFUGE_RECIPE_TIME = COMMON_BUILDER.comment("Recipe time for generated centrifuge recipes","This does not affect recipes that are not auto generated by us.","Time is in seconds.") //TODO <---- This comment doesn't make sense.
                        .defineInRange("centrifugeRecipeTime", 10,1,60);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Beehive Options");
                HIVE_MAX_BEES = COMMON_BUILDER.comment("Maximum amount of bees in the base tier hive. \n(THIS * TIER_MODIFIER = MAX_BEES) for a range of 4 -> 16")
                        .defineInRange("hiveMaxBees", 4, 1, 4);
                HIVE_MAX_COMBS = COMMON_BUILDER.comment("Base honeycomb harvest amount \n(THIS * TIER_MODIFIER = MAX_COMBS) for a range of 5 -> 64")
                        .defineInRange("hiveMaxCombs", 5, 5, 16);
            COMMON_BUILDER.pop();

            COMMON_BUILDER.push("Spawning Options");
            SPAWN_WEIGHT = COMMON_BUILDER.comment("This is the spawn weighting for ALL bees.")
                    .defineInRange("spawnWeighting", 3, 1, 20);
            SPAWN_MIN_GROUP = COMMON_BUILDER.comment("This is the min amount of bees that can spawn in a group. \nNote: this is for ALL bees!")
                    .defineInRange("spawnMinBeeGroup", 1, 1, 5);
            SPAWN_MAX_GROUP = COMMON_BUILDER.comment("This is the max amount of bees that can spawn in a group. \nNote: this is for ALL bees!")
                    .defineInRange("spawnMaxBeeGroup", 5, 5, 10);
            GENERATE_BEE_NESTS = COMMON_BUILDER.comment("Should bee nests generate in world? \nNote: They will only generate in biomes where bees can spawn")
                    .define("generateBeeNests", true);
            COMMON_BUILDER.pop();
            BEE_NEST_GENERATION_WEIGHT = COMMON_BUILDER.comment("Bee nest weighting when generating chunks. Higher value means nest is more likely to generate")
                    .defineInRange("bee_nest_generation_weight", 0.5, 0.0, 1.0);

            COMMON_CONFIG = COMMON_BUILDER.build();
        }
    }
}
