package com.xulai.itemdelete.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xulai.itemdelete.BlockedItems;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

@Mixin(targets = "net.minecraft.world.item.CreativeModeTab$ItemDisplayBuilder")
public class CreativeModeTabOutputMixin {
    @Inject(
            method = "accept(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/CreativeModeTab$TabVisibility;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void itemdelete$hideBlockedItems(ItemStack stack, CreativeModeTab.TabVisibility visibility, CallbackInfo callback) {
        if (BlockedItems.hideFromCreativeTabs() && BlockedItems.isBlocked(stack)) {
            callback.cancel();
        }
    }
}
