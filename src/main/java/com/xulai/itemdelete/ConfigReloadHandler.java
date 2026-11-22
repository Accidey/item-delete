package com.xulai.itemdelete;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = ItemDeleteMod.MODID)
public final class ConfigReloadHandler {
    private ConfigReloadHandler() {
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        BlockedItems.resetCache();
    }
}
