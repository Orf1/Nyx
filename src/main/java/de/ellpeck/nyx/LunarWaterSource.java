package de.ellpeck.nyx;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

public enum LunarWaterSource {
    BOTTLE,
    STANDING,
    CAULDRON;

    public static final Set<LunarWaterSource> NONE = Sets.newHashSet();
    public static final Set<LunarWaterSource> ALL = Sets.newHashSet(BOTTLE, STANDING, CAULDRON);

    public static String[] getNames() {
        return Arrays.stream(values()).map(LunarWaterSource::name).collect(Collectors.toList()).toArray(new String[0]);
    }

    public static boolean containsName(String s) {
        return Arrays.stream(values()).map(LunarWaterSource::name).anyMatch(s::equals);
    }
}