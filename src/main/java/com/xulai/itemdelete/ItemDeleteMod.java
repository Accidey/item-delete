package com.xulai.itemdelete;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.xulai.itemdelete.command.ItemDeleteCommand;
import com.xulai.itemdelete.logic.RecipeJsonFilter;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ItemDeleteMod.MODID)
public class ItemDeleteMod {
    public static final String MODID = "itemdelete";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ItemDeleteMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.addListener(ItemDeleteCommand::register);
        NeoForge.EVENT_BUS.addListener(RecipeJsonFilter::onModifyRecipeJsons);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(BlockedItems::logConfigured);
    }
}
