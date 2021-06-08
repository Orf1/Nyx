package de.ellpeck.nyx;

import com.google.common.collect.Sets;
import de.ellpeck.nyx.capabilities.NyxWorld;
import de.ellpeck.nyx.lunarevents.StarShower;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class Config {

    public static Configuration instance;
    public static Set<String> allowedDimensions;
    public static boolean enchantments;
    public static boolean lunarWater;
    public static String[] lunarWaterItemParts;
    public static boolean addPotionEffects;
    public static int additionalMobsChance;
    public static double maxLunarEdgeXpMult;
    public static double minLevelLunarEdgeDamage;
    public static double maxLevelLunarEdgeDamage;
    public static double baseLunarEdgeDamage;
    public static boolean disallowDayEnchanting;
    public static double meteorShardGuardianChance;
    public static boolean fallingStars;
    public static double fallingStarRarity;
    public static double fallingStarRarityShower;
    public static double fallingStarImpactVolume;
    public static double fallingStarAmbientVolume;
    public static boolean fullMoon;
    public static boolean bloodMoonSleeping;
    public static int bloodMoonSpawnMultiplier;
    public static Set<String> mobDuplicationBlacklist;
    public static boolean isMobDuplicationWhitelist;
    public static boolean bloodMoonVanish;
    public static int bloodMoonSpawnRadius;
    public static boolean harvestMoonOnFull;
    public static boolean bloodMoonOnFull;
    public static boolean moonEventTint;
    public static int harvestMoonGrowAmount;
    public static int harvestMoonGrowInterval;
    public static LunarEventConfig harvestMoon;
    public static LunarEventConfig starShowers;
    public static LunarEventConfig bloodMoon;
    public static int[] lunarWaterTicks;
    public static double meteorChance;
    public static double meteorChanceNight;
    public static String meteorGateDimension;
    public static double meteorChanceAfterGate;
    public static double meteorChanceAfterGateNight;
    public static double meteorChanceStarShower;
    public static double meteorChanceEnd;
    public static int meteorSpawnRadius;
    public static boolean meteors;
    public static int meteorDisallowRadius;
    public static int meteorDisallowTime;
    public static boolean meteorKillUnloaded;
    public static boolean meteorCacheUnloaded;
    public static Set<String> enchantingWhitelistDimensions;
    public static boolean eventNotifications;
    public static int crystalDurability;
    public static int hammerDamage;
    public static double bowDamageMultiplier;
    public static String[] scytheDropChances;
    public static Set<ItemStack> scytheDropBlacklist;
    private static Set<String> _scytheDropBlacklist;
    public static Set<LunarWaterSource> lunarWaterRemoveNegative;
    public static Set<LunarWaterSource> lunarWaterRemoveAll;

    public static int colorBloodMoon;
    public static int colorHarvestMoon;
    public static int colorStarShower;

    private static Map<LunarWaterSource, Set<String>> _lunarWaterEffects = new HashMap<>();
    public static Map<LunarWaterSource, Set<PotionEffect>> lunarWaterEffects;

    public static void preInit(File file) {
        instance = new Configuration(file);
        instance.load();
        load();
    }

    public static void init() {
        lunarWaterEffects = _lunarWaterEffects.entrySet().stream()
                .map(e -> new SimpleEntry<LunarWaterSource, Set<PotionEffect>>(e.getKey(), e.getValue().stream()
                        .map(s -> {
                            String[] split = s.split(";");
                            if (split.length != 3)
                                return new PotionEffect((Potion) null);
                            return new PotionEffect(Potion.getPotionFromResourceLocation(split[0]), MathHelper.getInt(split[1], 20), MathHelper.getInt(split[2], 0));
                        })
                        .filter(p -> p.getPotion() != null)
                        .collect(Collectors.toSet()))
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        
        scytheDropBlacklist = _scytheDropBlacklist.stream()
                .map(s -> ItemMetaHelper.getFromString("scythe drop blacklist", s))
                .flatMap(Set::stream)
                .filter(i -> i != null && !i.isEmpty())
                .collect(Collectors.toSet());
    }

    public static void load() {
        allowedDimensions = Sets.newHashSet(instance.get("general", "allowedDimensions", new String[]{"overworld"}, "Names of the dimensions that lunar events should occur in").getStringList());
        meteorShardGuardianChance = instance.get("general", "meteorShardGuardianChance", 0.05, "The chance in percent (1 = 100%) for a meteor shard to be dropped from an elder guardian", 0, 1).getDouble();
        mobDuplicationBlacklist = Sets.newHashSet(instance.get("general", "mobDuplicationBlacklist", new String[0], "The registry names of entities that should not be spawned during the full and blood moons. If isMobDuplicationWhitelist is true, this acts as a whitelist instead.").getStringList());
        isMobDuplicationWhitelist = instance.get("general", "isMobDuplicationWhitelist", false, "If the mobDuplicationBlacklist should act as a whitelist instead").getBoolean();
        moonEventTint = instance.get("general", "moonEventTint", true, "If moon events should tint the sky").getBoolean();
        eventNotifications = instance.get("general", "eventNotifications", true, "If moon events should be announced in chat when they start").getBoolean();


        lunarWater = instance.get("lunarWater", "lunarWater", true, "If lunar water should be enabled").getBoolean();
        lunarWaterItemParts = instance.get("lunarWater", "lunarWaterItem", "minecraft:dye:11", "The item that needs to be dropped into a cauldron to turn it into lunar water.\nExamples include 'minecraft:stick', 'minecraft:wool:3', and 'ore:stone'").getString().split(":");
        lunarWaterTicks = instance.get("lunarWater", "lunarWaterTicks", new int[]{1200, -1, 4800, 4800, 3600, 3600, 2400, 2400, 600, -1}, 
                "The amount of ticks that a cauldron of water must be exposed to the night sky to be ready to turn into lunar water, per moon phase.\n" + 
                "From first to last, the entries are: Full moon, new moon, waning crescent, waxing crescent, third quarter, first quarter, waning gibbous, waxing gibbous, harvest moon and blood moon.\n" + 
                "Set any entry to -1 to disable lunar water production for that phase.").getIntList();
        lunarWaterRemoveNegative = getLunarWaterConfig("lunarWater", "lunarWaterRemoveNegative", LunarWaterSource.ALL, "Which lunar water methods should clear negative effects\nPossible values are " + String.join(", ", LunarWaterSource.getNames()));
        lunarWaterRemoveAll = getLunarWaterConfig("lunarWater", "lunarWaterRemoveAll", LunarWaterSource.NONE, "Which lunar water methods should clear all effects\nPossible values are " + String.join(", ", LunarWaterSource.getNames()));
        for (LunarWaterSource c : LunarWaterSource.values()) {
            _lunarWaterEffects.put(c, 
                Sets.newHashSet(
                    instance.get("lunarWater", 
                        "lunarWaterEffects" + c.name(), 
                        new String[]{"minecraft:regeneration;100;1"}, 
                        "The effects that lunar water in the method '" + c.name() + "' should give")
                    .getStringList()));
        }

        fullMoon = instance.get("fullMoon", "fullMoon", true, "If the vanilla full moon should be considered a proper lunar event").getBoolean();
        addPotionEffects = instance.get("fullMoon", "addPotionEffects", true, "If mobs spawned during a full moon should have random potion effects applied to them (similarly to spiders in the base game)").getBoolean();
        additionalMobsChance = instance.get("fullMoon", "additionalMobsChance", 5, "The chance for an additional mob to be spawned when a mob spawns during a full moon. The higher the number, the less likely. Set to 0 to disable.", 0, 1000).getInt();

        enchantments = instance.get("enchantments", "enchantments", true, "If the enchantments should be enabled").getBoolean();
        maxLunarEdgeXpMult = instance.get("enchantments", "maxLunarEdgeXpMult", 1.0, 
                "The max multiplier on the amount of xp added (which happens during a full moon)\n" + 
                "Can be set to 0 to disable lunar edge xp gains\n" + 
                "The multiplier scales up to the max according to the level and moon phase" + 
                "Ex. if the config option is set to 2.5, a full moon with max lunar edge level would give" + 
                "3.5x xp and a new moon would give 1x xp").getDouble();
        minLevelLunarEdgeDamage = instance.get("enchantments", "minLevelLunarEdgeDamage", 1.25, "The amount of additional damage that should be applied to an item with level 1 lunar edge on a full moon.").getDouble();
        maxLevelLunarEdgeDamage = instance.get("enchantments", "maxLevelLunarEdgeDamage", 3.25, "The amount of additional damage that should be applied to an item with max level lunar edge on a full moon.").getDouble();
        baseLunarEdgeDamage = instance.get("enchantments", "baseLunarEdgeDamage", 0, "The amount of additional damage that will always be applied regardless of moon phase.").getDouble();
        disallowDayEnchanting = instance.get("enchantments", "disallowDayEnchanting", true, "If enchanting should be disallowed during the day").getBoolean();
        enchantingWhitelistDimensions = Sets.newHashSet(instance.get("enchantments", "enchantingWhitelistDimensions", new String[]{"the_nether", "the_end"}, "A list of names of dimensions where enchanting is always allowed, and not just at night").getStringList());

        harvestMoon = new LunarEventConfig("harvestMoon", "harvestMoon", "Harvest Moon", 0.05);
        colorHarvestMoon = Integer.parseInt(instance.get("harvestMoon", "harvestMoonColor", "3f3fc0", "The hex code of the harvest moon color").getString(), 16);
        harvestMoonOnFull = instance.get("harvestMoon", "harvestMoonOnFull", true, "If the harvest moon should only occur on full moon nights").getBoolean();
        harvestMoonGrowAmount = instance.get("harvestMoon", "harvestMoonGrowAmount", 15, "The amount of plants that should be grown per chunk during the harvest moon", 0, 100).getInt();
        harvestMoonGrowInterval = instance.get("harvestMoon", "harvestMoonGrowInterval", 10, "The amount of ticks that should pass before plants are grown again during the harvest moon", 1, 100).getInt();

        starShowers = new LunarEventConfig("fallingStars", "starShowers", "Star Showers", 0.05);
        colorStarShower = Integer.parseInt(instance.get("fallingStars", "starShowerColor", "dec25f", "The hex code of the star shower color").getString(), 16);
        fallingStars = instance.get("fallingStars", "fallingStars", true, "If falling stars should be enabled").getBoolean();
        fallingStarRarity = instance.get("fallingStars", "fallingStarRarity", 0.01F, "The chance in percent (1 = 100%) for a falling star to appear at night for each player per second", 0, 1).getDouble();
        fallingStarRarityShower = instance.get("fallingStars", "fallingStarRarityShower", 0.15F, "The chance for a falling star to appear during a star shower for each player per second", 0, 1).getDouble();
        fallingStarImpactVolume = instance.get("fallingStars", "fallingStarImpactVolume", 10F, "The volume for the falling star impact sound").getDouble();
        fallingStarAmbientVolume = instance.get("fallingStars", "fallingStarAmbientVolume", 5F, "The volume for the falling star ambient sound").getDouble();

        bloodMoon = new LunarEventConfig("bloodMoon", "bloodMoon", "Blood Moon", 0.05);
        colorBloodMoon = Integer.parseInt(instance.get("bloodMoon", "bloodMoonColor", "420d03", "The hex code of the blood moon color").getString(), 16);
        bloodMoonSleeping = instance.get("bloodMoon", "bloodMoonSleeping", false, "If sleeping is allowed during a blood moon").getBoolean();
        bloodMoonSpawnMultiplier = instance.get("bloodMoon", "bloodMoonSpawnMultiplier", 2, "The multiplier with which mobs should spawn during the blood moon (eg 2 means 2 mobs spawn instead of 1)", 1, 1000).getInt();
        bloodMoonVanish = instance.get("bloodMoon", "bloodMoonVanish", true, "If mobs spawned by the blood moon should die at sunup").getBoolean();
        bloodMoonSpawnRadius = instance.get("bloodMoon", "bloodMoonSpawnRadius", 20, "The closest distance that mobs can spawn away from a player during the blood moon. Vanilla value is 24.").getInt();
        bloodMoonOnFull = instance.get("bloodMoon", "bloodMoonOnFull", true, "If the blood moon should only occur on full moon nights").getBoolean();

        meteors = instance.get("meteors", "meteors", true, "If meteor content should be enabled").getBoolean();
        meteorChance = instance.get("meteors", "meteorChance", 0.00014, "The chance of a meteor spawning every second, during the day").getDouble();
        meteorChanceNight = instance.get("meteors", "meteorChanceNight", 0.0024, "The chance of a meteor spawning every second, during nighttime").getDouble();
        meteorGateDimension = instance.get("meteors", "meteorGateDimension", "the_nether", "The dimension that needs to be entered to increase the spawning of meteors").getString();
        meteorChanceAfterGate = instance.get("meteors", "meteorChanceAfterGate", 0.0002, "The chance of a meteor spawning every second, during the day, after the gate dimension has been entered once").getDouble();
        meteorChanceAfterGateNight = instance.get("meteors", "meteorChanceAfterGateNight", 0.003, "The chance of a meteor spawning every second, during the night, after the gate dimension has been entered once").getDouble();
        meteorChanceStarShower = instance.get("meteors", "meteorChanceStarShower", 0.0075, "The chance of a meteor spawning every second, during a star shower").getDouble();
        meteorChanceEnd = instance.get("meteors", "meteorChanceEnd", 0.003, "The chance of a meteor spawning every second, in the end dimension").getDouble();
        meteorSpawnRadius = instance.get("meteors", "meteorSpawnRadius", 1000, "The amount of blocks a meteor can spawn away from the nearest player").getInt();
        meteorDisallowRadius = instance.get("meteors", "meteorDisallowRadius", 16, "The radius in chunks that should be marked as invalid for meteor spawning around each player").getInt();
        meteorDisallowTime = instance.get("meteors", "meteorDisallowTime", 12000, "The amount of ticks that need to pass for each player until the chance of a meteor spawning in the area is halved (and then halved again, and so on). This decreases the chance of a meteor hitting a base or player hub").getInt();
        meteorKillUnloaded = instance.get("meteors", "meteorKillUnloaded", false, "If meteors passing through unloaded chunks should be removed. If the game is lagging because of the unloaded chunks, try enabling this").getBoolean();
        meteorCacheUnloaded = instance.get("meteors", "meteorCacheUnloaded", false, "If meteors passing through unloaded chunks should be cached at that position until entering the unloaded chunk. This option is ignored if meteorKillUnloaded is true.").getBoolean();
        
        crystalDurability = instance.get("equipment", "crystalDurability", 1000, "The amount of uses that a gleaning crystal should have for bone-mealing").getInt();
        hammerDamage = instance.get("equipment", "hammerDamage", 15, "The amount of damage that the meteor hammer deals if the maximum flight time was used").getInt();
        bowDamageMultiplier = instance.get("equipment", "bowDamageMult", 1.75, "The multiplier for the amount of damage inflicted by the meteor bow's arrows").getDouble();
        _scytheDropBlacklist = Sets.newHashSet(instance.get("equipment", "scytheDropBlacklist", new String[0], "Drops that the scythe shouldn't multiply").getStringList());
        scytheDropChances = instance.get("equipment", "scytheDropChances", new String[]{"0.6;2", "0.4;3", "0.2;4"}, 
                "The drop chances for the scythe. The order of this list matters!\n" + 
                "The scythe will, for each drop, check against this list in order.\n" + 
                "Each line shows the chance for that drop and the multiplier for that drop if the chance is selected.\n" + 
                "If the drop is not selected, the scythe moves to the next drop chance.\n" + 
                "If the list is empty, the scythe will not multiply drops.\n" + 
                "The format for each line is 'chance;drop_mult'").getStringList();

        if (instance.hasChanged())
            instance.save();
    }

    public static double getMeteorChance(World world, NyxWorld data) {
        DimensionType dim = world.provider.getDimensionType();
        if (dim == DimensionType.THE_END)
            return meteorChanceEnd;

        if (!Config.allowedDimensions.contains(dim.getName()))
            return 0;
        boolean visitedGate = data.visitedDimensions.contains(meteorGateDimension);
        if (!NyxWorld.isDaytime(world)) {
            if (data.currentEvent instanceof StarShower) {
                return meteorChanceStarShower;
            } else {
                return visitedGate ? meteorChanceAfterGateNight : meteorChanceNight;
            }
        }
        return visitedGate ? meteorChanceAfterGate : meteorChance;
    }

    public static LunarWaterSource getLunarWaterConfig(String category, String key, LunarWaterSource defaultValue, String comment) {
        return LunarWaterSource.valueOf(instance.get(category, key, defaultValue.name(), comment, LunarWaterSource.getNames()).getString());
    }

    public static Set<LunarWaterSource> getLunarWaterConfig(String category, String key, Set<LunarWaterSource> defaultValue, String comment) {
        return Arrays.stream(instance.get(category, key, 
            defaultValue.stream()
                    .map(LunarWaterSource::name)
                    .collect(Collectors.toList())
                    .toArray(new String[0]), comment)
            .getStringList())
                .filter(LunarWaterSource::containsName)
                .map(LunarWaterSource::valueOf)
                .collect(Collectors.toSet());
    }

    public static class LunarEventConfig {

        public boolean enabled;
        public double chance;
        public int startNight;
        public int nightInterval;
        public int graceDays;

        public LunarEventConfig(String category, String name, String displayName, double defaultChance) {
            this.enabled = instance.get(category, name, true, "If the " + displayName + " should be enabled").getBoolean();
            this.chance = instance.get(category, name + "Chance", defaultChance, "The chance in percent (1 = 100%) of the " + displayName + " occuring", 0, 1).getDouble();
            this.startNight = instance.get(category, name + "StartNight", 0, "The amount of nights that should pass before the " + displayName + " occurs for the first time", 0, 1000).getInt();
            this.nightInterval = instance.get(category, name + "Interval", 0, "The interval in days at which the " + displayName + " should occur. Overrides chance setting if set to a value greater than 0.", 0, 1000).getInt();
            this.graceDays = instance.get(category, name + "GracePeriod", 0, "The amount of days that should pass until the " + displayName + " happens again", 0, 1000).getInt();
        }
    }
}
