package com.xulai.itemdelete.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xulai.itemdelete.BlockedItems;
import com.xulai.itemdelete.ItemDeleteMod;

import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.event.ModifyRecipeJsonsEvent;

public final class RecipeJsonFilter {
    private static final String RESULT = "result";
    private static final String ID = "id";

    private RecipeJsonFilter() {
    }

    public static void onModifyRecipeJsons(ModifyRecipeJsonsEvent event) {
        if (!BlockedItems.stripRecipes()) {
            return;
        }
        Map<Identifier, JsonElement> recipes = event.getRecipeJsons();
        List<Identifier> blocked = new ArrayList<>();
        for (Map.Entry<Identifier, JsonElement> entry : recipes.entrySet()) {
            if (BlockedItems.isBlocked(resultId(entry.getValue()))) {
                blocked.add(entry.getKey());
            }
        }
        for (Identifier id : blocked) {
            recipes.remove(id);
        }
        if (!blocked.isEmpty()) {
            ItemDeleteMod.LOGGER.info("Removed {} recipe(s) producing blocked items", blocked.size());
        }
    }

    private static String resultId(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }
        JsonObject recipe = element.getAsJsonObject();
        JsonElement result = recipe.get(RESULT);
        if (result == null) {
            return null;
        }
        if (result.isJsonPrimitive()) {
            return result.getAsString();
        }
        if (!result.isJsonObject()) {
            return null;
        }
        JsonElement id = result.getAsJsonObject().get(ID);
        return id != null && id.isJsonPrimitive() ? id.getAsString() : null;
    }
}
