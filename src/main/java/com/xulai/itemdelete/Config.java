package com.xulai.itemdelete;

import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BLOCKED_ITEMS = BUILDER
                .comment(
                        "禁用物品列表",
                        "Blocked item list",
                        "写法：modid:item_id 精确匹配，modid:* 匹配整个模组。",
                        "Format: modid:item_id for an exact match, modid:* for every item of a mod.",
                        "被禁物品仍会注册，但会从创造模式物品栏、配方产出、战利品表和村民交易中移除。",
                        "Blocked items are still registered, but they are removed from creative tabs, recipe outputs, loot tables and villager trades.",
                        "/give 与已有存档中的物品不受影响。",
                        "/give and items already present in a world are not affected.",
                        "Default: [] / 默认：[]"
                )
                .defineListAllowEmpty("blockedItems", List.of(), () -> "", obj -> obj instanceof String);

        BUILDER.comment(" ");

        HIDE_FROM_CREATIVE_TABS = BUILDER
                .comment(
                        "从创造模式物品栏隐藏",
                        "Hide from creative tabs",
                        "开启后被禁物品不会出现在任何创造模式标签页与搜索页中。",
                        "When enabled, blocked items never show up in creative tabs or the creative search tab.",
                        "修改后新打开的物品栏立即生效。",
                        "Takes effect the next time the creative inventory is built.",
                        "Default: true / 默认：true"
                )
                .define("hideFromCreativeTabs", true);

        BUILDER.comment(" ");

        STRIP_RECIPES = BUILDER
                .comment(
                        "清除产出被禁物品的配方",
                        "Strip recipes producing blocked items",
                        "开启后所有以被禁物品为产出的配方都会被移除，无法再合成。",
                        "When enabled, every recipe whose output is a blocked item is removed and can no longer be crafted.",
                        "配方中把被禁物品当作材料的配方不做处理。",
                        "Recipes that merely use a blocked item as an ingredient are left untouched.",
                        "修改后需要执行 /reload 才会生效。",
                        "Takes effect after /reload.",
                        "Default: true / 默认：true"
                )
                .define("stripRecipes", true);

        BUILDER.comment(" ");

        STRIP_LOOT_TABLES = BUILDER
                .comment(
                        "清除战利品表中的被禁物品",
                        "Strip blocked items from loot tables",
                        "开启后战利品表里指向被禁物品的条目会在解析前被摘掉，剩余条目权重自动归一。",
                        "When enabled, loot table entries pointing at blocked items are removed before parsing, and the remaining weights are renormalised.",
                        "整张表不会失效，箱子照常生成其他奖励。",
                        "The table itself stays valid, so chests still generate their other loot.",
                        "修改后需要执行 /reload 才会生效。",
                        "Takes effect after /reload.",
                        "Default: true / 默认：true"
                )
                .define("stripLootTables", true);

        BUILDER.comment(" ");

        STRIP_TRADES = BUILDER
                .comment(
                        "清除村民交易中的被禁物品",
                        "Strip blocked items from villager trades",
                        "开启后结果或价格里含有被禁物品的交易不会被添加到村民与流浪商人身上。",
                        "When enabled, trades whose result or price contains a blocked item are not added to villagers or wandering traders.",
                        "该过滤在交易生成时执行，已经存在的村民不受影响。",
                        "Filtering happens when trades are generated, so villagers that already exist are not affected.",
                        "Default: true / 默认：true"
                )
                .define("stripTrades", true);
    }

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLOCKED_ITEMS;
    public static final ModConfigSpec.BooleanValue HIDE_FROM_CREATIVE_TABS;
    public static final ModConfigSpec.BooleanValue STRIP_RECIPES;
    public static final ModConfigSpec.BooleanValue STRIP_LOOT_TABLES;
    public static final ModConfigSpec.BooleanValue STRIP_TRADES;

    public static final ModConfigSpec SPEC = BUILDER.build();
}
