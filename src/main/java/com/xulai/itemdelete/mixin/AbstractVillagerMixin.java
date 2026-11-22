package com.xulai.itemdelete.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xulai.itemdelete.BlockedItems;
import com.xulai.itemdelete.ItemDeleteMod;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffers;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin {
    @Inject(method = "addOffersFromItemListings", at = @At("RETURN"))
    private void itemdelete$stripBlockedTrades(MerchantOffers offers, VillagerTrades.ItemListing[] listings, int maxNumbers, CallbackInfo callback) {
        if (!BlockedItems.stripTrades()) {
            return;
        }
        int before = offers.size();
        offers.removeIf(offer -> BlockedItems.isBlocked(offer.getResult())
                || BlockedItems.isBlocked(offer.getCostA())
                || BlockedItems.isBlocked(offer.getCostB()));
        int removed = before - offers.size();
        if (removed > 0) {
            ItemDeleteMod.LOGGER.info("Removed {} trade(s) containing blocked items", removed);
        }
    }
}
