package com.xulai.itemdelete.logic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.xulai.itemdelete.BlockedItems;
import com.xulai.itemdelete.ItemDeleteMod;

public final class RecipeJsonFilter {
    private static final String RESULT = "result";
    private static final String ID = "id";
    private static final String PLACEHOLDER = "minecraft:barrier";

    private RecipeJsonFilter() {
    }

    public static JsonElement filter(JsonElement value) {
        if (!(value instanceof JsonObject recipe)) {
            return value;
        }
        if (!BlockedItems.stripRecipes()) {
            return value;
        }
        if (!BlockedItems.isBlocked(resultId(recipe))) {
            return value;
        }
        neutralizeResult(recipe);
        ItemDeleteMod.LOGGER.info("Neutralized a recipe producing a blocked item");
        return recipe;
    }

    private static String resultId(JsonObject recipe) {
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

    private static void neutralizeResult(JsonObject recipe) {
        JsonElement result = recipe.get(RESULT);
        if (result != null && result.isJsonObject()) {
            result.getAsJsonObject().add(ID, new JsonPrimitive(PLACEHOLDER));
        } else {
            JsonObject emptyResult = new JsonObject();
            emptyResult.add(ID, new JsonPrimitive(PLACEHOLDER));
            recipe.add(RESULT, emptyResult);
        }
    }
}
