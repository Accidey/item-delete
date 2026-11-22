package com.xulai.itemdelete.mixin;

import java.io.Reader;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.xulai.itemdelete.BlockedItems;
import com.xulai.itemdelete.logic.LootTableJsonFilter;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;

@Mixin(SimpleJsonResourceReloadListener.class)
public abstract class LootTableJsonMixin {
    private static final String LOOT_TABLE_PREFIX = "loot_table";

    @Redirect(
            method = "scanDirectory(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/resources/FileToIdConverter;Lcom/mojang/serialization/DynamicOps;Lcom/mojang/serialization/Codec;Ljava/util/Map;)V",
            at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonParser;parseReader(Ljava/io/Reader;)Lcom/google/gson/JsonElement;")
    )
    private static JsonElement itemdelete$filterLootJson(Reader reader, ResourceManager manager, FileToIdConverter lister, DynamicOps<JsonElement> ops, Codec<?> codec, Map<Identifier, ?> result) {
        JsonElement parsed = JsonParser.parseReader(reader);
        if (!BlockedItems.stripLootTables() || !LOOT_TABLE_PREFIX.equals(lister.prefix())) {
            return parsed;
        }
        return LootTableJsonFilter.filter(parsed);
    }
}
