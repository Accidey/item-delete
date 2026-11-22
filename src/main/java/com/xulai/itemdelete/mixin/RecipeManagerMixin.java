package com.xulai.itemdelete.mixin;

import java.util.LinkedHashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.xulai.itemdelete.BlockedItems;
import com.xulai.itemdelete.ItemDeleteMod;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Shadow
    @Final
    private HolderLookup.Provider registries;

    @Shadow
    private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

    @Shadow
    private Map<ResourceLocation, RecipeHolder<?>> byName;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("RETURN"))
    private void itemdelete$filterLoadedRecipes(Map<ResourceLocation, JsonElement> recipes, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo callback) {
        itemdelete$filterCurrentRecipes();
    }

    @Inject(method = "replaceRecipes(Ljava/lang/Iterable;)V", at = @At("RETURN"))
    private void itemdelete$filterSyncedRecipes(Iterable<RecipeHolder<?>> recipes, CallbackInfo callback) {
        itemdelete$filterCurrentRecipes();
    }

    private void itemdelete$filterCurrentRecipes() {
        if (!BlockedItems.stripRecipes()) {
            return;
        }
        Map<ResourceLocation, RecipeHolder<?>> filtered = new LinkedHashMap<>();
        for (Map.Entry<ResourceLocation, RecipeHolder<?>> entry : this.byName.entrySet()) {
            ItemStack result;
            try {
                result = entry.getValue().value().getResultItem(this.registries);
            } catch (RuntimeException exception) {
                filtered.put(entry.getKey(), entry.getValue());
                continue;
            }
            if (!BlockedItems.isBlocked(result)) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        int removed = this.byName.size() - filtered.size();
        if (removed <= 0) {
            return;
        }
        ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> builder = ImmutableMultimap.builder();
        for (RecipeHolder<?> holder : filtered.values()) {
            builder.put(holder.value().getType(), holder);
        }
        this.byType = builder.build();
        this.byName = filtered;
        ItemDeleteMod.LOGGER.info("Removed {} recipe(s) producing blocked items", removed);
    }
}
