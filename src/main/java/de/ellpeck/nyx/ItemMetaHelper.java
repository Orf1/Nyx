package de.ellpeck.nyx;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import static de.ellpeck.nyx.Nyx.LOGGER;

public class ItemMetaHelper {
    public static Set<ItemStack> getFromString(String debugString, String s) {
        String[] itemData = s.split(":");
        if (itemData.length < 2 || itemData.length > 3) {
            LOGGER.warn("Invalid {} '{}'", debugString, s);
            return Sets.newHashSet(ItemStack.EMPTY);
        }
        ResourceLocation r = new ResourceLocation(itemData[0], itemData[1]);
        if (!ForgeRegistries.ITEMS.containsKey(r))
            return Sets.newHashSet(ItemStack.EMPTY);
        Item item = ForgeRegistries.ITEMS.getValue(r);

        // has meta
        if (itemData.length == 3) {
            int meta = MathHelper.getInt(itemData[2], -1);
            if (meta < 0) {
                LOGGER.warn("Invalid meta '{}' for {} '{}'", meta, debugString, s);
                return Sets.newHashSet(ItemStack.EMPTY);
            }
            return Sets.newHashSet(new ItemStack(item, 1, meta));
        } else {
            // no meta, wildcard it
            NonNullList<ItemStack> subItems = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, subItems);

            return new HashSet<>(subItems);
        }
    }

    public static Set<ItemStack> getFromStringArray(String debugString, String[] a) {
        return Arrays.stream(a).map(s -> ItemMetaHelper.getFromString(debugString, s)).flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public static Set<ItemStack> getFromStringCollection(String debugString, Collection<String> c) {
        return getFromStringArray(debugString, c.toArray(new String[0]));
    }
}