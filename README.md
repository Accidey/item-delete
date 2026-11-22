# 删除物品 / Item Delete

把指定物品从**获取渠道**里清掉，但保留注册表条目，避免连锁崩溃。

## 处理范围

| 位置 | 处理方式 | 生效时机 |
|---|---|---|
| 创造模式物品栏 / 搜索页 | 不显示 | 重新打开物品栏 |
| 配方产出 | 以该物品为产出的配方整体移除 | `/reload` |
| 战利品表 | 解析前摘掉指向该物品的条目与 `set_item` 函数，剩余权重自动归一 | `/reload` |
| 村民 / 流浪商人交易 | 结果或价格含该物品的交易不生成 | 新生成的村民 |

物品本身仍然注册，所以 `/give`、已有存档里的物品、以及模组代码里直接 `new ItemStack(...)` 产生的掉落都不受影响。

## 配置

`config/itemdelete-common.toml`

```toml
blockedItems = ["somemod:junk_item", "othermod:*"]
hideFromCreativeTabs = true
stripRecipes = true
stripLootTables = true
stripTrades = true
```

- `blockedItems`：`modid:item_id` 精确匹配，`modid:*` 匹配整个模组。
- 其余四项为各渠道的独立开关，关掉即可让该渠道恢复原样。

## 命令

需要权限等级 2。

```
/itemdelete add      把手持物品写进 blockedItems 并保存配置文件
/itemdelete remove   把手持物品从 blockedItems 移除
/itemdelete list     列出当前所有条目
```

命令只负责改配置。配方与战利品表要在 `/reload` 之后才会重新清理；如果手持物品被 `modid:*` 通配覆盖，`remove` 会提示先删掉那条通配。

## 已知限制

- 战利品表里的 `minecraft:tag` 条目不做过滤，标签内的被禁物品仍可能掉落。
- 模组用 Java 代码直接发放的掉落、结构模板里写死的物品、已有存档中的箱子内容不会追溯清理。
- 已经存在的村民不会重新生成交易列表。

## 与开发规范的偏离

`开发规范.md` 0.4 要求所有配置支持热重载。`blockedItems` 的匹配缓存会在 `ModConfigEvent.Reloading` 时清空，但配方、战利品表、村民交易属于加载期数据，改完必须 `/reload` 或重启才生效，这一点无法绕过。
