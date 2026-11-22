package com.xulai.itemdelete.logic;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xulai.itemdelete.BlockedItems;
import com.xulai.itemdelete.ItemDeleteMod;

import net.minecraft.resources.Identifier;

public final class LootTableJsonFilter {
    private static final String ITEM_TYPE = "minecraft:item";
    private static final String SET_ITEM_FUNCTION = "minecraft:set_item";
    private static final List<String> COMPOSITE_TYPES = List.of("minecraft:alternatives", "minecraft:sequence", "minecraft:group");

    private LootTableJsonFilter() {
    }

    public static JsonElement filter(JsonElement value) {
        if (!(value instanceof JsonObject table)) {
            return value;
        }
        if (!BlockedItems.stripLootTables()) {
            return value;
        }
        JsonArray pools = array(table, "pools");
        if (pools == null) {
            return value;
        }
        int removed = 0;
        for (int i = pools.size() - 1; i >= 0; i--) {
            JsonElement element = pools.get(i);
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject pool = element.getAsJsonObject();
            removed += stripEntries(array(pool, "entries"));
            removed += stripFunctions(pool);
            JsonArray entries = array(pool, "entries");
            if (entries != null && entries.isEmpty()) {
                pools.remove(i);
            }
        }
        removed += stripFunctions(table);
        if (removed > 0) {
            ItemDeleteMod.LOGGER.info("Removed {} blocked entr(ies) from a loot table", removed);
        }
        return table;
    }

    private static int stripEntries(JsonArray entries) {
        int removed = 0;
        if (entries == null) {
            return 0;
        }
        for (int i = entries.size() - 1; i >= 0; i--) {
            JsonElement element = entries.get(i);
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject entry = element.getAsJsonObject();
            String type = string(entry, "type");
            if (ITEM_TYPE.equals(type) || (type == null && entry.has("name"))) {
                if (BlockedItems.isBlocked(string(entry, "name"))) {
                    entries.remove(i);
                    removed++;
                }
                continue;
            }
            if (!COMPOSITE_TYPES.contains(type)) {
                continue;
            }
            JsonArray children = array(entry, "children");
            if (children == null) {
                continue;
            }
            removed += stripEntries(children);
            if (children.isEmpty()) {
                entries.remove(i);
                removed++;
            }
        }
        return removed;
    }

    private static int stripFunctions(JsonObject holder) {
        JsonArray functions = array(holder, "functions");
        if (functions == null) {
            return 0;
        }
        int removed = 0;
        for (int i = functions.size() - 1; i >= 0; i--) {
            JsonElement element = functions.get(i);
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject function = element.getAsJsonObject();
            if (!SET_ITEM_FUNCTION.equals(string(function, "function"))) {
                continue;
            }
            if (BlockedItems.isBlocked(string(function, "item"))) {
                functions.remove(i);
                removed++;
            }
        }
        return removed;
    }

    private static JsonArray array(JsonObject object, String name) {
        JsonElement element = object.get(name);
        return element != null && element.isJsonArray() ? element.getAsJsonArray() : null;
    }

    private static String string(JsonObject object, String name) {
        JsonElement element = object.get(name);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
    }
}
