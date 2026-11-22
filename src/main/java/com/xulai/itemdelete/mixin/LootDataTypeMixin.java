package com.xulai.itemdelete.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.xulai.itemdelete.logic.LootTableJsonFilter;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataType;

@Mixin(LootDataType.class)
public abstract class LootDataTypeMixin {
    @Shadow
    @Final
    private ResourceKey<? extends Registry<?>> registryKey;

    @Redirect(
            method = "deserialize",
            at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;")
    )
    @SuppressWarnings({"unchecked", "rawtypes"})
    private DataResult<?> itemdelete$stripBlockedLoot(Codec codec, DynamicOps ops, Object value, ResourceLocation resourceLocation) {
        return codec.parse(ops, LootTableJsonFilter.filter(this.registryKey, resourceLocation, value));
    }
}
