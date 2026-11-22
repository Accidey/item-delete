package com.xulai.itemdelete.mixin;

import java.io.Reader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.gson.JsonElement;
import com.mojang.serialization.Decoder;
import com.xulai.itemdelete.logic.LootTableJsonFilter;
import com.xulai.itemdelete.logic.RecipeJsonFilter;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.StrictJsonParser;

@Mixin(targets = "net.minecraft.resources.RegistryLoadTask$PendingRegistration")
public abstract class RegistryDataLoadMixin {
    @Redirect(
            method = "loadFromResource(Lcom/mojang/serialization/Decoder;Lnet/minecraft/resources/RegistryOps;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/server/packs/resources/Resource;)Lcom/mojang/datafixers/util/Either;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/StrictJsonParser;parse(Ljava/io/Reader;)Lcom/google/gson/JsonElement;")
    )
    private static JsonElement itemdelete$filterRegistryJson(Reader reader, Decoder<?> elementDecoder, RegistryOps<JsonElement> ops, ResourceKey<?> elementKey, Resource thunk) {
        JsonElement parsed = StrictJsonParser.parse(reader);
        if (elementKey.isFor(Registries.LOOT_TABLE)) {
            return LootTableJsonFilter.filter(parsed);
        }
        if (elementKey.isFor(Registries.RECIPE)) {
            return RecipeJsonFilter.filter(parsed);
        }
        return parsed;
    }
}
