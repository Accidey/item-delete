package com.xulai.itemdelete.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xulai.itemdelete.BlockedItems;
import com.xulai.itemdelete.ItemDeleteMod;

import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.LootContext;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin {
    @Inject(
            method = "addOffersFromItemListings(Lnet/minecraft/world/level/storage/loot/LootContext;Lnet/minecraft/world/item/trading/MerchantOffers;Lnet/minecraft/core/HolderSet;I)V",
            at = @At("RETURN")
    )
    private static void itemdelete$stripBlockedTrades(LootContext lootContext, MerchantOffers offers, HolderSet<VillagerTrade> potentialOffers, int numberOfOffers, CallbackInfo callback) {
        itemdelete$strip(offers);
    }

    @Inject(
            method = "addOffersFromItemListingsWithoutDuplicates(Lnet/minecraft/world/level/storage/loot/LootContext;Lnet/minecraft/world/item/trading/MerchantOffers;Lnet/minecraft/core/HolderSet;I)V",
            at = @At("RETURN")
    )
    private static void itemdelete$stripBlockedTradesWithoutDuplicates(LootContext lootContext, MerchantOffers offers, HolderSet<VillagerTrade> potentialOffers, int numberOfOffers, CallbackInfo callback) {
        itemdelete$strip(offers);
    }

    private static void itemdelete$strip(MerchantOffers offers) {
        if (!BlockedItems.stripTrades()) {
            return;
        }
        int before = offers.size();
        offers.removeIf(offer -> itemdelete$isBlocked(offer));
        int removed = before - offers.size();
        if (removed > 0) {
            ItemDeleteMod.LOGGER.info("Removed {} trade(s) containing blocked items", removed);
        }
    }

    private static boolean itemdelete$isBlocked(MerchantOffer offer) {
        return BlockedItems.isBlocked(offer.getResult())
                || BlockedItems.isBlocked(offer.getCostA())
                || BlockedItems.isBlocked(offer.getCostB());
    }
}
