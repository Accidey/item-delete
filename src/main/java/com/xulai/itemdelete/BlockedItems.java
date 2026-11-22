package com.xulai.itemdelete;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class BlockedItems {
    private static volatile Matcher matcher;

    private BlockedItems() {
    }

    public static boolean isBlocked(ItemStack stack) {
        return !stack.isEmpty() && isBlocked(stack.getItem());
    }

    public static boolean isBlocked(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        return id != null && isBlocked(id);
    }

    public static boolean isBlocked(String rawId) {
        ResourceLocation id = rawId == null ? null : ResourceLocation.tryParse(rawId);
        return id != null && isBlocked(id);
    }

    public static boolean isBlocked(ResourceLocation id) {
        Matcher current = matcher;
        if (current == null) {
            current = rebuild();
            if (current == null) {
                return false;
            }
        }
        return current.matches(id);
    }

    public static boolean hideFromCreativeTabs() {
        return booleanValue(Config.HIDE_FROM_CREATIVE_TABS, true);
    }

    public static boolean stripRecipes() {
        return booleanValue(Config.STRIP_RECIPES, true);
    }

    public static boolean stripLootTables() {
        return booleanValue(Config.STRIP_LOOT_TABLES, true);
    }

    public static boolean stripTrades() {
        return booleanValue(Config.STRIP_TRADES, true);
    }

    public static List<String> entries() {
        try {
            return new ArrayList<>(Config.BLOCKED_ITEMS.get());
        } catch (IllegalStateException exception) {
            return new ArrayList<>();
        }
    }

    public static boolean addEntry(ResourceLocation id) {
        List<String> entries = entries();
        String value = id.toString();
        if (entries.contains(value)) {
            return false;
        }
        entries.add(value);
        save(entries);
        return true;
    }

    public static boolean removeEntry(ResourceLocation id) {
        List<String> entries = entries();
        boolean removed = entries.removeIf(entry -> entry.equals(id.toString()));
        if (removed) {
            save(entries);
        }
        return removed;
    }

    public static String findNamespaceWildcard(ResourceLocation id) {
        String wildcard = id.getNamespace() + ":*";
        return entries().contains(wildcard) ? wildcard : null;
    }

    public static void resetCache() {
        matcher = null;
    }

    public static void logConfigured() {
        List<String> entries = entries();
        if (entries.isEmpty()) {
            ItemDeleteMod.LOGGER.info("blockedItems is empty");
            return;
        }
        ItemDeleteMod.LOGGER.info("blockedItems has {} entries: {}", entries.size(), String.join(", ", entries));
    }

    private static synchronized Matcher rebuild() {
        if (matcher != null) {
            return matcher;
        }
        List<? extends String> entries;
        try {
            entries = Config.BLOCKED_ITEMS.get();
        } catch (IllegalStateException exception) {
            return null;
        }
        Set<ResourceLocation> exactIds = new LinkedHashSet<>();
        Set<String> namespaces = new LinkedHashSet<>();
        for (String raw : entries) {
            String entry = raw.trim();
            if (entry.isEmpty()) {
                continue;
            }
            int separator = entry.indexOf(':');
            if (separator <= 0 || separator == entry.length() - 1) {
                continue;
            }
            if ("*".equals(entry.substring(separator + 1))) {
                namespaces.add(entry.substring(0, separator));
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(entry);
            if (id != null) {
                exactIds.add(id);
            }
        }
        matcher = new Matcher(Set.copyOf(exactIds), Set.copyOf(namespaces));
        return matcher;
    }

    private static void save(List<String> entries) {
        Config.BLOCKED_ITEMS.set(entries);
        Config.SPEC.save();
        resetCache();
    }

    private static boolean booleanValue(ModConfigSpec.BooleanValue value, boolean fallback) {
        try {
            return value.get();
        } catch (IllegalStateException exception) {
            return fallback;
        }
    }

    private record Matcher(Set<ResourceLocation> exactIds, Set<String> namespaces) {
        boolean matches(ResourceLocation id) {
            return exactIds.contains(id) || namespaces.contains(id.getNamespace());
        }
    }
}
